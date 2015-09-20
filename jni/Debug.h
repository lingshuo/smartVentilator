#ifndef	__DEBUG__
#define	__DEBUG__

<<<<<<< HEAD
#define DEBUG_ON	0
=======
#define DEBUG_ON	1
>>>>>>> branch 'master' of https://github.com/lingshuo/smartVentilator.git

#if DEBUG_ON

#include <android/log.h>
#define TAG	"[UART]"
#define sDEBUG(...) __android_log_print(ANDROID_LOG_INFO,TAG ,__VA_ARGS__)

#else
#define sDEBUG(...)

#endif	//DEBUG_ON

#endif	//__DEBUG__
