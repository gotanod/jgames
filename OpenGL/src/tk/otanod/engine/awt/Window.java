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

import java.awt.Component;
import java.awt.Dimension;
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
	
	private WindowGlobalParameters params;
	private Frame frame;
	private GLCanvas canvas;
	List<Model> models;
	
	public Window(WindowGlobalParameters params, List<Model> models) {
		this.params = params;
		this.models = models;
	}
	
	public void createDisplay(String title) {
		frame = createWindowAWT(title);
		//frame = createWindowSWING(title);
		canvas = createGLCanvas();
		//frame.add(canvas).setPreferredSize(new Dimension(this.params.getWindow_width_px(), this.params.getWindow_height_px()));
		Component component = frame.add(canvas);																			// add the GLCanvas to the frame
		component.setPreferredSize(new Dimension(this.params.getWindow_width_px(), this.params.getWindow_height_px()));		// set the exact canvas size, ignoring window decoration
		frame.pack();			// sets frame size to fit the canvas component size
		
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
		// ALl available profiles
		String supportedModes = GLProfile.glAvailabilityToString();
		debug("Supported modes", supportedModes);
		
		// https://jogamp.org/deployment/jogamp-next/javadoc/jogl/javadoc/com/jogamp/opengl/GL4ES3.html
		GLProfile profile = GLProfile.get(GLProfile.GL4ES3);				// OpenGL ES 3.0
		//GLProfile profile = GLProfile.get(GLProfile.GL2ES2);				// OpenGL ES 2.0
		
		GLCapabilities capabilities = new GLCapabilities(profile);
		capabilities.setDepthBits(24);
		capabilities.setSampleBuffers(true);								// Together with gl.glEnable(GL4ES3.GL_MULTISAMPLE);
		capabilities.setNumSamples(4);										// Together with gl.glEnable(GL4ES3.GL_MULTISAMPLE);
		//capabilities.setDoubleBuffered(true);
		debug("GL HW acceleration", "" + capabilities.getHardwareAccelerated());
		debug("GL doble buffer", "" + capabilities.getDoubleBuffered());

		// Canvas
		GLCanvas canvas = new GLCanvas(capabilities); 						// jogl
		CanvasListener cl = new CanvasListener(this.models, this.params);
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

		System.setProperty("sun.awt.noerasebackground", "true");
		System.out.println(System.getProperties());
		
		Frame frame = new Frame(); 				// creating instance of JFrame
		frame.setTitle(title); 			
		frame.setFocusable(true);
		//frame.setUndecorated(true);
		frame.setResizable(true);
		frame.setLocation(new Point(this.params.getWindow_xOffset(), this.params.getWindow_yOffset())); // top-left corner distance to the top-left corner of the screen/monitor
		//frame.setSize(this.params.getWindow_width_px(), this.params.getWindow_height_px());
		//frame.setExtendedState(Frame.MAXIMIZED_BOTH);
		
		//frame.setLayout(null);					//using no layout managers
		
		
		return frame;
	}
	
	private JFrame createWindowSWING(String title) {
		JFrame frame = new JFrame(); 				// creating instance of JFrame
		frame.setTitle(title); 			
		//frame.setUndecorated(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setFocusable(true);
		frame.setLocation(new Point(this.params.getWindow_xOffset(), this.params.getWindow_yOffset())); // top-left corner distance to the top-left corner of the screen/monitor
		frame.setSize(this.params.getWindow_width_px(), this.params.getWindow_height_px());
		frame.setResizable(false);
		//frame.setExtendedState(Frame.MAXIMIZED_BOTH);  // full screen mode
		
//       frame.getContentPane().setPreferredSize(new Dimension(400, 400));
//       frame.pack();
		
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
	
	private void debug(String tag, String msg) {
		System.out.println(">>> DEBUG >>> " + tag + " >>> " + msg);
	}
	
}
