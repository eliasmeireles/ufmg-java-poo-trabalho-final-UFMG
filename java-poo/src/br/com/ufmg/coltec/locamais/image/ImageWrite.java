package br.com.ufmg.coltec.locamais.image;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by elias on 11/11/18.
 */
public class ImageWrite {

    /**
     * @Metodo que salva uma imagem em um determinado diretório sendo informado por parametro,
     * esste metodo recebe o a URL onde será salva a imagem, a URL onde está a imagem a ser
     * copiada e o nome do diretório para essa imagem.
     * */
    public static String writeImage(String pathToSave, String filetPath, String folderName) throws IOException {
        File image = new File(filetPath);
        BufferedImage bufferedImage  = ImageIO.read(image);
        File newImage = new File(pathToSave.concat(folderName.toLowerCase().concat(String.valueOf(System.currentTimeMillis()).concat(".jpg"))));
        ImageIO.write(bufferedImage, "jpg", newImage);
        return newImage.getName();
    }
}
