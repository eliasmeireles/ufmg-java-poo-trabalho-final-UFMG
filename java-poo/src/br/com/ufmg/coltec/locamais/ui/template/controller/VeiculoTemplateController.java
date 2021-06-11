package br.com.ufmg.coltec.locamais.ui.template.controller;

import br.com.ufmg.coltec.locamais.model.Veiculo;
import br.com.ufmg.coltec.locamais.ui.template.VeiculoTemplate;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Observable;
import java.util.Observer;

import static br.com.ufmg.coltec.locamais.color.Colors.*;
import static br.com.ufmg.coltec.locamais.color.Colors.RED;
import static br.com.ufmg.coltec.locamais.converter.Converter.doubleToRealString;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.VEICULOS_IMAGE_DIRECTORY;
import static br.com.ufmg.coltec.locamais.image.ImageScale.scaleImage;
import static br.com.ufmg.coltec.locamais.ui.helper.VeiculoSituacao.DISPONIVEL;

public class VeiculoTemplateController extends Observable {

    private VeiculoTemplate veiculoTemplateView;
    private Veiculo veiculo;
    private int currenteImage;

    public VeiculoTemplateController(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.veiculoTemplateView = new VeiculoTemplate();
        this.currenteImage = 0;
        veiculoTemplateView.veiculoInfoContainer.setBackground(GREEN);
        veiculoTemplateView.veiculoInfo.setBackground(GREEN);

        notificaObservers();
        setVeiculoValues();
        nextImage();
    }

    /**
     * @Metodo que notifica seus obeservadores que esse painel foi
     * selecionado e retornando esse mesmo painel por parametro.
     * */
    private void notificaObservers() {
        veiculoTemplateView.veiculoInfoContainer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                notifyObservers(veiculo);
            }
        });
    }

    /**
     * @Metodo que altera a imagem atual para a proxíma imagem quando
     * haver um click sobre essa imagem.
     * */
    private void nextImage() {
        veiculoTemplateView.imageNextPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                setNextImage();
            }
        });
    }


    /**
     * @Metodo que analiza a proxíma imagem localizada dentro da lista
     * de imagens, caso a imagem atual sejá a ultima da lista, estão a
     * proxima imagens a ser mostrada será a primeira da lista.
     * */
    private void setNextImage() {
        if (currenteImage < veiculo.getFotos().size() - 1) {
            currenteImage++;
        } else currenteImage = 0;
        setImage();
    }


    /**
     * @Metodo que seta na tela todas as informações do veículo.
     * */
    private void setVeiculoValues() {
        veiculoTemplateView.veiculoAnoFabricacao.setText("Fab: ".concat(this.veiculo.getAnoFabricacao()));
        veiculoTemplateView.veiculoKilometragem.setText("Km: ".concat(String.valueOf(this.veiculo.getKilometragem())));
        veiculoTemplateView.veiculoNomeEFabicante.setText(this.veiculo.getFabricante().getNome().concat(" ").concat(this.veiculo.getNome()));
        veiculoTemplateView.veiculoPlaca.setText("Placa: ".concat(this.veiculo.getPlaca()));
        veiculoTemplateView.veiculoCor.setText(this.veiculo.getCor());
        veiculoTemplateView.veiculoValorPorKMInfo.setText(doubleToRealString(this.veiculo.getPrecoPor100Km()).concat(" a cada 100KM"));
        veiculoTemplateView.veiculoSituacao.setText(this.veiculo.getSituacao());
        setBackgroundStatus();
        setImage();
    }

    /**
     * @Metodo que seta a cor do background das informações do veículo
     * de acordo com o seu estado, sendo verde para diponivél e vermelho
     * para indisponivél.
     * */
    private void setBackgroundStatus() {
        if (this.veiculo.getSituacao().equals(DISPONIVEL)) {
            veiculoTemplateView.imageNextPanel.setBackground(TEAL);
        } else {
            veiculoTemplateView.imageNextPanel.setBackground(RED);
            veiculoTemplateView.veiculoInfo.setBackground(RED);
            veiculoTemplateView.veiculoInfoContainer.setBackground(RED);
        }
    }

    /**
     * @Metodo que gera um ImageIcon através da url da imagem passada pelo paramentro,
     * */
    private void setImage() {
        ImageIcon imageIcon = null;//get the image from file chooser and scale it to match JLabel size
        try {
            String pathname = VEICULOS_IMAGE_DIRECTORY.concat(this.veiculo.getFotos().get(currenteImage));
            File imageFile = new File(pathname);
            imageIcon = new ImageIcon(scaleImage(359, 212, ImageIO.read(imageFile)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        veiculoTemplateView.veiculoImage.setIcon(imageIcon);
    }

    /**
     * @Metodo que retorna o painel já com todas as informções já carregadas.
     * */
    public JPanel getMainPanel() {
        return veiculoTemplateView.mainPanel;
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }
}
