import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.text.ParseException;

public class Parser {
	
//## TRUE LINKS ###########################################

	public static Map<Integer, Links> trueLinkThis(Collection lowerCollection, Collection higherCollection, double threshDistance) {

		int linkCounter = 0;
		return trueMethod(lowerCollection, higherCollection, threshDistance, linkCounter);
	}

	public static Map<Integer, Links> trueLinkThis(Collection lowerCollection, Collection higherCollection, double threshDistance, int linkCounter) {

		return trueMethod(lowerCollection, higherCollection, threshDistance, linkCounter);
	}

	public static Map<Integer, Links> trueMethod(Collection lowerCollection, Collection higherCollection, double threshDistance, int linkCounter) {
	
		Map<Integer, Links> trueLinksMap = new TreeMap<Integer, Links>();
		int higherGph = higherCollection.closestGph(0);
//		System.out.println("higherGph = " + higherGph);
		int lowerGph = lowerCollection.closestGph(higherGph);
//		System.out.println("lowerGph = " + lowerGph);

		for(int stackLowKey: lowerCollection.getKeySet()) {
//			System.out.println("\tstackLowKey = " + stackLowKey);
			if (lowerCollection.getStack(stackLowKey).contains(lowerGph)) {
//				System.out.println("\t\tcontains.lowerGph = True");
				for(int dotLowKey: lowerCollection.getStack(stackLowKey).getLt(lowerGph).getKeySet()) {
//					System.out.println("\t\t\tdotLowKey = " + dotLowKey);
					for(int stackHighKey: higherCollection.getKeySet()) {
//						System.out.println("\t\t\t\tdotLowKey = " + dotLowKey);
						if(higherCollection.getStack(stackHighKey).contains(higherGph)) {
//							System.out.println("\t\t\t\t\tcontains.higherGph = True");
							if(higherCollection.getStack(stackHighKey).getLt(higherGph).contains(dotLowKey)) {
//								System.out.println("\t\t\t\t\t\tcontains.dotLowKey = True");
								Dots lowerDot = lowerCollection.getStack(stackLowKey).getLt(lowerGph).getDot(dotLowKey);
								Dots higherDot = higherCollection.getStack(stackHighKey).getLt(higherGph).getDot(dotLowKey);
//								System.out.print("\t\t\t\t\t\tdistance = " + distance(lowerDot.lat, lowerDot.lon, higherDot.lat, higherDot.lon));
								if(distance(lowerDot.lat, lowerDot.lon, higherDot.lat, higherDot.lon) < threshDistance) {
//									System.out.println("\tOK!!!");
									linkCounter++;
									trueLinksMap.put(linkCounter, new Links(linkCounter, lowerDot, higherDot, 1, threshDistance));
								}
							}
						}
					}
				}
			}
		}
		return trueLinksMap;
	}

//## FALSE LINKS ##########################################

	public static Map<Integer, Links> falseLinkThis(Collection lowerCollection, Collection higherCollection, Net trueLinksNet) {

		int linkCounter = 0;
		return falseMethod(lowerCollection, higherCollection, trueLinksNet, linkCounter);
	}

	public static Map<Integer, Links> falseLinkThis(Collection lowerCollection, Collection higherCollection, Net trueLinksNet, int linkCounter) {

		return falseMethod(lowerCollection, higherCollection, trueLinksNet, linkCounter);
	}

	public static Map<Integer, Links> falseMethod(Collection lowerCollection, Collection higherCollection, Net trueLinksNet, int linkCounter) {

		Map<Integer, Links> falseLinksMap = new TreeMap<Integer, Links>();
		int higherGph = higherCollection.closestGph(0);
		int lowerGph = lowerCollection.closestGph(higherGph);

		for(int linkKey: trueLinksNet.getKeySet()) {

			int lowerStackKey = trueLinksNet.getLink(linkKey).lowDot.currentStack();
			int higherStackKey = trueLinksNet.getLink(linkKey).highDot.currentStack();

			if(lowerCollection.getStack(lowerStackKey).contains(lowerGph) && higherCollection.getStack(higherStackKey).contains(higherGph)) {
				for(int currentFrame: lowerCollection.getStack(lowerStackKey).getLt(lowerGph).getKeySet()) {
					if(higherCollection.getStack(higherStackKey).getLt(higherGph).contains(currentFrame)) {
						Dots lowerDot = lowerCollection.getStack(lowerStackKey).getLt(lowerGph).getDot(currentFrame);
						Dots higherDot = higherCollection.getStack(higherStackKey).getLt(higherGph).getDot(currentFrame);
						double threshDistance = trueLinksNet.getGhostDistance();
						if(!(distance(lowerDot.lat, lowerDot.lon, higherDot.lat, higherDot.lon) < threshDistance)) {
							linkCounter++;
							falseLinksMap.put(linkCounter, new Links(linkCounter, lowerDot, higherDot, 2, threshDistance));
						}
					}
				}

			}
			
		}
		return falseLinksMap;
	}

//## GHOST LINKS ##########################################

	public static Map<Integer, Links> ghostLinkThis(Collection lowerCollection, Collection higherCollection, double threshDistance) {

		int linkCounter = 0;
		return ghostMethod(lowerCollection, higherCollection, threshDistance, linkCounter);
	}

	public static Map<Integer, Links> ghostLinkThis(Collection lowerCollection, Collection higherCollection, double threshDistance, int linkCounter) {

		return ghostMethod(lowerCollection, higherCollection, threshDistance, linkCounter);
	}

	public static Map<Integer, Links> ghostMethod(Collection lowerCollection, Collection higherCollection, double threshDistance, int linkCounter) {

		Map<Integer, Links> ghostLinksMap = new TreeMap<Integer, Links>();
		int higherGph = higherCollection.closestGph(0);
		int lowerGph = lowerCollection.closestGph(higherGph);

		for(int lowerStackKey: lowerCollection.getKeySet()) {
			if (lowerCollection.getStack(lowerStackKey).contains(lowerGph)) {
				for(int lowerDotKey: lowerCollection.getStack(lowerStackKey).getLt(lowerGph).getKeySet()) {
					linkCounter++;
					Dots lowerDot = lowerCollection.getStack(lowerStackKey).getLt(lowerGph).getDot(lowerDotKey);
					Dots ghostDot = new Dots(lowerDot.iD, higherGph, lowerDot.track, lowerDot.frame, lowerDot.lon, lowerDot.lat, lowerDot.module, lowerDot.startStr, true, 0);
					ghostLinksMap.put(linkCounter, new Links(linkCounter, lowerDot, ghostDot, 0, threshDistance));
				}
			}
		}
		return ghostLinksMap;
	}

//## COST FUNCTION ########################################

	public static double[] cMatrix(Net thisNet) throws ParseException
	{
		double [] cMatrix = new double[thisNet.getKeySet().size()];
		int index = 0;
		for (int key: thisNet.getKeySet()) 
		{
			Links currentLink = thisNet.getLink(key);
			cMatrix[index] = currentLink.getDistance();
			index++;
		}
		return cMatrix;
	}

//## MBT MATRIX ###########################################
	
	public static int[][] mbtMatrix(Net thisNet) throws ParseException 
	{
		int a = thisNet.getKeySet().size();
		ArrayList<int[]> mbtList = new ArrayList<int[]>();		
		ArrayList<int[]> alreadyListed = new ArrayList<int[]>();

		for (int iKey: thisNet.getKeySet()) 
		{
			Links iLink = thisNet.getLink(iKey);
			boolean isAlreadyListed = false;
			for (int[] checkPair: alreadyListed)
			{
				if (checkPair[0] == iLink.lowDot.currentStack() && checkPair[1] == iLink.highDot.currentStack()) {
					isAlreadyListed = true;
				}
			}
			if (iLink.highDot.currentStack() == 0 || isAlreadyListed) 
			{
				continue;
			}
			alreadyListed.add(new int[] {iLink.lowDot.currentStack(), iLink.highDot.currentStack()});
			int[] currentRestriction = new int[a];
			int jSelf = 0;
			int jIndex = 0;
			int negSelf = 0;
			for (int jKey: thisNet.getKeySet()) 
			{
				Links jLink = thisNet.getLink(jKey);
				if (iLink.lowDot.currentStack() == jLink.lowDot.currentStack() && iLink.highDot.currentStack() == jLink.highDot.currentStack()) 
				{
					if (iLink.lowDot.getDate().equals(jLink.lowDot.getDate())) 
					{
						jSelf = jIndex;						
					} 
					else 
					{
						negSelf--;
						currentRestriction[jIndex] = 1;
					}
				} 
				else 
				{
					currentRestriction[jIndex] = 0;
				}
				jIndex++;
			}
			currentRestriction[jSelf] = negSelf;
			mbtList.add(currentRestriction);
		}
		int[][] mbtMatrix = new int[mbtList.size()][thisNet.getKeySet().size()];
		int mbtIter = 0;
		for (int[] currentRestriction: mbtList) 
		{
			mbtMatrix[mbtIter] = currentRestriction;
			mbtIter++;
		}
		return mbtMatrix;
	}

	public static String[] mbtDir(int[][] mbtMatrix) {
		String[] mbtDir = new String[mbtMatrix.length];
		for (int i = 0; i < mbtMatrix.length; i++) {
			mbtDir[i] = "==";
		}
		return mbtDir;
	}

	public static int[] mbtRhs(int[][] mbtMatrix) {
		int[] mbtRhs = new int[mbtMatrix.length];
		for (int i = 0; i < mbtMatrix.length; i++) {
			mbtRhs[i] = 0;
		}
		return mbtRhs;
	}



//## MNBT MATRIX ##########################################

	public static int[][] mnbtMatrix(Net thisNet, Collection lowerCollection) {

		Map<Integer, Links> uniqueMap = new TreeMap<Integer, Links>();
		Set<Integer> uniqueSet = new HashSet<Integer>();
		int linkCounter = 0;
		for (int key: thisNet.getKeySet()) {
			if (!uniqueSet.contains(thisNet.getLink(key).lowDot.currentStack())) {
				linkCounter++;
				uniqueMap.put(key, thisNet.getLink(key));
				uniqueSet.add(thisNet.getLink(key).lowDot.currentStack());
			}
			
		}

		int a = thisNet.getKeySet().size();
		int b = uniqueMap.keySet().size();
		int[][] mnbtMatrix = new int[b][a];
		int iIter = 0;

		for (int iSKey: uniqueMap.keySet()) {
			int iStack = uniqueMap.get(iSKey).lowDot.currentStack();
			int iCounter = 0;
			for (int jSKey: thisNet.getKeySet()) {
				int jStack = thisNet.getLink(jSKey).lowDot.currentStack();

				if (iSKey == jSKey || iStack == jStack) {
					mnbtMatrix[iIter][jSKey-1] = 0;
					continue;
				}

				boolean simultaneous = false;
				for (int iLtKey: lowerCollection.getStack(iStack).getKeySet()) {
					if (lowerCollection.getStack(jStack).contains(iLtKey)) {
						for (int iDKey: lowerCollection.getStack(iStack).getLt(iLtKey).getKeySet()) {
							if (lowerCollection.getStack(jStack).getLt(iLtKey).contains(iDKey)) {
								simultaneous = true;
								//iCounter++;
							}
						}
					}
				}

				if (simultaneous) {
					iCounter++;
					mnbtMatrix[iIter][jSKey-1] = 1;
				} else {
					mnbtMatrix[iIter][jSKey-1] = 0;
				}
			}
			mnbtMatrix[iIter][iSKey-1] = iCounter;
			iIter++;
		}
		return mnbtMatrix;
	}

	public static String[] mnbtDir(int[][] mnbtMatrix) {
		String[] mnbtDir = new String[mnbtMatrix.length];
//		System.out.println(mnbtMatrix.length + " - " + mnbtMatrix[0].length);
		for (int i = 0; i < mnbtMatrix.length; i++) {
			mnbtDir[i] = "<=";
		}
		return mnbtDir;
	}

	public static int[] mnbtRhs(int[][] mnbtMatrix) {

		int[] mnbtRhs = new int[mnbtMatrix.length];
		for(int i = 0; i < mnbtMatrix.length; i++) {
			int max = 0;
			for (int j = 0; j < mnbtMatrix[0].length; j++) {
				if (mnbtMatrix[i][j] > max) {
					max = mnbtMatrix[i][j];
				}
			}
			mnbtRhs[i] = max;
		}
		return mnbtRhs;
	}

//## DRLT MATRIX ##########################################
	
	public static int[][] drltMatrix(Net thisNet) throws ParseException {
		int a = thisNet.getKeySet().size();
		int[][] drltMatrix = new int[a][a];
		int iIndex = 0;
		for (int iKey: thisNet.getKeySet()) {
			Links iLink = thisNet.getLink(iKey);
			int jIndex = 0;
			for (int jKey: thisNet.getKeySet()) {
				Links jLink = thisNet.getLink(jKey);
				if (iLink.lowDot.currentStack() == jLink.lowDot.currentStack() && iLink.lowDot.getDate().equals(jLink.lowDot.getDate())) {
					drltMatrix[iKey - 1][jKey - 1] = 1; //drltMatrix[iIndex][jIndex] = 1;
				} else {
					drltMatrix[iKey - 1][jKey - 1] = 0; //drltMatrix[iIndex][jIndex] = 0;
				}
				jIndex++;
			}
			iIndex++;			
		}
		return drltMatrix;
	}

	public static String[] drltDir(int[][] drltMatrix) {
		String[] drltDir = new String[drltMatrix.length];
		for (int i = 0; i < drltMatrix.length; i++) {
			drltDir[i] = "==";
		}
		return drltDir;
	}

	public static int[] drltRhs(int[][] drltMatrix) {
		int[] drltRhs = new int[drltMatrix.length];
		for (int i = 0; i < drltMatrix.length; i++) {
			drltRhs[i] = 1;
		}
		return drltRhs;
	}

//## DRHT MATRIX ##########################################
	
	public static int[][] drhtMatrix(Net thisNet) throws ParseException 
	{
		ArrayList<int[]> alreadyListed = new ArrayList<int[]>();
		ArrayList<int[]> drhtList = new ArrayList<int[]>();
		
		for (int iKey: thisNet.getKeySet()) 
		{
			Links iLink = thisNet.getLink(iKey);
			if (iLink.highDot.currentStack() == 0) 
			{
				continue;
			}
			
			boolean isAlreadyListed = false;
			for (int[] checkDot: alreadyListed)
			{
				if (checkDot[0] == iLink.highDot.gph && checkDot[1] == iLink.highDot.currentStack() && checkDot[2] == iLink.highDot.frame) 
				{
					isAlreadyListed = true;
				}
			}
			
			if (isAlreadyListed) 
			{
				continue;
			}
			
			int jIndex = 0;
			int[] currentConst = new int[thisNet.getKeySet().size()];
			for (int jKey: thisNet.getKeySet()) 
			{
				Links jLink = thisNet.getLink(jKey);
				if (jLink.highDot.gph == iLink.highDot.gph && jLink.highDot.currentStack() == iLink.highDot.currentStack() && jLink.highDot.frame == iLink.highDot.frame)
				{
					currentConst[jIndex] = 1;
				} 
				else 
				{
					currentConst[jIndex] = 0;
				}
				jIndex++;
			}
			drhtList.add(currentConst);
			alreadyListed.add(new int[] {iLink.highDot.gph, iLink.highDot.currentStack(), iLink.highDot.frame});
		}
			
		int[][] drhtMatrix = new int[drhtList.size()][thisNet.getKeySet().size()];
		int iIndex = 0;
		for (int[] currentConst: drhtList)
		{
			drhtMatrix[iIndex] = currentConst;
			iIndex++;
		}

		return drhtMatrix;
	}

	public static String[] drhtDir(int[][] drhtMatrix) {
		String[] drhtDir = new String[drhtMatrix.length];
		for (int i = 0; i < drhtMatrix.length; i++) {
			drhtDir[i] = "<=";
		}
		return drhtDir;
	}

	public static int[] drhtRhs(int[][] drhtMatrix) {
		int[] drhtRhs = new int[drhtMatrix.length];
		for (int i = 0; i < drhtMatrix.length; i++) {
			drhtRhs[i] = 1;
		}
		return drhtRhs;
	}

//## UTILITY ###########################################

	public static double distance(double lat1, double lng1, double lat2, double lng2) {
		double earthRadius = 6371000; //meters
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist =/* (double)*/ (earthRadius * c) / 1000; //Km
		return dist;
	}

//## MATRIX TO CSV ########################################

	public static void toCSV (String name, int[][] matrix) throws IOException {
		FileWriter writer = new FileWriter(new File(name + ".csv"));
		
		for (int i = 0; i < matrix.length; i++) {
			writer.write(matrix[i][0] + "");
			for (int j = 1; j < matrix[i].length; j++) {
				writer.write("," + matrix[i][j]);
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
		System.out.println(name + ".csv file has been generated.");
	}

	public static void toCSV (String name, double[][] matrix) throws IOException {
		FileWriter writer = new FileWriter(new File(name + ".csv"));
		
		for (int i = 0; i < matrix.length; i++) {
			writer.write(matrix[i][0] + "");
			for (int j = 1; j < matrix[i].length; j++) {
				writer.write("," + matrix[i][j]);
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
		System.out.println(name + ".csv file has been generated.");
	}

	public static void toCSV (String name, String[][] matrix) throws IOException {
		FileWriter writer = new FileWriter(new File(name + ".csv"));
		
		for (int i = 0; i < matrix.length; i++) {
			writer.write(matrix[i][0] + "");
			for (int j = 1; j < matrix[i].length; j++) {
				writer.write("," + matrix[i][j]);
			}
			writer.write("\n");
		}
		writer.flush();
		writer.close();
		System.out.println(name + ".csv file has been generated.");
	}

	public static void toCSV (String name, int[] matrix) throws IOException {
		FileWriter writer = new FileWriter(new File(name + ".csv"));

		writer.write(matrix[0] + "");
		for (int i = 1; i < matrix.length; i++) {
			writer.write("," + matrix[i]);
		}
		writer.flush();
		writer.close();
		System.out.println(name + ".csv file has been generated.");
	}

	public static void toCSV (String name, double[] matrix) throws IOException {
		FileWriter writer = new FileWriter(new File(name + ".csv"));

		writer.write(matrix[0] + "");
		for (int i = 1; i < matrix.length; i++) {
			writer.write("," + matrix[i]);
		}
		writer.flush();
		writer.close();
		System.out.println(name + ".csv file has been generated.");
	}

	public static void toCSV (String name, String[] matrix) throws IOException {
		FileWriter writer = new FileWriter(new File(name + ".csv"));

		writer.write(matrix[0] + "");
		for (int i = 1; i < matrix.length; i++) {
			writer.write("," + matrix[i]);
		}
		writer.flush();
		writer.close();
		System.out.println(name + ".csv file has been generated.");
	}


}
