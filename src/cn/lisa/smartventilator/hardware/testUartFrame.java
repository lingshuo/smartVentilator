package cn.lisa.smartventilator.hardware;

import java.util.Arrays;
import java.util.Random;

public class testUartFrame {

	public static void main(String[] args) {
		
		String tty = args[0];
		
		UartFrame frame = new UartFrame();
		
		boolean ok = frame.init(tty, 115200, 8, 1, (byte)'N');
		if(!ok) {
			System.out.printf("open: %s failed\n", tty);
			return;
		}
		System.out.printf("open: %s ok\n", tty);
		
		
		Random r = new Random();
		//int times = 1000000;
		int cnt = 0;
		//while(cnt<times) {
		while(true) {
			/* rand test */

			int lenBuf = r.nextInt();
			if(lenBuf<0)
				lenBuf = lenBuf * (-1);
			lenBuf = lenBuf % 256;
			if(lenBuf==0)
				lenBuf = 1;
			
			/* rand data */
			byte[] sendBuf = new byte[lenBuf];
			r.nextBytes(sendBuf);
			//byte[] sendBuf = new byte[]{0x0A,0x0B,0x0C,0x0D,0x21};
			System.out.printf("test[%d]", cnt);
			
			int bytes = frame.send(sendBuf, sendBuf.length);
			if(bytes<0) {
				System.out.printf("send: %s failed\n", tty);
				return;
			}
			System.out.printf("sent[%d]:", bytes);
			
			byte[] buf = new byte[1024];
			bytes = frame.recv(buf, 1024);
			if(bytes<0) {
				System.out.printf("recv: %s failed\n", tty);
				return;
			} else if(bytes==0) {
				System.out.printf("recv: %s timeout\n", tty);
				return;
			}
			System.out.printf("recv[%d]:", bytes);
			
			byte[] recvBuf = Arrays.copyOfRange(buf, 0, bytes);
			if( Arrays.equals(sendBuf, recvBuf) ) {
				System.out.printf("SAME", bytes);
			}else {
				System.out.printf("test[%d],DIFF", cnt, bytes);
			}
			System.out.printf("\n");
			
			cnt = (cnt+1) % 1000000;
		}
		//System.out.printf("test %d times, all PASS\n", cnt);

	}

}
