import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.ArrayList;

public class Dots {
	int iD;
	int track;
	int gph;
	int frame;
	private int currentStack;
	double lat;
	double lon;
	double module;
	String startStr;
	boolean isGhost;
	String origin;

	public Dots () {

	}

	public Dots (int gphCon, int trackCon, int frameCon, double lonCon, double latCon, double moduleCon, String startStrCon) {
		iD = 0;
		track = trackCon;
		gph = gphCon;
		frame = frameCon;
		lon = lonCon;
		lat = latCon;
		module = moduleCon;
		startStr = startStrCon;
	}
	
	public Dots (int iDCon, int gphCon, int trackCon, int frameCon, double latCon, double lonCon, double moduleCon, String startStrCon, boolean isGhostCon, int stackCon) {
		iD = iDCon;
		track = trackCon;
		gph = gphCon;
		frame = frameCon;
		lon = lonCon;
		lat = latCon;
		module = moduleCon;
		startStr = startStrCon;
		isGhost = isGhostCon;
		setCurrentStack(stackCon);
	}

	public Dots (int iDCon, int gphCon, int trackCon, int frameCon, double latCon, double lonCon, double moduleCon, String startStrCon, boolean isGhostCon, int stackCon, String originCon) {
		iD = iDCon;
		track = trackCon;
		gph = gphCon;
		frame = frameCon;
		lon = lonCon;
		lat = latCon;
		module = moduleCon;
		startStr = startStrCon;
		isGhost = isGhostCon;
		setCurrentStack(stackCon);
		origin = originCon;
	}

	public void setCurrentStack(int stackCon) {
		currentStack = stackCon;
	}

	public int currentStack() {
		return currentStack;
	}
	
	public void setStartString (String startString) {
		startStr = startString;
	}
	
	public void setNewFrame(String newStartString) throws ParseException {
		SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmm");
		long actualLong = this.getDate().getTime();
		
		long newStartLong = dt.parse(newStartString + "0000").getTime();
		
		long newLong = actualLong - newStartLong;
		long div = 1000*60*60*6;
		
		newLong = newLong / div;
		int setNewFrame = (int) newLong + 1;
		
		this.frame = setNewFrame;		
	}
	
	public Date getDate () throws ParseException {
		SimpleDateFormat dt = new SimpleDateFormat("yyyyMMddHHmm");
		Date startDate = dt.parse(startStr + "0000");
		long a = 1000*60*60*6;
		long b = frame - 1;
		Date date = new Date(startDate.getTime() + a * b);
		return date;
	}
	
	public void setOrigin (String startStr, int gph, int track, int frame) {
		origin = startStr + "-" + gph + "-" + track + "-" + frame;
	}
}
