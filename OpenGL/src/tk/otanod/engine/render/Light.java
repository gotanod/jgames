/*

Copyright (c) <1 ene. 2021> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.engine.render;

import tk.otanod.libMath.V3f;

public class Light {
	
	private V3f position;
	private V3f ambientColor;
	private V3f diffuseColor;
	private V3f specularColor;
	
	public Light(V3f position, V3f ambientColor, V3f diffuseColor, V3f specularColor) {
		this.position = position;
		this.ambientColor = ambientColor;
		this.diffuseColor = diffuseColor;
		this.specularColor = specularColor;
	}

	public float[] getPosition() {
		return position.getFloats();
	}

	public float[] getAmbientColor() {
		return ambientColor.getFloats();
	}

	public float[] getDiffuseColor() {
		return diffuseColor.getFloats();
	}

	public float[] getSpecularColor() {
		return specularColor.getFloats();
	}
	
	
}
