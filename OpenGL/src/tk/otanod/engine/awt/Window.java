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

package tk.otanod.engine.awt;

import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ComponentListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowListener;
import java.awt.event.WindowStateListener;
import java.util.List;

import javax.swing.JFrame;

import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

import tk.otanod.engine.render.CanvasListener;
import tk.otanod.engine.render.Model;

public class Window {
	
	private static final int WINDOW_TOP_LEFT_Y = 10;
	private static final int WINDOW_TOP_LEFT_X = 10;
	private static int HEIGHT = 768;
	private static int WIDTH = 1024;
	private Frame frame;
	private GLCanvas canvas;
	
	List<Model> models;
	public Window(int width, int height, List<Model> models) {
		Window.WIDTH = width;
		Window.HEIGHT = height;
		this.models = models;
	}
	
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
		capabilities.setSampleBuffers(true);								// Together with gl.glEnable(GL4ES3.GL_MULTISAMPLE);
		capabilities.setNumSamples(4);										// Together with gl.glEnable(GL4ES3.GL_MULTISAMPLE);
		//capabilities.setDoubleBuffered(true);	

		// Canvas
		GLCanvas canvas = new GLCanvas(capabilities); 						// jogl
		CanvasListener cl = new CanvasListener(models, WIDTH, HEIGHT);
		canvas.addGLEventListener(cl);
		
		//frame.add(canvas);
		canvas.requestFocus();
		
		return canvas;
	}

	private void displayWindowAWT(Frame frame) {
		frame.toFront();
		frame.setVisible(true); 				// Display the window.
		frame.requestFocus();
	}
	
	private Frame createWindowAWT(String title) {

		Frame frame = new Frame(); 				// creating instance of JFrame
		frame.setTitle(title); 			
		frame.setLocation(new Point(WINDOW_TOP_LEFT_X, WINDOW_TOP_LEFT_Y)); // top-left corner distance to the top-left corner of the screen/monitor
		frame.setFocusable(true);
		//frame.setUndecorated(true);
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		
		//frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		
		//frame.setLayout(null);					//using no layout managers
		
		return frame;
	}
	
	private JFrame createWindowSWING(String title) {
		JFrame frame = new JFrame(); 				// creating instance of JFrame
		frame.setTitle(title); 			
		frame.setLocation(new Point(WINDOW_TOP_LEFT_X, WINDOW_TOP_LEFT_Y)); // top-left corner distance to the top-left corner of the screen/monitor
		//frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setFocusable(true);
		frame.setSize(WIDTH, HEIGHT);
		frame.setResizable(false);
		//frame.setExtendedState(Frame.MAXIMIZED_BOTH);  // full screen mode
		
		//	frame.setLayout(null);					//using no layout managers
		
		return frame;
	}

	/***********************************************
	 * Listener
	 ***********************************************/
	
	public void attachListener(Object listener) {
		// if the object implements any well known listener interface, it will be attached to our FRAME
		if ( this.frame == null ) {
			System.err.println("ERROR: Frame is not already initiallized");
			System.exit(-1);
		}
		if ( WindowListener.class.isInstance(listener) ) {
			this.frame.addWindowListener((WindowListener) listener);
		}
		if ( WindowStateListener.class.isInstance(listener) ) {
			this.frame.addWindowStateListener((WindowStateListener) listener);
		}
		if ( ComponentListener.class.isInstance(listener) ) {
			this.frame.addComponentListener((ComponentListener) listener);
		}
		if ( KeyListener.class.isInstance(listener) ) {
			this.canvas.addKeyListener((KeyListener) listener);
		}
		if ( MouseListener.class.isInstance(listener) ) {
			this.canvas.addMouseListener((MouseListener) listener);
		}
		if ( MouseMotionListener.class.isInstance(listener) ) {
			this.canvas.addMouseMotionListener((MouseMotionListener) listener);
		}
		if ( MouseWheelListener.class.isInstance(listener) ) {
			this.canvas.addMouseWheelListener((MouseWheelListener) listener);
		}
	}
	
}
