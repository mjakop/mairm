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

import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;

import lib.MAIRInputMessageMouse.ButtonStatus;
import lib.MAIRInputMessageMouse.MouseState;

public class MAIRMouse extends MAIRObject {
	
	public static int AVERAGE_QUE_LENGTH=5;
	private Robot robot;
	private MAIRAverageQue avgX=new MAIRAverageQue(AVERAGE_QUE_LENGTH);
	private MAIRAverageQue avgY=new MAIRAverageQue(AVERAGE_QUE_LENGTH);
	
	private Robot getRobot() {
		if (robot==null){
			try{
				robot=new Robot();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return robot;
	}
	
	private void moveMouse(double dx, double dy){
		int dx_i=(int) Math.round(dx);
		int dy_i=(int) Math.round(dy);
		int locX=MouseInfo.getPointerInfo().getLocation().x;
		int locY=MouseInfo.getPointerInfo().getLocation().y;
		getRobot().mouseMove(locX+dx_i, locY+dy_i);
	}
	
	private void scrollMouse(double dx){
		getRobot().mouseWheel((int)Math.round(dx));
	}
	
	private double getValueIfGratherThan(double x, double limit, double defaultValue){
		if (x > limit){
			return x;
		}else {
			return defaultValue;
		}
	}
	
	private void processButtons(MAIRInputMessageMouse msg){
		boolean anyDown=false;
		boolean anyUp=false;
		int flagsDown=0;
		int flagsUp=0;
		if (msg.getLeftButtonStatus()==ButtonStatus.BUTTON_DOWN){
			flagsDown=flagsDown | InputEvent.BUTTON1_MASK;
			anyDown=true;
		}
		
		if (msg.getRightButtonStatus()==ButtonStatus.BUTTON_DOWN){
			flagsDown=flagsDown | InputEvent.BUTTON3_MASK;
			anyDown=true;
		}
		
		if (msg.getLeftButtonStatus()==ButtonStatus.BUTTON_UP){
			flagsUp=flagsUp | InputEvent.BUTTON1_MASK;
			anyUp=true;
		}

		if (msg.getRightButtonStatus()==ButtonStatus.BUTTON_UP){
			flagsUp=flagsUp | InputEvent.BUTTON3_MASK;
			anyUp=true;
		}
		
		if (anyDown){
			getRobot().mousePress(flagsDown);
		}
		
		if (anyUp){
			getRobot().mouseRelease(flagsUp);
		}		
	}
	
	public void processMoveScrolling(MAIRInputMessageMouse msg){
		double dx=0;
		double dy=0;
		int dirX; //-1 forward, 1 backward
		int dirY; //-1 left, 1 right
		//calculate direction
		if (msg.getAccY() < 0){
			dirX=1;
		} else {
			dirX=-1;
		}
		if (msg.getAccX() < 0){
			dirY=-1;
		} else {
			dirY=1;
		}
		dx=getValueIfGratherThan(Math.abs(msg.getAccY()),40,0);
		dy=getValueIfGratherThan(Math.abs(msg.getAccX()),40,0);
		//make more accurate when phone is not to much tilt.
		double factorChangeX=1.0;
		double factorChangeY=1.0;
		if (dx > 0 && dx < 100){
			factorChangeX=0.1;
		}
		if (dy > 0 && dy < 100){
			factorChangeY=0.1;
		}
		if(dx > 0 && dx < 200){
			factorChangeX=dx/200.0;
		}
		if(dy > 0 && dy < 200){
			factorChangeY=dy/200.0;
		}
		dx*=factorChangeX*dirX;
		dy*=factorChangeY*dirY;
		//average value with last values for smoother movement
		avgX.put(dx);
		avgY.put(dy);
		dx=avgX.getAverage();
		dy=avgY.getAverage();
		if (msg.getState()==MouseState.MOVING_MODE){	
			moveMouse(dx/4, dy/4); //only here divide and reduce 
		}else {
			//scroll window content on base of dx movement input device.
			scrollMouse(dx/4);
		}
	}
	
	public void process(MAIRInputMessageMouse msg){
		processMoveScrolling(msg);
		processButtons(msg);
	}
	
}
