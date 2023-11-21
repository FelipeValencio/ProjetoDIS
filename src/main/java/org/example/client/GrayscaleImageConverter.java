package org.example.client;
import javax.imageio.ImageIO;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.*;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class GrayscaleImageConverter {
    public GrayscaleImageConverter(List<Double> data, int size) {
        this.data = data;
        this.size = size;
    }
    List<Double> data;
    int size;

    public void saveImage(String name) {
        BufferedImage grayscaleImage = convertToGrayscaleImage();

        BufferedImage rotatedImage = rotateImage90Degrees(grayscaleImage);

        saveGrayscaleImage(rotatedImage, name);
    }

    public BufferedImage convertToGrayscaleImage() {
        this.size =  (int) Math.sqrt(this.size);
        BufferedImage image = new BufferedImage(this.size, this.size, BufferedImage.TYPE_BYTE_GRAY);
        WritableRaster raster = image.getRaster();

        // Find the minimum and maximum values in the data
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (Double value : data) {
            if (value < min) min = value;
            if (value > max) max = value;
        }

        // Normalize and set the grayscale pixel values
        for (int y = 0; y < this.size; y++) {
            for (int x = 0; x < this.size; x++) {
                int index = y * this.size + x;
                double value = data.get(index);
                int grayValue = (int) (255 * (value - min) / (max - min));

                // Set the grayscale pixel value
                raster.setSample(x, y, 0, grayValue);
            }
        }

        return image;
    }

    public static BufferedImage rotateImage90Degrees(BufferedImage originalImage) {
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();

        BufferedImage rotatedImage = new BufferedImage(originalWidth, originalHeight, originalImage.getType());
        Graphics2D g2d = rotatedImage.createGraphics();

        AffineTransform at = new AffineTransform();
        at.translate(0, originalWidth);
        at.rotate(-Math.PI / 2);

        g2d.setTransform(at);
        g2d.drawImage(originalImage, 0, 0, null);
        g2d.dispose();

        return rotatedImage;
    }

    private void saveGrayscaleImage(BufferedImage image, String name) {
        try {
            String nomeArquivo = "results/"+name + "/grayscale_image_" + name + ".png";
            ImageIO.write(image, "png", new File(nomeArquivo));
            System.out.println("Image saved as " + nomeArquivo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
