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
import java.nio.IntBuffer;

import com.jogamp.opengl.GL4ES3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;

import tk.otanod.engine.camera.Camera;
import tk.otanod.engine.light.Light;
import tk.otanod.libIO.ImageFile;
import tk.otanod.libIO.RawImage;
import tk.otanod.libMath.M4f;
import tk.otanod.libMath.V3f;



public class ModelMVPIndicesTexture implements Model {

    private int[] aAttribLocation = new int[10];
	private static final int ATTRIB_POSITION = 0;
	private static final int ATTRIB_TEXTURE_COORDS = 1;
	private static final int ATTRIB_SAMPLER = 2;
	private static final int ATTRIB_MVP = 3;
	
	private boolean isInitialized = false;
	
	public ModelMVPIndicesTexture() {
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
		} else {
			initialize(drawable);
			this.isInitialized = true;
		}
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		//
	}
	
	
	/***************************************************************
	 * https://www.khronos.org/opengl/wiki/Vertex_Specification
	 * https://learnopengl.com/Getting-started/Hello-Triangle
	 ***************************************************************/
	private int nElements;
	private int[] indices;
	private float[] positions;
	private float[] textureCoords;
	
	private void loadModelData() {
		//                                     ...... A....
		//                         ......----               ----- .....                                
		//                  B ...                                       .... D                                               
		//                   |       --------......    ......-------         |                  
		//                   |                      C                        |                                         
		//                   |                      |                        |                   
		//                   |                      |                        |                  
		//                   |                      |  E                     |                    
		//                   F___                   |                  ______H                          
		//                          -------  ...... G.......  ------                                   

		//  A    -1.0f,  1.0f,  -1.0f,
		//  B    -1.0f,  1.0f,    1.0f,
		//  C     1.0f,  1.0f,    1.0f,
		//  D     1.0f,  1.0f,  -1.0f,

		//  E    -1.0f,  -1.0f,   -1.0f,
		//  F    -1.0f,  -1.0f,    1.0f,
		//  G     1.0f,  -1.0f,   1.0f,
		//  H     1.0f,  -1.0f,  -1.0f,
		
		
		// Very important, camera is located at (0,0,0) looking to -Z axis
		// GL renders the cube [-1,1][-1,1][-1,1] but only the back half part z = ]0,-1] the other side is behind the camera [1,0]
		// Create vertex data
		this.indices = new int[] { 
				 0,  1,  2,  	// ABD
				 2,  1,  3, 	// DBC
				 4,  5,  6,		// BFC
				 6,  5,  7,		// CFG
				 8,  9, 10,		// CGD
				10,  9, 11,		// DGH
				12, 13, 14,		// DHA
				14, 13, 15, 	// AHE
				16, 17, 18, 	// AEB
				18, 17, 19, 	// BEF
				20, 21, 22, 	// FEG
				22, 21, 23  	// GEH
		};
		this.nElements = indices.length; 
		
		this.positions = new float[] {	                 
				 // ABDC
			 	 -1.0f,    1.0f,  -1.0f,
				 -1.0f,    1.0f,   1.0f,
				  1.0f,    1.0f,  -1.0f,
				  1.0f,    1.0f,   1.0f,
				  // BFCG
				  -1.0f,   1.0f,   1.0f,
				  -1.0f,  -1.0f,   1.0f,
				   1.0f,   1.0f,   1.0f,
				   1.0f,  -1.0f,   1.0f,
				  // CGDH
				   1.0f,   1.0f,   1.0f,
				   1.0f,  -1.0f,   1.0f,
				   1.0f,   1.0f,  -1.0f,
				   1.0f,  -1.0f,  -1.0f,
				  // DHAE
				   1.0f,   1.0f,  -1.0f,
				   1.0f,  -1.0f,  -1.0f,
				  -1.0f,   1.0f,  -1.0f,
				  -1.0f,  -1.0f,  -1.0f,
				  // AEBF
				  -1.0f,   1.0f,  -1.0f,
				  -1.0f,  -1.0f,  -1.0f,
				  -1.0f,   1.0f,   1.0f,
				  -1.0f,  -1.0f,   1.0f,
				  // FEGH
				  -1.0f,  -1.0f,   1.0f,
				  -1.0f,  -1.0f,  -1.0f,
				   1.0f,  -1.0f,   1.0f,
				   1.0f,  -1.0f,  -1.0f
		};
		

		
		// Create texture data	
		float min = 0.0f;
		float max = 1.0f;
		this.textureCoords = new float[] {	
				// ABDC
				min, max,
				min, min,
				max, max,
				max, min,    	
				// BFCG
				min, max,
				min, min,
				max, max,
				max, min,    
				// CGDH
				min, max*0.5f,				// You can use only a small part of the texture.
				min, min,
				max*0.5f, max*0.5f,
				max*0.5f, min,    
				// DHAE
				min+0.5f, max,	 		// You can use only a small part of the texture, not only the bottom right
				min+0.5f, min+0.5f,
				max, max,
				max, min+0.5f,    
				// AEBF
				min, 2f*max,				// Texture coordinates = [0.0, 1.0] if you set higher than 1.0 ...
				min, min,					// the texture will repeat with GL_REPEAT
				2f*max, 2f*max,				// or clamp to the edge with GL_CLAMP_TO_EDGE
				2f*max, min,    
				// FEGH
				min, 3f*max,
				min, min,
				3f*max, 3f*max,
				3f*max, min	
		};   
	}
	
	private int nVAOs;
	private int[] vaos;
	private int nVBOs;
	private int[] vbos;
	private int nTextures;
	private int[] textureIDs;
	private int textureUnit;
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
		this.nVBOs = 3;
		this.vbos = new int[this.nVBOs];
		gl.glGenBuffers(this.nVBOs, this.vbos, 0);					// Buffer object names returned by a call to glGenBuffers are not returned by subsequent calls, unless they are first deleted with glDeleteBuffers.
		
		addEBOtoVAO(gl, this.indices, this.vbos[0]);
		addVBOtoVAO(gl, this.positions, this.vbos[1], 3, ATTRIB_POSITION);
		//addVBOtoVAO(gl, this.colors, this.vbos[2], 4, ATTRIB_COLOR);
		addVBOtoVAO(gl, this.textureCoords, this.vbos[2], 2, ATTRIB_TEXTURE_COORDS);

		// 4. Unbind the VAO, just binding the default 0 VAO (0=no using VAOs)
		gl.glBindVertexArray(0); 							// Disable our Vertex Array Object
		
		
		// 5. Create the textures (They are NOT part of the VAO)
		this.nTextures = 1;
		this.textureIDs = new int[this.nTextures];
		gl.glGenTextures(this.nTextures, this.textureIDs, 0);
		
		RawImage tex;
		textureUnit = TextureUnitManager.getInstance().getTextureNumber();
		gl.glActiveTexture(GL4ES3.GL_TEXTURE0 + textureUnit);  				// activate the texture unit first before binding texture
		tex = ImageFile.loadFlippedImageFile("res/drawable/exodia_part1.jpg");
		createTextureBitmapRGBA(gl, this.textureIDs[0], tex);
		
		tex = null;			// after creating the texture (GPU) the image is no longer needed
		
        
	}

	private void addEBOtoVAO(GL4ES3 gl, int[] mData, int ebo) {
		// 3.1 Prepare the data, we need a IntBuffer instead of a Int Array
		IntBuffer ibData = getIntBuffer(mData);

		// 3.3 Transfer the data to the GPU
		final int BYTES_PER_INT = Integer.SIZE / Byte.SIZE;  				// float has 4 bytes
		int numBytes = (int) (mData.length * BYTES_PER_INT);
		
		//fbVertices = null; // It is OK to release CPU vertices memory after transfer to GPU        
		gl.glBindBuffer(GL4ES3.GL_ELEMENT_ARRAY_BUFFER, ebo);
		gl.glBufferData(GL4ES3.GL_ELEMENT_ARRAY_BUFFER, numBytes, ibData, GL4ES3.GL_STATIC_DRAW);
		
		// 3.5 Unbind the EBO
		// VERY IMPORTANT
		// A VAO stores the glBindBuffer calls when the target is GL_ELEMENT_ARRAY_BUFFER. 
		// This also means it stores its unbind calls 
		// so make sure you don't unbind the element array buffer before unbinding your VAO, 
		// otherwise it doesn't have an EBO configured. 
		//gl.glBindBuffer(GL4ES3.GL_ELEMENT_ARRAY_BUFFER, 0);						//	NEVER unbind the ELEMENT_ARRAY_BUFFER inside teh VAO
	}

	private void addVBOtoVAO(GL4ES3 gl, float[] mData, int vbo, int componentsPerVertex, int attrib) {
		// 3.1 Prepare the data, we need a FloatBuffer instead of a Float Array
		FloatBuffer fbData = getFloatBuffer(mData);

		// 3.2 Transfer the data to the GPU
		final int BYTES_PER_FLOAT = Float.SIZE / Byte.SIZE;  				// float has 4 bytes
		int numBytes = (int) (mData.length * BYTES_PER_FLOAT);
		gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, vbo);						// Enables the VBO, to write there the data and link it later with the VAO slot
		gl.glBufferData(GL4ES3.GL_ARRAY_BUFFER, numBytes, fbData, GL4ES3.GL_STATIC_DRAW);	// transfers data to the VBO
		//gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, 0);						
		
		// 3.3 Add the VBO to the VAO 
		//gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, vbo);
		int shaderPositionGLSL = this.aAttribLocation[attrib];				// Get the slot used by the GLSL program, that we saved in the array aAttribLocation 	
		//	gl.glBindVertexArray(this.vaos[0]);								// done in calling method
		gl.glEnableVertexAttribArray(shaderPositionGLSL);					// Enable the VAO slot (matches the GLSL location) and link it with the previous bound VBO
		debug("glEnableVertexAttribArray", "" + gl.glGetError());			// Error 1282 means that VAO is not active
		gl.glVertexAttribPointer(shaderPositionGLSL, componentsPerVertex, GL4ES3.GL_FLOAT, false , 0 , 0);	//	 glVertexAttribPointer( ShaderAttibIndex, sizePerElement, TypeValue, normalized?, stride, offset
		debug("glVertexAttribPointer", "" + gl.glGetError());

		// 3.5 Unbind the VBO
		gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, 0);						//	Unbind buffers
	}
	
	private IntBuffer getIntBuffer(int[] data) {
		// Bytes per Int (may vary on each system)
		final int BYTES_PER_INT = Integer.SIZE / Byte.SIZE;  				
		// Stride
		//int vertexStride = COORDS_PER_VERTEX * BYTES_PER_VERTEX; 			  
		// Total bytes needed for the buffer
		int numBytes = (int) (data.length * BYTES_PER_INT);
		
		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(numBytes);       	
		// use the device hardware's native byte order
		bb.order(ByteOrder.nativeOrder());
		// create a int point buffer from the ByteBuffer
		IntBuffer ibData = bb.asIntBuffer();
		// add the coordinates to the IntBuffer
		ibData.put(data);
		// set the buffer to read the first coordinate
		ibData.position(0);         

		return(ibData);
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

	
	// Projection matrix
	// M4f m4Projection = new M4f(Camera.getOrthoProjectionMatrix(2.0f, 2.0f, 0.1f, 10.0f));
	private M4f m4Projection = new M4f(Camera.getProjectionMatrix(1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 100.0f));
	// M4f m4Projection = new M4f(Camera.getInfiniteProjectionMatrix(1.0f, -1.0f, 1.0f, -1.0f, 1.0f));
	
	// View Matrix
	private V3f v3Up = new V3f(0.0f, 1.0f, 0.0f); 			// +Y axis is up in our world
	private V3f v3Center = new V3f(0.0f, 0.0f, -1.0f);		// default: we look to -Z axis, Change to the center of the object (our textured cube)
	private V3f v3Eye = new V3f(0.0f, 0.0f, 0.0f);			// defaul: camera at (0,0,0)
	private M4f m4View = new M4f(Camera.getViewMatrixLookAt(v3Eye, v3Center, v3Up));
	private float eyeZ = -0.003f;							// modify the camera position to simulate a zoom in and zoom out effect
	private float eyeZMin = -0.9f;
	private float eyeZMax = 5.0f;
		
	// World matrix
	private M4f m4World = new M4f();
	private float angleRadY = 0.0f;							// we will rotate the model around Y axis
	private float angleRadX = 0.0f;							// we will rotate the model around X axis
	
	private void draw(GLAutoDrawable drawable) {
		// 1. Get context
		GL4ES3 gl = drawable.getGL().getGL4ES3();

		// 2: Use Program 
		gl.glUseProgram(this.programGLSL);
		
		// 4: Update the Uniforms
		// 4.1 Texture Sampler
		// Texture Units => GL_TEXTURE# are unique in OpenGL, so if another model changes the textureID associated with the texture Unit (GL_TEXTURE#) it changes for all the models
		// glActiveTexture(GL_TEXTURE#) ==> glBindTexture(textureID) ==> glTexImage2D(byteBufferData)
		// TextureID is linked with the real data (GPU buffer), so it is safer to relink Texture Unit with the TextureID before drawing
		// int textureID = this.textureIDs[0]; 
		// gl.glActiveTexture(GL4ES3.GL_TEXTURE0 + textureUnit);  						// you can avoid this call, if you don't reuse the Texture Unit between model
		// gl.glBindTexture(GL4ES3.GL_TEXTURE_2D, textureID);							// you can avoid this call, if you don't reuse the Texture Unit between model
		gl.glUniform1i(this.aAttribLocation[ATTRIB_SAMPLER], textureUnit);			// 0 for GL_TEXTURE0, 1 for GL_TEXTURE1, ..., 15 for GL_TEXTURE15
		
		// 4.2 MVP matrix	
		// View zoom in and out animation
		if ( v3Eye.z() >= eyeZMax ) { eyeZ *= -1; }
		if ( v3Eye.z() <= eyeZMin ) { eyeZ *= -1; }
		v3Eye.add(new V3f(0.0f, 0.0f, eyeZ));
		
		// Model rotation
		m4View = new M4f(Camera.getViewMatrixLookAt(v3Eye, v3Center, v3Up));
		angleRadY += 0.0031f;
		angleRadX += 0.0017f;
		
		// Calculate the resultant Project-View-Model matrix
		M4f PVM = m4World.clone().scale(0.5f, 0.5f, 0.5f).rotateYaxisCCW(angleRadY).rotateXaxisCCW(angleRadX).setTranslate(-1.0f,  1.0f,  -3.0f).preMultiply(m4View).preMultiply(m4Projection);
		
		gl.glUniformMatrix4fv(this.aAttribLocation[ATTRIB_MVP], 1, false, PVM.getElements(),	0);	// glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int value_offset)
		

		// 5: draw the VAOs
		gl.glBindVertexArray(this.vaos[0]); 												// Bind our Vertex Array Object  
		
		// When using glDrawElements we're going to draw using indices provided in the element buffer object currently bound:
		// Leave the ELEMENT_ARRAY_BUFFER bound inside the VAO, just avoid the unbind after creating it. And you don't need to call it here if it is already bound!!!
		// gl.glBindBuffer(GL4ES3.GL_ELEMENT_ARRAY_BUFFER, this.vbos[0]);						
		gl.glDrawElements(GL4ES3.GL_TRIANGLES, this.nElements, GL4ES3.GL_UNSIGNED_INT, 0); 	// DrawElements triangles, count, type,  OFFSET
		
		// 6: Unbind
		gl.glBindVertexArray(0); 					// Unbind our Vertex Array Object or bind to default VAO
		gl.glUseProgram(0);
		
		
	}
	
	private void cleanUP(GLAutoDrawable drawable) {
		// 1. Get context
		GL4ES3 gl = drawable.getGL().getGL4ES3();

		// 2. Delete the VBOs
		gl.glDeleteBuffers(this.nVBOs, this.vbos, 0);
		
		// 3. Delete VAOs
		gl.glDeleteVertexArrays(this.nVAOs, this.vaos, 0);
		
		// 4. Delete Program
		gl.glDeleteProgram(this.programGLSL);
		
		// 5. Delete textures
		gl.glDeleteTextures(this.nTextures, this.textureIDs, 0);
	}

	private int getGLSLProgram(GL4ES3 gl) {
		
		String sVertexShaderCode =
				  "#if __VERSION__ >= 130\n" 				// GLSL 130+ uses in and out
				+ "  #define attribute in\n" 				// instead of attribute and varying 
				+ "  #define varying out\n" 				// used by OpenGL 3 core and later. 
				+ "#endif\n" 
				
				+ "#ifdef GL_ES \n" 
				+ "  precision mediump float; \n" 			// Precision Qualifiers
				+ "  precision mediump int; \n" 			// GLSL ES section 4.5.2
				+ "#endif \n" 
				
				+ "uniform 	  mat4  uPVMmatrix;\n"			// PVM matrix, column major, pre-multiplied
				+ "attribute  vec4  av4Position; \n" 		// the vertex shader
				+ "attribute  vec2  av2TextureCoord; \n"	// Texture coords
				+ "varying    vec2  vTextureCoord; \n" 
				
				+ "void main(void) {\n" 
				+ "  vTextureCoord = av2TextureCoord; \n"
				+ "  gl_Position = uPVMmatrix * av4Position; \n"
				//+ "  gl_Position = av4Position; \n" 				// GLSL automatically add the w=1.0 if you pass 3 values instead of 4 
				//+ "  gl_Position = vec4(av3Position, 1.0); \n"	// explicity adding the fourth value
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

				+ "varying    vec2 vTextureCoord; \n" 
	            + "uniform    sampler2D uSampler; \n"		// it will receive 0 for GL_TEXTURE0, 1 for GL_TEXTURE1, 2 for GL_TEXTURE2, ... GL_TEXTURE15
				+ "void main (void) { \n" 
//				+ "   gl_FragColor = vColor; \n"
//				+ "   gl_FragColor = texture2D(uSampler, vec2(vTextureCoord.s, 1.0 - vTextureCoord.t)).abgr; \n"
//				+ "   gl_FragColor = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t)).abgr; \n"		// Image with ABGR format, loaded by GL as RGBA (default order) so we need to swizzle the components  // https://www.khronos.org/registry/OpenGL/extensions/ARB/ARB_texture_swizzle.txt
				+ "   gl_FragColor = texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t)); \n"		
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
        this.aAttribLocation[ATTRIB_POSITION] 		= gl.glGetAttribLocation(mShaderProgram, "av4Position");
        this.aAttribLocation[ATTRIB_TEXTURE_COORDS] = gl.glGetAttribLocation(mShaderProgram, "av2TextureCoord");        
        this.aAttribLocation[ATTRIB_SAMPLER]	    = gl.glGetUniformLocation(mShaderProgram, "uSampler");      
        this.aAttribLocation[ATTRIB_MVP]			= gl.glGetUniformLocation(mShaderProgram, "uPVMmatrix");  
        
        // STEP 8: Detach and delete the shaders, they are no longer needed after the program is linked and compiled
        gl.glDetachShader(mShaderProgram, vertexShader);
        gl.glDeleteShader(vertexShader);
        gl.glDetachShader(mShaderProgram, fragmentShader);
        gl.glDeleteShader(fragmentShader);
        
        // STEP 9: How to use it
        // gl.glUseProgram(mShaderProgram);			// Done during the DRAW, each object can use different program
        // DRAW
        // gl.glUseProgram(0);							// Unbind the current program
        
        return(mShaderProgram);
	}	

	public void createTextureBitmapRGBA(GL4ES3 gl, int textureID, RawImage tex) {
    	    	
        gl.glBindTexture(GL4ES3.GL_TEXTURE_2D, textureID);
        //gl.pixelStorei(GL2ES2.GL_UNPACK_FLIP_Y_WEBGL, true);
        // Scale up if the texture if smaller.      // scale linearly when image smaller than texture
        //gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MAG_FILTER, GL4ES3.GL_LINEAR);
        //gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MIN_FILTER, GL4ES3.GL_LINEAR);
        // Use mipmaps
        gl.glTexParameteri(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MAG_FILTER,   GL4ES3.GL_LINEAR_MIPMAP_LINEAR); // GL4ES3.GL_NEAREST_MIPMAP_NEAREST);  //
//        gl.glTexParameteri(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MAG_FILTER,   GL4ES3.GL_NEAREST_MIPMAP_NEAREST);  //
        gl.glTexParameteri(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MIN_FILTER,  GL4ES3.GL_LINEAR_MIPMAP_LINEAR); // GL4ES3.GL_NEAREST_MIPMAP_NEAREST);  //
//        gl.glTexParameteri(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MIN_FILTER,  GL4ES3.GL_NEAREST_MIPMAP_NEAREST);  //
        
        // what to do if not enough image
//        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_WRAP_S, GL4ES3.GL_CLAMP_TO_EDGE);
//        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_WRAP_T, GL4ES3.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_WRAP_S, GL4ES3.GL_REPEAT);
        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_WRAP_T, GL4ES3.GL_REPEAT);
        
       	//System.out.println(">> TEXTURE 4 channels");
       	//GL2ES2.texImage2D(GL2ES2.GL_TEXTURE_2D, 0, GL2ES2.GL_RGBA, GL2ES2.GL_RGBA, GL2ES2.GL_UNSIGNED_BYTE, bitmap);  
//       	gl.glTexImage2D(GL4ES3.GL_TEXTURE_2D, 0, GL4ES3.GL_RGBA, tex.width, tex.height, 0, GL4ES3.GL_RGBA, GL4ES3.GL_UNSIGNED_BYTE, tex.byteDataBuffer);       	       	      	       	
       	gl.glTexImage2D(GL4ES3.GL_TEXTURE_2D, 0, GL4ES3.GL_SRGB_ALPHA, tex.width, tex.height, 0, GL4ES3.GL_RGBA, GL4ES3.GL_UNSIGNED_BYTE, tex.byteDataBuffer);       	       	      	       	

       	// Generate MIPMAPs
        gl.glGenerateMipmap(GL4ES3.GL_TEXTURE_2D);
        //System.out.println("MIPMAP done");	

        //Sets the object texture to the new created texture
       	//gl.glBindTexture(GL4ES3.GL_TEXTURE_2D, 0);
       	
        return;
	}
	
	private void debug(String tag, String msg) {
		System.out.println(">>> DEBUG >>> " + tag + " >>> " + msg);
	}

	@Override
	public void update(float x, float y, float z, Camera camera, Light light, M4f projection) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updatePosition(float x, float y, float z) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updateScale(float xScale, float yScale, float zScale) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(Camera camera) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(Light light) {
		// TODO Auto-generated method stub
	}

	@Override
	public void update(M4f projection) {
		// TODO Auto-generated method stub
	}
}
