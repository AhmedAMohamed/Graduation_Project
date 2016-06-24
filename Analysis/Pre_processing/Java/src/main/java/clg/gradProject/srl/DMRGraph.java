package clg.gradProject.srl;

import clg.gradProject.ArgumentBuilder;
import clg.gradProject.FrameBuilder;
import clg.gradProject.Node;
import clg.gradProject.SEPTBuilder;

import javax.xml.parsers.FactoryConfigurationError;
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
			ARGSCORES.put("A"+i, 1.0);
			ACTIONSCORE.put("A"+i, 1.5);
		}
	}

	DMRGraph (DMRGraph pre_graph,  ArrayList<ArrayList<FrameBuilder>> clusters) {
		this.ArgsHash = pre_graph.ArgsHash;
		ArrayList<FrameBuilder> nextLevel = getMaxCluster(clusters);
		FrameBuilder nextLevelFrame = new FrameBuilder("Level two", 0, 0, "", "Level two", 0);
		nextLevelFrame.nextLevel = nextLevel;
		this.ActionFrames = new ArrayList();
		ActionFrames.add(nextLevelFrame);
		for(FrameBuilder frame : pre_graph.ActionFrames) {
			if(!nextLevel.contains(frame)) {
				ActionFrames.add(frame);
			}
		}
	}

	private ArrayList<FrameBuilder> getMaxCluster(ArrayList<ArrayList<FrameBuilder>> clusters) {
		int maxClusterIndex = 0;
		int index = 0;
		int maxSize = 0;
		for (ArrayList<FrameBuilder> cluster : clusters) {
			if(cluster.size() > maxSize) {
				maxSize = cluster.size();
				maxClusterIndex = index;
			}
			index++;
		}

		return clusters.get(maxClusterIndex);
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

	public void setScores(ArrayList<FrameBuilder> frames) {
		setArgScores();
		setActionScores(frames);
	}

	private void setActionScores(ArrayList<FrameBuilder> frames) {
		for (FrameBuilder frame : frames) {
			double score = 0;
			for (String argType : frame.arguments.keySet()) {
				ArrayList<ArgumentBuilder> argObjects = frame.arguments.get(argType);
				for (ArgumentBuilder arg : argObjects) {
					score += 1.5 * arg.score;
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
				score += 5 * frames.size();
			}
			argObject.score = score;
		}
	}

	public void addLinkingActionFrames(int totalSentenceNumber) throws CloneNotSupportedException {
		ArrayList<Integer> found = new ArrayList();
		ArrayList<Integer> noFrames = new ArrayList();

		for (FrameBuilder frame: this.ActionFrames) {
			found.add(frame.sentenceNumber);
		}

		for (int i = 1; i < totalSentenceNumber; i++) {
			if (! found.contains(i)) {
				noFrames.add(i);
			}
		}

		for (Integer sentNumber : noFrames) {
			Node sentRoot = SEPTBuilder.getSentenceByIndex(sentNumber);

			Node verbNode = SEPTBuilder.findNodeByPOS(sentRoot, "VB", 1, sentRoot.sentIndex);
			if (verbNode != null) {
				FrameBuilder frame = new FrameBuilder(verbNode.parseTreeNode.pennString(), verbNode.sentIndex, verbNode.wordIndex, verbNode.parseTreeNode.value(), verbNode.parseTreeNode.pennString(), 3);

				Node subjectArgNode = SEPTBuilder.findNodeByPOS(sentRoot, "NP", 0, sentRoot.sentIndex);
				if (subjectArgNode != null) {
					ArrayList<ArgumentBuilder> a0Args = addCustomeArgs(subjectArgNode, true);
					frame.arguments.put("A0", a0Args);

					Node objectArgNode = SEPTBuilder.findNodeByPOS(sentRoot, "NP", 2, sentRoot.sentIndex);
					if (objectArgNode != null) {
						ArrayList<ArgumentBuilder> a1Args = addCustomeArgs(objectArgNode, false);
						frame.arguments.put("A1", a1Args);
						this.ActionFrames.add(frame);
					}
				}
			}
		}

	}

	private ArrayList<ArgumentBuilder> addCustomeArgs(Node argNode, boolean type) {
		String[] types = new String[2];
		if(type) {
			types[0] = "A0";
		}
		else {
			types[1] = "A1";
		}
		ArrayList<ArgumentBuilder> args  = new ArrayList();
		String value = argNode.parseTreeNode.pennString();
		if (value.contains("(CC and)")) {
			String[] values = value.split("(CC and)");
			ArgumentBuilder a = new ArgumentBuilder(argNode.sentIndex, argNode.wordIndex, argNode.parseTreeNode.nodeString(), values[0], types, types[0]);
			args.add(a);

			ArgumentBuilder a2 = new ArgumentBuilder(argNode.sentIndex, argNode.wordIndex, argNode.parseTreeNode.nodeString(), values[1], types, types[0]);
			args.add(a2);
		}
		else {
			ArgumentBuilder a = new ArgumentBuilder(argNode.sentIndex, argNode.wordIndex, argNode.parseTreeNode.nodeString(), value, types, types[0]);
			args.add(a);
		}
		return args;
	}

}