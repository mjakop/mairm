/***************************************************************************
 *   Copyright (C) 2010 by                                                 *
 *   	Matej Jakop <matej@jakop.si>                                       *
 *      Gregor Kali�nik <gregor@unimatrix-one.org>                         *
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
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import lib.MAIRInputMessageMouse.ButtonStatus;

public class MAIRInputBluetooth extends MAIRInput {

	private String serviceName;
	private String connectionString;
	private StreamConnectionNotifier streamConnNotifier;
	private StreamConnection connection;
	private InputStream inputStream;
	private OutputStream outputStream;
	private BufferedReader bufferedReader;
	
	public MAIRInputBluetooth(String serviceName) {
		this.serviceName=serviceName;
		generateServerConnectionString();
	}
	
	private void generateServerConnectionString(){
		//standard uuid for serial communication
		UUID uuid = new UUID("1101", true); 
		connectionString = "btspp://localhost:" +uuid+";name="+serviceName;
	}
	
	public String getConnectionString() {
		return connectionString;
	}
	
	@Override
	public boolean prepare() throws MAIRExceptionPrepare {
		try{
			streamConnNotifier = (StreamConnectionNotifier)Connector.open(connectionString);
		}catch (Exception e) {
			throw new MAIRExceptionPrepare();
		}
		return true;
	}
	
	public boolean connect() throws IOException {
		connection=streamConnNotifier.acceptAndOpen();
		return true;
	}
	
	@Override
	public boolean disconnect() throws IOException{
		connection.close();
		return true;
	}
	
	@Override
	public boolean cleanup() {
		try {
			streamConnNotifier.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	@Override
	public InputStream getInputStream() throws IOException {
		if (inputStream==null){
			inputStream=connection.openInputStream();
		}
		return inputStream;
	}
	
	@Override
	public OutputStream getOutputStream() throws IOException {
		if(outputStream==null){
			outputStream=connection.openOutputStream();
		}
		return outputStream;
	}
	
	private BufferedReader getBufferedReader() throws IOException {
		if(bufferedReader==null){
			bufferedReader=new BufferedReader(new InputStreamReader(getInputStream()));
		}
		return bufferedReader;
	}
	
	@Override
	public MAIRInputMessage get()  throws IOException {
		//TODO: Change to JSON format.
		String line=getBufferedReader().readLine();
		if (line==null){
			return null;
		} else{
			//parse data
			line=line.trim();
			try{
				MAIRInputMessageGesture gesture;
				if (line.startsWith("#")==false){
					String[] parts=line.split(";");
					int x=Integer.parseInt(parts[0]);
					int y=Integer.parseInt(parts[1]);
					int z=Integer.parseInt(parts[2]);
					gesture=new MAIRInputMessageGesture(x,y,z);
				} else{
					gesture=new MAIRInputMessageGesture(0, 0, 0);
				}
				if (line.startsWith("#levDOL")){
					gesture.setStartGesture(true);
				} else if (line.startsWith("#levGOR")){
					gesture.setEndGesture(true);				
				}
				return gesture;
			}catch (Exception e) {
				
			}
			/*
			 //MOUSE CONTROL
			  try{
					String[] parts=line.split(";");
					int x=Integer.parseInt(parts[0]);
					int y=Integer.parseInt(parts[1]);
					int z=Integer.parseInt(parts[2]);
					MAIRInputMessageMouse mouse=new MAIRInputMessageMouse(x,y,z);
					if (line.startsWith("#levDOL")){
						mouse.setLeftButtonStatus(ButtonStatus.BUTTON_DOWN);
					}else if (line.startsWith("#levGOR")){
						mouse.setLeftButtonStatus(ButtonStatus.BUTTON_UP);
					}
					return mouse;
			}catch (Exception e) {
			}*/
			return new MAIRInputMessage();
		}
	}

}