/*

Copyright (c) <7 ene. 2021> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.engine.awt;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Deque;
import java.util.Map;

import tk.otanod.engine.awt.Pointer.State;


public class MouseListeners implements MouseListener, MouseMotionListener, MouseWheelListener {

	private Deque<Pointer> pointerQueue;
	private Map<Integer, Pointer> pointerMap;
	private final int b1 = MouseEvent.BUTTON1_DOWN_MASK;
	private final int b2 = MouseEvent.BUTTON2_DOWN_MASK;
	private final int b3 = MouseEvent.BUTTON3_DOWN_MASK;
	private final int NOBUTTON = MouseEvent.NOBUTTON;
	private final int BUTTON1 = MouseEvent.BUTTON1;
	private final int BUTTON2 = MouseEvent.BUTTON2;
	private final int BUTTON3 = MouseEvent.BUTTON3;
	
	public MouseListeners(Deque<Pointer> pointerList, Map<Integer, Pointer> pointerMap) {
		this.pointerQueue = pointerList;
		this.pointerMap = pointerMap;
	}
	
	/******************************************
	 * MouseListener interface
	 ******************************************/
	
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		Pointer pointer = new Pointer(e.getButton(), State.PRESSED, true, e.getX(), e.getY(), e.getX(), e.getY());
		this.pointerQueue.addLast(pointer);	
		this.pointerMap.put(e.getButton(), pointer);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Pointer pointer = new Pointer(e.getButton(), State.RELEASED, false, e.getX(), e.getY(), e.getX(), e.getY());
		this.pointerQueue.addLast(pointer);
		this.pointerMap.put(e.getButton(), pointer);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	/******************************************
	 * MouseMotionListener interface
	 ******************************************/
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int id = NOBUTTON;
		if ((e.getModifiersEx() & b1) == b1 ) { id = BUTTON1; }
		if ((e.getModifiersEx() & b2) == b2 ) { id = BUTTON2; }
		if ((e.getModifiersEx() & b3) == b3 ) { id = BUTTON3; }
		
		Pointer pointer = new Pointer(id, State.DRAGGED, true, e.getX(), e.getY(), pointerMap.get(id).getxOrig(), pointerMap.get(id).getyOrig());
		this.pointerQueue.addLast(pointer);
		Pointer pointer2 = new Pointer(id, State.DRAGGED, true, e.getX(), e.getY(), e.getX(), e.getY());
		this.pointerMap.put(id, pointer2);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		int id = e.getButton();
		double xOrig = e.getX();
		double yOrig = e.getY();
		if ( this.pointerMap.containsKey(id) ) {
			xOrig = this.pointerMap.get(id).getxOrig();
			yOrig = this.pointerMap.get(id).getyOrig();
		}
		Pointer pointer = new Pointer(id, State.MOVED, false, e.getX(), e.getY(), xOrig, yOrig);
		this.pointerQueue.addLast(pointer);
		
		Pointer pointer2 = new Pointer(id, State.MOVED, false, e.getX(), e.getY(), e.getX(), e.getY());
		this.pointerMap.put(id, pointer2);
	}

	/******************************************
	 * MouseMotionListener interface
	 ******************************************/
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
	       String message;
	       int notches = e.getWheelRotation();
	       if (notches < 0) {
	           message = "Mouse wheel moved UP " + -notches + " notch(es)";
	       } else {
	           message = "Mouse wheel moved DOWN " + notches + " notch(es)";
	       }
	       if (e.getScrollType() == MouseWheelEvent.WHEEL_UNIT_SCROLL) {
	           message += "    Scroll amount: " + e.getScrollAmount();
	           message += "    Units to scroll: " + e.getUnitsToScroll();
	       } else { //scroll type == MouseWheelEvent.WHEEL_BLOCK_SCROLL
	       }
	       System.out.println(message);
	}
	
}
