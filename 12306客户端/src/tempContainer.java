import java.util.Hashtable;


public class tempContainer {
	//ÁÙÊ±ÈÝÆ÷
	private static Hashtable<String,String> ticketInfo ;
	public static Hashtable<String,String> getTicketInfo(){
		if(ticketInfo==null){
			ticketInfo = new Hashtable<String,String>() ;
		}
		 return ticketInfo;
	}
}
