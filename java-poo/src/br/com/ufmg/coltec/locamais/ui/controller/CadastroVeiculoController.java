package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.Fabricante;
import br.com.ufmg.coltec.locamais.model.Veiculo;
import br.com.ufmg.coltec.locamais.ui.inputlimit.JTextFieldLimit;
import br.com.ufmg.coltec.locamais.ui.template.controller.VeiculoImageTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.CadastroVeiculo;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.List;

import static br.com.ufmg.coltec.locamais.filemanager.FabricanteFileManager.getFabricantesFromJsonFile;
import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.createFolder;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.VEICULOS_DIRECTORY;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.VEICULOS_IMAGE_DIRECTORY;
import static br.com.ufmg.coltec.locamais.filemanager.VeiculosFileManager.getVeiculos;
import static br.com.ufmg.coltec.locamais.filemanager.VeiculosFileManager.savarVeiculo;
import static br.com.ufmg.coltec.locamais.image.ImageVerity.isImage;
import static br.com.ufmg.coltec.locamais.image.ImageWrite.writeImage;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.VEICULO_FORMULARIO_CADASTRO;
import static br.com.ufmg.coltec.locamais.ui.helper.VeiculoSituacao.DISPONIVEL;
import static br.com.ufmg.coltec.locamais.ui.helper.VeiculoSituacao.INDISPONIVEL;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_DATA_STRING;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_PLACA_VEICULO;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.*;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.inputIsValid;
import static br.com.ufmg.coltec.locamais.ui.message.ShowMessage.messageShow;

public class CadastroVeiculoController implements Observer {

    private UIMainController uiMainController;
    private CadastroVeiculo cadastroVeiculoView;
    private Veiculo veiculo;
    private List<Veiculo> veiculos;
    private List<String> imagensURL;
    private File[] imagesfile;
    private int quantiDeImagensRequiridas;

    public CadastroVeiculoController(UIMainController uiMainController) {
        this.uiMainController = uiMainController;
        this.uiMainController.addObserver(this);
        this.cadastroVeiculoView = new CadastroVeiculo();
        cadastroVeiculoView.cadastrarButton.setEnabled(false);
        this.veiculos = getVeiculos();
        setFabricantesOnComboBox();
        setSituacoesOnComboBox();
        imageSelect();
        veiculoImagePiker();
        efetuarCadastro();
        setMask();
        setLimit();
        setInputValidation();
        validaPlaca();
    }


    private void imageSelect() {
        this.veiculo = new Veiculo();
        this.imagensURL = new ArrayList<>();
        this.quantiDeImagensRequiridas = 4;
        cadastroVeiculoView.imagensRequerimentoInfo.setText("Informe no m??nimo ".concat(String.valueOf(this.quantiDeImagensRequiridas)).concat(" imagens para o ve??culo, maximo de 16."));
    }

    /**
     * @Metodo para setar as op????es de estado de um ve??culo em um
     * combobox na tela.
     */
    private void setSituacoesOnComboBox() {
        cadastroVeiculoView.veiculoSituacaoComboBox.addItem(DISPONIVEL);
        cadastroVeiculoView.veiculoSituacaoComboBox.addItem(INDISPONIVEL);
    }

    /**
     * @Metodo para configurar a m??scara do campo de data para o formato
     * de data Brasileiro do formul??rio de cadastro de ve??culo.
     */
    private void setMask() {
        try {
            cadastroVeiculoView.veiculoAnoFabricacao = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("##/##/####")));
            cadastroVeiculoView.veiculoAnoFabricacao.setFont(cadastroVeiculoView.veiculoNome.getFont());
            cadastroVeiculoView.veiculoAnoFabricacaoPanel.setLayout(new GridLayout());
            cadastroVeiculoView.veiculoAnoFabricacaoPanel.add(cadastroVeiculoView.veiculoAnoFabricacao);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Metodo para validar os campos de entrada de informa????es sobre o
     * novo ve??culo a ser cadastrdo no sistema apos a perda do seu foco, esse metodo torna ent??o
     * que todos do campos do formul??iro s??o obrigat??rios.
     */
    private void setInputValidation() {
        String message = "Campo abrigat??rio!";
        validacaoTextField(cadastroVeiculoView.veiculoNome, cadastroVeiculoView.veiculoNomeValidation, message);
        validacaoTextField(cadastroVeiculoView.veiculoPlaca, cadastroVeiculoView.veiculoPlacaValidation, message);
        validacaoTextField(cadastroVeiculoView.veiculoValorPorKilometragem, cadastroVeiculoView.veiculoPrecoPorKilometragemValidation, message);
        validacaoTextField(cadastroVeiculoView.veiculoCor, cadastroVeiculoView.veiculoCorValidation, message);
        validacaoTextField(cadastroVeiculoView.veiculoKilometragem, cadastroVeiculoView.veiculoKilometragemValidation, message);
        inputValidationMatches(cadastroVeiculoView.veiculoAnoFabricacao, cadastroVeiculoView.veiculoAnoFabricacaoValidation, REGEX_DATA_STRING, "Data inv??lida!");
        inputValidationMatches(cadastroVeiculoView.veiculoPlaca, cadastroVeiculoView.veiculoPlacaValidation, REGEX_PLACA_VEICULO, "Placa inv??lida");
        cadastroVeiculoView.veiculoFabricanteComboBox.addActionListener(al -> isValidFabricante());
    }


    /**
     * @Metodo para validar os campos de entrada de informa????es sobre o
     * novo ve??culo a ser cadastrdo no sistema, esse metodo torna ent??o
     * que todos do campos do formul??iro s??o obrigat??rios, caso os dados
     * informados n??o s??o validos, n??o ser?? poss??vel efetuar o cadastro
     * do ve??culo.
     */
    public boolean isValid() {
        String message = "Campo abrigat??rio!";
        try {
            return isValidFabricante()
                    && inputIsValid(cadastroVeiculoView.veiculoNome, cadastroVeiculoView.veiculoNomeValidation, message, null)
                    && inputIsValid(cadastroVeiculoView.veiculoPlaca, cadastroVeiculoView.veiculoPlacaValidation, message, null)
                    && inputIsValid(cadastroVeiculoView.veiculoValorPorKilometragem, cadastroVeiculoView.veiculoPrecoPorKilometragemValidation, message, null)
                    && inputIsValid(cadastroVeiculoView.veiculoCor, cadastroVeiculoView.veiculoCorValidation, message, null)
                    && inputIsValid(cadastroVeiculoView.veiculoKilometragem, cadastroVeiculoView.veiculoKilometragemValidation, message, null);
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * @Metodo que seta um limite para o campo de entrada da placa do ve??culo,
     * por padr??o as placas de ve??culos Brasileiros tem um total de 7 caracters,
     * sendo 3 letras e 4 n??meros.
     */
    private void setLimit() {
        cadastroVeiculoView.veiculoPlaca.setDocument(new JTextFieldLimit(7));
    }


    /**
     * @Metodo para efetuar o cadastro do ve??culo,
     * esse metodo adiciona o ActionListerner ao bot??o
     * de efetuar o cadastro do ve??culo caso todos os dados
     * informados pelo usu??rio do sistem estejam corretos.
     */
    private void efetuarCadastro() {
        cadastroVeiculoView.cadastrarButton.addActionListener(al -> {
            this.veiculos = getVeiculos();
            if (isValid() && placaDisponivel()) {
                this.veiculos.add(this.veiculo);
                ArrayList<String> imagesURLSaved = new ArrayList<>();
                veiculoSetValues();
                String veiculoFolder = this.veiculo.getNome().concat(this.veiculo.getPlaca());
                imagensURL.forEach(imageURL -> {
                    try {
                        createDirectorys(veiculoFolder);
                        imagesURLSaved.add(veiculoFolder.concat(File.separator).concat(writeImage(VEICULOS_IMAGE_DIRECTORY.concat(veiculoFolder).concat(File.separator), imageURL, veiculoFolder)));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                try {
                    this.veiculo.setFotos(imagesURLSaved);
                    savarVeiculo(this.veiculos);
                    messageShow(uiMainController.getMainFrame(), "Ve??culo cadastrado com sucesso.");
                    this.veiculo = new Veiculo();
                    this.imagensURL = new ArrayList<>();
                    cadastroVeiculoView.veiculoPlaca.setText("");
                    setVeiculoImages();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * @Metodo que avalia o campo de entrada da placa do ve??culo
     * logo ap??s esse mesmo campo tenha a perda o foco.
     */
    private void validaPlaca() {
        cadastroVeiculoView.veiculoPlaca.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                super.focusLost(e);
                placaDisponivel();
            }
        });
    }

    /**
     * @Metodo que percorre a lista de ve??culos cadastrados no sistema
     * para avaliar se a placa informada no campo de entrada dos dados
     * da placa do ve??culo j?? est?? em uso.
     */
    private boolean placaDisponivel() {
        boolean[] placaDisponivel = {true};
        this.veiculos.forEach(consumer -> {
            if (consumer.getPlaca().equals(cadastroVeiculoView.veiculoPlaca.getText())) {
                cadastroVeiculoView.veiculoPlacaValidation.setText("A placa informada j?? est?? em uso!");
                placaDisponivel[0] = false;
                return;
            }
        });
        return placaDisponivel[0];
    }

    /**
     * @Metodo para criar os seguinte diretorios caso n??o
     * estejam presentes.
     */
    private void createDirectorys(String veiculoFolder) {
        createFolder(VEICULOS_DIRECTORY);
        createFolder(VEICULOS_IMAGE_DIRECTORY);
        createFolder(VEICULOS_IMAGE_DIRECTORY.concat(veiculoFolder));
    }


    /**
     * @Metodo que cria um objeto do tipo ve??culo setando os valores
     * se seus atributos com valores informados pelo usu??rio.
     */
    private void veiculoSetValues() {
        this.veiculo.setNome(cadastroVeiculoView.veiculoNome.getText());
        this.veiculo.setPlaca(cadastroVeiculoView.veiculoPlaca.getText());
        this.veiculo.setKilometragem(Integer.parseInt(cadastroVeiculoView.veiculoKilometragem.getText()));
        this.veiculo.setAnoFabricacao(cadastroVeiculoView.veiculoAnoFabricacao.getText());
        this.veiculo.setFabricante(new Fabricante(cadastroVeiculoView.veiculoFabricanteComboBox.getSelectedItem().toString()));
        this.veiculo.setCor(cadastroVeiculoView.veiculoCor.getText());
        this.veiculo.setPrecoPor100Km(Double.parseDouble(cadastroVeiculoView.veiculoValorPorKilometragem.getText()));
        this.veiculo.setSituacao(cadastroVeiculoView.veiculoSituacaoComboBox.getSelectedItem().toString());
    }

    /**
     * @Metodo para setar no combobox do formul??rio de cadastro do  ve??culo,
     * os nomes dos fabricantes j?? cadastrados no sistema.
     */
    private void setFabricantesOnComboBox() {
        List<Fabricante> fabricantesFromJsonFile = getFabricantesFromJsonFile();
        cadastroVeiculoView.veiculoFabricanteComboBox.addItem("Selecione um fabricante");
        Collections.sort(fabricantesFromJsonFile);
        fabricantesFromJsonFile.forEach(fabricante -> cadastroVeiculoView.veiculoFabricanteComboBox.addItem(fabricante.getNome()));
    }

    /**
     * @Metodo para efetuar a valida????o do campo de fabricante,
     * , o primeiro item da lista ?? apenas um informativo e para
     * efetuar a valida????o, obrigando ao usu??rio a escolher um fabricante.
     */
    private boolean isValidFabricante() {
        if (cadastroVeiculoView.veiculoFabricanteComboBox.getSelectedIndex() > 0) {
            cadastroVeiculoView.veiculoFabricanteValidation.setText("");
            return true;
        }
        cadastroVeiculoView.veiculoFabricanteComboBox.requestFocus();
        cadastroVeiculoView.veiculoFabricanteValidation.setText("?? preciso selecionar um fabricante!");
        return false;
    }

    /**
     * @Metodo que adiciona um ActionListener ao bot??o seleccionarImageButtom.
     */
    private void veiculoImagePiker() {
        cadastroVeiculoView.selecionarImagemButton.addActionListener(al -> getFilePath());
    }


    /**
     * @Metodo que abre um tela onde o usu??rio dever?? escolher as fotos
     * para o ve??culo.
     */
    private void getFilePath() {
        try {
            JFileChooser chooser = new JFileChooser();
            chooser.setMultiSelectionEnabled(true);
            if (imagesfile != null) {
                chooser.setCurrentDirectory(imagesfile[0]);
            }
            FileFilter imageFilter = new FileNameExtensionFilter(
                    "Image files", ImageIO.getReaderFileSuffixes());
            chooser.setFileFilter(imageFilter);
            chooser.showOpenDialog(null);
            this.imagesfile = chooser.getSelectedFiles();
            if (chooser.getSelectedFile() != null) {
                trySetVeiculoImage();
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageShow(uiMainController.getMainFrame(), "Erro inesperado, tente novamente!");
        }
    }


    /**
     * @Metodo que percorre a lista de imagens selecionadas pelo usus??rio
     * e adicionala a lista de imagens "imagensURL" caso essa imagem ainda
     * n??o esteja na lista.
     */
    private void trySetVeiculoImage() {
        for (int i = 0; i < this.imagesfile.length; i++) {
            File image = this.imagesfile[i];
            if (isImage(image.getAbsolutePath())) {
                if (!this.imagensURL.contains(image.getAbsolutePath())) {
                    this.imagensURL.add(image.getAbsolutePath());
                    setVeiculoImages();
                }
            }
            if (i >= 16) break;
        }

    }


    /**
     * @Metodo que limita a quantia de imagem, sendo um limite de 16
     * imagens.
     */
    private void blockOnLimit() {
        if (this.imagensURL.size() >= quantiDeImagensRequiridas) {
            if (this.imagensURL.size() >= 16) {
                cadastroVeiculoView.veiculoImageChooserPanel.setVisible(false);
            } else {
                cadastroVeiculoView.veiculoImageChooserPanel.setVisible(true);
            }
            cadastroVeiculoView.cadastrarButton.setEnabled(true);
        } else {
            cadastroVeiculoView.veiculoImageChooserPanel.setVisible(true);
            cadastroVeiculoView.cadastrarButton.setEnabled(false);
        }
    }


    /**
     * @Metodo que percorre a lista de imagens selecionadas pelo o usu??rios
     * e mostralas na tela, esse processo em um Thread segund??ria.
     */
    public void setVeiculoImages() {
        SwingWorker worker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() throws Exception {
                cadastroVeiculoView.veiculoImagensContainer.removeAll();
                cadastroVeiculoView.veiculoImagensContainer.setLayout(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridy = 0;
                final int[] counter = {0};
                imagensURL.forEach(imageURL -> {
                    if (counter[0] == 4) {
                        constraints.gridy++;
                        constraints.gridx = 0;
                        counter[0] = 0;
                    } else {
                        constraints.gridx++;
                    }
                    VeiculoImageTemplateController template = new VeiculoImageTemplateController(imageURL);
                    template.addObserver(CadastroVeiculoController.this);
                    cadastroVeiculoView.veiculoImagensContainer.add(template.getVeiculoImageContainer(), constraints);
                    counter[0]++;
                });
                return null;
            }

            @Override
            public void done() {
                cadastroVeiculoView.veiculoImagensContainer.revalidate();
                blockOnLimit();
            }
        };
        worker.execute();
    }

    /**
     * @Metodo que remove uma imagem da lista de imagens selecionadas.
     */
    public void removerImage(String imageURL) {
        this.imagensURL.remove(imageURL);
        setVeiculoImages();
    }

    @Override
    public void update(Observable o, Object arg) {

        try {
            String rota = (String) arg;
            if (!this.uiMainController.getUltimoPanel().equals(VEICULO_FORMULARIO_CADASTRO) && rota.equals(VEICULO_FORMULARIO_CADASTRO)) {
                this.uiMainController.changePanel(cadastroVeiculoView.container, VEICULO_FORMULARIO_CADASTRO);
                this.uiMainController.deleteObserver(this);
            }
        } catch (ClassCastException e) {

        }
        try {
            removerImage((String) arg);
        } catch (ClassCastException e) {

        }
    }
}
