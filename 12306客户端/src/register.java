import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;

import mobilePhone.tool;

public class register extends JFrame implements Runnable{

	private static final long serialVersionUID = 1L;
	
	private Label user;
	private Label id;
	private Label phone;
	private TextField tuser;
	private TextField tid;
	private TextField tphone;
	private Button reg;
	
	private Socket s;
	
	public register(){
		
	}
	
	public register(Socket s) {
		this.s =s;
	}

	public void run() {
		
		user = new Label("用户名:");
		id = new Label("身份证（18位有效）:");
		phone = new Label("手机号码:");
		tuser = new TextField(20);
		tid = new TextField(20);
		tphone = new TextField(20);
		reg =new Button("注册");
		
		this.add(user);
		this.add(tuser);
		this.add(id);
		this.add(tid);
		this.add(phone);
		this.add(tphone);
		this.add(reg);
		
		this.setVisible(true);
		this.setTitle("12306注册界面");
		this.setLayout(new FlowLayout());
		this.setLocation(10, 20);
		this.setSize(190,300);
		
		reg.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String user =tuser.getText();
				String id =tid.getText();
				String phone =tphone.getText();
				if(user!=null&&isIDValid(id)&&isPhoneNumberValid(phone)){
					System.out.println(phone+":"+user+":"+id);
					//写
					tool.write(s,new String(phone+":"+user+":"+id));
					//读
					String str =tool.read(s);
					if(str.equals("r")){
						//添加对话框
						try {
							tool.myAlert(new register(), "恭喜您注册成功", 10, 50);
						}catch (Exception e1) {
						}
					}else if(str.equals("ur")){
						try {
							tool.myAlert(new register(), "该账号已注册过", 10, 50);
						}catch (Exception e1) {
						}
					}	
				}
			}
		});	
	}
	
	private static boolean isIDValid(String id){
		char c;
		if(id.length()==18){
			for(int i=0;i<id.length();i++){
				c=id.charAt(i);
				if(c<'0'||c>'9')
					return false;
			}
			return true;	
		}
		return false;	
			
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
