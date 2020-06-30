import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.ParseException;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class ScriptSize {
	public static double sizeGet (String [] args) throws ParseException {
		SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
		//System.out.println("ARGS[1] == " + args[1]);
		String startIn = args[1].substring(0,8);
		String endIn = args[1].substring(9,17);
		//System.out.println("STARTIN / ENDIN == " + startIn + " / " + endIn);
		Date startDate = dt.parse(startIn);
		Date endDate = dt.parse(endIn);	
		long periodDays = ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
		//System.out.println("PERIODDAYS == " + periodDays);
	
		double dayWeight = 0;
		String sizeStr = "";
		if (args[0].equals("cc")) {
			switch (args[3]) {
				case "F1280" :
					dayWeight = 51202;
					break;
				case "F640" : 
					dayWeight = 12800;
					break;
				case "F512" :
					dayWeight = 8192;
					break;
				case "F320" :
					dayWeight = 3200;
					break;
				case "F256" :
					dayWeight = 2048;
					break;
				case "F160" :
					dayWeight = 800;
					break;
				case "F128" :
					dayWeight = 512;
					break;
			}			
		} else {
			switch (args[3]) {
				case "F1280" :
					dayWeight = 102404;
					break;
				case "F640" : 
					dayWeight = 25600;
					break;
				case "F512" :
					dayWeight = 16384;
					break;
				case "F320" :
					dayWeight = 6400;
					break;
				case "F256" :
					dayWeight = 4096;
					break;
				case "F160" :
					dayWeight = 1600;
					break;
				case "F128" :
					dayWeight = 1024;
					break;
			}
		}
	
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
				
		double sizeGet = 4 + dayWeight * periodDays;
		if ((sizeGet / (1024)) < 1) {
			sizeStr = df.format(sizeGet) + " KB";
		} else if ((sizeGet / (1024*1024)) < 1) {
			sizeStr = df.format(sizeGet / 1024) + " MB";
		} else if ((sizeGet / (1024*1024*1024)) < 1) {
			sizeStr = df.format(sizeGet / (1024*1024)) + " GB";
		} else if ((sizeGet / (1024*1024*1024*1024)) < 1) {
			sizeStr = df.format(sizeGet / (1024*1024*1024)) + " TB";
		}
		
		System.out.println("Estimated download size: " + sizeStr);	
		//System.out.println("SIZEGET == " + Math.round(sizeGet));
		return sizeGet;
	}
	

	public static double sizeGetMute (String [] args) throws ParseException {
		SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
		//System.out.println("ARGS[1] == " + args[1]);
		String startIn = args[1].substring(0,8);
		String endIn = args[1].substring(9,17);
		//System.out.println("STARTIN / ENDIN == " + startIn + " / " + endIn);
		Date startDate = dt.parse(startIn);
		Date endDate = dt.parse(endIn);	
		long periodDays = ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
		//System.out.println("PERIODDAYS == " + periodDays);
	
		double dayWeight = 0;
		String sizeStr = "";
		if (args[0].equals("cc")) {
			switch (args[3]) {
				case "F1280" :
					dayWeight = 51202;
					break;
				case "F640" : 
					dayWeight = 12800;
					break;
				case "F512" :
					dayWeight = 8192;
					break;
				case "F320" :
					dayWeight = 3200;
					break;
				case "F256" :
					dayWeight = 2048;
					break;
				case "F160" :
					dayWeight = 800;
					break;
				case "F128" :
					dayWeight = 512;
					break;
			}
		} else {
			switch (args[3]) {
				case "F1280" :
					dayWeight = 102404;
					break;
				case "F640" : 
					dayWeight = 25600;
					break;
				case "F512" :
					dayWeight = 16384;
					break;
				case "F320" :
					dayWeight = 6400;
					break;
				case "F256" :
					dayWeight = 4096;
					break;
				case "F160" :
					dayWeight = 1600;
					break;
				case "F128" :
					dayWeight = 1024;
					break;
			}
		}
	
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
				
		double sizeGet = 4 + dayWeight * periodDays;
		if ((sizeGet / (1024)) < 1) {
			sizeStr = df.format(sizeGet) + " KB";
		} else if ((sizeGet / (1024*1024)) < 1) {
			sizeStr = df.format(sizeGet / 1024) + " MB";
		} else if ((sizeGet / (1024*1024*1024)) < 1) {
			sizeStr = df.format(sizeGet / (1024*1024)) + " GB";
		} else if ((sizeGet / (1024*1024*1024*1024)) < 1) {
			sizeStr = df.format(sizeGet / (1024*1024*1024)) + " TB";
		}
		
		//System.out.println("Estimated download size: " + sizeStr);	
		//System.out.println("SIZEGET == " + Math.round(sizeGet));
		return sizeGet;
	}


	public static String resGet (String args) {
		
		String resStr = "";
		switch (args) {
			case "F1280" :
				resStr = "5120x2560";
				break;
			case "F640" : 
				resStr = "2560x1280";
				break;
			case "F512" :
				resStr = "2048x1024";
				break;
			case "F320" :
				resStr = "1280x640";
				break;
			case "F256" :
				resStr = "1024x512";
				break;
			case "F160" :
				resStr = "640x320";
				break;
			case "F128" :
				resStr = "512x256";
				break;
			default : 
				resStr = "UNKNOWN";
		}
		
		return resStr;
	}
}
