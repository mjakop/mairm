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

public class MAIRFilterThreshold extends MAIRFilter{
	
	private int lowerLimit;
	private int upperLimit;
	
	public MAIRFilterThreshold(int lowerLimit) {
		this.lowerLimit=lowerLimit;
		this.upperLimit=Integer.MAX_VALUE;
	}
	
	public MAIRFilterThreshold(int lowerLimit, int upperLimit) {
		this.lowerLimit=lowerLimit;
		this.upperLimit=upperLimit;
	}
	
	@Override
	public MAIRInputMessage process(MAIRInputMessage msg) {
		if (msg instanceof MAIRInputMessageMouse){
			MAIRInputMessageMouse mMSG=(MAIRInputMessageMouse)msg;
			double size=mMSG.getSizeOfVector();
			if (size < lowerLimit || size >upperLimit){
				return null;
			}
		}
		return msg;
	}
}
