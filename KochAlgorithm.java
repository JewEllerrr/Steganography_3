import java.awt.image.BufferedImage;
import java.util.ArrayList;


public class KochAlgorithm {

	private double e = 55;
	private int n = 8;
	private int CONST = 125;
	private int messageLength = 0;
	private BufferedImage image;
	
	private ArrayList<int[][]> coefIndex = new ArrayList<int[][]>();
	private int coef1_x = 3;
	private int coef1_y = 5;
	private int coef2_x = 5;
	private int coef2_y = 3;

	public KochAlgorithm(BufferedImage image) {
		this.image = image;
	}

	public BufferedImage insertMessage(String message) {
		System.out.println("Embedding");
		int containerCapacity = image.getWidth()*image.getHeight() / (8*8);
		System.out.println("Container capacity = " + containerCapacity + " bits");
		int[] bits = Utils.getBitArray(message);
		messageLength = bits.length;
		System.out.println("Message contains = " + messageLength + " bits");
		if (messageLength > containerCapacity)
			System.out.println("Message bigger than capacity, message will be cut");
		
		BufferedImage stegoImage = image;
		int bitIndex = 0;
		for (int i = 0; i < image.getHeight(); i += 8) {
			for (int j = 0; j < image.getWidth(); j += 8) {
				if (bitIndex < messageLength && bitIndex < containerCapacity) {
					BufferedImage subImage = image.getSubimage(j, i, n, n);
					int[][] red = Utils.getComponent(subImage, "red");
					int[][] green = Utils.getComponent(subImage, "green");
					int[][] blue = Utils.getComponent(subImage, "blue");
					int[][] dct = Utils.directDiscreteCosineTransform(red);

						// get random coords
//					 int coef1_x = (int) (Math.random() * dct[0].length);
//					 int coef1_y = (int) (Math.random() * dct.length);
//					 int coef2_x = (int) (Math.random() * dct[0].length);
//					 int coef2_y = (int) (Math.random() * dct.length);
//					 int[][] coordinatesCoef = new int[][] { { coef1_x, coef1_y }, { coef2_x,
//					 coef2_y } };
//					 coefIndex.add(coordinatesCoef);

					// embedding
					double diff = Math.abs(dct[coef1_x][coef1_y]) - Math.abs(dct[coef2_x][coef2_y]);
					if (bits[bitIndex] == 0) {
						
						if (diff < e)
							dct[coef1_x][coef1_y] = Math.abs(dct[coef2_x][coef2_y]) + CONST;
			
//						while (diff < e) {
//							dct[coef1_x][coef1_y] = Math.abs(dct[coef1_x][coef1_y]) + 1;
//							dct[coef2_x][coef2_y] = Math.abs(dct[coef2_x][coef2_y]) - 1;
//							diff = Math.abs(dct[coef1_x][coef1_y]) - Math.abs(dct[coef2_x][coef2_y]);
//						}
						
					} else {
						
						if (diff < -e)
							dct[coef2_x][coef2_y] = Math.abs(dct[coef1_x][coef1_y]) + CONST;

//						while (diff < -e) {
//							dct[coef1_x][coef1_y] = Math.abs(dct[coef1_x][coef1_y]) - 1;
//							dct[coef2_x][coef2_y] = Math.abs(dct[coef2_x][coef2_y]) + 1;
//							diff = Math.abs(dct[coef1_x][coef1_y]) - Math.abs(dct[coef2_x][coef2_y]);
//						}
					}

					// inverse DCT
					int[][] idctRed = Utils.inverseDiscreteCosineTransform(dct);
					int[][] rgb = Utils.combineComponents(idctRed, green, blue);
					Utils.setSubImage(stegoImage, rgb, j, i);
					bitIndex++;

				}
			}
		}
		System.out.println("Embedded " + bitIndex + " bits");

		return stegoImage;
	}

	public int[] extractMessage(BufferedImage processed) {
		System.out.println("Extraction");
		int index = 0;
		int[] bits = new int[messageLength];
		for (int i = 0; i < image.getHeight(); i += 8) {
			for (int j = 0; j < image.getWidth(); j += 8) {
				if (index < messageLength) {
					BufferedImage pSubImage = processed.getSubimage(j, i, n, n);
					int[][] red = Utils.getComponent(pSubImage, "red");
					int[][] dct = Utils.directDiscreteCosineTransform(red);
//					int[][] tmpCoefIndex = coefIndex.get(index);
//					double diff = dct[tmpCoefIndex[0][0]][tmpCoefIndex[0][1]] -
//					dct[tmpCoefIndex[1][0]][tmpCoefIndex[1][1]];
					double diff = dct[coef1_x][coef1_y] - dct[coef2_x][coef2_y];
					if (diff > e)
						bits[index] = 0;
					else
						bits[index] = 1;

					index++;
				}
			}
		}
		return bits;
	}

}