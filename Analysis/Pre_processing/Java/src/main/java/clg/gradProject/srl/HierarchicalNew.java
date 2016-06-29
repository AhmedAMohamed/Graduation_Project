package clg.gradProject.srl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import clg.gradProject.*;
import net.didion.jwnl.JWNL;
import net.didion.jwnl.JWNLException;
import net.didion.jwnl.data.IndexWord;
import net.didion.jwnl.data.POS;
import net.didion.jwnl.data.Pointer;
import net.didion.jwnl.data.PointerType;
import net.didion.jwnl.data.Synset;
import net.didion.jwnl.data.relationship.AsymmetricRelationship;
import net.didion.jwnl.data.relationship.Relationship;
import net.didion.jwnl.data.relationship.RelationshipFinder;
import net.didion.jwnl.data.relationship.RelationshipList;
import net.didion.jwnl.dictionary.Dictionary;

public class HierarchicalNew {
    HashMap<ArrayList<FrameBuilder>, Integer> levels;
    HashMap<ArrayList<FrameBuilder>, String> parentWord;
    HashMap<FrameBuilder, ArrayList<FrameBuilder>> parents;
    int level = 0;
    private HashMap<String, HashMap<String, Double>> mapDistanceTable;

    HierarchicalNew(DMRGraph graph, MeaningDistance meaning) throws FileNotFoundException, JWNLException {
        levels = new HashMap();
        parentWord = new HashMap();
        parents = new HashMap();

        createDistanceTable(meaning);
        getMinDistance(graph);
        getCommonParent(null, null, 0, graph);

    }

    private void getCommonParent(FrameBuilder frame1, FrameBuilder frame2, double distance, DMRGraph graph) throws FileNotFoundException, JWNLException {

        FrameBuilder[] pair = new FrameBuilder[2];
        pair[0] = frame1;
        pair[1] = frame2;

        JWNL.initialize(new FileInputStream("C:\\Users\\GAMAL\\Desktop\\jwnl14-rc2\\config\\file_properties.xml"));
        IndexWord WORD1 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(frame1.partOfSpeech), frame1.word);
        IndexWord WORD2 = Dictionary.getInstance().getIndexWord(POS.getPOSForLabel(frame2.partOfSpeech), frame2.word);

        RelationshipList list = RelationshipFinder.getInstance().findRelationships(WORD1.getSense(1), WORD2.getSense(1),
                PointerType.HYPERNYM);
        for (Iterator itr = list.iterator(); itr.hasNext(); ) {
            ((Relationship) itr.next()).getNodeList().print();
        }
        int CommonParentIndex = ((AsymmetricRelationship) list.get(0)).getCommonParentIndex();
        String parent = WORD1.getLemma();
        POS pos;
        FrameBuilder parentFrame = new FrameBuilder(null, -2, -2, null, null, -2);
        for (int x = 1; x <= CommonParentIndex; x++) {
            IndexWord l = Dictionary.getInstance().lookupIndexWord(WORD1.getPOS(), parent);
            parent = GetParentSynset(l.getSense(1)).getWord(0).getLemma();
            pos = GetParentSynset(l.getSense(1)).getWord(0).getPOS();

            parentFrame.partOfSpeech = pos.toString();
            parentFrame.word = parent;
        }

        ArrayList<FrameBuilder> clusteredTerms = new ArrayList<FrameBuilder>();
        clusteredTerms.add(frame1);
        clusteredTerms.add(frame2);
        /*
		 * if (parents.containsKey(frame1))
		 * clusteredTerms.addAll(parents.get(frame1)); if
		 * (parents.containsKey(frame2))
		 * clusteredTerms.addAll(parents.get(frame2));
		 */


        //what if we are clustering two common parents? where do we get the pos tag?
        parents.put(parentFrame, clusteredTerms);
        parentWord.put(clusteredTerms, parent);
        levels.put(clusteredTerms, level);
        level++;


        updateDistanceTable(pair, parentFrame, graph);
    }

    private void updateDistanceTable(FrameBuilder[] pair, FrameBuilder parentFrame, DMRGraph graph) {

        HashMap<String, Double> distanceW1 = mapDistanceTable.get(pair[0].frameID);

        //removing ClusterL and ClusterR
        mapDistanceTable.remove(pair[0].frameID);
        mapDistanceTable.remove(pair[1].frameID);
		
		graph.ActionFrames.add(parentFrame);
        //I am sure either going by frameID is wrong or going by simply adding an arrayList is wrong
        mapDistanceTable.put(parentFrame.frameID, distanceW1);
    }

    public static Synset GetParentSynset(Synset node) throws JWNLException {
        Synset parent = node;
        Pointer[] point = node.getPointers();
        for (int i = 0, d = point.length; i < d; i++) {
            if (point[i].getType().getLabel().equals("hypernym")) {
                parent = point[i].getTargetSynset();
            }
        }
        return parent;
    }

    private void getMinDistance(DMRGraph graph) throws FileNotFoundException, JWNLException {
        if (mapDistanceTable.size() > 1) {
            double min = -1000000;

            FrameBuilder frame1 = null;
            FrameBuilder frame2 = null;

            String rowIndex = null;
            String colIndex = null;
            // this row and coloumnIndex is for sure wrong

            for (String frameID_1 : mapDistanceTable.keySet()) {
                HashMap<String, Double> cols = mapDistanceTable.get(frameID_1);
                for (String frameID_2 : cols.keySet()) {
                    if (mapDistanceTable.get(frameID_1).get(frameID_2) < min) {
                        min = mapDistanceTable.get(frameID_1).get(frameID_2);
                        rowIndex = frameID_1;
                        colIndex = frameID_2;
                    }
                }
            }

            ArrayList<FrameBuilder> frames = graph.ActionFrames;

            frame1 = getFrame(frames, rowIndex);
            frame2 = getFrame(frames, colIndex);

            //TODO: in case getFrame returned null

            getCommonParent(frame1, frame2, min, graph);
        }
    }

    private FrameBuilder getFrame(ArrayList<FrameBuilder> frames, String rowIndex) {
        for (FrameBuilder f : frames) {
            if (f.frameID.equalsIgnoreCase(rowIndex)) {
                return f;
            }
        }
        return null;
    }

    private void createDistanceTable(MeaningDistance meaning) {
        mapDistanceTable = new HashMap();

        for (MeaningDistancePair pair : MeaningDistance.pairDistances.keySet()) {
            if (mapDistanceTable.get(pair.word1.frameID) != null) {
                mapDistanceTable.get(pair.word1.frameID).put(pair.word2.frameID, MeaningDistance.pairDistances.get(pair));
            } else {

                HashMap<String, Double> disHashForTerm = new HashMap<String, Double>();
                disHashForTerm.put(pair.word2.frameID, meaning.pairDistances.get(pair));
                mapDistanceTable.put(pair.word1.frameID, disHashForTerm);
            }
        }
    }

}
