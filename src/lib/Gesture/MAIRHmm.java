/***************************************************************************
 *   Copyright (C) 2010 by                                                 *
 *   	Matej Jakop <matej@jakop.si>                                       *
 *      Gregor Kališnik <gregor@unimatrix-one.org>                         *
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License version 3        *
 *   as published by the Free Software Foundation.                         *
 *                                                                         *
 *   This program is distributed in the hope that it will be useful,       *
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of        *
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the         *
 *   GNU General Public License for more details.                          *
 ***************************************************************************/

package lib.Gesture;

import java.util.ArrayList;
import java.util.List;

import be.ac.ulg.montefiore.run.jahmm.Hmm;
import be.ac.ulg.montefiore.run.jahmm.Observation;
import be.ac.ulg.montefiore.run.jahmm.ObservationInteger;
import be.ac.ulg.montefiore.run.jahmm.ObservationReal;
import be.ac.ulg.montefiore.run.jahmm.ObservationVector;
import be.ac.ulg.montefiore.run.jahmm.OpdfGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.OpdfGaussianMixtureFactory;
import be.ac.ulg.montefiore.run.jahmm.OpdfInteger;
import be.ac.ulg.montefiore.run.jahmm.OpdfIntegerFactory;
import be.ac.ulg.montefiore.run.jahmm.OpdfMultiGaussianFactory;
import be.ac.ulg.montefiore.run.jahmm.draw.GenericHmmDrawerDot;
import be.ac.ulg.montefiore.run.jahmm.learn.BaumWelchLearner;
import be.ac.ulg.montefiore.run.jahmm.learn.KMeansLearner;
import be.ac.ulg.montefiore.run.jahmm.toolbox.KullbackLeiblerDistanceCalculator;
import lib.MAIRInputMessageGesture;
import lib.MAIRObject;

public class MAIRHmm extends MAIRObject {

	public static ArrayList<Hmm<ObservationVector>> hmms=new ArrayList<Hmm<ObservationVector>>();
	private static int NUMBER_OF_STATES=5;
	private Hmm<ObservationVector> hmm;
	
	public void build(MAIRGestureRecorder recorder){
		//prepare data for learning
		try{
		List<List<ObservationVector>> seqs=new ArrayList<List<ObservationVector>>();
		List<ObservationVector> seq=new ArrayList<ObservationVector>();
		for(int i=0;i<recorder.getValues().size();i++){
			MAIRInputMessageGesture msg=recorder.getValues().get(i);
			double[] dataV=msg.featureVector();
			ObservationVector item=new ObservationVector(dataV);
			seq.add(item);
		}
		System.out.println("Dolzina sekvence: "+seq.size());
		seqs.add(seq);
		seqs.add(seq);

		//create hmm
		KMeansLearner<ObservationVector>kMeansLearner=new KMeansLearner<ObservationVector>(NUMBER_OF_STATES, new OpdfMultiGaussianFactory(3), seqs);
		hmm=kMeansLearner.learn();
		//improve
		BaumWelchLearner bwl=new BaumWelchLearner();
		hmm=bwl.learn(hmm,seqs);
		
		if (hmms.size() > 0){
			KullbackLeiblerDistanceCalculator calc=new KullbackLeiblerDistanceCalculator();
			double minDistance=Double.MAX_VALUE;
			int min=-1;
			for(int i=0;i<hmms.size();i++){
				Hmm<ObservationVector> hmm1=hmms.get(i);
				double distance=calc.distance(hmm1, hmm);
				if(distance < minDistance){
					minDistance=distance;
					min=i;
				}
				
			}
			System.out.println("Najmanjsa razdalja je pri i="+min+" znasa pa: "+minDistance);
		}
		hmms.add(hmm);
		}catch (Exception e) {
			System.out.println("Napaka.");
			e.printStackTrace();
		}
		try{
		(new GenericHmmDrawerDot()).write(hmm , "hmm.dot ");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
