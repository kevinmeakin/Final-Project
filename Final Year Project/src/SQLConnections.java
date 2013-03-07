import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class SQLConnections{

	public static void addData(){
		
		//Set constants to connect to the database
		String route, point;
		Connection connec = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "cs408";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root"; 
		String password = "celtic";
		String points;


		try {
			//Read in files
			BufferedReader permitted_route_filereader = new BufferedReader(new FileReader("permitted_route_identifierTEMPNEW.txt"));
			BufferedReader routing_point_filereader = new BufferedReader(new FileReader("routeing_point_identifierTEMPNEW.txt"));

			while ((route = permitted_route_filereader.readLine()) != null) {

				//Split data in order to have correct syntax when inputting to database
				String[] information = route.split(" ");
				points="";
				if(information.length == 3){
					points = information[2];
				} else{
					for(int i = 2; i<=information.length-2;i++){
						points = points + information[i] + " ";		
					}
				}

				try {
					//Connect to the database
					Class.forName(driver).newInstance();
					connec = DriverManager.getConnection(url+dbName,userName,password);

					//Remove data from table
					Statement remove1 = connec.createStatement();
					remove1.executeUpdate("delete from Permitted_Routes");
					
					//Add data to the table
					Statement addData = connec.createStatement();
					addData.executeUpdate("insert into Permitted_Routes (Start_Station ,End_Station , Routes) " +
							"values ('" + information[0]+ "','" + information[1] + "','" + points + "')");

					connec.close();
				} catch (Exception e) {
					//	e.printStackTrace();
				}
			}


			while ((point = routing_point_filereader.readLine()) != null) {

				String[] data = point.split(" ");
				points="";
				if(data.length == 2){
					points = data[1];
				} else{
					for(int i = 1; i<=data.length-1;i++){
						points = points + data[i] + " ";		
					}
				}

				try {
					//Connect to the database
					Class.forName(driver).newInstance();
					connec = DriverManager.getConnection(url+dbName,userName,password);

					/*
					 * Delete the data currently in the table
					 */
					Statement remove = connec.createStatement();
					remove.executeUpdate("delete from Routeing_Points");
					
					/*
					 * Add data to the database
					 */
					Statement addData1 = connec.createStatement();
					addData1.executeUpdate("insert into Routeing_Points (Station ,Route_Point) " +
							"values ('" + data[0]+ "','" + points + "')");


					connec.close();
					
				} catch (Exception e) {
					//	e.printStackTrace();
				}



			}


		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		addData();

	}
}
