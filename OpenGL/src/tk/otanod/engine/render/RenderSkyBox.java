/*

Copyright (c) <6 ene. 2021> <jdperezg@yahoo.es> All rights reserved.

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
import tk.otanod.engine.camera.Camera;
import tk.otanod.engine.light.Light;
import tk.otanod.libIO.RawImage;
import tk.otanod.libIO.RawImagePack;
import tk.otanod.libMath.M4f;
import tk.otanod.libMath.V3f;
import tk.otanod.libOBJ.RawOBJ;



public class RenderSkyBox implements Model {

	private boolean isInitialized = false;
	
	// World
	private float xWorld;
	private float yWorld;
	private float zWorld;
	private float xScale;
	private float yScale;
	private float zScale;
	
	// Light
	private Light light;
	
	// Projection matrix
	private M4f m4Projection;
	// View Matrix
	private V3f v3Eye;
	private M4f m4View;
	// World matrix
	private M4f m4World;
	
	// Texture
	RawImagePack textureImagePack;
	
	// Model
	private int nElements;
	private int[] indices;
	private float[] positions;
	private float[] textureCoords;
	private float[] normals;
		
	// OpenGL - internal
	private int nVAOs;
	private int[] vaos;
	private int nVBOs;
	private int[] vbos;
	private int nTextures;
	private int[] textureIDs;
	private int textureUnit;
	private int programGLSL;
	
	// GLSL
    private int[] aAttribLocation = new int[9];
	private static final int ATTRIB_POSITION = 0;
	private static final int ATTRIB_SAMPLER = 1;
	private static final int ATTRIB_P = 2;
	private static final int ATTRIB_V = 3;
	private static final int ATTRIB_M = 4;
	private static final int ATTRIB_FOG_UPPER_LIMIT = 5;
	private static final int ATTRIB_FOG_LOWER_LIMIT = 6;
	private static final int ATTRIB_FOG_COLOR = 7;
	private static final int ATTRIB_EYE_POSITION = 8;

	private static final double ROTATION_SPEED = 0.00007;

	
	public RenderSkyBox(V3f position, V3f scale, RawOBJ model, RawImagePack textureSkyBoxPack, Camera camera, Light light, M4f projection) {
		
		// Model
		this.indices = model.getIndices();
		this.nElements = model.getnElements();
		this.positions = model.getPositions();
		this.textureCoords = model.getTextureCoords();
		this.normals = model.getNormals();
		
		// Texture
		this.textureImagePack = textureSkyBoxPack; 
		
		// World position
		updatePosition(position.x(), position.y(), position.z());
		// World scale
		updateScale(scale.x(), scale.y(), scale.z());
		// View matrix
		update(camera);
		// Light
		update(light);
		// Projection matrix
		update(projection);
		
	}
	
	@Override
	public void update(float x, float y, float z, Camera camera, Light light, M4f projection) {
		// World position
		updatePosition(x, y, z);
		// View matrix
		update(camera);
		// Light
		update(light);
		// Projection matrix
		update(projection);
	}

	@Override
	public void updatePosition(float x, float y, float z) {
		// World position
		this.xWorld = x;
		this.yWorld = y;
		this.zWorld = z;
		
		this.m4World = new M4f().scale(this.xScale, this.yScale, this.zScale).setTranslate(this.xWorld, this.yWorld, this.zWorld);
	}

	@Override
	public void updateScale(float xScale, float yScale, float zScale) {
		// World scale
		this.xScale = xScale;
		this.yScale = yScale;
		this.zScale = zScale;
		
		this.m4World = new M4f().scale(this.xScale, this.yScale, this.zScale).setTranslate(this.xWorld, this.yWorld, this.zWorld);		
	}
	
	@Override
	public void update(Camera camera) {
		// View matrix
		this.v3Eye = camera.getEye();
		this.m4View = camera.getLookAtViewMatrix();
	}

	@Override
	public void update(Light light) {
		// Light
		this.light = light;
	}

	@Override
	public void update(M4f projection) {
		// Projection matrix
		this.m4Projection = projection;
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
	
	private void initialize(GLAutoDrawable drawable) {
		// 1. Get context
		GL4ES3 gl = drawable.getGL().getGL4ES3();

		// 2. Create a GLSL program
		this.programGLSL = getGLSLProgram(gl);
		
		// 3. Generate Vertex Array Object (VAO)
		this.nVAOs = 1;
		this.vaos = new int[nVAOs]; 							// Our Vertex Array Object ID, gl functions need pointers, that's why we use an object (=pointer). An array pointer
		
		gl.glGenVertexArrays(1, vaos, 0); 				// Create our Vertex Array Object  // Vertex array object names returned by a call to glGenVertexArrays are not returned by subsequent calls, unless they are first deleted with glDeleteVertexArrays.
		int vao = this.vaos[0];
		gl.glBindVertexArray(vao); 						//	2.  Bind our Vertex Array Object so we can use it   
		
		
		// 3. Add data to the VAO	
		// 3.2 Create a VBO
		this.nVBOs = 2;
		this.vbos = new int[this.nVBOs];
		gl.glGenBuffers(this.nVBOs, this.vbos, 0);					// Buffer object names returned by a call to glGenBuffers are not returned by subsequent calls, unless they are first deleted with glDeleteBuffers.
		
		addEBOtoVAO(gl, this.indices, this.vbos[0]);
		addVBOtoVAO(gl, this.positions, this.vbos[1], 3, ATTRIB_POSITION);

		// 4. Unbind the VAO, just binding the default 0 VAO (0=no using VAOs)
		gl.glBindVertexArray(0); 							// Disable our Vertex Array Object

		
		// 5. Create the textures (They are NOT part of the VAO)
		this.nTextures = 1; 											// it is only ONE texture, with 6 sides
		this.textureIDs = new int[this.nTextures];
		gl.glGenTextures(this.nTextures, this.textureIDs, 0);
		this.textureUnit = TextureUnitManager.getInstance().getTextureNumber("skyBox");				// Get texture unit form centralized class to avoid conflict with other classes using also TEXTURE units
		createCubeTexture(gl, this.textureIDs[0], this.textureUnit);  
	      
	}

	private void createCubeTexture(GL4ES3 gl, int textureID, int textureUnit) {
		gl.glActiveTexture(GL4ES3.GL_TEXTURE0 + textureUnit);  									// activate the texture unit first before binding texture

		gl.glBindTexture(GL4ES3.GL_TEXTURE_CUBE_MAP, textureID);

		int sides = this.textureImagePack.getSize();
		for(int i=0; i<sides; i++) {
			RawImage textureImage = this.textureImagePack.getRawImage(i);
			gl.glTexImage2D(GL4ES3.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL4ES3.GL_SRGB_ALPHA, textureImage.width, textureImage.height, 0, GL4ES3.GL_RGBA, GL4ES3.GL_UNSIGNED_BYTE, textureImage.byteDataBuffer);
			debug("GL TEXTURE", textureImage.name + ", " + textureID + ", " + textureUnit); 
		}

		gl.glTexParameteri(GL4ES3.GL_TEXTURE_CUBE_MAP, GL4ES3.GL_TEXTURE_MAG_FILTER, GL4ES3.GL_LINEAR);
		gl.glTexParameteri(GL4ES3.GL_TEXTURE_CUBE_MAP, GL4ES3.GL_TEXTURE_MIN_FILTER, GL4ES3.GL_LINEAR);
		gl.glTexParameteri(GL4ES3.GL_TEXTURE_CUBE_MAP, GL4ES3.GL_TEXTURE_WRAP_S, GL4ES3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL4ES3.GL_TEXTURE_CUBE_MAP, GL4ES3.GL_TEXTURE_WRAP_T, GL4ES3.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL4ES3.GL_TEXTURE_CUBE_MAP, GL4ES3.GL_TEXTURE_WRAP_R, GL4ES3.GL_CLAMP_TO_EDGE);
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
		gl.glVertexAttribPointer(shaderPositionGLSL, componentsPerVertex, GL4ES3.GL_FLOAT, false , 0 , 0);	//	 glVertexAttribPointer( ShaderAttibIndex, sizePerElement, TypeValue, to_be_normalized?, stride, offset
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
		
	M4f m4Model = new M4f();
	
	private void draw(GLAutoDrawable drawable) {
		// 1. Get context
		GL4ES3 gl = drawable.getGL().getGL4ES3();

		// 2: Use Program 
		gl.glUseProgram(this.programGLSL);
		
		// 4: Update the Uniforms
		// 4.1 Texture Sampler
		// gl.glActiveTexture(GL4ES3.GL_TEXTURE0 + textureUnit);  						// you can avoid this call, if you don't reuse the Texture Unit between model
		// gl.glBindTexture(GL4ES3.GL_TEXTURE_2D, textureID);							// you can avoid this call, if you don't reuse the Texture Unit between model
		gl.glUniform1i(this.aAttribLocation[ATTRIB_SAMPLER], this.textureUnit);			// 0 for GL_TEXTURE0, 1 for GL_TEXTURE1, ..., 15 for GL_TEXTURE15
		
		// 4.2 PVM matrix					
		gl.glUniformMatrix4fv(this.aAttribLocation[ATTRIB_P], 1, false, m4Projection.getElements(),	0);	// glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int value_offset)
		gl.glUniformMatrix4fv(this.aAttribLocation[ATTRIB_V], 1, false, m4View.getElements(),	0);	// glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int value_offset)
		
		m4Model.rotateZaxisCW(ROTATION_SPEED);
		gl.glUniformMatrix3fv(this.aAttribLocation[ATTRIB_M], 1, false, m4Model.getM3Elements(),	0);	// glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int value_offset)
		
		// 4.3 Fog
		gl.glUniform1f(this.aAttribLocation[ATTRIB_FOG_UPPER_LIMIT], this.light.getFogUpperLimit());
		gl.glUniform1f(this.aAttribLocation[ATTRIB_FOG_LOWER_LIMIT], this.light.getFogLowerLimit());
		gl.glUniform3fv(this.aAttribLocation[ATTRIB_FOG_COLOR], 1, this.light.getFogColor(), 0);
		
		// 4.5 Camera/Eye position
		gl.glUniform3fv(this.aAttribLocation[ATTRIB_EYE_POSITION],  1, v3Eye.getFloats(),   0);
	
				
		// 5: draw the VAOs
		gl.glBindVertexArray(this.vaos[0]); 												// Bind our Vertex Array Object  
		
		// When using glDrawElements we're going to draw using indices provided in the element buffer object currently bound:
		// Leave the ELEMENT_ARRAY_BUFFER bound inside the VAO, just avoid the unbind after creating it. And you don't need to call it here if it is already bound!!!
		// gl.glBindBuffer(GL4ES3.GL_ELEMENT_ARRAY_BUFFER, this.vbos[0]); 
		gl.glDepthFunc(GL4ES3.GL_LEQUAL); 			// required for SkyBox trick pos.xyww
		gl.glDrawElements(GL4ES3.GL_TRIANGLES, this.nElements, GL4ES3.GL_UNSIGNED_INT, 0); 	// DrawElements triangles, count, type,  OFFSET
		gl.glDepthFunc(GL4ES3.GL_LESS);
		
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
				
				+ "uniform 	  mat4  uPmatrix; \n"			// P matrix, column major, pre-multiplied, projection space
				+ "uniform 	  mat4  uVmatrix; \n"			// V matrix, column major, pre-multiplied, from view matrix from camera position/angle,lookAt
				+ "uniform 	  mat3  uMmatrix; \n"			// M matrix, column major, pre-multiplied, from model rotation/scale/translation in the world
				+ "attribute  vec3  av3Position; \n" 		// the vertex shader
				
				+ "varying    vec3  vTextureCoord; \n"
				+ "varying    vec3  v3Position; \n"
				
				+ "void main(void) {\n" 
				
				+ "  vTextureCoord = uMmatrix * av3Position; \n"							// Pass-through
				
				// SkyBox with fog
				+ "  v3Position = av3Position; \n"											// Pass-through
				
				+ "  vec4 pos = uPmatrix * uVmatrix * vec4(av3Position, 0.0); \n"			// w=0.0 to avoid the translation!!! The current view matrix however transforms all the skybox's positions by rotating, scaling and translating them, so if the player moves, the cubemap moves as well! We want to remove the translation part of the view matrix so only rotation will affect the skybox's position vectors. 
//				+ "  gl_Position = vec4(pos.xy, pos.w*0.9999, pos.w); \n"					// The resulting normalized device coordinates will then always have a z value equal to 1.0: the maximum depth value. The skybox will as a result only be rendered wherever there are no objects visible (only then it will pass the depth test, everything else is in front of the skybox). 
				+ "  gl_Position = pos.xyww; \n"											// The resulting normalized device coordinates will then always have a z value equal to 1.0: the maximum depth value. The skybox will as a result only be rendered wherever there are no objects visible (only then it will pass the depth test, everything else is in front of the skybox). We do have to change the depth function a little by setting it to GL_LEQUAL instead of the default GL_LESS. 
				 				
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

				+ "#define    M_PI   3.1415926535897932384626433832795 \n"

				+ "varying   vec3  vTextureCoord; \n" 
				+ "varying    vec3  v3Position; \n"
				
	            + "uniform   samplerCube uSampler; \n"											// it will receive 0 for GL_TEXTURE0, 1 for GL_TEXTURE1, 2 for GL_TEXTURE2, ... GL_TEXTURE15
				+ "uniform   float 		uFogUpperLimit; \n"
				+ "uniform   float 		uFogLowerLimit; \n"
				+ "uniform   vec3    	uFogColor; \n"	
				+ "uniform   vec3  		uEyePosition; \n"


//				// http://fooplot.com/#W3sidHlwZSI6MCwiZXEiOiJlXigtKCgwLjAxNSp4KV4yLjUpKSIsImNvbG9yIjoiIzAwMDAwMCJ9LHsidHlwZSI6MTAwMCwid2luZG93IjpbIi0xODQuNzQxMTExMjk3NjI1NzQiLCIxODQuNzQxMTExMjk3NjI1NzQiLCItMS4yNDk5OTk5OTk5OTk5OTk4IiwiMS4yNDk5OTk5OTk5OTk5OTk4Il19XQ--
//				+ "const   	  float fogDensity = 0.015; \n"				// farZPlane in the Perspective matrix (0.028  for 100) (0.018 for 150) (0.01 for 330)				
//				+ "const      float fogGradient = 2.5; \n"	
//				+ "const      float maxTheta = 1.4; \n"	
            
				+ "void main (void) { \n"	
				
				// Sky Box without Fog
//				+ "   gl_FragColor =  texture(uSampler, vTextureCoord); \n"
				
				// Sky Box with fog (method 1 linear)
//				+ "   vec4 color =  texture(uSampler, vTextureCoord); \n"
//				//+ "   float fogVisibility = (vTextureCoord.y - uFogLowerLimit) / (uFogUpperLimit - uFogLowerLimit); \n"	// TextureCoord.y < uFogLowerLimit all FOG // TextureCoord.y > uFogUpperLimit no FOG  // between we have a linear mix
//				+ "   float fogVisibility = ((v3Position.y-0.035+uEyePosition.y/128.0) - uFogLowerLimit) / (uFogUpperLimit - uFogLowerLimit); \n"	// TextureCoord.y < uFogLowerLimit all FOG // TextureCoord.y > uFogUpperLimit no FOG  // between we have a linear mix
//				+ "   fogVisibility = clamp(fogVisibility, 0.0, 1.0); \n"
//				+ "   gl_FragColor =  mix(vec4(uFogColor, 1.0), color, fogVisibility); \n"
				
				// SkyBox with fog (method 2 gradient)
				// http://www.fooplot.com/#W3sidHlwZSI6MCwiZXEiOiIxLWVeKC03Kih4LTAuMDM1KSkiLCJjb2xvciI6IiMwMDAwMDAifSx7InR5cGUiOjEwMDAsIndpbmRvdyI6WyItMS4xODEzODM3MTM5NDIzMDU4IiwiMS4zMTg2MTYyODYwNTc2OTEiLCItMS4zOTEyNzgwNzYxNzE4NzMzIiwiMS4xMDg3MjE5MjM4MjgxMjM2Il19XQ--
//				+ "   vec4 color =  texture(uSampler, vTextureCoord); \n"
//				+ "   float fogVisibility = 1.0 - exp(-7.0*(v3Position.y-0.12+uEyePosition.y/128.0)); \n"		// 0.035 = MAX_TREE_HIGH / 128.0              CAMERA.y = [-128, 128]
//				+ "   fogVisibility = clamp(fogVisibility, 0.0, 1.0); \n"				
//				+ "   gl_FragColor =  mix(vec4(uFogColor, 1.0), color, fogVisibility); \n"
				
				// SkyBox with fog (method 3 SIGMOID)
				// http://www.fooplot.com/#W3sidHlwZSI6MCwiZXEiOiIxLygxK2VeKC0oeC0wLjIpKjI1KSkiLCJjb2xvciI6IiMwMDAwMDAifSx7InR5cGUiOjEwMDAsIndpbmRvdyI6WyItMi45ODMxNDE1MjY0NDIzMDI1IiwiMy4xMjAzNzQwOTg1NTc2ODc4IiwiLTMuMTkzMDM1ODg4NjcxODciLCIyLjkxMDQ3OTczNjMyODEyMDMiXX1d
				+ "   vec4 color =  texture(uSampler, vTextureCoord); \n"
				+ "   float fogVisibility = 1.0 / (1+exp(-(v3Position.y-0.2+uEyePosition.y/128.0)*25)); \n"		// 0.035 = MAX_TREE_HIGH / 128.0              CAMERA.y = [-128, 128]
				+ "   fogVisibility = clamp(fogVisibility, 0.0, 1.0); \n"				
				+ "   gl_FragColor =  mix(vec4(uFogColor, 1.0), color, fogVisibility); \n"
				
					
				+ "} ";
		
						
		if(gl.isGL3core()){
            debug("GLSL", "GL3 core detected: explicit add #version 130 to shaders");
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
        this.aAttribLocation[ATTRIB_POSITION] 		= gl.glGetAttribLocation(mShaderProgram, "av3Position");
        this.aAttribLocation[ATTRIB_SAMPLER]	    = gl.glGetUniformLocation(mShaderProgram, "uSampler");      
        this.aAttribLocation[ATTRIB_P]				= gl.glGetUniformLocation(mShaderProgram, "uPmatrix");  
        this.aAttribLocation[ATTRIB_V]				= gl.glGetUniformLocation(mShaderProgram, "uVmatrix");  
        this.aAttribLocation[ATTRIB_M]				= gl.glGetUniformLocation(mShaderProgram, "uMmatrix");  
        this.aAttribLocation[ATTRIB_FOG_UPPER_LIMIT]= gl.glGetUniformLocation(mShaderProgram, "uFogUpperLimit");  
        this.aAttribLocation[ATTRIB_FOG_LOWER_LIMIT]= gl.glGetUniformLocation(mShaderProgram, "uFogLowerLimit");  
        this.aAttribLocation[ATTRIB_FOG_COLOR]		= gl.glGetUniformLocation(mShaderProgram, "uFogColor");  
        this.aAttribLocation[ATTRIB_EYE_POSITION]  	= gl.glGetUniformLocation(mShaderProgram, "uEyePosition");

        
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
	
	private void debug(String tag, String msg) {
		//System.out.println(">>> DEBUG >>> " + tag + " >>> " + msg);
	}

}
