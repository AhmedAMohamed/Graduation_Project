package clg.gradProject;

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
import clg.gradProject.CorefInputChain.CorefNode;
import com.rabbitmq.client.AMQP.BasicProperties;
import clg.gradProject.srl.DMRGraph;
import clg.gradProject.srl.Main;

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

	public static void main(String[] args) throws Throwable,  Exception{
        //StartRPCClient();
        genTree("Shakespeare and John had 3 children .");
        for (int i=0;i<10;i++) {
            UUID uuid = UUID.randomUUID();
            System.out.println(uuid.toString());
        }
    }

    public static void StartRPCClient() throws Throwable, Exception {
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

    public static HashMap<Pair, String> getWordSense(String message) throws Exception {
        WordSenseRPCClient wordSenseRPCClient = new WordSenseRPCClient();
        String json = wordSenseRPCClient.call(message);
        HashMap<String, String> word_sense_dictionary = new Gson().fromJson(json, new TypeToken<HashMap<String, String>>(){}.getType());

        HashMap<Pair, String> convertedWSDictionary = new HashMap<Pair, String>();
        for (String keyPair: word_sense_dictionary.keySet()) {
            String[] indicies = keyPair.split(" ", 2);
            Pair pair = new Pair(Integer.parseInt(indicies[0]), Integer.parseInt(indicies[1]));
            convertedWSDictionary.put(pair, word_sense_dictionary.get(keyPair));
        }
      return convertedWSDictionary;
    }


    public static String genTree(String message) throws Throwable, Exception {
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

        // Comment this line to remove unnecessary RPC for wordsense
        //SEPTBuilder.addWS(getWordSense(message));

        String response = "";

        for(Node node : SEPTBuilder.SEPTs) {
            SEPTBuilder.printSEPT(node);
            response += (node );
            break;
        }
        System.out.println("Now second semester code....");

        DMRGraph result = Main.generateTree(message);
        HashMap<String, Object> jsonRes = new HashMap<String, Object>();
        jsonRes.put("frames", result.ActionFrames);
        jsonRes.put("args", result.ArgsHash);
        String jsonStringResponse = new Gson().toJson(jsonRes);
        System.out.println("OUTPUT: " + jsonStringResponse);
        return new Gson().toJson(jsonRes);
    }
}