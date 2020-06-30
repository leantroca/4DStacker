import java.util.TreeMap;
import java.util.Map;
import java.util.Set;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class Stacks {
	
	int iD;
	String startStr;
	Map<Integer, LongTracks> ltMap = new TreeMap<Integer, LongTracks>();

	public Stacks(int iDCon, String startStrCon) {
		iD = iDCon;
		startStr = startStrCon;
	}
	
	public void addLt(int gphCon, LongTracks ltCon) {
		this.ltMap.put(gphCon, ltCon);
	}

	public void addDot(int gphCon, int frameCon, Dots dotCon) {
		LongTracks thisLt = this.getLt(gphCon);
		thisLt.addDot(frameCon, dotCon);
		this.addLt(gphCon, thisLt);
	}

	public Set<Integer> getKeySet() {
		return this.ltMap.keySet();
	}

	public LongTracks getLt(int key) {
		return this.ltMap.get(key);
	}

	public int mapSize() {
		return this.ltMap.keySet().size();
	}

	public void addDot(int gphCon, Dots dotCon) {
		LongTracks currentLongTrack = this.getLt(gphCon);
		currentLongTrack.addDot(dotCon.frame, dotCon);
		this.addLt(gphCon, currentLongTrack);
	}

	public boolean contains(int key) {
		return this.ltMap.containsKey(key);
	}
	
	public Dots firstDot () throws ParseException {
		Dots firstDot = new Dots();
		Date firstDate = new Date();
		
		for (int ltKey: this.ltMap.keySet()) {
			for (int dKey: this.ltMap.get(ltKey).getKeySet()) {
				if (this.ltMap.get(ltKey).getDot(dKey).getDate().before(firstDate)) {
					firstDot = this.ltMap.get(ltKey).getDot(dKey);
					firstDate = this.ltMap.get(ltKey).getDot(dKey).getDate();
				}
			}
		}
		return firstDot;
	}
	
	public Dots lastDot () throws ParseException {
		Dots lastDot = new Dots();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		Date lastDate = sdf.parse("01/01/1800");
		
		for (int ltKey: this.ltMap.keySet()) {
			for (int dKey: this.ltMap.get(ltKey).getKeySet()) {
				if (this.ltMap.get(ltKey).getDot(dKey).getDate().after(lastDate)) {
					lastDot = this.ltMap.get(ltKey).getDot(dKey);
					lastDate = this.ltMap.get(ltKey).getDot(dKey).getDate();
				}
			}
		}
		return lastDot;
	}
	
	public int dotCounter () {
		int dotCounter = 0;
		
		for (int ltKey: this.ltMap.keySet()) {
			for (int dKey: this.ltMap.get(ltKey).getKeySet()) {
				dotCounter++;
			}
		}
		return dotCounter;
	}
	
	public int dayCounter () throws ParseException {
		return (this.lastDot().frame - this.firstDot().frame) / 4;
	}
}
