package br.com.ufmg.coltec.locamais.image;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Created by elias on 10/11/18.
 */
public class ImageScale {

    /**
     * @Metodo que gera um BufferedImage escalado com a altura e a largura da imagem.
     * */
    public static BufferedImage scaleImage(int width, int heigth, BufferedImage img) throws Exception {
        BufferedImage bufferedImage = new BufferedImage(width, heigth, BufferedImage.TRANSLUCENT);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g2d.drawImage(img, 0, 0, width, heigth, null);
        g2d.dispose();
        return bufferedImage;
    }
}
