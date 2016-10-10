package squidpony.ui;

import com.badlogic.gdx.graphics.Pixmap;
import java.awt.image.BufferedImage;

/**
 *
 * @author Eben Howard
 */
public class ImageConverter {

    public static Pixmap convertBufferedImage(BufferedImage image) {
        Pixmap output = new Pixmap(image.getWidth(), image.getHeight(), Pixmap.Format.RGBA8888);

        for (int i = 0; i < image.getWidth(); i++) {
            for (int j = 0; j < image.getHeight(); j++) {
                output.drawPixel(i, j, image.getRGB(i, j));
            }
        }

        return output;
    }
}
