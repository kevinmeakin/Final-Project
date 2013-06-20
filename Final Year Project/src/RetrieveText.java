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
	/**
	 * File Name Declarations
	 */
	static String Permitted_Route = "permitted_route_identifier";
	static String Routeing_Point = "routeing_point_identifier";
	static String maps = "Maps";

	/**
	 * Reads the PDF files and outputs the contents to a text file where
	 * the data is manipulated and sorted.
	 * 
	 * @param filename
	 */
	public static void readPDF(String filename){

		try {
			//Create input and output for the PDF file reader
			File PDFfile = new File(filename+".pdf");
			File pdf_text_file = new File(filename+ ".txt");
			//Load PDF file
			PDDocument pdf = PDDocument.load(PDFfile);
			PDFTextStripper text = new PDFTextStripper();
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(pdf_text_file)));
			//Write pdf contents to file using buffered writer
			text.writeText(pdf, writer);
			if (pdf != null) {
				pdf.close();
			}
			writer.close();
		} catch (Exception e){
		} 
	}

	/**
	 * Used to remove the separate line issue from permitted routes identifier
	 * 
	 * @param filename
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
			for (String unique : fixerHashTemp) {
				writer.write(unique);
				writer.newLine();
			}

			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Remove duplicate entries from the file to ensure there is no repeated data
	 * 
	 * @param filename
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

	/**
	 * Search text and remove lines based on criteria given, the main method in terms of 
	 * removing unneeded data
	 * 
	 * @param filename
	 */
	public static void TidyText(String filename){

		File tempFile = new File(filename + "TEMP.txt");
		String t = null;

		/*
		 * List of charsequences which will be used to remove unwanted lines of data from
		 * the files
		 */
		CharSequence ind = "Index", sta = "Station", nums = "1 2 3 4", mar = "Mar",
				yr = "2011", route = "routeing", con = "continued", pag = "page", hea = "Heathrow",
				via = "via", loc = "local", eng = "England", mem = "Point Member", rou = "Routeing",
				end ="Group Routeing Point Member";

		try {
			BufferedReader in = new BufferedReader(new FileReader(filename + ".txt"));
			BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile));
			String str;

			while ((str = in.readLine()) != null){
				t = str.trim();
				if(t.contains(end) && filename.equals(Routeing_Point)){
					CharSequence space = "";
					String rep = t.replace(end, space);
					writer.write(rep);
					writer.newLine();
				}
				
				/*
				 * Checks if the current line contains any of the specified char sequences,
				 * if it does it is ignored when writing to file.
				 */
				if(t.contains(ind) || t.contains(sta) || t.contentEquals(nums) || t.contentEquals(mar)
						|| t.contains(yr) || t.contains(route) && filename.equals("Maps") 
						|| t.contains(con) || t.contains(pag) || t.contains(via) || t.length()==2
						|| t.contains(loc) || t.contains(eng) || t.contains(mem) ||t.contains(rou) 
						|| t.contains(hea)) continue;
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

	/**
	 * Searches the files and replaces any full station names with their 3 letter
	 * abbreviations.
	 * 
	 * @param filename
	 */
	public static void find3Letters(String filename){

		try{
			/*
			 * Open the file, read the text and add it to a hashmap
			 */
			BufferedReader filereader;
			if(filename.equals(Permitted_Route)){
				filereader = new BufferedReader(new FileReader("Final.txt"));
			}else if(filename.equals("mapsinfo")){
				filereader = new BufferedReader(new FileReader("mapsinfo.txt"));
			}
			else{
				filereader = new BufferedReader(new FileReader(filename+"TEMP.txt"));

			}
			BufferedReader letterreader = new BufferedReader(new FileReader("3letters.txt"));

			Set<String> fileHash = new HashSet<String>(); 
			Set<String> letterhash = new HashSet<String>(); 
			String currentLine;

			while ((currentLine = letterreader.readLine()) != null) {
				currentLine = currentLine.toUpperCase();
				letterhash.add(currentLine);		//Add current line to the hashset
			}
						
			CharSequence group = "GROUP", wood = "WOODGPK", p = "PNWCY", sq = "NTN FOR HYDE", pq = "HIGH ST GLC",
					att = "  ", ebr = "EBR TOWN", bom = "BOM RAKE", gmg = "GARTH (BGN)",
							MTB	="MAT BATH",NLW = "NTN LE WILL",BSP =	"BSY PARK",
							BTD = "BON ON DEARNE",BYI = "BRY ISLAND", CSB = "CSH BEECH",
							CFR = "CHANDLERS FOD",TUT = "TUTBURY & HTN",TWI= "TWCKENHAM",
							WED = "WED WEDGWOOD", DWW ="DWL WARREN",BHI="BHM INTL",
							LSY="LSYENHAM",LTN=	"LUT AIRPORT PW",LGJ="LBO JN",w="LONDON GP",
							hea = "HEATHROW";
			while ((currentLine = filereader.readLine()) != null) {
				currentLine = currentLine.toUpperCase();
				String[] splitCurLine = currentLine.split(" ");

				for(String getAbbrev : letterhash){
					/*
					 * manipulate getAbbrev variable to separate the 3 letter
					 * abbreviation from the station name within the file
					 */
					String[] getID = getAbbrev.split("\t");
					String stationname = getAbbrev.substring(4);
					stationname = stationname.toUpperCase();
					CharSequence s = stationname;
					CharSequence penge = "Penge West", PON = "PONDERS END SSD",
							RDW ="RDG WEST",SRH ="STE HILL",WRX ="WRX GENERAL",
							ECR ="EAST ECR",WXC ="WRX CENTRAL",PMS="PMH & S",BMO="BHM M ST"
							,SOC="SOE CENTRAL",LSP="LIV SOUTH PARKWAY",SGB="SMR GAL BG";
					String[] check = stationname.split(" ");

					/*
					 * Fix for bug with Penge west
					 */
					if(currentLine.contains(SGB)){
						currentLine = currentLine.replace(SGB, "SGB");
					}
					if(currentLine.contains(LSP)){
						currentLine = currentLine.replace(LSP, "LSP");
					}
					if(currentLine.contains(SOC)){
						currentLine = currentLine.replace(SOC, "SOC");
					}
					if(currentLine.contains(BMO)){
						currentLine = currentLine.replace(BMO, "BMO");
					}
					if(currentLine.contains(PMS)){
						currentLine = currentLine.replace(PMS, "PMS");
					}
					if(currentLine.contains(WXC)){
						currentLine = currentLine.replace(WXC, "WXC");
					}
					if(currentLine.contains(penge)){
						currentLine = currentLine.replace(penge, "Penge W");
					}
					if(currentLine.contains(PON)){
						currentLine = currentLine.replace(PON, "PON");
					}
					if(currentLine.contains(RDW)){
						currentLine = currentLine.replace(RDW, "RDW");
					}
					if(currentLine.contains(SRH)){
						currentLine = currentLine.replace(SRH, "SRH");
					}
					if(currentLine.contains(WRX)){
						currentLine = currentLine.replace(WRX, "WRX");
					}
					if(currentLine.contains(ECR)){
						currentLine = currentLine.replace(ECR, "ECR");
					}
					
					if(currentLine.contains(att)){
						currentLine = currentLine.replace(att, " ");
					}
					
					if(currentLine.contains(s)){	//If current line of the data contains the current station name...

						if(check.length > 1){	//Check length of the station name (bug fix for words within words e.g. ford)
							currentLine = currentLine.replace(s, getID[0]);
						}else{

							/*
							 * checking if the station is one word long that the station is the one 
							 * required and not contained within another station name e.g. redford
							 * contains ford which is a separate station.
							 */

							for(int i =0;i<splitCurLine.length;i++){
								if(stationname.equals(splitCurLine[i])){
									currentLine = currentLine.replace(s, getID[0]);
								}
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
					if(currentLine.contains(wood)){
						currentLine = currentLine.replace(wood, "WGR").trim();
					}
					if(currentLine.contains(p)){
						currentLine =currentLine.replace(p, "PNW");
					}
					if(currentLine.contains(sq)){
						currentLine = currentLine.replace(sq, "NWN");
					}
					if(currentLine.contains(pq)){
						currentLine = currentLine.replace(pq, "HST");
					}
					if(currentLine.contains(ebr)){
						currentLine = currentLine.replace(ebr, "EBT");
					}
					if(currentLine.contains(bom)){
						currentLine = currentLine.replace(bom, "BMR");
					}
					if(currentLine.contains(gmg)){
						currentLine = currentLine.replace(gmg, "GMG");
					}
					if(currentLine.contains(MTB)){
						currentLine = currentLine.replace(MTB, "MTB");
					}
					if(currentLine.contains(NLW)){
						currentLine = currentLine.replace(NLW, "NLW");
					}
					if(currentLine.contains(BSP)){
						currentLine = currentLine.replace(BSP, "BSP");
					}
					if(currentLine.contains(BTD)){
						currentLine = currentLine.replace(BTD, "BTD");
					}				
					if(currentLine.contains(BYI)){
						currentLine = currentLine.replace(BYI, "BYI");
					}
					if(currentLine.contains(CSB)){
						currentLine = currentLine.replace(CSB, "CSB");
					}
					if(currentLine.contains(CFR)){
						currentLine = currentLine.replace(CFR, "CFR");
					}
					if(currentLine.contains(TUT)){
						currentLine = currentLine.replace(TUT, "TUT");
					}
					if(currentLine.contains(TWI)){
						currentLine = currentLine.replace(TWI, "TWI");
					}
					if(currentLine.contains(WED)){
						currentLine = currentLine.replace(WED, "WED");
					}
					if(currentLine.contains(DWW)){
						currentLine = currentLine.replace(DWW, "DWW");
					}
					if(currentLine.contains(BHI)){
						currentLine = currentLine.replace(BHI, "BHI");
					}
					if(currentLine.contains(LSY)){
						currentLine = currentLine.replace(LSY, "LSY");
					}
					if(currentLine.contains(LTN)){
						currentLine = currentLine.replace(LTN, "LTN");
					}
					if(currentLine.contains(LGJ)){
						currentLine = currentLine.replace(LGJ, "LGJ");
					}
					if(currentLine.contains(w)){
						currentLine = currentLine.replace(w, "LONDON");
					}
					if(currentLine.contains(w)){
						currentLine = currentLine.replace(w, "LONDON");
					}
					if(currentLine.contains(hea)){
						currentLine = currentLine.replace(hea, "LONDON");
					}
				}

				fileHash.add(currentLine);		//Add current line to the hashset
			}

			filereader.close();
			letterreader.close();
			CharSequence und = "UND";
			/*
			 * output results to file
			 */			
			BufferedWriter writer = new BufferedWriter(new FileWriter(filename+"TEMPNEW.txt"));

			for(String output : fileHash){
				String[] nn = output.split(" ");
				for(int i =0;i<nn.length;i++){
					if((nn[i].length()>3 || nn[i].length()<3) && filename.equals(Routeing_Point) && !nn[i].equals("LONDON")){
						System.out.println(nn[i]);
					}
					
					if(output.contains(und)){
						output.replace(und, "");
					}
				}
				writer.write(output);
				writer.newLine();
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args){

		readPDF(Permitted_Route);
		readPDF(Routeing_Point);
		readPDF(maps);

		TidyText(Permitted_Route);
		TidyText(Routeing_Point);
		TidyText(maps);

		find3Letters(Routeing_Point);
		find3Letters(Permitted_Route);
//		find3Letters("mapsinfo");

	}


}
