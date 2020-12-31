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

package tk.otanod.libOBJ;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import tk.otanod.libIO.TextFile;
import tk.otanod.libMath.V2f;
import tk.otanod.libMath.V3f;

public class OBJLoader {

	public static RawOBJ load(String fname) {
		// https://en.wikipedia.org/wiki/Wavefront_.obj_file
		
		// Step 1 - read the file from disk
		String str = TextFile.readTextFile(fname);
		
		// Step 2 - extract all the v, vt, vn, f
		List<V3f> positionList = new ArrayList<>();
		List<V3f> normalList = new ArrayList<>();
		List<V2f> textureList = new ArrayList<>();
		StringBuilder sbFaces = new StringBuilder();
		
		BufferedReader reader = new BufferedReader(new StringReader(str));
        try {
            String line = reader.readLine();
            while (line != null) {
            	String[] tokens = line.split(" ");
            	if ( line.startsWith("v ") ) {
            		// v 3.227124 -0.065127 -1.000000
            		V3f position = new V3f(
            				Float.parseFloat(tokens[1]),
            				Float.parseFloat(tokens[2]),
            				Float.parseFloat(tokens[3])
            				);
            		positionList.add(position);
            	}
            	if ( line.startsWith("vn ") ) {
            		// vn -0.740379 -0.095126 -0.665365
            		V3f normal = new V3f(
            				Float.parseFloat(tokens[1]),
            				Float.parseFloat(tokens[2]),
            				Float.parseFloat(tokens[3])
            				);
            		normalList.add(normal);
            	}
            	if ( line.startsWith("vt ") ) {
            		// vt 0.921287 0.703296
            		V2f textureCoord = new V2f(
            				Float.parseFloat(tokens[1]),
            				Float.parseFloat(tokens[2])
            				);
            		textureList.add(textureCoord);
            	}
            	if ( line.startsWith("f ") ) {
            		sbFaces.append(line);
            		sbFaces.append(System.lineSeparator());
            	}           	
                line = reader.readLine();
            }
            reader.close();
            
        } catch (IOException e) {
        	e.printStackTrace();
        }
        
        // Step 3 - build the arrays using the face information
        // We need to sort the data according with the indices
        // position indices are the master information
        // we add the position, normal and texture data in the index position
        // we extract that data from the list, using the index got from the face
        
		List<Integer> indices = new ArrayList<>();
		float[] positionArray = null;
		float[] normalArray = null;
		float[] textureArray = null;
		
		// Process the FACES
		BufferedReader reader2 = new BufferedReader(new StringReader(sbFaces.toString()));
		int size = positionList.size();
		positionArray = new float[size * 3];
		normalArray = new float[size * 3];
		textureArray = new float[size * 2];
				
        try {
            String line = reader2.readLine();
            while (line != null) {		
            	// f 41/1/1 38/2/1 45/3/1									// indices to each List (position, texture, normal)
            	String[] tokens = line.split(" ");
            	for (int i=1; i<=3; i++) {									// tokens[0] is "f", so we skip it 
            		// 38/2/1
            		String[] indexToken = tokens[i].split("/");
            		
            		int positionIndex = Integer.parseInt(indexToken[0]) - 1;  	// because OBJ indices start with 1, and our arrays start with 0
            		indices.add(positionIndex);								// position index is the master to load data in all the arrays
            		V3f position = positionList.get(positionIndex);
            		positionArray[positionIndex * 3 + 0] = position.x(); 
            		positionArray[positionIndex * 3 + 1] = position.y();
            		positionArray[positionIndex * 3 + 2] = position.z();
            		
            		int textureIndex = Integer.parseInt(indexToken[1]) - 1;		// because OBJ indices start with 1, and our arrays start with 0
            		V2f textureCoord = textureList.get(textureIndex);
            		textureArray[positionIndex * 2 + 0] = textureCoord.x();
            		textureArray[positionIndex * 2 + 1] = textureCoord.y();
            		
            		int normalIndex = Integer.parseInt(indexToken[2]) - 1;		// because OBJ indices start with 1, and our arrays start with 0
            		V3f normal = normalList.get(normalIndex);
            		normalArray[positionIndex * 3 + 0] = normal.x(); 
            		normalArray[positionIndex * 3 + 1] = normal.y();
            		normalArray[positionIndex * 3 + 2] = normal.z();   		
            	}            	
            	
            	line = reader2.readLine();
            }
            reader2.close();
            
        } catch (IOException e) {
        	e.printStackTrace();
        }
		
        // Step 4 - Create the index array
		int[] indicesArray = new int[indices.size()];
		for(int i=0;i<indices.size();i++) {
			indicesArray[i] = indices.get(i);
		}
		
		// Step 5 - Create the RawOBJ
		RawOBJ model = new RawOBJ(indicesArray.length, positionArray, textureArray, normalArray, indicesArray);
		debug("OBJloader","loaded " + fname + " with " + indicesArray.length + " vertices");

		return model;
	}

	private static void debug(String tag, String msg) {
		System.out.println(">>> DEBUG >>> " + tag + " >>> " + msg);
	}
	
}
