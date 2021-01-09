/*

Copyright (c) <28 dic. 2020> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.libIO;

import java.nio.ByteBuffer;

public class RawImage {

	public String name;
	public int width;
	public int height;
	public ByteBuffer byteDataBuffer;
	public boolean isTransparent = false;
				
	public RawImage(String imageName, int width, int height, ByteBuffer byteBufferedFile) {
		this.name = imageName;
		this.width = width;
		this.height = height;
		this.byteDataBuffer = byteBufferedFile;
	}

	public String getName() {
		return this.name;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public ByteBuffer getByteDataBuffer() {
		return byteDataBuffer;
	}

	public boolean isTransparent() {
		return isTransparent;
	}

	public void setTransparent(boolean isTransparent) {
		this.isTransparent = isTransparent;
	}

}
