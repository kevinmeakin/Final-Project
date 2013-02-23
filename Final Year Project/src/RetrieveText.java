import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;



public class RetrieveText {

	static String Permitted_Route = "permitted_route_identifier";
	static String Routeing_Point = "routeing_point_identifier";
	static String maps = "Maps";

	/*
	 * Reads the PDF files and outputs the contents to a text file where
	 * I am able to manipulate and sort out the data.
	 */
	public static void readPDF(String filename){

		PDDocument pdfFile;
		BufferedWriter writer;
		try {
			File input = new File(filename+".pdf");  // The PDF file from where you would like to extract
			File output = new File(filename+ ".txt"); // The text file where you are going to store the extracted data
			pdfFile = PDDocument.load(input);


			PDFTextStripper text = new PDFTextStripper();
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output)));
			text.writeText(pdfFile, writer);
			if (pdfFile != null) {
				pdfFile.close();
			}
			writer.close();
		} catch (Exception e){
			e.printStackTrace();
		} 
	}

	/*
	 * Used to remove the separate line issue from permitted routes identifier
	 */
	public static void fixLines(String filename){

		String t = null;
		try {
			/*
			 * Opens inputted text files for reading and creates Final.txt for the output of the text
			 * manipulation
			 */
			BufferedReader in = new BufferedReader(new FileReader(filename));
			BufferedWriter writer = new BufferedWriter(new FileWriter("Final.txt"));
			Set<String> fixerHashTemp = new HashSet<String>(10000); 

			String str,lastline = null;

			while ((str = in.readLine()) != null){
				boolean overwrite = false;

				t = str.trim();

				String[] checkcaps = t.split(" ");

				/*
				 * If the first characters of the line are either 2 letters long or have a + as the 3rd character
				 * then remove the previous line (which is connected) and replace it with a line
				 * which is a concatenated version of the previous and current lines.
				 */
				if(t.startsWith("+", 2) == true || checkcaps[0].length() == 2){
					fixerHashTemp.remove(lastline);
					lastline = lastline + " " + t;

					fixerHashTemp.add(lastline);
					overwrite = true;
				}

				/*
				 * Add line to hashmap if the special criteria for the if above are not met
				 */
				if(overwrite == false){
					lastline = t;
					fixerHashTemp.add(t);
				}
			}
			in.close();

			/*
			 * Write output from hashmap to new file
			 */
			//TODO: Change all to one file once fully working.

			for (String unique : fixerHashTemp) {
				writer.write(unique);
				writer.newLine();
			}

			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Remove duplicate entries from the file to ensure there is no repeated data
	 */
	public static void stripDuplicatesFromFile(String filename){
		try{
			/*
			 * Open the file, read the text and add it to a hashmap
			 */
			BufferedReader reader = new BufferedReader(new FileReader(filename));
			Set<String> duplicateHashTemp = new HashSet<String>(10000); 
			String currentLine;

			while ((currentLine = reader.readLine()) != null) {
				currentLine = currentLine.toUpperCase();	//Make all word upper case to ensure no duplicates with dif cases
				duplicateHashTemp.add(currentLine);		//Add current line to the hashset
			}

			reader.close();


			/*
			 * Write the hashmap to a file, the hashmap automatically removes 
			 * repeating data
			 */
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename));

			for (String unique : duplicateHashTemp) {
				writer.write(unique);
				writer.newLine();
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Search text and remove lines based on criteria given, the main method in terms of 
	 * removing unneeded data
	 */
	public static void TidyText(String filename){

		File tempFile = new File(filename + "TEMP.txt");
		String t = null;

		/*
		 * List of charsequences which will be used to remove unwanted lines of data from
		 * the files
		 */
		CharSequence ind = "Index", sta = "Station", nums = "1 2 3 4", mar = "Mar",
				yr = "2011", route = "routeing", con = "continued", pag = "page",
				via = "via", loc = "local", eng = "England";

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename + ".txt"));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			String str;

			while ((str = in.readLine()) != null){

				t = str.trim();

				/*
				 * Checks if the current line contains any of the specified char sequences,
				 * if it does it is ignored when writing to file.
				 */
				if(t.contains(ind) || t.contains(sta) || t.contentEquals(nums) || t.contentEquals(mar)
						|| t.contains(yr) || t.contains(route) && filename.equals("Maps") 
						|| t.contains(con) || t.contains(pag) || t.contains(via) || t.length()==2
						|| t.contains(loc) || t.contains(eng)) continue;
				writer.write(str+"\n");

			}
			in.close();
			writer.close();

			/*
			 * Selects other methods to run based on which file is being edited
			 */
			if(filename.equals("Maps")){
				stripDuplicatesFromFile(filename+"TEMP.txt");
			}else if(filename.equals(Permitted_Route)){
				fixLines(filename+"TEMP.txt");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * Method to search the files and replace any full station names with their 3 letter
	 * abbreviations. This will make storage and searching the dataset much simpler in the
	 * future.
	 */
	public static void find3Letters(String filename){

		try{
			/*
			 * Open the file, read the text and add it to a hashmap
			 */
			BufferedReader filereader;
			if(filename.equals(Permitted_Route)){
				filereader = new BufferedReader(new FileReader("Final.txt"));
			}
			else{
				filereader = new BufferedReader(new FileReader(filename+"TEMP.txt"));

			}
			BufferedReader letterreader = new BufferedReader(new FileReader("3letters.txt"));

			Set<String> fileHash = new HashSet<String>(10000); 
			Set<String> letterhash = new HashSet<String>(10000); 


			String currentLine;

			while ((currentLine = letterreader.readLine()) != null) {
				currentLine = currentLine.toUpperCase();
				letterhash.add(currentLine);		//Add current line to the hashset
			}
			CharSequence group = "GROUP",cry = "CROYDON";
			while ((currentLine = filereader.readLine()) != null) {
				currentLine = currentLine.toUpperCase();
				String[] splitCurLine = currentLine.split(" ");

				for(String replace : letterhash){
					/*
					 * mainpulate replace variable to seperate the 3 letter
					 * abbreviation from the station name within the file
					 */
					String[] getID = replace.split("\t");
					String stationname = replace.substring(4);
					stationname = stationname.toUpperCase();
					CharSequence s = stationname;
					String[] check = stationname.split(" ");

					if(currentLine.contains(s)){	//If current line of the data contains the current station name...

						if(check.length > 1){	//Check length of the station name (bug fix for words within words e.g. ford)
							currentLine = currentLine.replace(s, getID[0]);
						}

						/*
						 * checking if the station is one word long that the station is the one 
						 * required and not contained within another station name e.g. redford
						 * contains ford which is a seperate station.
						 */
						for(int i =0;i<splitCurLine.length-1;i++){
							if(stationname.equals(splitCurLine[i])){
								currentLine = currentLine.replace(s, getID[0]);
							}
						}
					}

					/* 
					 * remove all occurences of the word group from the files (will implement a list system later
					 * that will select a group when a station within that group is chosen)
					 */
					if(currentLine.contains(group)){
						currentLine = currentLine.replace("GROUP", "").trim();
					}

				}

				fileHash.add(currentLine);		//Add current line to the hashset
			}

			filereader.close();
			letterreader.close();

			/*
			 * output results to file
			 */
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename+"TEMPNEW.txt"));


			for(String output : fileHash){
				writer.write(output);
				writer.newLine();
			}


			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args){
		System.out.println("***************START****************");
		readPDF(Permitted_Route);
		readPDF(Routeing_Point);
		readPDF(maps);

		TidyText(Permitted_Route);
		TidyText(Routeing_Point);
		TidyText(maps);
		//
		//		findStations(Permitted_Route);
		//		findStations(Routeing_Point);

		find3Letters(Routeing_Point);
		find3Letters(Permitted_Route);
		System.out.println("*****************DONE****************");
	}


}