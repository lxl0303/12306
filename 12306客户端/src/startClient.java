import java.net.Socket;

public class startClient {
	//当主线程下有子线程时，主线程执行结束才执行子线程，主线程比子线程优先级高
	public static void main(String[] args) throws Exception {
		
		Socket s =new Socket("localhost",20000);
		new Thread(new login(s)).start();
		
		
	}

}
