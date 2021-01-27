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

package tk.otanod.engine.light;

import tk.otanod.libMath.V3f;

public class Light {
	
	private V3f position;
	private V3f ambientColor;
	private V3f diffuseColor;
	private V3f specularColor;
	 
	private V3f skyColor = new V3f(90f/255f,120f/255f,144f/255f);			// used as fogColor
	private float fogUpperLimit = 0.2f;										// Skybox is a NDC cube  [-1,1]		
	private float fogLowerLimit = 0.0f;										// Skybox is a NDC cube  [-1,1]
	
	public Light(V3f position, V3f ambientColor, V3f diffuseColor, V3f specularColor, V3f skyColor) {
		this.position = position;
		this.ambientColor = ambientColor;
		this.diffuseColor = diffuseColor;
		this.specularColor = specularColor;
		this.skyColor = skyColor;
	}

	/**********************************
	 * Light position
	 **********************************/
	
	public float[] getPosition() {
		return position.getFloats();
	}

	/**********************************
	 * Blinn–Phong reflection model
	 **********************************/
	
	public float[] getAmbientColor() {
		return ambientColor.getFloats();
	}

	public float[] getDiffuseColor() {
		return diffuseColor.getFloats();
	}

	public float[] getSpecularColor() {
		return specularColor.getFloats();
	}

	public float[] getSkyColor() {
		return skyColor.getFloats();
	}
	
	/*****************************
	 * FOG
	 *****************************/
	public float[] getFogColor() {
		return skyColor.getFloats();
	}

	public float getFogUpperLimit() {
		return fogUpperLimit;
	}

	public float getFogLowerLimit() {
		return fogLowerLimit;
	}
	
}
