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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedSet;

import com.sun.org.apache.bcel.internal.generic.NEW;

import net.sf.javaml.classification.KNearestNeighbors;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.distance.fastdtw.FastDTW;

import lib.MAIRFilter;
import lib.MAIRFilterSimilarValue;
import lib.MAIRInputMessageGesture;

public class MAIRGestures {	
	public enum Mode {DETECT_MODE, LEARN_MODE};
	public static int NEAREST_NEIGHBORS_COUNT=5;
	public static int NORMALIZE_VALUES_LEVEL=500;
	public static int FASTDTW_RADIUS=7;
	
	private static ArrayList<MAIRGesture> gestures=new ArrayList<MAIRGesture>();
	private static Dataset data=new DefaultDataset();
	private static KNearestNeighbors knn=new KNearestNeighbors(NEAREST_NEIGHBORS_COUNT,new FastDTW(FASTDTW_RADIUS));
	private static Mode currentMode=Mode.DETECT_MODE;
	//here we save the name for learning gesture.
	private static String learningGestureName="";
	private static ArrayList<MAIRFilter> filters=new ArrayList<MAIRFilter>();
	private static ArrayList<MAIRGesturesListener> listeners=new ArrayList<MAIRGesturesListener>();
	
	public static void init(){
		MAIRFilterSimilarValue f=new MAIRFilterSimilarValue(5.0);
		getFilters().add(f);
	}
	
	public static void loadFromFile(String fileName) throws Exception{
		BufferedReader reader=new BufferedReader(new FileReader(fileName));
		while(true){
			String line=reader.readLine();
			if (line==null){
				break;
			}
			String[] parts=line.split(";");
			int n=Integer.parseInt(parts[1].trim());
			double[] dataV=new double[n];
			String[] values=parts[2].split(",");
			for(int i=0;i<values.length;i++){
				dataV[i]=Double.parseDouble(values[i].trim());
			}
			DenseInstance inst=new DenseInstance(dataV,parts[0].trim());
			data.add(inst);
		}
		reader.close();
		rebuildKnowledge();
	}
	
	public static void saveToFile(String fileName) throws Exception {
		BufferedWriter writer=new BufferedWriter(new FileWriter(fileName));
		for(int i=0;i<data.size();i++){
			DenseInstance inst=(DenseInstance)data.get(i);
			String line=inst.classValue()+";"+inst.noAttributes()+";";
			Iterator<Double> it=inst.values().iterator();
			while(it.hasNext()){
				line=line+it.next()+",";
			}
			writer.append(line);
			writer.newLine();
		}
		writer.flush();
		writer.close();
	}
	
	public static void rebuildKnowledge(){
		knn.buildClassifier(data);
	}
	
	public static void learnGestureFor(String name){
		currentMode=Mode.LEARN_MODE;
		learningGestureName=name.toUpperCase();
	}
	
	public static Mode getCurrentMode() {
		return currentMode;
	}
	
	public static void learn(MAIRGesture gesture){
		double[] gData=gesture.getRecorder().getData();
		DenseInstance inst=new DenseInstance(gData,learningGestureName);
		data.add(inst);
		rebuildKnowledge();
		currentMode=Mode.DETECT_MODE;
		//inform listeners about learning
		ArrayList<MAIRGesturesListener> listeners=getListeners();
		for(int i=0;i<listeners.size();i++){
			listeners.get(i).onGestureLearned(learningGestureName);
		}
	}
	
	public static void detect(MAIRGesture gesture){
		try{
			double[] gData=gesture.getRecorder().getData();
			DenseInstance inst=new DenseInstance(gData,"DETECT");
			Object obj=knn.classify(inst);			
			if (obj==null){
				//not recognized any of available gestures.
				ArrayList<MAIRGesturesListener> listeners=getListeners();
				for(int i=0;i<listeners.size();i++){
					listeners.get(i).onNoGestureRecognized();
				}
			} else{
				ArrayList<MAIRGesturesListener> listeners=getListeners();
				for(int i=0;i<listeners.size();i++){
					listeners.get(i).onGestureDetected(obj.toString());
				}
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<MAIRFilter> getFilters() {
		return filters;
	}
	
	public static ArrayList<MAIRGesturesListener> getListeners() {
		return listeners;
	}
	
	public static void addListener(MAIRGesturesListener listener){
		getListeners().add(listener);
	}
	
	public static String[] getLearnedGestureNames(){
		SortedSet<Object> v=data.classes();
		String[] result=new String[v.size()];
		int i=0;
		for (Object obj : v) {
			result[i]=(String)obj;
			i++;
		}
		return result;
	}

}
