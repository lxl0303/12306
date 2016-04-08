package mobilePhone;

import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;
import java.net.Socket;
import javax.swing.JFrame;

public class receiveMessage extends JFrame implements Runnable{
			
	private static final long serialVersionUID = 1L;
	private Label num;
	private Label message;
	private TextField mes;
	private Socket s;
	
	public receiveMessage(Socket s){
			this.s =s;
	}
	
	public void run() {
		
		num =new Label("手机号：                           ");
		message =new Label("短信");
		mes =new TextField(10);
		
		this.add(num);
		this.add(message);
		this.add(mes);
		
		this.setTitle("模拟手机");
		this.setSize(170,300);
		this.setLocation(200, 20);
		this.setLayout(new FlowLayout());
		this.setVisible(true);

		try {
			//读
			String[] data = tool.read(s).split(":");
			num.setText("手机号:"+data[0]);
			mes.setText(data[1]);
			//写
			tool.write(s,new String("手机端已接收到验证码:"+data[1]));
		} catch (Exception e1) {

		}	
	}
}
