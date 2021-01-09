package tk.otanod.engine.render;

import java.util.List;

import com.jogamp.opengl.GL4ES3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

/*

Copyright (c) <17 nov. 2020> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

public class CanvasListener implements GLEventListener {

	private List<Model> models;
	private int width;
	private int height;
	
	public CanvasListener(List<Model> models, int width, int height) {
		this.models = models;
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		
		System.out.println("init started");
		System.out.println("GL thread : " + Thread.currentThread().getName());
		
		GL4ES3 gl = drawable.getGL().getGL4ES3();
		
		//////////////////////////////////////////////////////////////
		/// INITIALIZE OPEN GL
		//////////////////////////////////////////////////////////////

		// Display GPU card details
		// cGLlibrary.displayGPUdetails(gl);

		// TODO: avoid limitation to 60FPS
		gl.setSwapInterval(0); // MAX FPS, not limited to 60 FPS

		/**
		 * http://forum.jogamp.org/GLCanvas-Built-In-FPS-cap-td4028958.html
		 * depending on the GL profile (> GL2) default it swap interval
		 * == 1, i.e. vsync of your screen. this is also true for
		 * OSX/CALayer. use gl.setSwapInterval(0) to disable it.
		 */

		// Avoid aliasing squared borders
		// https://learnopengl.com/Advanced-OpenGL/Anti-Aliasing
		// Together with the capabilities when creating the canvas 
		// capabilities.setSampleBuffers(true);  AND   capabilities.setNumSamples(4);
		 gl.glEnable(GL4ES3.GL_MULTISAMPLE);

		
		gl.glEnable(GL4ES3.GL_FRAMEBUFFER_SRGB);					// Enable gamma correction when drawing to frame buffer/screen BUT what about MATERIALS!!! Materials values MUST be in RGB linear space

		// Blend for transparency
//		gl.glEnable(GL4ES3.GL_BLEND);
//		gl.glBlendFunc(GL4ES3.GL_SRC_ALPHA, GL4ES3.GL_ONE_MINUS_SRC_ALPHA);
		// gl.glBlendFuncSeparate(GL4ES3.GL_SRC_ALPHA,
		// GL4ES3.GL_ONE_MINUS_SRC_ALPHA, GL4ES3.GL_ONE,
		// GL4ES3.GL_ZERO);
		// gl.glBlendFuncSeparate(GL4ES3.GL_SRC_ALPHA,
		// GL4ES3.GL_ONE_MINUS_SRC_ALPHA, GL4ES3.GL_SRC_ALPHA,
		// GL4ES3.GL_ONE_MINUS_SRC_ALPHA);

		// gl.glEnable(GL4ES3.GL_STENCIL_TEST);
		// gl.glStencilFunc(GL4ES3.GL_GEQUAL, 0x3F, 0xFF);
		// gl.glStencilOp(GL4ES3.GL_KEEP, GL4ES3.GL_KEEP,
		// GL4ES3.GL_REPLACE);
		// gl.glStencilMask(0xFF);
		// gl.glClear( GL4ES3.GL_STENCIL_BUFFER_BIT );

		// https://www.khronos.org/registry/OpenGL-Refpages/es2.0/xhtml/glBlendFuncSeparate.xml
		// gl.glBlendFuncSeparate(GL4ES3.GL_SRC_ALPHA,
		// GL4ES3.GL_ONE_MINUS_SRC_ALPHA, GL4ES3.GL_ONE, GL4ES3.GL_ONE);

		gl.glClearColor(0.3f, 0.3f, 0.3f, 1.0f);

		// reversed z (right handed in projection matrix)
		// ==> play with glDepthFunc(GL_GREATER) and glClearDepthf(0f)
		// ==> depth buffer lower for far objects
		// ==> play with glDepthFunc(GL_LEQUAL) and glClearDepthf(1f)
		// ==> depth buffer higher for far objects
		// the camera has a default position and orientation. By
		// default, the camera is situated at the origin, points down
		// the negative z-axis, and has an up-vector of (0, 1, 0).
		// Depth Buffer enabled, depth buffer higher value for far
		// objects
		gl.glEnable(GL4ES3.GL_DEPTH_TEST);
		gl.glDepthFunc(GL4ES3.GL_LESS); // GL_LEQUAL
		gl.glClearDepthf(1f);
		// Depth buffer reversed
		// gl.glDepthFunc(GL4ES3.GL_GREATER);
		// gl.glClearDepthf(0f);

		// Cull enabled, remove faces looking to -z (after all matrix movements)
		gl.glEnable(GL4ES3.GL_CULL_FACE); 	// Blending can only show objects behind but not the internal object
		gl.glFrontFace(GL4ES3.GL_CCW); 		// Counter Clockwise (right hand)
		// Front face reversed
		// gl.glFrontFace(GL4ES3.GL_CW); 	// Counter Clockwise (left hand)
		
		gl.glCullFace(GL4ES3.GL_BACK); 		// removed the faces NOT looking to camera (camera is at (0,0,0), removes triangles looking to -Z infinite))
		// Cull reversed
		//gl.glCullFace(GL4ES3.GL_FRONT); 	// removed the faces NOT looking to camera (to +Z (where the camera is))

		//gl.glLineWidth(10); 				// for TEST when using lines

		// String s = gl.glGetString(GL4ES3.GL_EXTENSIONS);
		// if (s.contains("GL_IMG_texture_compression_pvrtc")){
		// 		// Use PVR compressed textures
		// 		debug("GL", "PVRTC (PowerVR texture compression). Supported by devices with PowerVR GPUs (Nexus S, Kindle fire, etc.).");
		// } else if (s.contains("GL_AMD_compressed_ATC_texture") || s.contains("GL_ATI_texture_compression_atitc")){
		// 		// Load ATI Textures
		// 		// https://developer.nvidia.com/gpu-accelerated-texture-compression
		// 		debug("GL", "ATITC (ATI texture compression). Used in devices with Adreno GPU from Qualcomm (Nexus One, etc.).");
		// } else if (s.contains("GL_OES_texture_compression_S3TC") || s.contains("GL_EXT_texture_compression_s3tc")){
		// 		// Use DTX Textures
		// 		// https://developer.nvidia.com/gpu-accelerated-texture-compression
		// 		debug("GL", "S3TC (S3 texture compression). This texture compression is used in the NVIDIA chipset integrated devices (Motorola Xoom, etc.)");
		// }else{
		// 		//Handle no texture compression founded.
		// 		debug("GL", "Maybe ETC1 (Ericsson texture compression). This format is supported by all Android phones. But, it doesn't support an alpha channel, so can only be used for opaque textures.");
		// }

		// Get current canvas dimensions
		gl.glViewport(6, 29, width-6, height-29);				// BUG: if you do not set up the view port, it uses full screen and it goes much slower!!!!
		
		System.out.println("init completed");
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		// 1. Get the context
		GL4ES3 gl = drawable.getGL().getGL4ES3();

		// 2. Clear the screen color and depth buffer
		gl.glClearColor(0.5f, 0.5f, 0.95f, 1.0f);
		gl.glClear(GL4ES3.GL_COLOR_BUFFER_BIT | GL4ES3.GL_DEPTH_BUFFER_BIT);

		// 3. Draw all models
		for( Model model: models ) {
			model.display(drawable);
		}
		
		// 4. update and display the FPS in the console
		displayFPS(5);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL4ES3 gl = drawable.getGL().getGL4ES3();
		this.width = width;
		this.height = height;
		gl.glViewport(x, y, width, height);
		
		System.out.println(String.format("Reshape => x: %d y: %d width: %d height: %d", x,y,width,height));
	}
		
	private long time1 = System.nanoTime();
	private long time2 = System.nanoTime();
	private long delta = 0;
	private int FPS = 0;
	
	private void displayFPS(int everySeconds) {
		long everyNanoSeconds = (long) (everySeconds * 1E9);
		
		time2 = System.nanoTime();
		delta += time2 - time1;
		time1 = time2;
		if ( delta > everyNanoSeconds ) {
			System.out.println("FPS: " + FPS/everySeconds);
			delta -= everyNanoSeconds;
			FPS=0;
		} else {
			FPS++;
		}
	}

}
