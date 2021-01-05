/*

Copyright (c) <17 nov. 2020> <jdperezg@yahoo.es> All rights reserved.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.

*/

package tk.otanod.libMath;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;

/**
 * Name: cVec3f
 * Created by: Jose Donato Perez Gonzalez
 * Email: jdperezg@yahoo.es
 * 
 * VERSION
 * [1] [2016-05-13] Initial version
 * [2] [2017-07-10] modified bugs in dot and cross products
 * [3] [2017-08-07] added getFloats function
 * [4] [2018-08-13] bug corrected in function PreMultiplyMat4
 * [5] [2020-10-14] refactor 
 * 
 * REFERENCES:
 * https://github.com/toji/gl-matrix
 * https://github.com/JOML-CI/JOML/tree/master/src/org/joml
 * 
 */

public class V3f {

	private float x;
	private float y;
	private float z;
	public final static float THRESHOLD = 1E-6f;		// Float maximum precision is 6 or 7 decimal positions

	/* *******************************
	 * BUILDERS
	 * *******************************/
	
	public V3f() {
		this.x = 0.0f;
		this.y = 0.0f;
		this.z = 0.0f;
	}

	public V3f(float d) {
		this.x = d;
		this.y = d;
		this.z = d;
	}

	public V3f(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public V3f(float[] aValues) {
		this.x = aValues[0];
		this.y = aValues[1];
		this.z = aValues[2];
	}
	
	public V3f(double[] aValues) {
		this.x = (float) aValues[0];
		this.y = (float) aValues[1];
		this.z = (float) aValues[2];
	}
	
	public V3f(double x, double y, double z) {
		this.x = (float) x;
		this.y = (float) y;
		this.z = (float) z;
	}

	public V3f(V3f v) {
		this.x = v.x;
		this.y = v.y;
		this.z = v.z;
	}

	public V3f(int index, ByteBuffer buffer) {
		/* Float occupies 4 bytes, better use SIZE from the wrappers "Double for float" */
		int step = Float.SIZE/Byte.SIZE;
		this.x = buffer.getFloat(index);
		this.y = buffer.getFloat(index + step);
		this.z = buffer.getFloat(index + step + step);
	}

	public V3f(int index, FloatBuffer buffer) {
		this.x = buffer.get(index);
		this.y = buffer.get(index + 1);
		this.z = buffer.get(index + 2);
	}

	/* *******************************
	 * GETTERS
	 * *******************************/
	
	public float x() { return(this.x);  }

	public float y() { return(this.y);  }

	public float z() { return(this.z);  }

	public float[] getFloats() {
		return(new float[] { this.x, this.y, this.z });
	}
	
	/* ********************************
	 * Setters 
	 * ********************************/
	
	public V3f set(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
		return(this);
	}

	public V3f set(V3f that) {
		this.x = that.x;
		this.y = that.y;
		this.z = that.z;
		return(this);
	}

	public V3f set(ByteBuffer buffer) {
		return(set(buffer.position(), buffer));
	}

	public V3f set(int index, ByteBuffer buffer) {
		int step = Float.SIZE/Byte.SIZE;
		this.x = buffer.getFloat(index);
		this.y = buffer.getFloat(index + step);
		this.z = buffer.getFloat(index + step + step);
		return(this);
	}

	public V3f set(FloatBuffer buffer) {
		return(set(buffer.position(), buffer));
	}

	public V3f set(int index, FloatBuffer buffer) {
		this.x = buffer.get(index);
		this.y = buffer.get(index + 1);
		this.z = buffer.get(index + 2);
		return(this);
	}
	
	
	/* *******************************
	 * CLONE
	 * *******************************/
	
	@Override
	public V3f clone() {
		V3f that = new V3f();
		that.x = this.x;
		that.y = this.y;
		that.z = this.z;
		return that;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		V3f that = (V3f) obj;
		if ( Math.abs(this.x-that.x)>THRESHOLD ) 
			return false;
		if ( Math.abs(this.y-that.y)>THRESHOLD ) 
			return false;
		if ( Math.abs(this.z-that.z)>THRESHOLD ) 
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "V3f [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
	
	/* *******************************
	 * VECTOR OPERATIONS
	 * *******************************/
	
	public V3f scale(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
		this.z *= scalar;
		return(this);
	}
	
	public V3f scaleNonUniform(float scalar1, float scalar2, float scalar3) {
		// Hadamard product
		this.x *= scalar1;
		this.y *= scalar2;
		this.z *= scalar3;
		return(this);
	}  
	
	public V3f add(V3f that) {
		this.x += that.x;
		this.y += that.y;
		this.z += that.z;

		return(this);
	}
	
	public V3f sub(V3f that) {
		this.x -= that.x;
		this.y -= that.y;
		this.z -= that.z;

		return(this);
	}
	
	public float magnitude() {
		return((float) Math.sqrt(this.x*this.x + this.y*this.y + this.z*this.z));
	}
	
	public float squareMagnitude() {
		return(this.x*this.x + this.y*this.y + this.z*this.z);
	}
	
	public V3f unitVector() {
		float mag = magnitude();
		this.x /= mag;
		this.y /= mag;
		this.z /= mag;
		return(this);
	}
	
	public V3f normalize() {
		return this.unitVector();
	}
	
	public float dotProduct(V3f that) { 
		return(this.x * that.x + this.y * that.y + this.z * that.z);  // BUG DETECTED: replaced z by v.z
	}
	
	public float cosAngle(V3f that) {
		// a·b = |a|·|b|·cos(theta)
		float mag1 = this.magnitude();
		float mag2 = that.magnitude();
		float dot = this.x * that.x + this.y * that.y + this.z * that.z;
		
		return dot / (mag1 * mag2);
	}
	
	public float angleRad(V3f that) {
		return  (float) Math.acos(this.cosAngle(that));
	}

	/*
	public String direction(V3f that) {
		/ *
		Dot Product Tests
		Dot products are great for testing if two vectors are collinear or perpendicular,
		or whether they point in roughly the same or roughly opposite directions. For
		any two arbitrary vectors a and b, game programmers often use the following
		tests, as shown in Figure 4.11:
		• Collinear. (a · b) = |a| |b| = ab (i.e., the angle between them is exactly 0
		degrees—this dot product equals +1 when a and b are unit vectors).
		• Collinear but opposite. (a · b) = −ab (i.e., the angle between them is 180
		degrees—this dot product equals −1 when a and b are unit vectors).
		• Perpendicular. (a · b) = 0 (i.e., the angle between them is 90 degrees).
		• Same direction. (a · b) > 0 (i.e., the angle between them is less than 90
		degrees).
		• Opposite directions. (a · b) < 0 (i.e., the angle between them is greater
		than 90 degrees).
		* /
		
		float dot = this.dotProduct(that);
		
		String output = "?";
		if ( Math.abs(dot) <= THRESHOLD ) { output = "Perpendicullar"; }
		if ( dot > THRESHOLD ) { output = "Same direction"; }
		if ( dot < -THRESHOLD ) { output = "Opposite direction"; }
		if ( dot - this.magnitude() * that.magnitude() < THRESHOLD ) { output = "Collinear"; }
		if ( dot + this.magnitude() * that.magnitude() < THRESHOLD ) { output = "Collinear but opposite"; }
			
		return output;
	}
	*/
	
	public V3f crossProduct(V3f that) {
		/* Cross product
		 * a x b = (ay bz - az by, az bx - ax bz, ax by - ay bx)
		 *
		 *      | x ax bx |
		 *  det | y ay by |
		 *      | z az bz |
		 */
		
		float newX = this.y * that.z - this.z * that.y;
		float newY = this.z * that.x - this.x * that.z;
		float newZ = this.x * that.y - this.y * that.x;
		
		this.x = newX;
		this.y = newY;
		this.z = newZ;
		
		return(this);
	}
	
	public float squareDistance(V3f that) {
		//return the square euclide distance
		float a = this.x-that.x;
		float b = this.y-that.y;
		float c = this.z-that.z;
		float distance = a*a + b*b + c*c;

		return(distance);
	}
}
