import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class Algorithm {


	public static double find_Price(String station_1,String station_2) throws IOException{
		// Make a URL to the web page

		URL url = new URL("http://traintimes.org.uk/"+station_1+"/"+station_2+"/23:00/today");
		double price = 100000;
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
				double cheapest = Double.parseDouble(work);

				if(cheapest < price){
					price = cheapest;
				}
			}
		}
		return price;
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
			String used_Stations, double overallLowest,String finalLowest,String curMap){

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

		//Check if we reach the destination node
		if(next.equals(end)){

			if(overallLowest == 0){
				overallLowest = route_Distance;
				finalLowest = fullRoute;
			}
			if(overallLowest > route_Distance){
				overallLowest = route_Distance;
				finalLowest = fullRoute;
			}
			allinfo = curMap + " " + finalLowest + " " + route_Distance;

			//write the current route to file with the total distance to calculate shortest route
			String[] check = allinfo.split(" ");

			if(check.length > 3){
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
					e1.printStackTrace();
				}
			}
			return allinfo;

		}else{

			try {
				//Open files and general variable declaration
				BufferedReader filereader = new BufferedReader(new FileReader("mapsinfoTEMPNEW.txt"));
				String currentLine;

				//Read in the file
				while((currentLine = filereader.readLine()) != null){
					String station_Info = currentLine;
					fullfile.add(station_Info);
				}

				//Loop to ensure the final escape route is never met
				while(looper==""){
					for(String rr : fullfile){
						String[] mm = rr.split(" ");

						if(mm[1].equals(next) && mm[0].equals(curMap)){

							for(int k = 2; k <= mm.length -2; k +=2){
								CharSequence curStat = mm[k];

								/*
								 * Bulk of the algorithm is performed here, checks the current station
								 * against visited stations and if it is the current station.
								 * I add the current station to the list of visited stations and 
								 * calculate a new value for the distance of the route.
								 */
								if(!fullRoute.contains(curStat) || curStat.equals(end)){
									used_Stations = used_Stations + " " + mm[k];
									fullRoute = fullRoute + " " + mm[k];
									route_Distance = route_Distance + Double.parseDouble(mm[k+1]);
									worthago = recursive_route(start,end,fullRoute,route_Distance,
											used_Stations,overallLowest,finalLowest, curMap);
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

	public static Set<String> shortest_route(String s1,String s2) throws Exception{

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

		Class.forName(driver).newInstance();

		//-------------Query and output-----------------
		String point = "SELECT Routes FROM Permitted_Routes WHERE Start_Station = '" + s1 + 
				"' && End_Station = '" + s2 + "'";

		result = findRP.executeQuery(point);
		String route = "", findline ="";
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

		String end = "", last_Stations = "";

		for(int i = 0;i < routes.length;i++){
			/*
			 * If the length of the route is 2, there is only one map used meaning
			 * the start and end stations are both on it
			 */
			if(routes[i].length() == 2){

				recursive_route(s1, s2, s1, 0, s1,0,"",routes[i]);
			}
		}


		//		while((currentLine = filereader.readLine()) != null){
		String[] curLineSplit = null;
		boolean last = false;
		/*
		 * If the route has a plus in it I have to link the maps by
		 * Same stations on both to calculate the shortest distance.
		 */
		for(int i = 0;i<routes.length;i++){

			//				if(currentLine.contains(s1)){
			//					start = s1;
			//				}else if(currentLine.contains(s2)){
			//					end = s2;
			//				}

			if(routes[i].contains(plus)){
				//Separate the maps used by separating by the pluses
				connectedRoutes = routes[i].split("\\+");

				for(int h = 0; h <= connectedRoutes.length-1; h++){

					int second = h+1;
					if(second == connectedRoutes.length){
						second = h;
						last = true;
					}
					String curRoute = connectedRoutes[h];
					String nextRoute = connectedRoutes[second];
					BufferedReader linkFinder = new BufferedReader(new FileReader("mapsinfoTEMPNEW.txt"));
					String stations1 = "",stations2 = "";

					//Finding a list of stations for each map included in a route.

					if(last!=true){
						while((findline = linkFinder.readLine()) != null){
							curLineSplit = findline.split(" ");
							if(curLineSplit[0].equals(curRoute)){
								stations1 = curLineSplit[1] + " " + stations1;

							}else if (curLineSplit[0].equals(nextRoute)){
								stations2 = curLineSplit[1] + " " + stations2;

							}
						}
					} else {
						stations1 = last_Stations;
						stations2 = s2;
					}
					String[] route_Station_List1 = stations1.split(" ");
					stations2.split(" ");
					end="";

					/*
					 * If we are using the last map, finish the loop here
					 */

					for(int a = 0; a<route_Station_List1.length;a++){
						CharSequence c = route_Station_List1[a];
						if(stations2.contains(c)){
							end = route_Station_List1[a] + " " + end;
						}
					}

					String[] endStations = end.split(" ");
					//If the start station is not on this map (which will mean it has been visited) then set 
					//The Stations to start from as the links made from the last station.
					if(!stations1.contains(startSt)){
						route_Station_List1 = last_Stations.split(" ");

						for(int t = 0; t<route_Station_List1.length;t++){
							/*
							 * if the second map contains the destination, find all routes to it then break this loop
							 * else find the route between every 
							 */
							if(stations2.contains(end)){
								recursive_route(route_Station_List1[t], s2, route_Station_List1[t], 0, route_Station_List1[t],0,"",connectedRoutes[h]);
								last=true;
							}else{
								for(int w = 0;w<endStations.length;w++){
									recursive_route(route_Station_List1[t], endStations[w], route_Station_List1[t], 0, route_Station_List1[t],0,"",connectedRoutes[h]);
								}
							}
						}
						if(last==true){
							last_Stations = end;
							break;
						}
					}else{
						for(int z = 0; z<endStations.length;z++){
							recursive_route(s1, endStations[z], s1, 0, s1,0,"",connectedRoutes[h]);
						}
					}
					last_Stations = end;
				}
			}
		}

		/*
		 * 
		 * Validate the routes calculated above and create long lists of full
		 * routes rather than the route within maps we are currently storing.
		 * Other text manipualtion to ensure data is stored correctly
		 */
		BufferedReader calcShortest = new BufferedReader(new FileReader("Attempt.txt"));
		String readout;
		Set<String> routeList = new HashSet<String>();
		Set<String> startMap = new HashSet<String>();
		Set<String> finalRoutes = new HashSet<String>();
		Set<String> wrongRoutes = new HashSet<String>();
		CharSequence space = "  ";
		String[] neededMap;

		while((readout = calcShortest.readLine()) != null){	
			while(readout.contains(space)){
				readout = readout.replace(space, " ");
			}
			routeList.add(readout);
		}

		for(int j = 0;j<routes.length;j++){
			connectedRoutes = routes[j].split(" ");
			for(int h = 0; h < connectedRoutes.length;h++){
				neededMap = connectedRoutes[h].split("\\+");

				for(int z =0;z<neededMap.length;z++){

					for(String line : routeList){
						String[] map = line.split(" ");

						//Find all routes on the first map
						if(map[0].equals(neededMap[0])){
							startMap.add(line);				
							//Only work on lines that are the same as the current map we are using
						}else{
							if(map[0].equals(neededMap[z])){
								for(String smap : startMap){
									//Edit string to remove initial map and calculate distance and add it to the end

									String[] delete = smap.split(" ");
									String newline = "", secondline ="";

									for(int s = 1; s<delete.length-1;s++){
										newline = newline + " " + delete[s] + " ";
									}
									CharSequence hmm = map[1];

									for(int x = 2;x<map.length-1;x++){
										secondline = secondline + " " + map[x] + " ";
									}

									if(delete[delete.length-2].equals(hmm)){
										double dist = Double.parseDouble(delete[delete.length-1]);
										double ndist = Double.parseDouble(map[map.length-1]);

										dist = dist + ndist;
										map[map.length-1] = Double.toString(dist);
										dist = (double)Math.round(dist* 1000) / 1000;
										String rdistance = Double.toString(dist);

										//Concatenate the strings to create a new string with next part of route
										newline = newline + " " + secondline + " "  + rdistance;

										if(newline.contains(startSt) && newline.contains(endSt)){
											while(newline.contains(space)){
												newline = newline.replace(space, " ");
											}
											if(newline.startsWith(" ")){
												newline = newline.replaceFirst(" ", "");
											}
											finalRoutes.add(newline);
										}

									}
								}
							}
						}
					}
				}
			}
		}

		boolean unchaged=true;
		// Filtering out routes that double back on a station
		for(String output : finalRoutes){
			unchaged = true;

			String[] checkdups = output.split(" ");
			for(int a = 0;a<checkdups.length;a++){
				for(int b = 0;b<checkdups.length;b++){
					//Add route to remove list if a station is visited > once
					if(checkdups[a].equals(checkdups[b]) && a!=b){
						wrongRoutes.add(output);
						unchaged = false;
						break;
					}
				}
				//Escape clause for the loop after adding a route to the incorrect list
				if(unchaged==false){
					break;
				}
			}
		}
		finalRoutes.removeAll(wrongRoutes);

		for(String outp : wrongRoutes){
			System.out.println(outp);
		}
		return finalRoutes;
	}

	public Set<String> run(String start,String end, String stops) throws Exception{

		Set<String> output = new HashSet<String>();
		Set<String> toremove = new HashSet<String>();

		
		String r1 ="";
		String r2 ="";
		String[] places = stops.split(" ");		
		
		//Get routing points
		r1 = findRoutingPoints(start);
		r2 = findRoutingPoints(end);
		String same = compare(r1, r2);

		if(same.equals("no")){
			String[] start_R_Points = r1.split(" ");
			String[] end_R_Points = r2.split(" ");

			for(int st = 0; st < start_R_Points.length;st++){
				for(int en = 0; en<end_R_Points.length;en++){

					if(start_R_Points[st].equals(start) && end_R_Points[en].equals(end)){
						output = shortest_route(start, end);
					}else if(start_R_Points[st].equals(start) && !end_R_Points[en].equals(end)){
						output = shortest_route(start, end_R_Points[en]);
					}else if(!start_R_Points[st].equals(start) && end_R_Points[en].equals(end)){
						output = shortest_route(start_R_Points[st], end);
					}else{
						output = shortest_route(start_R_Points[st], end_R_Points[en]);
					}
				}
			}
		}else{
			//find routes between start point and routing point 
			output = shortest_route(start, same);

			//find routes between routing point and destination
			output = shortest_route(same, end);
		}

		for(String check_Points : output){
			for(int i = 0; i < places.length;i++){
				CharSequence current = places[i];
				if(!check_Points.contains(current)){
					toremove.add(check_Points);
				}
			}
		}		
		output.removeAll(toremove);
		
		File f = new File("Attempt.txt");
		if(f.exists()){
			f.delete();
		}else{
			System.out.println("File not found to delete");
		}		

		return output;
	}
}
