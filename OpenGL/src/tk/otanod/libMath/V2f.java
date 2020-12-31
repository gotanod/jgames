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
 * Name: V2f
 * Created by: Jose Donato Perez Gonzalez
 * Email: jdperezg@yahoo.es
 * 
 * VERSION
 * [1] [2020-10-17] simplification from V3f 
 * 
 * REFERENCES:
 * https://github.com/toji/gl-matrix
 * https://github.com/JOML-CI/JOML/tree/master/src/org/joml
 * 
 */

public class V2f {

	private float x;
	private float y;
	public final static float THRESHOLD = 1E-6f;		// Float maximum precision is 6 or 7 decimal positions

	/* *******************************
	 * BUILDERS
	 * *******************************/
	
	public V2f() {
		this.x = 0.0f;
		this.y = 0.0f;
	}

	public V2f(float d) {
		this.x = d;
		this.y = d;
	}

	public V2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public V2f(float[] aValues) {
		this.x = aValues[0];
		this.y = aValues[1];
	}
	
	public V2f(double[] aValues) {
		this.x = (float) aValues[0];
		this.y = (float) aValues[1];
	}
	
	public V2f(double x, double y) {
		this.x = (float) x;
		this.y = (float) y;
	}

	public V2f(V2f v) {
		this.x = v.x;
		this.y = v.y;
	}

	public V2f(int index, ByteBuffer buffer) {
		/* Float occupies 4 bytes, better use SIZE from the wrappers "Double for float" */
		int step = Float.SIZE/Byte.SIZE;
		this.x = buffer.getFloat(index);
		this.y = buffer.getFloat(index + step);
	}

	public V2f(int index, FloatBuffer buffer) {
		this.x = buffer.get(index);
		this.y = buffer.get(index + 1);
	}

	/* *******************************
	 * GETTERS
	 * *******************************/
	
	public float x() { return(this.x);  }

	public float y() { return(this.y);  }

    /* ********************************
     * Setters 
     * ********************************/
	
    public V2f set(float x, float y) {
        this.x = x;
        this.y = y;
        return(this);
    }

    public V2f set(V2f that) {
        this.x = that.x;
        this.y = that.y;
        return(this);
    }

    public V2f set(ByteBuffer buffer) {
        return(set(buffer.position(), buffer));
    }

    public V2f set(int index, ByteBuffer buffer) {
    	int step = Float.SIZE/Byte.SIZE;
        this.x = buffer.getFloat(index);
        this.y = buffer.getFloat(index + step);
        return(this);
    }

    public V2f set(FloatBuffer buffer) {
        return(set(buffer.position(), buffer));
    }

    public V2f set(int index, FloatBuffer buffer) {
        this.x = buffer.get(index);
        this.y = buffer.get(index + 1);
        return(this);
    }
	
	/* *******************************
	 * CLONE
	 * *******************************/
	
	@Override
	protected V2f clone() {
		V2f that = new V2f();
		that.x = this.x;
		that.y = this.y;
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

		V2f that = (V2f) obj;
		if ( Math.abs(this.x-that.x)>THRESHOLD ) 
			return false;
		if ( Math.abs(this.y-that.y)>THRESHOLD ) 
			return false;
		
		return true;
	}

	@Override
	public String toString() {
		return "V3f [x=" + x + ", y=" + y + "]";
	}
	
	/* *******************************
	 * VECTOR OPERATIONS
	 * *******************************/
	
	public V2f scale(float scalar) {
		this.x *= scalar;
		this.y *= scalar;
		return(this);
	}
	
	public V2f scaleNonUniform(float scalar1, float scalar2) {
		// Hadamard product
		this.x *= scalar1;
		this.y *= scalar2;
		return(this);
	}  
	
	public V2f add(V2f that) {
		this.x += that.x;
		this.y += that.y;

		return(this);
	}
	
	public V2f sub(V2f that) {
		this.x -= that.x;
		this.y -= that.y;

		return(this);
	}
	
	public float magnitude() {
		return((float) Math.sqrt(this.x*this.x + this.y*this.y));
	}
	
	public float squareMagnitude() {
		return(this.x*this.x + this.y*this.y);
	}
	
	public V2f unitVector() {
		float mag = magnitude();
		this.x /= mag;
		this.y /= mag;
		return(this);
	}
	
	public V2f normalize() {
		return this.unitVector();
	}
	
	public float dotProduct(V2f that) { 
		return(this.x * that.x + this.y * that.y);  
	}
	
	public float cosAngle(V2f that) {
		// a·b = |a|·|b|·cos(theta)
		float mag1 = this.magnitude();
		float mag2 = that.magnitude();
		float dot = this.x * that.x + this.y * that.y;
		
		return dot / (mag1 * mag2);
	}
	
	public float angleRad(V2f that) {
		return  (float) Math.acos(this.cosAngle(that));
	}

	
	public float squareDistance(V2f that) {
		//return the square euclide distance
		float a = this.x-that.x;
		float b = this.y-that.y;
		float distance = a*a + b*b;

		return(distance);
	}
}
