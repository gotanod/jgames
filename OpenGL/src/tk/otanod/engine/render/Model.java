/*

Copyright (c) <27 dic. 2020> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.engine.render;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL4ES3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

public class Model implements GLEventListener {

    private int[] aAttribLocation = new int[10];
	private static final int ATTRIB_POSITION = 0;
	private static final int ATTRIB_COLOR = 1;
	
	private boolean isInitialized = false;
	
	public Model() {
		loadModelData();
	}
	
	@Override
	public void init(GLAutoDrawable drawable) {
		initialize(drawable);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		cleanUP(drawable);
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		if ( this.isInitialized ) {
			draw(drawable);
			debug("MODEL", "display model");
		} else {
			initialize(drawable);
			this.isInitialized = true;
			debug("MODEL", "init model");
		}
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		//
	}
	
	
	/*****************************************
	 * 
	 *****************************************/
	int nTriangles;
	float[] positions;
	float[] colors;
	float[] textureCoords;
	private void loadModelData() {
		
		nTriangles = 2;
		
		// Very important, camera is located at (0,0,0) looking to -Z axis
		// GL renders the cube [-1,1][-1,1][-1,1] but only the back half part z = ]0,-1] the other side is behind the camera [1,0]
		// Create vertex data
		positions = new float[] {	                 
				-0.8f,  -0.8f,  -0.5f, 		// bottom-left
				 0.8f,  -0.8f,  -0.5f, 		// bottom-right
				 0.8f,   0.8f,  -0.5f, 		// top-right
				 0.8f,   0.8f,  -0.5f, 		// top-right
				-0.8f,   0.8f,  -0.5f, 		// top-left
				-0.8f,  -0.8f,  -0.5f, 		// bottom-left		          
		};
		
		// Create color data
		colors = new float[] {							
				1.0f, 0.0f, 0.0f, 1.0f,		// bottom-left
				0.0f, 1.0f, 0.0f, 1.0f,		// bottom-right
				0.0f, 0.0f, 1.0f, 1.0f,		// top-right
				1.0f, 0.0f, 0.0f, 1.0f,		// top-right
				0.0f, 1.0f, 0.0f, 1.0f,		// top-left
				0.0f, 0.0f,	1.0f, 1.0f, 	// bottom-left
		};   
		
		// Create texture data
		textureCoords = new float[] {		
				0.0f, 0.0f,		// bottom-left
				1.0f, 0.0f, 	// bottom-right
				1.0f, 1.0f, 	// top-right
				1.0f, 1.0f, 	// top-right
				0.0f, 1.0f, 	// top-left
				0.0f, 0.0f,		// bottom-left    				
		};
	}
	
	private int[] vaos;
	private int nVAOs;
	private int[] vbos;
	private int nVBOs;
	private int programGLSL;
	
	private void initialize(GLAutoDrawable drawable) {
		// 1. Get context
		GL4ES3 gl = drawable.getGL().getGL4ES3();

		
		// 2. Generate Vertex Array Object (VAO)
		this.nVAOs = 1;
		this.vaos = new int[nVAOs]; 							// Our Vertex Array Object ID, gl functions need pointers, that's why we use an object (=pointer). An array pointer
		
		gl.glGenVertexArrays(1, vaos, 0); 				// Create our Vertex Array Object  // Vertex array object names returned by a call to glGenVertexArrays are not returned by subsequent calls, unless they are first deleted with glDeleteVertexArrays.
		int vao = this.vaos[0];
		gl.glBindVertexArray(vao); 						//	2.  Bind our Vertex Array Object so we can use it   
		
		// 3. Create a GLSL program
		this.programGLSL = getGLSLProgram(gl);
		
		// 3. Add data to the VAO	
		// 3.2 Create a VBO
		this.nVBOs = 2;
		this.vbos = new int[this.nVBOs];
		gl.glGenBuffers(this.nVBOs, this.vbos, 0);					// Buffer object names returned by a call to glGenBuffers are not returned by subsequent calls, unless they are first deleted with glDeleteBuffers.
		
		addVBOtoVAO(gl, positions, this.vbos[0], 3, ATTRIB_POSITION);
		addVBOtoVAO(gl, colors, this.vbos[1], 4, ATTRIB_COLOR);

		// 4. Unbind the VAO, just binding the default 0 VAO (0=no using VAOs)
		gl.glBindVertexArray(0); 							// Disable our Vertex Array Object  
	}

	private void addVBOtoVAO(GL4ES3 gl, float[] mData, int vbo, int sizePerElement, int attrib) {
		// 3.1 Prepare the data, we need a FloatBuffer instead of a Float Array
		FloatBuffer fbData = getFloatBuffer(mData);

		// 3.3 Transfer the data to the GPU
		final int BYTES_PER_FLOAT = Float.SIZE / Byte.SIZE;  				// float has 4 bytes
		int numBytes = (int) (mData.length * BYTES_PER_FLOAT);
		gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, vbo);
		gl.glBufferData(GL4ES3.GL_ARRAY_BUFFER, numBytes, fbData, GL4ES3.GL_STATIC_DRAW);
		//gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, 0);  //unbind
		
		// 3.4 
		//gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, vbo);			//	Bind vertices buffer
		int shaderPositionGLSL = this.aAttribLocation[attrib];
		//	gl.glBindVertexArray(this.vaos[0]);								// done in calling method
		gl.glEnableVertexAttribArray(shaderPositionGLSL);					//	Enable the vertex array
		debug("glEnableVertexAttribArray", "" + gl.glGetError());			// Error 1282 means that VAO is not active
		gl.glVertexAttribPointer(shaderPositionGLSL, sizePerElement, GL4ES3.GL_FLOAT, false , 0 , 0);	//	 glVertexAttribPointer( ShaderAttibIndex, sizePerElement, TypeValue, normalized?, stride, offset
		debug("glVertexAttribPointer", "" + gl.glGetError());

		// 3.5 Unbind the VBO
		gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, 0);						//	Unbind buffers
	}
	
	private FloatBuffer getFloatBuffer(float[] data) {
		// Bytes per Float (may vary on each system)
		final int BYTES_PER_FLOAT = Float.SIZE / Byte.SIZE;  				// float has 4 bytes
		// Stride
		//int vertexStride = COORDS_PER_VERTEX * BYTES_PER_VERTEX; 			// 3 o 4 floats per vertex  
		// Total bytes needed for the buffer
		int numBytes = (int) (data.length * BYTES_PER_FLOAT);
		
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(numBytes);       	
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		// create a floating point buffer from the ByteBuffer
		FloatBuffer fbData = bb.asFloatBuffer();
		// add the coordinates to the FloatBuffer
		fbData.put(data);
		// set the buffer to read the first coordinate
		fbData.position(0);         

		return(fbData);
	}

	private void draw(GLAutoDrawable drawable) {
		// 1. Get context
		GL4ES3 gl = drawable.getGL().getGL4ES3();

		// 2: Use Program 
		gl.glUseProgram(this.programGLSL);
		debug("glGetError", "" + gl.glGetError());
		
		// 4: Update the Uniforms
		
		// 5: draw the VAOs
		gl.glBindVertexArray(this.vaos[0]); 						// Bind our Vertex Array Object  
		gl.glDrawArrays(GL4ES3.GL_TRIANGLES, 0, this.nTriangles*3); // Draw triangles,  OFFSET, COUNT  FIRST OBJECT
		//DEBUG //gl.glDrawArrays(GL4ES3.GL_LINE_LOOP, 0, this.nTriangles*3); // Draw lines,  OFFSET, COUNT  FIRST OBJECT
		//DEBUG //gl.glDrawArrays(GL4ES3.GL_POINTS, 0, this.nTriangles*3); // Draw points,  OFFSET, COUNT  FIRST OBJECT
		
		// 6: Unbind
		gl.glBindVertexArray(0); 					// Unbind our Vertex Array Object or bind to default VAO
		gl.glUseProgram(0);
		
		
	}
	
	private void cleanUP(GLAutoDrawable drawable) {
		// 1. Get context
		GL4ES3 gl = drawable.getGL().getGL4ES3();

		// 2. Delete the VBOs
		gl.glDeleteBuffers(nVBOs, vbos, 0);
		
		// 3. Delete VAOs
		gl.glDeleteVertexArrays(nVAOs, vaos, 0);
		
		// 4. Delete Program
		gl.glDeleteProgram(this.programGLSL);
	}

	public int getGLSLProgram(GL4ES3 gl) {
		
		String sVertexShaderCode =
				  "#if __VERSION__ >= 130\n" 		// GLSL 130+ uses in and out
				+ "  #define attribute in\n" 		// instead of attribute and varying 
				+ "  #define varying out\n" 		// used by OpenGL 3 core and later. 
				+ "#endif\n" 
				
				+ "#ifdef GL_ES \n" 
				+ "  precision mediump float; \n" 		// Precision Qualifiers
				+ "  precision mediump int; \n" 		// GLSL ES section 4.5.2
				+ "#endif \n" 
				
				+ "attribute vec4  av4Position; \n" 	// the vertex shader
				+ "attribute vec4  av4Color; \n"		// Colors
				+ "varying   vec4  vColor; \n" 
				
				+ "void main(void) {\n" 
				+ "  vColor = av4Color; \n" 	
				+ "  gl_Position = av4Position; \n" 
				+ "} ";

		String sFragmentShaderCode =
				  "#if __VERSION__ >= 130\n" 
				+ "  #define varying in\n" 
				+ "  out vec4 mgl_FragColor;\n" 
				+ "  #define texture2D texture\n" 
				+ "  #define gl_FragColor mgl_FragColor\n" 
				+ "#endif\n" 

				+ "#ifdef GL_ES \n" 
				+ "  precision mediump float; \n" 
				+ "  precision mediump int; \n" 
				+ "#endif \n" 

				+ "varying 	vec4 vColor; \n" 
				+ "void main (void) { \n" 
				+ "   gl_FragColor = vColor; \n"
				+ "} ";
						
		if(gl.isGL3core()){
            System.out.println("GL3 core detected: explicit add #version 130 to shaders");
            sVertexShaderCode = "#version 130\n"+sVertexShaderCode;
            sFragmentShaderCode = "#version 130\n"+sFragmentShaderCode;
        }

		// STEP 1: Create Shaders
        // Create GPU shader handles
        // OpenGL ES returns an index id to be stored for future reference.
	    int vertexShader = gl.glCreateShader(GL4ES3.GL_VERTEX_SHADER);		// create the Vertex Shader
	    int fragmentShader = gl.glCreateShader(GL4ES3.GL_FRAGMENT_SHADER);	// create the Fragment Shader

	    // STEP 2: Source and Compile Shaders
        //Compile the vertexShader String into a program.
        String[] vlines = new String[] { sVertexShaderCode };
        int[] vlengths = new int[] { vlines[0].length() };
        gl.glShaderSource(vertexShader, vlines.length, vlines, vlengths, 0);	// @jogl
        //GLES31.glShaderSource(vertexShader,sVertexShaderCode);  // @ANDROID
        gl.glCompileShader(vertexShader);

        //Compile the fragmentShader String into a program.
        String[] flines = new String[] { sFragmentShaderCode };
        int[] flengths = new int[] { flines[0].length() };
        gl.glShaderSource(fragmentShader, flines.length, flines, flengths, 0);		// @jogl
        //GLES31.glShaderSource(fragmentShader,sFragmentShaderCode);  // @ANDROID
        gl.glCompileShader(fragmentShader);
        
        // STEP 3: Check for compile errors
        // Vertex check errors
        int[] compiled = new int[1];
        gl.glGetShaderiv(vertexShader, GL4ES3.GL_COMPILE_STATUS, compiled,0);
        if (compiled[0]!=0) {
        	debug("GLSL", "OK vertex shader compiled");
        } else {
            int[] logLength = new int[1];
            gl.glGetShaderiv(vertexShader, GL4ES3.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(vertexShader, logLength[0], (int[])null, 0, log, 0);
            // GLES31.glGetShaderInfoLog(vertexShader); 	// @android

            System.err.println("Error compiling the vertex shader: " + new String(log));
            System.exit(1);
        }

        // Fragment check errors
        gl.glGetShaderiv(fragmentShader, GL4ES3.GL_COMPILE_STATUS, compiled,0);
        if (compiled[0]!=0) { 
        	debug("GLSL", "OK fragment shader compiled");
        } else {
            int[] logLength = new int[1];
            gl.glGetShaderiv(fragmentShader, GL4ES3.GL_INFO_LOG_LENGTH, logLength, 0);

            byte[] log = new byte[logLength[0]];
            gl.glGetShaderInfoLog(fragmentShader, logLength[0], (int[])null, 0, log, 0);
            // GLES31.glGetShaderInfoLog(vertexShader); 	// @android

            System.err.println("Error compiling the fragment shader: " + new String(log));
            System.exit(1);
        }

        // STEP 4: Attach shaders to program
        //Each mShaderProgram must have
        //one vertex shader and one fragment shader.
        int mShaderProgram = gl.glCreateProgram();			// Create the Shader Program
        gl.glAttachShader(mShaderProgram, vertexShader);	// Attach compiled vertex shader
        gl.glAttachShader(mShaderProgram, fragmentShader);  // Attach compiled fragment shader
        
        // STEP 5prev: 
        // Active attributes that are not explicitly bound will be bound by the linker when glLinkProgram is called.
        //gl.glBindAttribLocation(mShaderProgram, 0, "av4Position");
        //gl.glBindAttribLocation(mShaderProgram, 1, "av4Color");
        //gl.glBindAttribLocation(mShaderProgram, 2, "uSampler");
        //gl.glBindAttribLocation(mShaderProgram, 3, "uMVPmatrix");
        
        // STEP 5: Link the program
        gl.glLinkProgram(mShaderProgram);

        int[] linked = new int[1];
        gl.glGetProgramiv(mShaderProgram, GL4ES3.GL_LINK_STATUS, linked, 0);
        
        int[] validated = new int[1];
        gl.glGetProgramiv(mShaderProgram, GL4ES3.GL_VALIDATE_STATUS, validated, 0);
        
        // STEP 6: Check linking errors
        if (linked[0]!=0) {
        	debug("GLSL", "OK program linked " + linked[0] + " validated " + validated[0]);
        } else {
            int[] logLength = new int[1];
            gl.glGetShaderiv(vertexShader, GL4ES3.GL_INFO_LOG_LENGTH, logLength, 0);
            byte[] log = new byte[logLength[0]];
            gl.glGetProgramInfoLog(mShaderProgram, logLength[0], (int[])null, 0, log, 0);	// @jogl
            // GLES31.glGetProgramInfoLog(mShaderProgram);		// @android

            System.err.println("Error compiling the vertex shader: " + new String(log));
            System.exit(1);
        }      
        
        
        
        // STEP 7: UNIFORM link to JAVA
        // Link GLSL with java
        this.aAttribLocation[ATTRIB_POSITION] = gl.glGetAttribLocation(mShaderProgram, "av4Position");
        this.aAttribLocation[ATTRIB_COLOR] 	  = gl.glGetAttribLocation(mShaderProgram, "av4Color");
        
        // STEP 8: Use the program
        //gl.glUseProgram(mShaderProgram);			// Done during the DRAW, each object can use different program
        // DRAW
        // STEP 9: Unbind the program
        //gl.glUseProgram(0);							// Unbind the current program
        
        return(mShaderProgram);
	}	

	private void debug(String tag, String msg) {
		//System.out.println(">>> DEBUG >>> " + tag + " >>> " + msg);
	}
}
