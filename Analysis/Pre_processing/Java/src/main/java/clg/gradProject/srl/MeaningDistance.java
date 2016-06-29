package clg.gradProject.srl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import clg.gradProject.*;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.PointerUtils;
import net.didion.jwnl.data.list.PointerTargetTree;
import net.didion.jwnl.dictionary.Dictionary;

public class MeaningDistance {

	ArrayList<ArrayList<FrameBuilder>> Clusters;
	ArrayList<FrameBuilder> largestCluster;
	private IndexWord WORD1;
	private IndexWord WORD2;
	static HashMap<MeaningDistancePair, Double> pairDistances;

	public MeaningDistance(ArrayList<ArrayList<FrameBuilder>> clusters) throws FileNotFoundException, JWNLException {
		Clusters = clusters;

		largestCluster = getMaxDensityCluster();
		getMeaningDistanceMap();
	}

	private double getMeaningArgDistancePair(ArrayList<ArgumentBuilder> value, ArrayList<ArgumentBuilder> value2)
			throws JWNLException {
		double argsDistance = 1;
		for (int i = 0; i < value.size(); i++) {
			for (int j = 0; j < value2.size(); j++) {

				// getting index in dictionary for word1
				WORD1 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(value.get(i).partOfSpeech),
						value.get(i).word);

				// getting index in dictionary for word 2
				WORD2 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(value2.get(j).partOfSpeech),
						value2.get(j).word);

				// getHypernymTree of word 1
				PointerTargetTree hypernyms1 = PointerUtils.getInstance().getHypernymTree(WORD1.getSense(1));
				System.out.println("Hyponyms of \"" + WORD1.getLemma() + "\":");
				hypernyms1.print();
				List list1 = hypernyms1.toList();

				// getHypernymTree of word 2
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

		/** TODO:meaning related integration **/
		/*
		 * PointerTargetNodeList hypernyms =
		 * PointerUtils.getInstance().getDirectHypernyms(WORD1.getSense(1));
		 * System.out.println("Direct hypernyms of \"" + WORD1.getLemma() +
		 * "\":"); hypernyms.print();
		 * 
		 * PointerTargetTree hypernymsL =
		 * PointerUtils.getInstance().getHypernymTree(hypernyms);
		 */

		// getting index in dictionary for word1
		WORD1 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(value.partOfSpeech), value.word.toLowerCase().trim());

		// getting index in dictionary for word 2
		WORD2 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(value2.partOfSpeech), value2.word);

		// getHypernymTree of word 1
		PointerTargetTree hypernyms1 = PointerUtils.getInstance().getHypernymTree(WORD1.getSense(1));
		System.out.println("Hypernyms of \"" + WORD1.getLemma() + "\":");
		hypernyms1.print();
		List list1 = hypernyms1.toList();

		// getHypernymTree of word 2
		PointerTargetTree hypernyms2 = PointerUtils.getInstance().getHypernymTree(WORD2.getSense(1));
		System.out.println("Hypernyms of \"" + WORD2.getLemma() + "\":");
		hypernyms2.print();
		List list2 = hypernyms2.toList();

		// from word1 to root + from root to word2
		double distance = list1.size() + list2.size();

		return distance;

	}

	@SuppressWarnings("unchecked")
	private HashMap<MeaningDistancePair, Double> getMeaningDistanceMap() throws FileNotFoundException, JWNLException {

		// initializing JWNL for use
		JWNL.initialize(new FileInputStream("/Users/ahmedalaa/Documents/graduation/Graduation_Project/Analysis/Pre_processing/Java/src/lib/jwnl14-rc2/config/file_properties.xml"));
		MeaningDistancePair wordPair = new MeaningDistancePair();

		pairDistances = new HashMap();

		for (int i = 0; i < largestCluster.size(); i++) {
			for (int j = 0; j < largestCluster.size(); j++) {

				// setting the map values of word1 and word2
				wordPair.setWord1(largestCluster.get(i));
				wordPair.setWord2(largestCluster.get(j));

				double distance = getMeaningFrameDistancePair(largestCluster.get(i), largestCluster.get(j));

				HashMap<String, ArrayList<ArgumentBuilder>> arguments1 = wordPair.getWord1().arguments;
				HashMap<String, ArrayList<ArgumentBuilder>> arguments2 = wordPair.getWord1().arguments;

				Iterator<Entry<String, ArrayList<ArgumentBuilder>>> it1 = arguments1.entrySet().iterator();
				Iterator<Entry<String, ArrayList<ArgumentBuilder>>> it2 = arguments1.entrySet().iterator();

				double argsDistance = 0;

				while (it1.hasNext()) {

					Entry pair1 = (Entry) it1.next();

					while (it2.hasNext()) {
						Entry pair2 = (Entry) it2.next();
						if (pair1.getKey().equals(pair2.getKey())) {
							argsDistance = getMeaningArgDistancePair((ArrayList<ArgumentBuilder>) pair1.getValue(),
									(ArrayList<ArgumentBuilder>) pair2.getValue());
						}
					}

					double totalDistance = distance * 1;// *argsDistance;

					wordPair.setDistance(totalDistance);
					pairDistances.put(wordPair, totalDistance);

				}
			}
		}
		return pairDistances;
	}

	// needs to be written from scratch
	private ArrayList<FrameBuilder> getMaxDensityCluster() {
		int maxClusterIndex = 0;
		int index = 0;
		int maxSize = 0;
		for (ArrayList<FrameBuilder> cluster : Clusters) {
			if(cluster.size() > maxSize) {
				maxSize = cluster.size();
				maxClusterIndex = index;
			}
			index++;
		}

		return Clusters.get(maxClusterIndex);
	}

}
