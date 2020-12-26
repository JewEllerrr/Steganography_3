import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		String originalImagePath = "C:\\Users\\lizfr\\Desktop\\pens.bmp";
		String resultImagePath = "C:\\Users\\lizfr\\Desktop\\pensRes.bmp";
		BufferedImage image = ImageIO.read(new File(originalImagePath));
		KochAlgorithm koch = new KochAlgorithm(image);
		boolean exit = false;
		while (!exit) {
			System.out.println("Выбрать режим:");
			System.out.println("\t1 - Embedding");
			System.out.println("\t2 - Extract");
			System.out.println("\t3 - Lossy compression");
			System.out.println("\t4 - Exit");
			int mode = Integer.parseInt(scanner.nextLine());
			if (mode == 1) {
				System.out.println("Enter a message to insert: ");
				String message = scanner.nextLine();
				BufferedImage processedImage = koch.insertMessage(message);
				ImageIO.write(processedImage, "BMP", new File(resultImagePath));
				System.out.println("PSNR = " + Utils.calculatePSNR(image, processedImage));
			} else if (mode == 2) {
				BufferedImage processedImage = ImageIO.read(new File(resultImagePath));
				int[] extractedBits = koch.extractMessage(processedImage);
				String extractedMessage = Utils.getMessageFromBits(extractedBits);
				System.out.println("Extracted message: " + extractedMessage);
			} else if (mode == 3) {
				System.out.println("Enter a message to insert: ");
				String message = scanner.nextLine();
				BufferedImage processedImage = koch.insertMessage(message);
				BufferedImage restoredImage = Utils.compressBMP(processedImage);
				int[] messageBits = Utils.getBitArray(message);
				int[] extractedBits = koch.extractMessage(restoredImage);
				String extractedMessage = Utils.getMessageFromBits(extractedBits);
				System.out.println("Extracted message: " + extractedMessage);
				System.out.println("Number of wrong bits: " + Utils.getErrorsNumber(messageBits, extractedBits));
				System.out.println("PSNR = " + Utils.calculatePSNR(image, restoredImage));
			} else if (mode == 4) {
				System.out.println("Bye bye :)");
				System.exit(0);
			} else {
				throw new IllegalArgumentException("Unknown mode");
			}
		}
	}
}