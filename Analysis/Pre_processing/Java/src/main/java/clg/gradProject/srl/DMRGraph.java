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
	//private static Map ARGSCORESMAP;
	//private static Map ACTIONSCOREMAP;
	public Map<String, ArgumentBuilder> ArgsHash;
	public ArrayList<FrameBuilder> ActionFrames;

	DMRGraph(ArrayList<FrameBuilder> frames) {
		ArgsHash = new HashMap<String, ArgumentBuilder>();
		ActionFrames = frames;

		//ARGSCORESMAP = new HashMap<String, Double>();
		//ACTIONSCOREMAP = new HashMap<String, Double>();

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

				for (ArgumentBuilder argIt : FrameArguments) {
					String word;
					word = argIt.word;

					if (ArgsHash.get(word) != null) {
						argIt = ArgsHash.get(word); // it is used we checked before :D :D

					} else {
						ArgsHash.put(word, argIt);

					}
					ArrayList list = argIt.relatedFrames.get(ArgPair.getKey());
					if(list == null) {
						argIt.relatedFrames.put(ArgPair.getKey(), new ArrayList<FrameBuilder>());
						list = argIt.relatedFrames.get(ArgPair.getKey());
					}
					list.add(ActionFrame);
				}
			}
		}
	}
}