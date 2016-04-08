import java.awt.Button;
import java.awt.Choice;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.sql.Date;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;

import mobilePhone.tool;


public class buyTicket extends Frame implements Runnable{
	
	private static final long serialVersionUID = 1L;
	
	private Label name;
	private Button exit;
	
	private Button bookTicket;
	private Label bookTrain;
	private Choice bttn;
	
	private Button returnTicket;
	private Label returnTrain;
	private Choice rttn;
	private Label buyTickTime;
	private Choice year;
	private Label ylabel;
	private Choice month;
	private Label mlabel;
	private Choice day;
	private Label dlabel;
	
	private Button query;
	private Button returnAllTicket;
	
	private Label service;
	private Button send;
	private TextField chat;
	private TextArea showChat;
	
	private Label blank;
	private Label blank1;
	private Label blank2;
	
	private Calendar c = Calendar.getInstance();
	
	private String user;
	
	private int ticketNum =0;//存储一个用户的订票数标记
	private Hashtable<String, String> subtm = tempContainer.getTicketInfo();//存储一个用户的订票信息的容器对象
	
	private Socket s;
	private String tn;//订票车次
	private Date time;//订票时间
	private String key; 
	private String value; 
	private String leftNum;//剩余票数
	
	public buyTicket(Socket s){
		this.s = s;
	}
	
	public buyTicket() {
		
	}

	public void run(){
		
		name = new Label();
		exit =new Button("退出");
		
		blank = new Label("————————订票————————");
		bookTicket =new Button("订票");
		bookTrain = new Label("订票车次  ");
		bttn =new Choice();
		
		blank1 = new Label("————————退票————————");
		returnTicket =new Button("退票 ");
		returnTrain = new Label("退票车次  ");
		rttn =new Choice();
		buyTickTime = new Label("买票时间  ");
		year=new Choice();
		ylabel=new Label("年");
		month=new Choice();
		mlabel=new Label("月");
		day=new Choice();
		dlabel=new Label("日");
		
		blank2 = new Label("————————查询与全退————————");
		query =new Button("查询");
		returnAllTicket = new Button("全退");
		
		
		send =new Button("发送");
		chat =new TextField(20);
		showChat =new TextArea(8,25);
		service =new Label("————————咨询客服窗口————————");		
		
		year.add("");
		year.add(c.get(Calendar.YEAR)+"");
		if(hasTwelve())
			year.add(c.get(Calendar.YEAR)+1+"");
		
		month.add("");
		for(int i=0;i<3;i++)
			month.add(getProperMonth(c.get(Calendar.MONTH)+i+1));
		
		day.add("");
		for(int i=1;i<32;i++)
			if(i<10)
				day.add("0"+i);
			else
				day.add(""+i);
		
		
		this.add(name);
		this.add(exit);
		
		String[] init = tool.read(s).split(":");
		//解析用户名
		user=init[0];
		name.setText("尊敬的"+user+",您好：");
		//解析车次
		String[] strtn =init[1].substring(1, init[1].length()-1).split(", ");
		//添加车次
		bttn.add("");
		rttn.add("");
		for(int i=0;i<strtn.length;i++){
			bttn.add(strtn[i]);
			rttn.add(strtn[i]);
		}
		//写
		tool.write(s,"客户端初始化顺利完成"); 
//		System.out.println("客户端初始化完成！！！");
		
		this.add(blank);
		this.add(bookTicket);
		this.add(bookTrain);
		this.add(bttn);
		
		this.add(blank1);
		this.add(returnTicket);
		this.add(returnTrain);
		this.add(rttn);
		
		this.add(buyTickTime);
		this.add(year);
		this.add(ylabel);
		this.add(month);
		this.add(mlabel);
		this.add(day);
		this.add(dlabel);
		
		this.add(blank2);
		this.add(query);
		this.add(returnAllTicket);
		
		this.add(service);
		this.add(showChat);
		showChat.setEditable(false);
		this.add(chat);
		this.add(send);
		
		this.setTitle("12309售票窗");
		this.setVisible(true);
		this.setSize(280, 540);
		this.setLayout(new FlowLayout());
		this.setLocation(725, 20);
		
		//读取车票信息,换行符长度为2
		String info =tool.read(s);
		if(info!=System.lineSeparator()){
			String[] data=info.split(System.lineSeparator());
			for(int i=0;i<data.length;i++){
				String[] subData = data[i].split(":"); 	
			    subtm.put(subData[0], subData[1]+":"+subData[2]);
			}
		}	
		System.out.println("用户车票信息初始化完成："+subtm);
		
		exit.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				tool.write(s,"我要退出了");
				System.exit(0);
			}
		});
		
		bookTicket.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//获取车次
				tn = bttn.getSelectedItem();
				//获取订票时间
				if (tn!="") {
					time =new Date(System.currentTimeMillis()) ;
					//囤票机制：一天不能连续订同一车次，即一车次只能一张，且一天订票张数不超过3张，注意不与服务端通信，只需先将客户端的囤票实现即可
					if(ticketNum<3&&!subtm.containsKey(tn)){
						//发给服务器
						try {
							leftNum = sendTicketInfo(s,tn,time+"",0);
						} catch (Exception e1) {
						
						}
						//票数加一标记
						ticketNum++;
						//放进本地容器
						subtm.put(tn, time+":"+leftNum);//添加剩余票数
						//提示
						tool.myAlert2(new buyTicket(), "订票成功", 725, 50);
					}	
				}
			}	
		});
				
		returnTicket.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//获取车次
				String str = rttn.getSelectedItem();
				//获取时间
				String y = year.getSelectedItem();
				String m = month.getSelectedItem();
				String d = day.getSelectedItem();
					
				//删除容器数据
				if (str!=""&&y!=""&&m!=""&&d!="") {	
					int flag = 0;
					Entry<String, String> tickentry;
					Iterator<Entry<String, String>> i = subtm.entrySet().iterator();
					while(i.hasNext()){
						tickentry = i.next();
						key = tickentry.getKey();
						String[] strInfo = tickentry.getValue().split(":");
						value = strInfo[0];	
						if(flag==1)
							break;
						else if(key.equals(str)&&value.equals(y+"-"+m+"-"+d)){
							//发送退票信息给服务器
							try {
								leftNum = sendTicketInfo(s,key,value,1);
							} catch (Exception e1) {
							
							}
							//票数标记减一
							ticketNum--;
							//删除的是当前票，无法带参数指定
							i.remove();
							/*删除最近的一张票 : ht.remove(db.getId()-1);*/
							
							//提示
							tool.myAlert2(new buyTicket(), "退票成功", 725, 50);
							//标志改变为1，退出遍历退票操作
							flag = 1;	
						}
					}
					if (leftNum!=null) {//对出现在不同天订相同车次的剩票数进行更改
						while (i.hasNext()) {
							tickentry = i.next();
							String[] strInfo = tickentry.getValue().split(":");
							if (tickentry.getKey().equals(str)) {
								tickentry.setValue(strInfo[0] + ":"+ leftNum);
							}
						}
					}	
				}	
			}
		});
		
		query.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				new Thread(showTickInfo.getInstance()).start();
			}
			
		});
		
		returnAllTicket.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ticketNum=0;
				subtm.clear();
				//向服务端发出标志，说明该用户所有票消息删除
				tool.write(s,System.lineSeparator());
				//提示
				tool.myAlert2(new buyTicket(), "用户您退全票成功，我们感觉很遗憾", 725, 50);
			}	
		});
		
		send.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//1.获取文本内容
				String chatText = chat.getText();
				if(chatText!=null){
					//2.将文本内容显示到文本框中,并将文本框内容删除
					String content = user+":"+chatText+System.lineSeparator();
					showChat.append(content);
					chat.setText(null);
					//3.通过TCP协议与服务器实现全双工通信
//					Socket s;
//					try {
//						s = new Socket("localhost", 20001);
//						Thread cr =new Thread(new clientRead(s));
//						cr.start();
//						System.out.println("客户端读线程"+cr.getName()+"开启");
//						
//						Thread cw =new Thread(new clientWrite(s));
//						cw.start();
//						System.out.println("客户端写线程"+cw.getName()+"开启");
//					} catch (Exception e3) {
//						
//					} 	
				}	
			}
			
		});
	}


	private String getProperMonth(int i) {
		if(i%12==0)
			i=12;
		else
			i=i%12;
		if(i<10)
			return "0"+i;
		else
			return i+"";
	}

	private boolean hasTwelve() {
	
		return (c.get(Calendar.MONTH)+1)%12==0||(c.get(Calendar.MONTH)+2)%12==0?true:false;
	}
	
	//实现发送买票或退票信息给服务端，读取服务端剩余票数，为什么不能从服务端一次获取票的总数，出于多用户的可考虑
	private String sendTicketInfo(Socket s,String key,String value,int flag) throws Exception{
		tool.write(s,(key+":"+value+":"+flag));
		return tool.read(s);
	}
}
