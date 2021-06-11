package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.encryptor.PasswordEncryptor;
import br.com.ufmg.coltec.locamais.model.Adiministrador;
import br.com.ufmg.coltec.locamais.model.Diretor;
import br.com.ufmg.coltec.locamais.model.Funcionario;
import br.com.ufmg.coltec.locamais.model.Gerente;
import br.com.ufmg.coltec.locamais.ui.template.controller.ClienteFuncionarioTemplateController;
import br.com.ufmg.coltec.locamais.ui.template.controller.EnderecoTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.CadastroFuncionario;

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

import static br.com.ufmg.coltec.locamais.filemanager.FileReaderWriter.createFolder;
import static br.com.ufmg.coltec.locamais.filemanager.FuncionariosFileManager.getFuncionariosCadastrados;
import static br.com.ufmg.coltec.locamais.filemanager.FuncionariosFileManager.saveFuncionarios;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.FUNCIONARIOS_IMAGE_DIRECTORY;
import static br.com.ufmg.coltec.locamais.image.ImageScale.scaleImage;
import static br.com.ufmg.coltec.locamais.image.ImageWrite.writeImage;
import static br.com.ufmg.coltec.locamais.model.FuncionariosCargos.*;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.FUNCIONARIO_DESCRICAO_PANEL;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.FUNCIONARIO_FORMULARIO_CADASTRO;
import static br.com.ufmg.coltec.locamais.ui.helper.SelectorImage.getSingleImage;
import static br.com.ufmg.coltec.locamais.ui.helper.UiHelper.setLimit;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.*;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.*;
import static br.com.ufmg.coltec.locamais.ui.message.ShowMessage.messageShow;

public class CadastroFuncionarioController implements Observer {

    private CadastroFuncionario cadastroFuncionarioView;
    private EnderecoTemplateController enderecoTemplateController;
    private UIMainController uiMainController;
    private Funcionario funcionario;
    private List<Funcionario> funcionarios;
    private File imagefile;


    public CadastroFuncionarioController(UIMainController uiMainController) {
        this.uiMainController = uiMainController;
        this.uiMainController.addObserver(this);
        this.cadastroFuncionarioView = new CadastroFuncionario();
        this.enderecoTemplateController = new EnderecoTemplateController();
        GridLayout mgr = new GridLayout(1, 1);
        cadastroFuncionarioView.enderecoContainer.setLayout(mgr);
        cadastroFuncionarioView.enderecoContainer.add(enderecoTemplateController.getContainer());
        setFormattedTextField(mgr);
        setInputValidation();
        setCargosOnCombBox();
        setFieldLimit();
        escolherFuncionarioFoto();
        cadastrarEnvent();
    }


    /**
     * @Metodo para abrir janela onde o usuário do sistema deverá escolha um foto de perfíl para
     * o seguinte funcionário a ser cadastrado.
     */
    private void escolherFuncionarioFoto() {
        cadastroFuncionarioView.escolherFotoButton.addActionListener(al -> {
            try {
                File singleImage = getSingleImage(this.imagefile);
                this.imagefile = singleImage;
                ImageIcon imageIcon = new ImageIcon(scaleImage(255, 212, ImageIO.read(singleImage)));
                cadastroFuncionarioView.funcionarioImage.setIcon(imageIcon);
            } catch (Exception e) {
                e.printStackTrace();
                messageShow(uiMainController.uiMain.mainFrame, "Erro inesperado, tente novamente!");
            }
        });
    }

    /**
     * @Metodo para inserir campos de texto formatdos com suas respectivar modelagem.
     */
    private void setFormattedTextField(GridLayout mgr) {
        try {
            Font font = cadastroFuncionarioView.funcionarioNome.getFont();
            cadastroFuncionarioView.funcionarioCpf = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("###.###.###-##")));
            cadastroFuncionarioView.funcionarioCelular = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("(##) # ####-####")));
            cadastroFuncionarioView.funcionarioTelefoneFixo = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("(##) ####-####")));
            cadastroFuncionarioView.funcionarioTelefoneFixo.setFont(font);
            cadastroFuncionarioView.funcionarioCpf.setFont(font);
            cadastroFuncionarioView.funcionarioCelular.setFont(font);
            cadastroFuncionarioView.cpfPanel.setLayout(mgr);
            cadastroFuncionarioView.telefoneFixoPanel.setLayout(mgr);
            cadastroFuncionarioView.celularPanel.setLayout(mgr);
            cadastroFuncionarioView.cpfPanel.add(cadastroFuncionarioView.funcionarioCpf);
            cadastroFuncionarioView.telefoneFixoPanel.add(cadastroFuncionarioView.funcionarioTelefoneFixo);
            cadastroFuncionarioView.celularPanel.add(cadastroFuncionarioView.funcionarioCelular);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Metodo que tentar efetuar o cadastro do funcionário ao sistema.
     */
    private void cadastrarEnvent() {
        cadastroFuncionarioView.cadastrarButton.addActionListener(al -> {
            uiMainController.mostrarBarraDeProgresso();
            SwingWorker swingWorker = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    funcionarios = getFuncionariosCadastrados();
                    if (isInputsValid() && !cpfInUse()) {
                        setarInformacoes();
                        String funcionarioFolder = funcionario.getNome().toLowerCase().replaceAll("\\s", "_").concat(String.valueOf(System.currentTimeMillis()));
                        createDirectorys(funcionarioFolder);
                        setFuncionarioRegistro();
                        funcionarios.add(funcionario);
                        try {
                            funcionario.setImage(funcionarioFolder.concat(File.separator).concat(
                                    writeImage(FUNCIONARIOS_IMAGE_DIRECTORY.concat(funcionarioFolder.concat(File.separator)), imagefile.getAbsolutePath(), funcionarioFolder)));
                            saveFuncionarios(funcionarios);
                            messageShow(uiMainController.getMainFrame(), "Funcionário salvo com sucesso.");
                            ClienteFuncionarioTemplateController funcionarioTemplateController = new ClienteFuncionarioTemplateController(uiMainController, funcionario);
                            uiMainController.notifyObservers(FUNCIONARIO_DESCRICAO_PANEL);
                            uiMainController.changePanel(funcionarioTemplateController.getContainer(), FUNCIONARIO_DESCRICAO_PANEL);
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
     * @Metodo que coleta as informações passadas pelo usuáiro,
     * setando-as para o objeto do tipo funcionário.
     */
    private void setarInformacoes() {
        funcionario.setEndereco(enderecoTemplateController.getEndereco());
        funcionario.setNome(cadastroFuncionarioView.funcionarioNome.getText());
        funcionario.setCpf(cadastroFuncionarioView.funcionarioCpf.getText());
        funcionario.setTelefone(cadastroFuncionarioView.funcionarioTelefoneFixo.getText());
        funcionario.setCelular(cadastroFuncionarioView.funcionarioCelular.getText());
        funcionario.setSenha(PasswordEncryptor.encrypt(String.valueOf(cadastroFuncionarioView.funcionarioSenha.getPassword())).get(PasswordEncryptor.PASSWORD_KEY));
    }

    /**
     * @Metodo para a criação de todos os diretórios necessários para armazenar as
     * informações do funcionário a ser cadastrado.
     */
    private void createDirectorys(String funcionarioFolder) {
        createFolder(FUNCIONARIOS_IMAGE_DIRECTORY.concat(funcionarioFolder));
    }

    /**
     * @Metodo para setar a matrícula do funcionário a ser cadastrado no sistema,
     * caso não tenha nenhum funcionáiro, sua matrícula será o ano concatenado com 00001,
     * caso contrario, será a matrícula do ultimo somando +1;
     */
    private void setFuncionarioRegistro() {
        Integer ultimoRegistro = getLastRegistro();
        if (ultimoRegistro == 0) {
            String registroInicial = LocalDate.now().getYear() + "0001";
            funcionario.setMatricula(Integer.parseInt(registroInicial));
        } else {
            funcionario.setMatricula(ultimoRegistro + 1);
        }
    }

    /**
     * @Metodo para analize de matricula de funcionários,
     * retornando 0 caso não ha funcionários cadastrados no sistema.
     */
    private Integer getLastRegistro() {
        final Integer[] ultimoRegistro = {0};
        funcionarios.forEach(func -> {
            if (func.getMatricula() > ultimoRegistro[0]) {
                ultimoRegistro[0] = func.getMatricula();
            }
        });
        return ultimoRegistro[0];
    }

    /**
     * @Metodo para a analize de cpfs de funcionários já cadastrados ao sistema,
     * negando assim a permição de cadastro de cpf duplicados ao sistema.
     */
    private boolean cpfInUse() {
        boolean[] cpfIsInUse = {false};
        funcionarios.forEach(func -> {
            if (func.getCpf().equals(cadastroFuncionarioView.funcionarioCpf.getText())) {
                setDuplicatedError();
                cpfIsInUse[0] = true;
                return;
            }
        });
        return cpfIsInUse[0];
    }

    /**
     * @Metodo para ser um informa ao usuário, informando sobre o seguinte erro de
     * CPF duplicado.
     */
    private void setDuplicatedError() {
        cadastroFuncionarioView.funcionarioCpf.setText("");
        inputIsValid(cadastroFuncionarioView.funcionarioCpf, cadastroFuncionarioView.funcionarioCpfValidation, "Cpf já está em uso no sistema!", REGEX_CPF);
    }

    /**
     * @Metodo para validação de dados informados sobre o seguinte funcionário a ser
     * cadastrado no sistema.
     */
    public boolean isInputsValid() {
        try {
            return imageSelected()
                    && isCargoValido()
                    && inputIsValid(cadastroFuncionarioView.funcionarioNome, cadastroFuncionarioView.funcionarioNomeValidation, "Nome inválido!", REGEX_NOME)
                    && inputIsValid(cadastroFuncionarioView.funcionarioCpf, cadastroFuncionarioView.funcionarioCpfValidation, "Cpf inválido!", REGEX_CPF)
                    && inputIsValid(cadastroFuncionarioView.funcionarioCelular, cadastroFuncionarioView.funcionarioCelularValidation, "Celular inválido!", REGEX_CELULAR)
                    && inputIsValid(cadastroFuncionarioView.funcionarioTelefoneFixo, cadastroFuncionarioView.funcionarioTelefoneFixoValidation, "Telefone inválido!", REGEX_TELEFONE_FIXO)
                    && inputPasswordIsValid(cadastroFuncionarioView.funcionarioSenha, 8, 16, cadastroFuncionarioView.funcionarioSenaValidation)
                    && this.enderecoTemplateController.isValid();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /**
     * @Metodo que avalia se à uma foto selecionada para o funcionário antes de cadastralo ao sistema,
     * tornando assim a abrigatóriaedade de que cada funcionário do sistema conténha uma foto para
     * o complemento de suas informaçoes.
     */
    private boolean imageSelected() {
        if (this.imagefile != null) {
            cadastroFuncionarioView.imageSelectValidation.setText("");
            return true;
        }
        cadastroFuncionarioView.escolherFotoButton.requestFocus();
        cadastroFuncionarioView.imageSelectValidation.setText("Escolha uma foto para o funcionário!");
        return false;
    }

    /**
     * @Metodo para garitir que o seguinte usuário escolha um cargo para o seguinte
     * funcionário à ser cadastrado no sistema.
     */
    private boolean isCargoValido() {
        if (cadastroFuncionarioView.funcionarioCargoComboBox.getSelectedIndex() > 0) return true;

        cadastroFuncionarioView.funcionarioCargoComboBox.requestFocus();
        cadastroFuncionarioView.cargoValidation.setText("Escalha um cargo!");
        return false;
    }


    /**
     * @Metodo que avalia as informações de campos no instante em que esse mesmo campo perdo o foco.
     */
    private void setInputValidation() {
        inputValidationMatches(cadastroFuncionarioView.funcionarioCelular, cadastroFuncionarioView.funcionarioCelularValidation, REGEX_CELULAR, "Celular inválido!");
        inputValidationMatches(cadastroFuncionarioView.funcionarioTelefoneFixo, cadastroFuncionarioView.funcionarioTelefoneFixoValidation, REGEX_TELEFONE_FIXO, "Telefone inválido!");
        inputValidationMatches(cadastroFuncionarioView.funcionarioNome, cadastroFuncionarioView.funcionarioNomeValidation, REGEX_NOME, "Nome inválido!");
        inputValidationMatches(cadastroFuncionarioView.funcionarioCpf, cadastroFuncionarioView.funcionarioCpfValidation, REGEX_CPF, "Cpf inválido!");
        validacaoTextField(cadastroFuncionarioView.funcionarioSenha, cadastroFuncionarioView.funcionarioSenaValidation, "Senha não pode estar vazia!");
    }

    /**
     * @Metodo apenas limitando o tamanho maximo de caractres para a senha
     * do funcionário.
     */
    private void setFieldLimit() {
        setLimit(cadastroFuncionarioView.funcionarioSenha, 16);
    }


    /**
     * @Esse metodo cria um tipo de funcionário de acordo com o cargo escolhido
     * dentro de um combobox onde contém a opções de cargos para o funcionário.
     */
    private void setCargosOnCombBox() {
        cadastroFuncionarioView.funcionarioCargoComboBox.addItem("Escolha um Cargo!");
        cadastroFuncionarioView.funcionarioCargoComboBox.addItem(GERENTE);
        cadastroFuncionarioView.funcionarioCargoComboBox.addItem(DIRETOR);
        cadastroFuncionarioView.funcionarioCargoComboBox.addItem(ADMINISTRADOR);
        cadastroFuncionarioView.funcionarioCargoComboBox.addActionListener(al -> {
            if (isCargoValido()) {
                cadastroFuncionarioView.cargoValidation.setText("");
                String funcionarioCargo = cadastroFuncionarioView.funcionarioCargoComboBox.getSelectedItem().toString();
                switch (funcionarioCargo) {
                    case GERENTE:
                        this.funcionario = new Gerente();
                        break;
                    case DIRETOR:
                        this.funcionario = new Diretor();
                        break;
                    case ADMINISTRADOR:
                        this.funcionario = new Adiministrador();
                        break;
                }
            } else {
                cadastroFuncionarioView.cargoValidation.setText("Selecione um cargo!");
            }
        });

    }

    /**
     * @Metodo que retorna o painel com o formulário de cadastrdo um funcionário.
     */
//    public JPanel getContainer() {
//        return cadastroFuncionarioView.container;
//    }

    @Override
    public void update(Observable o, Object arg) {
        String rota = (String) arg;
        if (!this.uiMainController.getUltimoPanel().equals(FUNCIONARIO_FORMULARIO_CADASTRO) && rota.equals(FUNCIONARIO_FORMULARIO_CADASTRO)) {
            this.uiMainController.changePanel(cadastroFuncionarioView.container, FUNCIONARIO_FORMULARIO_CADASTRO);
            this.uiMainController.deleteObserver(this);
        }
    }
}
