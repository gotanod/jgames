/*

Copyright (c) <31 dic. 2020> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.libOBJ;

public class RawOBJ {

	private float[] positions;
	private float[] textureCoords;
	private float[] normals;
	private int[] indices;
	private int nElements;
	
	public RawOBJ(int nElements, float[] positions, float[] textureCoords, float[] normals, int[] indices) {
		this.nElements = nElements;
		this.positions = positions;
		this.textureCoords = textureCoords;
		this.normals = normals;
		this.indices = indices;
	}

	public int getnElements() {
		return nElements;
	}
	
	public float[] getPositions() {
		return positions;
	}

	public float[] getTextureCoords() {
		return textureCoords;
	}
	
	public float[] getNormals() {
		return normals;
	}

	public int[] getIndices() {
		return indices;
	}
	
	public static RawOBJ buildQuad() {
		// A ---------------  D
		// |                  |
		// |                  |
		// |                  |
		// |                  |
		// B ---------------- C
		
		// Create vertex data
		float[] positions = new float[] {	                 
				 // ABCD
			 	 -1.0f,    1.0f,   0.0f,
				 -1.0f,   -1.0f,   0.0f,
				  1.0f,   -1.0f,   0.0f,
				  1.0f,    1.0f,   0.0f,
		};
		
		int[] indices = new int[] { 
				 0,  1,  3,  	// ABD
				 3,  1,  2, 	// DBC
		};
		
		int nElements = indices.length; 
		
		// Create texture data	
		// The origin of textures in OpenGL is the lower-left corner.
		float min = 0.0f;
		float max = 1.0f;
		float[] textureCoords = new float[] {	
				// ABCD
				min, max,
				min, min,
				max, min,
				max, max,    	
		};   
		
		RawOBJ quad = new RawOBJ(nElements, positions, textureCoords, textureCoords, indices);
		
		return quad;
	}
	
	public static RawOBJ buildTextQuad() {
		// A ---------------  D
		// |                  |
		// |                  |
		// |                  |
		// |                  |
		// B ---------------- C
		// (0,0)
		
		// Create vertex data
		float[] positions = new float[] {	                 
				// ABCD
				0.0f,    1.0f,   0.0f,
				0.0f,    0.0f,   0.0f,
				1.0f,    0.0f,   0.0f,
				1.0f,    1.0f,   0.0f,
		};
		
		int[] indices = new int[] { 
				0,  1,  3,  	// ABD
				3,  1,  2, 	// DBC
		};
		
		int nElements = indices.length; 
		
		// Create texture data	
		// The origin of textures in OpenGL is the lower-left corner.
		float min = 0.0f;
		float max = 1.0f;
		float[] textureCoords = new float[] {	
				// ABCD
				min, max,
				min, min,
				max, min,
				max, max,    	
		};
		
		// Create normals	
		float[] normals = new float[] {	
				// ABCD
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f
		};  
		
		float aux = (float) Math.sqrt(1.0 / 3.0);
		float[] roundedNormals = new float[] {	
				// ABCD
				-aux,  aux,  aux,
				-aux, -aux,  aux,
				 aux, -aux,  aux,
				 aux,  aux,  aux
		};  
		
		RawOBJ quad = new RawOBJ(nElements, positions, textureCoords, normals, indices);
//		RawOBJ quad = new RawOBJ(nElements, positions, textureCoords, roundedNormals, indices);
		
		return quad;
	}
	
	public static RawOBJ buildCube() {
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
		
		int[] indices = new int[] { 
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
		int nElements = indices.length; 
		
		float[] positions = new float[] {	                 
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
		float[] textureCoords = new float[] {	
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
		
		RawOBJ cube = new RawOBJ(nElements, positions, textureCoords, null, indices);
		
		return cube;
	}
	
	public static RawOBJ buildSkyBox() {
		// SKYBOX TRIANGLES MUST LOOK TO INSIDE CUBE!!!!
		
		
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
		
		int[] indices = new int[] { 
				0,  3,  1,  	// ADB
				1,  3,  2, 		// BDC
				1,  2,  5,		// BCF
				5,  2,  6,		// FCG
				2,  3,  6,		// CDG
				6,  3,  7,		// GDH
				3,  0,  7,		// DAH
				7,  0,  4, 		// HAE
				0,  1,  4, 		// ABE
				4,  1,  5, 		// EBF
				5,  6,  4, 		// FGE
				4,  6,  7  		// EGH
		};
		int nElements = indices.length; 
		
		float[] positions = new float[] {	                 
				-1.0f,   1.0f,  -1.0f,		// A = 0
				-1.0f,   1.0f,   1.0f,		// B = 1
				 1.0f,   1.0f,   1.0f,		// C = 2
				 1.0f,   1.0f,  -1.0f,		// D = 3
				-1.0f,  -1.0f,  -1.0f,		// E = 4
				-1.0f,  -1.0f,   1.0f,		// F = 5
				 1.0f,  -1.0f,   1.0f,		// G = 6
				 1.0f,  -1.0f,  -1.0f		// H = 7
		};
		
		RawOBJ cube = new RawOBJ(nElements, positions, null, null, indices);		// normals and textureCoords are not needed
		
		return cube;
	}
	
}
