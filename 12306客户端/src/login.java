import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import mobilePhone.receiveMessage;
import mobilePhone.tool;

public class login extends Frame implements Runnable{
	
		private static final long serialVersionUID = 1L;
		
		private Label phone;
		private TextField num ;
		private Button send ;
		private Label securityCode;
		private TextField key;
		private Button register;
		private Button login;
		
		private Socket s;
		
		public login(){
			
		}
		
		public login(Socket s){
			this.s = s;
		}
		
		public void run() {
			
			phone =new Label("手机号码");
			num =new TextField(20);
			send =new Button("发送验证码");
			
			securityCode =new Label("验证码");
			key =new TextField(20);
			
			login =new Button("登录");
			register = new Button("尚未注册？？");
			
			this.add(phone);
			this.add(num);
			this.add(send);
			this.add(securityCode);
			this.add(key);
			this.add(login);
			this.add(register);

			this.setLayout(new FlowLayout());
			this.setTitle("12306登录界面");
			this.setVisible(true);
			this.setLocation(380, 20);
			this.setSize(350,300);

			this.addWindowListener(new WindowAdapter(){
				public void windowClosing(WindowEvent e) {
					super.windowClosing(e);
					try{
						s.close();
					}catch(IOException e1){
					}
					System.exit(0);
				}
			});	
			
			send.addActionListener(new ActionListener(){
				
				public void actionPerformed(ActionEvent e) {
					String number = num.getText();
					if(isPhoneNumberValid(number)){
						//写
						tool.write(s, number);
						System.out.println("发送的数据为"+number);
						//读
						if(tool.read(s).equals("r")){
							tool.myAlert2(new login(), "验证码发送成功", 380, 50);
							//创建线程
							new Thread(new receiveMessage(s)).start();
						}else{
							tool.myAlert2(new login(), "账号未注册", 380, 50);
						}
						//写
						tool.write(s, ".....");
						
					}
				}
			});
			
			login.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					//读
					if (key.getText().equals(tool.read(s))){
						new Thread(new buyTicket(s)).start();
						System.out.println("登录成功");
					}
					//写
					tool.write(s,"登录界面已获取到验证码");	
				}
			});
			
			register.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {					
					register r = new register(s);
					new Thread(r).start();
					//添加监听，弹出注册窗口时，主界面不能操作
					send.setEnabled(false);
					login.setEnabled(false);
					register.setEnabled(false);
					r.addWindowListener(new WindowAdapter(){
						public void windowClosing(WindowEvent e) {
							super.windowClosing(e);
							send.setEnabled(true);
							login.setEnabled(true);
							register.setEnabled(true);
						}
					});
				}
			});
		}
		
		private static boolean isPhoneNumberValid(String phoneNumber) {
			
			String expression = "((^(13|15|18)[0-9]{9}$)|(^0[1,2]{1}\\d{1}-?\\d{8}$)|(^0[3-9] {1}\\d{2}-?\\d{7,8}$)|(^0[1,2]{1}\\d{1}-?\\d{8}-(\\d{1,4})$)|(^0[3-9]{1}\\d{2}-? \\d{7,8}-(\\d{1,4})$))";
			CharSequence inputStr = phoneNumber;

			Pattern pattern = Pattern.compile(expression);
			Matcher matcher = pattern.matcher(inputStr);
			if (matcher.matches()) {
				return  true;
			}
			return false;
		}
}
