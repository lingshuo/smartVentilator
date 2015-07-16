#include "UartFrame.h"

#include "Uart.h"

#include <string.h>
#include <stdlib.h>

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

#define	LEN_HEAD	2
#define	LEN_SUM		1
#define	LEN_SIZE	1
#define	CHAR_HEAD	'!'
#define CHAR_ENCODE	'@'

#define	LEN_FRAME_MIN	(LEN_HEAD+LEN_SUM+LEN_SIZE)

static char sumOf(const char* msg, int len);
static int decode(char* dst, char* src, int len);
static int encode(char* dst, char* src, int len);

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
			break;

		char sumWish = decodeDST[0];
		char sumReal = sumOf(&decodeDST[LEN_SUM], LEN_SIZE+lenDataWish);
		if(sumWish!=sumReal)	/*invalid frame */
		{
			recvBytes = recvBytes - 1;
			memcpy(recvBuf, &recvBuf[1], recvBytes);
			continue;
		}

		/* copy data to user */
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
		recvBytes = 0;

	//int ret = UartRead(&recvBuf[recvBytes], sizeof(recvBuf)-recvBytes, 5);	/* five seconds timeout */
	//int ret = UartRead(&recvBuf[recvBytes], sizeof(recvBuf)-recvBytes);	/* read untill data back */
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
