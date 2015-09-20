#include "UartFrame.h"

//#include "Uart.h"
#include "Debug.h"

#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <termios.h>

#define	BUF_SIZE	64

#define	LEN_HEAD	2
#define	LEN_SUM		1
#define	LEN_SIZE	1
#define	CHAR_HEAD	'!'
#define CHAR_ENCODE	'@'

#define	LEN_FRAME_MIN	(LEN_HEAD+LEN_SUM+LEN_SIZE)

static char sumOf(const char* msg, int len);
static int decode(char* dst, char* src, int len);
static int encode(char* dst, char* src, int len);

bool UartOpen(const char* dev);
void UartClose(void);
bool isOpen(void);
bool UartConfig(int baudrate, int databits, int stopbits, char parity, int interval);
int UartRead(char *buf, int len);
int UartRead(char *buf, int len, int timeout);
int UartWrite(const char* buf, int len);

//private
static int fd = -1;
static int recvBytes = 0;
static char recvBuf[BUF_SIZE];

/*
 * Class:     Java_cn_lisa_smartventilator_utility_hardware_UartFrame
 * Method:    init
 * Signature: (Ljava/lang/String;IIII)Z
 */
JNIEXPORT jboolean JNICALL Java_cn_lisa_smartventilator_utility_hardware_UartFrame_init
  (JNIEnv *env, jobject, jstring device, jint baudrate, jint databits, jint stopbits, jbyte parity)
{
	bool ok = false;
	const char *dev = env->GetStringUTFChars(device, NULL);

	ok = UartOpen(dev);
	if(!ok)
		return false;

	ok = UartConfig(baudrate, databits, stopbits, parity, 500);
	if(!ok)
		return false;

	recvBytes = 0;
	memset( recvBuf, 0x00, sizeof(recvBuf) );
	return true;
}

/*
 * Class:     Java_cn_lisa_smartventilator_utility_hardware_UartFrame
 * Method:    destroy
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_cn_lisa_smartventilator_utility_hardware_UartFrame_destroy
  (JNIEnv *, jobject)
{
	UartClose();
}

/*
 * Class:     Java_cn_lisa_smartventilator_utility_hardware_UartFrame
 * Method:    recv
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_cn_lisa_smartventilator_utility_hardware_UartFrame_recv
  (JNIEnv *env, jobject, jbyteArray buf, jint len)
{
	if(len<0)
		return -1;

fetchFrame:
	while( recvBytes > LEN_FRAME_MIN )
	{

		if(recvBuf[0]!=CHAR_HEAD)
		{
			recvBytes = recvBytes-1;
			memcpy(recvBuf, &recvBuf[1], recvBytes);
			continue;
		}

		if(recvBuf[1]!=CHAR_HEAD)
		{
			recvBytes = recvBytes-2;
			memcpy(recvBuf, &recvBuf[2], recvBytes);
			continue;
		}
		/* frame head matched */

		char *decodeSRC = &recvBuf[LEN_HEAD];
		char decodeDST[BUF_SIZE];
		int lenBeforeDecode= recvBytes - LEN_HEAD;
		int cntDecode = decode(decodeDST, decodeSRC, lenBeforeDecode);
		int lenAfterDecode = lenBeforeDecode - cntDecode;

		int lenDataWish = (unsigned int)(decodeDST[LEN_SUM] & 0xFF);
		if(lenAfterDecode<lenDataWish+LEN_SUM+LEN_SIZE)	/* incomplete frame */
		{
			sDEBUG("Uart:frm incompleted\n");
			break;
		}

		char sumWish = decodeDST[0];
		char sumReal = sumOf(&decodeDST[LEN_SUM], LEN_SIZE+lenDataWish);
		if(sumWish!=sumReal)	/*invalid frame */
		{
			sDEBUG("Uart:frm invalid\n");
			recvBytes = recvBytes - 1;
			memcpy(recvBuf, &recvBuf[1], recvBytes);
			continue;
		}

		/* copy data to user */
		sDEBUG("Uart:frm FIND!!\n");
		//memcpy(buf, &decodeDST[LEN_SUM+LEN_SIZE], lenDataWish);
		env->SetByteArrayRegion(buf, 0, lenDataWish, (const jbyte *)(&decodeDST[LEN_SUM+LEN_SIZE]));

		char tmp[BUF_SIZE];
		int lenBeforeEncode = LEN_SUM+LEN_SIZE+lenDataWish;
		int cntEncode = encode(tmp, decodeDST, lenBeforeEncode);
		int lenAfterEncode = lenBeforeEncode + cntEncode;

		int needClean = lenAfterEncode + LEN_HEAD;
		if(needClean>recvBytes)
		{	/* when last data is CHAR_HEAD,
			 * and the CHAR_ENCODE for the CHAR_HEAD is not recved
			 * it will appear:lenAfterEncode > lenBeforeDecode
			 * Then needClean will BIGGER than recvBytes */
			needClean = recvBytes;
		}
		recvBytes = recvBytes - needClean;
		memcpy(recvBuf, &recvBuf[needClean], recvBytes);

		return lenDataWish;	/* return the length of the msg */
	}

	if( sizeof(recvBuf)-recvBytes<=0 )	/* all datas are rubish */
	{
		sDEBUG("Uart:all are rubish\n");
		recvBytes = 0;
	}

	//int ret = UartRead(&recvBuf[recvBytes], sizeof(recvBuf)-recvBytes, 5);	/* five seconds timeout */
	//int ret = UartRead(&recvBuf[recvBytes], sizeof(recvBuf)-recvBytes);	/* read untill data back */
	sDEBUG("Uart:try reading data\n");
	int ret = UartRead(&recvBuf[recvBytes], sizeof(recvBuf)-recvBytes, 0);	/* five seconds timeout */
	if(ret>0)	/* datas come */
	{
		recvBytes += ret;
		goto fetchFrame;
	}
	else if(ret<0)	/* error come */
	{
		return -1;
	}

	return 0;	/* timeout, OR (if it is block mode, it will never come here)    */
}

/*
 * Class:     Java_cn_lisa_smartventilator_utility_hardware_UartFrame
 * Method:    send
 * Signature: ([BI)I
 */
JNIEXPORT jint JNICALL Java_cn_lisa_smartventilator_utility_hardware_UartFrame_send
  (JNIEnv *env, jobject, jbyteArray buf, jint len)
{
	if(len<0)
		return -1;

	if(len==0)
		return 0;

	if( (len*2)>(BUF_SIZE-2) )
		return -1;

	char encodeSRC[BUF_SIZE];
	if(len>BUF_SIZE)
		len = BUF_SIZE;

	env->GetByteArrayRegion(buf, 0, len, (jbyte *)(&encodeSRC[LEN_SUM+LEN_SIZE]) );
	encodeSRC[LEN_SUM]	= len;
	char sum = sumOf( (const char *)(&encodeSRC[LEN_SUM]), LEN_SIZE+len );
	encodeSRC[0]		= sum;
	

	char frame[BUF_SIZE];
	int cntEncode = encode( &frame[LEN_HEAD], encodeSRC, LEN_SUM+LEN_SIZE+len);
	frame[0]	= CHAR_HEAD;
	frame[1]	= CHAR_HEAD;

	int lenFrame = LEN_HEAD + LEN_SUM+LEN_SIZE+len+cntEncode;
	int ret = UartWrite(frame, lenFrame);
	if(ret<0)
		return -1;

	return len;
}

/* -------------------static method------------------------*/
static char sumOf(const char* msg, int len)
{
	int i =0;
	char sum = 0;

	for(i=0;i<len;i++)
	{
		sum	= sum + msg[i];
	}

	return sum;
}


static int encode(char* dst, char* src, int len)
{
	int idxDst = 0;
	int idxSrc = 0;

	int cntHEAD = 0;

	for(idxSrc=0;idxSrc<len;idxSrc++)
	{
		if(src[idxSrc]!=CHAR_HEAD)
		{
			dst[idxDst++]	= src[idxSrc];
		}
		else
		{
			dst[idxDst++]	= CHAR_HEAD;
			dst[idxDst++]	= CHAR_ENCODE;
			cntHEAD++;
		}
	}

	return cntHEAD;
}

static int decode(char* dst, char* src, int len)
{
	int idxSrc=0;
	int idxDst=0;

	int cntHEAD=0;

	for(idxSrc=0;idxSrc<len;idxSrc++)
	{

		if(src[idxSrc]!=CHAR_HEAD)
		{
			dst[idxDst++] = src[idxSrc];
		}
		else
		{
			dst[idxDst++] = CHAR_HEAD;
			idxSrc++;
			cntHEAD++;
		}
	}

	return cntHEAD;
}

//UART:========================================
bool UartOpen(const char* dev)
{
	if(dev==NULL)
		return false;

	fd = open(dev, O_RDWR | O_NOCTTY | O_NDELAY);
	if(fd < 0)
	{
		printf("open %s failed, syserr=%d\n", dev, errno);
		return false;
	}

	return true;
}

void UartClose(void)
{
	if( fd>=0 )
	{
		close(fd);
		fd = -1;
	}
}

bool isOpen(void)
{
	if(fd>=0)
		return true;

	return false;
}

bool UartConfig(int baudrate, int databits, int stopbits, char parity, int interval)
{
	bool ret = false;
	struct termios options;

	if (tcgetattr(fd, &options) != 0)	//save old option
		goto EXIT;

	/* baudrate */
	switch( baudrate )
	{
	case 9600:
		cfsetispeed(&options, B9600);
		cfsetospeed(&options, B9600);
		break;
	case 38400:
			cfsetispeed(&options, B38400);
			cfsetospeed(&options, B38400);
			break;
	case 57600:
		cfsetispeed(&options, B57600);
		cfsetospeed(&options, B57600);
		break;
	case 115200:
		cfsetispeed(&options, B115200);
		cfsetospeed(&options, B115200);
		break;
	default:
		goto EXIT;
		break;
	}

	/* databits */
	options.c_cflag &= ~CSIZE;
	switch (databits)
	{
		case 7:
			options.c_cflag |= CS7;
			break;

		case 8:
			options.c_cflag |= CS8;
			break;

		default:
			goto EXIT;
			break;
	}

	/* stopbits */
	switch (stopbits)
	{
		case 1:
			options.c_cflag &= ~CSTOPB;
			break;

		case 2:
			options.c_cflag |= CSTOPB;
			break;

		default:
			goto EXIT;
			break;
	}

	/* partiy */
	switch (parity)
	{
		case 'n':
		case 'N':
			options.c_cflag &= ~PARENB;			// Clear parity enable
			options.c_iflag &= ~INPCK; 			// Enable parity checking
			break;

		case 'o':
		case 'O':
			options.c_cflag |= (PARODD | PARENB);
			options.c_iflag |= INPCK;          		// Disnable parity checking
			break;

		case 'e':
		case 'E':
			options.c_cflag |= PARENB;     			// Enable parity
			options.c_cflag &= ~PARODD;
			options.c_iflag |= INPCK;      			// Disnable parity checking
			break;

		default:
			goto EXIT;
			break;
	}

	tcflush(fd,TCIFLUSH);
	options.c_iflag	= 0;
	options.c_oflag	= 0;
	options.c_lflag	= 0;
	options.c_cc[VTIME]	= (int)(interval / 100);		//VTIME 的单位是100ms
	options.c_cc[VMIN]	= 0; 					// Update the options and do it NOW

	if (tcsetattr(fd,TCSANOW,&options) != 0)
		goto EXIT;

	ret = true;

EXIT:
	return ret;

}

int UartRead(char *buf, int len)
{
	int bytes = -1;

	if(len<0)
		return -1;

	bytes = read(fd, buf, len);
	if(bytes<0)
	{
		printf("read failed, syserr=%d\n", errno);
		return -1;
	}

	return bytes;
}

int UartRead(char *buf, int len, int timeout)
{
	int ret = 0;

	struct timeval tv;
	struct timeval* tout=NULL;
	fd_set fdset;

	if(len<0)
		return -1;

	if(timeout<0)
		timeout = 0;

	if( timeout == 0 )
		tout = NULL;
	else
	{
		tv.tv_sec	= timeout;	//sec
		tv.tv_usec	= 0;		//usec

		tout = &tv;
	}

	FD_ZERO(&fdset);
	FD_SET(fd,&fdset);
	sDEBUG("Uart:waiting data=====\n");
	ret = select(fd+1,&fdset,NULL,NULL,tout);
	sDEBUG("Uart:select ret=%d=====\n", ret);
	if(ret < 0)
		return -1;
	if(ret == 0)
		return 0;

tryRead:
	ret = read(fd, buf, len);
	sDEBUG("Uart:read ret=%d=====\n", ret);
	if( ret<0 )
	{
		switch(errno)
		{
		case EAGAIN:
			usleep(50000);
			goto tryRead;
			break;
		case EINTR:
			usleep(50000);
			goto tryRead;
			break;
		default:
			printf("read failed, syserr=%d\n", errno);
			return -1;
			break;
		}
	}

#if DEBUG_ON
#define	LINE_SIZE	14
		{
			sDEBUG("Uart:ret=%d=================\n", ret);

			int done = 0;
			while(done<ret)
			{
				int line=LINE_SIZE;
				if(ret-done<LINE_SIZE)
					line = ret -done;

				char frm[1024];
				int i=0;
				for(i=0;i<line;i++)
				{
					sprintf(&frm[i*3], "%02X|", (uint8_t)(buf[done+i]));
				}

				sDEBUG("%s\n",frm);

				done += line;
			}
		}
#endif

	return ret;
}

int UartWrite(const char* buf, int len)
{
	int snt=0;

	if(len<0)
		return -1;

tryWrite:
	while( snt<len )
	{
		int ret = write(fd, &buf[snt], len-snt);
		if( ret<0 )
		{
			switch(errno)
			{
			case EAGAIN:
				usleep(50000);
				goto tryWrite;
				break;
			case EINTR:
				usleep(50000);
				goto tryWrite;
				break;
			default:
				printf("write failed, syserr=%d\n", errno);
				return -1;
			}
		}

		snt += ret;
	}

	return snt;
}
