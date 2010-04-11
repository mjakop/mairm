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

import lib.MAIRInputMessageGesture;
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
	 * To eliminate effect of different amplitudes. Normalite values between 0 and level
	 * @param level
	 */
	public void normalizeValues(double level){
		
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
