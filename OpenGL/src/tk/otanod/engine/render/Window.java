/*

Copyright (c) <26 dic. 2020> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.engine.render;

import java.awt.Frame;
import java.awt.Point;

import javax.swing.JFrame;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

public class Window {
	
	private static final int WINDOW_TOP_LEFT_Y = 10;
	private static final int WINDOW_TOP_LEFT_X = 10;
	private static final int HEIGHT = 768;
	private static final int WIDTH = 1024;
	private Frame frame;
	private GLCanvas canvas;
	
	public void createDisplay(String title) {
		frame = createWindowAWT(title);
		//frame = createWindowSWING(title);
		canvas = createGLCanvas();
		frame.add(canvas);
		displayWindowAWT(frame);
	}
	
	public void updateDisplay() {
		//canvas.repaint();
		canvas.display();		// Better performance than repaint!!!	
	}
	
	public void closeDisplay() {
		canvas.destroy();
		frame.dispose();
	}
	
	
	public void loopWindow() {
		// Method 1 (JOGL)
		//FPSAnimator animator = new FPSAnimator(-1);
		//animator.add(canvas);
		//animator.start();
		
		// Method 2 (while true)
		while (true) {
			//canvas.repaint();
			canvas.display();		// Better performance than repaint!!!
		}
	}

	private GLCanvas createGLCanvas() {
		GLProfile profile = GLProfile.get(GLProfile.GL4ES3);				// OpenGL ES 3.0
		// GLProfile profile = GLProfile.get(GLProfile.GL2);				// OpenGL 2.0
		
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setDepthBits(24);					

		// Canvas
		GLCanvas canvas = new GLCanvas(capabilities); 						// jogl
		CanvasListener cl = new CanvasListener();
		canvas.addGLEventListener(cl);
		
		//frame.add(canvas);
		canvas.requestFocus();
		
		return canvas;
	}

	private void displayWindowAWT(Frame frame) {
		frame.toFront();
		frame.setVisible(true); 				// Display the window.
		//frame.requestFocus();
	}
	
	private Frame createWindowAWT(String title) {

		Frame frame = new Frame(); 				// creating instance of JFrame
		frame.setTitle(title); 			
		frame.setLocation(new Point(WINDOW_TOP_LEFT_X, WINDOW_TOP_LEFT_Y)); // top-left corner distance to the top-left corner of the screen/monitor
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setSize(WIDTH, HEIGHT);
		//frame.setUndecorated(true);
		
		//frame.setLayout(null);					//using no layout managers
		
		return frame;
	}
	
	private JFrame createWindowSWING(String title) {
		JFrame frame = new JFrame(); 				// creating instance of JFrame
		frame.setTitle(title); 			
		frame.setLocation(new Point(WINDOW_TOP_LEFT_X, WINDOW_TOP_LEFT_Y)); // top-left corner distance to the top-left corner of the screen/monitor
		frame.setResizable(false);
		frame.setFocusable(true);
		frame.setSize(WIDTH, HEIGHT);
		
		//	frame.setLayout(null);					//using no layout managers
		
		return frame;
	}
}
