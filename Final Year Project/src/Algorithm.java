import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;

import org.omg.CORBA.portable.InputStream;


public class Algorithm {


	public static void testerer() throws IOException{
		// Make a URL to the web page

		URL url = new URL("http://ojp.nationalrail.co.uk/service/timesandfares/PLY/GLC/today/0215/dep");

		// Get the input stream through URL Connection
		URLConnection con = url.openConnection();
		java.io.InputStream is =con.getInputStream();

		// Once you have the Input Stream, it's just plain old Java IO stuff.

		// For this case, since you are interested in getting plain-text web page
		// I'll use a reader and output the text content to System.out.

		// For binary content, it's better to directly read the bytes from stream and write
		// to the target file.


		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;

		// read each line and write to System.out
		while ((line = br.readLine()) != null) {
			System.out.println(line);
		}
	}
	public static String findRoutingPoints(String station) throws Exception{

		Connection connec = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "cs408";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root"; 
		String password = "celtic";

		Class.forName(driver).newInstance();
		connec = DriverManager.getConnection(url + dbName , userName , password);

		Statement findRP = connec.createStatement();

		ResultSet result;

		String point = "SELECT Route_Point FROM Routeing_Points WHERE Station = '" + station + "'";

		result = findRP.executeQuery(point);

		while(result.next()){

			station=result.getString(1);

		}

		return station;

	}


	public static String compare(String s1,String s2){

		String[] station1 = s1.split(" ");
		String[] station2 = s2.split(" ");

		for(int i = 0; i<=station1.length;i++){
			for(int h = 0; h<=station1.length;h++){
				if(station1[i].equals(station2[h])){
					return station1[i];
				}
			}
		}
		return null;
	}

	public static String recursive_route(String start, String end, String fullRoute, double route_Distance, 
			String used_Stations, double overallLowest,String finalLowest){

		String[] fRoute = fullRoute.split(" ");
		String next="";
		if(fRoute.length !=0){
			next = fRoute[fRoute.length-1];
		}
		String allinfo;
		CharSequence cur = next;
		String looper = "";
		ArrayList<String> fullfile = new ArrayList<String>();


		if(next.equals(end)){

			if(overallLowest == 0){
				overallLowest = route_Distance;
				finalLowest = fullRoute;
			}
			final String wplz = finalLowest;
			if(overallLowest > route_Distance){
				overallLowest = route_Distance;
				finalLowest = fullRoute;
			}

			System.out.println("ROUTE: " + finalLowest);
			System.out.println("Distance: " + route_Distance);
			allinfo = finalLowest + " " + route_Distance;
			try {
				File f = new File("Attempt.txt");
				if(f.exists()){
					FileWriter out = new FileWriter(f,true);
					out.write(allinfo);
					out.close();
				}else{
					BufferedWriter writer = new BufferedWriter(new FileWriter("Attempt.txt"));

					writer.write(allinfo);
					writer.newLine();
					writer.close();
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}




			return allinfo;
		}else{
			try {
				BufferedReader filereader = new BufferedReader(new FileReader("mapsinfoTEMPNEW.txt"));
				String currentLine;

				while((currentLine = filereader.readLine()) != null){
					String station_Info = currentLine;

					fullfile.add(station_Info);
				}
				while(looper==""){
					String worthago = "";
					for(String rr : fullfile){
						String[] mm = rr.split(" ");

							int count = 0;
							if(mm[1].equals(next)){

								for(int k = 2; k <= mm.length -2; k +=2){
									CharSequence curStat = mm[k];

									if(!fullRoute.contains(curStat) || curStat.equals(end)){
										System.out.println(used_Stations);
										used_Stations = used_Stations + " " + mm[k];
										fullRoute = fullRoute + " " + mm[k];
										route_Distance = route_Distance + Double.parseDouble(mm[k+1]);
										worthago = recursive_route(start,end,fullRoute,route_Distance,used_Stations,overallLowest,finalLowest);
										System.out.println("WORK MOFO::::  "+worthago);
										fullRoute = fullRoute.replace(mm[k], "");
										route_Distance = route_Distance - Double.parseDouble(mm[k+1]);
									}
								}
								if(used_Stations.contains(cur)){
									return worthago;
								}
							}
						
					}
					//					if(station_Info[1].equals(next)){
					//						temp = 500000;
					//						newcheck ="";
					//						double check =0;
					//						for(int i = 2;i<=station_Info.length-2;i+=2){
					//							String newstation = station_Info[i];
					//							double distance = Double.parseDouble(station_Info[i+1]);
					//							check = check + distance;
					//							s = newstation;
					//
					//							if(used_Stations.contains(s)){
					//								distance = 500000;
					//							}
					//							if(distance <temp){
					//								temp = distance;
					//								newcheck = newstation;
					//							}
					//
					//						}
					//						if(temp == 500000){
					//							fullRoute = fullRoute.replace(cur, "");
					//							fullRoute = fullRoute.replace("  ", "");
					//							route_Distance = route_Distance - check;
					//							if(route_Distance == 0){
					//								return finalLowest;
					//							}
					//							recursive_route(start,end,fullRoute,route_Distance,used_Stations,overallLowest,finalLowest);
					//
					//						}else{
					//							//TODO: recursion here i think
					//							route_Distance = route_Distance + temp;
					//							used_Stations = used_Stations + " " + newcheck;
					//							fullRoute = fullRoute + " " + newcheck;
					//							recursive_route(start,end,fullRoute,route_Distance,used_Stations,overallLowest,finalLowest);
					//							//							if(!outp.isEmpty()){
					//							//								fullRoute = fullRoute.substring(0, fullRoute.length()-8);
					//							//								recursive_route(start,end,fullRoute,route_Distance,used_Stations);
					//							//							}
					//						}
					//
					//					}
					//
				}

			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return finalLowest;
	}

	public static void shortest_route(String s1,String s2) throws Exception{

		//---------------MYSQL Connection-------------
		Connection connec = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "cs408";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root"; 
		String password = "celtic";
		connec = DriverManager.getConnection(url + dbName , userName , password);
		Statement findRP = connec.createStatement();
		ResultSet result;
		//--------------------------------------------

		BufferedReader filereader = new BufferedReader(new FileReader("mapsinfoTEMPNEW.txt"));

		Class.forName(driver).newInstance();

		//-------------Query and output-----------------
		String point = "SELECT Routes FROM Permitted_Routes WHERE Start_Station = '" + s1 + 
				"' && End_Station = '" + s2 + "'";

		result = findRP.executeQuery(point);
		String route = "";
		CharSequence startSt = s1;
		CharSequence endSt = s2;
		CharSequence plus = "+";
		while(result.next()){

			route=result.getString(1);

		}
		String[] connectedRoutes ;
		String[] routes = route.split(" ");
		//-----------------------------------------------

		//Get start and end stations for current map

		String start = "", end = "", currentLine = "",stationsList = "";

		//		while((currentLine = filereader.readLine()) != null){
		//			String[] curLineSplit = null;


		for(int i = 0;i < routes.length;i++){
			/*
			 * If the length of the route is 2, there is only one map used meaning
			 * the start and end stations are both on it
			 */
			if(routes[i].length() == 2){

				String h =recursive_route(s1, s2, s1, 0, s1,0,"");
				System.out.println("OUTPUT::: "+h);
			}
		}
		//		}



		//		while((currentLine = filereader.readLine()) != null){
		//			String[] curLineSplit = null;
		//
		//			/*
		//			 * If the route has a plus in it I have to link the maps by
		//			 * Same stations on both to calculate the shortest distance.
		//			 */
		//			for(int i = 0;i<=routes.length;i++){
		//
		//				if(currentLine.contains(s1)){
		//					start = s1;
		//				}else if(currentLine.contains(s2)){
		//					end = s2;
		//				}
		//
		//				if(routes[i].contains(plus)){
		//					connectedRoutes = routes[i].split("+");
		//					for(int h = 0; h <= connectedRoutes.length; h++){
		//						String curRoute = connectedRoutes[h];
		//					}
		//				}
		//			}	
		//		}


	}
	public static void main(String[] args) throws Exception{

		String r1 ="";
		String r2 ="";

		//testerer();
		System.out.print("Enter your Start Station: ");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String station = "";

		try {
			station = br.readLine();
		} catch (IOException ioe) {
			System.exit(1);
		}

		//		try {
		//			r1 = findRoutingPoints(station);
		//		} catch (Exception e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}

		System.out.print("Enter your End Station: ");

		//  open up standard input
		BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

		String endstation = "";

		try {
			endstation = br1.readLine();
		} catch (IOException ioe) {
			System.exit(1);
		}

		//		try {
		//			r2 = findRoutingPoints(endstation);
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}

		//	String same = compare(r1, r2);

		//		if(same.equals(null)){
		//			//TODO: Create part of algorithm that searches when there are no routing points in common
		//		}else{
		//			//TODO: Create search for when a common routing point is found
		//		}

		shortest_route("WRX", "SMR");

		//		System.out.println("The Start Station's Routeing Points Are: " + r1);
		//		System.out.println("The End Station's Routeing Points Are:   " + r2);
	}
}
