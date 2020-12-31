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

package tk.otanod.libIO;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

public class TextFile {
	////////////////////////////////////////////////////////////////////
	/// READ TEXT FILES
	////////////////////////////////////////////////////////////////////
	
	public static String readTextFile(String fname) {
		String everything = "";
		
		try {
			//AssetManager assetManager = activity.getAssets();												// @android assets
			//BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open(fname)));		// @android  assets
			FileReader fr = new FileReader(fname);
			BufferedReader br = new BufferedReader(fr);			// JDK 6
			
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();
			while (line != null) {
				sb.append(line);
				sb.append(System.lineSeparator());
				line = br.readLine();
			}

			br.close();
			everything = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return(everything);		
	}
	
	public static String readTextFile7(String sName) {
		Path path = Paths.get(sName);				// JDK 1.7	// @android 26
		
		//Charset charset = Charset.forName("ISO-8859-13");  // others like "US-ASCII" see  http://www.iana.org/assignments/character-sets
		//ArrayList<String> lines = new ArrayList<String>();
		String str = "";
		try (
			BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.ISO_8859_1);		// JDK 7		
		) {
			String line = null;
			while (( line = reader.readLine() ) != null )  {
				//lines.add(line);
			str += line + "\n";		// TODO: adds extra "\n" at the end of the String
				
			}	
			return(str);
		} catch ( IOException e ) {
			e.printStackTrace();
			return(str);
		}
	}
	
	public static String readContent(String sName) {
		String sContent = "";
		FileInputStream fileInputStream = null;

		File file = new File(sName);

		try {
			//ANDROID: fileInputStream =  context.openFileInput(sName);
			fileInputStream = new FileInputStream(file);
			byte[] buffer =  new byte[(int) fileInputStream.getChannel().size()];
			fileInputStream.read(buffer);

			sContent = new String(buffer);
			buffer = null;
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
			} catch ( NullPointerException | IOException e) {
				e.printStackTrace();
			}
		}
		return sContent;
	}
	
	////////////////////////////////////////////////////////////////////
	/// WRITE TEXT FILES
	////////////////////////////////////////////////////////////////////
	
	public static void writeTextFile(String sName, String sContent) {
		// JDK 1.6
		FileOutputStream fileOutputStream = null;

		File file = new File(sName);

		try {
			//ANDROID: fileOutputStream =  context.openFileOutput(sName, MODE_PRIVATE);
			fileOutputStream = new FileOutputStream(file, false);
			fileOutputStream.write(sContent.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch ( NullPointerException | IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void writeTextFile7(String sName, String sContent) {
		Path path = Paths.get(sName);				// JDK 1.7	// @android 26
		//Charset charset = Charset.forName("UTF_8");  // others like "US-ASCII" see  http://www.iana.org/assignments/character-sets
		
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING};
		try ( 
			BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.ISO_8859_1, options);		// JDK 7
		) {
			writer.append(sContent, 0, sContent.length());
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void appendToTextFile7(String sName, String sContent) {
		Path path = Paths.get(sName);				// JDK 1.7	// @android 26
		//Charset charset = Charset.forName("UTF_8");  // others like "US-ASCII" see  http://www.iana.org/assignments/character-sets

		
		OpenOption[] options = {StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.APPEND};
		try ( 
			BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.ISO_8859_1, options);
		) {
			writer.append(sContent, 0, sContent.length());
			writer.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String readCSVFile(String fname, String sDelimeter) {
		String everything = "";
		
		try (
			Scanner s = new Scanner(new BufferedReader(new FileReader(fname)));			// JDK 7
		) 
		{
			StringBuilder sb = new StringBuilder();
			s.useDelimiter(sDelimeter);
			while ( s.hasNext() ) {
				sb.append(s.next());
				sb.append(System.lineSeparator());
			}
			everything = sb.toString();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return(everything);		
	}
	

}
