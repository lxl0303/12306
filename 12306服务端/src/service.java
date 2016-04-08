import java.io.IOException;
import java.math.BigDecimal;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

public class service implements Runnable {

	 private dataBase db = dataBase.getInstance();
	 private Hashtable<String, String> ticketInfo;
	 private String[] ticInfFromcli;
	 private String phone;
	 private int code ;
	 private Hashtable<String, Integer> ht =ticketNumber.getTicketContainer();
	 private  String data;
	 private String[] str;
	 private boolean flag;

	 private Socket s;
	 private Lock l;
	 
	 public service(Socket s,Lock l){
		this.s = s;
		this.l = l;
	 }
	
	public void run(){
		try {
			flag =true;
			while(true){
				if(flagService())
					loginService();//登录：根据接受的电话号码是否注册进行处理，如注册发送验证码到客户端和手机上，未注册不处理	
				else 
					registerService();//注册
			}	
		} catch (Exception e) {
		}
	}
	
	private boolean flagService() throws Exception{
		if(flag)
			System.out.println("———————线程："+Thread.currentThread().getName()+"开启了——————————");	
		flag =false;	
		//读,在这不用工具类，因为工具类要求必须有返回值，体现不了阻塞式方法的作用
		byte[] b =new byte[1024];
		int len;
		len = s.getInputStream().read(b);
		data = new String(b,0,len);
		System.out.println("服务器收到信息："+data);
		//分割数据，获取号码
		str= data.split(":");
		phone = str[0];
		return str.length==1?true:false;
	}
	 
	private void registerService() throws Exception{
		System.out.println("——————————————注册处理————————————————");
		if(!db.contain(phone)){//防止重复注册
			//写入数据库
			db.getuserInfo().add(data);
			//获取用户名
			String user = str[1];
			//注册成功
			System.out.println("用户"+user+"注册成功！！！");
			//写
			IOTool.write(s, "r");
		}else{
			System.out.println("该用户已注册过了");
			//写
			IOTool.write(s, "ur");
		}
	}

	private void loginService() throws Exception {
		//判断手机号是否注册，若是发送验证码到12306客户端
		if(recePhoService()){
			//发送验证码到手机
			sendCodeService();		
			//发送客户名和车票信息到买票界面
			sendInitInfo();	
			//实现读取客户端的票信息，发送给客户端剩余的票数
			ticketService();
		}	
	}

	private void ticketService() throws Exception  {
		System.out.println("——————第四步：用户订退票处理（标志着用户正式登陆了）—————————");
		while(true){ 
			ticInfFromcli =IOTool.read(s).split(":");
			if(ticInfFromcli[0].equals("我要退出了")){//客户端退出登录
				System.out.println("很遗憾，用户"+db.getUser(phone)+"退出客户端"+System.lineSeparator()
						+"———————————————————————————————————————");
				break;		
			}else if(ticInfFromcli.length==1){
				System.out.println("客户端退全票信息发来");
				returnAllService();//退全票处理
			}else if(ticInfFromcli.length==3){
				if(new Integer(ticInfFromcli[2])==0){
					System.out.println("客户端订票信息发来:"+ticInfFromcli[0]+":"+ticInfFromcli[1]);
					bookService();//订票处理
				}	
				else if(new Integer(ticInfFromcli[2])==1){
					System.out.println("客户端退票信息发来:"+ticInfFromcli[0]+":"+ticInfFromcli[1]);
					returnService();//退票处理
				}		
			}
		}	
	}
	
	private void returnAllService() {
		l.lock();
		try{
			//剩票处理
			String usertn,dbtn;
			Iterator<String> dbIt;
			Iterator<String> userIt =ticketInfo.keySet().iterator();
			while(userIt.hasNext()){
				 usertn= userIt.next();
				 dbIt=ht.keySet().iterator();
				 while(dbIt.hasNext()){
					dbtn=dbIt.next();
					if(usertn.equals(dbtn)){
						 Integer v = ht.get(dbtn);
						 v++;
						 ht.remove(dbtn);
						 ht.put(dbtn, v);
						 break;
					}		
				}
			}
			//全退
			ticketInfo.clear();
			System.out.println("这个用户肯定太不给力了，竟然退全票！！！");
			System.out.println("****************************************");
		}finally{
			l.unlock();
		}	
	}

	private void returnService() throws Exception{
		l.lock();
		try{
			//删除指定数据
			if(ticketInfo!=null){
				Iterator<Entry<String, String>> i =  ticketInfo.entrySet().iterator();
				while(i.hasNext()){
					Entry<String, String> entryInfo = i.next();
					String key = entryInfo.getKey();
					String value = entryInfo.getValue();
					if(ticInfFromcli[0].equals(key)&&ticInfFromcli[1].equals(value)){
						System.out.println("用户退票信息：车次为"+key+"，订票时间为"+value);
						i.remove();
						System.out.println("此时用户车票信息为："+System.lineSeparator()+ticketInfo);
						System.out.println("****************************************************");
						//剩票处理
						Iterator<String> it =ht.keySet().iterator();
						while(it.hasNext()){
							String k =it.next();
							if(ticInfFromcli[0].equals(k)){
								 Integer v = ht.get(k);
								 v++;
								 ht.remove(k);
								 ht.put(k, v);
								 IOTool.write(s,new String(v+""));
								 break;
							}		
						}
						break;
					}
				}
			}
		}finally{
			l.unlock();
		}
	}
	
	private void bookService() throws Exception{
		l.lock();
		try{	
			ticketInfo.put( ticInfFromcli[0],  ticInfFromcli[1]);
			System.out.println("用户订票信息：车次为"+ticInfFromcli[0]+"，订票时间为"+ticInfFromcli[1]);
			System.out.println("此时用户车票信息为："+System.lineSeparator()+ticketInfo);
			System.out.println("****************************************************");
			String k;
			//剩票处理
			Iterator<String> it =ht.keySet().iterator();
			while(it.hasNext()){
				k =it.next();
				if(ticInfFromcli[0].equals(k)){
					 Integer v = ht.get(k);
					 v--;
					 ht.remove(k);
					 ht.put(k, v);
					 IOTool.write(s,new String(v+""));
					 break;
				}		
			}
		}finally{
			l.unlock();
		}	
	}

	private boolean recePhoService() throws Exception{
		System.out.println("——————————————登录处理——————————————————");
		System.out.println("已注册的用户信息（电话+用户名+身份证）：");
		System.out.println(db.getuserInfo());
		System.out.println("共有"+db.getuserInfo().size()+"个用户注册了，努力突破一亿用户！！！");
		System.out.println("———————————第一步：用户是否注册处理——————————————");
		if(!db.contain(phone)){
			System.out.println("号码"+phone+"尚未注册");	
			//写
			IOTool.write(s,"ur");
			//读,只为了与写操作对应
			System.out.println(IOTool.read(s));
			return false;
		}else{
			System.out.println("号码"+phone+"注册了");	
			//生成验证码
			code =new BigDecimal(1000+Math.ceil(Math.random()*8999)).setScale(0).intValue();
			System.out.println("生成验证码："+code);
			//写
			IOTool.write(s,"r");
			//读，只为了与写操作对应
			System.out.println(IOTool.read(s));
			return true;
		}
		
	}
	
	private void sendCodeService() throws InterruptedException, IOException {
		System.out.println("——————————第二步：发送验证码到手机和12306客户端——————————");
		
		//写给手机
		IOTool.write(s, new String(phone+":"+new Integer(code)));
		
		//读
		System.out.println("收到手机信息："+IOTool.read(s));
		
		//写给12306客户端
		s.getOutputStream().write(new String(code+"").getBytes());
		
		//为了将前后写方法分开，有没有更好的的方法
		//方式一：关闭Socket，但是要重新创建Socket
//		s.close();
//		s=ss.accept();
		//方式二：关闭标记，但是后面不能进行写操作
//		s.shutdownOutput();
		//方式三：随便调用Socket某个方法，无效
//		System.out.println(s.getInetAddress().getHostAddress());
		//方式四：读12306客户端
		System.out.println(IOTool.read(s));	
	}
	
	private void sendInitInfo() throws Exception {		
		System.out.println("———————————第三步：用户初始化处理————————————————");
		
		//发送用户名字和车次
		IOTool.write(s, db.getUser(phone)+":"+ht.keySet().toString());
		//读取回馈信息
		System.out.println("收到客户端信息："+IOTool.read(s));
		
		//1.根据号码获取用户信息，并取得存放票信息的容器
		String info = phone+":"+db.getUser(phone)+":"+db.getID(phone);
		ticketInfo = db.getInfo().get(info);
		if (ticketInfo!=null) {
			System.out.println("该注册用户的车票信息"+System.lineSeparator()+ticketInfo);
			if (ticketInfo.size()!=0) {
				//2.遍历存储车票信息的容器依次发过去
				Iterator<Entry<String, String>> i = ticketInfo.entrySet().iterator();
				while (i.hasNext()) {
					Entry<String, String> ei = i.next();
					String key = ei.getKey();
					Iterator<String> it =ht.keySet().iterator();
					while(it.hasNext()){
						String k =it.next();
						if(key.equals(k)){
							 Integer v = ht.get(k);
							 IOTool.write(s,new String(key + ":" + ei.getValue() + ":" +v));
						}		
					}
				}
			}else{
				IOTool.write(s,System.lineSeparator());
			}		
		}else{
			ticketInfo = new Hashtable<String, String>();
			db.getInfo().put(info, ticketInfo);
			IOTool.write(s,System.lineSeparator());
		}
	}
}
