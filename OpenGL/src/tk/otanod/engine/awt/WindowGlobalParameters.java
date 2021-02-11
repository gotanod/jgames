/*

Copyright (c) <9 feb. 2021> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.engine.awt;

public class WindowGlobalParameters {

	// https://upload.wikimedia.org/wikipedia/commons/0/0c/Vector_Video_Standards8.svg
	// HD 720
	private int window_width_px = 1280;
	private int window_height_px = 720;
	private int window_xOffset = 10;
	private int window_yOffset = 10;
	private int FPS;
	
	
	public WindowGlobalParameters(int window_width_px, int window_height_px, int window_xOffset, int window_yOffset, int fPS) {
		this.window_width_px = window_width_px;
		this.window_height_px = window_height_px;
		this.window_xOffset = window_xOffset;
		this.window_yOffset = window_yOffset;
		FPS = fPS;
	}

	public WindowGlobalParameters(int window_width_px, int window_height_px, int fPS) {
		this.window_width_px = window_width_px;
		this.window_height_px = window_height_px;
		FPS = fPS;
	}

	public int getWindow_width_px() {
		return window_width_px;
	}

	public void setWindow_width_px(int window_width_px) {
		this.window_width_px = window_width_px;
	}

	public int getWindow_height_px() {
		return window_height_px;
	}

	public void setWindow_height_px(int window_height_px) {
		this.window_height_px = window_height_px;
	}

	public int getWindow_xOffset() {
		return window_xOffset;
	}

	public void setWindow_xOffset(int window_xOffset) {
		this.window_xOffset = window_xOffset;
	}

	public int getWindow_yOffset() {
		return window_yOffset;
	}

	public void setWindow_yOffset(int window_yOffset) {
		this.window_yOffset = window_yOffset;
	}

	public int getFPS() {
		return FPS;
	}

	public void setFPS(int fPS) {
		FPS = fPS;
	}

	@Override
	public String toString() {
		return "WindowGlobalParameters [window_width_px=" + window_width_px + ", window_height_px=" + window_height_px
				+ ", window_xOffset=" + window_xOffset + ", window_yOffset=" + window_yOffset + ", FPS=" + FPS + "]";
	}
	
}
