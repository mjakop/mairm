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

public class MAIRInputMessageMouse extends MAIRInputMessage {

	public enum ButtonStatus {BUTTON_DOWN, BUTTON_UP, BUTTON_NONE};
	
	private int accX;
	private int accY;
	private int accZ;
	
	private ButtonStatus leftButtonStatus=ButtonStatus.BUTTON_NONE;
	private ButtonStatus rightButtonStatus=ButtonStatus.BUTTON_NONE;
	
	public MAIRInputMessageMouse(int x, int y, int z) {
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
	
	public double getSizeOfVector(){
		double size=Math.sqrt(accX*accX+accY*accY+accZ*accZ);
		return size;
	}
	
	public ButtonStatus getLeftButtonStatus() {
		return leftButtonStatus;
	}
	
	public ButtonStatus getRightButtonStatus() {
		return rightButtonStatus;
	}
	
	public void setLeftButtonStatus(ButtonStatus leftButtonStatus) {
		this.leftButtonStatus = leftButtonStatus;
	}
	
	public void setRightButtonStatus(ButtonStatus rightButtonStatus) {
		this.rightButtonStatus = rightButtonStatus;
	}
	
}
