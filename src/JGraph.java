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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JComponent;


public class JGraph  extends JComponent {
	private static final long serialVersionUID = 1L;
	private double[] points;
	private int size=0;
	private int putPosition=0;
	private double minValue=Double.MAX_VALUE;
	private double maxValue=-Double.MAX_VALUE;
	private Color backgroundColor=Color.gray;
	private Color lineColor=Color.WHITE;
	private Color dotColor=Color.RED;
	private Color legendColor=Color.darkGray;
	private Color fontColor=Color.WHITE;
	private String title;
	
	public JGraph(int maxPoints) {
		points=new double[maxPoints];
		setDoubleBuffered(true);
	}
	
	public void setTextTitle(String title){
		this.title=title;
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getParent().getWidth(), getHeight());
	}
	
	@Override
	public void setBackground(Color bg) {
		backgroundColor=bg;
	}
	
	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}
	
	public void setDotColor(Color dotColor) {
		this.dotColor = dotColor;
	}
	
	
	public void addPoint(double value){
		if (size < points.length){
			size++;
		} else if (putPosition >= points.length){
			putPosition=0;
		}
		points[putPosition]=value;
		putPosition++;		
		if (value > maxValue){
			maxValue=value;
		} else if (value < minValue){
			minValue=value;
		}
		repaint();
	}
	
	private double normalizeValue(double value){
		double range=Math.abs(minValue)+Math.abs(maxValue);
		double height=Math.abs(maxValue)/range*getHeight();
		double k=Math.abs(value)/range;
		if (value > 0.0){
			//upper half
			return height-height*k;
		} else {
			//bottom half
			return height+height*k;
		}
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
		g2.setColor(backgroundColor);
		g2.fillRect(0, 0, getWidth(), getHeight());
		if (size > 0){
			//center line
			double range=Math.abs(minValue)+Math.abs(maxValue);
			double height=Math.abs(maxValue)/range*getHeight();
			g2.setColor(legendColor);
			g2.drawLine(0, (int)height, getWidth(), (int)height);
			int dx=getWidth()/size;
			for(int i=1;i<size;i++){
				g2.drawLine(i*dx,0, i*dx,getHeight());
			}
			int prevX=0;
			int prevY=(int)normalizeValue(points[0]);
			int i=0;
			int pos=putPosition;
			while(i < size-1){
				pos++;
				if (pos >= size){
					pos=0;
				}
				int currX=i*dx;
				int currY=(int)normalizeValue(points[pos]);
				g2.setColor(lineColor);
				g2.drawLine(prevX, prevY,currX , currY);
				g2.setColor(dotColor);
				g2.fillOval(currX-2, currY-2, 4, 4);
				prevX=currX;
				prevY=currY;
				i++;
			}
		}else {
			g2.setColor(fontColor);
			g2.drawString("Waiting to receive data.", 10, getHeight()/2);
		}
		g2.setColor(fontColor);
		g2.drawString(title, 10, 20);
	}
}
