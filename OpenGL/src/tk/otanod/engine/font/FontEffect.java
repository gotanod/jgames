/*

Copyright (c) <7 feb. 2021> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.engine.font;

public class FontEffect {
	
	// SDF Font - Signed Distance Field Font
	// <--textBorderEdge--><--textBorderWidth--><--textEdge--><--textWidth--> Center <--textWidth--><--textEdge--><--textBorderWidth--><--textBorderEdge-->
	
	private float textWidth = 0.47f;
	private float textEdge = 0.00f;
	private float textBorderWidth = 0.00f;
	private float textBorderEdge = 0.07f;
	private float[] textOffset = new float[] {0.0f, 0.0f};				
	private float[] textColor = FontEffect.RGBcolor(100, 161, 244);
	private float[] textBorderColor = FontEffect.RGBcolor(74, 145, 242);
	
	public FontEffect() {
		// use default values
	}
	
	public FontEffect(float textWidth, float textEdge, float textBorderWidth, float textBorderEdge, float[] textOffset,
			float[] textColor, float[] textBorderColor) {
		super();
		this.textWidth = textWidth;
		this.textEdge = textEdge;
		this.textBorderWidth = textBorderWidth;
		this.textBorderEdge = textBorderEdge;
		this.textOffset = textOffset;
		this.textColor = textColor;
		this.textBorderColor = textBorderColor;
	}

	public float getTextWidth() {
		return textWidth;
	}

	public void setTextWidth(float textWidth) {
		this.textWidth = textWidth;
	}

	public float getTextEdge() {
		return textEdge;
	}

	public void setTextEdge(float textEdge) {
		this.textEdge = textEdge;
	}

	public float getTextBorderWidth() {
		return textBorderWidth;
	}

	public void setTextBorderWidth(float textBorderWidth) {
		this.textBorderWidth = textBorderWidth;
	}

	public float getTextBorderEdge() {
		return textBorderEdge;
	}

	public void setTextBorderEdge(float textBorderEdge) {
		this.textBorderEdge = textBorderEdge;
	}

	public float[] getTextOffset() {
		return textOffset;
	}

	public void setTextOffset(float[] textOffset) {
		this.textOffset = textOffset;
	}

	public float[] getTextColor() {
		return textColor;
	}

	public void setTextColor(float[] textColor) {
		this.textColor = textColor;
	}
	
	public void setTextColor(int r, int g, int b) {
		this.textColor = new float[] { r/255f, g/255f, b/255f};
	}

	public float[] getTextBorderColor() {
		return textBorderColor;
	}

	public void setTextBorderColor(float[] textBorderColor) {
		this.textBorderColor = textBorderColor;
	}
	
	public void setTextBorderColor(int r, int g, int b) {
		this.textBorderColor = new float[] { r/255f, g/255f, b/255f};
	}

	public static float[] RGBcolor(int r, int g, int b) {
		return new float[] { r/255f, g/255f, b/255f};
	}
	
}
