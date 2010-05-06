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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JDialog;


public class NotifyWindow extends JDialog {
	
	public static int MESSAGE_TIMEOUT=4000;
	private static int BORDER_WIDTH=5;
	private String textToDisplay="no text";
	private Image background;
	
	public NotifyWindow() {
		setVisible(false);
		setAlwaysOnTop(true);
		setUndecorated(true);
		setFocusable(false);
		setFocusableWindowState(false);
	}
	
	private void setSizePosition(int width, int height){
		setSize(width, height);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(d.width-getWidth(), 0);
	}
	
	public void display(){
		setSizePosition(10, 10);
		setVisible(true);
		TimerTask task=new TimerTask() {
			public void run() {
				removeFromScreen();
			}
		};
		Timer t=new Timer();
		t.schedule(task, MESSAGE_TIMEOUT);
	}
	
	public void removeFromScreen(){
		setVisible(false);
	}
	
	public void displayMessage(String text){
		textToDisplay=text;
		display();
	}
	
    private static void enableAntiAlliasing(Graphics2D g2d) {
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        } catch (Exception ex) {
        }
    }
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2=(Graphics2D)g;
		enableAntiAlliasing(g2);
		g2.setFont(new Font("sansserif",Font.PLAIN,38));
		//calculate needed size
		int width=g2.getFontMetrics().stringWidth(textToDisplay)+2*BORDER_WIDTH;
		int height=g2.getFontMetrics().getHeight()+BORDER_WIDTH/2;
		setSizePosition(width, height);
		//now paint text
		g2.drawString(textToDisplay, BORDER_WIDTH, 2*BORDER_WIDTH+g2.getFontMetrics().getHeight()/2);
	}

}
