import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Collections;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.lang.Math;


public class Collection {
	Map<Integer, Stacks> stacksMap = new TreeMap<Integer, Stacks>();
	String startStr = "";
	
//## CONSTRUCTORS ######################################################
	
	public Collection () {
		
	}
	
	public Collection(String thisDir) throws IOException {
		this.stacksMap = fileToStacks(thisDir);
	}
	
	public Collection(String thisDir, int thisGph, String thisStartDate) throws IOException {
		this.stacksMap = tracksToStacks(thisDir, thisGph, thisStartDate);
	}
	
	public Collection(String thisDir, int thisGph, String thisStartDate, int days) throws IOException {
		this.stacksMap = tracksToStacks(thisDir, thisGph, thisStartDate, days);
	}

	public Collection(Collection lowerCollection, Collection higherCollection, Net thisNet, String lpsolPath) throws IOException, ParseException {
		this.stacksMap = lpsolToStacks(lowerCollection, higherCollection, thisNet, lpsolPath);
	}	
	
//## METHODS ###########################################################

	public Set<Integer> getKeySet() {
		return this.stacksMap.keySet();
	}
	
	public Stacks getStack(int key) {
		return this.stacksMap.get(key);
	}

	public int mapSize() {
		return this.stacksMap.keySet().size();
	}

	public boolean contains(int key) {
		return this.stacksMap.containsKey(key);
	}

	public Set<Integer> getFrameSet() {
		Set<Integer> frameSetAdded = new TreeSet<Integer>();
		for (int stackKey: this.getKeySet()) {
			for (int ltKey: this.getStack(stackKey).getKeySet()) {
				for (int dotKey: this.getStack(stackKey).getLt(ltKey).getKeySet()) {
					frameSetAdded.add(dotKey);
				}
			}
		}
		return frameSetAdded;
	}

	public Set<Integer> getGphSet() {
		Set<Integer> gphSetAdded = new TreeSet<Integer>();
		for (int stackKey: this.getKeySet()) {
			for (int ltKey: this.getStack(stackKey).getKeySet()) {
				gphSetAdded.add(ltKey);
			}
		}
		return gphSetAdded;
	}

	public int closestGph(int thisGph) {
		int closestGph = 10000;
		for (int key: this.getGphSet()) {
			if (Math.abs(thisGph - key) < Math.abs(closestGph-thisGph)) {
				closestGph = key;
			}
		}
		return closestGph;
	}

	public String startStr() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date earliest = new Date();
//		String startStr = "";
		for (int sKey: this.getKeySet()) {
			for (int ltKey: this.getStack(sKey).getKeySet()) {
				for (int dKey: this.getStack(sKey).getLt(ltKey).getKeySet()) {
					if (this.getStack(sKey).getLt(ltKey).getDot(dKey).frame == 1) {
						earliest = this.getStack(sKey).getLt(ltKey).getDot(dKey).getDate();
					}
				}
			}
		}
		
		return sdf.format(earliest);
	}
	
	public void setStartStr() throws ParseException {
		this.startStr = this.startStr();
	}	
	
	public void setStartStr(String startStrCon) {
		this.startStr = startStrCon;
	}
	
	public int dotCounter() {
		int dotCounter = 0;
		for (int sKey: this.getKeySet()) {
			for (int ltKey: this.getStack(sKey).getKeySet()) {
				for (int dKey: this.getStack(sKey).getLt(ltKey).getKeySet()) {
					dotCounter++;
				}
			}
		}
		return dotCounter;
	}
	
	public int dotCounter(int thisGph) {
		int dotCounter = 0;
		for (int sKey: this.getKeySet()) {
			if (this.getStack(sKey).getKeySet().contains(thisGph)) {
				for (int dKey: this.getStack(sKey).getLt(thisGph).getKeySet()) {
					dotCounter++;
				}
			}
		}
		return dotCounter;
	}
	
	public int trackCounter() {
		Set<String> trackSet = new TreeSet<String> ();
		for (int sKey: this.getKeySet()) {
			for (int ltKey: this.getStack(sKey).getKeySet()) {
				for (int dKey: this.getStack(sKey).getLt(ltKey).getKeySet()) {
					trackSet.add(ltKey + "-" + this.getStack(sKey).getLt(ltKey).getDot(dKey).track);
				}
			}
		}
		return trackSet.size();
	}
	
	public int trackCounter(int thisGph) {
		Set<String> trackSet = new TreeSet<String> ();
		for (int sKey: this.getKeySet()) {
			if (this.getStack(sKey).getKeySet().contains(thisGph)) {
				for (int dKey: this.getStack(sKey).getLt(thisGph).getKeySet()) {
					trackSet.add(thisGph + "-" + this.getStack(sKey).getLt(thisGph).getDot(dKey).track);
				}
			}
		}
		return trackSet.size();
	}
	
	public Date firstDate () throws ParseException {
		Date firstDate = new Date();
		//System.out.println("pre " + firstDate);
		for (int sKey: this.stacksMap.keySet()) {
			for (int ltKey: this.getStack(sKey).getKeySet()) {
				for (int dKey: this.getStack(sKey).getLt(ltKey).getKeySet()) {
					if (this.getStack(sKey).getLt(ltKey).getDot(dKey).getDate().before(firstDate)) {
						firstDate = this.getStack(sKey).getLt(ltKey).getDot(dKey).getDate();
					}
				}
			}
		}
		//System.out.println("pos " + firstDate);
		return firstDate;
	}
	
	public Date lastDate () throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date lastDate = sdf.parse("01/01/1800");
		//System.out.println("pre " + lastDate);
		//String checky = "";
		for (int sKey: this.stacksMap.keySet()) {
			for (int ltKey: this.getStack(sKey).getKeySet()) {
				for (int dKey: this.getStack(sKey).getLt(ltKey).getKeySet()) {
					if (this.getStack(sKey).getLt(ltKey).getDot(dKey).getDate().after(lastDate)) {
						lastDate = this.getStack(sKey).getLt(ltKey).getDot(dKey).getDate();
						//checky = sKey + "-" + ltKey + "-" + dKey;
					}
				}
			}
		}
		//System.out.println(checky + " " + lastDate);
		return lastDate;
	}

//## FILE TO STACKS #########################################################################

	public static Map<Integer, Stacks> fileToStacks (String thisDir) throws IOException {
		
		Map<Integer, Stacks> thisStacksMap = new TreeMap<Integer, Stacks>();
		BufferedReader reader = new BufferedReader(new FileReader(thisDir));
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {

			if (currentLine.replaceAll(" ", "").startsWith("STACK_")) {
				if (Integer.parseInt(currentLine.split("_")[3]) == 0) {
					currentLine = reader.readLine();
					continue;
				}
				int thisStackKey = Integer.parseInt(currentLine.split("_")[1]);
				int thisStackMapSize = Integer.parseInt(currentLine.split("_")[3]);

				currentLine = reader.readLine();
				int thisStackiD = Integer.parseInt(currentLine.replaceAll(" ", "").split("_")[1]);
				String thisStackstartStr = currentLine.replaceAll(" ","").split("_")[2];
				if (thisStackstartStr == "null")
					thisStackstartStr = null;
				Stacks thisStack = new Stacks(thisStackiD, thisStackstartStr);

				for (int gphIter = 0; gphIter < thisStackMapSize; gphIter++) {
					currentLine = reader.readLine();
					if (Integer.parseInt(currentLine.split("_")[3]) == 0) {
						currentLine = reader.readLine();
						continue;
					}
					int thisLtKey = Integer.parseInt(currentLine.split("_")[1]);
					int thisLtMapSize = Integer.parseInt(currentLine.split("_")[3]);
					currentLine = reader.readLine();
					int thisLtiD = Integer.parseInt(currentLine.split("_")[1]);
					int thisLtgph = Integer.parseInt(currentLine.split("_")[2]);
					String thisLtstartStr = currentLine.split("_")[3];
					if (thisLtstartStr == "null")
						thisLtstartStr = null;

					LongTracks thisLt = new LongTracks(thisLtgph, thisLtiD, thisLtstartStr, thisLtiD);
						
					for (int frameIter = 0; frameIter < thisLtMapSize; frameIter++) {
						currentLine = reader.readLine();
						int thisDotKey = Integer.parseInt(currentLine.split("_")[1]);

						currentLine = reader.readLine();
						int iD = Integer.parseInt(currentLine.split("_")[1]);
						int track = Integer.parseInt(currentLine.split("_")[2]);
						int gph = Integer.parseInt(currentLine.split("_")[3]);
						int frame = Integer.parseInt(currentLine.split("_")[4]);

						currentLine = reader.readLine();
						double lat = Double.parseDouble(currentLine.split("_")[1]);
						double lon = Double.parseDouble(currentLine.split("_")[2]);
						double module = Double.parseDouble(currentLine.split("_")[3]);

						currentLine = reader.readLine();
						String startDate = currentLine.split("_")[1];
						if (startDate == "null")
							startDate = null;
						boolean isGhost = Boolean.parseBoolean(currentLine.split("_")[2]);
						int currentStack = Integer.parseInt(currentLine.split("_")[3]);
						
						currentLine = reader.readLine();
						String origin = currentLine.split("_")[1];

						thisLt.addDot(thisDotKey, new Dots(iD, gph, track, frame, lat, lon, module, startDate, isGhost, currentStack, origin));
					}
					thisStack.addLt(thisLtKey, thisLt);
				}
				thisStacksMap.put(thisStackKey, thisStack);
			}
		}
		return thisStacksMap;
	}

//## TRACKS TO STACKS #######################################################################

	public static Map<Integer, Stacks> tracksToStacks(String thisDir, int thisGph, String thisStartDate) throws IOException {

		Map<Integer, Stacks> thisStacksMap = new TreeMap<Integer, Stacks>();
		BufferedReader reader = new BufferedReader(new FileReader(thisDir));
		String currentLine;
		int stackCounter = 0;
		int ltCounter = 0;

		while ((currentLine = reader.readLine()) != null) {

			if (currentLine.startsWith("TRACK_ID")) {
				stackCounter++;	
				ltCounter++;
				LongTracks currentLT = new LongTracks(thisGph, ltCounter, thisStartDate, stackCounter);
				Stacks currentStack = new Stacks(stackCounter, thisStartDate);

				int currentTrack = Integer.parseInt(currentLine.split("  ")[1]);

				currentLine = reader.readLine();
				int currentDotCount = Integer.parseInt(currentLine.split("  ")[1]);
				for (int i = 0; i < currentDotCount; i++) {
					
					currentLine = reader.readLine();
					int frame = Integer.parseInt(currentLine.split(" ")[0]);
					double lon = Double.parseDouble(currentLine.split(" ")[1]);
					double lat = Double.parseDouble(currentLine.split(" ")[2]);
					double mod = Double.parseDouble(currentLine.split(" ")[3]);
					
					Dots newDot = new Dots(0 ,thisGph, currentTrack, frame, lat, lon, mod, thisStartDate, false, stackCounter);
					newDot.setOrigin(thisStartDate, thisGph, currentTrack, frame);

					currentLT.addDot(frame, newDot);
				}
				currentStack.addLt(thisGph, currentLT);
				thisStacksMap.put(stackCounter, currentStack);				
			}
		}
		return thisStacksMap;
	}

	public static Map<Integer, Stacks> tracksToStacks(String thisDir, int thisGph, String thisStartDate, int days) throws IOException {

		Map<Integer, Stacks> thisStacksMap = new TreeMap<Integer, Stacks>();
		BufferedReader reader = new BufferedReader(new FileReader(thisDir));
		String currentLine;
		int stackCounter = 0;
		int ltCounter = 0;

		while ((currentLine = reader.readLine()) != null) {

			if (currentLine.startsWith("TRACK_ID")) {

				int currentTrack = Integer.parseInt(currentLine.split("  ")[1]);
				currentLine = reader.readLine();
				int currentDotCount = Integer.parseInt(currentLine.split("  ")[1]);

				if ((days * 4 - 1) < currentDotCount) {
				
					stackCounter++;	
					ltCounter++;
					LongTracks currentLT = new LongTracks(thisGph, ltCounter, thisStartDate, stackCounter);
					Stacks currentStack = new Stacks(stackCounter, thisStartDate);

					for (int i = 0; i < currentDotCount; i++) {
				
						currentLine = reader.readLine();
						int frame = Integer.parseInt(currentLine.split(" ")[0]);
						double lon = Double.parseDouble(currentLine.split(" ")[1]);
						double lat = Double.parseDouble(currentLine.split(" ")[2]);
						double mod = Double.parseDouble(currentLine.split(" ")[3]);

						currentLT.addDot(frame, new Dots(0 ,thisGph, currentTrack, frame, lat, lon, mod, thisStartDate, false, stackCounter));
					}

					currentStack.addLt(thisGph, currentLT);
					thisStacksMap.put(stackCounter, currentStack);
				}
			}
		}
		return thisStacksMap;
	}	

//## LPSOL TO STACKS ########################################################################

	public static Map<Integer, Stacks> lpsolToStacks(Collection lowerCollection, Collection higherCollection, Net thisNet, String lpsolPath) throws IOException, ParseException {

		Map<Integer, Stacks> lpsolStacks = new TreeMap<Integer, Stacks>();

		Map<Integer, Boolean> lpsolMap = new TreeMap<Integer, Boolean>();
		BufferedReader lpsolReader = new BufferedReader(new FileReader(lpsolPath));
		String lpsolLine;

		Set<String> stringLinkSet = new HashSet<String>();

		while ((lpsolLine = lpsolReader.readLine()) != null) {
			if (lpsolLine.split(",")[0].replaceAll("\"", "").length() < 1) {
				continue;

			}

			int key = Integer.parseInt(lpsolLine.split(",")[0].replaceAll("\"", ""));
			boolean linkIsActive = false;
			if (Integer.parseInt(lpsolLine.split(",")[1]) == 1) {
				linkIsActive = true;
			}

			lpsolMap.put(key, linkIsActive);

			if (linkIsActive && thisNet.getLink(key).type != 0) {
				Dots lowerDot = thisNet.getLink(key).lowDot;
				Dots higherDot = thisNet.getLink(key).highDot;
				String stackString = lowerDot.gph + "/" + lowerDot.currentStack() + "-" + higherDot.gph + "/" + higherDot.currentStack();
				stringLinkSet.add(stackString);
			}
		}
		
		Map<Integer, Set<String>> sTracks = new HashMap<Integer, Set<String>>();
		int sTrackCounter = 0;
		
/*		for (String stringLink: stringLinkSet) {
			boolean needNewSet = true;
			Set<Integer> belongSet = new TreeSet<Integer> ();
			for (int key: sTracks.keySet()) {
				Set<String> currentSet = sTracks.get(key);
				if (currentSet.contains(stringLink.split("-")[0]) || currentSet.contains(stringLink.split("-")[1])) {
					belongSet.add(key);
					currentSet.add(stringLink.split("-")[0]);
					currentSet.add(stringLink.split("-")[1]);
					sTracks.put(key, currentSet);
					needNewSet = false;
			}
		}*/

		for (String stringLink: stringLinkSet) {
			//System.out.println(stringLink);
			boolean needNewSet = true;
			for (int key: sTracks.keySet()) {
				Set<String> currentSet = sTracks.get(key);
				if (currentSet.contains(stringLink.split("-")[0]) || currentSet.contains(stringLink.split("-")[1])) {
					currentSet.add(stringLink.split("-")[0]);
					currentSet.add(stringLink.split("-")[1]);
					sTracks.put(key, currentSet);
					needNewSet = false;
				}
			}
			
			if (needNewSet) {
				Set<String> neededSet = new HashSet<String>();
				neededSet.add(stringLink.split("-")[0]);
				neededSet.add(stringLink.split("-")[1]);
				sTrackCounter++;
				sTracks.put(sTrackCounter, neededSet);
			}
		}
		

/*		boolean stillMergeable = true;
		int iteration = 0;
		while (stillMergeable) {
			System.out.println(++iteration);
			Map<Integer, Set<String>> newGensTracks = new HashMap<Integer, Set<String>>();
			Set<Integer> newRemoveSet = new TreeSet<Integer> ();
			for (int keysTrack: sTracks.keySet()) {
				newGensTracks.put(keysTrack, sTracks.get(keysTrack));
			}
			int mergeCount = 0;
			for (int aKey: sTracks.keySet()) {
				for (int bKey: sTracks.keySet()) {
					if (aKey == bKey) continue;
					System.out.println("\t" + aKey + " - " + bKey);
					
					Set<String> aSet = sTracks.get(aKey);
					Set<String> bSet = sTracks.get(bKey);
					boolean needNewSet = false;
					
					for (String aString: aSet) {
						if (bSet.contains(aString)) {
							needNewSet = true;
						}
					}
					
					if (needNewSet) {
						mergeCount++;
						Set<String> newSet = new TreeSet<String> ();
						System.out.print("\t\t");
						for (String element: aSet) {
							System.out.print(element + ", ");
							newSet.add(element);
						}
						for (String element: bSet) {
							newSet.add(element);
							System.out.print(element + ", ");
						}
						sTrackCounter++;
						System.out.println("\n\t\tMerges to new Set " + sTrackCounter);
						newGensTracks.put(sTrackCounter, newSet);
						newRemoveSet.add(aKey);
						newRemoveSet.add(bKey);
					}
				}
			}
			
//			for (int key: sTracks.keySet()) {
//				sTracks.remove(key);
//			}
			for (int key: newGensTracks.keySet()) {
				sTracks.put(key, newGensTracks.get(key));
			}
			for (int key: newRemoveSet) {
				sTracks.remove(key);
			}
			
			if (mergeCount == 0) {
				stillMergeable = false;
			}
		}*/
							
			
			

		int higherGph = higherCollection.closestGph(0);
		int lowerGph = lowerCollection.closestGph(higherGph);

		for (int stackKey: lowerCollection.getKeySet()) {
			String checkStr = lowerGph + "/" + stackKey;
//			LongTracks currentLt = lowerCollection.getStack(stackKey).getLt(lowerGph);
			boolean needNewSet = true;
			for (int sKey: sTracks.keySet()) {
				Set<String> currentSet = sTracks.get(sKey);
				if (currentSet.contains(checkStr)) {
					needNewSet = false;
				}
			}
				
			if (needNewSet) {
				Set<String> neededSet = new HashSet<String>();
				neededSet.add(checkStr);
				sTrackCounter++;
				sTracks.put(sTrackCounter, neededSet);
			}
		}

		for (int stackKey: higherCollection.getKeySet()) {
			String checkStr = higherGph + "/" + stackKey;
//			LongTracks currentLt = higherCollection.getStack(stackKey).getLt(higherGph);
			boolean needNewSet = true;
			for (int sKey: sTracks.keySet()) {
				Set<String> currentSet = sTracks.get(sKey);
				if (currentSet.contains(checkStr)) {
					needNewSet = false;
				}
			}
				
			if (needNewSet) {
				Set<String> neededSet = new HashSet<String>();
				neededSet.add(checkStr);
				sTrackCounter++;
				sTracks.put(sTrackCounter, neededSet);
			}
		}
		
//		for (int key: sTracks.keySet()) {
//			System.out.print(key + ": ");
//			for (String string: sTracks.get(key)) {
//				System.out.print(string + ", ");
//			}
//			System.out.println("");
//		}
		
		boolean mergeable = true;
		int iter = 0;
		while (mergeable) {
			//System.out.println("Iter " + ++iter);
			mergeable = false;
			int aMerge = 0;
			int bMerge = 0;
			
			for (int aKey: sTracks.keySet()) {
				for (int bKey: sTracks.keySet()) {
					if (aKey == bKey) continue;
					for (String aString: sTracks.get(aKey)) {					
						if (sTracks.get(bKey).contains(aString)) {
//							System.out.println("\ta " + aKey + " - b " + bKey);
							mergeable = true;
							aMerge = aKey;
							bMerge = bKey;
							break;
						}
					}
					if (mergeable) break;
				}
				if (mergeable) break;				
			}
			
			if (mergeable) {
				Set<String> newStackSet = new TreeSet<String> ();
				sTrackCounter++;
//				System.out.print("\tStack " + sTrackCounter + ":\n\ta ");
				
				for (String element: sTracks.get(aMerge)) {
					newStackSet.add(element);
//					System.out.print(element + ", ");
				}
//				System.out.print("\n\tb ");
				for (String element: sTracks.get(bMerge)) {
					newStackSet.add(element);
//					System.out.print(element + ", ");
				}
//				System.out.println("");
				sTracks.remove(aMerge);
				sTracks.remove(bMerge);
				sTracks.put(sTrackCounter, newStackSet);				
			}		
		}
		

		for (int sKey: sTracks.keySet()) {
			//System.out.print("\nStack " + sKey + "\n\t");
//			System.out.println("");
//			for (String string: sTracks.get(sKey)) {
//				System.out.print(string + " ,");
//			}
			Stacks newStack = new Stacks(sKey, lowerCollection.startStr());
			for (String sTring: sTracks.get(sKey)) {
//				System.out.print(sTring + ", ");
				int thisGph = Integer.parseInt(sTring.split("/")[0]);
				int thisStack = Integer.parseInt(sTring.split("/")[1]);
				Collection sourceCollection = new Collection();

				if (lowerCollection.getGphSet().contains(thisGph)) {
					sourceCollection = lowerCollection;
				} else if (higherCollection.getGphSet().contains(thisGph)) {
					sourceCollection = higherCollection;
				} else {
					System.out.println("error: Missing sourceCollection for newStack[" + sKey + "] = " + sTracks.get(sKey));
					continue;
				}

				for (int ltKey: sourceCollection.getStack(thisStack).getKeySet()) {
					if (!newStack.contains(ltKey)) {
						LongTracks newLt = new LongTracks(ltKey, sKey, sourceCollection.startStr(), sKey);
//						for (int dKey: newLt.getKeySet()) {
//							newLt.getDot(dKey).setCurrentStack(sKey); 
//						}
						newStack.addLt(ltKey, newLt);
					}

					for (int dotKey : sourceCollection.getStack(thisStack).getLt(ltKey).getKeySet()) {
						Dots thisDot = sourceCollection.getStack(thisStack).getLt(ltKey).getDot(dotKey);
						thisDot.setCurrentStack(sKey);
						newStack.addDot(ltKey, dotKey, thisDot);
					}
				}
			}
			lpsolStacks.put(sKey, newStack);
		}		
		return lpsolStacks;
	}

//## COLLECTION TO FILE #########################################################################

	public void toFile() throws IOException, ParseException {
		Collection auxCollection = new Collection();
		auxCollection.stacksMap = this.stacksMap;
		toFile(auxCollection.defaultName());
	}
	
	public void toFile(String fileStr) throws IOException, ParseException {

		Map<Integer, Stacks> thisStackMap = this.stacksMap;

		FileWriter writer = new FileWriter(new File(fileStr + ".Collection"));
		
		writer.write("### STACK HUMAN-READABLE FORMAT ##########################################\n");
		writer.write("#STACK_XXXX_COLUMNSIZE_XX\n");
		writer.write("#\tint.iD_xxx_String.startStr_xxx\n");
		writer.write("#\tGPH_XXX_MAPSIZE_XX\n");
		writer.write("#\t\tint.iD_xxx_int.gph_xxx_String.startStr_xxx\n");
		writer.write("#\t\tFRAME_XX\n");
		writer.write("#\t\t\tint.iD_xxx_int.track_xxx_int.gph_xxx_int.frame_xxx\n"); 
		writer.write("#\t\t\tdouble.lat_xxx_double.lon_xxx_double.module_xxx\n"); 
		writer.write("#\t\t\tString.startStr_xxx_boolean.isGhost_xxx_int.currentStack_xxx\n"); 
		writer.write("#\t\t\tString.origin_xxx\n");
		writer.write("##########################################################################\n");
		
		for (int currentStack: thisStackMap.keySet()) {
			Stacks thisStack = thisStackMap.get(currentStack);
			writer.write("\nSTACK_" + currentStack + "_");
			writer.write("COLUMNSIZE_" + thisStack.mapSize() + "\n");
			writer.write("\t_" + thisStack.iD + "_" + thisStack.startStr + "\n");
			for (int currentGph: thisStackMap.get(currentStack).getKeySet()) {
				LongTracks thisLt = thisStackMap.get(currentStack).getLt(currentGph);
				writer.write("\tGPH_" + currentGph + "_");
				writer.write("MAPSIZE_" + thisLt.mapSize() + "\n");
				writer.write("\t\t_" + thisLt.iD + "_" + thisLt.gph + "_" + thisLt.startStr + "\n");
				for (int currentFrame: thisStackMap.get(currentStack).getLt(currentGph).getKeySet()) {
					Dots thisDot = thisStackMap.get(currentStack).getLt(currentGph).getDot(currentFrame);
					writer.write("\t\tFRAME_" + currentFrame + "\n");
					writer.write("\t\t\t_" + thisDot.iD + "_" + thisDot.track + "_" + thisDot.gph + "_" + thisDot.frame + 
						"\n\t\t\t_" + thisDot.lat + "_" + thisDot.lon + "_" + thisDot.module + 
						"\n\t\t\t_" + thisDot.startStr + "_" + thisDot.isGhost + "_" + thisDot.currentStack() + 
						"\n\t\t\t_" + thisDot.origin + "\n");
				}
			}
		}
		writer.flush();
		writer.close();
		System.out.println(fileStr + ".Collection has been generated.");
	}

//## COLLECTION NAME ############################################################################

	public String defaultName() throws ParseException {
		String defaultName = "";

		for (int gph: this.getGphSet()) {
			defaultName = defaultName + gph + "-";
		}

		return defaultName = defaultName + this.startStr();
	}
	

	public String defaultName(Collection thisCollection) throws ParseException {
		String defaultName = "";

		for (int gph: thisCollection.getGphSet()) {
			defaultName = defaultName + gph + "-";
		}

		return defaultName = defaultName + thisCollection.startStr();
	}
	
//## COLLECTION TO PLOT #########################################################################


	public void toPlot() {

		Map<Integer, Stacks> thisStackMap = this.stacksMap;	
		for (int currentStack: thisStackMap.keySet()) {
			System.out.println("STACK[" + currentStack + "]");
			for (int currentGph: thisStackMap.get(currentStack).ltMap.keySet()) {
				System.out.println("\tGPH(" + currentGph + ") - ");
				for (int currentFrame: thisStackMap.get(currentStack).ltMap.get(currentGph).dotsMap.keySet()) {
					Dots dot = thisStackMap.get(currentStack).ltMap.get(currentGph).dotsMap.get(currentFrame);
					System.out.println("\t\tKEY[" + currentFrame + "]  \tStack = " + dot.currentStack() + "\tGph = " + dot.gph + "\tFrame = " + dot.frame + "\t(track " + dot.track + ")");
				}
				System.out.println("");
			}
		}
	}
	
//###################################################################################################
}
