package grad.project;

import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;

import java.util.*;

/**
 * Created by Khaled on 2/12/2015.
 */

public class API {

	public static List<Node> tree;
	
	public static Annotation annotate(String Sentence) {
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, parse, dcoref");
		props.put("dcoref.score", true);
		props.put("dcoref.postprocessing", true);
		props.put("dcoref.maxdist", "-1");
		props.put("ner.model",
				"edu/stanford/nlp/models/ner/english.all.3class.distsim.crf.ser.gz");
		props.put("ner.applyNumericClassifiers", "true");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);

		Annotation annotation = new Annotation(Sentence);

		pipeline.annotate(annotation);
		return annotation;
	}

	public static List<CoreMap> getSentences(Annotation annotation) {
		return annotation.get(CoreAnnotations.SentencesAnnotation.class);
	}

	public static Map<Integer, CorefChain> getAllMentions(Annotation annotation) {
		return annotation.get(CorefCoreAnnotations.CorefChainAnnotation.class);
	}

	public static Set getMentionsSetOf(int i, Annotation annotation) {
		Map<Integer, CorefChain> mentions = API.getAllMentions(annotation);
		return mentions.get(i).getMentionMap().keySet();
	}

	public static List getAllMentionsSets(Annotation annotation) {
		List<Set<IntPair>> mentions = new ArrayList<>();
		Map<Integer, CorefChain> m = getAllMentions(annotation);
		for (int key : m.keySet()) {
			mentions.add(m.get(key).getMentionMap().keySet());
		}
		return mentions;
	}

	public static List<Tree> getAllTrees(Annotation annotation) {
		List<Tree> trees = new ArrayList<>();
		List<CoreMap> sentences = API.getSentences(annotation);
		for (CoreMap sentence : sentences) {
			trees.add(sentence.get(TreeCoreAnnotations.TreeAnnotation.class));
		}
		return trees;
	}

	public void addWsd(HashMap<Pair, String> wordSense) {
		for (Pair row : wordSense.keySet()) {
			Node node = getSentenceByIndex(row.getSentIndex());
			Node.addWsdToNode(node, row.getWordIndex(), wordSense.get(row));
		}
	}

	private static Node getSentenceByIndex(int sentIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	private static void makeCorefs(List<CorefInputChain> corefs, List coref) {
		for (int i = 0; i < coref.size(); i++) {
			String corefList = coref.get(i).toString();
			corefList = corefList.substring(1, corefList.length() - 1);
			corefs.add(new CorefInputChain(corefList));
		}
	}
	
	public static void main(String[] args) {
		Annotation a = API
				.annotate("The term ontology has its origin in philosophy and has been applied in many different ways. The word element onto comes from the Greek present participle of the verb.");
		List<Tree> trees = API.getAllTrees(a);
		tree = new ArrayList<Node>();
		for(int i = 0; i < trees.size(); i++) {
			tree.add(Node.sentenceBuilder(trees.get(i), i+1));
		}
		List coref = API.getAllMentionsSets(a);
		List<CorefInputChain> corefs = new ArrayList<CorefInputChain>();
		makeCorefs(corefs, coref);
		Node.addCoref(tree,corefs);
		for(Node t : tree) {
			Node.printSEPT(t);
		}
	}
}