package grad.project.srl;

import grad.project.ArgumentBuilder;
import grad.project.FrameBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DMRGraph {
	private Map<String, Integer> scoreRoot;
	public Map<String, ArgumentBuilder> ArgsHash;
	public ArrayList<FrameBuilder> ActionFrames;

	DMRGraph(ArrayList<FrameBuilder> frames) {
		ArgsHash = new HashMap<String, ArgumentBuilder>();
		ActionFrames = frames;
	}

	public ArrayList<String> createGraph() {

		Iterator<FrameBuilder> ActionFramesIterator = ActionFrames.iterator();
		while (ActionFramesIterator.hasNext()) {
			// Iterate over all the pairs in the hashmap of ActionArgs
			FrameBuilder ActionFrame = ActionFramesIterator.next();
			for (Map.Entry<String, ArrayList<ArgumentBuilder>> ArgPair : ActionFrame.arguments.entrySet()) {
				ArrayList<ArgumentBuilder> FrameArguments = ArgPair.getValue();

				for (ArgumentBuilder Arg : FrameArguments) {
					String Word;
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