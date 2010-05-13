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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
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
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	private JLabel gestureNameLabel;
	private JButton learnButton;
	private JResizeButton resizeButton;
	private MAIR m;
	private boolean systemTraySupported=false;
	private SystemTray tray;
	private TrayIcon myTrayIcon;
	private boolean  alreadyDisplayedMinimizeMSG=false;
	private Image trayIconNormal=null;
	private Image trayIconConnected=null;
	
	
	public Main() {
		systemTraySupported=SystemTray.isSupported();
		setUI();
	}
	
	public void init() throws Exception{
		m=new MAIR();
		//with input we specify from where lib should read data
		int i=1000+(int)(Math.random()*1000);
		MAIRInput input=new MAIRInputBluetooth("MAIR"+i);
		m.setInput(input);
		try{
			m.getGestures().loadFromFile("znanje.txt");
		}catch (Exception e) {
			
		}
		m.setEventListener(this);
		m.setInputMessageListener(this);
		m.getGestures().addListener(this);
		m.start();
		try{
			GestureDetectedActions.loadFromFile(gestureActionsFileName);
		}catch (Exception e) {
			
		}
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
				trayIconNormal=ImageIO.read(new File("trayicon.gif"));
				trayIconConnected=ImageIO.read(new File("trayicon_connected.gif"));
				myTrayIcon=new TrayIcon(trayIconNormal, "MAIRM");		
				final String reloadButtonLabel="Reload gesture action bindings";
				ActionListener listenerMenu=new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if (e.getActionCommand().equalsIgnoreCase(reloadButtonLabel)){
							try {
								GestureDetectedActions.loadFromFile(gestureActionsFileName);
								myTrayIcon.displayMessage("MAIRM", "Gesture binding file "+gestureActionsFileName+" has been reloaded.", TrayIcon.MessageType.INFO);
							} catch (Exception e1) {
								myTrayIcon.displayMessage("MAIRM", "Error with reloading.", TrayIcon.MessageType.ERROR);
							}
						}
						
					}
				};
				
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
				PopupMenu menu=new PopupMenu();
				menu.add(reloadButtonLabel);
				menu.addActionListener(listenerMenu);
				myTrayIcon.setPopupMenu(menu);
				myTrayIcon.addMouseListener(listener);
			}catch (Exception e) {
				e.printStackTrace();
				System.exit(-1);
			}
		}
		return myTrayIcon;
	}
	
	public JButton getLearnButton(){
		if (learnButton==null){
			learnButton=new JButton("Learn");
			learnButton.addActionListener(this);
		}
		return learnButton;
	}
	
	public JLabel getGestureNameLabel(){
		if (gestureNameLabel==null){
			gestureNameLabel=new JLabel("Gesture name:");
		}
		return gestureNameLabel;
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
			controlsContainer.setSize(new Dimension(400, 595));
			controlsContainer.setLayout(new FlowLayout());
			controlsContainer.setBorder(BorderFactory.createLineBorder(Color.black));
		}
		return controlsContainer;
	}
	
	public JPanel getGraphContainer(){
		if (graphContainer==null){
			graphContainer=new JPanel();
			graphContainer.setSize(new Dimension(400, 600));
			graphContainer.setLayout(new FlowLayout(FlowLayout.CENTER));
		}
		return graphContainer;
	}
	
	public JResizeButton getResizeButton() {
		if (resizeButton==null){
			resizeButton=new JResizeButton();
			resizeButton.setSize(10,200);
			resizeButton.addActionListener(this);
		}
		return resizeButton;
	}
	
	public JGraph getGraphX(){
		if (graphX==null){
			graphX=new JGraph(50);
			graphX.setSize(10, 150);
			graphX.setTextTitle("X axle");
		}
		return graphX;
	}
	
	public JGraph getGraphY(){
		if (graphY==null){
			graphY=new JGraph(50);
			graphY.setSize(100, 150);
			graphY.setTextTitle("Y axle");
		}
		return graphY;
	}
	
	public JGraph getGraphZ(){
		if (graphZ==null){
			graphZ=new JGraph(50);
			graphZ.setSize(10, 150);
			graphZ.setTextTitle("Z axle");
		}
		return graphZ;
	}
	
	public JGraph getGraphSize(){
		if (graphSize==null){
			graphSize=new JGraph(50);
			graphSize.setSize(10, 150);
			graphSize.setTextTitle("Size of vector");
		}
		return graphSize;
	}
	
	
	private void calculateFrameSize(boolean fullSize){
		if (fullSize==false){
			setSize(800-getControlsContainer().getWidth()+getResizeButton().getWidth()+15,640);
		} else {
			setSize(getGraphContainer().getWidth()+getControlsContainer().getWidth()+getResizeButton().getWidth()+15,640);
		}
	}
	
	private void setUI() {
		setTitle("MAIR");
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		calculateFrameSize(false);
		setResizable(false);
		addWindowListener(this);
		//center window
		Dimension screenSize=Toolkit.getDefaultToolkit().getScreenSize();
		setLocation((screenSize.width-getWidth())/2, (screenSize.height-getHeight())/2);
		setLayout(null);
		add(getGraphContainer());
		getGraphContainer().add(getGraphX());
		getGraphContainer().add(getGraphY());
		getGraphContainer().add(getGraphZ());
		getGraphContainer().add(getGraphSize());
				
		add(getResizeButton());
		
		getControlsContainer().setVisible(false);
		add(getControlsContainer());
		getControlsContainer().add(getGestureNameLabel());
		getControlsContainer().add(getGestureNameInput());
		getControlsContainer().add(getLearnButton());
		repositionUIElements(false);
	}
	
	private void repositionUIElements(boolean controlsVisible){
		//position elements
		getGraphContainer().setLocation(5, 0);
		getResizeButton().setLocation(getGraphContainer().getX()+getGraphContainer().getWidth()+5, (getHeight()-getResizeButton().getHeight())/2);
		if (controlsVisible==true){
			getControlsContainer().setLocation(getWidth()-getControlsContainer().getWidth()-5, 5);
			getControlsContainer().setVisible(true);
		}else {
			getControlsContainer().setVisible(false);
		}
	}

	@Override
	public void deviceConnected() {
		getNotifyWindow().displayMessage("MAIRM mobile device is connected.");
		if(systemTraySupported){
			getMyTrayIcon().setImage(trayIconConnected);
		}
	}
	
	@Override
	public void deviceDisconnected() {
		getNotifyWindow().displayMessage("MAIRM mobile device is disconnected.");
		if(systemTraySupported){
			getMyTrayIcon().setImage(trayIconNormal);
		}
		try{
			m.getGestures().saveToFile("znanje.txt");
			GestureDetectedActions.saveChangesToFile("actions.xml");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}	
	@Override
	public void messageReceived(MAIRInputMessage msg) {
		if (isVisible()){
			setTitle("MAIRM working at "+m.getInput().getInputFrequency()+"Hz");
		}
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
					getMyTrayIcon().displayMessage("MAIRM","Aplication is still active Click on icon to restore window back.",TrayIcon.MessageType.INFO);
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
			getGestureNameInput().setEditable(false);
			System.out.println("V redu, pokaži mi.");
		}else if (e.getSource()==getResizeButton()){
			getResizeButton().changeResizeStatus();
			if (getResizeButton().isResized()){
				calculateFrameSize(true);
				repositionUIElements(true);
			} else {
				calculateFrameSize(false);
			}
		}
	}

	@Override
	public void onGestureDetected(String gestureName){
		getNotifyWindow().displayMessage("Recognized "+gestureName);
		GestureDetectedAction action = GestureDetectedActions.getActionForGesture(gestureName);
		action.execute();
	}

	@Override
	public void onNoGestureRecognized() {
		getNotifyWindow().displayMessage("No gesture has been recognized.");
	}

	@Override
	public void onNotEnoughData(int recorded, int needed) {
		getNotifyWindow().displayMessage("Please repeat. To small amount of data.");
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
		getNotifyWindow().displayMessage("I learnt "+gestureName);	
		getGestureNameInput().setEditable(true);
		try{
			GestureDetectedActions.saveChangesToFile(gestureActionsFileName);
		}catch (Exception e) {
		}
	}	
}
