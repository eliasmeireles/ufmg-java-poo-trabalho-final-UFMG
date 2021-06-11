package br.com.ufmg.coltec.locamais.ui.template.controller;

import br.com.ufmg.coltec.locamais.ui.template.VeiculoImageTemplate;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Observable;

import static br.com.ufmg.coltec.locamais.color.Colors.RED;
import static br.com.ufmg.coltec.locamais.image.ImageScale.scaleImage;

public class VeiculoImageTemplateController extends Observable {

    private String imagePath;
    private VeiculoImageTemplate veiculoImageTemplateView;


    public VeiculoImageTemplateController(String imagePath) {
        this.imagePath = imagePath;
        this.veiculoImageTemplateView = new VeiculoImageTemplate();
        veiculoImageTemplateView.removerImagemPanel.setBackground(RED);
        veiculoImageTemplateView.container.setBackground(RED);
        veiculoImagePiker();
        removerImagem();
    }


    /**
     * @Metodo que gera um ImageIcon e inseri esse icon no background,
     * também informa o nome a imagem seleciona.
     * */
    private void veiculoImagePiker() {
        try {
            File imageFile = new File(this.imagePath);
            ImageIcon imageIcon = new ImageIcon(scaleImage(325, 212, ImageIO.read(imageFile)));//get the image from file chooser and scale it to match JLabel size
            veiculoImageTemplateView.veiculoImage.setIcon(imageIcon);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    /**
     * @Metodo que adiona um MouseListener no removerImagemPanel,
     * quando a ação de click nesse painel, os Observer são notificados
     * passando um paramentro do tipo String que é a URL da imagem a ser
     * removida.
     * */
    private void removerImagem() {
        veiculoImageTemplateView.removerImagemPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                notifyObservers(imagePath);
            }
        });
    }

    /**
     * @Metodo o painel carregado com os dados já preenchidos.
     * */
    public JPanel getVeiculoImageContainer() {
        return veiculoImageTemplateView.veiculoImageContainer;
    }

    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
}
