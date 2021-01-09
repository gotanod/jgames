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

package tk.otanod.engine.camera;

import tk.otanod.libMath.M4f;
import tk.otanod.libMath.V3f;

public class Camera {
	
	private V3f v3Up = new V3f(0.0f, 1.0f, 0.0f); 			// default: +Y axis is up in our world
	private V3f v3Center = new V3f(0.0f, 0.0f, -1.0f);		// default: we look to -Z axis, Change to the center of the object (our textured cube)
	private V3f v3Eye = new V3f(0.0f, 0.0f, 0.0f);			// default: camera at (0,0,0)
	private M4f m4LookAtViewMatrix;	// = new M4f(Camera.getViewMatrixLookAt(v3Eye, v3Center, v3Up));
	private boolean isDelayedUpdateNeeded = false;

	public Camera(V3f v3Up, V3f v3Center, V3f v3Eye) {
		this.v3Up = v3Up;
		this.v3Center = v3Center;
		this.v3Eye = v3Eye;
		
		this.m4LookAtViewMatrix = new M4f(Camera.getViewMatrixLookAt(v3Eye, v3Center, v3Up));
		this.isDelayedUpdateNeeded = false;
	}

	/************************
	 *	GETTERS 
	 ************************/
	
	public V3f getUp() {
		updateCamera();
		return v3Up;
	}

	public V3f getLookTo() {
		updateCamera();
		return v3Center;
	}

	public V3f getEye() {
		updateCamera();
		return v3Eye;
	}

	public M4f getLookAtViewMatrix() {
		updateCamera();
		return m4LookAtViewMatrix;
	}
	
	public void updateCamera() {
		if ( isDelayedUpdateNeeded ) {
			M4f yawMatrix = new M4f().rotateYaxisCCW(draggedYawAngleRad);
			M4f pitchMatrix = new M4f().rotateXaxisCW(draggedPitchAngleRad);
			m4LookAtViewMatrix = new M4f(Camera.getViewMatrixLookAt(v3Eye, v3Center, v3Up));
			m4LookAtViewMatrix.preMultiply(yawMatrix).preMultiply(pitchMatrix);
	
			if ( step > 0.0f ) {
				V3f invForward = new V3f(m4LookAtViewMatrix.getElement(2),m4LookAtViewMatrix.getElement(6),m4LookAtViewMatrix.getElement(10)).scale(-1.0f).normalize();
				V3f invForwardXZ = new V3f(invForward.x(), 0.0f, invForward.z()).normalize();
				v3Eye.add(invForwardXZ.clone().scale(step));
				v3Center = v3Eye.clone().add(invForwardXZ.scale(10f));
				
				m4LookAtViewMatrix = new M4f(Camera.getViewMatrixLookAt(v3Eye, v3Center, v3Up));
				m4LookAtViewMatrix.preMultiply(pitchMatrix);
				draggedYawAngleRad = 0.0f;
				step = 0.0f;
			}
			this.isDelayedUpdateNeeded = false;
		}		
	}

	/************************
	 *	CAMERA MOVEMENTS
	 ************************/
	
	private double draggedPitchAngleRad = 0d;
	private double draggedYawAngleRad = 0d;
	private float step = 0.0f;
	
	public void setDeltaPitch(double incAngleGrad) {
		this.isDelayedUpdateNeeded = true;
		this.draggedPitchAngleRad += toRad(incAngleGrad);		
	}	
	
	public void setDeltaYaw(double incAngleGrad) {
		this.isDelayedUpdateNeeded = true;
		this.draggedYawAngleRad += toRad(incAngleGrad);
	}
	
	public void moveForward(float step) {
		this.isDelayedUpdateNeeded = true;
		this.step += step;
	}
	
	public void moveBackward(float step) {
		this.isDelayedUpdateNeeded = true;
		this.step -= step;
	}
	
	
	/************************
	 *	PROJECTION MATRICES
	 ************************/
	
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
	
	public static float[] getProjectionMatrix(float fovYDegrees, float aspectRatio, float nearZnegative, float farZnegative) {
		// fovY vertical angle of visio in degrees
		// aspect ratio ==> screen ratio  width/height  (16/9,  4/3, ...)
		float tangent = (float) Math.tan(toRad(fovYDegrees/2));   		// tangent of half fovY
		float height = nearZnegative * tangent;           		// half height of near plane
		float width = height * aspectRatio;      				 // half width of near plane
		
		return getProjectionMatrix(width, -width, height, -height, nearZnegative, farZnegative);	// params: right, left, top, bottom, near, far
	}
	
	/************************
	 *	VIEW MATRICES
	 ************************/
	
	public static float[] getViewMatrixLookAt(V3f v3Eye, V3f v3Center, V3f v3UP) {
		
		// http://www.songho.ca/opengl/gl_camera.html
		
		// Orig XYZ
		// Dest right, up, -forward
		
		V3f forward = v3Eye.clone().sub(v3Center);		// V3f, all operations modify the source vector
		forward.normalize();

		V3f up = v3UP;

		V3f right = up.clone().crossProduct( forward );		// V3f, all operations modify the source vector
		right.normalize();

		up = forward.clone().crossProduct( right );
		up.normalize();								// Not needed, because the product of two unit vectors is another unit vector. 

		// Column major and pre-multiplied
		float[] out = new float[16];

		//first column, Right vector in XYZ cartesian system
		out[0] = right.x();
		out[4] = right.y();
		out[8] = right.z();
		
		//second column, Up vector in XYZ cartesian system
		out[1] = up.x();
		out[5] = up.y();
		out[9] = up.z();
		
		//third column, Forward vector in XYZ cartesian system
		out[2] = forward.x();
		out[6] = forward.y();
		out[10]= forward.z();
		
		// Fourth row
		out[3] = 0;
		out[7] = 0;
		out[11]= 0;

		//Fourth column
		out[12] = -right.dotProduct( v3Eye );  //tx
		out[13] = -up.dotProduct( v3Eye );  //ty
		out[14] = -forward.dotProduct( v3Eye );  //tz
		
		// Always 1
		out[15] = 1.0f;

		return(out);
	}
	
	public static float[] getViewMatrixFPS(V3f v3Eye, double pitchGrades, double yawGrades) {
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
	
	/************************
	 *	AUXILIARY FUNCTIONS
	 ************************/

	private static final double TORAD = Math.PI / 180d;
	private static final double TODEG = 180d / Math.PI;
	
	private static double toRad(double angleGrad) {
		//return (angleGrad * Math.PI / 180d);
		return (angleGrad * TORAD);
	}
	
	private static double toGrad(double angleRad) {
		//return (angleRad * 180d / Math.PI);
		return (angleRad * TODEG);
	}

}
