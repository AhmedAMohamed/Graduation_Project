package clg.gradProject.srl;

import clg.gradProject.ArgumentBuilder;
import clg.gradProject.FrameBuilder;

import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;

/**
 * Created by DOHA on 04/06/2016.
 */
public class KMeans {
    int K;
    ArrayList<Double> Centroids,OldCentroids;
    ArrayList<FrameBuilder>InputFrames;
    ArrayList<ArrayList<FrameBuilder>>Clusters;  //Clusters Created according to the value of k
    ArrayList<Double> Distances; //Distances between the data item and the centroids
    public KMeans(DMRGraph graph,int k){

        InputFrames = graph.ActionFrames;
        Centroids = new ArrayList();
        OldCentroids = new ArrayList();
        Clusters = new ArrayList();
        Distances = new ArrayList();

        this.K=k;
        //Choose the initial clusters
        Random generator =new Random();
        for (int i = 0; i < K; i++) {

            int centroidID=generator.nextInt(InputFrames.size());

            Centroids.add(InputFrames.get(centroidID).score);
            //Add the centroid to the cluster
            Clusters.add(new ArrayList<>());

        }
        int iter=1;
        do{
        for( FrameBuilder arg:InputFrames){
            for(double centroid :Centroids){
                double dist=abs(centroid-arg.score);
                Distances.add(dist);
            }
            Clusters.get(Distances.indexOf(Collections.min(Distances))).add(arg);
            Distances.removeAll(Distances);

        }
            for (int i = 0; i < k; i++) {
                if (iter == 1) {
                    OldCentroids.add(Centroids.get(i));
                } else {
                    OldCentroids.set(i, Centroids.get(i));
                }
                //Calculate the new centroids
                if (!Clusters.get(i).isEmpty()) {
                    Centroids.set(i, average(Clusters.get(i)));
                }
            }
            if (!Centroids.equals(OldCentroids)) {
                for (int i = 0; i < Clusters.size(); i++) {
                    Clusters.get(i).removeAll(Clusters.get(i));
                }
            }
            iter++;
        }
        while(!Centroids.equals(OldCentroids));
    }

    public static double average(ArrayList<FrameBuilder> list) {
        double sum = 0;
        for (FrameBuilder value : list) {
            sum = sum + value.score;
        }
        return sum / list.size();
    }
/*public float validity(){
    float validity;
    float intra=0;
    double inter=0;
    for(int i=0;i<this.K;i++){
    float c=Centroids.get(i);
    ArrayList<ArgumentBuilder> cluster=Clusters.get(i);
    for(float centroid:Centroids){
        inter=Math.pow(abs(c-centroid),2);
    }
        inter=inter/this.K;
    }

    return validity;

}*/
    }

