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

import tk.otanod.engine.awt.MouseListeners;
import tk.otanod.engine.awt.Pointer;
import tk.otanod.engine.awt.Pointer.State;
import tk.otanod.engine.awt.Window;
import tk.otanod.engine.awt.WindowGlobalParameters;
import tk.otanod.engine.awt.WindowListeners;
import tk.otanod.engine.camera.Camera;
import tk.otanod.engine.font.Font;
import tk.otanod.engine.font.FontEffect;
import tk.otanod.engine.light.Light;
import tk.otanod.engine.render.LayoutPercentage;
import tk.otanod.engine.render.Model;
import tk.otanod.engine.render.RenderGenericInstance;
import tk.otanod.engine.render.RenderGenericInstanceAtlasText;
import tk.otanod.engine.render.RenderGenericInstanceAtlasTextGUI;
import tk.otanod.engine.render.RenderSkyBox;
import tk.otanod.engine.render.RenderTerrainMultitexture;
import tk.otanod.engine.terrain.RawTerrain;
import tk.otanod.engine.terrain.TerrainFlat;
import tk.otanod.libIO.ImageFile;
import tk.otanod.libIO.RawImage;
import tk.otanod.libIO.RawImagePack;
import tk.otanod.libMath.M4f;
import tk.otanod.libMath.V3f;
import tk.otanod.libOBJ.OBJLoader;
import tk.otanod.libOBJ.RawOBJ;


public class Main {

	// https://upload.wikimedia.org/wikipedia/commons/0/0c/Vector_Video_Standards8.svg
	// HD 720
	private static int WIDTH = 1280;
	private static int HEIGHT = 720;
	private static int FPS = 60;
	private static final float FOV = 60;										// vertical vision angle (60º)
	private static final double PITCH_SENSIBILITY = 150d / (double) HEIGHT;		// Higher values move the camera faster
	private static final double YAW_SENSIBILITY = 150d / (double) WIDTH;		// Higher values move the camera faster
	private static final float ZOOM_SENSIBILITY = 0.02f;						// Higher values move the camera faster
	private static final float UP_SENSIBILITY = 0.05f;							// Higher values move the camera faster

	private static Random random = new Random();
	
	public static void main(String args[]) {
		
		System.out.println("Main thread : " + Thread.currentThread().getName());
		
		List<Model> models = new ArrayList<>();
		
		// Common environment for all the models
		/*********************
		 *  Light
		 *********************/
		Light light = new Light(
				new V3f(256.0f, 256.0f, 256.0f),			// light position
				new V3f(0.4f, 0.4f, 0.4f),					// ambient light
				new V3f(1.0f, 1.0f, 1.0f),					// diffuse light
				new V3f(1.0f, 1.0f, 1.0f),					// specular light
				new V3f(90f/255f,120f/255f,144f/255f)		// skyColor / fogColor
				);

		/*********************
		 *  Camera
		 *********************/
		Camera camera = new Camera(
				new V3f(0.0f, 1.0f, 0.0f), 				// +Y axis is up in our world
				new V3f(0.0f, 0.0f, -20.0f),			// default: we look to -Z axis, Change to the center of the scene
				new V3f(0.0f, 3.0f, 0.0f)				// default: camera at (0,0,0)
				);

		/*********************
		 *  PVM matrices
		 *********************/
		//M4f m4Projection = new M4f(Camera.getOrthoProjectionMatrix(2.0f, 2.0f, 0.1f, 10.0f));
		//M4f m4Projection = new M4f(Camera.getInfiniteProjectionMatrix(1.0f, -1.0f, 1.0f, -1.0f, 1.0f));
		//M4f m4Projection = new M4f(Camera.getProjectionMatrix(1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 100.0f));
		M4f m4Projection = new M4f(Camera.getProjectionMatrix(FOV, WIDTH/HEIGHT, 1.0f, 150.0f));			// 150 farZPlane ==> fogDensity = 0.02
		
		
		/*********************
		 *  Models
		 *********************/
//		List<GLEventListener> models = new ArrayList<>();
		// 3D model drawn with Arrays
//		Model m1 = new ModelArray();
//		models.add(m1);
		// 3D model drawn with indices
//		Model m2 = new ModelIndices();
//		models.add(m2);
		// 3D model drawn with indices and texture
//		Model m3 = new ModelIndicesTexture();
//		models.add(m3);
		// 3D model drawn with indices and texture and MVP
//		Model m4 = new ModelMVPIndicesTexture();
//		models.add(m4);
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
//		Model m5 = new ModelMVPIndicesTextureOBJ();
//		models.add(m5);
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
//		Model m6 = new ModelMVPIndicesTextureOBJLight();
//		models.add(m6);		// add after solid objects
		// 3D model loaded from OBJ file drawn with indices and material and MVP
//		Model m7 = new ModelMVPIndicesMaterialOBJLight();
//		models.add(m7);		// add after solid objects	
			
		/*******************************
		 * SOLID OBJETS
		 *******************************/
		// 3D Flat terrain
//		float width = 512.0f;
//		int slices = 128;
//		RawTerrain terrain = TerrainFlat.getInstance().create(width, slices);				// width, slices
//		RawImage textureImageGround = ImageFile.loadFlippedImageFile("res/drawable/grass.png");
//		Model t1 = new RenderTerrain(new V3f(-width/2.0f, 0f, -width/2.0f), new V3f(1f,1f,1f), terrain, textureImageGround, camera, light, m4Projection);
//		models.add(t1);
		
		// 3D Flat terrain Multitexture
		float width = 512.0f;
		int slices = 128;
		RawTerrain terrain = TerrainFlat.getInstance().create(width, slices);				// width, slices
		RawImagePack textureImageGroundPack = new RawImagePack(new String[] {
				"res/drawable/grassy2.png",
				"res/drawable/mud.png",
				"res/drawable/grassFlowers.png",
				"res/drawable/path.png",
				"res/drawable/blendMap.png"
			}, true);
		Model t2 = new RenderTerrainMultitexture(new V3f(-width/2.0f, 0f, -width/2.0f), new V3f(1f,1f,1f), terrain, textureImageGroundPack, camera, light, m4Projection);
		models.add(t2);
		
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
//		RawOBJ dragon = OBJLoader.load("res/models/dragon.obj");
//		RawImage textureImageDragon = ImageFile.loadFlippedImageFile("res/drawable/white.png");
//		Model d = new RenderGeneric(new V3f(0f, 0f, -30f), new V3f(.2f,.2f,.2f), dragon, textureImageDragon, camera, light, m4Projection);
//		models.add(d);
			
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ stall = OBJLoader.load("res/models/stall.obj");
		RawImage textureImageStall = ImageFile.loadFlippedImageFile("res/drawable/stall.png");
		int instancesStall = 3;
		float[] instancesModelMatrixStall = createInstancesModelArray(1.5f, 1.5f, -60.0f, 60.0f, -60.0f, 60.0f, instancesStall);
		Model stallModel = new RenderGenericInstance(instancesStall, instancesModelMatrixStall, stall, textureImageStall, camera, light, m4Projection);
		models.add(stallModel);
			
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ tree1 = OBJLoader.load("res/models/tree.obj");
		RawImage textureImageTree1 = ImageFile.loadFlippedImageFile("res/drawable/tree.png");
		int instancesTree1 = 50;
		float[] instancesModelMatrixTree1 = createInstancesModelArray(3.0f, 4.0f, -60.0f, 60.0f, -60.0f, 60.0f, instancesTree1);
		Model tree1Model = new RenderGenericInstance(instancesTree1, instancesModelMatrixTree1, tree1, textureImageTree1, camera, light, m4Projection);
		models.add(tree1Model);
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ tree2 = OBJLoader.load("res/models/lowPolyTree.obj");
		RawImage textureImageTree2 = ImageFile.loadFlippedImageFile("res/drawable/lowPolyTree.png");
		int instancesTree2 = 50;
		float[] instancesModelMatrixTree2 = createInstancesModelArray(5.0f, 6.0f, -60.0f, 60.0f, -60.0f, 60.0f, instancesTree2);
		Model tree2Model = new RenderGenericInstance(instancesTree2, instancesModelMatrixTree2, tree2, textureImageTree2, camera, light, m4Projection);
		models.add(tree2Model);
		
		
		// SkyBox
		// Skybox with individual images
//		RawImagePack textureSkyBox = new RawImagePack(new String[] {
//				"res/drawable/skybox4/right.png",		// GL_TEXTURE_CUBE_MAP_POSITIVE_X 	Right
//				"res/drawable/skybox4/left.png",		// GL_TEXTURE_CUBE_MAP_NEGATIVE_X 	Left
//				"res/drawable/skybox4/top.png",			// GL_TEXTURE_CUBE_MAP_POSITIVE_Y 	Top
//				"res/drawable/skybox4/bottom.png",		// GL_TEXTURE_CUBE_MAP_NEGATIVE_Y 	Bottom
//				"res/drawable/skybox4/back.png",		// GL_TEXTURE_CUBE_MAP_POSITIVE_Z 	Back
//				"res/drawable/skybox4/front.png"		// GL_TEXTURE_CUBE_MAP_NEGATIVE_Z 	Front
//		}, false);
		// Skybox with 1 image	
		RawImagePack textureSkyBox = new RawImagePack("res/drawable/skyboxClouds.png", 4, 4);			// skyboxTEST.png
		
		RawOBJ cube = RawOBJ.buildSkyBox();
		Model skyBoxModel = new RenderSkyBox(new V3f(0.0f, 0.0f, 0.0f), new V3f(1.0f,1.0f,1.0f), cube, textureSkyBox, camera, light, m4Projection);
		models.add(skyBoxModel);
		
		
		
		/*******************************
		 * TRANSPARENT OBJETS
		 *******************************/
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ grass = OBJLoader.load("res/models/grassY.obj");
		RawImage textureImageGrass = ImageFile.loadFlippedImageFile("res/drawable/grass1.png");
		textureImageGrass.setTransparent(true);
		int instancesGrass = 300;
		float[] instancesModelMatrixGrass = createInstancesModelArray(0.5f, 1.0f, -60.0f, 60.0f, -60.0f, 60.0f, instancesGrass);
		Model grassModel = new RenderGenericInstance(instancesGrass, instancesModelMatrixGrass, grass, textureImageGrass, camera, light, m4Projection);
		models.add(grassModel);
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ tree3 = OBJLoader.load("res/models/pine_sorted.obj");
		RawImage textureImageTree3 = ImageFile.loadFlippedImageFile("res/drawable/pine.png");
		textureImageTree3.setTransparent(true);
		int instancesTree3 = 60;
		float[] instancesModelMatrixTree3 = createInstancesModelArray(3.0f, 4.0f, -60.0f, 60.0f, -60.0f, 60.0f, instancesTree3);
		Model tree3Model = new RenderGenericInstance(instancesTree3, instancesModelMatrixTree3, tree3, textureImageTree3, camera, light, m4Projection);
		models.add(tree3Model);
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		RawOBJ fern = OBJLoader.load("res/models/fern.obj");
		RawImage textureImageFern = ImageFile.loadFlippedImageFile("res/drawable/fern.png");
		textureImageFern.setTransparent(true);
		int instancesFern = 60;
		float[] instancesModelMatrixFern = createInstancesModelArray(1.0f, 2.0f, -60.0f, 60.0f, -60.0f, 60.0f, instancesFern);
		Model fernModel = new RenderGenericInstance(instancesFern, instancesModelMatrixFern, fern, textureImageFern, camera, light, m4Projection);
		models.add(fernModel);
	
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
		String fontName;
//		fontName = "Allura-Regular";
//		fontName = "Arabella";
//		fontName = "Ubuntu-R";
//		fontName = "QUIGLEYW";
//		fontName = "Qwigley-Regular";
//		fontName = "Odin-Rounded-Regular";
//		fontName = "Courier Prime Sans";
		fontName = "LeckerliOne-Regular";
//		fontName = "Carten";
	
		RawOBJ textObj = RawOBJ.buildTextQuad();
		RawImage textureImageText = ImageFile.loadImageFile("res/fonts/" + fontName + ".png");
		textureImageText.setTransparent(true);
		String str = "Hello World!";
		FontEffect fe = new FontEffect(0.47f, 0.01f, 0.06f, 0.04f, new float[] {0f,0f}, FontEffect.RGBcolor(100, 161, 0), FontEffect.RGBcolor(145, 74, 0));
		int instancesText = str.length();
		Font f = new Font("res/fonts/" + fontName + ".fnt");
		float[] m4InstancesModel = f.buildInstancesModel(str, -0.5f, 1.0f);
		float[] instancesTextureAtlasArea = f.buildInstancesTextureAtlasArea(str);
		M4f stringModelMatrix = new M4f().scale(4.0f, 2.0f, 1.0f).rotateYaxisCCW(0d).setTranslate(0.0f, 1.0f, -15.0f);
		Model textModel = new RenderGenericInstanceAtlasText(instancesText, stringModelMatrix, m4InstancesModel, instancesTextureAtlasArea, textObj, textureImageText, fe, camera, light, m4Projection);
		models.add(textModel);
		
		// GUI Text
		fontName = "Ubuntu-R";
		RawOBJ textGUI = RawOBJ.buildTextQuad();
		RawImage textureImageTextGUI = ImageFile.loadImageFile("res/fonts/" + fontName + ".png");
		textureImageTextGUI.setTransparent(true);
		Font fontGUI = new Font("res/fonts/" + fontName + ".fnt");
		FontEffect fontEffectTextGUI = new FontEffect(0.48f, 0.05f, 0.12f, 0.05f, new float[] {0f,0f}, FontEffect.RGBcolor(255, 255, 255), FontEffect.RGBcolor(0, 0, 0));
		String strGUI = getElpasedTimeString(System.nanoTime());
		int instancesTextGUI = strGUI.length();
		float[] m4InstancesModelGUI = fontGUI.buildInstancesModel(strGUI, 0.0f, 1.0f);
		float[] instancesTextureAtlasAreaGUI = fontGUI.buildInstancesTextureAtlasArea(strGUI);
		//M4f stringModelMatrixGUI = (new LayoutPercentage(0.0f, 100.0f, 0.0f, 10.0f, 5.0f, 0.0f)).getMatrix();
		M4f stringModelMatrixGUI = (new LayoutPercentage(1.0f, 5.0f, 25.0f, 5.0f)).getMatrix();
		Model texGUItModel = new RenderGenericInstanceAtlasTextGUI(instancesTextGUI, stringModelMatrixGUI, m4InstancesModelGUI, instancesTextureAtlasAreaGUI, textGUI, textureImageTextGUI, fontEffectTextGUI);
		models.add(texGUItModel);
		
		// 3D model loaded from OBJ file drawn with indices and texture and MVP
//		Model m6 = new ModelMVPIndicesTextureOBJLight();
//		models.add(m6);		// add after solid objects
		// 3D model loaded from OBJ file drawn with indices and material and MVP
//		Model m7 = new ModelMVPIndicesMaterialOBJLight();
//		models.add(m7);		// add after solid objects
		

		/*********************
		 *  Window
		 *********************/
		WindowGlobalParameters params = new WindowGlobalParameters(WIDTH, HEIGHT, FPS);
		// AWT - OpenGL window
		Window w = new Window(params, models);
		// Init the windows/openGL
		w.createDisplay("DEMO 14 - GUI Text");
		// Attach the listeners
		WindowListeners listener1 = new WindowListeners();
		w.attachListener(listener1);
		//Deque<Pointer> listPointers = new LinkedList<Pointer>();
		Deque<Pointer> pointersQueue = new ConcurrentLinkedDeque<Pointer>();
		Map<Integer,Pointer> pointersMap = new ConcurrentHashMap<>();
		MouseListeners listener2 = new MouseListeners(pointersQueue, pointersMap);
		w.attachListener(listener2);

		/*********************
		 *  Game Loop
		 *********************/
		Pointer pointer;
		while ( true ) {			
			// 1. User input
			while ( (pointer = pointersQueue.pollFirst() ) != null ) {
				// Camera LookAt
				if ( pointer.getPointerID() == 1 ) {
					if ( pointer.getState() == State.DRAGGED ) {
						double incPitchGrades = (pointer.getyOrig() - pointer.getY()) * PITCH_SENSIBILITY;
						camera.setDeltaPitch(incPitchGrades);
						double incYawGrades = (pointer.getX() - pointer.getxOrig()) * YAW_SENSIBILITY;
						camera.setDeltaYaw(incYawGrades);
					}
				}
				// Up Down
				if ( pointer.getPointerID() == 3 ) {
					if ( pointer.getState() == State.DRAGGED ) {
						float incUpDown = (float) ((pointer.getyOrig() - pointer.getY()) * UP_SENSIBILITY);
						camera.moveUpDown(incUpDown);
					}
				}
			}
			// Forward
			if ( pointersMap.containsKey(1) && pointersMap.get(1).isPressed() ) {
				camera.moveForward(ZOOM_SENSIBILITY);
			}			
			// Backwards
			if ( pointersMap.containsKey(3) && pointersMap.get(3).isPressed() ) {
				camera.moveForward(-ZOOM_SENSIBILITY);
			}				
			
			
			// Game updates
			if ( true == logicTick(1f) ) {
				strGUI = getElpasedTimeString(System.nanoTime()) + "  FPS " + params.getFPS() + " Screen " + params.getWindow_width_px() + "x" + params.getWindow_height_px();
				instancesTextGUI = strGUI.length();
				m4InstancesModelGUI = fontGUI.buildInstancesModel(strGUI, 0.0f, 1.0f);
				instancesTextureAtlasAreaGUI = fontGUI.buildInstancesTextureAtlasArea(strGUI);
				((RenderGenericInstanceAtlasTextGUI) texGUItModel).update(instancesTextGUI, m4InstancesModelGUI, instancesTextureAtlasAreaGUI);
			}

			// 2. update the camera
			for (Model model: models) {
				model.update(camera);
			}
			
			// 3. display
			w.updateDisplay();
		}
			
	}

	private static float[] createInstancesModelArray(float minSize, float maxSize, float minX, float maxX, float minZ, float maxZ, int instances) {
		
		float[] instancesModelMatrix = new float[instances * 16];
		
		for (int i=0; i<instances; i++) {
			float x = (random.nextFloat() * (maxX - minX) ) + minX;
			float y = 0.0f;
			float z = (random.nextFloat() * (maxZ - minZ) ) + minZ;

			float scale = ( random.nextFloat() * (maxSize-minSize) ) + minSize;		
			
			float angle = (float) (random.nextFloat() * Math.PI);
			
			M4f m4 = new M4f().scale(scale, scale, scale).rotateYaxisCCW(angle).setTranslate(x, y, z);
			System.arraycopy(m4.getElements(), 0, instancesModelMatrix, i*16, 16);		// arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
		}
		return instancesModelMatrix;
	}
	
	private static long startTime = System.nanoTime();
	private static String getElpasedTimeString(long now) {
		float nowSeconds = (now - startTime) / 1E9f;
		String mTime = String.format("Time %02.0f:%02.0f", Math.floor(nowSeconds / 60.0f), nowSeconds % 60); 	
		return mTime;
	}
	
	private static String getElpasedTimeDeciSecondsString(long now) {
		float nowMilliSeconds = (now - startTime) / 1E6f;
		String mTime = String.format("Time %02.0f:%02.0f:%01.0f", Math.floor(nowMilliSeconds / 60000.0f), (nowMilliSeconds / 1000f) % 60, (nowMilliSeconds / 100f) % 10); 	
		return mTime;
	}
	
	private static long time1 = System.nanoTime();
	private static long time2 = System.nanoTime();
	private static long delta = 0;
	
	private static boolean logicTick(float everySeconds) {
		boolean update = false;
		
		long everyNanoSeconds = (long) (everySeconds * 1E9);
		
		time2 = System.nanoTime();
		delta += time2 - time1;
		time1 = time2;
		if ( delta > everyNanoSeconds ) {
			update = true;
			delta -= everyNanoSeconds;
		} else {
			update = false;
		}
		
		return update;
	}
	
}
