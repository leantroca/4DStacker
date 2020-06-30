import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Arrays;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;

public class Checker {
	public static void main (String[] args) throws IOException, ParseException {
		Collection thisCollection = new Collection(args[0]);
		
		boolean detail = false;
		if (Arrays.asList(args).contains("detail")) {
			detail = true;
		}

//### DOTS CHECK ##############################################################	
		
		if (args.length > 1) {
			String[] originArray = Arrays.copyOfRange(args,1,args.length);

			Set<String> errorMessages = new TreeSet<String> ();
			int dotCounter = 0;
			for (String currentOrigin: originArray) {
				
				if (currentOrigin.equals("detail")) {
					continue;
				}
				
				Collection originCollection = new Collection(currentOrigin);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
				String startString = originCollection.startStr();
				
				int beta = originCollection.getKeySet().size();
				System.out.print("\nDOTCHECK " + currentOrigin + " - ");
				
				for (int aSKey: originCollection.getKeySet()) {
					String message = aSKey + " / " + beta;
					System.out.print(message);

					for (int aLtKey: originCollection.getStack(aSKey).getKeySet()) {

						for (int aDKey: originCollection.getStack(aSKey).getLt(aLtKey).getKeySet()) {
							Dots aDot = originCollection.getStack(aSKey).getLt(aLtKey).getDot(aDKey);
							//System.out.print(aDot.origin);
							boolean dotOk = false;
							dotCounter++;
							for (int bSKey: thisCollection.getKeySet()) {
								if (dotOk) break;
								
								if (thisCollection.getStack(bSKey).contains(aLtKey)) {
									for (int bDKey: thisCollection.getStack(bSKey).getLt(aLtKey).getKeySet()) {
										if (thisCollection.getStack(bSKey).getLt(aLtKey).getDot(bDKey).getDate().equals(aDot.getDate())) {
											Dots bDot = thisCollection.getStack(bSKey).getLt(aLtKey).getDot(bDKey);
											if(aDot.lat == bDot.lat && aDot.lon == bDot.lon && aDot.module == bDot.module) { //24118 Error messages
												dotOk = true;
												//System.out.println(aDot.getDate() + "\n" + bDot.getDate() + "\n");
												break;
											}
										}
									}
								}
							}
							
//							if (dotOk) {
//								System.out.println("");
//							} else {
//								System.out.println("\tMISSING");
//							}
							
							//System.out.println("\tRANDOM_1 " + aSKey + "-" + aLtKey + "-" + aDKey + " : " + dotOk + " " + detail);
							if (dotOk && detail) {
								System.out.println("\t" + startString + "-" + aSKey + "-" + aLtKey + "-" + aDKey + "   \tOK!");
							} else if (detail) {
								System.out.println("\t" + startString + "-" + aSKey + "-" + aLtKey + "-" + aDKey + "   \tERROR!!!");
							}
							
							//System.out.println("\tRANDOM_1 " + aSKey + "-" + aLtKey + "-" + aDKey + " : " + dotOk);
							if (!dotOk) {
								errorMessages.add(aDot.origin + "\tMissing.");
							}
						}
						
					}
					for (int chardi = 0; chardi < message.length(); chardi++) {
						System.out.print("\b");
					}
				}
			}
			System.out.println("");
			
			DecimalFormat df = new DecimalFormat("#.0000");
			if (errorMessages.size() > 0) {
				if (detail) {
					for (String message: errorMessages) {
						System.out.println(message);
					}
				}
				System.out.println("Dots Check\t " + errorMessages.size() + " missing dots (" + df.format((double)errorMessages.size() / (double)dotCounter) + " % loss)");
			} else {
				System.out.println("Dots Check\t OK!");
			}
		}
		
//### TRACK CHECK #############################################################
		
		Map<String, Set<Integer>> trackCheck = new TreeMap<String, Set<Integer>> ();

		for (int iSKey: thisCollection.getKeySet()) {
			for (int iLtKey: thisCollection.getStack(iSKey).getKeySet()) {
				for (int iDKey: thisCollection.getStack(iSKey).getLt(iLtKey).getKeySet()) {
					int iTrack = thisCollection.getStack(iSKey).getLt(iLtKey).getDot(iDKey).track;
					String iStartString = thisCollection.getStack(iSKey).getLt(iLtKey).getDot(iDKey).startStr;

					if (!trackCheck.keySet().contains(thisCollection.getStack(iSKey).getLt(iLtKey).getDot(iDKey).origin)) {
						Set<Integer> checkSet = new TreeSet<Integer> ();
						checkSet.add(iSKey);
						trackCheck.put(thisCollection.getStack(iSKey).getLt(iLtKey).getDot(iDKey).origin, checkSet);
					} else {
						//Set<Integer> checkSet = trackCheck.get(iLtKey + "-" + iTrack);
						//checkSet.add(iSKey);
						trackCheck.get(thisCollection.getStack(iSKey).getLt(iLtKey).getDot(iDKey).origin).add(iSKey);
					}
				}
			}
		}
		
		boolean trackOk = false;
		Set<String> errorMessages = new TreeSet<String> ();
		
		for (String key: trackCheck.keySet()) {
			if (detail) System.out.print("Track " + key + ",\t");
			if (trackCheck.get(key).size() != 1) {
				errorMessages.add(key + " belongs to " + trackCheck.get(key).size() + " Stacks.");
			}
			for (int track: trackCheck.get(key)) {
				if (detail) System.out.print(track + ",");
			}
			if (detail) System.out.println("");
		}
		
		if (errorMessages.size() > 0) {
			for (String message: errorMessages) {
				System.out.println(message);
			}
		} else {
			System.out.println("Tracks Check\t OK!");
		}
		
//### STACK CHECK #############################################################
		
		Map<Integer, int[]> stackCheck = new TreeMap<Integer, int[]> ();
		for (int sKey: thisCollection.getKeySet()) {

			if (sKey != thisCollection.getStack(sKey).iD) {
				if (stackCheck.containsKey(sKey)) {
					stackCheck.get(sKey)[0]++;
				} else {
					stackCheck.put(sKey, new int[3]);
					stackCheck.get(sKey)[0]++;
				}
			}
			
			for (int ltKey: thisCollection.getStack(sKey).getKeySet()) {
				if (sKey != thisCollection.getStack(sKey).getLt(ltKey).iD) {
//					System.out.println("\t" + sKey + " - " + thisCollection.getStack(sKey).getLt(ltKey).iD);
					if (stackCheck.containsKey(sKey)) {
						stackCheck.get(sKey)[1]++;
					} else {
						stackCheck.put(sKey, new int[3]);
						stackCheck.get(sKey)[1]++;
					}
				}
				
				for (int dKey: thisCollection.getStack(sKey).getLt(ltKey).getKeySet()) {
					//System.out.println("\t" + sKey + " - " + thisCollection.getStack(sKey).getLt(ltKey).getDot(dKey).currentStack());
					if (sKey != thisCollection.getStack(sKey).getLt(ltKey).getDot(dKey).currentStack()) {
						
						if (stackCheck.containsKey(sKey)) {
							stackCheck.get(sKey)[2]++;
						} else {
							stackCheck.put(sKey, new int[3]);
							stackCheck.get(sKey)[2]++;
						}
					}		
				}
			}
		}
		
		
		if (stackCheck.keySet().size() > 0) {
			for (int sKey: stackCheck.keySet()) {
				System.out.println("Stack " + sKey + " contains\t" + stackCheck.get(sKey)[0] + " Stack ID discrepancies\t" + stackCheck.get(sKey)[1] + " Track ID discrepancies\t" + stackCheck.get(sKey)[2] + " Dot ID discrepancies.");
			}
		} else {
			System.out.println("Stacks Check\t OK!");
		}

		
//### OVERLAP CHECK ###########################################################
		
		Set<String> overlapMessages = new TreeSet<String> ();
		for (int aSKey: thisCollection.getKeySet()) {
			for (int aLtKey: thisCollection.getStack(aSKey).getKeySet()) {
				for (int aDKey: thisCollection.getStack(aSKey).getLt(aLtKey).getKeySet()) {
					Dots aDot = thisCollection.getStack(aSKey).getLt(aLtKey).getDot(aDKey);
					for (int bDKey: thisCollection.getStack(aSKey).getLt(aLtKey).getKeySet()) {
						Dots bDot = thisCollection.getStack(aSKey).getLt(aLtKey).getDot(bDKey);
						if (aDot.getDate().equals(bDot.getDate()) && aDot.track != bDot.track) {
							if (detail) System.out.println(aSKey + "-" + aLtKey + "-" + aDKey + " overlaps with " + aSKey + "-" + aLtKey + "-" + bDKey + ".");
							overlapMessages.add(aSKey + "-" + aLtKey + "-" + aDKey + " overlaps with " + aSKey + "-" + aLtKey + "-" + bDKey + ".");
						}
					}
				}
			}
		}
		
		if (overlapMessages.size() > 0) {
			for (String message: overlapMessages) {
				System.out.println(message);
			}
		} else {
			System.out.println("Overlap Check\t OK!");
		}		
	}
}