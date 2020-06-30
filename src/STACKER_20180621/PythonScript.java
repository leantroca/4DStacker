import java.io.*;
import java.text.ParseException;

public class PythonScript {
    public static String pythonGet(String[] args) throws ParseException {

	/*if (args.length < 1) {
		System.out.println("java Pythoneer [Parameter \"vo\"] [StartDate \"20170101\"] [EndDate \"20170331\"] [Geopotential \"850\"] [Resolution \"F640\"]");
		System.exit(0);
	}*/

	StringBuffer startDateStr = new StringBuffer(args[1]);
	startDateStr.delete(8,args[1].length());
	startDateStr.insert(4,"-");
	startDateStr.insert(7,"-");
	
	StringBuffer endDateStr = new StringBuffer(args[1]);
	endDateStr.delete(0,9);
	endDateStr.insert(4,"-");
	endDateStr.insert(7,"-");
	
	//System.out.println(startDateStr + " " + endDateStr + " " + args[1].length());

	String paramNum = "";
	String paramStr = "";
	switch (args[0]) {
		case "vo" :
			paramNum = "138.128";
			paramStr = "Vorticity (relative)";
			break;
		case "ge" :
			paramNum = "129.128";
			paramStr = "Geopotential";
			break;
		case "te" :
			paramNum = "130.128";
			paramStr = "Temperature";
			break;
		case "wu" :
			paramNum = "131.128";
			paramStr = "U component of wind";
			break;
		case "wv" :
			paramNum = "132.128";
			paramStr = "V component of wind";
			break;
		case "cc" :
			paramNum = "248.128";
			paramStr = "Fraction of cloud cover";
			break;
		case "pv" :
			paramNum = "60.128";
			paramStr = "Potential vorticity";
			break;
		case "rh" :
			paramNum = "157.128";
			paramStr = "Relative humidity";
			break;
		case "sh" :
			paramNum = "133.128";
			paramStr = "Specific humidity";
			break;
	}
	
	//String formatStr = args[3];
	String resStr = ScriptSize.resGet(args[3]);

	System.out.println(paramStr + " at " + args[2] + "hPa,\nfor the period from " + startDateStr + " to " + endDateStr + ",\nin the resolution " + resStr + " (" + args[3] + ") requested...\n");

	try{
		File pythonScript = new File("GET_" + args[0] + "_" + args[1] + "_" + args[2] + "_" + args[3] + ".py");
		FileWriter writer = new FileWriter(pythonScript);
		
		writer.write("#!/usr/bin/env python\n");
		writer.write("from ecmwfapi import ECMWFDataServer\n");
		writer.write("server = ECMWFDataServer()\n");
		writer.write("server.retrieve({\n");
		writer.write("    \"class\": \"ei\",\n");
		writer.write("    \"dataset\": \"interim\",\n");
		writer.write("    \"date\": \"" + startDateStr + "/to/" + endDateStr + "\",\n");
		writer.write("    \"expver\": \"1\",\n");
		writer.write("    \"grid\": \"" + args[3] + "\",\n");
		writer.write("    \"levelist\": \"" + args[2] + "\",\n");
		writer.write("    \"levtype\": \"pl\",\n");
		writer.write("    \"param\": \"" + paramNum + "\",\n");
		writer.write("    \"step\": \"0\",\n");
		writer.write("    \"stream\": \"oper\",\n");
		writer.write("    \"time\": \"00:00:00/06:00:00/12:00:00/18:00:00\",\n");
		writer.write("    \"type\": \"an\",\n");
		writer.write("    \"target\": \"" + args[0] + "_" + args[1] + "_" + args[2] + "_" + args[3] + ".grib" + "\",\n");
		writer.write("})\n");

		writer.flush();
		writer.close();
	}catch(IOException e){
		e.printStackTrace();
	}

	System.out.println("The file " + "GET_" + args[0] + "_" + args[1] + "_" + args[2] + "_" + args[3] + ".py" + " has been successfully created!");
	String pythonStr = args[0] + "_" + args[1] + "_" + args[2] + "_" + args[3];

	/*SimpleDateFormat dt = new SimpleDateFormat("yyyyMMdd");
	Date startDate = dt.parse(args[1]);
	Date endDate = dt.parse(args[2]);	
	long periodDays = ((endDate.getTime() - startDate.getTime()) / (1000 * 60 * 60 * 24)) + 1;
	
	double dayWeight = 0;
	String sizeStr = "";
	switch (args[4]) {
		case "F640" : 
			dayWeight = 25604;
			break;
		case "F128" :
			dayWeight = 1028;
			break;
		default : 
			System.out.println("Unknown resolution format");
	}
	
	double sizeGet = dayWeight * periodDays;
	if ((sizeGet / (1024)) < 1) {
		sizeStr = (sizeGet) + " KB";
	} else if ((sizeGet / (1024*1024)) < 1) {
		sizeStr = Math.round(sizeGet / 1024) + " MB";
	} else if ((sizeGet / (1024*1024*1024)) < 1) {
		sizeStr = Math.round(sizeGet / (1024*1024)) + " GB";
	} else if ((sizeGet / (1024*1024*1024*1024)) < 1) {
		sizeStr = Math.round(sizeGet / (1024*1024*1024)) + " TB";
	} //else {
	//	sizeStr = (sizeGet / (1024*1024*1024*1024)) + " TB";
	//}
	
	System.out.println("Estimated download: " + sizeStr);*/
	
	return pythonStr;
    }
}


