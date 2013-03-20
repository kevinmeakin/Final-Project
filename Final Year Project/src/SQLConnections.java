import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class SQLConnections{

	public static void addData(){
		
		//Set constants to connect to the database
		String route, point;
		Connection connect = null;
		String loc = "jdbc:mysql://localhost:3306/";
		String dB = "cs408";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root"; 
		String password = "passw";
		String points;


		try {
			//Read in files
			BufferedReader permitted_route_filereader = new BufferedReader(new FileReader("permitted_route_identifierTEMPNEW.txt"));
			BufferedReader routing_point_filereader = new BufferedReader(new FileReader("routeing_point_identifierTEMPNEW.txt"));

			//Connect to the database
			Class.forName(driver).newInstance();
			connect = DriverManager.getConnection(loc+dB,userName,password);

			//Remove data from table
			Statement remove1 = connect.createStatement();
			remove1.executeUpdate("delete from Permitted_Routes");
			
			/*
			 * Delete the data currently in the table
			 */
			Statement remove = connect.createStatement();
			remove.executeUpdate("delete from Routeing_Points");
			
			connect.close();

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
					connect = DriverManager.getConnection(loc+dB,userName,password);

					//Add data to the table
					Statement addData = connect.createStatement();
					addData.executeUpdate("insert into Permitted_Routes (Start_Station ,End_Station , Routes) " +
							"values ('" + information[0]+ "','" + information[1] + "','" + points + "')");

					connect.close();
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
					connect = DriverManager.getConnection(loc+dB,userName,password);
					
					/*
					 * Add data to the database
					 */
					Statement addData1 = connect.createStatement();
					addData1.executeUpdate("insert into Routeing_Points (Station ,Route_Point) " +
							"values ('" + data[0]+ "','" + points + "')");


					connect.close();
					
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
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {

		addData();

	}
}
