import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Scanner;

import javax.imageio.*;
import javax.swing.*;


public class Main {
	
	public static BufferedImage readFile(File image) {
		BufferedImage img = null;
		try {
			img = ImageIO.read(image);
		} catch (IOException e) {
			System.out.println("Error reading file: " + image);
		}
		return img;
	}

	private static Color iterateThroughImage(BufferedImage image) {
		int w = image.getWidth();
		int h = image.getHeight();
		//System.out.println("width, height: " + w + ", " + h);
		int r = 0;
		int b = 0;
		int g = 0;

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				int pixel = image.getRGB(j, i);
				int alpha = (pixel >> 24) & 0xff;
				int red = (pixel >> 16) & 0xff;
				int green = (pixel >> 8) & 0xff;
				int blue = (pixel) & 0xff;
				if (i == 0 && j == 0) { //we store the first rgb, and from then on add to the current total 
					r = red;
					b = blue;
					g = green;
				}
				else {
					r += red;
					b += blue;
					g += green;
				}
				//System.out.println("x,y: " + j + ", " + i);
				//System.out.println("");
			}
		}
		return new Color(r/(h*w), g/(h*w), b/(h*w)); //return the total divided by the number of pixels -> average
	}

	public static void moveFile(boolean dayNight, File source, String directoryPath) {
		File destination = dayNight ? new File(directoryPath + "\\Day\\" +source.getName()) : new File(directoryPath + "\\Night\\" +source.getName());
		InputStream inStream = null;
		OutputStream outStream = null;
		try {
			inStream = new FileInputStream(source); 
			outStream = new FileOutputStream(destination);
			byte[] buffer = new byte[1024];
    	    int length;
    	    //copy the file content in bytes 
    	    while ((length = inStream.read(buffer)) > 0){
 
    	    	outStream.write(buffer, 0, length);
 
    	    }
    	    inStream.close();
    	    outStream.close();
 
    	    //delete the original file
    	    source.delete();
			
			inStream.close();
			outStream.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}
	
	public static void main(String[] args) {
		System.out.println("Enter the name of the folderpath");
		Scanner input = new Scanner(System.in);
		String directoryPath = input.nextLine();
		File dir = new File(directoryPath);
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) { //assures we have a directory 
			File day = new File(directoryPath + "\\Day\\"); //automatically tries to make a day and night folder
			day.mkdir();
			File night = new File(directoryPath + "\\Night\\");
			night.mkdir();
			System.out.println("Processing...");
			for (File child : directoryListing) {
				String childName = child.getName();
				if (childName.length() > 4) { //checks that we are only looking at files, not folders 
					String fileType = childName.substring(childName.length()-4, childName.length());
					if (fileType.equals(".jpg") || fileType.equals(".png")) { //end tag must be jpg or png
						BufferedImage image = readFile(child); //converts file to bufferedimage

						Color avg = iterateThroughImage(image); //returns an average color of all pixels in the image
						double perceivedBrightness = 0.299*avg.getRed() + 0.587*avg.getGreen() + 0.114*avg.getBlue();
						//System.out.println(child.getName() + " " + perceivedBrightness); 
						if (perceivedBrightness >= 100) { //daytime
							moveFile(true, child, directoryPath);				
						}
						else { //nighttime
							moveFile(false, child, directoryPath);			
						}
					}
				}
			}
			System.out.println("Files copied over successfully!");
		} else {
			System.out.println("Directory not recognized");
		}
	}

}
