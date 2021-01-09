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

package tk.otanod.engine.terrain;


public class TerrainFlat {

	////////////////////////////////////////////////////////
	/// SINGLETON
	////////////////////////////////////////////////////////	
	private static TerrainFlat ourInstance = new TerrainFlat();

	public static TerrainFlat getInstance() {
		return ourInstance;
	}

	private TerrainFlat() {
	}
	
	/***********************
	 * FLAT TERRAIN
	 ************************/
	
	private static final float SIZE = 800;
	private static final int SLICES = 128;
	
	public RawTerrain create(){
		int count = SLICES * SLICES;
		
		float[] vertices = new float[count * 3];
		float[] normals = new float[count * 3];
		float[] textureCoords = new float[count*2];
		int[] indices = new int[6*(SLICES-1)*(SLICES-1)];
		
		// Step 1. create the v, vt, vn
		/**
		 *               S = number of SLICES
		 *   
		 *    ---------------------------------------------------------------> X   
		 *   |        0         1         2   .    .    .        S-1
		 *   |      S+0       S+1       S+2   .    .    .      2*S-1
		 *   |     2S+0      2S+1      2S+2   .    .    .      3*S-1 
		 *   |      .           .         .
		 *   |      .           .         . 
		 *   | (S-1)S+0  (S-1)S+1  (S-1)S+2   .    .    .      S*S-1 = (S-1)*S+(S-1)
		 *   |
		 *   v
		 *   Z
		 */
		int vertexPointer = 0;
		for(int i=0;i<SLICES;i++){
			for(int j=0;j<SLICES;j++){
				// VERTEX coordinates
				vertices[vertexPointer*3] = (float)j/((float)SLICES - 1) * SIZE;
				vertices[vertexPointer*3+1] = 0;
				vertices[vertexPointer*3+2] = (float)i/((float)SLICES - 1) * SIZE;
				// Flat terrain in XZ plane with normal (0,1,0)   Y axis is UP
				normals[vertexPointer*3] = 0;				
				normals[vertexPointer*3+1] = 1;		
				normals[vertexPointer*3+2] = 0;
				// TEXTURE coordinates
//				textureCoords[vertexPointer*2] = (float)j/((float)SLICES - 1);
//				textureCoords[vertexPointer*2+1] = (float)i/((float)SLICES - 1);
				textureCoords[vertexPointer*2] = (float)j;
				textureCoords[vertexPointer*2+1] = (float)i;
				
				vertexPointer++;
			}
		}
		
		// Step 2. create the indices
		int pointer = 0;
		for(int gz=0;gz<SLICES-1;gz++){
			for(int gx=0;gx<SLICES-1;gx++){
				// quad coordinates
				int topLeft = (gz*SLICES)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*SLICES)+gx;
				int bottomRight = bottomLeft + 1;
				// 2 triangle for each quad
				indices[pointer++] = topLeft;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = topRight;
				indices[pointer++] = topRight;
				indices[pointer++] = bottomLeft;
				indices[pointer++] = bottomRight;
			}
		}
		RawTerrain rawTerrain = new RawTerrain(pointer, vertices, textureCoords, normals, indices);
		
		return rawTerrain;
	}
	
}
