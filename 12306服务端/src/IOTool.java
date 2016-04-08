import java.io.IOException;
import java.net.Socket;


public class IOTool {
	public static void write(Socket s,String str) {
		try {
			s.getOutputStream().write(str.getBytes());
		} catch (IOException e) {
		}
	}
	
	public static String read(Socket s){
		byte[] b =new byte[1024];
		int len;
		try {
			len = s.getInputStream().read(b);
			return new String(b,0,len);
		} catch (IOException e) {
		}
		return null;
		
	}
}
