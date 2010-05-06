import java.awt.Robot;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Stack;

import lib.MAIRInputMessageKeyboard;
import lib.MAIRKeyboard;

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

public class GestureDetectedAction {

	private String exec="";
	private ArrayList<GestureDetectedActionKey> keys=new ArrayList<GestureDetectedActionKey>();
	private Stack<GestureDetectedActionKey> releaseLater=new Stack<GestureDetectedActionKey>();
	
	public void addKey(GestureDetectedActionKey key){
		keys.add(key);
	}
	
	public ArrayList<GestureDetectedActionKey> getKeys() {
		return keys;
	}
	
	public void setExec(String exec) {
		this.exec = exec;
	}
	
	public String getExec() {
		return exec;
	}
	
	public void execute(){
		try{
			if (exec!=null && exec.isEmpty()==false){
				//execute program if needed
				Runtime.getRuntime().exec(exec);
			}
			//execute key presses
			Robot r = new Robot();
			for(int i=0;i<keys.size();i++){
				GestureDetectedActionKey k=keys.get(i);
				k.press(r);
				if (k.isImediatellyRelease()){
					k.release(r);
				} else {
					//add to stack for later release
					releaseLater.add(k);
				}
			}
			//now release in reverse order
			while(releaseLater.isEmpty()==false){
				GestureDetectedActionKey k=releaseLater.pop();
				k.release(r);
			}
				
		}catch (Exception e) {
			//
		}
	}
	
}
