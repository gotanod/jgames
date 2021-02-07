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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import tk.otanod.libIO.TextFile;
import tk.otanod.libMath.M4f;

public class Font {

	private String fontFile;
	private boolean hasKernings = false;
	private boolean keepAspectRatio = false;
	private float fontAspectRatio = 0.5f;		// font aspect ratio		https://www.lifewire.com/aspect-ratio-table-common-fonts-3467385
	private char invalidChar = '?';
	
	
	private Map<String,String> properties = new HashMap<>();
	private Map<Character, Glyph> glyphs;
	private Map<String, Integer> kernings;
	
	
	public Font(String fntFile) {
		this.fontFile = fntFile;

		loadFontSpecification(fntFile);
		
	}

	private void loadFontSpecification(String fntFile) {
		
		String newLine = System.lineSeparator();
		
		// Step 1 - read the file from disk
		this.fontFile = fntFile;
		String str = TextFile.readTextFile(fntFile);
		
		BufferedReader reader = new BufferedReader(new StringReader(str));

		// Step 2 - extract the header and the raw chars	
		StringBuilder sbChars = new StringBuilder();
		StringBuilder sbKernings = new StringBuilder();
        try {
            String line = reader.readLine();
            while (line != null) {

            	String[] tokens = line.split("\\s+");
            	
            	if ( line.startsWith("info ") ) {
            		// info face="Arial" size=57 bold=0 italic=0 charset="" unicode=0 stretchH=100 smooth=1 aa=1 padding=8,8,8,8 spacing=0,0
            		extractProperties(tokens, properties);
            	}
            	if ( line.startsWith("common ") ) {
            		// common lineHeight=82 base=53 scaleW=512 scaleH=512 pages=1 packed=0
            		extractProperties(tokens, properties);
            	}
            	if ( line.startsWith("page ") ) {
            		// page id=0 file="arial.png"
            		extractProperties(tokens, properties);
            	}
            	if ( line.startsWith("chars ") ) {
            		// chars count=95
            		extractProperties(tokens, properties, "chars_");			// count is a duplicated property, we need a prefix
            	}
            	if ( line.startsWith("char ") ) {
            		// char id=41   x=220     y=0     width=32     height=70     xoffset=-7     yoffset=3    xadvance=35     page=0  chnl=0 
            		sbChars.append(line);
            		sbChars.append(newLine);
            	}
            	if ( line.startsWith("kernings ") ) {
            		// kernings count=95
            		extractProperties(tokens, properties, "kernings_");			// count is a duplicated property, we need a prefix
            	}            	
            	if ( line.startsWith("kerning ") ) {
            		// kerning first=98 second=44 amount=-3
            		sbKernings.append(line);
            		sbKernings.append(newLine);
            	}
            	line = reader.readLine();
            }
            reader.close();            
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        //for ( String key : properties.keySet() ) {
        //	System.out.println("FNT property " + key + " -> " + properties.get(key));
        //}
        
        // Step 3 - prepare the glyphs map
        int count = Integer.parseInt(properties.get("chars_count"));
        glyphs = new HashMap<>(count);
        
        // Step 4 - process the chars
        BufferedReader reader2 = new BufferedReader(new StringReader(sbChars.toString()));
        try {
            String line = reader2.readLine();
            while (line != null) {		
            	// char id=125   x=92     y=0     width=35     height=70     xoffset=-7     yoffset=3    xadvance=35     page=0  chnl=0 
            	String[] tokens = line.split("\\s+");
            	
            	HashMap<String, String> charProps = new HashMap<>();
            	extractProperties(tokens, charProps);
            	char id = (char) Integer.parseInt(charProps.get("id"));
            	int s = Integer.parseInt(charProps.get("x"));
            	int t = Integer.parseInt(charProps.get("y"));
            	int sWidth = Integer.parseInt(charProps.get("width"));
            	int tHeight = Integer.parseInt(charProps.get("height"));
            	int xOffset = Integer.parseInt(charProps.get("xoffset"));
            	int yOffset = Integer.parseInt(charProps.get("yoffset"));
            	int xAdvance = Integer.parseInt(charProps.get("xadvance"));
            	//int page = Integer.parseInt(charProps.get("page"));
            	//int channel = Integer.parseInt(charProps.get("chnl"));
            	
            	Glyph g = new Glyph(id, s, t, sWidth, tHeight, xOffset, yOffset, xAdvance);
            	glyphs.put(id, g);
            	
            	line = reader2.readLine();
            }
            reader2.close();            
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        // Step 5 - prepare the kernings map
        this.hasKernings = properties.containsKey("kernings_count");
        if ( hasKernings ) {
	        int countKernings = Integer.parseInt(properties.get("kernings_count"));
	        kernings = new HashMap<>(countKernings);
	        
	        BufferedReader reader3 = new BufferedReader(new StringReader(sbKernings.toString()));
	        try {
	            String line = reader3.readLine();
	            while (line != null) {		
	            	// first=98 second=44 amount=-3 
	            	String[] tokens = line.split("\\s+");
	            	
	            	HashMap<String, String> kerningProps = new HashMap<>();
	            	extractProperties(tokens, kerningProps);
	            	char id1 = (char) Integer.parseInt(kerningProps.get("first"));
	            	char id2 = (char) Integer.parseInt(kerningProps.get("second"));
	            	int amount = Integer.parseInt(kerningProps.get("amount"));
	            	        
	            	kernings.put(buildKerningKey(id1, id2), amount);
	            	
	            	line = reader3.readLine();
	            }
	            reader3.close();            
	        } catch (IOException e) {
	        	e.printStackTrace();
	        }
	        
	        for ( String key : kernings.keySet() ) {
	        	debug("kerning", key + " -> " + kernings.get(key));
	        }
        }
	}

	private void extractProperties(String[] tokens, Map<String,String> props) {
		extractProperties(tokens, props, "");
	}
	
	private void extractProperties(String[] tokens, Map<String,String> props, String prefix) {
		for ( String token : tokens ) {
			String[] pair = token.split("=");
			if ( pair.length == 2 ) {
				props.put(prefix + pair[0], pair[1]);
			}
		}
	}
	
	public Glyph getGlyph(char c) {
		Glyph g;
		if ( this.glyphs.containsKey(c) ) {
			g = this.glyphs.get(c);
		} else {
			g = this.glyphs.get(this.invalidChar);				
		}
		return g;
	}

	public float getWidth() {
		float width = Integer.parseInt(properties.get("scaleW"));
		return width;
	}
	
	public float getHeight() {
		float height = Integer.parseInt(properties.get("scaleH"));
		return height;
	}

	public float[] buildInstancesModel(String str) {
		// Build the model matrix for each character
		// The whole string is inside a unitary box  x=[-0.5, 0.5]  y=[0.0, 1.0]
		// With the String model matrix you can scale, rotate and translate this box
		float x0 = -0.5f;
		float worldWidth = 1.0f;
		float worldHeight = 1.0f;
		
		
		int instances = str.length();		
		float[] m4InstancesModel = new float[16 * instances];
		
		// Calculate the total width in font units
		float totalWidth = 0.0f;
		
		char prevChar = ' ';					// space
		for (int i=0; i<instances; i++) {
			char c = str.charAt(i);
			Glyph g = getGlyph(c);
			
			totalWidth += g.getxAdvance();
			
	        if ( hasKernings ) {
				String key = buildKerningKey(prevChar, c);
				if ( this.kernings.containsKey(key) ) {
					int kerning = this.kernings.get(key);
					totalWidth += kerning;
				}
				prevChar = c;
	        }
			
		}
		// get the font base height for 1 line
		float maxHeight = Float.parseFloat(this.properties.get("lineHeight"));			
		
		float cursorPos = x0;
		prevChar = ' ';
		float scaleH = worldWidth / totalWidth;
		float scaleV;
		if ( this.keepAspectRatio ) {
			scaleV = scaleH ;				
		} else {
			scaleV = worldHeight / maxHeight;
		}
		
		for (int i=0; i<instances; i++) {
			char c = str.charAt(i);
			Glyph g = getGlyph(c);
			
	        if ( this.hasKernings ) {
				String key = buildKerningKey(prevChar, c);
				if ( this.kernings.containsKey(key) ) {
					int kerning = this.kernings.get(key);
					cursorPos += kerning * scaleH;
				}
				prevChar = c;
	        }
			
			// Model matrix
			float xScale = g.getsWidth() * scaleH;
			float yScale = g.gettHeight() * scaleV;
			float xOffset = g.getxOffset() * scaleH;
			float yOffset = (maxHeight - g.getyOffset() - g.gettHeight()) * scaleV;
			M4f m4 = (new M4f()).setScale(xScale, yScale, 1.0f).setTranslate(cursorPos + xOffset,  yOffset, 0.0f);		
			System.arraycopy(m4.getElements(), 0, m4InstancesModel, 16*i, 16);
			
			cursorPos += g.getxAdvance() * scaleH;		
		}

		return m4InstancesModel;
	}

	private String buildKerningKey(char prevChar, char c) {
		// Build a UNIQUE key with first and second character used to identify the kerning.
		// first=98 second=44 amount=-3 
		return prevChar + "_" + c;
	}
	
	public float[] buildInstancesTextureAtlasArea(String str) {
		int instances = str.length();
		
		float[] instancesTextureAtlasArea = new float[4 * instances];
		
		float fontWidth = this.getWidth();
		float fontHeight = this.getHeight();
		
		for (int i=0; i<instances; i++) {
			char c = str.charAt(i);
			Glyph g = getGlyph(c);
			
			// Texture Atlas coordinates
			instancesTextureAtlasArea[i*4 + 0] = g.getS()/fontWidth;
			instancesTextureAtlasArea[i*4 + 1] = g.getT()/fontHeight;
			instancesTextureAtlasArea[i*4 + 2] = g.getsWidth()/fontWidth;
			instancesTextureAtlasArea[i*4 + 3] = g.gettHeight()/fontHeight;
		}
		
		return instancesTextureAtlasArea;
	}

	private void debug(String tag, String msg) {
		//System.out.println(">>> DEBUG >>> " + tag + " >>> " + msg);
	}
	
}
