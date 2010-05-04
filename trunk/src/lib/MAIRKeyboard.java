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

import java.awt.Robot;

public class MAIRKeyboard {

	private Robot robot;
	
	private Robot getRobot(){
		if (robot==null){
			try{
				robot=new Robot();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return robot;
	}
	
	
	public void process(MAIRInputMessageKeyboard msg){

	}
}
