#ifndef __UARTAGENT_H__
#define __UARTAGENT_H__

#define	BUF_SIZE	1024

extern bool UartOpen(const char* dev);
extern void UartClose(void);
extern bool isOpen(void);
extern bool UartConfig(int baudrate, int databits, int stopbits, char parity, int interval);
extern int UartRead(char *buf, int len);
extern int UartRead(char *buf, int len, int timeout);
extern int UartWrite(const char* buf, int len);


#endif	//__UARTAGENT_H__
