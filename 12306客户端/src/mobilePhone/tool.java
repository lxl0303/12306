package mobilePhone;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Label;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.Socket;

import javax.swing.JFrame;


public class tool {
	
	private static Dialog d;
	
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

	public static void myAlert(JFrame jf, String s, int x,int y) {
		d = new Dialog(jf,"提示框");
		setAlert(d, s, x, y);	
	}

	public static void myAlert2(Frame f, String s, int x,int y) {
		d = new Dialog(f,"提示框");
		setAlert(d, s, x, y);
	}
    
	private static void setAlert(final Dialog d,String s, int x, int y) {
		d.add(new Label(s,Label.CENTER));
		d.setVisible(true);
		d.setLocation(x, y);
		d.setSize(280,75);
		d.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				d.setVisible(false);
			}	
		});	
	}
}
