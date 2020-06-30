import java.util.Map;
import java.util.TreeMap;
import java.util.Set;
import java.util.TreeSet;
import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.File;

public class Net{
	private double ghostDistance;
	Map<Integer, Links> linksMap = new TreeMap<Integer, Links>();

//## CONSTRUCTORS ######################################

	public Net() {

	}

	public Net(Map<Integer, Links> thisMap) {
		linksMap = thisMap;
	}

	public Net(String filePath) throws IOException {
		linksMap = fileToNet(filePath);	
	}

//## METHODS ###########################################

	public Set<Integer> getKeySet() {
		return this.linksMap.keySet();
	}
	
	public Links getLink(int key) {
		return this.linksMap.get(key);
	}

	public void addLink(int key, Links linkCon) {
		linksMap.put(key, linkCon);	
	}

	public int mapSize() {
		return this.linksMap.keySet().size();
	}

	public boolean contains(int key) {
		return this.linksMap.containsKey(key);
	}

	public Set<Integer> getGphSet() {
		Set<Integer> gphSet = new TreeSet<Integer>();
		for (int key: this.getKeySet()) {
			gphSet.add(this.getLink(key).lowDot.gph);
			gphSet.add(this.getLink(key).highDot.gph);
		}
		return gphSet;
	}

	public void setGhostDistance(double ghostCon) {
		for(int key: this.getKeySet()) {
			this.getLink(key).setGhostDistance(ghostCon);
			break;
		}
		
	}

	public double getGhostDistance() {
		ghostDistance = 0;
		for(int key: this.getKeySet()) {
			ghostDistance = this.getLink(key).ghostDistance();
			break;
		}
		return ghostDistance;
	}

	public String startStr() {
		return this.getLink(1).lowDot.startStr;
	}

	public void toFile() throws IOException {
		Net auxNet = new Net();
		auxNet.linksMap = this.linksMap;
		netToFile(auxNet, this.defaultName());
	}

	public void toFile(String fileStr) throws IOException {
		Net auxNet = new Net();
		auxNet.linksMap = this.linksMap;
		netToFile(auxNet, fileStr);
	}
	
	public void toPlot() {
		Net auxNet = new Net();
		auxNet.linksMap = this.linksMap;
		netToPlot(auxNet);	
	}

//## NET TO PLOT #######################################

	public static void netToPlot(Net thisNet) {
		for(int linkKey: thisNet.getKeySet()) {
			Links thisLink = thisNet.getLink(linkKey);
			Dots lowerDot = thisNet.getLink(linkKey).lowDot;
			Dots higherDot = thisNet.getLink(linkKey).highDot;
			String thisType = "";
			if (thisLink.type == 0)
				thisType = "GHOST link";
			if (thisLink.type == 1)
				thisType = "TRUE link";
			if (thisLink.type == 2)
				thisType = "FALSE link";
			System.out.println("LINK[" + linkKey + "]\tType = " + thisType + "\tDistance = " + thisLink.getDistance() + ";");
			System.out.println("\tLowDot: \tSTACK = " + lowerDot.currentStack() + "\tgph = " + lowerDot.gph + "\tframe = " + lowerDot.frame + "\tghost = " + lowerDot.isGhost);
			System.out.println("\tHighDot:\tSTACK = " + higherDot.currentStack() + "\tgph = " + higherDot.gph + "\tframe = " + higherDot.frame + "\tghost = " + higherDot.isGhost);
			System.out.println("");
		}
	}

//## NET TO FILE #######################################

	public void netToFile(Net thisNet, String fileStr) throws IOException {
		
		FileWriter writer = new FileWriter(new File(fileStr + ".Net"));
		for (int currentLink: thisNet.getKeySet()) {
			writer.write("\nLINK__" + currentLink);
			writer.write("\n\tint.iD_" + thisNet.getLink(currentLink).iD + "_int.type_" + thisNet.getLink(currentLink).type + "_double.ghostDistance_" + thisNet.getLink(currentLink).ghostDistance());
			Dots lowDot = thisNet.getLink(currentLink).lowDot;
			Dots highDot = thisNet.getLink(currentLink).highDot;
			writer.write("\n\tLOWERDOT");
			writer.write("\n\t\tint.iD_" + lowDot.iD + "_int.track_" + lowDot.track + "_int.gph_" + lowDot.gph + "_int.frame_" + lowDot.frame + 
				"\n\t\t\tdouble.lat_" + lowDot.lat + "_double.lon_" + lowDot.lon + "_double.module_" + lowDot.module + 
				"\n\t\t\tString.startStr_" + lowDot.startStr + "_boolean.isGhost_" + lowDot.isGhost + "_int.currentStack_" + lowDot.currentStack());
			writer.write("\n\tHIGHERDOT");
			writer.write("\n\t\tint.iD_" + highDot.iD + "_int.track_" + highDot.track + "_int.gph_" + highDot.gph + "_int.frame_" + highDot.frame + 
				"\n\t\t\tdouble.lat_" + highDot.lat + "_double.lon_" + highDot.lon + "_double.module_" + highDot.module + 
				"\n\t\t\tString.startStr_" + highDot.startStr + "_boolean.isGhost_" + highDot.isGhost + "_int.currentStack_" + highDot.currentStack());
			writer.write("\n");
			
		}
		writer.flush();
		writer.close();
		System.out.println(fileStr + ".Net has been generated.");
	}

//## NET NAME ##########################################

	public String defaultName() {
		String defaultName = "";

		for (int gph: this.getGphSet()) {
			defaultName = defaultName + gph + "-";
		}

		return defaultName = defaultName + this.startStr();
		
	}

//## FILE TO NET #######################################

	public Map<Integer, Links> fileToNet(String filePath) throws IOException {

		Map<Integer, Links> newMap = new TreeMap<Integer, Links>();

		BufferedReader reader = new BufferedReader(new FileReader(filePath));
		String currentLine;
		while ((currentLine = reader.readLine()) != null) {
			if (currentLine.startsWith("LINK__")) {
				int currentKey = Integer.parseInt(currentLine.split("__")[1]);

				currentLine = reader.readLine();
				int currentiD = Integer.parseInt(currentLine.split("_")[1]);
				int currenttype = Integer.parseInt(currentLine.split("_")[3]);
				double currentGD = Double.parseDouble(currentLine.split("_")[5]);
				
				currentLine = reader.readLine();
				currentLine = reader.readLine();
				int lowiD = Integer.parseInt(currentLine.split("_")[1]);
				int lowtrack = Integer.parseInt(currentLine.split("_")[3]);
				int lowgph = Integer.parseInt(currentLine.split("_")[5]);
				int lowframe = Integer.parseInt(currentLine.split("_")[7]);

				currentLine = reader.readLine();
				double lowlat = Double.parseDouble(currentLine.split("_")[1]);
				double lowlon = Double.parseDouble(currentLine.split("_")[3]);
				double lowmodule = Double.parseDouble(currentLine.split("_")[5]);

				currentLine = reader.readLine();
				String lowSS = currentLine.split("_")[1];
				boolean lowIG = Boolean.parseBoolean(currentLine.split("_")[3]);
				int lowCS = Integer.parseInt(currentLine.split("_")[5]);

				Dots lowerDot = new Dots(lowiD, lowgph, lowtrack, lowframe, lowlat, lowlon, lowmodule, lowSS, lowIG, lowCS);

				currentLine = reader.readLine();
				currentLine = reader.readLine();
				int highiD = Integer.parseInt(currentLine.split("_")[1]);
				int hightrack = Integer.parseInt(currentLine.split("_")[3]);
				int highgph = Integer.parseInt(currentLine.split("_")[5]);
				int highframe = Integer.parseInt(currentLine.split("_")[7]);

				currentLine = reader.readLine();
				double highlat = Double.parseDouble(currentLine.split("_")[1]);
				double highlon = Double.parseDouble(currentLine.split("_")[3]);
				double highmodule = Double.parseDouble(currentLine.split("_")[5]);

				currentLine = reader.readLine();
				String highSS = currentLine.split("_")[1];
				boolean highIG = Boolean.parseBoolean(currentLine.split("_")[3]);
				int highCS = Integer.parseInt(currentLine.split("_")[5]);

				Dots higherDot = new Dots(highiD, highgph, hightrack, highframe, highlat, highlon, highmodule, highSS, highIG, highCS);

				Links newLink = new Links(currentiD, lowerDot, higherDot, currenttype, currentGD);
				newMap.put(currentKey, newLink);
			}
		}
		return newMap;
	}
}
