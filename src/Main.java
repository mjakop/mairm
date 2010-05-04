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

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import lib.*;
import lib.Gesture.MAIRGestures;
import lib.Gesture.MAIRGesturesListener;

public class Main extends JFrame implements MAIREventListener, MAIRInputMessageListener, WindowListener, ActionListener, MAIRGesturesListener {
	
	public static String gestureActionsFileName="actions.xml";
	
	private NotifyWindow notifyWindow=new NotifyWindow();
	private JPanel graphContainer;
	private JPanel controlsContainer;
	private JGraph graphX;
	private JGraph graphY;
	private JGraph graphZ;
	private JGraph graphSize;
	private JTextField gestureNameInput;
	private JButton learnButton;
	private MAIR m;
	private boolean systemTraySupported=false;
	private SystemTray tray;
	private TrayIcon myTrayIcon;
	private boolean  alreadyDisplayedMinimizeMSG=false;
	
	public Main() {
		systemTraySupported=SystemTray.isSupported();
		setUI();
	}
	
	public void init() throws Exception{
		m=new MAIR();
		//with input we specify from where lib should read data
		int i=100+(int)(Math.random()*100);
		MAIRInput input=new MAIRInputBluetooth("MAIR"+i);
		m.setInput(input);
		m.getGestures().loadFromFile("znanje.txt");
		m.setEventListener(this);
		m.setInputMessageListener(this);
		m.getGestures().addListener(this);
		m.start();
		GestureDetectedActions.loadFromFile(gestureActionsFileName);
		//synchronize databases
		String[] learned=MAIRGestures.getLearnedGestureNames();
		for(int j=0;j<learned.length;j++){
			GestureDetectedActions.createNewActionForGestureIfNotExist(learned[j]);
		}
		GestureDetectedActions.saveChangesToFile(gestureActionsFileName);
	}
	
	public NotifyWindow getNotifyWindow() {
		return notifyWindow;
	}
	
	public SystemTray getSystemTray() {
		if (tray==null){
			tray=SystemTray.getSystemTray();
		}
		return tray;
	}
	
	public TrayIcon getMyTrayIcon() {
		if (myTrayIcon==null){
			try{
				Image img=ImageIO.read(new File("trayicon.gif"));
				myTrayIcon=new TrayIcon(img, "MAIRM");				
				MouseListener listener=new MouseListener() {
					
					@Override
					public void mouseReleased(MouseEvent e) {

						
					}
					
					@Override
					public void mousePressed(MouseEvent e) {

						
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {

						
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {
						//do only if left button is used
						if (e.getButton()==MouseEvent.BUTTON1){
							setVisible(true);
							setState (JFrame.NORMAL);
						}
					}
				};
				myTrayIcon.addMouseListener(listener);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return myTrayIcon;
	}
	
	public JButton getLearnButton(){
		if (learnButton==null){
			learnButton=new JButton("Nauèi");
			learnButton.addActionListener(this);
		}
		return learnButton;
	}
	
	public JTextField getGestureNameInput(){
		if (gestureNameInput==null){
			gestureNameInput=new JTextField();
			gestureNameInput.setPreferredSize(new Dimension(100, 30));
		}
		return gestureNameInput;
	}
	
	public JPanel getControlsContainer(){
		if (controlsContainer==null){
			controlsContainer=new JPanel();
			controlsContainer.setPreferredSize(new Dimension(400, 600));
		}
		return controlsContainer;
	}
	
	public JPanel getGraphContainer(){
		if (graphContainer==null){
			graphContainer=new JPanel();
			graphContainer.setPreferredSize(new Dimension(400, 600));
			graphContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
		}
		return graphContainer;
	}
	
	public JGraph getGraphX(){
		if (graphX==null){
			graphX=new JGraph(150);
			graphX.setSize(10, 150);
			graphX.setTextTitle("X os");
		}
		return graphX;
	}
	
	public JGraph getGraphY(){
		if (graphY==null){
			graphY=new JGraph(150);
			graphY.setSize(100, 150);
			graphY.setTextTitle("Y os");
		}
		return graphY;
	}
	
	public JGraph getGraphZ(){
		if (graphZ==null){
			graphZ=new JGraph(150);
			graphZ.setSize(10, 150);
			graphZ.setTextTitle("Z os");
		}
		return graphZ;
	}
	
	public JGraph getGraphSize(){
		if (graphSize==null){
			graphSize=new JGraph(150);
			graphSize.setSize(10, 150);
			graphSize.setTextTitle("Vektor velikosti");
		}
		return graphSize;
	}
	
	private void setUI() {
		setTitle("MAIR: learning application");
		setSize(800,658);
		setResizable(false);
		addWindowListener(this);
		//center window
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-getWidth())/2, (screenSize.height-getHeight())/2);
		//setLayout(new FlowLayout(FlowLayout.CENTER));
		setLayout(new GridLayout(1,1));
		add(getGraphContainer());
		getGraphContainer().add(getGraphX());
		getGraphContainer().add(getGraphY());
		getGraphContainer().add(getGraphZ());
		getGraphContainer().add(getGraphSize());
		add(getControlsContainer());
		getControlsContainer().add(getGestureNameInput());
		getControlsContainer().add(getLearnButton());
	}

	
	@Override
	public void deviceConnected() {
		getNotifyWindow().displayMessage("Naprava se je povezala.");
	}
	
	@Override
	public void deviceDisconnected() {
		getNotifyWindow().displayMessage("Naprava se je odklopila.");
		try{
			m.getGestures().saveToFile("znanje.txt");
			GestureDetectedActions.saveChangesToFile("actions.xml");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void messageReceived(MAIRInputMessage msg) {
		if (msg instanceof MAIRInputMessageMouse){
			MAIRInputMessageMouse m=(MAIRInputMessageMouse)msg;
			getGraphX().addPoint(m.getAccX());
			getGraphY().addPoint(m.getAccY());
			getGraphZ().addPoint(m.getAccZ());
			getGraphSize().addPoint(m.getSizeOfVector());
		} else if (msg instanceof MAIRInputMessageGesture){
			MAIRInputMessageGesture m=(MAIRInputMessageGesture)msg;
			getGraphX().addPoint(m.getAccX());
			getGraphY().addPoint(m.getAccY());
			getGraphZ().addPoint(m.getAccZ());	
			getGraphSize().addPoint(m.getSizeOfVector());
		}
	}

	@Override
	public void windowActivated(WindowEvent e) {
	
	}

	@Override
	public void windowClosed(WindowEvent e) {
		//close notify window
		getNotifyWindow().dispose();
	}

	@Override
	public void windowClosing(WindowEvent e) {
		m.stop();
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		if (systemTraySupported){
			setVisible(true);
			try{
				getSystemTray().remove(getMyTrayIcon());
			}catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void windowIconified(WindowEvent e) {
		if (systemTraySupported){
			//window has been minimized, hide frame and display tray icon.
			setVisible(false);
			try {
				getSystemTray().add(getMyTrayIcon());	
				//display only once.
				if(alreadyDisplayedMinimizeMSG==false){
					alreadyDisplayedMinimizeMSG=true;
					getMyTrayIcon().displayMessage("MAIRM","Aplikacija je še vedno aktivna in v ozadju spremlja vaše akcije.",TrayIcon.MessageType.INFO);
				}
			} catch (AWTException e1) {
				e1.printStackTrace();
			}
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {		
	}
	
	
	public static void main(String[] args) throws Exception{
		Main demo=new Main();
		demo.init();
		demo.setVisible(true);

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==getLearnButton()){
			m.getGestures().learnGestureFor(getGestureNameInput().getText());
			System.out.println("V redu, pokaži mi.");
		}
	}

	@Override
	public void onGestureDetected(String gestureName){
		getNotifyWindow().displayMessage("Prepoznal sem "+gestureName);
		GestureDetectedAction action = GestureDetectedActions.getActionForGesture(gestureName);
		action.execute();
	}

	@Override
	public void onNoGestureRecognized() {
		getNotifyWindow().displayMessage("Nisem prepoznal nobene.");
	}

	@Override
	public void onNotEnoughData(int recorded, int needed) {
		getNotifyWindow().displayMessage("Premalo podatkov, posnel="+recorded+", potreboval="+needed);
	}

	@Override
	public void onRecordingFinished(int length) {
		//getNotifyWindow().displayMessage("Snemanje konèano, posnel "+length+" premikov.");
		
	}

	@Override
	public void onRecordingStarted() {
		//getNotifyWindow().displayMessage("Snemanje se je zaèelo.");	
	}
	
	@Override
	public void onGestureLearned(String gestureName) {
		GestureDetectedActions.createNewActionForGestureIfNotExist(gestureName);
		getNotifyWindow().displayMessage("Nauèil (upam) sem se: "+gestureName);		
	}
	
	
}
