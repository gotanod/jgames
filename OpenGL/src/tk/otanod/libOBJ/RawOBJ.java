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


		
}
