//floatArray(line 139): 11 years max for 256*512 resolution. 2 years max for 512*1024 resolution.

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.util.Scanner;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;

public class ReGrow {
	
	public static int eastIndex (int currentIndex, int lonRange, int latRange) {
		currentIndex++;
		if (currentIndex % lonRange == 0) {
			return currentIndex - lonRange + 1 - 1;
		} else {
			return currentIndex + 1 - 1;
		}
	}

	public static int westIndex (int currentIndex, int lonRange, int latRange) {
		currentIndex++;
		if (currentIndex % lonRange == 1) {
			return currentIndex + lonRange - 1 - 1;
		} else {
			return currentIndex - 1 - 1;
		}
	}
	
	public static boolean northExists (int currentIndex, int lonRange, int latRange) {
		currentIndex++;
		if (currentIndex % (lonRange * latRange) > lonRange) {
			return true;
		} else {
			return false;
		}
	}
	
	public static int northIndex (int currentIndex, int lonRange, int latRange) {
		currentIndex++;
		return currentIndex - lonRange - 1;
	}

	public static boolean southExists (int currentIndex, int lonRange, int latRange) {
		currentIndex++;
		if (currentIndex % (lonRange * latRange) > lonRange * (latRange - 1)) {
			return false;
		} else {
			return true;
		}
	}

	public static int southIndex (int currentIndex, int lonRange, int latRange) {
		currentIndex++;
		return currentIndex + lonRange - 1;
	}

//## MAIN ##################################################################################################################################
	
	public static void main(String[] args) throws IOException {

		if (args.length < 3) {
			System.out.println("java ReGrow [Collection] [Cdl.Path] [Gph] [Stacks] - [track/thresh value]");
			System.exit(0);			
		}

		double thresh = 0.00003;
		boolean trackBool = false;
		boolean backNegative = false;
		
		if (args.length > 4) {
			if (Arrays.asList(Arrays.copyOfRange(args, 4, args.length)).contains("thresh")) {
				try {
					String s = args[Arrays.asList(Arrays.copyOfRange(args, 4, args.length)).indexOf("thresh") + 4];
					thresh = Double.parseDouble(s);
//					System.out.println("Given thresh = " + thresh);
				} catch (NumberFormatException ex) {}
			}

			if (Arrays.asList(Arrays.copyOfRange(args, 4, args.length)).contains("track")) {
				trackBool = true;
			}

			if (Arrays.asList(Arrays.copyOfRange(args, 4, args.length)).contains("negative")) {
//				backNegative = true;
			}
		}
		
		BufferedReader cdl = new BufferedReader(new FileReader(args[1]));
		String cdlLine;
//		BufferedReader kh = new BufferedReader(new FileReader(args[1]));
//		String khLine;
		Collection thisCollection = new Collection(args[0]);
		int thisGph = Integer.parseInt(args[2]);
		
//## LONGTRACKS LIST ######################################################################################################################		
		
//		int[] rootStackEx = new int[args[3].split(",").length];			//######################### ESTO SE VA!! ############################
		int rootIter = 0;
		ArrayList<LongTracks> trackPlotList = new ArrayList<LongTracks>();

		for (String stackStr: args[3].split(",")) {
//			rootStackEx[rootIter] = Integer.parseInt(stackStr);

			if (thisCollection.getStack(Integer.parseInt(stackStr)).contains(thisGph)) {
				trackPlotList.add(thisCollection.getStack(Integer.parseInt(stackStr)).getLt(thisGph));
				rootIter++;
			}
		}															//######################### ESTO SE VA!! ############################
		
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
		double[] floatArray = new double[0]; 						//11 years max for 256*512 resolution. 2 years max for 512*1024 resolution.

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
						floatParam = cdlLine.split(" ")[1].substring(0,2);
//						System.out.println(floatParam);
						hasFloat = true;
					}

					if (cdlLine.startsWith("data:")) {
						floatRange = lonRange * latRange * timeRange;
						floatArray = new double[floatRange];
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
	
							if (cdlLine.indexOf(floatParam + " =") > -1) {
								cdlLine = cdlLine.replaceAll(floatParam + " =", "");
								cdlLine = cdlLine.replaceAll(" ", "");
								for (int i = 0; i < floatRange; i++) {
									if (!(cdlLine.indexOf(",") > -1)) {
										cdlLine = cdl.readLine();
										cdlLine = cdlLine.replaceAll(" ", "");
										cdlLine = cdlLine.replaceAll(";", ",");
									}
									String strToDouble = cdlLine.substring(0, cdlLine.indexOf(","));
									cdlLine = cdlLine.substring(cdlLine.indexOf(",") + 1);
									floatArray[i] = Double.parseDouble(strToDouble);
								}
							}												//Array de data values (massive).
						}
					}
				}
			}
		}
		
//## LEGACY SET BUILDER ###############################################################################################################

		Set<Integer> legacySet = new HashSet<>();
		Set<Integer> trackSet = new HashSet<>();

		for(LongTracks thisLt: trackPlotList) {

			for (int dKey: thisLt.getKeySet()) {
				Dots currentDot = thisLt.getDot(dKey);
				
				int lonIndex;
				int latIndex;
				double lastGenMax = 1000;

				int i = lonRange - 1;
				while (currentDot.lon < lonArray[i]) {
//					System.out.println(currentDot.lon + " < " + lonArray[i]);
					i--;
				}

				lonIndex = i + 1;
				
				double latDist = 1000;
				int j = 0;
				while (Math.abs(currentDot.lat - latArray[j]) < latDist) {
					latDist = Math.abs(currentDot.lat - latArray[j]);
					j++;
				}
				latIndex = j - 1;
	
				currentDot.iD = (currentDot.frame - 1) * lonRange * latRange + latIndex * lonRange + lonIndex;
//				System.out.println("Dot; lon " + currentDot.lon + "; lat " + currentDot.lat + "; time " + currentDot.frame + "; iD " + currentDot.iD);
//				currentDot.shapeFormIndex.add(currentDot.iD);
				
				int iter = 0;
				while (currentDot.iD + iter * lonRange * latRange < lonRange * latRange * timeRange && trackBool) {
					trackSet.add(currentDot.iD + iter * lonRange * latRange);
					iter++;
				}
	
				Set<Integer> currentSet = new HashSet<>();
				currentSet.add(currentDot.iD);
				Set<Integer> newSet = new HashSet<>();
	
				while (!currentSet.isEmpty()) {
	
					double currentMax = 0;
					for (int pivot: currentSet) {
	
						double eastFloat = Math.abs(floatArray[eastIndex(pivot, lonRange, latRange)]);
						double westFloat = Math.abs(floatArray[westIndex(pivot, lonRange, latRange)]);
						double northFloat = Math.abs(floatArray[northIndex(pivot, lonRange, latRange)]);
						double southFloat = Math.abs(floatArray[southIndex(pivot, lonRange, latRange)]);
	
						if (eastFloat > thresh && eastFloat < lastGenMax && !legacySet.contains(eastIndex(pivot, lonRange, latRange))) {
							newSet.add(eastIndex(pivot, lonRange, latRange));
						}
		
						if (westFloat > thresh && westFloat < lastGenMax && !legacySet.contains(westIndex(pivot, lonRange, latRange))) {
							newSet.add(westIndex(pivot, lonRange, latRange));
						}
						
						if (northExists(pivot, lonRange, latRange)) {
							if (northFloat > thresh && northFloat < lastGenMax && !legacySet.contains(northIndex(pivot, lonRange, latRange))) {
								newSet.add(northIndex(pivot, lonRange, latRange));
						}
					}
		
						if (southExists(pivot, lonRange, latRange)) {
							if (southFloat > thresh && southFloat < lastGenMax &&  !legacySet.contains(southIndex(pivot, lonRange, latRange))) {
								newSet.add(southIndex(pivot, lonRange, latRange));
							}
						}
	
						currentMax = 
							Math.max(
								Math.max(
									Math.max(
										Math.max(
											eastFloat * ((eastFloat < lastGenMax)? 1:0), 
											westFloat * ((westFloat < lastGenMax)? 1:0)
										), 
										northFloat * ((northFloat < lastGenMax)? 1:0)
									), 
									southFloat * ((southFloat < lastGenMax)? 1:0)
								), 
								currentMax
							);
					}
					
					lastGenMax = currentMax;
//					System.out.println(lastGenMax);
					legacySet.addAll(currentSet);
					currentSet.clear();
					currentSet.addAll(newSet);
					newSet.clear();
				
				}
			}
		}
		
//## CDL WRITER #####################################################################################################	

		cdl = new BufferedReader(new FileReader(args[1]));
		String[] tokens = args[1].split("\\.(?=[^\\.]+$)");
		String nameFile = tokens[0] + "_" + args[3] + "." + tokens[1];
		FileWriter writer = new FileWriter(new File(nameFile));
		int blueIndex = 0;
		double defaultDouble = 0;

		
		double[] maxedFloat = new double[trackPlotList.size()];
		double[] minedFloat = new double[trackPlotList.size()];
		
		int sortedIter = 0;
//		System.out.println("trackPlotList.size() " + trackPlotList.size());
		for (LongTracks thisLt: trackPlotList) {

			double[] trackFloat = new double[thisLt.getKeySet().size()];
//			System.out.println("thisIter " + sortedIter + " dotList.size() " + thisTrack.dotList.size());
			int floatIndex = 0;
			for (int dKey: thisLt.getKeySet()) {
				Dots dotValue = thisLt.getDot(dKey);
				trackFloat[floatIndex] = floatArray[dotValue.iD];
				floatIndex++;
			}
		
			double[] sortedFloat = trackFloat;
			Arrays.sort(sortedFloat);
//			System.out.println("sortedIter " + sortedIter + " / " + sortedFloat.length);
			maxedFloat[sortedIter] = sortedFloat[sortedFloat.length - 1];
			minedFloat[sortedIter] = sortedFloat[0];
			sortedIter++;
		}

		Arrays.sort(maxedFloat);
		Arrays.sort(minedFloat);
		double trackGlobal;
		if (minedFloat[0] < 0) {
			trackGlobal = minedFloat[0];
		} else {
			trackGlobal = maxedFloat[maxedFloat.length - 1];
		}
//		double maxGlobal = sortedFloat[sortedFloat.length - 1];
//		double minGlobal = sortedFloat[0];
//		System.out.println("max = " + trackGlobal);
		

		while ((cdlLine = cdl.readLine()) != null) {
			if (cdlLine.startsWith(" " + floatParam + " =")) {
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
						if (!legacySet.contains(blueIndex)) {
							currentString = " " + defaultDouble;
						}
						if (trackSet.contains(blueIndex)) {
							currentString = " " + trackGlobal; //Max/Min Global <<<<<
						}
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
		System.out.println(nameFile + " has been generated.");
		cdl.close();
	}
}
