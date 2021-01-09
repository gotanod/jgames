/*

Copyright (c) <8 ene. 2021> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.engine.render;

import com.jogamp.opengl.GLAutoDrawable;

import tk.otanod.engine.camera.Camera;
import tk.otanod.engine.light.Light;
import tk.otanod.libMath.M4f;

public interface Model {
	
	public void init(GLAutoDrawable drawable);
	
	public void dispose(GLAutoDrawable drawable);
	
	public void display(GLAutoDrawable drawable);
	
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height);
	
	public void update(float x, float y, float z, Camera camera, Light light, M4f projection);
	public void updatePosition(float x, float y, float z);
	public void updateScale(float xScale, float yScale, float zScale);
	public void update(Camera camera);
	public void update(Light light);
	public void update(M4f projection);
	
	
}
