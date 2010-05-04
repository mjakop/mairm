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

package lib;

public class MAIRFilterSimilarValue extends MAIRFilter {

	private MAIRInputMessage prevMsg=null;
	private double tolerance=0;
	
	public MAIRFilterSimilarValue(double tolerance) {
		this.tolerance=tolerance;
	}
	
	private double getVectorSize(MAIRInputMessage msg){
		if (msg instanceof MAIRInputMessageMouse){
			MAIRInputMessageMouse m=(MAIRInputMessageMouse)msg;
			return m.getSizeOfVector();
		} else if(msg instanceof MAIRInputMessageGesture){
			MAIRInputMessageGesture m=(MAIRInputMessageGesture)msg;
			return m.getSizeOfVector();
		}
		return 0.0;
	}
	
	@Override
	public MAIRInputMessage process(MAIRInputMessage msg) {
		if (prevMsg==null){
			return msg;
		}else {
			double size1=getVectorSize(msg);
			double size2=getVectorSize(prevMsg);
			double diff=Math.abs(size1-size2);
			prevMsg=msg;
			if (diff > tolerance){
				System.out.println("Razlika: "+diff);
				return msg;
			} else {
				System.out.println("Razdalja je premajhna: "+diff);
				return null;
			}
		}
	}

	
	
}
