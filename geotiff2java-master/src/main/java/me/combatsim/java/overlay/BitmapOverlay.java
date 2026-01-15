package me.combatsim.java.overlay;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BitmapOverlay implements Overlay {

    private final BufferedImage image;
    private boolean visible = false;

    public BitmapOverlay(String resourcePath) throws IOException {
        this.image = ImageIO.read(BitmapOverlay.class.getResource(resourcePath));

        if (image == null) {
            throw new IllegalArgumentException("Bitmap not found: " + resourcePath);
        }
    }

    @Override
    public void draw(Graphics2D g) {
        if (!visible) return;
        g.drawImage(image, 0, 0, null);
    }

    @Override
    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }
}
