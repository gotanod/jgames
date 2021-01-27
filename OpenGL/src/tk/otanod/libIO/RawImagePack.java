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

	public RawImagePack(String[] imageNames, boolean isFlipped) {
		this.nameImages = imageNames;

		this.size = this.nameImages.length;
		
		this.rawImages = new RawImage[this.size];
		
		for (int i=0; i<this.size; i++) {
			this.rawImages[i] = ImageFile.loadImageFile(this.nameImages[i], isFlipped);
		}
	}
	
	public RawImagePack(String imagePackName, int rows, int cols) {
		this.nameImages = new String[] { "right", "left", "top", "bottom", "back", "front" };
		
		this.size = this.nameImages.length;
		
		this.rawImages = new RawImage[this.size];

		RawImage image = ImageFile.loadImageFile(imagePackName,  false);
		
		this.rawImages[0] = ImageFile.subImage(image, "right",  rows, cols, 6, true, false);	// GL_TEXTURE_CUBE_MAP_POSITIVE_X 	Right
		this.rawImages[1] = ImageFile.subImage(image, "left",   rows, cols, 4, true, false);	// GL_TEXTURE_CUBE_MAP_NEGATIVE_X 	Left
		this.rawImages[2] = ImageFile.subImage(image, "top",    rows, cols, 1, false, true);	// GL_TEXTURE_CUBE_MAP_POSITIVE_Y 	Top
		this.rawImages[3] = ImageFile.subImage(image, "bottom", rows, cols, 9, false, true);	// GL_TEXTURE_CUBE_MAP_NEGATIVE_Y 	Bottom
		this.rawImages[4] = ImageFile.subImage(image, "back",   rows, cols, 7, true, false);	// GL_TEXTURE_CUBE_MAP_POSITIVE_Z 	Back
		this.rawImages[5] = ImageFile.subImage(image, "front", 	rows, cols, 5, true, false);	// GL_TEXTURE_CUBE_MAP_NEGATIVE_Z 	Front
		
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
