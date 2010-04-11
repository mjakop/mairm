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

public class MAIRInputMessageGesture extends MAIRInputMessage {
	
	private int accX;
	private int accY;
	private int accZ;
	
	//true if the message is first of the gesture
	private boolean startGesture=false;
	//true if the message is last of the gesture
	private boolean endGesture=false;
	
	public MAIRInputMessageGesture(int x, int y, int z) {
		accX=x;
		accY=y;
		accZ=z;
	}
	
	public void setAccX(int accX) {
		this.accX = accX;
	}
	
	public void setAccY(int accY) {
		this.accY = accY;
	}
	
	public void setAccZ(int accZ) {
		this.accZ = accZ;
	}
	
	public int getAccX() {
		return accX;
	}
	
	public int getAccY() {
		return accY;
	}
	
	public int getAccZ() {
		return accZ;
	}
	
	public boolean isStartGesture() {
		return startGesture;
	}
	
	public boolean isEndGesture() {
		return endGesture;
	}
	
	public void setStartGesture(boolean startGesture) {
		this.startGesture = startGesture;
	}
	
	public void setEndGesture(boolean endGesture) {
		this.endGesture = endGesture;
	}
	
	public double getSizeOfVector(){
		double size=Math.sqrt(accX*accX+accY*accY+accZ*accZ);
		return size;
	}
	
	public double[] featureVector(){
		double diffXY=getAccX()-getAccY();
		double diffXZ=getAccX()-getAccZ();
		double diffYZ=getAccY()-getAccZ();
		double[] vector={getAccX(),getAccY(),getAccZ(),getSizeOfVector(),diffXY,diffXZ,diffYZ};
		return vector;
	}
}
