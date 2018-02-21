/*
 * author:Melvin James
 * net id: mxj162130
 * created: 4/9/17
 */
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

public class Main {
	public static void main(String[] args) {
		
		if (args.length < 3){
		    System.out.println("Usage: Kmeans <input-image> <k> <output-image>");
		    return;
		}
		try{
		    BufferedImage originalImage = ImageIO.read(new File(args[0]));
		    int k=Integer.parseInt(args[1]);
		    BufferedImage kmeansJpg = kmeans_helper(originalImage,k);
		    ImageIO.write(kmeansJpg, "jpg", new File(args[2])); 
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private static BufferedImage kmeans_helper(BufferedImage originalImage, int k) {
		int w = originalImage.getWidth();
		int h = originalImage.getHeight();

		BufferedImage kmeansImage = new BufferedImage(w, h, originalImage.getType());
		Graphics2D g = kmeansImage.createGraphics();
		g.drawImage(originalImage, 0, 0, w, h, null);
		// Read rgb values from the image
		int[] rgb = new int[w*h];
		int count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				rgb[count++] = kmeansImage.getRGB(i, j);
			}
		}
		// Call kmeans algorithm: update the rgb values
		kmeans(rgb, k);

		// Write the new rgb values to the image
		count = 0;
		for (int i = 0; i < w; i++) {
			for (int j = 0; j < h; j++) {
				kmeansImage.setRGB(i, j, rgb[count++]);
			}
		}
		return kmeansImage;
	}

	// k-means
	private static void kmeans(int[] rgb, int k) {

		int[] k_array = new int[k];
		Random rand = new Random();
		for (int i = 0; i < k_array.length; i++) {
			int r_no;
			boolean similar = true;
			if (i == 0) {
				r_no = rand.nextInt(rgb.length);
				k_array[i] = rgb[r_no];
			} else {
				do {
					r_no = rand.nextInt(rgb.length);
					for (int j = 0; j < i; j++) {
						if (j == i - 1 && k_array[j] != rgb[r_no]) {
							k_array[i] = rgb[r_no];
							similar = false;
						} else if (k_array[j] == rgb[r_no]) 
							j = i;
					}
				} while (similar);
			}
		}

		int iterations = 1;
		int[] p = new int[rgb.length];
		while (iterations <= 50) {

			for (int i = 0; i < rgb.length; i++) {
				double min_dist = Double.MAX_VALUE;
				int c_index = 0;
				
				for (int j = 0; j < k_array.length; j++) {	
					int red = ((rgb[i]>> 16) & 0xFF) - ((k_array[j]>> 16) & 0xFF);
					int green = ((rgb[i]>> 8) & 0xFF) - ((k_array[j]>> 8) & 0xFF);
					int blue = (rgb[i] & 0xFF) - (k_array[j] & 0xFF);
					double dist = Math.sqrt(red * red + green * green + blue * blue);
					if (dist < min_dist) {
						min_dist = dist;
						c_index = j;
					}
				}

				p[i] = c_index;
				iterations++;
			}
			for (int i = 0; i < rgb.length; i++) {
				rgb[i] = k_array[p[i]];
			}
		}
	}

}