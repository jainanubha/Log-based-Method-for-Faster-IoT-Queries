import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Scanner;

public class make_log {

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		String url = "jdbc:postgresql://localhost:5433/smartcity";
		String user = "postgres";
		String pswrd = "postgres";
		Connection con = DriverManager.getConnection(url, user, pswrd);
		
		
		// fixed order of query execution
				Map<Integer, Integer> queryFreq = new HashMap<>();
				for(int k=1;k<=21;k++){
					switch (k) {
					case 1:
						queryFreq.put(k, 40);
						break;
					case 2:
						queryFreq.put(k, 32);
						break;
					case 3:
						queryFreq.put(k, 88);
						break;
					case 4:
						queryFreq.put(k, 32);
						break;
					case 5:
						queryFreq.put(k,24);
						break;
					case 6:
						queryFreq.put(k, 16);
						break;
					case 7:
						queryFreq.put(k, 80);
						break;
					case 8:
						queryFreq.put(k, 24);
						break;
					case 9:
						queryFreq.put(k, 24);
						break;
					case 10:
						queryFreq.put(k, 56);
						break;
					case 11:
						queryFreq.put(k, 40);
						break;
					case 12:
						queryFreq.put(k, 32);
						break;
					case 13:
						queryFreq.put(k, 16);
						break;
					case 14:
						queryFreq.put(k, 8);
						break;
					case 15:
						queryFreq.put(k, 80);
						break;
					case 16:
						queryFreq.put(k, 64);
						break;
					case 17:
						queryFreq.put(k, 40);
						break;
					case 18:
						queryFreq.put(k, 8);
						break;
					case 19:
						queryFreq.put(k, 8);
						break;
					case 20:
						queryFreq.put(k, 32);
						break;
					case 21:
						queryFreq.put(k, 32);
						break;
					}
				}// 16
				CallableStatement proc = null;
				System.out.println("start time : "+Calendar.getInstance().getTime());
				for(int a=1;a<=21;a++){
					proc = con.prepareCall("{ ? = call query(?) }");
					proc.registerOutParameter(1, Types.OTHER);
					proc.setInt(2, a);
					
					for(int b=1;b<=queryFreq.get(a);b++){
						proc.execute();
						System.out.println(" done with query "+b+" for query_num= "+a);
					}
				}
				
				
				System.out.println("end time : "+Calendar.getInstance().getTime());
				
				if(!con.isClosed())
					con.close();
				
		
		// for fixed order with frequency given with queries
		/*System.out.println("start time : "+Calendar.getInstance().getTime());
		String key = null;
		Integer value = 0;
		int b = 0;
		for (HashMap.Entry<String, Integer> entry : query_list.entrySet()) {
			b++;
			key = entry.getKey();
			value = entry.getValue();
			for(int a=0;a<value;a++){
				sqlst = key;
				st.executeUpdate(sqlst);
				System.out.println("done with query "+ sqlst +" for a= "+(a+1));
			}
		}
		System.out.println("end time : "+Calendar.getInstance().getTime());
		
		if(!con.isClosed())
			con.close();*/
				
		
		
		
	}

}
