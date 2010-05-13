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

public class MAIRInputMessageKeyboard extends MAIRInputMessage {
	
	private int keyCode;
	//key modifiers
	private boolean altDown=false;
	private boolean shiftDown=false;
	private boolean controlDown=false;
	
	public MAIRInputMessageKeyboard(int keyCode) {
		this.keyCode=keyCode;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}
	
	public void setAltDown(boolean status){
		altDown=status;
	}
	
	public boolean isAltDown() {
		return altDown;
	}
	
	public boolean isShiftDown() {
		return shiftDown;
	}
	
	public void setShiftDown(boolean shiftDown) {
		this.shiftDown = shiftDown;
	}
	
	public boolean isControlDown() {
		return controlDown;
	}
	
	public void setControlDown(boolean controlDown) {
		this.controlDown = controlDown;
	}
	
	public void setModifier(String name){
		name=name.toUpperCase();
		if (name.equals("SHIFT")){
			setShiftDown(true);
		}else if (name.equals("CONTROL")){
			setControlDown(true);
		}else if (name.equals("ALT")) {
			setAltDown(true);
		}
	}
	
	
	
}
