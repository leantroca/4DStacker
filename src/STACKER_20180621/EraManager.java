import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DecimalFormat;
import java.math.RoundingMode;

public class EraManager {
	public static void main(String [] args) throws ParseException {
		
		/*if (args.length < 1) {
		System.out.println("java Pythoneer [Parameter \"vo,ge,wu\"] [PeriodDate \"20170101-20171231\"] [Geopotential \"100,500,850\"] [Resolution \"F640\"]");
		System.exit(0);
		}*/

		String [] paramArray = args[0].split(",");
		String [] periodArray = args[1].split(",");
		String [] geoArray = args[2].split(",");
		String [] resArray = args[3].split(",");
		String pythonStr = "";
		double downAll = 0;
		int downCount = 0;
		String downStr = "";
		
		try{
			File bashScript = new File(args[4] + ".sh");
			FileWriter writer = new FileWriter(bashScript);
	
			for (String paramEl : paramArray) {
				for (String periodEl : periodArray) {
					for (String geoEl : geoArray) {
						for (String resEl : resArray) {
							String [] pythonArray = {paramEl,periodEl,geoEl,resEl};
							pythonStr = PythonScript.pythonGet(pythonArray);
							downAll = downAll + ScriptSize.sizeGet(pythonArray);
							downCount = downCount + 1;
							System.out.println("**********************************************\n");
							writer.write("SCRIPTSIZE=" + Math.round(ScriptSize.sizeGetMute(pythonArray) * 1000) + "\n");
							writer.write("DOWNSIZE=0\n");
							writer.write("while [ $DOWNSIZE -lt $SCRIPTSIZE ] ; do\n");
							writer.write("	echo \'Now downloading " + pythonStr + ".grib...\'\n" );
							writer.write("	date\n");
							//writer.write("	echo \'SCRIPTSIZEbefore = \' $SCRIPTSIZE\n");
							//writer.write("	echo \'DOWNSIZEbefore = \' $DOWNSIZE\n");
							writer.write("	time python GET_" + pythonStr + ".py >> LOG_" + pythonStr + "\n");
							writer.write("	if [ -f " + pythonStr + ".grib ]; then DOWNSIZE=$(wc -c <" + pythonStr + ".grib); else DOWNSIZE=0; fi\n");
							writer.write("	if [ $DOWNSIZE -lt $SCRIPTSIZE ]; then echo \'Download incomplete. Restarting...\'; else echo \'Download complete!\'; fi\n");
							//writer.write("	echo \'SCRIPTSIZEafter = \' $SCRIPTSIZE\n");
							//writer.write("	echo \'DOWNSIZEafter = \' $DOWNSIZE\n");
							writer.write("done\n");
							writer.write("echo\n");
							writer.write("cdo -f nc copy " + pythonStr + ".grib NCs/" + pythonStr + ".nc\n");
							//writer.write("ncdump NCs/" + pythonStr + ".nc > CDLs/" + pythonStr + ".cdl\n"); //Takes too much space.
							writer.write("mv " + pythonStr + ".grib GRIBs/" + pythonStr + ".grib\n");
							writer.write("mv LOG_" + pythonStr + " LOGs/LOG_" + pythonStr + "\n");
							writer.write("rm GET_" + pythonStr + ".py\n");
							writer.write("echo \"###################################################################\"\n");
							writer.write("echo\n\n");

						}
					}
				}
			}

			writer.flush();
			writer.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	
		DecimalFormat df = new DecimalFormat("#.##");
		df.setRoundingMode(RoundingMode.CEILING);
		
		if ((downAll / (1024)) < 1) {
			downStr = df.format(downAll) + " KB";
		} else if ((downAll / (1024*1024)) < 1) {
			downStr = df.format(downAll / 1024) + " MB";
		} else if ((downAll / (1024*1024*1024)) < 1) {
			downStr = df.format(downAll / (1024*1024)) + " GB";
		} else if ((downAll / (1024*1024*1024*1024)) < 1) {
			downStr = df.format(downAll / (1024*1024*1024)) + " TB";
		}

		System.out.println("\nESTIMATED TOTAL DOWNLOAD: " + downStr + " in " + downCount + " files.");
		
	}
}
