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

import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import lib.*;

public class LearningApp extends JFrame implements MAIREventListener, MAIRInputMessageListener, WindowListener {
	
	private JGraph graphX;
	private JGraph graphY;
	private JGraph graphZ;
	private MAIR m;
	
	public LearningApp() {
		setUI();
	}
	
	public void init() throws Exception{
		m=new MAIR();
		//with input we specify from where lib should read data
		int i=100+(int)(Math.random()*100);
		MAIRInput input=new MAIRInputBluetooth("MAIR"+i);
		m.setInput(input);
		m.loadGesturesFromFile("demo.txt");
		m.setEventListener(this);
		m.setInputMessageListener(this);
		m.start();			
	}
	
	public JGraph getGraphX(){
		if (graphX==null){
			graphX=new JGraph(150);
			graphX.setSize(10, 150);
		}
		return graphX;
	}
	
	public JGraph getGraphY(){
		if (graphY==null){
			graphY=new JGraph(150);
			graphY.setSize(10, 150);
		}
		return graphY;
	}
	
	public JGraph getGraphZ(){
		if (graphZ==null){
			graphZ=new JGraph(150);
			graphZ.setSize(10, 150);
		}
		return graphZ;
	}
	
	private void setUI() {
		setTitle("MAIR: learning application");
		setSize(600,600);
		addWindowListener(this);
		
		setLayout(new FlowLayout(FlowLayout.CENTER));
		add(getGraphX());
		add(getGraphY());
		add(getGraphZ());
	}

	
	@Override
	public void deviceConnected() {
		System.out.println("Naprava povezana.");
	}
	
	@Override
	public void deviceDisconnected() {
		System.out.println("Naprava se je odklopila.");
	}
	
	@Override
	public void messageReceived(MAIRInputMessage msg) {
		if (msg instanceof MAIRInputMessageMouse){
			MAIRInputMessageMouse m=(MAIRInputMessageMouse)msg;
			getGraphX().addPoint(m.getAccX());
			getGraphY().addPoint(m.getAccY());
			getGraphZ().addPoint(m.getAccZ());
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
	
	}

	@Override
	public void windowClosed(WindowEvent e) {

	}

	@Override
	public void windowClosing(WindowEvent e) {
		m.stop();
		System.out.println("Koncam.");
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {		
	}
	
	
	public static void main(String[] args) throws Exception{
		LearningApp demo=new LearningApp();
		demo.init();
		demo.setVisible(true);

	}
}
