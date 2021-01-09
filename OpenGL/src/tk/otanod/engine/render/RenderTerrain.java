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
import com.jogamp.opengl.GLEventListener;

import tk.otanod.engine.camera.Camera;
import tk.otanod.engine.light.Light;
import tk.otanod.engine.terrain.RawTerrain;
import tk.otanod.libIO.RawImage;
import tk.otanod.libMath.M4f;
import tk.otanod.libMath.V3f;



public class RenderTerrain implements Model {

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
	
	// Texture
	RawImage textureImage;
	
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
	private static final int ATTRIB_PV = 4;
	private static final int ATTRIB_M = 5;
	private static final int ATTRIB_LIGHT_POSITION = 6;
	private static final int ATTRIB_LIGHT_AMBIENT_COLOR = 7;
	private static final int ATTRIB_LIGHT_DIFFUSE_COLOR = 8;
	private static final int ATTRIB_LIGHT_SPECULAR_COLOR = 9;
	private static final int ATTRIB_EYE_POSITION = 10;
	
	public RenderTerrain(V3f position, V3f scale, RawTerrain model, RawImage textureImage, Camera camera, Light light, M4f projection) {	

		// Model
		this.indices = model.getIndices();
		this.nElements = model.getnElements();
		this.positions = model.getPositions();
		this.textureCoords = model.getTextureCoords();
		this.normals = model.getNormals();
		
		// Texture
		this.textureImage = textureImage; 
		
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
	}
	
	@Override
	public void updateScale(float xScale, float yScale, float zScale) {
		// World scale
		this.xScale = xScale;
		this.yScale = yScale;
		this.zScale = zScale;		
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
		this.nVBOs = 4;
		this.vbos = new int[this.nVBOs];
		gl.glGenBuffers(this.nVBOs, this.vbos, 0);					// Buffer object names returned by a call to glGenBuffers are not returned by subsequent calls, unless they are first deleted with glDeleteBuffers.
		
		addEBOtoVAO(gl, this.indices, this.vbos[0]);
		addVBOtoVAO(gl, this.positions, this.vbos[1], 3, ATTRIB_POSITION);
		addVBOtoVAO(gl, this.textureCoords, this.vbos[2], 2, ATTRIB_TEXTURE_COORDS);
		addVBOtoVAO(gl, this.normals, this.vbos[3], 3, ATTRIB_NORMAL);

		// 4. Unbind the VAO, just binding the default 0 VAO (0=no using VAOs)
		gl.glBindVertexArray(0); 							// Disable our Vertex Array Object
		
		
		// 5. Create the textures (They are NOT part of the VAO)
		this.nTextures = 1;
		this.textureIDs = new int[this.nTextures];
		gl.glGenTextures(this.nTextures, this.textureIDs, 0);
		
		textureUnit = TextureUnitManager.getInstance().getTextureNumber();
		gl.glActiveTexture(GL4ES3.GL_TEXTURE0 + textureUnit);  				// activate the texture unit first before binding texture
		createTextureBitmapRGBA(gl, this.textureIDs[0], textureImage);
		
		textureImage = null;			// after creating the texture (GPU) the image is no longer needed
		
        
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
		M4f M = new M4f().scale(1.0f, 1.0f, 1.0f).setTranslate(this.xWorld, this.yWorld, this.zWorld);
		M4f PV = m4View.clone().preMultiply(m4Projection);
		
		gl.glUniformMatrix4fv(this.aAttribLocation[ATTRIB_PV], 1, false, PV.getElements(),	0);	// glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int value_offset)
		gl.glUniformMatrix4fv(this.aAttribLocation[ATTRIB_M], 1, false, M.getElements(),	0);	// glUniformMatrix4fv(int location, int count, boolean transpose, float[] value, int value_offset)
		
		// 4.3 Light uniforms
		gl.glUniform3fv(this.aAttribLocation[ATTRIB_LIGHT_POSITION],       1, light.getPosition(),      0);
		gl.glUniform3fv(this.aAttribLocation[ATTRIB_LIGHT_AMBIENT_COLOR],  1, light.getAmbientColor(),  0);
		gl.glUniform3fv(this.aAttribLocation[ATTRIB_LIGHT_DIFFUSE_COLOR],  1, light.getDiffuseColor(),  0);
		gl.glUniform3fv(this.aAttribLocation[ATTRIB_LIGHT_SPECULAR_COLOR], 1, light.getSpecularColor(), 0);
		
		// 4.5 Camera/Eye position
		gl.glUniform3fv(this.aAttribLocation[ATTRIB_EYE_POSITION],  1, v3Eye.getFloats(),   0);
		
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
				
				+ "uniform 	  mat4  uPVmatrix; \n"			// PV matrix, column major, pre-multiplied, from world to view and projection space
				+ "uniform    mat4  uMmatrix; \n"			// Model matrix, from model to world
				+ "attribute  vec4  av4Position; \n" 		// the vertex shader
				+ "attribute  vec2  av2TextureCoord; \n"	// Texture coords
				+ "attribute  vec3  av3Normal; \n"
				
				+ "varying    vec2  vTextureCoord; \n"
				+ "varying    vec3  vWorldNormal; \n"
				+ "varying    vec4  vv4WorldPosition; \n"
				
				+ "void main(void) {\n" 
				+ "  vTextureCoord = av2TextureCoord; \n"										// Pass-through
				
				+ "  vv4WorldPosition = uMmatrix * av4Position; \n"								// Vertex World position
				+ "  gl_Position = uPVmatrix * vv4WorldPosition; \n"							// Vertex position in Projection/View/World
				 
				+ "  vWorldNormal = normalize((uMmatrix * vec4(av3Normal, 0.0)).xyz); \n"		// Normal vector in the world (from model to world) w=0.0 to ignore translations, normalize to ignore scales, only rotations affect the normal vector
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

				+ "varying   vec2  vTextureCoord; \n" 
	            + "uniform   sampler2D uSampler; \n"											// it will receive 0 for GL_TEXTURE0, 1 for GL_TEXTURE1, 2 for GL_TEXTURE2, ... GL_TEXTURE15
				+ "varying   vec3  vWorldNormal; \n"
				+ "varying   vec4  vv4WorldPosition; \n"
				
				+ "uniform struct Light {\n"
				+ "   vec3  position; \n"				
				+ "   vec3  ambientColor; \n"
				+ "   vec3  diffuseColor; \n"
				+ "   vec3  specularColor; \n"
				+ "} uLight; \n"
				
				+ "uniform   vec3  uEyePosition; \n"
	            
				+ "void main (void) { \n"
				
				//	 Normalize the fragment normal ==> Interpolating normals into a face can (and almost always will) result in a shortening of the normal. That's why the highlight is darker in the center of a face and brighter at corners and edges. If you do this, just re-normalize the normal in the fragment shader:
				+ "   vec3 fragmentWorldNormal = normalize(vWorldNormal); \n"
				
				//    ambient
				+ "   vec4 ambient = vec4(uLight.ambientColor, 1.0); \n"
				
				+ "   vec3 localUnitToLight = normalize(uLight.position - vv4WorldPosition.xyz); \n"
				//    diffuse
				+ "   float diffuseCoefficient = dot(fragmentWorldNormal, localUnitToLight); \n"							// angle between Light and Normal
				+ "   diffuseCoefficient = clamp(diffuseCoefficient, 0.0, 1.0); \n"											// clamp to range [0,1]. clamp returns the value of x constrained to the range minVal to maxVal. The returned value is computed as min(max(x, minVal), maxVal).
				+ "   vec4 diffuse = diffuseCoefficient * vec4(uLight.diffuseColor, 1.0); \n"									
			    
				//    specular
				+ "   float specularCoefficient = 0.0; \n"
				+ "   if ( diffuseCoefficient > 0.0 ) { \n"																	// no need to calculate specular light if he surface is not looking to the light source
				+ "       vec3 reflected = reflect(-localUnitToLight, fragmentWorldNormal); \n"								// Incident, Normal    ORDER is important!!!!!
				+ "       vec3 vUnitToEye = normalize(uEyePosition - vv4WorldPosition.xyz); \n"
				+ "       specularCoefficient = dot(reflected, vUnitToEye); \n"
				+ "       specularCoefficient = clamp(specularCoefficient, 0.0, 1.0); \n"
				+ "   } \n"
				+ "   vec4 specular = pow(specularCoefficient, 1.0) * vec4(uLight.specularColor, 1.0); \n"			// shininessConstant = 1.0
				
				//    Final color
				+ "   gl_FragColor =  ( ambient + (diffuse + specular) ) * texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t)); \n"		// Image with RGBA color format				
//				+ "   gl_FragColor =  texture2D(uSampler, vec2(vTextureCoord.s, vTextureCoord.t)); \n"		// Image with RGBA color format				
//				+ "   gl_FragColor =  vec4(1.0, 0.0, 0.3, 1.0); \n"		// Image with RGBA color format				
				
				//    Gamma correction			// done by GL gl.glEnable(GL4ES3.GL_FRAMEBUFFER_SRGB);
				// + "   float gamma = 2.2; \n"										
				// + "   gl_FragColor.rgb = pow(gl_FragColor.rgb, vec3(1.0/gamma)); \n"				
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
        this.aAttribLocation[ATTRIB_NORMAL] 		= gl.glGetAttribLocation(mShaderProgram, "av3Normal");        
        this.aAttribLocation[ATTRIB_SAMPLER]	    = gl.glGetUniformLocation(mShaderProgram, "uSampler");      
        this.aAttribLocation[ATTRIB_PV]				= gl.glGetUniformLocation(mShaderProgram, "uPVmatrix");  
        this.aAttribLocation[ATTRIB_M]				= gl.glGetUniformLocation(mShaderProgram, "uMmatrix");
        this.aAttribLocation[ATTRIB_LIGHT_POSITION]			= gl.glGetUniformLocation(mShaderProgram, "uLight.position");
        this.aAttribLocation[ATTRIB_LIGHT_AMBIENT_COLOR]	= gl.glGetUniformLocation(mShaderProgram, "uLight.ambientColor");
        this.aAttribLocation[ATTRIB_LIGHT_DIFFUSE_COLOR]	= gl.glGetUniformLocation(mShaderProgram, "uLight.diffuseColor");
        this.aAttribLocation[ATTRIB_LIGHT_SPECULAR_COLOR]	= gl.glGetUniformLocation(mShaderProgram, "uLight.specularColor");
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

}
