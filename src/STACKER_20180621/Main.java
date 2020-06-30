import java.util.Map;
import java.io.IOException;
import java.text.ParseException;

public class Main {
	
	public static void main(String[] args) throws IOException, ParseException {

		if (args.length < 3) {
			System.out.println("java Main [lower.Collection] [higher.Collection] [km.Thresh]");
			System.exit(0);
		}

		Collection lowerCollection = new Collection(args[0]);
//		System.out.println("lowerCollection.size : " + lowerCollection.getKeySet().size());
//		lowerCollection.toFile(lowerGph + "");

//		int higherGph = Integer.parseInt(args[4]);
		Collection higherCollection = new Collection(args[1]);
//		System.out.println("higherCollection.size : " + higherCollection.getKeySet().size());
//		lowerCollection.toFile(higherGph + "");

		int thresh = Integer.parseInt(args[2]);
		int linkCounter = 0;

		Net trueNet = new Net(Parser.trueLinkThis(lowerCollection, higherCollection, thresh, linkCounter));
//		String trueStr = "";
//		for (int gph: lowerCollection.getGphSet()) {
//			trueStr = trueStr + gph + "-";
//		}
//		trueStr = trueStr + higherCollection.closestGph(0) + "-true";
//		System.out.println("trueNet.size : " + trueNet.getKeySet().size());
//		trueNet.toFile(trueStr);

		linkCounter = trueNet.getKeySet().size();
		Net falseNet = new Net(Parser.falseLinkThis(lowerCollection, higherCollection, trueNet, linkCounter));
//		String falseStr = "";
//		for (int gph: lowerCollection.getGphSet()) {
//			falseStr = falseStr + gph + "-";
//		}
//		falseStr = falseStr + higherCollection.closestGph(0) + "-false";
//		System.out.println("falseNet.size : " + falseNet.getKeySet().size());
//		falseNet.toFile(falseStr);

		linkCounter = trueNet.getKeySet().size() + falseNet.getKeySet().size();
		Net ghostNet = new Net(Parser.ghostLinkThis(lowerCollection, higherCollection, thresh, linkCounter));
//		String ghostStr = "";
//		for (int gph: lowerCollection.getGphSet()) {
//			ghostStr = ghostStr + gph + "-";
//		}
//		ghostStr = ghostStr + higherCollection.closestGph(0) + "-ghost";
//		System.out.println("ghostNet.size : " + ghostNet.getKeySet().size());
//		ghostNet.toFile(ghostStr);

		Net fullNet = new Net();
//		for (int key: ghostNet.getKeySet()) {
//			System.out.println("GHOST " + key);
//			fullNet.addLink(key, ghostNet.getLink(key));
//		}
//		for (int key: trueNet.getKeySet()) {
//			System.out.println("TRUE " + key);
//			fullNet.addLink(key, trueNet.getLink(key));
//		}
//		for (int key: falseNet.getKeySet()) {
//			System.out.println("FALSE " + key);
//			fullNet.addLink(key, falseNet.getLink(key));
//		}

		fullNet.linksMap.putAll(trueNet.linksMap);
		fullNet.linksMap.putAll(falseNet.linksMap);
		fullNet.linksMap.putAll(ghostNet.linksMap);
//		String fullStr = "";
//		for (int gph: lowerCollection.getGphSet()) {
//			fullStr = fullStr + gph + "-";
//		}
//		for (int gph: higherCollection.getGphSet()) {
//			fullStr = fullStr + gph + "-";
//		}
//		fullStr = fullStr + lowerCollection.startStr();
		System.out.println("fullNet.size : " + fullNet.getKeySet().size());
		fullNet.toFile();

		Parser.toCSV("C", Parser.cMatrix(fullNet));

		Parser.toCSV("MBT", Parser.mbtMatrix(fullNet));
		Parser.toCSV("MBTdir", Parser.mbtDir(Parser.mbtMatrix(fullNet)));
		Parser.toCSV("MBTrhs", Parser.mbtRhs(Parser.mbtMatrix(fullNet)));

		Parser.toCSV("MNBT", Parser.mnbtMatrix(fullNet, lowerCollection));
		Parser.toCSV("MNBTdir", Parser.mnbtDir(Parser.mnbtMatrix(fullNet, lowerCollection)));
		Parser.toCSV("MNBTrhs", Parser.mnbtRhs(Parser.mnbtMatrix(fullNet, lowerCollection)));

		Parser.toCSV("DRLT", Parser.drltMatrix(fullNet));
		Parser.toCSV("DRLTdir", Parser.drltDir(Parser.drltMatrix(fullNet)));
		Parser.toCSV("DRLTrhs", Parser.drltRhs(Parser.drltMatrix(fullNet)));

		Parser.toCSV("DRHT", Parser.drhtMatrix(fullNet));
		Parser.toCSV("DRHTdir", Parser.drhtDir(Parser.drhtMatrix(fullNet)));
		Parser.toCSV("DRHTrhs", Parser.drhtRhs(Parser.drhtMatrix(fullNet)));	

	}		
}
