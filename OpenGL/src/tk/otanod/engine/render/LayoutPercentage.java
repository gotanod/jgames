/*

Copyright (c) <8 feb. 2021> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.engine.render;

import tk.otanod.libMath.M4f;

public class LayoutPercentage {

	// PCT Percentage 
	// (0%,0%) is the top left screen corner
	// (100%, 100%) is the bottom right screen corner
	// (0%=> z=1 close to the TV screen, 100% => z=-1 back of the TV screen)
	private float xPCT;
	private float yPCT;
	private float zPCT;
	private float xScalePCT;
	private float yScalePCT;
	private float zScalePCT;
	
	// NDC  
	// (0,0,0) is the center of the NDC cube
	// x,y,z = [-1,1]
	
	public LayoutPercentage(float[] topLeftPositionPCT, float[] scalePCT) {
		// positions
		this.xPCT = clamp(topLeftPositionPCT[0], 0.0f, 100.0f);
		this.yPCT = clamp(topLeftPositionPCT[1], 0.0f, 100.0f);
		this.zPCT = clamp(topLeftPositionPCT[2], 0.0f, 100.0f);
		
		// scale
		this.xScalePCT = clamp(scalePCT[0], 0.0f, 100.0f);
		this.yScalePCT = clamp(scalePCT[1], 0.0f, 100.0f);
		this.zScalePCT = clamp(scalePCT[2], 0.0f, 100.0f);
	}
	
	public LayoutPercentage(float xPosition, float yPosition, float xScale, float yScale) {
		// By default, in front of the screen (zNDC=-1.0f, zPCT=0%)
		// positions
		this.xPCT = clamp(xPosition, 0.0f, 100.0f);
		this.yPCT = clamp(yPosition, 0.0f, 100.0f);
		float zPosition = 0.0f;			// to force the model in front of the screen
		this.zPCT = clamp(zPosition, 0.0f, 100.0f);
		
		// scale
		this.xScalePCT = clamp(xScale, 0.0f, 100.0f);
		this.yScalePCT = clamp(yScale, 0.0f, 100.0f);
		float zScale = 0.0f; 			// to ignore any z component of the model
		this.zScalePCT = clamp(zScale, 0.0f, 100.0f);
	}
	
	public LayoutPercentage(float xPosition, float yPosition, float zPosition, float xScale, float yScale, float zScale) {
		// positions
		this.xPCT = clamp(xPosition, 0.0f, 100.0f);
		this.yPCT = clamp(yPosition, 0.0f, 100.0f);
		this.zPCT = clamp(zPosition, 0.0f, 100.0f);
		
		// scale
		this.xScalePCT = clamp(xScale, 0.0f, 100.0f);
		this.yScalePCT = clamp(yScale, 0.0f, 100.0f);
		this.zScalePCT = clamp(zScale, 0.0f, 100.0f);
	}

	public float getxPCT() {
		return xPCT;
	}

	public float getyPCT() {
		return yPCT;
	}

	public float getzPCT() {
		return zPCT;
	}

	public float getxScalePCT() {
		return xScalePCT;
	}

	public float getyScalePCT() {
		return yScalePCT;
	}

	public float getzScalePCT() {
		return zScalePCT;
	}

	public float getxNDC() {
		return pointPCTtoNDC(this.xPCT);
	}

	public float getyNDC() {
		// flipped
		return -1.0f * pointPCTtoNDC(this.yPCT);
	}

	public float getzNDC() {
		return pointPCTtoNDC(this.zPCT);
	}

	public float getxScaleNDC() {
		return sizePCTtoNDC(this.xScalePCT);
	}

	public float getyScaleNDC() {
		return sizePCTtoNDC(this.yScalePCT);
	}

	public float getzScaleNDC() {
		return sizePCTtoNDC(this.zScalePCT);
	}
	
	/****************************************
	 * Model/World matrix
	 ****************************************/
	public M4f getMatrix() {
		M4f m4 = new M4f()
				.scale(getxScaleNDC(), getyScaleNDC(), getzScaleNDC())
				.setTranslate(getxNDC(), getyNDC(), getzNDC());
		return m4;
	}
	
	/****************************************
	 * AUX functions
	 ****************************************/
	
	private float pointPCTtoNDC(float pct) {
		// [0,100] ==> [-1,1]
		return ( (pct/50.0f) - 1.0f);
	}
	
	private float sizePCTtoNDC(float pct) {
		// [0,100] ==> [0,2]
		return (pct/50.0f) ;
	}
	
	private float clamp(float val, float min, float max) {
	    return Math.max(min, Math.min(max, val));
	}
}
