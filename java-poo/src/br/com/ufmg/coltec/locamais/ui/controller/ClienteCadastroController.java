package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.Cliente;
import br.com.ufmg.coltec.locamais.ui.template.controller.EnderecoTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.ClienteCadastro;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDate;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static br.com.ufmg.coltec.locamais.filemanager.ClienteFileManager.getClientes;
import static br.com.ufmg.coltec.locamais.filemanager.ClienteFileManager.saveClientes;
import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.createFolder;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.CLIENTES_IMAGE_DIRECTORY;
import static br.com.ufmg.coltec.locamais.image.ImageScale.scaleImage;
import static br.com.ufmg.coltec.locamais.image.ImageWrite.writeImage;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.CLIENTE_FORMULARIO_CADASTRO;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.CLIENTE_LISTAR;
import static br.com.ufmg.coltec.locamais.ui.helper.SelectorImage.getSingleImage;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.*;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.inputIsValid;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.inputValidationMatches;
import static br.com.ufmg.coltec.locamais.ui.message.ShowMessage.messageShow;

public class ClienteCadastroController implements Observer {

    private ClienteCadastro clienteCadastroView;
    private EnderecoTemplateController enderecoTemplateController;
    private List<Cliente> clientes;
    private File imagefile;
    private UIMainController uiMainController;

    public ClienteCadastroController(UIMainController uiMainController) {
        this.uiMainController = uiMainController;
        this.uiMainController.addObserver(this);
        this.clienteCadastroView = new ClienteCadastro();
    }

    /**
     * @Metodo que carrega as informações em segundo plano
     * sem congelar a tela
     */
    private void carregarInformacoes() {
        uiMainController.mostrarBarraDeProgresso();
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground()  {
                clientes = getClientes();
                clienteCadastroView.enderecoContainer.setLayout(new GridLayout());
                enderecoTemplateController = new EnderecoTemplateController();
                clienteCadastroView.enderecoContainer.add(enderecoTemplateController.getContainer());

                setFormattedTextField();
                escolherClienteImage();
                cadastrarEnvent();
                setInputValidation();
                return null;
            }

            @Override
            protected void done() {
                super.done();
                uiMainController.esconderBarraDeProgresso();
            }
        };
        swingWorker.execute();
    }

    /**
     * @Metodo para cadastrar o cliente caso todos os campos estejam devidamente preenchidos.
     */
    private void cadastrarEnvent() {
        clienteCadastroView.cadastrarButton.addActionListener(al -> {
            SwingWorker swingWorker = new SwingWorker() {
                @Override
                protected Object doInBackground()  {
                    if (isInputsValid() && !cpfInUse() && !emailUse()) {
                        Cliente cliente = buielCliente();
                        String clienteFolder = cliente.getNome().toLowerCase().replaceAll("\\s", "_").concat(String.valueOf(System.currentTimeMillis()));
                        createDirectorys(clienteFolder);
                        try {
                            cliente.setImage(clienteFolder.concat(File.separator).concat(
                                    writeImage(CLIENTES_IMAGE_DIRECTORY.concat(clienteFolder.concat(File.separator)), imagefile.getAbsolutePath(), clienteFolder)));
                            saveClientes(clientes);
                            messageShow(uiMainController.getMainFrame(), "Cliente salvo com sucesso.");

                            new ClientesController(uiMainController);
                            uiMainController.notifyObservers(CLIENTE_LISTAR);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                    return null;
                }

                @Override
                protected void done() {
                    super.done();
                    uiMainController.esconderBarraDeProgresso();
                }
            };
            swingWorker.execute();

        });
    }

    /**
     * @Metodo para criação de um objeto do tipo cliente com
     * as seguinte informações inseridas pelo usuário.
     */
    private Cliente buielCliente() {
        Cliente cliente = new Cliente();
        cliente.setEndereco(this.enderecoTemplateController.getEndereco());
        cliente.setNome(clienteCadastroView.clienteNome.getText());
        cliente.setEmail(clienteCadastroView.clienteEmail.getText());
        cliente.setCpf(clienteCadastroView.clienteCpf.getText());
        cliente.setCelular(clienteCadastroView.clienteCelular.getText());
        cliente.setMatricula(getMatricula());
        this.clientes.add(cliente);
        return cliente;
    }

    /**
     * @Gera seguintes diretorios necessários para o salvamento do clientes.
     */
    private void createDirectorys(String clienteFolder) {
        createFolder(CLIENTES_IMAGE_DIRECTORY.concat(clienteFolder));
    }

    /**
     * @Metodo que avalia a matrícula para cada cliente, retorno a +1 caso já exista clientes cadastrados no sistema.
     */
    private Integer getMatricula() {
        Integer[] matricula = {0};

        clientes.forEach(consumer -> {
            if (consumer.getMatricula() >= matricula[0]) {
                matricula[0] = consumer.getMatricula() + 1;
            }
        });

        if (matricula[0] == 0) {
            matricula[0] = Integer.valueOf(LocalDate.now().getYear() + "0001");
        }

        return matricula[0];
    }


    /**
     * @Metodo para abrir janela onde on usuário deverá escolher uma foto de perfíl para o cliente.
     */
    private void escolherClienteImage() {
        clienteCadastroView.escolherFotoButton.addActionListener(al -> {
            try {
                File singleImage = getSingleImage(this.imagefile);
                this.imagefile = singleImage;
                ImageIcon imageIcon = new ImageIcon(scaleImage(255, 212, ImageIO.read(singleImage)));
                clienteCadastroView.clienteImage.setIcon(imageIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * @Metodo que avalia se as informações inseridas são ou não validas antes de efetuar o cadastro do cliente.
     */
    public boolean isInputsValid() {
        try {
            return imageSelected()
                    && inputIsValid(clienteCadastroView.clienteNome, clienteCadastroView.clienteNomeValidation, "Nome inválido!", REGEX_NOME)
                    && inputIsValid(clienteCadastroView.clienteEmail, clienteCadastroView.clienteEmailValidation, "Email inválido!", EMAIL_PATTERN)
                    && inputIsValid(clienteCadastroView.clienteCpf, clienteCadastroView.clienteCpfValidation, "Cpf inválido!", REGEX_CPF)
                    && inputIsValid(clienteCadastroView.clienteCelular, clienteCadastroView.clienteCelularValidation, "Celular inválido!", REGEX_CELULAR)
                    && this.enderecoTemplateController.isValid();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * @Metodo que avalia as informações de campos no instante em que esse mesmo campo perdo o foco.
     */
    private void setInputValidation() {
        inputValidationMatches(clienteCadastroView.clienteCelular, clienteCadastroView.clienteCelularValidation, REGEX_CELULAR, "Celular inválido!");
        inputValidationMatches(clienteCadastroView.clienteEmail, clienteCadastroView.clienteEmailValidation, EMAIL_PATTERN, "Email inválido!");
        inputValidationMatches(clienteCadastroView.clienteNome, clienteCadastroView.clienteNomeValidation, REGEX_NOME, "Nome inválido!");
        inputValidationMatches(clienteCadastroView.clienteCpf, clienteCadastroView.clienteCpfValidation, REGEX_CPF, "Cpf inválido!");
    }


    /**
     * @Metodo para a analize de bloqueio de CPF duplicados, sendo assim, não será possivel cadastrar mais que um
     * CPF no sistema.
     */
    private boolean cpfInUse() {
        boolean[] cpfIsInUse = {false};
        clientes.forEach(consumer -> {
            if (consumer.getCpf().equals(clienteCadastroView.clienteCpf.getText())) {
                clienteCadastroView.clienteCpf.setText("");
                inputIsValid(clienteCadastroView.clienteCpf, clienteCadastroView.clienteCpfValidation, "Cpf já está em uso no sistema!", REGEX_CPF);
                cpfIsInUse[0] = true;
                return;
            }
        });
        return cpfIsInUse[0];
    }

    /**
     * @Metodo para a analize de bloqueio de EMAIL duplicados, sendo assim, não será possivel cadastrar mais que um
     * EMAIL no sistema.
     */
    private boolean emailUse() {
        boolean[] emailUse = {false};
        clientes.forEach(consumer -> {
            if (consumer.getEmail().equals(clienteCadastroView.clienteEmail.getText())) {
                clienteCadastroView.clienteEmail.setText("");
                inputIsValid(clienteCadastroView.clienteEmail, clienteCadastroView.clienteEmailValidation, "Email já está em uso no sistema!", EMAIL_PATTERN);
                emailUse[0] = true;
                return;
            }
        });
        return emailUse[0];
    }


    /**
     * @Metodo para inserir na tela os campos cpf e celular com suas respectivas formatações.
     */
    private void setFormattedTextField() {
        GridLayout gridLayout = new GridLayout();
        try {
            Font font = clienteCadastroView.clienteNome.getFont();
            clienteCadastroView.clienteCelular = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("(##) # ####-####")));
            clienteCadastroView.clienteCpf = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("###.###.###-##")));
            clienteCadastroView.clienteCpf.setFont(font);
            clienteCadastroView.clienteCelular.setFont(font);
            clienteCadastroView.cpfPanel.setLayout(gridLayout);
            clienteCadastroView.celularPanel.setLayout(gridLayout);
            clienteCadastroView.cpfPanel.add(clienteCadastroView.clienteCpf);
            clienteCadastroView.celularPanel.add(clienteCadastroView.clienteCelular);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * @Metodo que avalia se à uma foto selecionada para o cliente antes de cadastralo ao sistema,
     * tornando assim a abrigatóriaedade de que cada cliente do sistema conténha uma foto para
     * o complemento de suas informaçoes.
     */
    private boolean imageSelected() {
        if (this.imagefile != null) {
            clienteCadastroView.imageSelectValidation.setText("");
            return true;
        }
        clienteCadastroView.escolherFotoButton.requestFocus();
        clienteCadastroView.imageSelectValidation.setText("Escolha uma foto para o cliente!");
        return false;
    }

    /**
     * @Retorna um painel já carregado com fo formulário de cadastro de um novo cliente.
     */
//    public JPanel getContainer() {
//        return container;
//    }

    @Override
    public void update(Observable o, Object arg) {
        String rota = (String) arg;
        if (!this.uiMainController.getUltimoPanel().equals(CLIENTE_FORMULARIO_CADASTRO) && rota.equals(CLIENTE_FORMULARIO_CADASTRO)) {
            carregarInformacoes();
            this.uiMainController.changePanel(clienteCadastroView.container, CLIENTE_FORMULARIO_CADASTRO);
            this.uiMainController.deleteObserver(this);
        }
    }
}
