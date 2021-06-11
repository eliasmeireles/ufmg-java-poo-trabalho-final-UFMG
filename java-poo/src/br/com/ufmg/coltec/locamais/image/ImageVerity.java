package br.com.ufmg.coltec.locamais.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

/**
 * Created by elias on 10/11/18.
 */
public class ImageVerity {

    /**
     * @Metodo para avaliar se o arquivo selecionado é ou não
     * uma imagem.
     * */
    public static boolean isImage(String imagePath) {
        try {
            Image image = ImageIO.read(new File(imagePath));
            return image != null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
