package serverConnection;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Hashtable;

public class server {
	private static Connection mainCon;
	static Statement mainStat = null;
    static ResultSet mainResultset = null;
	public static void main(String[] args) {
        connectDB();
        int port = 9777;                                            //port used (a variable)
        
        try {
			ServerSocket servsock = new ServerSocket(port);         //server socket created
			Socket clntsock = new Socket();                         //socket for each client
			clntsock = servsock.accept();                           //accepting a connection from a client
            int i=0;
            Hashtable<String,String> ht=new Hashtable<String,String>();
            ObjectOutputStream os = new ObjectOutputStream(clntsock.getOutputStream());
            BufferedReader is = new BufferedReader(new InputStreamReader(
                    clntsock.getInputStream()));
            String messagefromclient="";
            while(is!=null)
            {
            	String obj = new String((String) is.readLine());
                messagefromclient = obj;
            // getting message from Android device
            System.out.println("message from Android phone: " + messagefromclient);
            if (messagefromclient.contains("MESSAGEA"))
            {
            	String[] arr = messagefromclient.split(" ");
            	String value = arr[1];
            	
            	String mysql = "SELECT * FROM LIBRARY_DB WHERE ROOM_NO='"+value+"'";
            	
            	mainResultset = mainStat.executeQuery(mysql);
            	int result =0;
            	if(mainResultset.next()){
            		 result = mainResultset.getInt(2);	
            	}
            	 
            	if(result == 0){
            		result = 1;
            	}else{
            		result = 0;
            	}
                
            	String updateQuery = "UPDATE LIBRARY_DB SET FLAG_OCCUPANCY = '"+result+"' WHERE ROOM_NO = '"+value+"'";
            	mainStat.executeUpdate(updateQuery);

            }
            else if(messagefromclient.equals("MESSAGEB"))
            {
            	String mysql = "SELECT * FROM LIBRARY_DB";
            	
            	mainResultset = mainStat.executeQuery(mysql);
            	
            	while(mainResultset.next()){
            		 System.out.println(mainResultset.getString(1)+"   "+mainResultset.getInt(2));	
            	}
            }
            os.reset();
            }
             is.close();
             clntsock.close();
             servsock.close();
		}
        catch (Exception e) {
			System.out.println(e);
		}
	}
    
    public static void connectDB()
    {
        try {
            // loading Oracle Driver
            System.out.print("Looking for Oracle's jdbc-odbc driver ... ");
            DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
            System.out.print("Loaded.");
            
            String url = "jdbc:oracle:thin:@216.240.46.30:1521:trainingdb";
            String userId = "gra";
            String password = "gra";
            
            System.out.print("Connecting to DB...");
            mainCon = DriverManager.getConnection(url, userId, password);
            mainStat = mainCon.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("connected !!");
            
        } catch (Exception e) {
            System.out.println( "Error while connecting to DB: "+ e.toString() );
            e.printStackTrace();
            System.exit(-1);
        }
        
    }
}
