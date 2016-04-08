import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class startServer {
	
	public static void main(String[] args) throws Exception {
		
		Lock l = new ReentrantLock();
		
		Socket s;
		ServerSocket ss = new ServerSocket(20000);	
		
		while(true){
			try {
				s=ss.accept();
				new Thread(new service(s,l)).start();
			} catch (Exception e) {

			}			
		}	
	}
}
