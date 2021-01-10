/*

Copyright (c) <10 ene. 2021> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.libIO;

public class RawImagePack {

	private String[] nameImages;
	private RawImage[] rawImages;
	private int size;

	public RawImagePack(String[] imageNames) {
		this.nameImages = imageNames;

		this.size = this.nameImages.length;
		
		this.rawImages = new RawImage[this.size];
		
		for (int i=0; i<this.size; i++) {
			this.rawImages[i] = ImageFile.loadFlippedImageFile(this.nameImages[i]);
		}
	}
	
	public int getSize() {
		return(this.size);
	}
	
	public RawImage getRawImage(int index) {
		RawImage aux = null;
		if ( index >= 0 && index < this.size ) {
			aux = this.rawImages[index]; 
		}
		return(aux);
	}
		
}
