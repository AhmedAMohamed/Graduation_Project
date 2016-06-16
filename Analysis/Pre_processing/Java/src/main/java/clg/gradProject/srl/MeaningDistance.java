package Analysis.Pre_processing.Java.src.main.java.clg.gradProject.srl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Analysis.Pre_processing.Java.src.main.java.clg.gradProject.ArgumentBuilder;
import Analysis.Pre_processing.Java.src.main.java.clg.gradProject.FrameBuilder;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.list.PointerTargetNodeList;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.data.relationship.AsymmetricRelationship;
import net.didion.jwnl.data.relationship.Relationship;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;

public class MeaningDistance {

	ArrayList<ArrayList<FrameBuilder>> Clusters;
	ArrayList<FrameBuilder> largestCluster;
	private IndexWord WORD1;
	private IndexWord WORD2;

	public MeaningDistance(ArrayList<ArrayList<FrameBuilder>> clusters) throws FileNotFoundException, JWNLException {
		Clusters = clusters;

		getMaxDensityCluster();
		getMeaningDistanceMap();
	}


	private double getMeaningArgDistancePair(ArrayList<ArgumentBuilder> value, ArrayList<ArgumentBuilder> value2) throws JWNLException {
		double argsDistance = 1;
		for (int i=0; i<value.size(); i++){
			for (int j=0; j<value2.size(); j++){
				
				//getting index in dictionary for word1
				WORD1 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(value.get(i).partOfSpeech),
						value.get(i).word);
				
				//getting index in dictionary for word 2
				WORD2 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(value2.get(j).partOfSpeech),
						value2.get(j).word);
				
				//getHypernymTree of word 1
				PointerTargetTree hypernyms1 = PointerUtils.getInstance().getHypernymTree(WORD1.getSense(1));
				System.out.println("Hyponyms of \"" + WORD1.getLemma() + "\":");
				hypernyms1.print();
				List list1 = hypernyms1.toList();
				
				//getHypernymTree of word 2
				PointerTargetTree hypernyms2 = PointerUtils.getInstance().getHypernymTree(WORD2.getSense(1));
				System.out.println("Hyponyms of \"" + WORD2.getLemma() + "\":");
				hypernyms2.print();
				List list2 = hypernyms2.toList();
				
				argsDistance *= (list1.size() + list2.size());
			}
		}
		return argsDistance;
	}
	

	private double getMeaningFrameDistancePair(FrameBuilder value, FrameBuilder value2) throws JWNLException {

		/**TODO:meaning related integration**/
	/*
		PointerTargetNodeList hypernyms = PointerUtils.getInstance().getDirectHypernyms(WORD1.getSense(1));
		System.out.println("Direct hypernyms of \"" + WORD1.getLemma() + "\":");
		hypernyms.print();
		
		PointerTargetTree hypernymsL = PointerUtils.getInstance().getHypernymTree(hypernyms);
	*/	
		
		
				//getting index in dictionary for word1
				WORD1 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(value.partOfSpeech),
						value.word);
				
				//getting index in dictionary for word 2
				WORD2 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(value2.partOfSpeech),
						value2.word);
				
				//getHypernymTree of word 1
				PointerTargetTree hypernyms1 = PointerUtils.getInstance().getHypernymTree(WORD1.getSense(1));
				System.out.println("Hypernyms of \"" + WORD1.getLemma() + "\":");
				hypernyms1.print();
				List list1 = hypernyms1.toList();
				
				//getHypernymTree of word 2
				PointerTargetTree hypernyms2 = PointerUtils.getInstance().getHypernymTree(WORD2.getSense(1));
				System.out.println("Hypernyms of \"" + WORD2.getLemma() + "\":");
				hypernyms2.print();
				List list2 = hypernyms2.toList();
				

				//from word1 to root + from root to word2
				double distance = list1.size() + list2.size();
				
				return distance;
			
	}
	
	@SuppressWarnings("unchecked")
	private Map<MeaningDistancePair, Double> getMeaningDistanceMap() throws FileNotFoundException, JWNLException {
		
		//initializing JWNL for use
		JWNL.initialize(new FileInputStream("C:\\Users\\GAMAL\\Desktop\\jwnl14-rc2\\config\\file_properties.xml"));
		MeaningDistancePair wordPair = new MeaningDistancePair();

		Map<MeaningDistancePair, Double> pairDistances = new HashMap<MeaningDistancePair, Double>();

		for (int i = 0; i < largestCluster.size(); i++) {
			for (int j = 0; j < largestCluster.size(); j++) {

				//setting the map values of word1 and word2
				wordPair.setWord1(largestCluster.get(i));
				wordPair.setWord2(largestCluster.get(j));
				
				double distance = getMeaningFrameDistancePair(largestCluster.get(i), largestCluster.get(j));

				
				HashMap <String, ArrayList<ArgumentBuilder>> arguments1 = wordPair.getWord1().arguments;
				HashMap <String, ArrayList<ArgumentBuilder>> arguments2 = wordPair.getWord1().arguments;
				
				Iterator<Entry<String, ArrayList<ArgumentBuilder>>> it1 = arguments1.entrySet().iterator();
				Iterator<Entry<String, ArrayList<ArgumentBuilder>>> it2 = arguments1.entrySet().iterator();
				
    			double argsDistance =0;
				
			    while (it1.hasNext()) {

			        Map.Entry pair1 = (Map.Entry)it1.next();
			    
			    	while (it2.hasNext()){
			    		Map.Entry pair2 = (Map.Entry)it2.next();
			    		if (pair1.getKey().equals(pair2.getKey())){
			    			argsDistance = getMeaningArgDistancePair((ArrayList<ArgumentBuilder>) pair1.getValue(), (ArrayList<ArgumentBuilder>)pair2.getValue());
			        }
			    }
				
				double totalDistance = distance*argsDistance;
				
				wordPair.setDistance(totalDistance);
				
				
			}
		}
		}
		return pairDistances;
	}


	private void getMaxDensityCluster() {

		Collections.sort(Clusters, new Comparator<ArrayList>() {
			public int compare(ArrayList cluster1, ArrayList cluster2) {
				return cluster2.size() - cluster1.size();
			}
		});

		largestCluster = Clusters.get(0);
	}

}
