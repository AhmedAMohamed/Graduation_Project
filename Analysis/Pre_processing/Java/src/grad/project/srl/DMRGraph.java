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

	public Map<String, ArgumentBuilder> ArgsHash;
	public ArrayList<FrameBuilder> ActionFrames;

	DMRGraph(ArrayList<FrameBuilder> frames) {
		ArgsHash = new HashMap();
		ActionFrames = frames;
	}

	public void createGraph() {

		Iterator<FrameBuilder> ActionFramesIterator = ActionFrames.iterator();
		while (ActionFramesIterator.hasNext()) {
			// Iterate over all the pairs in the hashmap of ActionArgs
			FrameBuilder currentActionFrame = ActionFramesIterator.next();
			for (Map.Entry<String, ArrayList<ArgumentBuilder>> ArgPair : currentActionFrame.arguments.entrySet()) {
				ArrayList<ArgumentBuilder> FrameArguments = ArgPair.getValue();

				for (ArgumentBuilder currentArg : FrameArguments) {
					String wordString;
					wordString = currentArg.word;

					if (ArgsHash.get(wordString) != null) {
						currentArg = ArgsHash.get(wordString);

					} else {
						ArgsHash.put(wordString, currentArg);
					}
				}

			}
		}
	}
}