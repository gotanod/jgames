/*

Copyright (c) <30 dic. 2020> <jdperezg@yahoo.es> All rights reserved.

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

public class Camera {

	public static float[] getOrthoProjectionMatrix(float right, float top, float near, float far) {
		// http://www.songho.ca/opengl/gl_projectionmatrix.html
		// Column major and pre-multiplied
		float[] out = new float[16];

		//first column
		out[0] = 1 / right;
		out[1] = 0;
		out[2] = 0;
		out[3] = 0;

		//second column
		out[4] = 0;
		out[5] = 1/top;
		out[6] = 0;
		out[7] = 0;

		//third column
		out[8] = 0;
		out[9] = 0;
		out[10]= (-2f / (far - near));
		out[11]= 0;

		//second column
		out[12] = 0;  //tx
		out[13] = 0;  //ty
		out[14] = -(far + near) / (far - near);  //tz
		out[15] = 1;

		return(out);
	}

	public static float[] getProjectionMatrix(float right, float left, float top, float bottom, float nearZnegative, float farZnegative) {
		// http://www.songho.ca/opengl/gl_projectionmatrix.html
		
		// EYE in (0,0,0) Vertical (0,1,0) and looking to -Z
		// We can ONLY VIEW vertices in -Z region (between -NEAR and -FAR)
		if ( nearZnegative < 0.1f ) {
			System.err.printf("ERROR: near plane ( %f ) to close to 0. Deep buffer precission errors", nearZnegative);
			nearZnegative = 0.1f;
			//System.exit(1);
		}

		if ( farZnegative < nearZnegative ) {
			System.err.printf("ERROR: far (%f) must be always positive and greater than near (%f) %n", farZnegative, nearZnegative);
			System.exit(1);
		}
		// Column major and pre-multiplied
		float[] out = new float[16];

		float r_l = right - left;
		float ril = right + left;
		float t_b = top - bottom;
		float tib = top + bottom;
		float f_n = farZnegative - nearZnegative;
		float fin = farZnegative + nearZnegative;


		//first column
		out[0] = 2f * nearZnegative / r_l;
		out[1] = 0;
		out[2] = 0;
		out[3] = 0;

		//second column
		out[4] = 0;
		out[5] = 2f * nearZnegative / t_b;
		out[6] = 0;
		out[7] = 0;

		//third column
		out[8] = ril / r_l;
		out[9] = tib / t_b;
		out[10]= -(fin) / f_n;
		out[11]= -1;

		//second column
		out[12] = 0;  						//tx
		out[13] = 0;  						//ty
		out[14] = -2f * farZnegative * nearZnegative / f_n;  	//tz
		out[15] = 0;						

		return(out);
	}

	public static float[] getInfiniteProjectionMatrix(float right, float left, float top, float bottom, float nearZnegative) {
		// http://www.terathon.com/gdc07_lengyel.pdf
		
		// EYE in (0,0,0) Vertical (0,1,0) and looking to -Z
		// We can ONLY VIEW vertices in -Z region (between -NEAR and -FAR)
		if ( nearZnegative < 0.1f ) {
			System.err.printf("ERROR: near plane ( %f ) to close to 0. Deep buffer precission errors", nearZnegative);
			nearZnegative = 0.1f;
			//System.exit(1);
		}
		
//		if ( farZnegative < nearZnegative ) {
//			System.err.printf("ERROR: far (%f) must be always positive and greater than near (%f) %n", farZnegative, nearZnegative);
//			System.exit(1);
//		}
		// Column major and pre-multiplied
		float[] out = new float[16];
		
		float r_l = right - left;
		float ril = right + left;
		float t_b = top - bottom;
		float tib = top + bottom;
		//float f_n = farZnegative - nearZnegative;
		//float fin = farZnegative + nearZnegative;
		
		
		//first column
		out[0] = 2f * nearZnegative / r_l;
		out[1] = 0;
		out[2] = 0;
		out[3] = 0;
		
		//second column
		out[4] = 0;
		out[5] = 2f * nearZnegative / t_b;
		out[6] = 0;
		out[7] = 0;
		
		//third column
		out[8] = ril / r_l;
		out[9] = tib / t_b;
		out[10]= -1; 						// f=infinite   -(fin) / f_n;
		out[11]= -1;
		
		//second column
		out[12] = 0;  						//tx
		out[13] = 0;  						//ty
		out[14] = -2f * nearZnegative; 		 // f=Infinite   -2f * farZnegative * nearZnegative / f_n;  	//tz
		out[15] = 0;						// ALWAYS 1
		
		return(out);
	}

	public static float[] getViewMatrixLookAt(V3f v3Eye, V3f v3Center, V3f v3UP) {
		
		// http://www.songho.ca/opengl/gl_camera.html
		
		V3f Z = v3Eye.clone().sub(v3Center);		// V3f, all operations modify the source vector
		Z.normalize();

		V3f Y = v3UP;

		V3f X = Y.clone().crossProduct( Z );		// V3f, all operations modify the source vector
		X.normalize();

		Y = Z.clone().crossProduct( X );
		Y.normalize();								// Not needed, because the product of two unit vectors is another unit vector. 

		// Column major and pre-multiplied
		float[] out = new float[16];

		//first column
		out[0] = X.x();
		out[4] = X.y();
		out[8] = X.z();
		
		//second column
		out[1] = Y.x();
		out[5] = Y.y();
		out[9] = Y.z();
		
		//third column
		out[2] = Z.x();
		out[6] = Z.y();
		out[10]= Z.z();
		
		// Fourth row
		out[3] = 0;
		out[7] = 0;
		out[11]= 0;

		//Fourth column
		out[12] = -X.dotProduct( v3Eye );  //tx
		out[13] = -Y.dotProduct( v3Eye );  //ty
		out[14] = -Z.dotProduct( v3Eye );  //tz
		
		// Always 1
		out[15] = 1.0f;

		return(out);
	}
	
	public static float[] getViewMatrixFPS(V3f v3Eye, float pitchGrades, float yawGrades) {
		// https://www.3dgep.com/understanding-the-view-matrix/#FPS_Camera
		// http://planning.cs.uiuc.edu/node104.html
		/*
		 * 
		 * Camera ==> [T]x[Ryaw]x[Rpitch]x[XYZ1]			// transformations of the camera
		 * 
		 * View = Camera (inverse)
		 * 
		 * View ==> [R-pitch]x[R-yaw]x[-T]x[XYZ1]
		 * 
		 */
		double pitch = toRad(pitchGrades);
		double yaw   = toRad(yawGrades);
		
		// I assume the values are already converted to radians.
		double cosPitch 	= Math.cos(pitch);
		double sinPitch 	= Math.sin(pitch);
		double cosYaw 	= Math.cos(yaw);
		double sinYaw 	= Math.sin(yaw);

		V3f X = new V3f( cosYaw, 0, -sinYaw );
		V3f Y = new V3f( sinYaw * sinPitch, cosPitch, cosYaw * sinPitch );
		V3f Z = new V3f( sinYaw * cosPitch, -sinPitch, cosPitch * cosYaw );

		// Column major and pre-multiplied
		float[] out = new float[16];

		//first column
		out[0] = X.x();
		out[4] = X.y();
		out[8] = X.z();
		
		//second column
		out[1] = Y.x();
		out[5] = Y.y();
		out[9] = Y.z();
		
		//third column
		out[2] = Z.x();
		out[6] = Z.y();
		out[10]= Z.z();
		
		out[3] = 0;
		out[7] = 0;
		out[11]= 0;

		//fourth column
		out[12] = -X.dotProduct( v3Eye );  //tx
		out[13] = -Y.dotProduct( v3Eye );  //ty
		out[14] = -Z.dotProduct( v3Eye );  //tz
		out[15] = 1.0f;

		return(out);

	}
	
	public static double toRad(double angleGrad) {
		return (angleGrad * Math.PI / 180d);
	}
	
	public static double toGrad(double angleRad) {
		return (angleRad * 180d / Math.PI);
	}

}
