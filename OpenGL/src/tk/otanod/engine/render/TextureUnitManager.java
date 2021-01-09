package tk.otanod.engine.render;

import java.util.HashMap;

public class TextureUnitManager {
	
	private static int iTextureUnit = 0;
	////////////////////////////////////////////////////////
	/// SINGLETON
	////////////////////////////////////////////////////////	
	private static TextureUnitManager ourInstance = new TextureUnitManager();

	public static TextureUnitManager getInstance() {
		return ourInstance;
	}

	private TextureUnitManager() {
	}

	public int getTextureNumber() {
		// Keep it for backwards compatibility
		// we create a unique id for each call
		return(getTextureNumber("NA_" + System.nanoTime()));
	}
	
	HashMap<String, Integer> textureUnitsMap = new HashMap<>();
	
	public boolean isTextureNumberLoaded(String name) {
		boolean isTextureNameLoaded = false;
		if ( textureUnitsMap.containsKey(name) ) {
			isTextureNameLoaded = true;
		}
		return(isTextureNameLoaded);
	}
	
	public int getTextureNumber(String name) {
		int textureUnit;
		if ( textureUnitsMap.containsKey(name) ) {
			textureUnit = textureUnitsMap.get(name);
		} else {
			textureUnit = this.iTextureUnit++;
			textureUnitsMap.put(name, textureUnit);
		}
		return(textureUnit);
	}

}
