package Main;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;



public class MouseDrag implements MouseMotionListener{
    private double xNow;
    private double yNow;
	
	@Override
	public void mouseDragged(MouseEvent event) {
		// TODO Auto-generated method stub
		this.xNow = event.getXOnScreen();
		this.yNow = event.getYOnScreen();
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	public double getXNow(){
	    return this.xNow;
	}
	
	public double getYNow(){
	    return this.yNow;
	}
	
}