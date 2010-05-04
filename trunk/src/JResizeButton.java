import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JComponent;


public class JResizeButton extends JComponent implements MouseListener {

	private boolean statusResized=false;
	private boolean mouseOver=false;
	private ArrayList<ActionListener> actionListeners=new ArrayList<ActionListener>();
	
	public JResizeButton() {
		addMouseListener(this);
	}
	
	public void changeResizeStatus(){
		statusResized=!statusResized;
	}
	
	public boolean isResized(){
		return statusResized;
	}
	
	public void addActionListener(ActionListener listener){
		actionListeners.add(listener);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (mouseOver){
			g.setColor(Color.red);
		}else {
			g.setColor(Color.darkGray);
		}
		g.fillRoundRect(0, 0, getWidth(), getHeight(),2,2);
		g.setColor(Color.white);
		int x=getWidth()/2;
		int y=getHeight()/2;
		g.drawLine(x-1, y-3, x+1, y-3);
		g.drawLine(x-1, y, x+1, y);
		g.drawLine(x-1, y+3, x+1, y+3);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for(int i=0;i<actionListeners.size();i++){
			ActionEvent event=new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null);
			actionListeners.get(i).actionPerformed(event);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		mouseOver=true;
		repaint();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		mouseOver=false;
		repaint();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		
	}
	
	
	
}
