import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;
import java.lang.Math;

public class Modulator {

	public static void main(String[] args) throws IOException {
		
		if (args.length < 3) {
			System.out.println("java Modulator [WindU.cdl] [WindV.cdl] [Output.file]");
			System.exit(0);			
		}	
		
//## CDL PARSER ###########################################################################################################################		
		
		boolean hasLon = false;
		boolean hasLat = false;
		boolean hasTime = false;
		boolean hasFloat = false;
		int lonRange = 0;
		int latRange = 0;
		int timeRange = 0;
		int floatRange;
		String floatParam = "";
		double[] lonArray = new double[0];
		double[] latArray = new double[0];
		double[] floatArrayU = new double[0]; 						//11 years max for 256*512 resolution. 2 years max for 512*1024 resolution.
		double[] floatArrayV = new double[0]; 
		
		BufferedReader cdl = new BufferedReader(new FileReader(args[0]));
		String cdlLine;		
		
		while ((cdlLine = cdl.readLine()) != null) {

			if (cdlLine.startsWith("dimensions:")) {
				while ((cdlLine = cdl.readLine()) != null) {

					if (cdlLine.indexOf("lon =") > -1 && !hasLon) {
						Scanner in = new Scanner(cdlLine).useDelimiter("[^0-9]+");
						lonRange = in.nextInt();
//						System.out.println(lonRange);
						lonArray = new double[lonRange];
						hasLon = true;
						continue;
					}
		
					if (cdlLine.indexOf("lat =") > -1 && !hasLat) {
						Scanner in = new Scanner(cdlLine).useDelimiter("[^0-9]+");
						latRange = in.nextInt();
//						System.out.println(latRange);
						latArray = new double[latRange];
						hasLat = true;
						continue;
					}
		
					if (cdlLine.indexOf("time =") > -1 && !hasTime) {
						Scanner in = new Scanner(cdlLine).useDelimiter("[^0-9]+");
						timeRange = in.nextInt();
//						System.out.println(timeRange);
						hasTime = true;
						continue;
					}
		
					if (cdlLine.indexOf("float") > -1 && !hasFloat) {
						floatParam = cdlLine.split(" ")[1].split("\\(")[0];
//						System.out.println(floatParam);
						hasFloat = true;
					}

					if (cdlLine.startsWith("data:")) {
						floatRange = lonRange * latRange * timeRange;
						floatArrayU = new double[floatRange];
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
	
							if (cdlLine.indexOf(" " + floatParam + " =") > -1) {
								//System.out.println("CHK_1: [" + cdlLine + "]");
								cdlLine = cdlLine.replaceAll(floatParam + " =", "");
								cdlLine = cdlLine.replaceAll(" ", "");
								for (int i = 0; i < floatRange; i++) {
									if (!(cdlLine.indexOf(",") > -1)) {
										//System.out.println("\t" + cdlLine);
										cdlLine = cdl.readLine();
										cdlLine = cdlLine.replaceAll(" ", "");
										cdlLine = cdlLine.replaceAll(";", ",");
									}
									
									//if (cdlLine.indexOf(",") < 0) System.out.println("ERROR: [" + i + "/" + floatRange + "] = " + cdlLine);
									String strToDouble = cdlLine.substring(0, cdlLine.indexOf(","));
									cdlLine = cdlLine.substring(cdlLine.indexOf(",") + 1);
									floatArrayU[i] = Double.parseDouble(strToDouble);
								}
							}												//Array de data values (massive).
						}
					}
				}
			}
		}
		
		cdl = new BufferedReader(new FileReader(args[1]));
		cdlLine = "";
		hasFloat = false;
		
		while ((cdlLine = cdl.readLine()) != null) {

			if (cdlLine.startsWith("dimensions:")) {
				while ((cdlLine = cdl.readLine()) != null) {
		
					if (cdlLine.indexOf("float") > -1 && !hasFloat) {
						floatParam = cdlLine.split(" ")[1].split("\\(")[0];
//						System.out.println(floatParam);
						hasFloat = true;
					}

					if (cdlLine.startsWith("data:")) {
						floatRange = lonRange * latRange * timeRange;
						floatArrayV = new double[floatRange];
						while ((cdlLine = cdl.readLine()) != null) {
	
							if (cdlLine.indexOf(" " + floatParam + " =") > -1) {
								//System.out.println("CHK_2: [" + cdlLine + "]");
								cdlLine = cdlLine.replaceAll(floatParam + " =", "");
								cdlLine = cdlLine.replaceAll(" ", "");
								for (int i = 0; i < floatRange; i++) {
									if (!(cdlLine.indexOf(",") > -1)) {
										cdlLine = cdl.readLine();
										cdlLine = cdlLine.replaceAll(" ", "");
										cdlLine = cdlLine.replaceAll(";", ",");
									}
									
									//if (cdlLine.indexOf(",") < 0) System.out.println("ERROR: [" + i + "/" + floatRange + "] = " + cdlLine);
									String strToDouble = cdlLine.substring(0, cdlLine.indexOf(","));
									cdlLine = cdlLine.substring(cdlLine.indexOf(",") + 1);
									floatArrayV[i] = Double.parseDouble(strToDouble);
								}
							}												//Array de data values (massive).
						}
					}
				}
			}
		}
		
//## MODULATOR ###################################################################################################################		
		
		double[] newFloatArray = new double[lonRange * latRange * timeRange];
		
		for (int i = 0; i < lonRange * latRange * timeRange; i++) {
			newFloatArray[i] = Math.sqrt( Math.pow(floatArrayU[i], 2) + Math.pow(floatArrayV[i], 2));
			//if (newFloatArray[i] < 0) System.out.println("[" + i + "] - " + newFloatArray[i]);
		}

//## CDL WRITER ###################################################################################################################		
		
		cdl = new BufferedReader(new FileReader(args[1]));
		//String[] tokens = args[1].split("\\.(?=[^\\.]+$)");
		//String nameFile = tokens[0] + "_" + args[3] + "." + tokens[1];
		FileWriter writer = new FileWriter(new File(args[2]));
		int blueIndex = 0;
		double defaultDouble = 0;
		
		//System.out.println("floatParam = " + floatParam);
		while ((cdlLine = cdl.readLine()) != null) {
			if (cdlLine.startsWith(" " + floatParam + " =")) {
				//System.out.println(cdlLine);
				writer.write(cdlLine + "\n");
				while ((cdlLine = cdl.readLine()) != null) {
					if (cdlLine.startsWith("}"))
						break;
					cdlLine = cdlLine.replaceAll(" ", "");
					String finalString = "    ";
					String currentString = "";
					while (cdlLine.indexOf(",") > -1) {
						currentString = cdlLine.substring(0,cdlLine.indexOf(","));
						cdlLine = cdlLine.substring(cdlLine.indexOf(",") + 1);
						currentString = newFloatArray[blueIndex] + "";
						finalString = finalString + currentString + ",";
						blueIndex++;
					}
					writer.write(finalString + cdlLine + "\n");
				}
			} else {
				writer.write(cdlLine + "\n");
			}
		}
		writer.write("}\n");
		writer.flush();
		writer.close();
		System.out.println(args[2] + " has been generated.");
		cdl.close();
		
	}
}
