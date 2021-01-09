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

package tk.otanod.demo;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.jogamp.opengl.GLEventListener;

import tk.otanod.engine.awt.MouseListeners;
import tk.otanod.engine.awt.Pointer;
import tk.otanod.engine.awt.Pointer.State;
import tk.otanod.engine.awt.Window;
import tk.otanod.engine.awt.WindowListeners;
import tk.otanod.engine.camera.Camera;
import tk.otanod.engine.light.Light;
import tk.otanod.engine.render.Model;
import tk.otanod.engine.render.ModelMVPIndicesMaterialOBJLight;
import tk.otanod.engine.render.RenderGeneric;
import tk.otanod.engine.render.RenderTerrain;
import tk.otanod.engine.terrain.RawTerrain;
import tk.otanod.engine.terrain.TerrainFlat;
import tk.otanod.libIO.ImageFile;
import tk.otanod.libIO.RawImage;
import tk.otanod.libMath.M4f;
import tk.otanod.libMath.V3f;
import tk.otanod.libOBJ.OBJLoader;
import tk.otanod.libOBJ.RawOBJ;

public class Main {

	// https://upload.wikimedia.org/wikipedia/commons/0/0c/Vector_Video_Standards8.svg
	// HD 720
	private static final int WIDTH = 1280;
	private static final int HEIGHT = 720;
	private static final float FOV = 60;										// vertical vision angle (60º)
	private static final double PITCH_SENSIBILITY = 100d / (double) HEIGHT;		// Higher values move the camera faster
	private static final double YAW_SENSIBILITY = 100d / (double) WIDTH;		// Higher values move the camera faster
	private static final float ZOOM_SENSIBILITY = 0.05f;						// Higher values move the camera faster

	public static void main(String args[]) {
		
		System.out.println("Main thread : " + Thread.currentThread().getName());
		
		List<Model> models = new ArrayList<>();
		// 3D model drawn with Arrays
//		GLEventListener m1 = new ModelArray();
//		models.add(m1);
		// 3D model drawn with indices
//		GLEventListener m2 = new ModelIndices();
//		models.add(m2);
		// 3D model drawn with indices and texture
//		GLEventListener m3 = new ModelIndicesTexture();
//		models.add(m3);
		// 3D model drawn with indices and texture and MVP
//		GLEventListener m4 = new ModelMVPIndicesTexture();
//		models.add(m4);
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
//		GLEventListener m5 = new ModelMVPIndicesTextureOBJ();
//		models.add(m5);
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
//		GLEventListener m6 = new ModelMVPIndicesTextureOBJLight();
//		models.add(m6);
		// 3D model loaded from OBJ file drawn with indices and material and MVP
//		GLEventListener m7 = new ModelMVPIndicesMaterialOBJLight();
//		models.add(m7);

		// Common environment for all the models
		/*********************
		 *  Light
		 *********************/
		Light light = new Light(
				new V3f(256.0f, 256.0f, 256.0f),
				new V3f(0.4f, 0.4f, 0.4f),
				new V3f(1.0f, 1.0f, 1.0f),
				new V3f(1.0f, 1.0f, 1.0f)
				);

		/*********************
		 *  Camera
		 *********************/
		Camera camera = new Camera(
				new V3f(0.0f, 1.0f, 0.0f), 				// +Y axis is up in our world
				new V3f(0.0f, 0.0f, -20.0f),			// default: we look to -Z axis, Change to the center of the object (our textured cube)
				new V3f(0.0f, 2.0f, 0.0f)				// default: camera at (0,0,0)
				);
		
		/*********************
		 *  PVM matrices
		 *********************/
		//M4f m4Projection = new M4f(Camera.getOrthoProjectionMatrix(2.0f, 2.0f, 0.1f, 10.0f));
		//M4f m4Projection = new M4f(Camera.getInfiniteProjectionMatrix(1.0f, -1.0f, 1.0f, -1.0f, 1.0f));
		//M4f m4Projection = new M4f(Camera.getProjectionMatrix(1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 100.0f));
		M4f m4Projection = new M4f(Camera.getProjectionMatrix(FOV, WIDTH/HEIGHT, 1.0f, 100.0f));
		
		
		// 3D Flat terrain
		RawTerrain terrain = TerrainFlat.getInstance().create();
		RawImage textureImageGround = ImageFile.loadFlippedImageFile("res/drawable/grass.png");
		Model t1 = new RenderTerrain(new V3f(-400f, 0f, -400f), new V3f(1f,1f,1f), terrain, textureImageGround, camera, light, m4Projection);
		models.add(t1);
		
		
//		// 3D model loaded from OBJ file drawn with indices and texture and MVP
//		RawOBJ dragon = OBJLoader.load("res/models/dragon.obj");
//		RawImage textureImageDragon = ImageFile.loadFlippedImageFile("res/drawable/white.png");
//		Model d = new RenderGeneric(new V3f(0f, 0f, -30f), new V3f(1f,1f,1f), dragon, textureImageDragon, camera, light, m4Projection);
//		models.add(d);

		Random random = new Random();
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ tree = OBJLoader.load("res/models/tree.obj");
		RawImage textureImageTree = ImageFile.loadFlippedImageFile("res/drawable/tree.png");
		for (int i=0; i<50; i++) {
			float x = (random.nextFloat() * 40.0f ) - 10.0f;
			float z = (random.nextFloat() * 40.0f ) - 20.0f;
			float scale = 0.7f + (random.nextFloat() * 1.0f);
			Model aux = new RenderGeneric(new V3f(x, 0f, z), new V3f(scale, scale, scale), tree, textureImageTree, camera, light, m4Projection);
			models.add(aux);
		}
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ tree2 = OBJLoader.load("res/models/lowPolyTree.obj");
		RawImage textureImageTree2 = ImageFile.loadFlippedImageFile("res/drawable/lowPolyTree.png");
		for (int i=0; i<50; i++) {
			float x = (random.nextFloat() * 40.0f ) - 30.0f;
			float z = (random.nextFloat() * 40.0f ) - 20.0f;
			float scale = 2.0f + (random.nextFloat() * 1f);
			Model aux = new RenderGeneric(new V3f(x, 0f, z), new V3f(scale, scale, scale), tree2, textureImageTree2, camera, light, m4Projection);
			models.add(aux);
		}
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ fern = OBJLoader.load("res/models/fern.obj");
		RawImage textureImageFern = ImageFile.loadFlippedImageFile("res/drawable/fern.png");
		textureImageFern.setTransparent(true);
		for (int i=0; i<20; i++) {
			float x = (random.nextFloat() * 60.0f ) - 30.0f;
			float z = (random.nextFloat() * 60.0f ) - 30.0f;
			Model aux = new RenderGeneric(new V3f(x, 0f, z), new V3f(.2f,.2f,.2f), fern, textureImageFern, camera, light, m4Projection);
			models.add(aux);
		}
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ grass = OBJLoader.load("res/models/grass.obj");
		RawImage textureImageGrass = ImageFile.loadFlippedImageFile("res/drawable/grass5.png");
		textureImageGrass.setTransparent(true);
		for (int i=0; i<40; i++) {
			float x = (random.nextFloat() * 60.0f ) - 30.0f;
			float z = (random.nextFloat() * 60.0f ) - 30.0f;
			Model aux = new RenderGeneric(new V3f(x, 0f, z), new V3f(0.3f,0.3f,0.3f), grass, textureImageGrass, camera, light, m4Projection);
			models.add(aux);
		}
		
		// AWT - OpenGL window
		Window w = new Window(WIDTH, HEIGHT, models);
		// Init the windows/openGL
		w.createDisplay("DEMO 8 - Flat terrain");
		// Attach the listeners
		WindowListeners listener1 = new WindowListeners();
		w.attachListener(listener1);
		//Deque<Pointer> listPointers = new LinkedList<Pointer>();
		Deque<Pointer> pointersQueue = new ConcurrentLinkedDeque<Pointer>();
		Map<Integer,Pointer> pointersMap = new ConcurrentHashMap<>();
		MouseListeners listener2 = new MouseListeners(pointersQueue, pointersMap);
		w.attachListener(listener2);

		// Display the window (loop)
		// 1. User input
		Pointer pointer;
		while ( true ) {			
			while ( (pointer = pointersQueue.pollFirst() ) != null ) {
				if ( pointer.getPointerID() == 1 ) {
					if ( pointer.getState() == State.DRAGGED ) {
						double incPitchGrades = (pointer.getyOrig() - pointer.getY()) * PITCH_SENSIBILITY;
						camera.setDeltaPitch(incPitchGrades);
						double incYawGrades = (pointer.getX() - pointer.getxOrig()) * YAW_SENSIBILITY;
						camera.setDeltaYaw(incYawGrades);
					}
				}
			}
			if ( pointersMap.containsKey(3) && pointersMap.get(3).isPressed() ) {
				camera.moveForward(ZOOM_SENSIBILITY);
			}				
			
			// 2. update the camera
			for (Model model: models) {
				model.update(camera);
			}
			
			// 3. display
			w.updateDisplay();
		}
			
	}
	
}
