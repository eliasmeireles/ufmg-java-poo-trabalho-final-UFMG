package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.encryptor.PasswordEncryptor;
import br.com.ufmg.coltec.locamais.model.Diretor;
import br.com.ufmg.coltec.locamais.model.Funcionario;
import br.com.ufmg.coltec.locamais.ui.view.Autenticacao;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static br.com.ufmg.coltec.locamais.color.Colors.RED;
import static br.com.ufmg.coltec.locamais.filemanager.FuncionariosFileManager.getFuncionariosCadastrados;
import static br.com.ufmg.coltec.locamais.help.BuildFuncionarioAutenticavel.getFuncionarioAutenticavel;
import static br.com.ufmg.coltec.locamais.ui.constant.Constant.SYSTEM_PASSWORD;
import static br.com.ufmg.coltec.locamais.ui.constant.Constant.SYSTEM_USER;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.*;
import static br.com.ufmg.coltec.locamais.ui.helper.UiHelper.getNumberFormatter;
import static br.com.ufmg.coltec.locamais.ui.message.ShowMessage.messageShow;
import static java.lang.Thread.sleep;

public class AutenticacaoController implements Observer {

    private Autenticacao autenticacao;
    private UIMainController uiMainController;
    public String funcionarioPassword;
    public Integer funcionarioMatricula;

    public AutenticacaoController(UIMainController uiMainController) {
        this.uiMainController = uiMainController;
        this.autenticacao = new Autenticacao();
        this.uiMainController.uiMain.mainFrame.getRootPane().setDefaultButton(this.autenticacao.efetuarLoginButton);
        this.autenticacao.progressPanel.setVisible(false);
        this.autenticacao.logingInfoResult.setForeground(RED);
        uiMainController.addObserver(this);
        setMatriculaFieldFormatted();
        login();
    }


    /**
     * @Metodo para criar campo formatado para inserir a matrícula.
     */
    private void setMatriculaFieldFormatted() {
        this.autenticacao.numeroMatriculaPanel.setLayout(new GridLayout());
        NumberFormatter formatter = getNumberFormatter(-1, 99999999);
        this.autenticacao.numeroMatricula = new JFormattedTextField(formatter);
        Insets insets = new Insets(5, 0, 5, 0);
        this.autenticacao.numeroMatricula.setMargin(insets);
        this.autenticacao.password.setMargin(insets);
        this.autenticacao.numeroMatricula.setFont(this.autenticacao.password.getFont());
        this.autenticacao.numeroMatriculaPanel.add(this.autenticacao.numeroMatricula);
    }


    /**
     * @Metdo que capitura a ação do botão para efetuar o login
     */
    private void login() {
        uiMainController.mostrarBarraDeProgresso();
        this.autenticacao.efetuarLoginButton.addActionListener(al -> {
            String passwordString = String.valueOf(this.autenticacao.password.getPassword());
            String matriculaString = String.valueOf(this.autenticacao.numeroMatricula.getValue());

            if (validaLoginForm(passwordString, matriculaString)) {
                this.autenticacao.progressPanel.setVisible(true);
                this.autenticacao.logingInfoResult.setVisible(false);
                this.autenticacao.loginPanel.setVisible(false);
                funcionarioPassword = PasswordEncryptor.encrypt(passwordString).get(PasswordEncryptor.PASSWORD_KEY);
                funcionarioMatricula = Integer.parseInt(matriculaString);
                loginBackground();
            } else {
                this.autenticacao.logingInfoResult.setText("Dados inválidos!");
            }
        });
        uiMainController.esconderBarraDeProgresso();
    }

    private boolean validaLoginForm(String passwordString, String matriculaString) {
        return passwordString != null && !passwordString.isEmpty() && matriculaString != null && !matriculaString.isEmpty();
    }

    /**
     * @Metodo de processamendo dos dados em uma nova Thread
     **/
    private void loginBackground() {
        SwingWorker<Object, Object> swingWorker = new SwingWorker<Object, Object>() {
            @Override
            protected Object doInBackground() {
                try {
                    sleep(125);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                super.done();
                progressInfoReset();
                try {
                    setFuncionarioLogedIn();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        };
        swingWorker.execute();
    }

    /**
     * @Metodo que tenta ensirir os dados do seguinte usuário
     **/
    private void setFuncionarioLogedIn() {
        Funcionario funcionario = getFuncionario();
        if (funcionario != null) {
            uiMainController.setFuncionario(getFuncionarioAutenticavel(funcionario));
            new VeiculosController(this.uiMainController, false);
            uiMainController.notifyObservers(LISTAR_VEICULOS);
            uiMainController.setUserIcon();
        } else {
            this.autenticacao.logingInfoResult.setVisible(true);
            this.autenticacao.logingInfoResult.setText("Acesso negado, dados informados inválidos!");
        }
    }

    /**
     * @Metodo para resetar os estados do formulário
     */
    private void progressInfoReset() {
        this.autenticacao.progressPanel.setVisible(false);
        this.autenticacao.loginPanel.setVisible(true);
    }


    /**
     * @Validação de login como administrador geral do sistema
     * Ou um funcionário
     **/
    private Funcionario getFuncionario() {
        Funcionario[] funonario = {null};
        if (funcionarioPassword.equals(SYSTEM_PASSWORD) && encryptMe(String.valueOf(funcionarioMatricula), PasswordEncryptor.PASSWORD_KEY).equals(SYSTEM_USER)) {
            messageShow(uiMainController.getMainFrame(), "Você efetuol login como um administrador do sistema");
            Diretor adiministrador = new Diretor();
            uiMainController.setFuncionario(adiministrador);
            adiministrador.setNome("Admin");
            new CadastroFuncionarioController(this.uiMainController);
            uiMainController.notifyObservers(FUNCIONARIO_FORMULARIO_CADASTRO);
            uiMainController.setUserIcon();
        } else {
            funonario[0] = getFuncionarioIfDataMatches();
        }
        return funonario[0];
    }

    /**
     * @Localiza lista de funcionários cadastrados
     * @Verifica se os dados informados ao login form são validos
     */
    private Funcionario getFuncionarioIfDataMatches() {
        Funcionario[] funonario = {null};
        try {
            List<Funcionario> funcionarios = getFuncionariosCadastrados();
            funcionarios.forEach(consumer -> {
                if (consumer.getMatricula().equals(funcionarioMatricula) && consumer.getSenha().equals(funcionarioPassword)) {
                    funonario[0] = consumer;
                    return;
                }
            });

        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return funonario[0];
    }

    /**
     * @Metodo que recebe um valor e uma chave por parametro e retorna um dado criptografado.
     */
    private String encryptMe(String value, Object key) {
        return PasswordEncryptor.encrypt(value).get(key);
    }


    /**
     * @Metodo que retorna o painel de autenticação.
     */
    public JPanel getContainer() {
        return this.autenticacao.container;
    }

    @Override
    public void update(Observable o, Object arg) {
        String rota = (String) arg;

        if (rota.equals(AUTENTICACAO_PANEL) && !uiMainController.getUltimoPanel().equals(AUTENTICACAO_PANEL)) {
            this.uiMainController.changePanel(this.autenticacao.container, AUTENTICACAO_PANEL);
        }
    }


}
