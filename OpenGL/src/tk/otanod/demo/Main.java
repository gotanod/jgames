/*

Copyright (c) <17 nov. 2020> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.demo;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GLEventListener;

import tk.otanod.engine.render.ModelArray;
import tk.otanod.engine.render.ModelIndices;
import tk.otanod.engine.render.ModelIndicesTexture;
import tk.otanod.engine.render.ModelMVPIndicesTexture;
import tk.otanod.engine.render.Window;

public class Main {

	public static void main(String args[]) {
		
		System.out.println("Main thread : " + Thread.currentThread().getName());
		
		List<GLEventListener> models = new ArrayList<>();
		// 3D model drawn with Arrays
		GLEventListener m1 = new ModelArray();
		models.add(m1);
		// 3D model drawn with indices
		GLEventListener m2 = new ModelIndices();
		models.add(m2);
		// 3D model drawn with indices and texture
		GLEventListener m3 = new ModelIndicesTexture();
		models.add(m3);
		// 3D model drawn with indices and texture and MVP
		GLEventListener m4 = new ModelMVPIndicesTexture();
		models.add(m4);
		
		// AWT - OpenGL window
		Window w = new Window(models);

		// Init the windows/openGL
		w.createDisplay("DEMO 5");

		// Display the window (loop)
		//w.updateDisplay();
		w.loopWindow();
			
	}
	
}
