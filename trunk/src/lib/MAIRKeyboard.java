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
import java.awt.event.KeyEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashMap;

public class MAIRKeyboard {

	private Robot robot;
	private static HashMap<String, Integer> keyMap=new HashMap<String, Integer>();
	
	private static void ensureKeyMapLoaded(){
		//created with search/replace from http://java.sun.com/javase/6/docs/api/constant-values.html#java.awt.event.KeyEvent.VK_CONTEXT_MENU
		addToKeyMap(KeyEvent.VK_0);
		addToKeyMap(KeyEvent.VK_1);
		addToKeyMap(KeyEvent.VK_2);
		addToKeyMap(KeyEvent.VK_3);
		addToKeyMap(KeyEvent.VK_4);
		addToKeyMap(KeyEvent.VK_5);
		addToKeyMap(KeyEvent.VK_6);
		addToKeyMap(KeyEvent.VK_7);
		addToKeyMap(KeyEvent.VK_8);
		addToKeyMap(KeyEvent.VK_9);
		addToKeyMap(KeyEvent.VK_A);
		addToKeyMap(KeyEvent.VK_ACCEPT);
		addToKeyMap(KeyEvent.VK_ADD);
		addToKeyMap(KeyEvent.VK_AGAIN);
		addToKeyMap(KeyEvent.VK_ALL_CANDIDATES);
		addToKeyMap(KeyEvent.VK_ALPHANUMERIC);
		addToKeyMap(KeyEvent.VK_ALT);
		addToKeyMap(KeyEvent.VK_ALT_GRAPH);
		addToKeyMap(KeyEvent.VK_AMPERSAND);
		addToKeyMap(KeyEvent.VK_ASTERISK);
		addToKeyMap(KeyEvent.VK_AT);
		addToKeyMap(KeyEvent.VK_B);
		addToKeyMap(KeyEvent.VK_BACK_QUOTE);
		addToKeyMap(KeyEvent.VK_BACK_SLASH);
		addToKeyMap(KeyEvent.VK_BACK_SPACE);
		addToKeyMap(KeyEvent.VK_BEGIN);
		addToKeyMap(KeyEvent.VK_BRACELEFT);
		addToKeyMap(KeyEvent.VK_BRACERIGHT);
		addToKeyMap(KeyEvent.VK_C);
		addToKeyMap(KeyEvent.VK_CANCEL);
		addToKeyMap(KeyEvent.VK_CAPS_LOCK);
		addToKeyMap(KeyEvent.VK_CIRCUMFLEX);
		addToKeyMap(KeyEvent.VK_CLEAR);
		addToKeyMap(KeyEvent.VK_CLOSE_BRACKET);
		addToKeyMap(KeyEvent.VK_CODE_INPUT);
		addToKeyMap(KeyEvent.VK_COLON);
		addToKeyMap(KeyEvent.VK_COMMA);
		addToKeyMap(KeyEvent.VK_COMPOSE);
		addToKeyMap(KeyEvent.VK_CONTEXT_MENU);
		addToKeyMap(KeyEvent.VK_CONTROL);
		addToKeyMap(KeyEvent.VK_CONVERT);
		addToKeyMap(KeyEvent.VK_COPY);
		addToKeyMap(KeyEvent.VK_CUT);
		addToKeyMap(KeyEvent.VK_D);
		addToKeyMap(KeyEvent.VK_DEAD_ABOVEDOT);
		addToKeyMap(KeyEvent.VK_DEAD_ABOVERING);
		addToKeyMap(KeyEvent.VK_DEAD_ACUTE);
		addToKeyMap(KeyEvent.VK_DEAD_BREVE);
		addToKeyMap(KeyEvent.VK_DEAD_CARON);
		addToKeyMap(KeyEvent.VK_DEAD_CEDILLA);
		addToKeyMap(KeyEvent.VK_DEAD_CIRCUMFLEX);
		addToKeyMap(KeyEvent.VK_DEAD_DIAERESIS);
		addToKeyMap(KeyEvent.VK_DEAD_DOUBLEACUTE);
		addToKeyMap(KeyEvent.VK_DEAD_GRAVE);
		addToKeyMap(KeyEvent.VK_DEAD_IOTA);
		addToKeyMap(KeyEvent.VK_DEAD_MACRON);
		addToKeyMap(KeyEvent.VK_DEAD_OGONEK);
		addToKeyMap(KeyEvent.VK_DEAD_SEMIVOICED_SOUND);
		addToKeyMap(KeyEvent.VK_DEAD_TILDE);
		addToKeyMap(KeyEvent.VK_DEAD_VOICED_SOUND);
		addToKeyMap(KeyEvent.VK_DECIMAL);
		addToKeyMap(KeyEvent.VK_DELETE);
		addToKeyMap(KeyEvent.VK_DIVIDE);
		addToKeyMap(KeyEvent.VK_DOLLAR);
		addToKeyMap(KeyEvent.VK_DOWN);
		addToKeyMap(KeyEvent.VK_E);
		addToKeyMap(KeyEvent.VK_END);
		addToKeyMap(KeyEvent.VK_ENTER);
		addToKeyMap(KeyEvent.VK_EQUALS);
		addToKeyMap(KeyEvent.VK_ESCAPE);
		addToKeyMap(KeyEvent.VK_EURO_SIGN);
		addToKeyMap(KeyEvent.VK_EXCLAMATION_MARK);
		addToKeyMap(KeyEvent.VK_F);
		addToKeyMap(KeyEvent.VK_F1);
		addToKeyMap(KeyEvent.VK_F10);
		addToKeyMap(KeyEvent.VK_F11);
		addToKeyMap(KeyEvent.VK_F12);
		addToKeyMap(KeyEvent.VK_F13);
		addToKeyMap(KeyEvent.VK_F14);
		addToKeyMap(KeyEvent.VK_F15);
		addToKeyMap(KeyEvent.VK_F16);
		addToKeyMap(KeyEvent.VK_F17);
		addToKeyMap(KeyEvent.VK_F18);
		addToKeyMap(KeyEvent.VK_F19);
		addToKeyMap(KeyEvent.VK_F2);
		addToKeyMap(KeyEvent.VK_F20);
		addToKeyMap(KeyEvent.VK_F21);
		addToKeyMap(KeyEvent.VK_F22);
		addToKeyMap(KeyEvent.VK_F23);
		addToKeyMap(KeyEvent.VK_F24);
		addToKeyMap(KeyEvent.VK_F3);
		addToKeyMap(KeyEvent.VK_F4);
		addToKeyMap(KeyEvent.VK_F5);
		addToKeyMap(KeyEvent.VK_F6);
		addToKeyMap(KeyEvent.VK_F7);
		addToKeyMap(KeyEvent.VK_F8);
		addToKeyMap(KeyEvent.VK_F9);
		addToKeyMap(KeyEvent.VK_FINAL);
		addToKeyMap(KeyEvent.VK_FIND);
		addToKeyMap(KeyEvent.VK_FULL_WIDTH);
		addToKeyMap(KeyEvent.VK_G);
		addToKeyMap(KeyEvent.VK_GREATER);
		addToKeyMap(KeyEvent.VK_H);
		addToKeyMap(KeyEvent.VK_HALF_WIDTH);
		addToKeyMap(KeyEvent.VK_HELP);
		addToKeyMap(KeyEvent.VK_HIRAGANA);
		addToKeyMap(KeyEvent.VK_HOME);
		addToKeyMap(KeyEvent.VK_I);
		addToKeyMap(KeyEvent.VK_INPUT_METHOD_ON_OFF);
		addToKeyMap(KeyEvent.VK_INSERT);
		addToKeyMap(KeyEvent.VK_INVERTED_EXCLAMATION_MARK);
		addToKeyMap(KeyEvent.VK_J);
		addToKeyMap(KeyEvent.VK_JAPANESE_HIRAGANA);
		addToKeyMap(KeyEvent.VK_JAPANESE_KATAKANA);
		addToKeyMap(KeyEvent.VK_JAPANESE_ROMAN);
		addToKeyMap(KeyEvent.VK_K);
		addToKeyMap(KeyEvent.VK_KANA);
		addToKeyMap(KeyEvent.VK_KANA_LOCK);
		addToKeyMap(KeyEvent.VK_KANJI);
		addToKeyMap(KeyEvent.VK_KATAKANA);
		addToKeyMap(KeyEvent.VK_KP_DOWN);
		addToKeyMap(KeyEvent.VK_KP_LEFT);
		addToKeyMap(KeyEvent.VK_KP_RIGHT);
		addToKeyMap(KeyEvent.VK_KP_UP);
		addToKeyMap(KeyEvent.VK_L);
		addToKeyMap(KeyEvent.VK_LEFT);
		addToKeyMap(KeyEvent.VK_LEFT_PARENTHESIS);
		addToKeyMap(KeyEvent.VK_LESS);
		addToKeyMap(KeyEvent.VK_M);
		addToKeyMap(KeyEvent.VK_META);
		addToKeyMap(KeyEvent.VK_MINUS);
		addToKeyMap(KeyEvent.VK_MODECHANGE);
		addToKeyMap(KeyEvent.VK_MULTIPLY);
		addToKeyMap(KeyEvent.VK_N);
		addToKeyMap(KeyEvent.VK_NONCONVERT);
		addToKeyMap(KeyEvent.VK_NUM_LOCK);
		addToKeyMap(KeyEvent.VK_NUMBER_SIGN);
		addToKeyMap(KeyEvent.VK_NUMPAD0);
		addToKeyMap(KeyEvent.VK_NUMPAD1);
		addToKeyMap(KeyEvent.VK_NUMPAD2);
		addToKeyMap(KeyEvent.VK_NUMPAD3);
		addToKeyMap(KeyEvent.VK_NUMPAD4);
		addToKeyMap(KeyEvent.VK_NUMPAD5);
		addToKeyMap(KeyEvent.VK_NUMPAD6);
		addToKeyMap(KeyEvent.VK_NUMPAD7);
		addToKeyMap(KeyEvent.VK_NUMPAD8);
		addToKeyMap(KeyEvent.VK_NUMPAD9);
		addToKeyMap(KeyEvent.VK_O);
		addToKeyMap(KeyEvent.VK_OPEN_BRACKET);
		addToKeyMap(KeyEvent.VK_P);
		addToKeyMap(KeyEvent.VK_PAGE_DOWN);
		addToKeyMap(KeyEvent.VK_PAGE_UP);
		addToKeyMap(KeyEvent.VK_PASTE);
		addToKeyMap(KeyEvent.VK_PAUSE);
		addToKeyMap(KeyEvent.VK_PERIOD);
		addToKeyMap(KeyEvent.VK_PLUS);
		addToKeyMap(KeyEvent.VK_PREVIOUS_CANDIDATE);
		addToKeyMap(KeyEvent.VK_PRINTSCREEN);
		addToKeyMap(KeyEvent.VK_PROPS);
		addToKeyMap(KeyEvent.VK_Q);
		addToKeyMap(KeyEvent.VK_QUOTE);
		addToKeyMap(KeyEvent.VK_QUOTEDBL);
		addToKeyMap(KeyEvent.VK_R);
		addToKeyMap(KeyEvent.VK_RIGHT);
		addToKeyMap(KeyEvent.VK_RIGHT_PARENTHESIS);
		addToKeyMap(KeyEvent.VK_ROMAN_CHARACTERS);
		addToKeyMap(KeyEvent.VK_S);
		addToKeyMap(KeyEvent.VK_SCROLL_LOCK);
		addToKeyMap(KeyEvent.VK_SEMICOLON);
		addToKeyMap(KeyEvent.VK_SEPARATER);
		addToKeyMap(KeyEvent.VK_SEPARATOR);
		addToKeyMap(KeyEvent.VK_SHIFT);
		addToKeyMap(KeyEvent.VK_SLASH);
		addToKeyMap(KeyEvent.VK_SPACE);
		addToKeyMap(KeyEvent.VK_STOP);
		addToKeyMap(KeyEvent.VK_SUBTRACT);
		addToKeyMap(KeyEvent.VK_T);
		addToKeyMap(KeyEvent.VK_TAB);
		addToKeyMap(KeyEvent.VK_U);
		addToKeyMap(KeyEvent.VK_UNDEFINED);
		addToKeyMap(KeyEvent.VK_UNDERSCORE);
		addToKeyMap(KeyEvent.VK_UNDO);
		addToKeyMap(KeyEvent.VK_UP);
		addToKeyMap(KeyEvent.VK_V);
		addToKeyMap(KeyEvent.VK_W);
		addToKeyMap(KeyEvent.VK_WINDOWS);
		addToKeyMap(KeyEvent.VK_X);
		addToKeyMap(KeyEvent.VK_Y);
		addToKeyMap(KeyEvent.VK_Z);
		try{
			//now put everything in file
			BufferedWriter w=new BufferedWriter(new FileWriter("keyboard_keys_available.txt"));
			w.append("#Key code name that are accepted by MAIRM system.\n");
			for (String name : keyMap.keySet()) {
				w.append(name);
				w.append("\n");
			}
			w.flush();
			w.close();
		}catch (Exception e) {
			
		}
	}
	
	private static void addToKeyMap(int keyCode){
		keyMap.put(KeyEvent.getKeyText(keyCode).toUpperCase(), keyCode);
	}
	
	public static int stringToKeyCode(String keyName){
		ensureKeyMapLoaded();
		Integer i=keyMap.get(keyName.toUpperCase());
		if(i!=null){
			return i.intValue();
		} else {
			return 0;
		}
	}
	
	public static String keyCodeToString(int keyCode){
		return KeyEvent.getKeyText(keyCode).toUpperCase();
	}
	
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
		getRobot().keyPress(msg.getKeyCode());
		getRobot().keyRelease(msg.getKeyCode());
	}
}
