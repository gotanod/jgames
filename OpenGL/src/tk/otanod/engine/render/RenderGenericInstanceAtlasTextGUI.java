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
import java.util.Arrays;

import com.jogamp.opengl.GL4ES3;
import com.jogamp.opengl.GLAutoDrawable;
import tk.otanod.engine.camera.Camera;
import tk.otanod.engine.font.FontEffect;
import tk.otanod.engine.light.Light;
import tk.otanod.libIO.RawImage;
import tk.otanod.libMath.M4f;
import tk.otanod.libMath.V3f;
import tk.otanod.libOBJ.RawOBJ;


enum Action {
	INIT,
	DRAW,
	UPDATE;
}

public class RenderGenericInstanceAtlasTextGUI implements Model {

	//private boolean isInitialized = false;
	private Action action = Action.INIT;
	
	// World
	private float xWorld;
	private float yWorld;
	private float zWorld;
	private float xScale;
	private float yScale;
	private float zScale;
	// Per instance
	private int instances;
		
	// World matrix
	private M4f m4World;
	private float[] instancesM4World;
	
	// Texture
	private RawImage textureImage;
	private float[] instancesAtlasArea;
	
	// Font Effect
	private FontEffect fontEffect;
	
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
    private int[] aAttribLocation = new int[11];

	private static final int ATTRIB_POSITION = 0;
	private static final int ATTRIB_TEXTURE_COORDS = 1;
	private static final int ATTRIB_NORMAL = 2;
	private static final int ATTRIB_SAMPLER = 3;
	private static final int ATTRIB_M = 4;
	private static final int INSTANCE_M = 5;	
	private static final int INSTANCE_ATLAS = 6;
	private static final int ATTRIB_TEXT_WIDTH = 7;
	private static final int ATTRIB_TEXT_SHADOW_OFFSET = 8;
	private static final int ATTRIB_TEXT_COLOR = 9;
	private static final int ATTRIB_TEXT_BORDER_COLOR = 10;	
	
	public RenderGenericInstanceAtlasTextGUI(int instances, M4f m4World, float[] instancesM4View, float[] instancesAtlasArea, RawOBJ model, RawImage textureImage, FontEffect fontEffect) {
		
		// Model
		this.indices = model.getIndices();
		this.nElements = model.getnElements();
		this.positions = model.getPositions();
		this.textureCoords = model.getTextureCoords();
		this.normals = model.getNormals();
		
		// Texture
		this.textureImage = textureImage;
		this.instancesAtlasArea = instancesAtlasArea;
		
		// Font Effect
		this.fontEffect = fontEffect;
		
		// World position
		this.m4World = m4World;
		this.instances = instances;
		this.instancesM4World = instancesM4View;
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
		// NOT USED
	}

	@Override
	public void update(Light light) {
		// NOT USED	
	}

	@Override
	public void update(M4f projection) {
		// NOT USED
	}
	
	public void update(int instancesTextGUI, float[] instancesM4View, float[] instancesAtlasArea) {
		
		this.instances = instancesTextGUI;
		 
		// Texture
		this.instancesAtlasArea = instancesAtlasArea;
		
		// World position
		this.instancesM4World = instancesM4View;
		
		// Request the update, if already ready
		if ( this.action == Action.DRAW ) { 
			this.action = Action.UPDATE;
		}

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
		switch ( this.action ) {
		case INIT:
			initialize(drawable);
			draw(drawable);
			this.action = Action.DRAW;
			break;
		case DRAW:
			draw(drawable);
			break;
		case UPDATE:
			updateInstances(drawable);
			draw(drawable);					// added for high frequency updates, without it nothing is render during the update tick
			this.action = Action.DRAW;
			break;
		default:
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
		this.nVBOs = 6;
		this.vbos = new int[this.nVBOs];
		gl.glGenBuffers(this.nVBOs, this.vbos, 0);					// Buffer object names returned by a call to glGenBuffers are not returned by subsequent calls, unless they are first deleted with glDeleteBuffers.
		
		addEBOtoVAO(gl, this.indices, this.vbos[0]);
		addVBOtoVAO(gl, this.positions, this.vbos[1], 3, ATTRIB_POSITION);
		addVBOtoVAO(gl, this.textureCoords, this.vbos[2], 2, ATTRIB_TEXTURE_COORDS);
		addVBOtoVAO(gl, this.normals, this.vbos[3], 3, ATTRIB_NORMAL);
		
		addInstanceVBOtoVAO(gl, this.vbos[4], this.instancesM4World, this.aAttribLocation[INSTANCE_M], 16, 4);
		addInstanceVBOtoVAO(gl, this.vbos[5], this.instancesAtlasArea, this.aAttribLocation[INSTANCE_ATLAS], 4, 4);


		// 4. Unbind the VAO, just binding the default 0 VAO (0=no using VAOs)
		gl.glBindVertexArray(0); 							// Disable our Vertex Array Object

		
		// 5. Create the textures (They are NOT part of the VAO)
		if ( TextureUnitManager.getInstance().isTextureNumberLoaded(textureImage.getName()) ) {
			textureUnit = TextureUnitManager.getInstance().getTextureNumber(textureImage.getName());
		} else {
			this.nTextures = 1;
			this.textureIDs = new int[this.nTextures];
			gl.glGenTextures(this.nTextures, this.textureIDs, 0);
			this.textureUnit = TextureUnitManager.getInstance().getTextureNumber(textureImage.getName());
			createTexture(gl, this.textureIDs[0], this.textureUnit, textureImage);			
		}		
		        
	}
	
	private void updateInstances(GLAutoDrawable drawable) {
		// 1. Get context
		GL4ES3 gl = drawable.getGL().getGL4ES3();

		// 3. Update the VBOs in the VAO
		updateInstanceVBOtoVAO(gl, this.vbos[4], this.instancesM4World, this.aAttribLocation[INSTANCE_M], 16, 4);
		updateInstanceVBOtoVAO(gl, this.vbos[5], this.instancesAtlasArea, this.aAttribLocation[INSTANCE_ATLAS], 4, 4);
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
		debug("glEnableVertexAttribArray", "" + getGLErrorDesc(gl.glGetError()));			// Error 1282 means that VAO is not active
		gl.glVertexAttribPointer(shaderPositionGLSL, componentsPerVertex, GL4ES3.GL_FLOAT, false , 0 , 0);	//	 glVertexAttribPointer( ShaderAttibIndex, sizePerElement, TypeValue, to_be_normalized?, stride, offset
		debug("glVertexAttribPointer", "" + getGLErrorDesc(gl.glGetError()));

		// 3.5 Unbind the VBO
		gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, 0);						//	Unbind buffers
	}
	
	private void addInstanceVBOtoVAO(GL4ES3 gl, int vbo, float[] mData, int attrib_location, int stride, int elements) {
		// 3.1 Prepare the data, we need a FloatBuffer instead of a Float Array
		FloatBuffer fbData = getFloatBuffer(mData);
		
		// 3.2 Transfer the data to the GPU
		final int BYTES_PER_FLOAT = Float.SIZE / Byte.SIZE;  				// float has 4 bytes
		int numBytes = (int) (mData.length * BYTES_PER_FLOAT);
		gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, vbo);						// Enables the VBO, to write there the data and link it later with the VAO slot
		gl.glBufferData(GL4ES3.GL_ARRAY_BUFFER, numBytes, fbData, GL4ES3.GL_DYNAMIC_DRAW);	// transfers data to the VBO, DYNAMIC because it will changed 
		debug("glBufferData instances", getGLErrorDesc(gl.glGetError()));

		// 3.3 Add the VBO to the VAO 
		//gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, vbo);
		//	gl.glBindVertexArray(this.vaos[0]);								// done in calling method
		
		for ( int i = 0; i < (stride/elements); i++) {
			int shaderPositionGLSL = attrib_location + i;				// Get the slot used by the GLSL program, that we saved in the array aAttribLocation 	
			gl.glEnableVertexAttribArray(shaderPositionGLSL);					// Enable the VAO slot (matches the GLSL location) and link it with the previous bound VBO
			debug("glEnableVertexAttribArray", "" + getGLErrorDesc(gl.glGetError()));			// Error 1282 means that VAO is not active
			gl.glVertexAttribPointer(shaderPositionGLSL, elements, GL4ES3.GL_FLOAT, false , stride * BYTES_PER_FLOAT, i*elements*BYTES_PER_FLOAT);	//	 STRIDE AND OFFSET IN BYTES!!!!!!
			debug("glVertexAttribPointer", "" + getGLErrorDesc(gl.glGetError()));
			gl.glVertexAttribDivisor(shaderPositionGLSL, 1);
			debug("glVertexAttribDivisor", "" + getGLErrorDesc(gl.glGetError()));
		}
		
		// 3.5 Unbind the VBO
		gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, 0);						//	Unbind buffers
	}
	
	private void updateInstanceVBOtoVAO(GL4ES3 gl, int vbo, float[] mData, int attrib_location, int stride, int elements) {
		// 3.1 Prepare the data, we need a FloatBuffer instead of a Float Array
		FloatBuffer fbData = getFloatBuffer(mData);
		
		// 3.2 Transfer the data to the GPU
		final int BYTES_PER_FLOAT = Float.SIZE / Byte.SIZE;  				// float has 4 bytes
		int numBytes = (int) (mData.length * BYTES_PER_FLOAT);
		
		gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, vbo);						// Enables the VBO, to write there the data and link it later with the VAO slot
		
		int[] allocatedBytes = new int[1];		
		gl.glGetBufferParameteriv(GL4ES3.GL_ARRAY_BUFFER, GL4ES3.GL_BUFFER_SIZE, allocatedBytes, 0);		// glGetBufferParameteriv(target, pname, params, offset);
		int neededBytes = this.instances * stride * BYTES_PER_FLOAT; 
		if ( allocatedBytes[0] < neededBytes ) {
			// recreate the buffer, because buffer was not big enough
			gl.glBufferData(GL4ES3.GL_ARRAY_BUFFER, numBytes, fbData, GL4ES3.GL_DYNAMIC_DRAW);	// transfers data to the VBO, DYNAMIC because it will changed 
			//debug("glBufferData [" + attrib_location + "]", getGLErrorDesc(gl.glGetError()));		
		} else {
			// update the buffer
			gl.glBufferSubData(GL4ES3.GL_ARRAY_BUFFER, 0, numBytes, fbData);	// glBufferSubData(GLenum target, GLintptr offset, GLsizeiptr size,	const GLvoid * data);
			//debug("glBufferSubData [" + attrib_location + "]", getGLErrorDesc(gl.glGetError()));		
		}

		// 3.5 Unbind the VBO
		gl.glBindBuffer(GL4ES3.GL_ARRAY_BUFFER, 0);							//	Unbind buffers
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
		
		// 4.2 PVM matrix					
		gl.glUniformMatrix4fv(this.aAttribLocation[ATTRIB_M], 1, false, m4World.getElements(),	0);	// glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int value_offset)
		
		// 4.3 Light uniforms
		
		// 4.5 Camera/Eye position

		// 4.6 Per instance
		
		// 4.7 Text Effects
		gl.glUniform4f(this.aAttribLocation[ATTRIB_TEXT_WIDTH], this.fontEffect.getTextWidth(), this.fontEffect.getTextEdge(), this.fontEffect.getTextBorderWidth(), this.fontEffect.getTextBorderEdge());
		gl.glUniform2fv(this.aAttribLocation[ATTRIB_TEXT_SHADOW_OFFSET],	1, this.fontEffect.getTextOffset(),  		0);
		gl.glUniform3fv(this.aAttribLocation[ATTRIB_TEXT_COLOR],			1, this.fontEffect.getTextColor(),   		0);
		gl.glUniform3fv(this.aAttribLocation[ATTRIB_TEXT_BORDER_COLOR],		1, this.fontEffect.getTextBorderColor(),	0);
		
		
		// 5: draw the VAOs
		gl.glBindVertexArray(this.vaos[0]); 												// Bind our Vertex Array Object  
		

		if ( this.textureImage.isTransparent() ) {
			//gl.glDisable(GL4ES3.GL_DEPTH_TEST);					// we can disable the DEPTH_TEST if we draw the GUI as the last model
			gl.glEnable(GL4ES3.GL_BLEND);
			gl.glBlendFunc(GL4ES3.GL_SRC_ALPHA, GL4ES3.GL_ONE_MINUS_SRC_ALPHA);
		}
			
		// When using glDrawElements we're going to draw using indices provided in the element buffer object currently bound:
		// Leave the ELEMENT_ARRAY_BUFFER bound inside the VAO, just avoid the unbind after creating it. And you don't need to call it here if it is already bound!!!
		// gl.glBindBuffer(GL4ES3.GL_ELEMENT_ARRAY_BUFFER, this.vbos[0]);
		gl.glDrawElementsInstanced(GL4ES3.GL_TRIANGLES, this.nElements, GL4ES3.GL_UNSIGNED_INT, 0, instances);
		//debug("glDrawElementsInstanced", getGLErrorDesc(gl.glGetError()));

		if ( this.textureImage.isTransparent() ) {
			//gl.glEnable(GL4ES3.GL_DEPTH_TEST);
			gl.glDisable(GL4ES3.GL_BLEND);
		}

		
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
				
				+ "uniform    mat4  uMmatrix; \n"			// World matrix, from "string/sentence" to world coordinates
				+ "attribute  mat4  am4InstanceMmatrix; \n" // model matrix for each instance (square=glyph)

				+ "attribute  vec4  av4Position; \n" 		// the vertex shader QUAD
				+ "attribute  vec2  av2TextureCoord; \n"	// Texture coords QUAD
				
				+ "varying    vec2  vTextureCoord; \n"
				+ "varying    vec3  vWorldNormal; \n"
				+ "varying    vec4  vv4WorldPosition; \n"
				
				+ "attribute  vec4  a4fInstanceAtlas; \n"	// texture coords for each instance (square=glyph)
				+ "varying    vec4  v4fInstanceAtlas; \n"
								
				+ "void main(void) {\n"
				
				+ "  vTextureCoord = av2TextureCoord; \n"										// Pass-through				
				+ "  v4fInstanceAtlas = a4fInstanceAtlas;  \n"									// Pass-through
				+ "  gl_Position = uMmatrix * am4InstanceMmatrix * av4Position; \n"				// WorldPos = WorldMatrix * ModelMatrix * ModelPos
				
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

	            + "uniform   sampler2D uSampler; \n"											// it will receive 0 for GL_TEXTURE0, 1 for GL_TEXTURE1, 2 for GL_TEXTURE2, ... GL_TEXTURE15

	            + "varying   vec2  vTextureCoord; \n" 			
				+ "varying    vec4  v4fInstanceAtlas; \n"				
												
				+ "uniform vec4 textWidthEffect; \n"
				+ "uniform vec2 v2TextOffset; \n"				
				+ "uniform vec3 v3TextColor; \n"
				+ "uniform vec3 v3TextBorderColor; \n"
	            
				+ "void main (void) { \n"

				// Font Effects constants
				+ "   float textWidth = textWidthEffect.x; \n "
				+ "   float textEdge = textWidthEffect.y; \n "
				+ "   float textBorderWidth = textWidthEffect.z + textWidth; \n "
				+ "   float textBorderEdge = textWidthEffect.w; \n "
				
				// Texture atlas position
				//+ "   float atlasTextureSOffset = v4fInstanceAtlas.x; \n "
				//+ "   float atlasTextureTOffset = v4fInstanceAtlas.y; \n "
				//+ "   float atlasTextureSWidth = v4fInstanceAtlas.z; \n "
				//+ "   float atlasTextureTWidth = v4fInstanceAtlas.w; \n "
				
				//    Texture
				+ "   float s = vTextureCoord.s * v4fInstanceAtlas.z + v4fInstanceAtlas.x; \n"
				+ "   float t = (1.0 - vTextureCoord.t) * v4fInstanceAtlas.w + v4fInstanceAtlas.y; \n"				// without flipping, i.e. upside down, just switch the t axis of the quad texture coordinates

				+ "   float textDistance = 1.0 - texture2D(uSampler, vec2(s, t)).a; \n"
				+ "   float alpha = 1.0 - smoothstep(textWidth, textWidth + textEdge, textDistance); \n"			// smoothstep(lowEdge, upperEdge, value)
				
				+ "   float textBorderDistance = 1.0 - texture2D(uSampler, vec2(s, t) + v2TextOffset).a; \n"
				+ "   float alphaEdge = 1.0 - smoothstep(textBorderWidth, textBorderWidth + textBorderEdge, textBorderDistance); \n"
				
				+ "   float overallAlpha = alpha + (1.0 - alpha) * alphaEdge; \n"
				
				+ "   vec3 overallColor = mix(v3TextBorderColor, v3TextColor, alpha / overallAlpha); \n"
				+ "   vec4 textureColor = vec4(overallColor, overallAlpha); \n"								
				
				+ "   if ( textureColor.a <= 0.1 ) { discard; } \n"				// we can disable the DEPTH_TEST if we draw the GUI as the last model
				
				+ "   gl_FragColor =  textureColor; \n"					
			
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
        int[] nAttribs = new int[1];
        gl.glGetProgramiv(mShaderProgram, GL4ES3.GL_ACTIVE_ATTRIBUTES, nAttribs, 0);
        System.out.println("ATTRIBS ==> " + nAttribs[0]);
        int[] attribLength = new int[nAttribs[0]];
        int[] attribSize = new int[nAttribs[0]];
        int[] attribType = new int[nAttribs[0]];
        int maxAttribNameLength = 24;
        byte[] attribName = new byte[nAttribs[0] * maxAttribNameLength];
        
        for (int i=0; i< nAttribs[0]; i++) {
        	// glGetActiveAttrib(int program, int index, int bufSize, int[] length, int length_offset, int[] size, int size_offset, int[] type, int type_offset, byte[] name, int name_offset)
        	gl.glGetActiveAttrib(mShaderProgram, i, maxAttribNameLength, attribLength, i, attribSize, i, attribType, i, attribName, i * maxAttribNameLength);
        	// Type
        	// https://www.khronos.org/registry/OpenGL/api/GL/glext.h
        	String strAttrib = new String(Arrays.copyOfRange(attribName, i*maxAttribNameLength, i*maxAttribNameLength + attribLength[i]));
        	System.out.println(i + " ==> " + strAttrib + ", " + gl.glGetAttribLocation(mShaderProgram, strAttrib) +  ", " + attribLength[i] + ", " + attribSize[i] + ", " + attribType[i]);
        }
        
//        int[] nUniforms = new int[1];
//        gl.glGetProgramiv(mShaderProgram, GL4ES3.GL_ACTIVE_UNIFORMS, nUniforms, 0);
//        System.out.println("UNIFORMS ==> " + nUniforms[0]);
//        int[] uniformLength = new int[nUniforms[0]];
//        int[] uniformSize = new int[nUniforms[0]];
//        int[] uniformType = new int[nUniforms[0]];
//        int maxUniformNameLength = 24;
//        byte[] uniformName = new byte[nUniforms[0] * maxUniformNameLength];
//        for (int i=0; i< nUniforms[0]; i++) {
//        	gl.glGetActiveUniform(mShaderProgram, i, maxUniformNameLength, uniformLength, i, uniformSize, i, uniformType, i, uniformName, i * maxUniformNameLength);
//        	// Type
//        	// https://www.khronos.org/registry/OpenGL/api/GL/glext.h
//        	String strUniform = new String(Arrays.copyOfRange(uniformName, i*maxUniformNameLength, i*maxUniformNameLength + uniformLength[i]));
//        	System.out.println(i + " ==> " + strUniform + ", " + gl.glGetUniformLocation(mShaderProgram, strUniform) +  ", " + uniformLength[i] + ", " + uniformSize[i] + ", " + uniformType[i]);
//        }
        
        this.aAttribLocation[ATTRIB_POSITION] 			= gl.glGetAttribLocation(mShaderProgram, "av4Position");
        this.aAttribLocation[ATTRIB_TEXTURE_COORDS] 	= gl.glGetAttribLocation(mShaderProgram, "av2TextureCoord");        
        
        this.aAttribLocation[ATTRIB_SAMPLER]	    	= gl.glGetUniformLocation(mShaderProgram, "uSampler");      
        this.aAttribLocation[ATTRIB_M]					= gl.glGetUniformLocation(mShaderProgram, "uMmatrix");
        this.aAttribLocation[INSTANCE_M]  				= gl.glGetAttribLocation(mShaderProgram, "am4InstanceMmatrix");
        this.aAttribLocation[INSTANCE_ATLAS]  			= gl.glGetAttribLocation(mShaderProgram, "a4fInstanceAtlas");
                
        this.aAttribLocation[ATTRIB_TEXT_WIDTH]  			= gl.glGetUniformLocation(mShaderProgram, "textWidthEffect");
        this.aAttribLocation[ATTRIB_TEXT_SHADOW_OFFSET]		= gl.glGetUniformLocation(mShaderProgram, "v2TextOffset");
        this.aAttribLocation[ATTRIB_TEXT_COLOR]  			= gl.glGetUniformLocation(mShaderProgram, "v3TextColor");
        this.aAttribLocation[ATTRIB_TEXT_BORDER_COLOR] 		= gl.glGetUniformLocation(mShaderProgram, "v3TextBorderColor");
		
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

	public void createTexture(GL4ES3 gl, int textureID, int textureUnit, RawImage tex) {

		gl.glActiveTexture(GL4ES3.GL_TEXTURE0 + textureUnit);  										// activate the texture unit first before binding texture
		
        gl.glBindTexture(GL4ES3.GL_TEXTURE_2D, textureID);
        //gl.pixelStorei(GL2ES2.GL_UNPACK_FLIP_Y_WEBGL, true);
        
        // Scale up/down if the texture if smaller.      
        // GL_NEAREST - no filtering, no mipmaps
        // GL_LINEAR - filtering, no mipmaps
//        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MAG_FILTER, GL4ES3.GL_LINEAR);		// scale linearly when image smaller than texture
//        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MIN_FILTER, GL4ES3.GL_LINEAR);
        
        // Use mipmaps. Scale up/down if the texture if smaller.
        // GL_NEAREST_MIPMAP_NEAREST - no filtering, sharp switching between mipmaps
        // GL_NEAREST_MIPMAP_LINEAR - no filtering, smooth transition between mipmaps
        // GL_LINEAR_MIPMAP_NEAREST - filtering, sharp switching between mipmaps
        // GL_LINEAR_MIPMAP_LINEAR - filtering, smooth transition between mipmaps
        gl.glTexParameteri(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MAG_FILTER,   GL4ES3.GL_LINEAR);	// Magnifier only supports GL_LINEAR or GL_NEAREST
        gl.glTexParameteri(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MIN_FILTER,  GL4ES3.GL_LINEAR_MIPMAP_LINEAR);	// down
//        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MAX_LOD, 1000.0f);
//        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_MIN_LOD, -1000.0f);
        
        // what to do if not enough image
        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_WRAP_S, GL4ES3.GL_CLAMP_TO_EDGE);		// avoids the square border around transparent quads
        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_WRAP_T, GL4ES3.GL_CLAMP_TO_EDGE);		// avoids the square border around transparent quads
//        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_WRAP_S, GL4ES3.GL_REPEAT);				// to repeat the exture n times inside the quad
//        gl.glTexParameterf(GL4ES3.GL_TEXTURE_2D, GL4ES3.GL_TEXTURE_WRAP_T, GL4ES3.GL_REPEAT);				// to repeat the exture n times inside the quad
        
       	//System.out.println(">> TEXTURE 4 channels");
       	gl.glTexImage2D(GL4ES3.GL_TEXTURE_2D, 0, GL4ES3.GL_SRGB_ALPHA, tex.width, tex.height, 0, GL4ES3.GL_RGBA, GL4ES3.GL_UNSIGNED_BYTE, tex.byteDataBuffer);       	       	      	       	

       	// Generate MIPMAPs
        gl.glGenerateMipmap(GL4ES3.GL_TEXTURE_2D);

        //Sets the object texture to the new created texture
       	//gl.glBindTexture(GL4ES3.GL_TEXTURE_2D, 0);
       	
        return;
	}
	
	private void debug(String tag, String msg) {
		System.out.println(">>> DEBUG >>> " + tag + " >>> " + msg);
	}

	private String getGLErrorDesc(int errorCode) {
		// https://code.woboq.org/qt5/include/GL/gl.h.html
		// Section ==> /* Errors */
		String errorDesc;
		switch ( errorCode ) {
		case 0:
			errorDesc = "GL_NO_ERROR";
			break;
		case 0x0500:
			errorDesc = "GL_INVALID_ENUM";
			break;
		case 0x0501:
			errorDesc = "GL_INVALID_VALUE";
			break;
		case 0x0502:
			errorDesc = "GL_INVALID_OPERATION";
			break;
		case 0x0505:
			errorDesc = "GL_OUT_OF_MEMORY";
			break;		
		default:
			errorDesc = "unknown";
		}
		
		return errorDesc;
	}
	
}
