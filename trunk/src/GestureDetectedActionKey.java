import java.awt.Robot;
import java.awt.event.KeyEvent;


public class GestureDetectedActionKey {
	
	private boolean iRelease=true;
	private int keyCode=0;
	
	public void setImediatellyRelease(boolean iRelease) {
		this.iRelease = iRelease;
	}
	
	public boolean isImediatellyRelease() {
		return iRelease;
	}
	
	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}
	
	public int getKeyCode() {
		return keyCode;
	}
	
	public void press(Robot r){
		System.out.println(KeyEvent.getKeyText(keyCode));
		r.keyPress(keyCode);
	}
	
	public void release(Robot r){
		r.keyRelease(keyCode);
	}
}
