import java.util.TreeMap;
import java.util.Map;
import java.util.Date;
import java.util.Set;
import java.text.ParseException;

public class LongTracks {
	int gph;
	int iD;
	String startStr;

	private int currentStack;	

	Map<Integer, Dots> dotsMap = new TreeMap<Integer, Dots>();

	public LongTracks() {
	
	}

	public LongTracks(int gphCon, int iDCon, String startStrCon, int stackCon) {
		gph = gphCon;
		iD = iDCon;
		startStr = startStrCon;
		setCurrentStack(stackCon);
	}

	public void addDot(int frameCon, Dots dotCon) {
		this.dotsMap.put(frameCon, dotCon);
	}

	public Set<Integer> getKeySet() {
		return this.dotsMap.keySet();
	}

	public Dots getDot(int key) {
		return dotsMap.get(key);
	}

	public int mapSize() {
		return this.dotsMap.keySet().size();
	}

	public boolean contains(int key) {
		return this.dotsMap.containsKey(key);
	}

	public int currentStack() {
		return currentStack;
	}

	public void setCurrentStack(int stack) {
		currentStack = stack;
	}
}
