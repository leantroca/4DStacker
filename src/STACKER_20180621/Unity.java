import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

public class Unity {
	public static void main(String[] args) throws IOException, ParseException {

		if (args.length < 1) {
			System.out.println("This program translates external files into 3DStacker format files.");
			System.out.println("Unity [trackPath.kho] [gph] [startDate] || days [filter]");
			System.out.println("Unity [lowerPath.Collection] [higherPath.Collection] [currentPath.Net] [lpsolPath.r]");
			System.exit(0);
		} else if (args.length == 3) {
			int gph = Integer.parseInt(args[1]);
			Collection thisCollection = new Collection(args[0], gph, args[2]);

			thisCollection.toFile(thisCollection.defaultName());
			System.exit(0);
		} else if (args.length == 4) {
			Collection lowerCollection = new Collection(args[0]);
			Collection higherCollection = new Collection(args[1]);
			Net thisNet = new Net(args[2]);

			Collection thisCollection = new Collection(lowerCollection, higherCollection, thisNet, args[3]);
			thisCollection.toFile(thisCollection.defaultName());
			System.exit(0);
		} else if (Arrays.asList(args).contains("days")) {
			int gph = Integer.parseInt(args[1]);
			int days = Integer.parseInt(args[4]);
			Collection thisCollection = new Collection(args[0], gph, args[2], days);

			thisCollection.toFile(thisCollection.defaultName() + "-df" + days);
			System.exit(0);
		}
		
	}
}
