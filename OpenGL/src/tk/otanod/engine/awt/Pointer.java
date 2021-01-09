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


public class Pointer {

	public enum State {
		PRESSED,
		RELEASED,
		DRAGGED,
		MOVED,
	};
	
	private int pointerID;
	private State state;					// for a Event List
	private boolean isPressed;				// for a Event Map
	private double x;					
	private double y;
	private double xOrig;					// initial X position of the Drag
	private double yOrig;					// initial Y position of the Drag
		
	public Pointer(int id, State state, boolean isPressed,int x, int y, int xOrig, int yOrig) {
		this.pointerID = id;
		this.state = state;
		this.isPressed = isPressed;
		this.x = x;
		this.y = y;
		this.xOrig = xOrig;
		this.yOrig = yOrig;
	}
	
	public Pointer(int id, State state, boolean isPressed,float x, float y, float xOrig, float yOrig) {
		this.pointerID = id;
		this.state = state;
		this.isPressed = isPressed;
		this.x = x;
		this.y = y;
		this.xOrig = xOrig;
		this.yOrig = yOrig;
	}
	
	public Pointer(int id, State state, boolean isPressed,double x, double y, double xOrig, double yOrig) {
		this.pointerID = id;
		this.state = state;
		this.isPressed = isPressed;
		this.x = x;
		this.y = y;
		this.xOrig = xOrig;
		this.yOrig = yOrig;
	}

	/***********************
	 * Getters & Setters
	 ***********************/
	
	public int getPointerID() {
		return pointerID;
	}
	
	public void setPointerID(int pointerID) {
		this.pointerID = pointerID;
	}

	public boolean isPressed() {
		return isPressed;
	}

	public void setPressed(boolean isPressed) {
		this.isPressed = isPressed;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}
	
	public double getxOrig() {
		return xOrig;
	}

	public void setxOrig(double xOrig) {
		this.xOrig = xOrig;
	}

	public double getyOrig() {
		return yOrig;
	}

	public void setyOrig(double yOrig) {
		this.yOrig = yOrig;
	}

	@Override
	public String toString() {
		return "Pointer [pointerID=" + pointerID + ", state=" + state + ", isPressed=" + isPressed + ", x=" + x + ", y="
				+ y + ", xOrig=" + xOrig + ", yOrig=" + yOrig + "]";
	}
	
}
