package clg.gradProject.srl;

import clg.gradProject.ArgumentBuilder;
import clg.gradProject.FrameBuilder;

import java.util.*;
import java.util.Map.Entry;

public class DMRGraph {

	private static Map ARGSCORES;
	private static Map ACTIONSCORE;
	public Map<String, ArgumentBuilder> ArgsHash;
	public ArrayList<FrameBuilder> ActionFrames;

	DMRGraph(ArrayList<FrameBuilder> frames) {
		ArgsHash = new HashMap();
		ActionFrames = frames;

		ARGSCORES = new HashMap<String, Double>();
		ACTIONSCORE = new HashMap<String, Double>();
		for (int i = 0; i < 10; i++) {
			ARGSCORES.put("A"+i, new Random().nextDouble());
			ACTIONSCORE.put("A"+i, 1.5);
		}
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
						argIt.relatedFrames.put(ArgPair.getKey(), new ArrayList<String>());
						list = argIt.relatedFrames.get(ArgPair.getKey());
					}
					list.add(ActionFrame.frameID);
				}
			}
		}
	}

	public void setScores() {
		setArgScores();
		setActionScores();
	}

	private void setActionScores() {
		for (FrameBuilder frame : ActionFrames) {
			double score = 0;
			for (String argType : frame.arguments.keySet()) {
				ArrayList<ArgumentBuilder> argObjects = frame.arguments.get(argType);
				for (ArgumentBuilder arg : argObjects) {
					score += (double)ACTIONSCORE.get(argType) * arg.score;
				}
			}
			frame.score = score;
		}
	}

	private void setArgScores() {
		for(String argWord : ArgsHash.keySet()) {
			ArgumentBuilder argObject = ArgsHash.get(argWord);
			double score = 0;
			for(String argType : argObject.relatedFrames.keySet()) {
				ArrayList<String> frames = argObject.relatedFrames.get(argType);
				score += (double)ARGSCORES.get(argType) * frames.size();
			}
			argObject.score = score;
		}
	}
}