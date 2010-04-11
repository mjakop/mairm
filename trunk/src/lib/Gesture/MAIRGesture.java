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

import lib.MAIRInputMessageGesture;
import lib.MAIRObject;

public class MAIRGesture extends MAIRObject{

	private MAIRGestureRecorder recorder = new MAIRGestureRecorder();
	//knowledge about gesture
	private MAIRHmm hmm = new MAIRHmm();
	
	public void process(MAIRInputMessageGesture msg){
		if (msg==null){
			return ; 
		}
		if (msg.isStartGesture()){
			recorder.clear();
			recorder.startRecordig();
			System.out.println("Zacetek snemanja.");
		}
		recorder.record(msg);
		if (msg.isEndGesture()){
			recorder.stopRecording();
			System.out.println("Konec snemanja.");
			//normalize all values between 0 and 500
			recorder.normalizeValues(500);
			//now recognize or learn gesture
			//TODO: Add IF sentence to decide what to do. 
			hmm.build(recorder);
			System.out.println(recorder.toString());
		}
	}
}
