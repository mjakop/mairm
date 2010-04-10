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

import java.util.ArrayList;

public class MAIR implements Runnable {

	public static short MODE_NORMAL = 0;
	public static short MODE_LEARN = 1;

	private boolean doWork = false;
	private short mode = MODE_NORMAL;
	private MAIRInput input;
	private MAIRInputMessageListener inputMessageListener;
	private MAIRDispacher dispatcher=new MAIRDispacher();
	private ArrayList<MAIRFilter> filters=new ArrayList<MAIRFilter>();
	
	public MAIR() {
		filters.add(new MAIRFilterThreshold(10));
	}
	
	public boolean loadGesturesFromFile(String fileName){
		return false;
	}

	public void setInput(MAIRInput input) {
		this.input = input;
	}

	/**
	 * Use this to set up event listener for received messages. This can be used for drawing graphs.
	 * @param listener
	 */
	public void setInputMessageListener(MAIRInputMessageListener listener) {
		this.inputMessageListener = listener;
	}

	private void checkSettings() throws Exception {
		if (input == null) {
			throw new MAIRExceptionNoInput();
		}
	}

	public boolean start() throws Exception {
		if (doWork == false) {
			checkSettings();
			mode = MODE_NORMAL;
			doWork = true;
			(new Thread(this)).start();
			return true;
		}
		return false;
	}

	public boolean startLearnMode() throws Exception {
		if (doWork == false) {
			checkSettings();
			mode = MODE_LEARN;
			doWork = true;
			(new Thread(this)).start();
			return true;
		}
		return false;
	}

	public void stop() {
		doWork = false;
	}

	public void run() {
		try {
			input.prepare();
			while(doWork){
				input.connect();
				while (doWork) {
					MAIRInputMessage msg = input.get();
					if (msg==null){
						//device has been disconnected
						System.out.println("Naprava se je odklopila.");
						doWork=false;
						break;
					}
					//inform listener about new message
					if (inputMessageListener!=null){
						inputMessageListener.messageReceived(msg);
					}
					//send message to all filters
					if (msg.isIgnoreFilters()==false){
						int n_filters=filters.size();
						for(int i=0;i<n_filters;i++){
							MAIRFilter filter=filters.get(i);
							msg=filter.process(msg);
							if(msg==null){
								break;
							}
						}
						//if we filtered out message then continue with the loop
						if (msg==null){
							continue;
						}
					}
					//now process
					dispatcher.dispatch(msg);
				}
				input.disconnect();
			}
			input.cleanup();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}
