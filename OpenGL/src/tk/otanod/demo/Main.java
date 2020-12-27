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

import tk.otanod.engine.render.Model;
import tk.otanod.engine.render.Window;

public class Main {

	public static void main(String args[]) {
		
		System.out.println("Main thread : " + Thread.currentThread().getName());
		
		Model m = new Model();
		
		Window w = new Window(m);

		w.createDisplay("DEMO 2");

		//w.updateDisplay();
		w.loopWindow();
			
	}
	
}
