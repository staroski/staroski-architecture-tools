package br.com.staroski.utils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

/**
 * Utility class to manipulate images.
 *
 * @author Staroski, Ricardo Artur
 */
public final class CG {

    public static Color add(Color color, int r, int g, int b) {
        int red = color.getRed() + r;
        int green = color.getGreen() + g;
        int blue = color.getBlue() + b;

        red = Math.min(255, Math.max(0, red));
        green = Math.min(255, Math.max(0, green));
        blue = Math.min(255, Math.max(0, blue));

        return new Color(red, green, blue);
    }

    public static BufferedImage colorify(BufferedImage image, Color color) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage colorizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int gray = image.getRGB(x, y) & 0xFF;

                int alpha = image.getRGB(x, y) >> 24 & 0xFF;
                int red = color.getRed() * gray / 255;
                int green = color.getGreen() * gray / 255;
                int blue = color.getBlue() * gray / 255;

                int rgba = (alpha << 24) | (red << 16) | (green << 8) | blue;
                colorizedImage.setRGB(x, y, rgba);
            }
        }

        return colorizedImage;
    }

    public static BufferedImage read(String resource) {
        try {
            InputStream input = CG.class.getResourceAsStream(resource);
            return ImageIO.read(input);
        } catch (IOException e) {
            throw new RuntimeException("Could not read \"" + resource + "\"", e);
        }
    }

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
        g.dispose();
        return resizedImage;
    }

    private CG() {}
}
