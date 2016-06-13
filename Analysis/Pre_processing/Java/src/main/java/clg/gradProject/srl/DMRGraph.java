package clg.gradProject.srl;

import clg.gradProject.ArgumentBuilder;
import clg.gradProject.FrameBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class DMRGraph {
	private Map<String, Integer> scoreRoot = new HashMap<String, Integer>();
	public Map<String, ArgumentBuilder> ArgsHash;
	public ArrayList<FrameBuilder> ActionFrames;

	DMRGraph(ArrayList<FrameBuilder> frames) {
		ArgsHash = new HashMap<String, ArgumentBuilder>();
		ActionFrames = frames;
		System.out.println("Args hash: "  + ArgsHash);
		System.out.println("Action frames: : "  + ActionFrames);
	}

	public void createGraph() {

		Iterator<FrameBuilder> ActionFramesIterator = ActionFrames.iterator();
		while (ActionFramesIterator.hasNext()) {
			// Iterate over all the pairs in the hashmap of ActionArgs
			FrameBuilder ActionFrame = ActionFramesIterator.next();
			for (Entry<String, ArrayList<ArgumentBuilder>> ArgPair : ActionFrame.arguments.entrySet()) {
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
				}
			}
		}
	}
}