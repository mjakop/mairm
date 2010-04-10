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

import java.awt.MouseInfo;
import java.awt.Robot;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.bluetooth.RemoteDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import lib.*;

public class Demo {
	
	public static void main(String[] args) throws Exception{
		System.out.println(MAIRObject.version());
		MAIR m=new MAIR();
		//with input we specify from where lib should read data
		int i=100+(int)(Math.random()*100);
		MAIRInput input=new MAIRInputBluetooth("MAIR"+i);
		m.setInput(input);
		m.loadGesturesFromFile("demo.txt");
		m.start();
	}
}
