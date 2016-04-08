import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

public class dataBase {
	
	private static dataBase db;
	
	private Hashtable<String,Hashtable<String,String>> mydatabase ;//存储特点：后进先出，放用户信息和车票信息
	private ArrayList<String> userinfo ;//放用户信息
	
	private final int maxsize =100;//如何动态？减少内存分配
	private String[] elem  = new String[maxsize];
	private String[] pho = new String[maxsize];
	private String[] user = new String[maxsize];
	private String[] id = new String[maxsize];
	
	private dataBase(){
		
	}
	
	public static dataBase getInstance(){	
		if(db==null)
			db = new dataBase();
		return db;
	}
	
	public int query(String phone){
		int i;
		if (userinfo!=null&&userinfo.size()!=0){
			for (i = 0; i < pho.length; i++)
				if (pho[i].equals(phone))
					return i;
			return -1;
		}			
		return -1;
			
	}
	public String getUser(String phone){
		int index = query(phone);
		if(index==-1)
			return null;
		return user[index];
		
	}
	
	public String getID(String phone){
		int index = query(phone);
		if(index==-1)
			return null;
		return id[index];
		
	}
	
	public boolean contain(String phone){
		splitUserInfo();
		int index = query(phone);
		if(index==-1)
			return false;
		return true;
	}
	
	private void splitUserInfo(){
		int i=0;
		if(userinfo!=null&&userinfo.size()!=0){
			Iterator<String> it = userinfo.iterator();
			while(it.hasNext()){
				elem= it.next().split(":");
				pho[i] =elem[0];
				user[i] =elem[1];
				id[i] = elem[2];
				i++;
			}
		}
	}
	
	public Hashtable<String,Hashtable<String,String>> getInfo(){
		if(mydatabase==null){
			mydatabase = new Hashtable<String,Hashtable<String,String>>() ;
		}
		 return mydatabase;
	}
	
	
	public  ArrayList<String> getuserInfo(){
		if(userinfo==null)
			userinfo= new  ArrayList<String>() ;
		 return userinfo;
	}
	
	
}
