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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import lib.MAIRInputMessageMouse.ButtonStatus;
import lib.MAIRInputMessageMouse.MouseState;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MAIRInputBluetooth extends MAIRInput {

	private String serviceName;
	private String connectionString;
	private StreamConnectionNotifier streamConnNotifier;
	private StreamConnection connection;
	private InputStream inputStream;
	private OutputStream outputStream;
	private BufferedReader bufferedReader;
	private long startFrequencyMeasureTime=-1;
	private int frequencyMeasureCount=0;
	private long frequency=0;
	
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
			e.printStackTrace();
			throw new MAIRExceptionPrepare();
		}
		return true;
	}
	
	public boolean connect() throws IOException {
		try{
			connection=streamConnNotifier.acceptAndOpen();
		}catch (InterruptedIOException e) {
			//if we interrupt waiting input we get this exception
			return false;
		}catch (IOException e) {
			//something more serious is .... 
			return false;
		}
		return true;
	}
	
	@Override
	public boolean disconnect() throws IOException{
		if (inputStream!=null){
			inputStream.close();
			inputStream=null;
		}
		if (outputStream!=null){
			outputStream.close();
			outputStream=null;
		}
		bufferedReader=null;
		if (connection!=null){
			connection.close();
		}
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
	
	private int doubleToInt(Double d, int defaultV){
		try{
			return d.intValue();
		}catch (Exception e) {
			return defaultV;
		}
	}
	
	public long getInputFrequency() {
		return frequency;
	}
	
	private void calculateInputFrequency(){
		if (startFrequencyMeasureTime==0){
			startFrequencyMeasureTime=System.currentTimeMillis();
		}else{
			long diff=System.currentTimeMillis()-startFrequencyMeasureTime;
			if (diff > 1000){
				double freq=(double)(frequencyMeasureCount/(double)diff*1000.0);
				frequency=Math.round(freq);
				frequencyMeasureCount=0;
				startFrequencyMeasureTime=System.currentTimeMillis();
			}
		}
		frequencyMeasureCount++;
	}
	
	@Override
	public MAIRInputMessage get()  throws IOException {
		String line=getBufferedReader().readLine();
		if (line==null){
			return null;
		} else{
			calculateInputFrequency();
			//parse data
			line=line.trim();
			if (line.startsWith("{")){
				//json
				//System.out.println(line);
				try {
					JSONParser p=new JSONParser();
					Object o = p.parse(line);
					if (o instanceof JSONObject){
						JSONObject jobj=(JSONObject)o;
						if (jobj.containsKey("mouse")){
							//parse mouse moving info
							JSONObject mObj=(JSONObject)jobj.get("mouse");
							int x=doubleToInt((Double)mObj.get("x"),0);
							int y=doubleToInt((Double)mObj.get("y"),0);
							int z=doubleToInt((Double)mObj.get("z"),0);
							MAIRInputMessageMouse m=new MAIRInputMessageMouse(x, y, z);
							
							String middleButton=(String)mObj.get("middlebutton");
							//if is scrolling mode
							if (middleButton!=null && middleButton.equalsIgnoreCase("scrolling")){
								m.setState(MouseState.SCROLLING_MODE);
							}
							//left button
							String leftButton=(String)mObj.get("leftbutton");
							if (leftButton!=null){
								if (leftButton.equalsIgnoreCase("down")){
									m.setLeftButtonStatus(ButtonStatus.BUTTON_DOWN);
								}else if (leftButton.equalsIgnoreCase("up")){
									m.setLeftButtonStatus(ButtonStatus.BUTTON_UP);
								}else {
									//no change
									m.setLeftButtonStatus(ButtonStatus.BUTTON_NONE);
								}
								//because it must go through
								m.setIgnoreFilters(true);
							}
							//right button
							String rightButton=(String)mObj.get("rightbutton");
							if (rightButton!=null){
								if (rightButton.equalsIgnoreCase("down")){
									m.setRightButtonStatus(ButtonStatus.BUTTON_DOWN);
								}else if (rightButton.equalsIgnoreCase("up")){
									m.setRightButtonStatus(ButtonStatus.BUTTON_UP);
								}else {
									//no change
									m.setRightButtonStatus(ButtonStatus.BUTTON_NONE);
								}
								//because it must go through
								m.setIgnoreFilters(true);								
							}
							return m;
						} else if (jobj.containsKey("gesture")){
							System.out.println(line);
							JSONObject gObj=(JSONObject)jobj.get("gesture");
							int x=doubleToInt((Double)gObj.get("x"),0);
							int y=doubleToInt((Double)gObj.get("y"),0);
							int z=doubleToInt((Double)gObj.get("z"),0);
							MAIRInputMessageGesture m=new MAIRInputMessageGesture(x, y, z);
							String start=(String)gObj.get("start");
							if (start!=null){
								m.setStartGesture(start.equalsIgnoreCase("true"));
							}else {
								//because it must be start or end and not both at the same time
								String end=(String)gObj.get("end");
								if(end!=null){
									m.setEndGesture(end.equalsIgnoreCase("true"));
								}
							}
							return m;
						} else if(jobj.containsKey("keyboard")){
							System.out.println(line);
							JSONObject kObj=(JSONObject)jobj.get("keyboard");
							String key=(String)kObj.get("keys");
							if (key!=null){
								int keyCode=MAIRKeyboard.stringToKeyCode(key);
								if (keyCode > 0){
									MAIRInputMessageKeyboard keyboard=new MAIRInputMessageKeyboard(keyCode);
									keyboard.setIgnoreFilters(true);
									//now parse modifiers
									Object modiff=kObj.get("modifiers");
									if (modiff instanceof JSONArray){
										JSONArray array=(JSONArray)modiff;
										for(int i=0;i<array.size();i++){
											String s=(String)array.get(i);
											keyboard.setModifier(s);
										}
									}else if (modiff instanceof String){
										keyboard.setModifier((String)modiff);
									}
									return keyboard;
								}
							}
						}
					}
				} catch (ParseException e) {

				}
			}else{
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
			}
			return new MAIRInputMessage();
		}
	}
	
	@Override
	public void interruptWaiting() {
		try {
			streamConnNotifier.close();
		} catch (IOException e) {
		}		
	}

}
