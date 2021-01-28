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


/**
 * Name: cMat4f
 * Created by: Jose Donato Perez Gonzalez
 * Email: jdperezg@yahoo.es
 * 
 * VERSION
 * [1] Initial version
 * [2] [2017-07-10] modified bugs in premultiply and postmultiply
 * [3] [2017-08-12] added transpose function
 * [4] [2017-08-14] Bug detected in getFloats, it was returning a pointer instead of a copy
 * [5] [2018-07-25] Renamed some mehtods to add the prefix "u" updated
 * [6] [2020-10-16] refactor
 * 
 * REFERENCES:
 * https://github.com/toji/gl-matrix
 * https://github.com/JOML-CI/JOML/tree/master/src/org/joml
 * 
 */

/**
 * Created by perezgon on 14/05/2016.
 * 
 * COLUMN-MAJOR
 * PRE MULTIPLIED
 * Column-major for PreMul  M*v
 * [sx 0 0 0 0 sy 0 0 0 0 sz 0 tx ty tz 1]
 *
 * sx  0  0  tx     x
 *  0 sy  0  ty     y
 *  0  0 sz  tz     z
 *  0  0  0   1     1
 *
 * VECTOR-MAJOR
 * POST MULTIPLIED
 * Vector-major for PostMul  v*M
 * [sx 0 0 0 0 sy 0 0 0 0 sz 0 tx ty tz 1]
 *
 *           sx  0  0   0   
 * [x y z 1]  0 sy  0   0   
 *            0  0 sz   0   
 *           tx ty tz   1   
 *
 * Column major for PostMul v*M
 * [sx 0 0 tx 0 sy 0 ty 0 0 sz tz 0 0 0 1]
 *
 *              sx  0  0  0
 *               0 sy  0  0
 *  [x y z 1 ]   0  0 sz  0
 *              tx ty tz  1
 *            
 */

public class M4f {
	
	private final static int ROWS = 4;
	private final static int COLS = 4;
	private final static int NELEMENTS = ROWS * COLS;
    private float[] m = new float[NELEMENTS];
	public final static float THRESHOLD = 1E-6f;		// Float maximum precision is 6 or 7 decimal positions

	/* *******************************
	 * BUILDERS
	 * *******************************/

    public M4f() {
        //first column
        this.m[0]=1;
        this.m[1]=0;
        this.m[2]=0;
        this.m[3]=0;

        //second column
        this.m[4]=0;
        this.m[5]=1;
        this.m[6]=0;
        this.m[7]=0;

        //second column
        this.m[8]=0;
        this.m[9]=0;
        this.m[10]=1;
        this.m[11]=0;

        //second column
        this.m[12]=0;
        this.m[13]=0;
        this.m[14]=0;
        this.m[15]=1;
    }
    
    public M4f(float[] data) {
	    //first column
	    this.m[0] = data[0];
	    this.m[1] = data[1];
	    this.m[2] = data[2];
	    this.m[3] = data[3];

	    //second column
	    this.m[4] = data[4];
	    this.m[5] = data[5];
	    this.m[6] = data[6];
	    this.m[7] = data[7];

	    //second column
	    this.m[8] = data[8];
	    this.m[9] = data[9];
	    this.m[10]= data[10];
	    this.m[11]= data[11];

	    //second column
	    this.m[12] = data[12];  //tx
	    this.m[13] = data[13];  //ty
	    this.m[14] = data[14];  //tz
	    this.m[15] = data[15];
    }

    
    /* *********************
     * Getters 
     * *********************/

    public float getElement(int index) {
    	return(this.m[index]);
    }
    
    public float getElement(int row, int col) {
    	return(this.m[row+col*ROWS]);
    }
    
    public float[] getFloats() {
        return(this.m.clone());		// VERY IMPORTANT // SLOWER THAN System.arraycopy!!!!
    }
    
    public float[] getElements() {
    	// FASTER THAN getFloats()
        float[] that = new float[NELEMENTS];
        System.arraycopy(this.m, 0, that, 0, NELEMENTS);
        return(that);
    }
    
    public float[] getM3Elements() {
    	// FASTER THAN getFloats()
    	float[] that = new float[9];
    	that[0] = this.m[0];
    	that[1] = this.m[1];
    	that[2] = this.m[2];
    	that[3] = this.m[4];
    	that[4] = this.m[5];
    	that[5] = this.m[6];
    	that[6] = this.m[8];
    	that[7] = this.m[9];
    	that[8] = this.m[10];
    	return(that);
    }
    
    /* **********************
     * Setters 
     * **********************/
    
    public void setElement(int row, int col, float value) {
    	this.m[row+col*ROWS] = value;
    }
    
    public void setFloats(float[] data) {
        //first column
        this.m[0] = data[0];
        this.m[1] = data[1];
        this.m[2] = data[2];
        this.m[3] = data[3];

        //second column
        this.m[4] = data[4];
        this.m[5] = data[5];
        this.m[6] = data[6];
        this.m[7] = data[7];

        //second column
        this.m[8] = data[8];
        this.m[9] = data[9];
        this.m[10]= data[10];
        this.m[11]= data[11];

        //second column
        this.m[12] = data[12];  //tx
        this.m[13] = data[13];  //ty
        this.m[14] = data[14];  //tz
        this.m[15] = data[15];
    }
   
	/* *******************************
	 * CLONE
	 * *******************************/
    
	@Override
	public M4f clone() { 
		M4f that = new M4f(this.m);
		
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

		M4f that = (M4f) obj;
		for(int i=0;i<this.NELEMENTS;i++) {
			if ( Math.abs(this.m[i]-that.m[i])>THRESHOLD ) 
				return false;			
		}
		
		return true;
	}

    @Override
    public String toString() {
        return( "m0 " + this.m[0] + "\tm4 " + this.m[4] + "\tm08 " +  this.m[8] + "\tm12 " + this.m[12] + "\n" +
                "m1 " + this.m[1] + "\tm5 " + this.m[5] + "\tm09 " +  this.m[9] + "\tm13 " + this.m[13] + "\n" +
                "m2 " + this.m[2] + "\tm6 " + this.m[6] + "\tm10 " + this.m[10] + "\tm14 " + this.m[14] + "\n" +
                "m3 " + this.m[3] + "\tm7 " + this.m[7] + "\tm11 " + this.m[11] + "\tm15 " + this.m[15] + "\n"
                );
    }
   
    /* **************************
     * Matrix operations 
     * **************************/

	public M4f identity() {
        //first column
        this.m[0]=1;
        this.m[1]=0;
        this.m[2]=0;
        this.m[3]=0;

        //second column
        this.m[4]=0;
        this.m[5]=1;
        this.m[6]=0;
        this.m[7]=0;

        //second column
        this.m[8]=0;
        this.m[9]=0;
        this.m[10]=1;
        this.m[11]=0;

        //second column
        this.m[12]=0;
        this.m[13]=0;
        this.m[14]=0;
        this.m[15]=1;
        
        return(this);
    }
    
    public float determinant() {
        // Cache the matrix values (makes for huge speed increases!)
        double   a00 = this.m[0], a01 = this.m[4], a02 = this.m[8],  a03 = this.m[12],
                a10 = this.m[1], a11 = this.m[5], a12 = this.m[9],  a13 = this.m[13],
                a20 = this.m[2], a21 = this.m[6], a22 = this.m[10], a23 = this.m[14],
                a30 = this.m[3], a31 = this.m[7], a32 = this.m[11], a33 = this.m[15];

        // Determinante por adjuntos usando la ultima fila (a30 a31 a32 a33) porque será la que más ceros contendrá
        return (float)  ( - a30 * ( a01 * a12 * a23 + a11 * a22 * a03 + a21 * a02 * a13 )
                 + a30 * ( a21 * a12 * a03 + a22 * a13 * a01 + a23 * a11 * a02 )
                 + a31 * ( a00 * a12 * a23 + a10 * a22 * a03 + a20 * a02 * a13 )
                 - a31 * ( a20 * a12 * a03 + a22 * a13 * a00 + a23 * a10 * a02 )
                 - a32 * ( a00 * a11 * a23 + a10 * a21 * a03 + a20 * a01 * a13 )
                 + a32 * ( a20 * a11 * a03 + a21 * a13 * a00 + a23 * a10 * a01 )
                 + a33 * ( a00 * a11 * a22 + a10 * a21 * a02 + a20 * a01 * a12 )
                 - a33 * ( a20 * a11 * a02 + a21 * a12 * a00 + a22 * a10 * a01 )
               );
    }

    public M4f getInverse() {
        // Cache the matrix values (makes for huge speed increases!)
        double  a00 = this.m[0], a01 = this.m[4], a02 = this.m[8],  a03 = this.m[12],
                a10 = this.m[1], a11 = this.m[5], a12 = this.m[9],  a13 = this.m[13],
                a20 = this.m[2], a21 = this.m[6], a22 = this.m[10], a23 = this.m[14],
                a30 = this.m[3], a31 = this.m[7], a32 = this.m[11], a33 = this.m[15];

        double  b00 = a00 * a11 - a01 * a10,
                b01 = a00 * a12 - a02 * a10,
                b02 = a00 * a13 - a03 * a10,
                b03 = a01 * a12 - a02 * a11,
                b04 = a01 * a13 - a03 * a11,
                b05 = a02 * a13 - a03 * a12,
                b06 = a20 * a31 - a21 * a30,
                b07 = a20 * a32 - a22 * a30,
                b08 = a20 * a33 - a23 * a30,
                b09 = a21 * a32 - a22 * a31,
                b10 = a21 * a33 - a23 * a31,
                b11 = a22 * a33 - a23 * a32;

        double d = (b00 * b11 - b01 * b10 + b02 * b09 + b03 * b08 - b04 * b07 + b05 * b06);
       
        double invDet;

        // Calculate the determinant
        float[] dest = new float[NELEMENTS];

        if ( Math.abs(d) > THRESHOLD ) {
            invDet = 1 / d;

            dest[0] = (float) ((a11 * b11 - a12 * b10 + a13 * b09) * invDet);
            dest[4] = (float) ((-a01 * b11 + a02 * b10 - a03 * b09) * invDet);
            dest[8] = (float) ((a31 * b05 - a32 * b04 + a33 * b03) * invDet);
            dest[12] = (float) ((-a21 * b05 + a22 * b04 - a23 * b03) * invDet);
            dest[1] = (float) ((-a10 * b11 + a12 * b08 - a13 * b07) * invDet);
            dest[5] = (float) ((a00 * b11 - a02 * b08 + a03 * b07) * invDet);
            dest[9] = (float) ((-a30 * b05 + a32 * b02 - a33 * b01) * invDet);
            dest[13] = (float) ((a20 * b05 - a22 * b02 + a23 * b01) * invDet);
            dest[2] = (float) ((a10 * b10 - a11 * b08 + a13 * b06) * invDet);
            dest[6] = (float) ((-a00 * b10 + a01 * b08 - a03 * b06) * invDet);
            dest[10] = (float) ((a30 * b04 - a31 * b02 + a33 * b00) * invDet);
            dest[14] = (float) ((-a20 * b04 + a21 * b02 - a23 * b00) * invDet);
            dest[3] = (float) ((-a10 * b09 + a11 * b07 - a12 * b06) * invDet);
            dest[7] = (float) ((a00 * b09 - a01 * b07 + a02 * b06) * invDet);
            dest[11] = (float) ((-a30 * b03 + a31 * b01 - a32 * b00) * invDet);
            dest[15] = (float) ((a20 * b03 - a21 * b01 + a22 * b00) * invDet);
        } 
        return(new M4f(dest));
    }

    public M4f getTranspose() {
        // Transpose
        float[] dest = new float[NELEMENTS];
      
        dest[0] = this.m[0];
        dest[1] = this.m[4];
        dest[2] = this.m[8];
        dest[3] = this.m[12];
        dest[4] = this.m[1];
        dest[5] = this.m[5];
        dest[6] = this.m[9];
        dest[7] = this.m[13];
        dest[8] = this.m[2];
        dest[9] = this.m[6];
        dest[10] = this.m[10];
        dest[11] = this.m[14];
        dest[12] = this.m[3];
        dest[13] = this.m[7];
        dest[14] = this.m[11];
        dest[15] = this.m[15];
        
        return(new M4f(dest));
    }
    
    public M4f postMultiply(M4f m4) {
        // A(orig) * B
        float[] A = new float[NELEMENTS];
        A = this.getFloats();
        float[] B = m4.getFloats();

        this.m[0]  = A[0] * B[0] + A[4] * B[1] + A[8]  * B[2] + A[12] * B[3];
        this.m[1]  = A[1] * B[0] + A[5] * B[1] + A[9]  * B[2] + A[13] * B[3];
        this.m[2]  = A[2] * B[0] + A[6] * B[1] + A[10] * B[2] + A[14] * B[3];
        this.m[3]  = A[3] * B[0] + A[7] * B[1] + A[11] * B[2] + A[15] * B[3];  // bug corrected

        this.m[4]  = A[0] * B[4] + A[4] * B[5] + A[8]  * B[6] + A[12] * B[7];
        this.m[5]  = A[1] * B[4] + A[5] * B[5] + A[9]  * B[6] + A[13] * B[7];
        this.m[6]  = A[2] * B[4] + A[6] * B[5] + A[10] * B[6] + A[14] * B[7];
        this.m[7]  = A[3] * B[4] + A[7] * B[5] + A[11] * B[6] + A[15] * B[7];

        this.m[8]  = A[0] * B[8] + A[4] * B[9] + A[8]  * B[10] + A[12] * B[11];
        this.m[9]  = A[1] * B[8] + A[5] * B[9] + A[9]  * B[10] + A[13] * B[11];
        this.m[10] = A[2] * B[8] + A[6] * B[9] + A[10] * B[10] + A[14] * B[11];
        this.m[11] = A[3] * B[8] + A[7] * B[9] + A[11] * B[10] + A[15] * B[11];

        this.m[12] = A[0] * B[12] + A[4] * B[13] + A[8]  * B[14] + A[12] * B[15];
        this.m[13] = A[1] * B[12] + A[5] * B[13] + A[9]  * B[14] + A[13] * B[15];
        this.m[14] = A[2] * B[12] + A[6] * B[13] + A[10] * B[14] + A[14] * B[15];
        this.m[15] = A[3] * B[12] + A[7] * B[13] + A[11] * B[14] + A[15] * B[15];
        
        return(this);
    }

    public M4f preMultiply(M4f m4) {
        // A * B(orig)
        float[] B = new float[NELEMENTS];
        B = this.getFloats();
        float[] A = m4.getFloats();

        this.m[0]  = A[0] * B[0] + A[4] * B[1] + A[8]  * B[2] + A[12] * B[3];
        this.m[1]  = A[1] * B[0] + A[5] * B[1] + A[9]  * B[2] + A[13] * B[3];
        this.m[2]  = A[2] * B[0] + A[6] * B[1] + A[10] * B[2] + A[14] * B[3];
        this.m[3]  = A[3] * B[0] + A[7] * B[1] + A[11] * B[2] + A[15] * B[3];		// bug corrected

        this.m[4]  = A[0] * B[4] + A[4] * B[5] + A[8]  * B[6] + A[12] * B[7];
        this.m[5]  = A[1] * B[4] + A[5] * B[5] + A[9]  * B[6] + A[13] * B[7];
        this.m[6]  = A[2] * B[4] + A[6] * B[5] + A[10] * B[6] + A[14] * B[7];
        this.m[7]  = A[3] * B[4] + A[7] * B[5] + A[11] * B[6] + A[15] * B[7];

        this.m[8]  = A[0] * B[8] + A[4] * B[9] + A[8]  * B[10] + A[12] * B[11];
        this.m[9]  = A[1] * B[8] + A[5] * B[9] + A[9]  * B[10] + A[13] * B[11];
        this.m[10] = A[2] * B[8] + A[6] * B[9] + A[10] * B[10] + A[14] * B[11];
        this.m[11] = A[3] * B[8] + A[7] * B[9] + A[11] * B[10] + A[15] * B[11];

        this.m[12] = A[0] * B[12] + A[4] * B[13] + A[8]  * B[14] + A[12] * B[15];
        this.m[13] = A[1] * B[12] + A[5] * B[13] + A[9]  * B[14] + A[13] * B[15];
        this.m[14] = A[2] * B[12] + A[6] * B[13] + A[10] * B[14] + A[14] * B[15];
        this.m[15] = A[3] * B[12] + A[7] * B[13] + A[11] * B[14] + A[15] * B[15];
        
        return(this);
    }


    /***********************
     *  OpenGL 
     **********************/

    public M4f setTranslate(float tx, float ty, float tz) {
        this.m[12]=tx;
        this.m[13]=ty;
        this.m[14]=tz;
        
        return this;
    }

    public M4f addTranslate(float tx, float ty, float tz) {
        this.m[12]+=tx;
        this.m[13]+=ty;
        this.m[14]+=tz;
        
        return this;
    }

    public M4f setScale(float sx, float sy, float sz) {
        this.m[0] = sx;
        this.m[5] = sy;
        this.m[10] = sz;
        
        return this;
    }
    
    public M4f scale(float sx, float sy, float sz) {
	    M4f m4Scale = new M4f();
	    m4Scale.m[0] = sx;
	    m4Scale.m[5] = sy;
	    m4Scale.m[10] = sz;
	    this.preMultiply(m4Scale);

	    return this;
    }    

    /****************************
     * EULER ROTATIONS
     ***************************/
    
    public void setRotationXaxisCCW(double angle) {
        this.m[5]=(float) Math.cos(angle);
        this.m[6]=(float) Math.sin(angle);
        this.m[9]=(float) -Math.sin(angle);
        this.m[10]=(float) Math.cos(angle);
    }

    public void setRotationXaxisCW(double angle) {
        this.setRotationXaxisCCW(-angle);
    }

    public M4f rotateXaxisCCW(double angleRad) {
	    
	    M4f m4Rotation  = new M4f();
	    m4Rotation.setRotationXaxisCCW(angleRad);
	    this.preMultiply(m4Rotation);
	    
	    return this;
    }
    
    public M4f rotateXaxisCW(double angleRad) {
	    
	    M4f m4Rotation  = new M4f();
	    m4Rotation.setRotationXaxisCW(angleRad);
	    this.preMultiply(m4Rotation);
	    
	    return this;
    }

    public void setRotationYaxisCCW(double angleRad) {
        this.m[0]=(float) Math.cos(angleRad);
        this.m[2]=(float) -Math.sin(angleRad);
        this.m[8]=(float) Math.sin(angleRad);
        this.m[10]=(float) Math.cos(angleRad);
    }

    public void setRotationYaxisCW(double angleRad) {
	        this.setRotationYaxisCCW(-angleRad);
    }
    
    public M4f rotateYaxisCCW(double angleRad) {
	    
	    M4f m4Rotation  = new M4f();
	    m4Rotation.setRotationYaxisCCW(angleRad);
	    this.preMultiply(m4Rotation);
	    
	    return this;
    }
    
    public M4f rotateYaxisCW(double angleRad) {
	    
	    M4f m4Rotation  = new M4f();
	    m4Rotation.setRotationYaxisCW(angleRad);
	    this.preMultiply(m4Rotation);
	    
	    return this;
    }

    public void setRotationZaxisCCW(double angleRad) {
        this.m[0]=(float) Math.cos(angleRad);
        this.m[1]=(float) Math.sin(angleRad);
        this.m[4]=(float) -Math.sin(angleRad);
        this.m[5]=(float) Math.cos(angleRad);
    }

    public void setRotationZaxisCW(double angleRad) {
        this.setRotationZaxisCCW(-angleRad);
    }

    public M4f rotateZaxisCCW(double angleRad) {
	    
	    M4f m4Rotation  = new M4f();
	    m4Rotation.setRotationZaxisCCW(angleRad);
	    this.preMultiply(m4Rotation);
	    
	    return this;
    }
    
    public M4f rotateZaxisCW(double angleRad) {
	    
	    M4f m4Rotation  = new M4f();
	    m4Rotation.setRotationZaxisCW(angleRad);
	    this.preMultiply(m4Rotation);
	    
	    return this;
    }
  
}

