/*

Copyright (c) <31 dic. 2020> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.libIO;

import java.awt.Graphics2D;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.ComponentColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Hashtable;

import javax.imageio.ImageIO;


public final class ImageFile {

	public static RawImage loadImageFile(String file) {
		return loadImageFile(file, false);
	}
	
	public static RawImage loadFlippedImageFile(String file) {
		return loadImageFile(file, true);
	}
	
	public static RawImage loadImageFile(String file, boolean isFlipped) {

		BufferedImage mBufferedImage = null;
		ByteBuffer byteBufferedFile = null;
		int width = 0;
		int height = 0;

		byte[] bytes = null;

		try {
			File mFile = new File(file);
			debug("loadImageFile",mFile.getAbsolutePath() + ", " +  mFile.length() );

			mBufferedImage = ImageIO.read(mFile);
			
			if ( isFlipped == true ) {
				mBufferedImage = createFlipped(mBufferedImage);											// this avoids the tricks inside the GLSL fragment shader!!!!!!!!!
			} else {				
				mBufferedImage = createTransformed(mBufferedImage);											// this avoids the tricks inside the GLSL fragment shader!!!!!!!!!
			}
			
						
			// Returns the image type. If it is not one of the known types, TYPE_CUSTOM is returned.
			// Returns:the image type of this BufferedImage.
			// See Also:TYPE_INT_RGB TYPE_INT_ARGB TYPE_INT_ARGB_PRE TYPE_INT_BGR TYPE_3BYTE_BGR TYPE_4BYTE_ABGR TYPE_4BYTE_ABGR_PRE TYPE_BYTE_GRAY TYPE_BYTE_BINARY TYPE_BYTE_INDEXED TYPE_USHORT_GRAY TYPE_USHORT_565_RGB TYPE_USHORT_555_RGB TYPE_CUSTOM        	

			// IMPORTANT
			// Type 6: TYPE_4BYTE_ABGR_PRE
			// Photoshop: unblock layer, remove a portion to see that it shows transparency, save as png
			// Check the file details that show 32bits, 8bits per channel, include tranparency/alpha
			// requires ==> GLSL: gl_FragColor = texture2D(uSampler, vTextureCoord).abgr;
			//
			// FLIP texture
			// T = 1 - T
			// "   gl_FragColor = texture2D(uSampler, vec2(vTextureCoord.s, 1.0 - vTextureCoord.t)).abgr; \n" +
			// IMPORTANT

			width = mBufferedImage.getWidth();
			height = mBufferedImage.getHeight();
			debug("loadImageFile", " size " + mBufferedImage.getWidth() + " x " + mBufferedImage.getHeight());
			String[] aImageType = new String[]{"TYPE_CUSTOM", "TYPE_INT_RGB","TYPE_INT_ARGB","TYPE_INT_ARGB_PRE","TYPE_INT_BGR","TYPE_3BYTE_BGR","TYPE_4BYTE_ABGR","TYPE_4BYTE_ABGR_PRE","TYPE_BYTE_GRAY","TYPE_BYTE_BINARY","TYPE_BYTE_INDEXED","TYPE_USHORT_GRAY","TYPE_USHORT_565_RGB","TYPE_USHORT_555_RGB"};
			debug("loadImageFile", "TEXTURE channel type: " + aImageType[mBufferedImage.getType()]);


			// COLOR MODEL
			//ColorModel cm = mBufferedImage.getColorModel();
			//System.out.println(">>> readImageFile: TEXTURE alpha? " + cm.hasAlpha() );
			//System.out.println(">>> readImageFile: TEXTURE channel type: " + cm );

			// RASTER
			WritableRaster raster = mBufferedImage.getRaster();

			SampleModel sm = raster.getSampleModel();
			debug("loadImageFile", "Bytes per pixel: " + sm.getNumDataElements() );
			
			DataBufferByte data   = (DataBufferByte) raster.getDataBuffer();

			bytes = data.getData();

			byteBufferedFile = ByteBuffer.allocateDirect(bytes.length);
			byteBufferedFile.order(ByteOrder.nativeOrder());
			byteBufferedFile.put(bytes);   
			byteBufferedFile.position(0);

			bytes = null;

		} catch (IOException e) {
			System.err.println("IOException while reading ImageFile: " + e.getMessage());
		}

		RawImage rawImg = new RawImage(file, width, height, byteBufferedFile);
		
		return(rawImg);
	}
	
	private static BufferedImage createTransformed(BufferedImage image)   {
		// TYPE_CUSTOM (RGBA_pre) ==> we use RGBA as GL expects it, and you can load textures in linear space and correct gamma at the end of fragment shader (GL_SRGB_ALPHA) 
		WritableRaster raster =  Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,image.getWidth(), image.getHeight(), image.getWidth() * 4, 4, new int[] {0, 1, 2, 3}, null);
		ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8,8,8,8}, true, true, ColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
		BufferedImage newImage = new BufferedImage(colorModel, raster, true, new Hashtable<>());	   
		
		Graphics2D g = newImage.createGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
	
	private static BufferedImage createTransformed(BufferedImage image, AffineTransform at)   {
		// TYPE_4BYTE_ABGR_PRE => requires swizzling in the fragment shader  ().abgr
		//BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(),BufferedImage.TYPE_4BYTE_ABGR_PRE);
		
		// TYPE_CUSTOM (RGBA_pre) ==> we use RGBA as GL expects it, and you can load textures in linear space and correct gamma at the end of fragment shader (GL_SRGB_ALPHA) 
        WritableRaster raster =  Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,image.getWidth(), image.getHeight(), image.getWidth() * 4, 4, new int[] {0, 1, 2, 3}, null);
        ColorModel colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), new int[]{8,8,8,8}, true, true, ColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);
        BufferedImage newImage = new BufferedImage(colorModel, raster, true, new Hashtable<>());	   
        
		Graphics2D g = newImage.createGraphics();
		g.transform(at);
		g.drawImage(image, 0, 0, null);
		g.dispose();
		return newImage;
	}
		
	private static BufferedImage createFlipped(BufferedImage image)  {
		AffineTransform at = new AffineTransform();
		at.concatenate(AffineTransform.getScaleInstance(1, -1));
		at.concatenate(AffineTransform.getTranslateInstance(0, -image.getHeight()));
		return createTransformed(image, at);
	}
	
	private static BufferedImage createRotated(BufferedImage image) {
		AffineTransform at = AffineTransform.getRotateInstance(Math.PI, image.getWidth()/2, image.getHeight()/2.0);
		return createTransformed(image, at);
	}
    	
    private static BufferedImage convertToABGRpre(BufferedImage image)     {
        BufferedImage newImage = new BufferedImage(image.getWidth(), image.getHeight(),BufferedImage.TYPE_4BYTE_ABGR_PRE);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }  

	public static RawImage subImage(RawImage image, String name, int rows, int cols, int index, boolean isFlippedHorizontal, boolean isFlippedVertical) {
		// Image 
		int imagePixelWidth = image.getWidth();
		int imagePixelHeight = image.getHeight();
		ByteBuffer bb = image.getByteDataBuffer();
		bb.position(0);
		int bytesPerPixel = bb.remaining() / (imagePixelWidth * imagePixelHeight);
		
		// SubImage
		int subImagePixelWidth = imagePixelWidth / cols;
		int subImagePixelHeight = imagePixelHeight / rows;
		byte[] bytes = new byte[subImagePixelWidth * subImagePixelHeight * bytesPerPixel];
		int subImageCol = index % cols;
		int subImageRow = index / cols;
		int imageRowStartPosition = subImageRow * subImagePixelHeight * imagePixelWidth * bytesPerPixel + subImageCol * subImagePixelWidth * bytesPerPixel;
		int sign = 1;
		if ( isFlippedVertical ) {
			imageRowStartPosition += (subImagePixelHeight-1) * imagePixelWidth * bytesPerPixel;
			sign = -1;			// decrement inside the loop
		}
		for (int i=0;i<subImagePixelHeight; i++) {
			bb.position(imageRowStartPosition);
			
			int off = i * subImagePixelWidth * bytesPerPixel;
			int len = subImagePixelWidth * bytesPerPixel;
			
			if ( isFlippedHorizontal ) {
				// Horizontally flipped
				for (int j = 0; j < len; j+=bytesPerPixel) {
					// for each pixel, store the bytes in the original order (ex. RGBA, and do not reverse them to ABGR..)
					for (int k=bytesPerPixel-1; k>=0; k--) {
						bytes[off + len - j - 1 - k] = bb.get();			// store in reverse order				
					}
				}
			} else { 
				// not Horizontally Flipped
				bb.get(bytes, off, len);
			}
			// Next row
			imageRowStartPosition += sign * imagePixelWidth * bytesPerPixel;
		}
		
		// Prepare the output ByteBuffer
		ByteBuffer sbb = ByteBuffer.allocateDirect(bytes.length);
		sbb.order(ByteOrder.nativeOrder());
		sbb.put(bytes); 
		sbb.position(0);
		
		// Prepare the output RawImage
		RawImage subImage = new RawImage(name, subImagePixelWidth, subImagePixelHeight, sbb);
		
		return subImage;
	}

	private static void debug(String tag, String msg) {
		//System.out.println(">>> DEBUG >>> " + tag + " >>> " + msg);
	}
}
