import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Scanner;
import java.io.IOException;
import java.text.ParseException;

public class Filter {

	public static void main (String[] args) throws IOException, ParseException {
		
		if (args.length < 2) {
			System.out.println("java Filter [Collection.path] ... [dayFilter] [minLevelFilter] [maxLevelFilter] [minGphFilter] [maxGphFilter] ... [fileName]");
			System.exit(0);
		}
		
		Scanner scan = new Scanner (System.in);
	
		boolean levelFilterOn = false;
		boolean frameFilterOn = false;
		boolean dotFilterOn = false;
		boolean startDateFilterOn = false;
		boolean longFilterOn = false;
		
		int dayFilter = 0;
		int minLevelFilter = 0;
		int maxLevelFilter = 10;
		int minGphFilter = 0;
		int maxGphFilter = 1000;
		
		Collection thisCollection = new Collection (args[0]);
		
		Map<Integer, Stacks> filteredMap = new TreeMap<Integer, Stacks> ();
		
		//## DAYS FILTER ##########################################################################
		
		dayFilter = Integer.parseInt(args[1]);;
		Set<Integer> daySet = new TreeSet<Integer> ();

		for (int sKey: thisCollection.getKeySet()) {
			if (thisCollection.getStack(sKey).dayCounter() > (dayFilter - 1)) {
				filteredMap.put(sKey, thisCollection.getStack(sKey));//daySet.add(sKey);
			}
		}				 
				
		//## MIN LEVEL FILTER ##########################################################################

		Set<Integer> minLevelSet = new TreeSet<Integer> ();
		
		if (args.length > 2) {
			minLevelFilter = Integer.parseInt(args[2]);
			Map<Integer, Stacks> auxMap = filteredMap;
			filteredMap = new TreeMap<Integer, Stacks>();
			
			for (int sKey: auxMap.keySet()) {
				if (thisCollection.getStack(sKey).mapSize() > (minLevelFilter - 1)) {
					filteredMap.put(sKey, thisCollection.getStack(sKey));
				}
			}
		}
		
		//## MAX LEVEL FILTER ##########################################################################

		Set<Integer> maxLevelSet = new TreeSet<Integer> ();
		
		if (args.length > 3) {
			maxLevelFilter = Integer.parseInt(args[3]);
			Map<Integer, Stacks> auxMap = filteredMap;
			filteredMap = new TreeMap<Integer, Stacks>();

			for (int sKey: auxMap.keySet()) {
				if (thisCollection.getStack(sKey).mapSize() < (maxLevelFilter + 1)) {
					filteredMap.put(sKey, thisCollection.getStack(sKey));
				}
			}		
		}		
		
		//## MIN GPH FILTER ##########################################################################
		
		Set<Integer> minGphSet = new TreeSet<Integer> ();
		
		if (args.length > 4) {
			minGphFilter = Integer.parseInt(args[4]);
			Map<Integer, Stacks> auxMap = filteredMap;
			filteredMap = new TreeMap<Integer, Stacks>();

			for (int sKey: auxMap.keySet()) {
				boolean putStack = true;
				for(int gph: thisCollection.getStack(sKey).getKeySet()) {
					if (gph < minGphFilter) putStack = false;
				}
				if (putStack) filteredMap.put(sKey, thisCollection.getStack(sKey));
			}
		}			
		
		//## MIN GPH FILTER ##########################################################################
		
		Set<Integer> maxGphSet = new TreeSet<Integer> ();
	
		if (args.length > 5) {
			maxGphFilter = Integer.parseInt(args[5]);
			Map<Integer, Stacks> auxMap = filteredMap;
			filteredMap = new TreeMap<Integer, Stacks>();

			for (int sKey: auxMap.keySet()) {
				boolean putStack = true;
				for(int gph: thisCollection.getStack(sKey).getKeySet()) {
					if (gph > maxGphFilter) putStack = false;
				}
				if (putStack) filteredMap.put(sKey, thisCollection.getStack(sKey));
			}
		}				
		
		//## MIX FILTER ##########################################################################
		
		/*for (int sKey: thisCollection.getKeySet()) {
			if (daySet.contains(sKey) && minLevelSet.contains(sKey) && maxLevelSet.contains(sKey) && minGphSet.contains(sKey) && maxGphSet.contains(sKey)) {
				filteredMap.put(sKey, thisCollection.getStack(sKey));
			}
		}*/
		
		//## FILTERED STACKS ##########################################################################
		
		/*if (args.length > 4) {
			int findStack = Integer.parseInt(args[4]);
			
			Stacks foundStack = thisCollection.getStack(findStack);
			System.out.println("STACK_" + findStack);
			for (int ltKey: foundStack.getKeySet()) {
				System.out.println("\tGPH_" + ltKey);
				
				for (int dKey: foundStack.getLt(ltKey).getKeySet()) {
					Dots foundDot = foundStack.getLt(ltKey).getDot(dKey);
					System.out.println("\t\t" + foundDot.getDate() + " - latº " + foundDot.lat + " - lonº " + foundDot.lon);
				}
			}
		
		}*/
		
		//## FILTERED STACKS ##########################################################################
		
		System.out.print(dayFilter + " days long, ");
		if (args.length > 2) System.out.print(minLevelFilter + " minLeveled, ");
		if (args.length > 3) System.out.print(maxLevelFilter + " maxLeveled, ");
		if (args.length > 4) System.out.print(minGphFilter + " or lower, ");
		if (args.length > 5) System.out.print(maxGphFilter + " or higher,");
		System.out.println("sized filtered Events: " + filteredMap.keySet().size() + " matches!");
		
		String ans = "";
		System.out.print("Print detailed results? Y/n ");
		ans = scan.nextLine();

		if (ans.equals("Y") || ans.equals("y") || ans.equals("Yes") || ans.equals("yes") || ans.equals("")) {
			System.out.println();
			for (int sKey: filteredMap.keySet()) {
				Stacks thisStack = filteredMap.get(sKey);
				System.out.println("STACK " + sKey + " (" + thisStack.mapSize() + " levels - " + thisStack.dayCounter() + " days - " + thisStack.dotCounter() + " dots):");
				Dots firstDot = thisStack.firstDot();
				Dots lastDot = thisStack.lastDot();
				System.out.println("\tStarts: " + firstDot.getDate() + " at [" + firstDot.gph + "hPa - " + firstDot.lon + "ºlon - " + firstDot.lat + "ºlat]");
				System.out.println("\t  Ends: " + lastDot.getDate() + " at [" + lastDot.gph + "hPa - " + lastDot.lon + "ºlon - " + lastDot.lat + "ºlat]");
				/*Set<String> originSet = new TreeSet<String>();
				for (int ltKey: thisStack.getKeySet()) {
					for (int dKey: thisStack.getLt(ltKey).getKeySet()) {
						String[] auxString = thisStack.getLt(ltKey).getDot(dKey).origin.split("-");
						originSet.add(auxString[1] + "-" + auxString[0] + "-" + auxString[2]);
					}
				}
				for (String oriString: originSet) {
					System.out.println("\t\t" + oriString);
				}*/

			}
		}
		
		System.out.print("Export detailed results? Y/n ");
		ans = scan.nextLine();
		
		if (ans.equals("Y") || ans.equals("y") || ans.equals("Yes") || ans.equals("yes") || ans.equals("")) {
			System.out.println();
			Collection filteredCollection = new Collection();
			filteredCollection.setStartStr(thisCollection.startStr());
			for (int sKey: filteredMap.keySet()) {
				filteredCollection.stacksMap.put(sKey, filteredMap.get(sKey));
			}
			String fileName = "Filtered_" + filteredCollection.defaultName().split("\\-(?=[^\\-]+$)")[0] + "-" + filteredCollection.startStr;
			if (args.length > 6) fileName = args[6];
			filteredCollection.toFile(fileName);
		}

	}
}
