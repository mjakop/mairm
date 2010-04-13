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

import lib.MAIR;
import lib.MAIRFilter;
import lib.MAIRInputMessageGesture;
import lib.MAIRObject;
import lib.Gesture.MAIRGestures.Mode;

public class MAIRGesture extends MAIRObject{

	private MAIRGestureRecorder recorder = new MAIRGestureRecorder();
	
	@SuppressWarnings("static-access")
	public void process(MAIRInputMessageGesture msg){
		if (msg==null){
			return ; 
		}
		if (msg.isStartGesture()){
			recorder.clear();
			recorder.startRecordig();
			System.out.println("Zacetek snemanja.");
		}else if (msg.isEndGesture()){
			recorder.stopRecording();
			if (recorder.getRecordedSize() <= 15){
				System.out.println("Premalo podatkov.");
			}else{
				System.out.println("Konec snemanja. Posnel "+recorder.getRecordedSize());
				//normalize all values
				recorder.normalizeValues(MAIRGestures.NORMALIZE_VALUES_LEVEL);
				//now recognize or learn gesture
				if (MAIR.getGestures().getCurrentMode()==Mode.LEARN_MODE){
					MAIR.getGestures().learn(this);
				} else {
					MAIR.getGestures().detect(this);
				}
			}
		}else{
			//send message through filters
			ArrayList<MAIRFilter> filters=MAIR.getGestures().getFilters();
			for(int i=0;i<filters.size();i++){
				MAIRFilter filter=filters.get(i);
				msg=(MAIRInputMessageGesture)filter.process(msg);
			}
			if (msg==null){
				//no recording
				return ;
			}
			recorder.record(msg);
		}
	}	
	
	public MAIRGestureRecorder getRecorder() {
		return recorder;
	}
}
