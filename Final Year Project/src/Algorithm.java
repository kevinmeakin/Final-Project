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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.omg.CORBA.portable.InputStream;


public class Algorithm {


	public static void testerer() throws IOException{
		// Make a URL to the web page

		URL url = new URL("http://traintimes.org.uk/edb/ply/23:00/today");

		// Get the information through a URLConnection
		URLConnection con = url.openConnection();
		java.io.InputStream is =con.getInputStream(); // Get the data from the webpage in


		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		String line = null;

		while ((line = br.readLine()) != null) {

			CharSequence pound = ">&pound;", ad = "Advance";
			if(line.contains(pound) && !line.contains(ad)){
				int findingPound = line.indexOf(">&pound;");

				String work = line.substring(findingPound+8, findingPound+15);

				// Manipulation to make sure we always have a full double value 
				int findpoint = work.indexOf(".");
				work = work.substring(0, findpoint+3);
				System.out.println(work) ;
			}
		}
	}


	public static String findRoutingPoints(String station) throws Exception{

		Connection connec = null;
		String url = "jdbc:mysql://localhost:3306/";
		String dbName = "cs408";
		String driver = "com.mysql.jdbc.Driver";
		String userName = "root"; 
		String password = "passw";

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

		if(station1.length == 1){
			
			
		}
		for(int i = 0; i<station1.length;i++){
			for(int h = 0; h<station2.length;h++){
				if(station1[i].equals(station2[h])){
					return station1[i];
				}
			}
		}
		return "no";
	}

	public static String recursive_route(String start, String end, String fullRoute, double route_Distance, 
			String used_Stations, double overallLowest,String finalLowest){

		String[] fRoute = fullRoute.split(" ");
		String next="";
		String worthago = "";

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
			if(overallLowest > route_Distance){
				overallLowest = route_Distance;
				finalLowest = fullRoute;
			}
			allinfo = finalLowest + " " + route_Distance;
			
			try {
				File f = new File("Attempt.txt");
				if(f.exists()){
					FileWriter out = new FileWriter(f,true);
					out.write(allinfo);
					out.write("\n");
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
					for(String rr : fullfile){
						String[] mm = rr.split(" ");

						if(mm[1].equals(next)){

							for(int k = 2; k <= mm.length -2; k +=2){
								CharSequence curStat = mm[k];

								if(!fullRoute.contains(curStat) || curStat.equals(end)){
									used_Stations = used_Stations + " " + mm[k];
									fullRoute = fullRoute + " " + mm[k];
									route_Distance = route_Distance + Double.parseDouble(mm[k+1]);
									worthago = recursive_route(start,end,fullRoute,route_Distance,used_Stations,overallLowest,finalLowest);
									fullRoute = fullRoute.replace(mm[k], "");
									route_Distance = route_Distance - Double.parseDouble(mm[k+1]);
								}
							}
							if(used_Stations.contains(cur)){
								return worthago;
							}
						}	
					}
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
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
		String password = "passw";
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

		while((currentLine = filereader.readLine()) != null){
			String[] curLineSplit = null;


			for(int i = 0;i < routes.length;i++){
				/*
				 * If the length of the route is 2, there is only one map used meaning
				 * the start and end stations are both on it
				 */
				if(routes[i].length() == 2){

					String h =recursive_route(s1, s2, s1, 0, s1,0,"");
					//System.out.println("OUTPUT:: "+ h);
				}
			}
		}



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

		testerer();
		//System.out.print("Enter your Start Station: ");

		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String station = "WRX";

//		try {
//			station = br.readLine();
//		} catch (IOException ioe) {
//			System.exit(1);
//		}

		try {
			r1 = findRoutingPoints(station);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Routing points: "+r1);
		//System.out.print("Enter your End Station: ");

		//  open up standard input
		//BufferedReader br1 = new BufferedReader(new InputStreamReader(System.in));

		String endstation = "SMR";

//		try {
//			endstation = br1.readLine();
//		} catch (IOException ioe) {
//			System.exit(1);
//		}

		try {
			r2 = findRoutingPoints(endstation);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Routing points: "+r2);
		String same = compare(r1, r2);

		if(same.equals("no")){
			//TODO: Create part of algorithm that searches when there are no routing points in common
			
			shortest_route(station, endstation);
		}else{
			//TODO: Create search for when a common routing point is found
			
			//find shortest distance between start point and routing point 
			shortest_route(station, same);
			
			//find shortest route between routing point and destination
			shortest_route(same, endstation);
			
			//Calculate the shortest route and that is the valid one

		}

		BufferedReader calcShortest = new BufferedReader(new FileReader("Attempt.txt"));

		String readout;
		String shortestRoute = "";
		Set<String> routeList = new HashSet<String>(10000); 

		while((readout = calcShortest.readLine()) != null){
			
			routeList.add(readout);
			
		}
		
		double lowval =10000000;
		
		for(String route : routeList){
			
			String[] sep = route.split(" ");
			double temp = Double.parseDouble(sep[sep.length-1]);
			
			if(temp < lowval){
				lowval = temp;
				shortestRoute = route;
			}
						
		}
		System.out.println(shortestRoute);

		
		File f = new File("Attempt.txt");
		if(f.exists())
		  {
		    f.delete();
		  }
		  else 
		  {
		    System.out.println("File not found to delete");
		  }
	
	}
}
