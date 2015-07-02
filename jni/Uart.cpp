#include "Uart.h"


#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <termios.h>

static int fd = -1;

bool UartOpen(const char* dev)
{
	if(dev==NULL)
		return false;

	fd = open(dev, O_RDWR | O_NOCTTY | O_NDELAY);
	if(fd < 0)
	{
		fprintf(stderr, "open %s failed, syserr=%d\n", dev, errno);
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
		fprintf(stderr, "read failed, syserr=%d\n", errno);
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
	ret = select(fd+1,&fdset,NULL,NULL,tout);
	if(ret < 0)
		return -1;
	if(ret == 0)
		return 0;

tryRead:
	ret = read(fd, buf, len);
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
			fprintf(stderr, "read failed, syserr=%d\n", errno);
			return -1;
			break;
		}
	}

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
				fprintf(stderr, "write failed, syserr=%d\n", errno);
				return -1;
			}
		}

		snt += ret;
	}

	return snt;
}


