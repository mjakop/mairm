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

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import lib.MAIRInputMessageGesture;
import lib.MAIRMouse;
import lib.MAIRObject;

public class MAIRGestureRecorder extends MAIRObject {

	private ArrayList<MAIRInputMessageGesture> records=new ArrayList<MAIRInputMessageGesture>();
	private boolean recording=false;
	
	public void startRecordig(){
		recording=true;
	}
	
	public void stopRecording(){
		recording=false;
	}
	
	public int getRecordedSize(){
		return records.size();
	}
	
	public void saveToFile(String fileName){
		try {
			BufferedWriter writer=new BufferedWriter(new FileWriter(fileName));
			for(int i=0;i<records.size();i++){
				MAIRInputMessageGesture g=records.get(i);
				writer.write(g.getAccX()+","+g.getAccY()+","+g.getAccZ());
				writer.newLine();
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void clear(){
		records.clear();
	}
	
	public void record(MAIRInputMessageGesture msg){
		if (recording){
			records.add(msg);
		}
	}
	
	public ArrayList<MAIRInputMessageGesture> getValues() {
		return records;
	}
	
	/**
	 * To eliminate effect of different amplitudes. Normalize values between 0 and level
	 * @param level
	 */
	public void normalizeValues(int level){
		int minAccX=Integer.MAX_VALUE;
		int minAccY=Integer.MAX_VALUE;
		int minAccZ=Integer.MAX_VALUE;
		int maxAccX=Integer.MIN_VALUE;
		int maxAccY=Integer.MIN_VALUE;
		int maxAccZ=Integer.MIN_VALUE;
		for(int i=0;i<records.size();i++){
			MAIRInputMessageGesture g=records.get(i);
			//get minimum for axes
			if (g.getAccX() < minAccX){
				minAccX=g.getAccX();
			}
			if (g.getAccY() < minAccY){
				minAccY=g.getAccY();
			}
			if (g.getAccZ() < minAccZ){
				minAccZ=g.getAccZ();
			}
			if ( g.getAccX() > maxAccX){
				maxAccX=g.getAccX();
			}
			if ( g.getAccY() > maxAccY){
				maxAccY=g.getAccY();
			}
			if ( g.getAccZ() > maxAccZ){
				maxAccZ=g.getAccZ();
			}
		}
		//now normalize
		double absSizeX=Math.abs(maxAccX-minAccX);
		double absSizeY=Math.abs(maxAccY-minAccY);
		double absSizeZ=Math.abs(maxAccZ-minAccZ);
		for(int i=0;i<records.size();i++){
			MAIRInputMessageGesture g=records.get(i);
			double accX=(g.getAccX()-minAccX)/absSizeX*level;
			g.setAccX((int)accX);
			double accY=(g.getAccY()-minAccY)/absSizeY*level;
			g.setAccY((int)accY);
			double accZ=(g.getAccZ()-minAccZ)/absSizeZ*level;
			g.setAccZ((int)accZ);
		}
	}
	
	/**
	 * FOr each message the data are encoded in form
	 * accX,accY,accZ,SizeOfvector,abs(dxy),abs(dxz),abs(dyz)
	 * @return
	 */
	public double[] getData(){
		double[] result=new double[records.size()*7];
		for(int i=0;i<records.size();i++){
			MAIRInputMessageGesture r=records.get(i);
			result[i*7]=r.getAccX();
			result[i*7+1]=r.getAccY();
			result[i*7+2]=r.getAccZ();
			result[i*7+3]=r.getSizeOfVector();
			result[i*7+4]=Math.abs(r.getAccX()-r.getAccY());
			result[i*7+5]=Math.abs(r.getAccX()-r.getAccZ());
			result[i*7+6]=Math.abs(r.getAccY()-r.getAccZ());
		}
		return result;
	}
	
	@Override
	public String toString() {
		StringBuffer buff=new StringBuffer();
		for(int i=0;i<records.size();i++){
			MAIRInputMessageGesture msg=records.get(i);
			buff.append("x: "+msg.getAccX()+", Y="+msg.getAccY()+", Z="+msg.getAccZ());
			buff.append("\n");
		}
		return buff.toString();
	}
	
}
