package br.com.ufmg.coltec.locamais.ui.helper;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;

/**
 * Created by elias on 18/11/18.
 */
public class SelectorImage {

    /**
     * @Metodo que abre a tela para seleção de apenas uma imagem por vez e retorna
     * essa mesma imagem como um File.
     * */
    public static File getSingleImage(File baseDirectory) throws IOException {
        JFileChooser chooser = new JFileChooser();

        if (baseDirectory != null) {
            chooser.setCurrentDirectory(baseDirectory);
        }
        FileFilter imageFilter = new FileNameExtensionFilter(
                "Image files", ImageIO.getReaderFileSuffixes());
        chooser.setFileFilter(imageFilter);
        chooser.showOpenDialog(null);
        if (chooser.getSelectedFile() != null) {
            String absolutePath = chooser.getSelectedFile().getAbsolutePath();
            return new File(absolutePath);
        }
        return null;
    }
}
