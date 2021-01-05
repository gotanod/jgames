package tk.otanod.engine.render;

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
		//if ( this.iTexture >= 31 ) { this.iTexture = -1; }		// SOL: do not call getTextureNumber if you already have one ;)
		return(this.iTextureUnit++);
	}

}
