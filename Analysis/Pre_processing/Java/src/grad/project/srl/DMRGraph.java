import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DMRGraph {
	private Map<String, Integer> scoreRoot;
	private Map<String, Argument> ArgsHash;
	private ArrayList<Frame> ActionFrames;

	DMRGraph(ArrayList<Frame> frames) {
		ArgsHash = new HashMap<String, Argument>();
		ActionFrames = frames;

	}

	public ArrayList<String> createGraph() {

		Iterator<Frame> ActionFramesIterator = ActionFrames.iterator();
		while (ActionFramesIterator.hasNext()) {
			// Iterate over all the pairs in the hashmap of ActionArgs
			Frame ActionFrame = ActionFramesIterator.next();
			for (Map.Entry<String, ArrayList<Argument>> ArgPair : ActionFrame.arguments.entrySet()) {
				ArrayList<Argument> FrameArguments = ArgPair.getValue();

				for (Argument Arg : FrameArguments) {
					String Word;
					// Check if the Argument has a coref
					/*
					 * if(Arg.Coref!=null){ Word=Arg.Coref; } else {
					 * 
					 * Word=Arg.word; }
					 */
					Word = Arg.word;

					if (ArgsHash.get(Word) != null) {
						Arg = ArgsHash.get(Word);

					} else {
						ArgsHash.put(Word, Arg);

					}
					
					ArgsHash.get(Word).argumentType = null;
					
					if (scoreRoot.containsKey(Word)) {
						scoreRoot.put(Word, 1);
					} else {
						scoreRoot.put(Word, scoreRoot.get(Word) + 1);
					}

				}
				
			}
		}
		

		ArrayList<String> root = new ArrayList<String>();
		int maxScore = Collections.max(scoreRoot.values());
		Iterator<Entry<String, Integer>> rootIterator = scoreRoot.entrySet().iterator();
		while (rootIterator.hasNext()) {
			Entry<String, Integer> rootPair = rootIterator.next();
			if (rootPair.getValue() == maxScore) {
				root.add(rootPair.getKey());
			}
		}
		return root;
		
	}

}