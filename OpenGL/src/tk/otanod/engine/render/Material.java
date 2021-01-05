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

public class Material {
	
	// http://www.it.hiof.no/~borres/j3d/explain/light/p-materials.html
	// http://web.archive.org/web/20100725103839/http://www.cs.utk.edu/~kuck/materials_ogl.htm
	private float[] ambientConstant;
	private float[] diffuseConstant;
	private float[] specularConstant;
	private float shininessConstant;
	
	public Material(float[] ambientConstant, float[] diffuseConstant, float[] specularConstant, float shininessConstant) {
		this.ambientConstant  = new float[4];
		this.diffuseConstant  = new float[4];
		this.specularConstant = new float[4];

		for (int i=0; i<4; i++) {
			this.ambientConstant[i]  = (ambientConstant!=null && ambientConstant.length>i)?ambientConstant[i]:1.0f;
			this.diffuseConstant[i]  = (diffuseConstant!=null && diffuseConstant.length>i)?diffuseConstant[i]:1.0f;
			this.specularConstant[i] = (specularConstant!=null && specularConstant.length>i)?specularConstant[i]:1.0f;
		}
		
		this.shininessConstant = shininessConstant;
		
	}
	
	public Material(TYPE type) {
		int element = type.ordinal();
		int base = element * 13;
		
		this.ambientConstant   = new float[] {raw[base + 0], raw[base + 1], raw[base + 2], raw[base + 3]};
		this.diffuseConstant   = new float[] {raw[base + 4], raw[base + 5], raw[base + 6], raw[base + 7]};
		this.specularConstant  = new float[] {raw[base + 8], raw[base + 9], raw[base + 10], raw[base + 11]};
		this.shininessConstant = raw[base + 12];
	}
	
	/***********************
	 * Getters
	 **********************/	
	
	public float[] getAmbientConstant() {
		return ambientConstant;
	}

	public float[] getDiffuseConstant() {
		return diffuseConstant;
	}

	public float[] getSpecularConstant() {
		return specularConstant;
	}

	public float[] getAmbientConstantLinearRGB() {
		return(new float[] {
			sRGBToLinearRGB(ambientConstant[0]),	
			sRGBToLinearRGB(ambientConstant[1]),	
			sRGBToLinearRGB(ambientConstant[2]),	
			ambientConstant[3]	
		});
	}

	public float[] getDiffuseConstantLinearRGB() {
		return(new float[] {
				sRGBToLinearRGB(diffuseConstant[0]),	
				sRGBToLinearRGB(diffuseConstant[1]),	
				sRGBToLinearRGB(diffuseConstant[2]),	
				diffuseConstant[3]	
			});		
	}

	public float[] getSpecularConstantLinearRGB() {
		return(new float[] {
				sRGBToLinearRGB(specularConstant[0]),	
				sRGBToLinearRGB(specularConstant[1]),	
				sRGBToLinearRGB(specularConstant[2]),	
				specularConstant[3]	
			});				
	}

	public float getShininessConstant() {
		return shininessConstant;
	}
	
	public boolean isTranslucent() {
		boolean translucent = false;
		if ( ambientConstant[3] + diffuseConstant[3] + specularConstant[3] < 3.0f ) {
			translucent = true;
		}
		return translucent;
	}
	
	/****************************
	 * RGB conversion functions
	 ****************************/
	
	private static float sRGBToLinearRGB(float x) {
		if (x <= 0)
			return 0;
		else if (x >= 1)
			return 1;
		else if (x < 0.04045f)
			return x / 12.92f;
		else
			return (float)Math.pow((x + 0.055) / 1.055, 2.4);
	}
	
	private static float linearRGBTosRGB(float x) {
		if (x <= 0)
			return 0;
		else if (x >= 1)
			return 1;
		else if (x < 0.0031308f)
			return x * 12.92f;
		else
			return (float)(Math.pow(x, 1 / 2.4) * 1.055 - 0.055);
	}
	
	/****************************
	 * PRE DEFINED MATERIALS
	 ****************************/
	
	public static enum TYPE {
		GLASS,
		BRASS,
		BRONZE,
		BRONZE_POLISHED,
		CHROME,
		COPPER,
		COPPER_POLISHED,
		GOLD,
		GOLD_POLISHED,
		TIN,
		SILVER,
		SILVER_POLISHED,
		EMERALD,
		JADE,
		OBSIDIAN,
		PERL,
		RUBY,
		TURQUOISE,
		PLASTIC_BLACK,
		PLASTIC_CYAN,
		PLASTIC_GREEN,
		PLASTIC_RED,
		PLASTIC_WHITE,
		PLASTIC_YELLOW,
		RUBBER_BLACK,
		RUBBER_CYAN,
		RUBBER_GREEN,
		RUBBER_RED,
		RUBBER_WHITE,
		RUBBER_YELLOW,
		PEWTER
	};
	
	private static float[] raw = new float[] {
			// Glass
			0.0f, 		0.0f, 		0.0f, 		0.55f,
		    0.588235f, 	0.670588f, 	0.729412f, 	0.55f,
		    0.9f, 		0.9f, 		0.9f, 		0.55f,
		    96.0f,			
			//Brass
			0.329412f, 	0.223529f, 	0.027451f,	1.0f,
			0.780392f, 	0.568627f, 	0.113725f,	1.0f,
			0.992157f, 	0.941176f, 	0.807843f,	1.0f,
			27.8974f,
			// Bronze
			0.2125f, 	0.1275f, 	0.054f,		1.0f,
			0.714f, 	0.4284f, 	0.18144f,	1.0f,
			0.393548f, 	0.271906f, 	0.166721f,	1.0f,
			25.6f,
			//Polished bronze
			0.25f, 		0.148f, 	0.06475f,	1.0f,	
			0.4f, 		0.2368f, 	0.1036f,	1.0f,
			0.774597f, 	0.458561f, 	0.200621f,	1.0f,
			76.8f,
			//Chrome
			0.25f, 		0.25f, 		0.25f,		1.0f,
			0.4f, 		0.4f, 		0.4f,		1.0f,
			0.774597f, 	0.774597f, 	0.774597f,	1.0f,
			76.8f,
			//Copper
			0.19125f, 	0.0735f, 	0.0225f,	1.0f,
			0.7038f, 	0.27048f, 	0.0828f,	1.0f,
			0.256777f, 	0.137622f, 	0.086014f,	1.0f,
			12.8f,
			//Polished copper
			0.2295f, 	0.08825f,	0.0275f, 	1.0f,
			0.5508f, 	0.2118f, 	0.066f, 	1.0f,
			0.580594f, 	0.223257f, 	0.0695701f, 1.0f,
			51.2f,
			//Gold
			0.24725f, 	0.1995f, 	0.0745f, 	1.0f,
			0.75164f, 	0.60648f, 	0.22648f, 	1.0f,
			0.628281f, 	0.555802f, 	0.366065f, 	1.0f,
			51.2f,
			//Polished gold
			0.24725f, 	0.2245f, 	0.0645f,	1.0f,
			0.34615f, 	0.3143f, 	0.0903f,	1.0f,
			0.797357f, 	0.723991f, 	0.208006f,	1.0f,
			83.2f,
			//Tin
			0.105882f, 	0.058824f, 0.113725f,	1.0f,
			0.427451f, 	0.470588f, 0.541176f,	1.0f,
			0.333333f, 	0.333333f, 0.521569f,	1.0f,
			9.84615f,
			//Silver
			0.19225f, 	0.19225f, 	0.19225f,	1.0f,
			0.50754f, 	0.50754f, 	0.50754f,	1.0f,
			0.508273f, 	0.508273f, 	0.508273f,	1.0f,
			51.2f,
			//Polished silver
			0.23125f, 	0.23125f, 	0.23125f,	1.0f,
			0.2775f, 	0.2775f, 	0.2775f,	1.0f,
			0.773911f, 	0.773911f, 	0.773911f,	1.0f,
			89.6f,
			//Emerald
			0.0215f, 	0.1745f, 	0.0215f, 	0.55f,
			0.07568f, 	0.61424f, 	0.07568f, 	0.55f,
			0.633f, 	0.727811f, 	0.633f, 	0.55f,
			76.8f,
			//Jade
			0.135f, 	0.2225f, 	0.1575f, 	0.95f,
			0.54f, 		0.89f, 		0.63f, 		0.95f,
			0.316228f, 	0.316228f, 	0.316228f, 	0.95f,
			12.8f,
			//Obsidian
			0.05375f, 	0.05f, 		0.06625f, 	0.82f,
			0.18275f, 	0.17f, 		0.22525f, 	0.82f,
			0.332741f, 	0.328634f, 	0.346435f, 	0.82f,
			38.4f,
			//Perl
			0.25f, 		0.20725f, 	0.20725f, 	0.922f,
			1.0f, 		0.829f, 	0.829f, 	0.922f,
			0.296648f, 	0.296648f, 	0.296648f, 	0.922f,
			11.264f,
			//Ruby
			0.1745f, 	0.01175f, 	0.01175f, 	0.55f,
			0.61424f, 	0.04136f, 	0.04136f, 	0.55f,
			0.727811f, 	0.626959f, 	0.626959f, 	0.55f,
			76.8f,
			//Turquoise
			0.1f, 		0.18725f, 	0.1745f, 	0.8f,
			0.396f, 	0.74151f, 	0.69102f, 	0.8f,
			0.297254f, 	0.30829f, 	0.306678f, 	0.8f,
			12.8f,
			//Black plastic
			0.0f, 		0.0f, 		0.0f, 		1.0f,
			0.01f, 		0.01f, 		0.01f, 		1.0f,
			0.50f, 		0.50f, 		0.50f, 		1.0f,
			32.0f,
			//Cyan plastic
			0.0f,		0.1f,		0.06f ,		1.0f,
			0.0f,		0.50980392f,0.50980392f,1.0f,
			0.50196078f,0.50196078f,0.50196078f,1.0f,
			32.0f,
			//Green plastic
			0.0f,		0.0f,		0.0f,		1.0f,
			0.1f,		0.35f,		0.1f,		1.0f,
			0.45f,		0.55f,		0.45f,		1.0f,
			32.0f,
			//Red plastic
			0.0f,		0.0f,		0.0f,		1.0f,
			0.5f,		0.0f,		0.0f,		1.0f,
			0.7f,		0.6f,		0.6f,		1.0f,
			32.0f,
			//White plastic
			0.0f,		0.0f,		0.0f,		1.0f,
			0.55f,		0.55f,		0.55f,		1.0f,
			0.70f,		0.70f,		0.70f,		1.0f,
			32.0f,
			//Yellow plastic
			0.0f,		0.0f,		0.0f,		1.0f,
			0.5f,		0.5f,		0.0f,		1.0f,
			0.60f,		0.60f,		0.50f,		1.0f,
			32.0f,
			//Black rubber
			0.02f, 		0.02f, 		0.02f, 		1.0f,
			0.01f, 		0.01f, 		0.01f, 		1.0f,
			0.4f, 		0.4f, 		0.4f, 		1.0f,
			10.0f,
			//Cyan rubber
			0.0f,		0.05f,		0.05f,		1.0f,
			0.4f,		0.5f,		0.5f,		1.0f,
			0.04f,		0.7f,		0.7f,		1.0f,
			10.0f,
			//Green rubber
			0.0f,		0.05f,		0.0f,		1.0f,
			0.4f,		0.5f,		0.4f,		1.0f,
			0.04f,		0.7f,		0.04f,		1.0f,
			10.0f,
			//Red rubber
			0.05f,		0.0f,		0.0f,		1.0f,
			0.5f,		0.4f,		0.4f,		1.0f,
			0.7f,		0.04f,		0.04f,		1.0f,
			10.0f,
			//White rubber
			0.05f,		0.05f,		0.05f,		1.0f,
			0.5f,		0.5f,		0.5f,		1.0f,
			0.7f,		0.7f,		0.7f,		1.0f,
			10.0f,
			//Yellow rubber
			0.05f,		0.05f,		0.0f,		1.0f,
			0.5f,		0.5f,		0.4f,		1.0f,
			0.7f,		0.7f,		0.04f,		1.0f,
			10.0f,
			//Pewter
			0.105882f, 	0.058824f,	0.113725f, 	1.0f,
			0.427451f, 	0.470588f, 	0.541176f, 	1.0f,
			0.333333f, 	0.333333f, 	0.521569f, 	1.0f,
			9.84615f, 
	};


}
