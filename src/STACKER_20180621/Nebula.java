
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;
//import java.util.ArrayList;
//import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class Nebula {

	public static double distance(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371000; //meters
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist =/* (double)*/ (earthRadius * c) / 1000; //Km
		return dist;
	}
	
	public static void main (String[] args) throws IOException {
		
		if (args.length < 5) {
			System.out.println("java ReGrow [Collection] [Cdl_Path] [Gph] [Grade_Thrsh] [File_Name] ... [biggerCollection]");
			System.exit(0);			
		}
		
		Collection thisCollection = new Collection(args[0]);
		double collectionCount = thisCollection.getKeySet().size();
		if (args.length > 5) {
			Collection biggerCollection = new Collection(args[5]);
			collectionCount = biggerCollection.getKeySet().size();
		}
		
		BufferedReader cdl = new BufferedReader(new FileReader(args[1]));
		String cdlLine;
		
		String[] gphStrArray = args[2].split(",");
		int[] gphArray = new int[gphStrArray.length];
		int aux = 0;
		for (String gphStr: gphStrArray) {
			gphArray[aux] = Integer.parseInt(gphStr);
			aux++;
		}
		
		
		double gradeThresh = Double.parseDouble(args[3]);
		
		String fileName = args[4] + ".cdl";
		
//########################################################################################################
		
		System.out.print("Parsing...");
		
		boolean hasLon = false;
		boolean hasLat = false;
		boolean hasFloat = false;
		int lonRange = 0;
		int latRange = 0;
		String floatParam = "";
		double[] lonArray = new double[0];
		double[] latArray = new double[0];

		while ((cdlLine = cdl.readLine()) != null) {

			if (cdlLine.startsWith("dimensions:")) {
				while ((cdlLine = cdl.readLine()) != null) {

					if (cdlLine.indexOf("lon =") > -1 && !hasLon) {
						Scanner in = new Scanner(cdlLine).useDelimiter("[^0-9]+");
						lonRange = in.nextInt();
						lonArray = new double[lonRange];
						hasLon = true;
						continue;
					}
		
					if (cdlLine.indexOf("lat =") > -1 && !hasLat) {
						Scanner in = new Scanner(cdlLine).useDelimiter("[^0-9]+");
						latRange = in.nextInt();
						latArray = new double[latRange];
						hasLat = true;
						continue;
					}
		
					if (cdlLine.indexOf("float") > -1 && !hasFloat) {
						floatParam = cdlLine.split(" ")[1].substring(0,2);
						hasFloat = true;
					}

					if (cdlLine.startsWith("data:")) {

						while ((cdlLine = cdl.readLine()) != null) {
							if (cdlLine.indexOf("lon =") > -1) {
								cdlLine = cdlLine.replaceAll("lon =", "");
								cdlLine = cdlLine.replaceAll(" ", "");
								for (int i = 0; i < lonRange; i++) {
									if (!(cdlLine.indexOf(",") > -1)) {
										cdlLine = cdl.readLine();
										cdlLine = cdlLine.replaceAll(" ", "");
										cdlLine = cdlLine.replaceAll(";", ",");
									}
									String strToDouble = cdlLine.substring(0, cdlLine.indexOf(","));
									cdlLine = cdlLine.substring(cdlLine.indexOf(",") + 1);
									lonArray[i] = Double.parseDouble(strToDouble);
								}
							}												//Array de lon values.
							
							if (cdlLine.indexOf("lat =") > -1) {
								cdlLine = cdlLine.replaceAll("lat =", "");
								cdlLine = cdlLine.replaceAll(" ", "");
								for (int i = 0; i < latRange; i++) {
									if (!(cdlLine.indexOf(",") > -1)) {
										cdlLine = cdl.readLine();
										cdlLine = cdlLine.replaceAll(" ", "");
										cdlLine = cdlLine.replaceAll(";", ",");
									}
									String strToDouble = cdlLine.substring(0, cdlLine.indexOf(","));
									cdlLine = cdlLine.substring(cdlLine.indexOf(",") + 1);
									latArray[i] = Double.parseDouble(strToDouble);
								}
							}												//Array de lat values.
	
						}
					}
				}
			}
		}
		
		System.out.println(" Complete!");
		
//########################################################################################################	
		
		System.out.print("Floating...");
		String message = "";
		
		double[] floatArray = new double[lonRange * latRange];
		int floatCount = 0;
		
		for (double lat: latArray) {
			/*if (lat > 0) {
				floatCount = floatCount + lonRange;
				continue;
			}*/
			for (int ch = 0; ch < message.length(); ch++){System.out.print("\b");}
			message = " " + lat + "/-90";
			System.out.print(message);
			for (double lon: lonArray) {				
				int eventCount = 0;
				
				double lonlon = lon + gradeThresh;
				if (lonlon > 360) {
					lonlon = lon - gradeThresh;
				}
				
				double kmThresh = distance(lat, lon, lat, lonlon);
								
				for (int sKey: thisCollection.getKeySet()) {					
					boolean eventOut = true;
					
					for (int ltKey: thisCollection.getStack(sKey).getKeySet()) {
						if (!eventOut) continue;
						boolean check = false;
						for (int pipi: gphArray) {if (pipi == ltKey) {check = true;}}
						if (check/*Arrays.asList(gphArray).contains(ltKey)*/) {
							
							for (int dKey: thisCollection.getStack(sKey).getLt(ltKey).getKeySet()) {
								if (!eventOut) continue;
								Dots thisDot = thisCollection.getStack(sKey).getLt(ltKey).getDot(dKey);
								//System.out.println(distance(lat, lon, thisDot.lat, thisDot.lon) + "(" + sKey + ") < " + kmThresh + "(" + lat + "," + lon + ")");
								
								if (eventOut && distance(lat, lon, thisDot.lat, thisDot.lon) < kmThresh) {
									eventCount++;
									eventOut = false;
								}
							}
						}
					}
				}
								
				floatArray[floatCount] = eventCount / collectionCount;
				floatCount++;
			}
		}
		
		for (int ch = 0; ch < message.length(); ch++){System.out.print("\b");}
		System.out.println(" Complete!");
		
//########################################################################################################
		
		System.out.print("Writing...");
		
		cdl = new BufferedReader(new FileReader(args[1]));
		FileWriter cdlWriter = new FileWriter(new File(fileName));
		int floatIter = 0;
		boolean breakOut = false;
		
		while ((cdlLine = cdl.readLine()) != null) {
			if (breakOut) break;
			if (cdlLine.startsWith("data:")) {
				cdlWriter.write(cdlLine + "\n");
				
				while ((cdlLine = cdl.readLine()) != null) {
					if (breakOut) break;
					if (cdlLine.startsWith(" time =")) {
						cdlWriter.write(" time = 0 ;\n");
						
						while (cdlLine.indexOf(";") < 0) {
							cdlLine = cdl.readLine();
						}
						
						while ((cdlLine = cdl.readLine()) != null) {
							if (breakOut) break;
							if (cdlLine.startsWith(" " + floatParam + " =")) {
								cdlWriter.write(cdlLine + "\n");
								
								while ((cdlLine = cdl.readLine()) != null) {
									if (breakOut) break;
									if (cdlLine.indexOf("}") > -1) {
										System.out.println("ERROR: cdlFile too short!");
										System.exit(0);
									}

									cdlLine = cdlLine.replaceAll(" ", "");
									String finalString = "    ";
									String currentString = "";

									while (cdlLine.indexOf(",") > -1) {
										if (breakOut) break;
										currentString = " " + floatArray[floatIter];
										cdlLine = cdlLine.substring(cdlLine.indexOf(",") + 1);
										finalString = finalString + currentString + ",";
										floatIter++;
										
										if (floatIter == lonRange * latRange - 1) {
											finalString = finalString + " " + floatArray[lonRange * latRange - 1] + " ;";
											cdlLine = "";
											breakOut = true;
										}
									}
									
									cdlWriter.write(finalString + cdlLine + "\n");
								}
								
								cdlWriter.write("}\n");
							} else {
								cdlWriter.write(cdlLine + "\n");
							}
						}
					} else {
						cdlWriter.write(cdlLine + "\n");
					}
				}
			} else {
				cdlWriter.write(cdlLine + "\n");
			}		
		}
		
		System.out.println(" Complete!");
		
//########################################################################################################
		
		cdlWriter.flush();
		cdlWriter.close();
		cdl.close();
		System.out.println(fileName + " has been generated.");
	}
}
