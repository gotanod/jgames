/*

Copyright (c) <29 ene. 2021> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.engine.font;

public class Glyph {

	// char id=32   x=0     y=0     width=0     height=0     xoffset=0     yoffset=53    xadvance=32     page=0  chnl=0
	private char id;
	private int s;
	private int t;
	private int sWidth;
	private int tHeight;
	private int xOffset;
	private int yOffset;
	private int xAdvance;
	private int page = 0;
	private int channel = 0;
	
	/**
	 * @param id
	 *            - the ASCII value of the character.
	 * @param s
	 *            - the x texture coordinate for the top left corner of the character in the texture atlas.
	 * @param t
	 *            - the y texture coordinate for the top left corner of the character in the texture atlas.
	 * @param sWidth
	 *            - the width of the character in the texture atlas.
	 * @param tHeight
	 *            - the height of the character in the texture atlas.
	 * @param xOffset
	 *            - the x distance from the curser to the left edge of the character's quad.
	 * @param yOffset
	 *            - the y distance from the curser to the top edge of the character's quad. 
	 * @param xAdvance
	 *            - how far in pixels the cursor should advance after adding this character.
	 */
	public Glyph(char id, int s, int t, int sWidth, int tHeight, int xOffset, int yOffset, int xAdvance) {
		this.id = id;
		this.s = s;
		this.t = t;
		this.sWidth = sWidth;
		this.tHeight = tHeight;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
		this.xAdvance = xAdvance;
	}

	public char getId() {
		return id;
	}

	public int getS() {
		return s;
	}

	public int getT() {
		return t;
	}

	public int getsWidth() {
		return sWidth;
	}

	public int gettHeight() {
		return tHeight;
	}

	public int getxOffset() {
		return xOffset;
	}

	public int getyOffset() {
		return yOffset;
	}

	public int getxAdvance() {
		return xAdvance;
	}

	public int getPage() {
		return page;
	}

	public int getChannel() {
		return channel;
	}

	@Override
	public String toString() {
		return "Glyph [id=" + (int) id + ", s=" + s + ", t=" + t + ", sWidth=" + sWidth + ", tHeight=" + tHeight
				+ ", xOffset=" + xOffset + ", yOffset=" + yOffset + ", xAdvance=" + xAdvance + ", page=" + page
				+ ", channel=" + channel + "]";
	}
	
}
