import java.awt.BorderLayout;
import java.util.Hashtable;
import java.util.Map.Entry;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class showTickInfo extends JFrame implements Runnable{
	
		private static final long serialVersionUID = 1L;
		
		//单例化
		private static showTickInfo q ;
		private showTickInfo(){		
		}
		public static showTickInfo getInstance(){
			if(q==null){
				q = new showTickInfo();
			}
			return q;
		}
				
		private JTable jt;

		private final String[] item = {"序号","车次","订票时间","剩余票数"};
		
		public void run() {
				Object[][] data = null;
				Hashtable<String,String> ticketInfo = tempContainer.getTicketInfo();
				if(!ticketInfo.isEmpty()){
					data =new String[10][4];//如何动态？减少内存分配
					int i=0;
					String[] info;
					for(Entry<String, String> e:ticketInfo.entrySet()){
						data[i][0]=i+"";
						data[i][1]=e.getKey();
						//获取订票时间和剩余票数
						info=e.getValue().split(":");
						data[i][2] =info[0];
						data[i][3] =info[1];	
						i++;
					}
				}
				
				DefaultTableModel dtm = new DefaultTableModel(data,item);
				jt= new JTable();
				jt.setModel(dtm);
				jt.setEnabled(false);
				JScrollPane jsp =new JScrollPane(jt);
				this.add(jsp,BorderLayout.CENTER);
				this.setSize(320, 250);
				this.setTitle("查询");
				this.setVisible(true);
				this.setLocation(1010, 20);
			}
}
