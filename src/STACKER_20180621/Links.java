public class Links {
	int iD;
	Dots lowDot;
	Dots highDot;
	//Date date;
	int type; //0:ghostLink; 1: trueLink; 2:falseLink;
	static private double ghostDistance;
	
	public Links() {

	}

	public Links(Dots lowCon, Dots highCon) {
		lowDot = lowCon;
		highDot = highCon;
	}

	public Links(int idCon, Dots lowCon, Dots highCon, int typeCon, double ghostCon) {
		iD = idCon;
		lowDot = lowCon;
		highDot = highCon;
		type = typeCon;
		setGhostDistance(ghostCon);
	}

	public void setGhostDistance(double distCon) {
		ghostDistance = distCon;
	}

	public double ghostDistance() {
		return ghostDistance;
	}

	public double getDistance() {
		double lat1 = lowDot.lat;
		double lat2 = highDot.lat;
		double lng1 = lowDot.lon;
		double lng2 = highDot.lon;
		double earthRadius = 6371000; //meters
		double dLat = Math.toRadians(lat2-lat1);
		double dLng = Math.toRadians(lng2-lng1);
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
		double dist =/* (double)*/ (earthRadius * c) / 1000;
		if (type == 0) {
			dist = ghostDistance;
		}
		return dist;
	}
}
