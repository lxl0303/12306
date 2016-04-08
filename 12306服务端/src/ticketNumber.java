import java.util.Hashtable;

public class ticketNumber {
	
	private static int tickNum = 500;
	
	public static String[] tn = {"北京——上海","广州——深圳","广州——杭州","深圳——北京"};
	
	private static Hashtable<String,Integer> ht;
	
	public static  Hashtable<String,Integer> getTicketContainer(){
		if(ht==null){
			ht= new Hashtable<String,Integer>();
			for(int i=0;i<tn.length;i++)
				ht.put(tn[i], tickNum);
		}	
		return ht;
	}	
}
