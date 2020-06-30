import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.io.IOException;

public class Concat {
	
	public static void main (String[] args) throws IOException, ParseException {
		
		if (args.length < 2) {
			System.out.println("java Concat [early.Collection] [later.Collection] ... [other.Collections]");
			System.exit(0);
		}
	
		Collection prevCollection = new Collection (args[0]);
		System.out.println("Starting collection: " + args[0]);
		
		for (int i = 1; i < args.length; i++) {
			
			Collection collection1 = prevCollection;	
			Collection collection2 = new Collection (args[i]);
			
			System.out.print("Adding collection: " + args[i] + " - ");

//	## LONGTRACKER ##############################################################

			Date startDate = new Date ();
			if (collection2.firstDate().after(collection1.firstDate())) {
				startDate = collection2.firstDate();
			} else {
				startDate = collection1.firstDate();
			}

			Date endDate = new Date ();
			if (collection1.lastDate().before(collection2.lastDate())) {
				endDate = collection1.lastDate();
			} else {
				endDate = collection2.lastDate();
			}

//			System.out.println("startDate: " + startDate + "\nendDate: " + endDate);

			Map<Integer, Set<String>> longStacks = new TreeMap<Integer, Set<String>> ();
			int longTrackCounter = 0;
			int beta = collection1.getKeySet().size();

			for (int sKey1: collection1.getKeySet()) {
				String message = sKey1 + "/" + beta;
				System.out.print(message);
//				System.out.println("Stack: " + sKey1);
				for (int ltKey1: collection1.getStack(sKey1).getKeySet()) {
//					System.out.println("\tLt: " + ltKey1);
					for (int dKey1: collection1.getStack(sKey1).getLt(ltKey1).getKeySet()) {
//						System.out.println("\t\tDots: " + dKey1);
						Dots dot1 = collection1.getStack(sKey1).getLt(ltKey1).getDot(dKey1);
						if (!dot1.getDate().before(startDate) && !dot1.getDate().after(endDate)) {

							boolean needNewSet = true;
							int currentSetKey = 0;

							for (int setKey: longStacks.keySet()) {
								if (longStacks.get(setKey).contains("pre-" + sKey1)) {
									needNewSet = false;
									currentSetKey = setKey;
								}
							}

							if (needNewSet) {
								Set<String> newSet = new TreeSet<String> ();
								newSet.add("pre-" + sKey1);
								longTrackCounter++;
								longStacks.put(longTrackCounter, newSet);
								currentSetKey = longTrackCounter;
//								System.out.println("\t\t\tNew set " + currentSetKey + ": " + "pre-" + sKey1);
							}

							for (int sKey2: collection2.getKeySet()) {
								if (longStacks.get(currentSetKey).contains("pos-" + sKey2)) continue;

								if (collection2.getStack(sKey2).contains(ltKey1)) {
									if (longStacks.get(currentSetKey).contains("pos-" + sKey2)) continue;

									for (int dKey2: collection2.getStack(sKey2).getLt(ltKey1).getKeySet()) {
										if (longStacks.get(currentSetKey).contains("pos-" + sKey2)) continue;

										Dots dot2 = collection2.getStack(sKey2).getLt(ltKey1).getDot(dKey2);

										if (dot1.getDate().equals(dot2.getDate()) && dot1.lat == dot2.lat && dot1.lon == dot2.lon && dot1.module == dot2.module) {
											longStacks.get(currentSetKey).add("pos-" + sKey2);
//											System.out.println("\t\t\tAdded " + currentSetKey + ": " + "pos-" + sKey2);

										}
									}
								}
							}
						}

					}
				}
				
				for (char ch: message.toCharArray()) {
					System.out.print("\b");
				}
			}
			
			System.out.println("Matching complete!");

			for (int sKey1: collection1.getKeySet()) {
				boolean needNewSet = true;
//				System.out.print("pre-" + sKey1);
				for (int lsKey: longStacks.keySet()) {
					if (longStacks.get(lsKey).contains("pre-" + sKey1)) {
//						System.out.print(" exists!");
						needNewSet = false;
						break;
					}
				}
//				System.out.println("");


				if (needNewSet) {
					Set<String> newSet = new TreeSet<String> ();
					newSet.add("pre-" + sKey1);
					longTrackCounter++;
					longStacks.put(longTrackCounter, newSet);
//					System.out.println("\t\t\tNew set " + longTrackCounter + ": " + "pre-" + sKey1);
				}
			}

			for (int sKey2: collection2.getKeySet()) {
				boolean needNewSet = true;
//				System.out.print("pos-" + sKey2);

				for (int lsKey: longStacks.keySet()) {
					if (longStacks.get(lsKey).contains("pos-" + sKey2)) {
//						System.out.print(" exists!");
						needNewSet = false;
						break;
					}
				}
//				System.out.println("");

				if (needNewSet) {
					Set<String> newSet = new TreeSet<String> ();
					newSet.add("pos-" + sKey2);
					longTrackCounter++;
					longStacks.put(longTrackCounter, newSet);
//					System.out.println("\t\t\tNew set " + longTrackCounter + ": " + "pos-" + sKey2);
				}
			}

			boolean mergeable = true;
			int iter = 0;
			while (mergeable) {
//				System.out.println("Iter " + ++iter);
				mergeable = false;
				int aMerge = 0;
				int bMerge = 0;

				for (int aKey: longStacks.keySet()) {
					for (int bKey: longStacks.keySet()) {
						if (aKey == bKey) continue;
						for (String aString: longStacks.get(aKey)) {					
							if (longStacks.get(bKey).contains(aString)) {
//								System.out.println("\ta " + aKey + " - b " + bKey);
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
					longTrackCounter++;
//					System.out.print("\tStack " + longTrackCounter + ":\n\ta ");

					for (String element: longStacks.get(aMerge)) {
						newStackSet.add(element);
//						System.out.print(element + ", ");
					}
//					System.out.print("\n\tb ");
					for (String element: longStacks.get(bMerge)) {
						newStackSet.add(element);
//						System.out.print(element + ", ");
					}
//					System.out.println("");
					longStacks.remove(aMerge);
					longStacks.remove(bMerge);
					longStacks.put(longTrackCounter, newStackSet);				
				}		
			}

			int checkCount = 0;
//			System.out.println("longStacks.size " + longStacks.keySet().size());
//			for (int key: longStacks.keySet()) {
//				System.out.print("longStack " + key + ": ");
//				for (String stack: longStacks.get(key)) {
//					checkCount++;
//					System.out.print(stack + ", ");

//					for (int jKey: longStacks.keySet()) {
//						if (jKey != key && longStacks.get(jKey).contains(stack)) {
//							System.out.println("\tlongStack " + stack + " already exists in Stack " + jKey);
//						}
//					}
//				}
//				System.out.println("");			
//			}

//			System.out.print("eventCount " + collection1.getKeySet().size() + " + " + collection2.getKeySet().size() + " = " + checkCount + " events.");
//			if (collection1.getKeySet().size() + collection2.getKeySet().size() == checkCount) {
//				System.out.println("\tOK");
//			} else {
//				System.out.println("\tERROR!");
//			}

//	###### UNIFIER ##################################################################

			Collection newCollection = new Collection ();
			Date newStartDate = new Date();

			if (collection1.firstDate().before(collection2.firstDate())) {
				newStartDate = collection1.firstDate();
			} else {
				newStartDate = collection2.firstDate();
			}

//			System.out.println("NEWSTACKdate " + newStartDate);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String newStartString = sdf.format(newStartDate);

//			System.out.println("NEWSTACKstr " + newStartString);

			for (int setKey: longStacks.keySet()) {

				Stacks newStack = new Stacks(setKey, newStartString);

				for (String currentString: longStacks.get(setKey)) {
					String currentCol = currentString.split("-")[0];
					int currentStack = Integer.parseInt(currentString.split("-")[1]);

					Collection sourceCollection = new Collection();
					if (currentCol.equals("pre")) {
						sourceCollection = collection1;
					} else if (currentCol.equals("pos")) {
						sourceCollection = collection2;
					}

//					System.out.println("currentStack: " + currentString + "\tsourceStart " + sourceCollection.startStr());

					for (int ltKey: sourceCollection.getStack(currentStack).getKeySet()) {

						if (!newStack.contains(ltKey)) {
							LongTracks currentLt = new LongTracks(ltKey, setKey, newStartString, setKey);
							newStack.addLt(ltKey, currentLt);
						}

						for (int dKey: sourceCollection.getStack(currentStack).getLt(ltKey).getKeySet()) {

							Dots thisDot = sourceCollection.getStack(currentStack).getLt(ltKey).getDot(dKey);
							thisDot.setCurrentStack(setKey);
							thisDot.setNewFrame(newStartString);
							thisDot.setStartString(newStartString);
							newStack.addDot(thisDot.gph, thisDot.frame, thisDot);						
						}

					}
				}

				newCollection.stacksMap.put(setKey, newStack);
			}
			prevCollection = new Collection();
			prevCollection = newCollection;	
			System.out.print(args[i - 1] + " finished! [");
			
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			System.out.println(dateFormat.format(date) + "]");
		}
		
		prevCollection.toFile();	
	}

}
