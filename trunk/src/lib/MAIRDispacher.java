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

import lib.Gesture.MAIRGesture;

public class MAIRDispacher {
	
	private MAIRKeyboard keyboard=new MAIRKeyboard();
	private MAIRMouse mouse=new MAIRMouse();
	private MAIRGesture gesture=new MAIRGesture();

	//detect what is might be based on input
	//and send to correct class....
	public void dispatch(MAIRInputMessage msg){
		if (msg instanceof MAIRInputMessageKeyboard){
			System.out.println("Ukaz tipkovnice");
		} else if (msg instanceof MAIRInputMessageMouse){
			mouse.process((MAIRInputMessageMouse)msg);
		}else if (msg instanceof MAIRInputMessageGesture){
			gesture.process((MAIRInputMessageGesture)msg);
		}
	}
}
