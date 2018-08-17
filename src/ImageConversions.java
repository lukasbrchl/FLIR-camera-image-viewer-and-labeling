import java.nio.ByteBuffer;
import java.util.Arrays;

import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

public class ImageConversions {
	
	public static Image convertBinaryToImage(byte[] byteArray, int imageWidth, int imageHeight) {
		float min = bytesToCelsius(getMin(byteArray));
		float max = bytesToCelsius(getMax(byteArray));
		return convertBinaryToImage(byteArray, imageWidth, imageHeight, min, max);
	}
	
	public static Image convertBinaryToImage(byte[] byteArray, int imageWidth, int imageHeight, float min, float max) {
	    WritableImage image = new WritableImage(imageWidth, imageHeight);
	    PixelFormat<ByteBuffer> pixelFormat = PixelFormat.getByteRgbInstance();
	    byte [] pixels = new byte[imageWidth * imageHeight * 3]; // * 3 because Image needs 3 bytes per pixel even if grayscale

	    for (int i = 0, cnt = 0; i < byteArray.length; i += 2, cnt += 3) {
	    	float value = normalizeToByte(bytesToCelsius(readTwoBytes(byteArray, i)), min, max);
	    	pixels[cnt] = (byte) value;
	    	pixels[cnt + 1] = (byte) value;
	    	pixels[cnt + 2] = (byte) value;
	    }	    
	    image.getPixelWriter().setPixels(0, 0, imageWidth, imageHeight, pixelFormat, pixels, 0, imageWidth * 3);
	    return image;
	}
	
	 //helper methods
	public static float bytesToCelsius(int value) {
		return ((float) value * 0.04f) - 273.15f; // * 0.04 FLIR Ax5 constant + kelvin to celsius
	}

	public static int unsignedToSigned(byte a) {
		return a & 0xFF;
	}
	
	public static float normalizeToByte(float value, float min, float max) {
		float newMin = 0.0f, newMax = 255.0f;
		return normalize(value, min, max, newMin, newMax);
	}

	public static float normalize(float value, float min, float max, float newMin, float newMax) {
		if (value > max) value = max;
		if (value < min) value = min;
		float normalized = (newMax - newMin) / (max - min) * (value - max) + newMax;
		return normalized;
	}
	
	//TODO: inefficient
	public static int getMax(byte[] inputArray) {
		int maxValue = 0;
		for (int i = 0; i < inputArray.length; i += 2) {
			int value = readTwoBytes(inputArray, i);
			if (value > maxValue) maxValue = value;
		}
		return maxValue;
	}

	public static int getMin(byte[] inputArray) {
		int minValue = Integer.MAX_VALUE;
		for (int i = 0; i < inputArray.length; i += 2) {
			int value = readTwoBytes(inputArray, i);
			if (value < minValue) minValue = value;
		}
		return minValue;
	}
	
	public static int getAvg(byte [] inputArray) {
		int counter = 0;
		int sum = 0;
		for (int i = 0; i < inputArray.length; i += 2) {
			sum += readTwoBytes(inputArray, i);
			++counter;
		}
		return sum/counter;
	}
	
	public static int getMedian(byte[] inputArray) {
		int[] convertedArray = new int[inputArray.length/2];
		int cnt = 0;
		for (int i = 0; i < inputArray.length; i += 2) {
			convertedArray[cnt++] = readTwoBytes(inputArray, i);
		}
		Arrays.sort(convertedArray);
		int middle = convertedArray.length/2;
		int medianValue = 0; //declare variable 
		if (convertedArray.length%2 == 1) 
		    medianValue = convertedArray[middle];
		else
		   medianValue = (convertedArray[middle-1] + convertedArray[middle]) / 2;
		return medianValue;
	}

	public static int readTwoBytes(byte[] inputArray, int index) {
		int firstByte = unsignedToSigned(inputArray[index]);
		int secondByte = unsignedToSigned(inputArray[index + 1]);
		return (secondByte << 8) | firstByte;
	}
	
	
}
