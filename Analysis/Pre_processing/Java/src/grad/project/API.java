package grad.project;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.rabbitmq.client.*;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;
import edu.stanford.nlp.util.IntPair;
import grad.project.CorefInputChain.CorefNode;
import com.rabbitmq.client.AMQP.BasicProperties;

import java.util.*;

//import edu.mit.jwi.item.Synset;

//import edu.mit.jwi.*;

/**
 * Created by Khaled on 2/12/2015.
 */

public class API {
    private static final String RPC_QUEUE_NAME = "rpc_queue";

	public static Annotation annotate(String Sentence) {
		Properties props = new Properties();
		props.put("annotators",
				"tokenize, ssplit, pos, lemma, ner, depparse, parse, sentiment, dcoref");
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
		List<Set<IntPair>> mentions = new ArrayList<Set<IntPair>>();
		Map<Integer, CorefChain> m = getAllMentions(annotation);
		for (int key : m.keySet()) {
			mentions.add(m.get(key).getMentionMap().keySet());
		}
		return mentions;
	}

	public static List<Tree> getAllTrees(Annotation annotation) {
		List<Tree> trees = new ArrayList<Tree>();
		List<CoreMap> sentences = API.getSentences(annotation);
		for (CoreMap sentence : sentences) {
			trees.add(sentence.get(TreeCoreAnnotations.TreeAnnotation.class));
		}
		return trees;
	}

	public static void main(String[] args) throws Exception{
        StartRPCClient();
	}

    public static void StartRPCClient() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.queueDeclare(RPC_QUEUE_NAME, false, false, false, null);

        channel.basicQos(1);

        QueueingConsumer consumer = new QueueingConsumer(channel);
        channel.basicConsume(RPC_QUEUE_NAME, false, consumer);

        System.out.println(" [x] Awaiting RPC requests");

        while (true) {
            QueueingConsumer.Delivery delivery = consumer.nextDelivery();

            BasicProperties props = delivery.getProperties();
            BasicProperties replyProps = new BasicProperties
                    .Builder()
                    .correlationId(props.getCorrelationId())
                    .build();

            String message = new String(delivery.getBody());

            System.out.println(" [.] ParseTree(" + message + ")");
            String response = "" + genTree(message);

            channel.basicPublish( "", props.getReplyTo(), replyProps, response.getBytes());

            channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
        }
    }

    public static String genTree(String message) throws Exception {
        Annotation a = API
                .annotate(message);

        List<Tree> trees = API.getAllTrees(a);
        List list = API.getAllMentionsSets(a);
        for(int i = 0; i < trees.size(); i++) {
            SEPTBuilder.SEPTs.add(SEPTBuilder.sentenceBuilder(trees.get(i), i+1));
        }

        for(int i = 0; i < list.size(); i++) {
            CorefInputChain coref = new CorefInputChain(list.get(i).toString());
            Node sourceNodeRoot = SEPTBuilder.getSentenceByIndex(coref.getSource().getSentenceIndex());
            Node sourceNode = SEPTBuilder.getNodeByWordIndex(sourceNodeRoot, coref.getSource().getWordIndex());
            SEPTBuilder.printSEPT(sourceNodeRoot);
            for(CorefNode ref : coref.getReferences()) {
                Node leafNodeRoot = SEPTBuilder.getSentenceByIndex(ref.getSentenceIndex());
                Node leafNode = SEPTBuilder.getNodeByWordIndex(leafNodeRoot, ref.getWordIndex());
                leafNode.ref = sourceNode;
            }
        }

        WordSenseRPCClient wordSenseRPCClient = new WordSenseRPCClient();
        String json = wordSenseRPCClient.call(message);
        HashMap<String, String> word_sense_dictionary = new Gson().fromJson(json, new TypeToken<HashMap<String, String>>(){}.getType());
        System.out.println("Dictionary: " + word_sense_dictionary);

        SEPTBuilder.addWS(new HashMap<Pair, String>());

        String response = "";

        for(Node node : SEPTBuilder.SEPTs) {
            SEPTBuilder.printSEPT(node);
            response += (node );
            break;
        }
        SEPTBuilder.SEPTs = new ArrayList<Node>();
        return response;
    }
}