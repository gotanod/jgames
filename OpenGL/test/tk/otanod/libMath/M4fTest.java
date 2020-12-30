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

public class M4fTest {

	static Random rnd;
	static int times;
	static float delta;
	
	@BeforeClass
	public static void before() {
		times = 1000;
		rnd = new Random();
		delta = V3f.THRESHOLD;
	}
	
	@Before
	public void beforeTest() {
	}

	@Test
	public void constructorTest() {
		M4f m4 = new M4f();
		assertNotNull(m4);
	}
	
	@Test
	public void constructorByFloatArrayTest() {
		// create a random 16 float vector
		float[] data = new float[16];
		for(int i=0;i<16;i++) {
			data[i] = rnd.nextFloat();
		}
		
		// create a M4f using the random float vector
		M4f m4 = new M4f(data);
		
		assertNotNull(m4);
		
		for(int i=0;i<16;i++) {
			Assert.assertEquals(data[i], m4.getElement(i), 0.0f);
		}
	}
	
	@Test
	public void getElementTest() {
		for (int t=0;t<times;t++) {
			// create a random 16 float vector
			float[] data = new float[16];
			for(int i=0;i<16;i++) {
				data[i] = rnd.nextFloat();
			}
			
			// create a M4f using the random float vector
			M4f m4 = new M4f(data);
			
			for(int i=0;i<16;i++) {
				Assert.assertEquals(data[i], m4.getElement(i), 0.0f);
			}
		}
	}
	
	@Test
	public void getFloatsUnmutableTest() {
		// create a random 16 float vector
		float[] data = new float[16];
		for(int i=0;i<16;i++) {
			data[i] = rnd.nextFloat();
		}
		
		// create a M4f using the random float vector
		M4f m4 = new M4f(data);
		
		float[] data2 = m4.getFloats();
		
		for(int i=0;i<16;i++) {
			data2[i] = 0.0f;
		}
		
		assertEquals(data[0], m4.getElement(0, 0), 0.0f);
		assertEquals(data[1], m4.getElement(1, 0), 0.0f);
		assertEquals(data[2], m4.getElement(2, 0), 0.0f);
		assertEquals(data[3], m4.getElement(3, 0), 0.0f);
		assertEquals(data[4], m4.getElement(0, 1), 0.0f);
		assertEquals(data[5], m4.getElement(1, 1), 0.0f);
		assertEquals(data[6], m4.getElement(2, 1), 0.0f);
		assertEquals(data[7], m4.getElement(3, 1), 0.0f);
		assertEquals(data[8], m4.getElement(0, 2), 0.0f);
		assertEquals(data[9], m4.getElement(1, 2), 0.0f);
		assertEquals(data[10], m4.getElement(2, 2), 0.0f);
		assertEquals(data[11], m4.getElement(3, 2), 0.0f);
		assertEquals(data[12], m4.getElement(0, 3), 0.0f);
		assertEquals(data[13], m4.getElement(1, 3), 0.0f);
		assertEquals(data[14], m4.getElement(2, 3), 0.0f);
		assertEquals(data[15], m4.getElement(3, 3), 0.0f);
		
	}

	@Test
	public void getElementsUnmutableTest() {
		// create a random 16 float vector
		float[] data = new float[16];
		for(int i=0;i<16;i++) {
			data[i] = rnd.nextFloat();
		}
		
		// create a M4f using the random float vector
		M4f m4 = new M4f(data);
		
		float[] data2 = m4.getElements();
		
		for(int i=0;i<16;i++) {
			data2[i] = 0.0f;
		}
		
		assertEquals(data[0], m4.getElement(0, 0), 0.0f);
		assertEquals(data[1], m4.getElement(1, 0), 0.0f);
		assertEquals(data[2], m4.getElement(2, 0), 0.0f);
		assertEquals(data[3], m4.getElement(3, 0), 0.0f);
		assertEquals(data[4], m4.getElement(0, 1), 0.0f);
		assertEquals(data[5], m4.getElement(1, 1), 0.0f);
		assertEquals(data[6], m4.getElement(2, 1), 0.0f);
		assertEquals(data[7], m4.getElement(3, 1), 0.0f);
		assertEquals(data[8], m4.getElement(0, 2), 0.0f);
		assertEquals(data[9], m4.getElement(1, 2), 0.0f);
		assertEquals(data[10], m4.getElement(2, 2), 0.0f);
		assertEquals(data[11], m4.getElement(3, 2), 0.0f);
		assertEquals(data[12], m4.getElement(0, 3), 0.0f);
		assertEquals(data[13], m4.getElement(1, 3), 0.0f);
		assertEquals(data[14], m4.getElement(2, 3), 0.0f);
		assertEquals(data[15], m4.getElement(3, 3), 0.0f);
		
	}

	@Test
	public void getFloatsVSgetElementsTest() {
		M4f m4 = new M4f();
		long t1 = System.nanoTime();
		for(int t=0;t<times*times;t++) {
			m4.getFloats();
		}
		long t2 = System.nanoTime();
		for(int t=0;t<times*times;t++) {
			m4.getElements();
		}
		long t3 = System.nanoTime();
		System.out.println("getFloats ==> " + (t2-t1));
		System.out.println("getElements ==> " + (t3-t2));
	}

	@Test
	public void identityTest() {
		// create a random 16 float vector
		float[] data = new float[16];
		for(int i=0;i<16;i++) {
			data[i] = rnd.nextFloat();
		}
		
		// create a M4f using the random float vector
		M4f m4 = new M4f(data);
		
		// re-initialize as identity
		m4.identity();
		
		assertEquals(1.0f,  m4.getElement(0, 0), 0);
		assertEquals(1.0f,  m4.getElement(1, 1), 0);
		assertEquals(1.0f,  m4.getElement(2, 2), 0);
		assertEquals(1.0f,  m4.getElement(3, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(0, 1), 0);
		assertEquals(0.0f,  m4.getElement(0, 2), 0);
		assertEquals(0.0f,  m4.getElement(0, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(1, 0), 0);
		assertEquals(0.0f,  m4.getElement(1, 2), 0);
		assertEquals(0.0f,  m4.getElement(1, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(2, 0), 0);
		assertEquals(0.0f,  m4.getElement(2, 1), 0);
		assertEquals(0.0f,  m4.getElement(2, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(3, 0), 0);
		assertEquals(0.0f,  m4.getElement(3, 1), 0);
		assertEquals(0.0f,  m4.getElement(3, 2), 0);
		
		for(int t=0;t<times;t++) {
			m4.preMultiply(m4);
		}
		
		assertEquals(1.0f,  m4.getElement(0, 0), 0);
		assertEquals(1.0f,  m4.getElement(1, 1), 0);
		assertEquals(1.0f,  m4.getElement(2, 2), 0);
		assertEquals(1.0f,  m4.getElement(3, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(0, 1), 0);
		assertEquals(0.0f,  m4.getElement(0, 2), 0);
		assertEquals(0.0f,  m4.getElement(0, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(1, 0), 0);
		assertEquals(0.0f,  m4.getElement(1, 2), 0);
		assertEquals(0.0f,  m4.getElement(1, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(2, 0), 0);
		assertEquals(0.0f,  m4.getElement(2, 1), 0);
		assertEquals(0.0f,  m4.getElement(2, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(3, 0), 0);
		assertEquals(0.0f,  m4.getElement(3, 1), 0);
		assertEquals(0.0f,  m4.getElement(3, 2), 0);
		
		for(int t=0;t<times;t++) {
			m4.postMultiply(m4);
		}
		
		assertEquals(1.0f,  m4.getElement(0, 0), 0);
		assertEquals(1.0f,  m4.getElement(1, 1), 0);
		assertEquals(1.0f,  m4.getElement(2, 2), 0);
		assertEquals(1.0f,  m4.getElement(3, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(0, 1), 0);
		assertEquals(0.0f,  m4.getElement(0, 2), 0);
		assertEquals(0.0f,  m4.getElement(0, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(1, 0), 0);
		assertEquals(0.0f,  m4.getElement(1, 2), 0);
		assertEquals(0.0f,  m4.getElement(1, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(2, 0), 0);
		assertEquals(0.0f,  m4.getElement(2, 1), 0);
		assertEquals(0.0f,  m4.getElement(2, 3), 0);
		
		assertEquals(0.0f,  m4.getElement(3, 0), 0);
		assertEquals(0.0f,  m4.getElement(3, 1), 0);
		assertEquals(0.0f,  m4.getElement(3, 2), 0);
		
	}

	@Test
	public void determinantTest() {
		M4f m4;
		float d;
		
		m4 = new M4f(new float[] { 1, 4, 0, 1, 2, 7, 0, 2, 6, 3, 0, 2, 6, 2, 0, 9});
		d = m4.determinant();
		assertEquals(0f, d, 0.0f);
		
		m4 = new M4f(new float[] { 2, 6, 0, 1, 1, 7, 6, 2, 2, 6, 0, 1, 3, 9, 0, 4});
		d = m4.determinant();
		assertEquals(0f, d, 0.0f);
		
		m4 = new M4f(new float[] { 1, 2, 4, 3, 2, 5, 10, 4, 3, 7, 14, 2, 4, 3, 6, 7});
		d = m4.determinant();
		assertEquals(0f, d, 0.0f);
		
		m4 = new M4f(new float[] { 4, 0, 0, 0, 3, 1, -1, 3, 2, -3, 3, 1, 2, 3, 3, 1});
		d = m4.determinant();
		assertEquals(-240f, d, 0.0f);
		
		m4 = new M4f(new float[] { 1, 2, 1, -1, -2, 3, 1, 4, 3, 1, 1, 2, 2, -1, 1, 1 });
		d = m4.determinant();
		assertEquals(-47f, d, 0.0f);
		
		m4 = new M4f(new float[] { 2, 4, 5, 1, 5, 1, 3, 0, 1, 6, 7, 2, 4, 3, 2, 4});
		d = m4.determinant();
		assertEquals(-36f, d, 0.0f);
		
		m4 = new M4f(new float[] { 4, 1, 2, 1, 7, 3, 5, 4, 2, 1, 3, 2, 3, 2, 4, 3});
		d = m4.determinant();
		assertEquals(-3f, d, 0.0f);
		
		m4 = new M4f(new float[] { 2, 1, 3, 4, 1, 3, 4, 2, 3, 4, 2, 1, 4, 2, 1, 3});
		d = m4.determinant();
		assertEquals(0f, d, 0.0f);
		
		m4 = new M4f(new float[] { 1.223f,9.2342334f,0.3434f,3.1415926789f,0.1234f,0.0002324f,-0.34832748f,25.2524f,-0.3434f,1.11111f,-0.34343f,-0.00232324f,9.34342134f,0.34348f,3.1415926f,1f});
		d = m4.determinant();
		assertEquals(-487.23425593460834836f, d, 0.0001f);  // ROUND ERROR!!!!!
		
	}

	@Test
	public void getInverseTest() {
		for(int t=0;t<times;t++) {
			// create a random 16 float vector
			float[] data = new float[16];
			for(int i=0;i<16;i++) {
				data[i] = rnd.nextFloat();
			}
			
			// create a M4f using the random float vector
			M4f m4 = new M4f(data);
			
			// create inverse
			M4f m4inv = m4.getInverse();
			
			// identity
			M4f m4identity = new M4f();
			m4identity.identity();
			
			//System.out.println(m4);
			System.out.println(t);
			assertEquals(m4identity, m4.preMultiply(m4inv));
		}
		
	}

	@Test
	public void getTransposeTest() {
		for(int t=0;t<times;t++) {
			// create a random 16 float vector
			float[] data = new float[16];
			for(int i=0;i<16;i++) {
				data[i] = rnd.nextFloat();
			}
			
			// create a M4f using the random float vector
			M4f m4 = new M4f(data);
			
			// create inverse
			M4f m4traspose = m4.getTranspose();
			
			Assert.assertNotEquals(m4,  m4traspose);
			
			Assert.assertEquals(m4,  m4traspose.getTranspose());
			
		}
		
	}
}
