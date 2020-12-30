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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Random;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;


public class V3fTest {

	static Random rnd;
	static int times;
	static float delta;
	static V3f[] vectors;
	
	@BeforeClass
	public static void before() {
		times = 1000;
		rnd = new Random();
		delta = V3f.THRESHOLD;
	}
	
	@Before
	public void beforeTest() {
		vectors = new V3f[] { 
				new V3f(-1.0f, -1.0f, -1.0f),
				new V3f(1.0f, 1.0f, 1.0f),
				new V3f(0.0f, 0.0f, 0.0f),
				new V3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE),							
				new V3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE),							
				new V3f(Float.MAX_VALUE, -Float.MAX_VALUE, Float.MAX_VALUE),							
				new V3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE),							
		};
	}

	@Test
	public void constructorTest() {
		V3f v3 = new V3f();
		assertNotNull(v3);
	}
	
	@Test
	public void cloneTest() {
		V3f v3clone;
		for ( V3f v3 : vectors ) {
			v3clone = (V3f) v3.clone();
			assertEquals(v3.x(), v3clone.x(), delta);
			assertEquals(v3.y(), v3clone.y(), delta);
			assertEquals(v3.z(), v3clone.z(), delta);
		}
	}
	
	@Test
	public void equalsTest() {
		float aux = 0f;
		for (int i=0;i<500;i++) {
			aux += 0.0002f;
		}
		V3f v3a = new V3f(aux, 0.0f, Float.MAX_VALUE);
		assertTrue(v3a.equals(v3a));
		assertFalse(v3a.equals(null));
		assertFalse(v3a.equals("hello"));
		
		V3f v3b = new V3f(.1f, 0.0f, Float.MAX_VALUE);
		System.out.println(v3a + "\n" + v3b);
		assertTrue(v3a.equals(v3b));
		
	}
	
	@Test
	public void scaleTest() {
		V3f aux;
		
		for(int t=0;t<times;t++) {
				float scalar = rnd.nextFloat();
				for (V3f v3 : vectors) {
					aux = v3.clone().scale(scalar);
					assertEquals(aux.x(), v3.x()*scalar, delta);
					assertEquals(aux.y(), v3.y()*scalar, delta);
					assertEquals(aux.z(), v3.z()*scalar, delta);
				}
		}
	}
	
	@Test
	public void scaleNonUniformTest() {
		V3f aux;
		
		for(int t=0;t<times;t++) {
			float scalar1 = (float) (rnd.nextFloat() * Math.pow(-1, rnd.nextInt(2)));
			float scalar2 = (float) (rnd.nextFloat() * Math.pow(-1, rnd.nextInt(2)));
			float scalar3 = (float) (rnd.nextFloat() * Math.pow(-1, rnd.nextInt(2)));
			for (V3f v3 : vectors) {
				aux = v3.clone().scaleNonUniform(scalar1, scalar2, scalar3);
				assertEquals(aux.x(), v3.x()*scalar1, delta);
				assertEquals(aux.y(), v3.y()*scalar2, delta);
				assertEquals(aux.z(), v3.z()*scalar3, delta);
			}
		}
	}
	
	@Test
	public void addTest() {
		for(int t=0;t<times;t++) {
			V3f v3a = new V3f(rnd.nextFloat()*Float.MAX_VALUE, rnd.nextFloat()*Float.MIN_VALUE, rnd.nextFloat()*Float.MAX_VALUE);
			V3f v3b = new V3f(rnd.nextFloat()*Float.MAX_VALUE, rnd.nextFloat()*Float.MIN_VALUE, rnd.nextFloat()*Float.MAX_VALUE);
			
			V3f v3add = v3a.clone().add(v3b);
			
			assertEquals( v3add.x(), v3a.x()+v3b.x(), delta );
			assertEquals( v3add.y(), v3a.y()+v3b.y(), delta );
			assertEquals( v3add.z(), v3a.z()+v3b.z(), delta );
		}
	}
	
	@Test
	public void subTest() {
		for(int t=0;t<times;t++) {
			V3f v3a = new V3f(rnd.nextFloat()*Float.MAX_VALUE, rnd.nextFloat()*Float.MIN_VALUE, rnd.nextFloat()*Float.MAX_VALUE);
			V3f v3b = new V3f(rnd.nextFloat()*Float.MAX_VALUE, rnd.nextFloat()*Float.MIN_VALUE, rnd.nextFloat()*Float.MAX_VALUE);
			
			V3f v3sub = v3a.clone().sub(v3b);
			
			assertEquals( v3sub.x(), v3a.x()-v3b.x(), delta );
			assertEquals( v3sub.y(), v3a.y()-v3b.y(), delta );
			assertEquals( v3sub.z(), v3a.z()-v3b.z(), delta );
			
			// v3a-v3b = v3a + (v3b*-1)
			assertEquals( v3sub, v3a.add(v3b.scale(-1f)));
		}
	}
	
	@Test
	public void magnitudeTest() {
		V3f v3a = new V3f(0f, 0f, 0f);
		assertEquals( v3a.magnitude(), 0f, delta);
		V3f v3b = new V3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		assertEquals( v3b.magnitude(), Float.POSITIVE_INFINITY, delta);
		V3f v3c = new V3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
		assertEquals( v3c.magnitude(), 0f, delta);
		V3f v3d = new V3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		assertEquals( v3d.magnitude(), Float.POSITIVE_INFINITY, delta);
	}
	
	@Test
	public void magnitudeAsDotProductTest() {
		for (int t=0;t<times;t++) {
			V3f v3a = new V3f(
					rnd.nextFloat()*Float.MAX_VALUE*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*Float.MAX_VALUE*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*Float.MAX_VALUE*Math.pow(-1, rnd.nextInt(2)));
			assertEquals( v3a.magnitude(), Math.sqrt(v3a.dotProduct(v3a)), delta);
		}
	}

	@Test
	public void squareMagnitudeTest() {
		V3f v3a = new V3f(0f, 0f, 0f);
		assertEquals( v3a.magnitude(), 0f, delta);
		V3f v3b = new V3f(Float.MAX_VALUE, Float.MAX_VALUE, Float.MAX_VALUE);
		assertEquals( v3b.magnitude(), Float.POSITIVE_INFINITY, delta);
		V3f v3c = new V3f(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
		assertEquals( v3c.magnitude(), 0f, delta);
		V3f v3d = new V3f(-Float.MAX_VALUE, -Float.MAX_VALUE, -Float.MAX_VALUE);
		assertEquals( v3d.magnitude(), Float.POSITIVE_INFINITY, delta);
		
		for(int t=0;t<times;t++) {
			V3f v3 = new V3f(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
			
			assertEquals( v3.squareMagnitude(), v3.magnitude()*v3.magnitude(), delta);
			
		}
	}

	@Test
	public void unitVectorTest() {
		for (int t=0; t<times; t++) {
			V3f v3a = new V3f(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
			//System.out.println(v3a.magnitude());
			//System.out.println(v3a);
			v3a.unitVector();
			//System.out.println(v3a);
			assertEquals( 1.0f, v3a.magnitude(), delta);
		}
	}
	
	@Test
	public void normalizeTest() {
		for (int t=0; t<times; t++) {
			V3f v3a = new V3f(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());
			//System.out.println(v3a.magnitude());
			//System.out.println(v3a);
			v3a.normalize();
			//System.out.println(v3a);
			assertEquals( 1.0f, v3a.magnitude(), delta);
		}
	}
	
	@Test
	public void dotProductTest() {
		// Cartesian axis dot products
		V3f i = new V3f(1.0f, 0.0f, 0.0f);
		V3f j = new V3f(0.0f, 1.0f, 0.0f);
		V3f k = new V3f(0.0f, 0.0f, 1.0f);
		
		assertEquals( 0.0f, i.dotProduct(j), delta);
		assertEquals( 0.0f, i.dotProduct(k), delta);
		
		assertEquals( 0.0f, j.dotProduct(i), delta);
		assertEquals( 0.0f, j.dotProduct(k), delta);
		
		assertEquals( 0.0f, k.dotProduct(i), delta);
		assertEquals( 0.0f, k.dotProduct(j), delta);
		
		assertEquals( 1.0f, i.dotProduct(i), delta);
		assertEquals( 1.0f, j.dotProduct(j), delta);
		assertEquals( 1.0f, k.dotProduct(k), delta);
		
		assertEquals( -1.0f, i.dotProduct(i.clone().scale(-1.0f)), delta);
		assertEquals( -1.0f, j.dotProduct(j.clone().scale(-1.0f)), delta);
		assertEquals( -1.0f, k.dotProduct(k.clone().scale(-1.0f)), delta);
		
		float aux = rnd.nextFloat();
		assertEquals( aux, i.dotProduct(i.clone().scale(aux)), delta);
		assertEquals( aux, j.dotProduct(j.clone().scale(aux)), delta);
		assertEquals( aux, k.dotProduct(k.clone().scale(aux)), delta);
	}
	
	@Test
	public void dotProductCommutativeTest() {
		for (int t=0;t<times;t++) {
			V3f v3a = new V3f(
					rnd.nextFloat()*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*Math.pow(-1, rnd.nextInt(2)));
			V3f v3b = new V3f(
					rnd.nextFloat()*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*Math.pow(-1, rnd.nextInt(2)));
			
			assertEquals( v3a.dotProduct(v3b), v3b.dotProduct(v3a), delta);
		}
	}
	
	@Test
	public void cosAngleTest() {
		// Cartesian axis dot products
		V3f i = new V3f(1.0f, 0.0f, 0.0f);
		V3f j = new V3f(0.0f, 1.0f, 0.0f);
		V3f k = new V3f(0.0f, 0.0f, 1.0f);
		
		assertEquals( 0.0f, i.cosAngle(j), delta);
		assertEquals( 0.0f, i.cosAngle(k), delta);
		
		assertEquals( 0.0f, j.cosAngle(i), delta);
		assertEquals( 0.0f, j.cosAngle(k), delta);
		
		assertEquals( 0.0f, k.cosAngle(i), delta);
		assertEquals( 0.0f, k.cosAngle(j), delta);
		
		assertEquals( 1.0f, i.cosAngle(i), delta);
		assertEquals( 1.0f, j.cosAngle(j), delta);
		assertEquals( 1.0f, k.cosAngle(k), delta);
		
		assertEquals( -1.0f, i.cosAngle(i.clone().scale(-1.0f)), delta);
		assertEquals( -1.0f, j.cosAngle(j.clone().scale(-1.0f)), delta);
		assertEquals( -1.0f, k.cosAngle(k.clone().scale(-1.0f)), delta);
		
		float aux = rnd.nextFloat();
		assertEquals( 1.0f, i.cosAngle(i.clone().scale(aux)), delta);
		assertEquals( 1.0f, j.cosAngle(j.clone().scale(aux)), delta);
		assertEquals( 1.0f, k.cosAngle(k.clone().scale(aux)), delta);
	}
	
	@Test
	public void angleRadBigVectorsTest() {
		float factor = 1E18f;
		for (int t=0;t<times;t++) {
			V3f v3a = new V3f(
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)));
			V3f v3b = new V3f(
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)));
			Assert.assertNotEquals( Float.NaN, v3a.angleRad(v3b));
		}
	}
	
	@Test
	public void angleRadSmallVectorsTest() {
		float factor = 1E-18f;
		for (int t=0;t<times;t++) {
			V3f v3a = new V3f(
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)));
			V3f v3b = new V3f(
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)),
					rnd.nextFloat()*factor*Math.pow(-1, rnd.nextInt(2)));
			Assert.assertNotEquals( Float.NaN, v3a.angleRad(v3b));
		}
	}
	
	@Test
	public void crossProductTest() {
		// Cartesian axis dot products
		V3f zero = new V3f(0.0f, 0.0f, 0.0f);
		float s2 = (float) (Math.sqrt(2)/2d);
		V3f i = new V3f(s2, s2, 0.0f);
		V3f j = new V3f(-s2, s2, 0.0f);
		V3f k = new V3f(0.0f, 0.0f, 1.0f);
		
		assertNotEquals(i,j);
		assertEquals( k, i.clone().crossProduct(j));
		assertEquals( j.clone().scale(-1.0f), i.clone().crossProduct(k));
		
		assertEquals( k.clone().scale(-1.0f), j.clone().crossProduct(i));
		assertEquals( i, j.clone().crossProduct(k));
		
		assertEquals( j, k.clone().crossProduct(i));
		assertEquals( i.clone().scale(-1.0f), k.clone().crossProduct(j));
		
		assertEquals( zero, i.clone().crossProduct(i));
		assertEquals( zero, j.clone().crossProduct(j));
		assertEquals( zero, k.clone().crossProduct(k));
		
		assertEquals( zero, i.clone().crossProduct(i.clone().scale(-1.0f)));
		assertEquals( zero, j.clone().crossProduct(j.clone().scale(-1.0f)));
		assertEquals( zero, k.clone().crossProduct(k.clone().scale(-1.0f)));
		
		float aux = rnd.nextFloat();
		assertEquals( zero, i.clone().crossProduct(i.clone().scale(aux)));
		assertEquals( zero, j.clone().crossProduct(j.clone().scale(aux)));
		assertEquals( zero, k.clone().crossProduct(k.clone().scale(aux)));
	}

	@Test
	public void squareDistanceTest() {
		// Cartesian axis dot products
		V3f zero = new V3f(0.0f, 0.0f, 0.0f);
		V3f i = new V3f(1.0f, 0.0f, 0.0f);
		V3f j = new V3f(0.0f, 1.0f, 0.0f);
		V3f k = new V3f(0.0f, 0.0f, 1.0f);
		
		assertEquals( 2.0f, i.squareDistance(j), delta);
		assertEquals( 2.0f, i.squareDistance(k), delta);
		assertEquals( 1.0f, i.squareDistance(zero), delta);
		
		assertEquals( 2.0f, j.squareDistance(i), delta);
		assertEquals( 2.0f, j.squareDistance(k), delta);
		assertEquals( 1.0f, j.squareDistance(zero), delta);
		
		assertEquals( 2.0f, k.squareDistance(i), delta);
		assertEquals( 2.0f, k.squareDistance(j), delta);
		assertEquals( 1.0f, k.squareDistance(zero), delta);
		
		
	}
}
