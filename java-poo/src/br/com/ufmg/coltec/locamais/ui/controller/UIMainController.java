package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.FuncionarioAutenticavel;
import br.com.ufmg.coltec.locamais.ui.builder.MenuBuilder;
import br.com.ufmg.coltec.locamais.ui.template.controller.ClienteFuncionarioTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.UIMain;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import static br.com.ufmg.coltec.locamais.color.Colors.RED;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.FUNCIONARIOS_IMAGE_DIRECTORY;
import static br.com.ufmg.coltec.locamais.image.ImageScale.scaleImage;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.AUTENTICACAO_PANEL;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.FUNCIONARIO_DESCRICAO_PANEL;

public class UIMainController extends Observable {

    public UIMain uiMain;
    private String ultimoPanel;
    private FuncionarioAutenticavel funcionario;

    public UIMainController() {
        configurarJFramePrincipal();
        new MenuBuilder(this).setAppMenu();
        new AutenticacaoController(this);
        showFuncionarioData(this.uiMain.userImage);
        showFuncionarioData(this.uiMain.userName);
        this.uiMain.progressBarMain.setVisible(false);

        setLogoff();
        notifyObservers(AUTENTICACAO_PANEL);
    }

    public FuncionarioAutenticavel getFuncionario() {
        return funcionario;
    }

    public void setFuncionario(FuncionarioAutenticavel funcionario) {
        this.funcionario = funcionario;
    }


    /**
     * @Metodo que seta as configurações para o JFrame principal da
     * aplicação.
     * */
    private void configurarJFramePrincipal() {
        this.uiMain = new UIMain();
        this.uiMain.mainFrame = new JFrame("Loca Mais");
        this.ultimoPanel = "";
        this.uiMain.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        URL resource = getClass().getResource("/br/com/ufmg/coltec/locamais/resources/locamais-logo.png");
        this.uiMain.userLogOffPanel.setBackground(RED);
        this.uiMain.userActivePanel.setVisible(false);

        this.uiMain.mainFrame.setIconImage(new ImageIcon(resource).getImage());
        this.uiMain.mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.uiMain.mainFrame.setVisible(true);
        this.uiMain.mainFrame.add(this.uiMain.mainApplicationPanel);
        this.uiMain.mainFrame.setMinimumSize(new Dimension(1360, 733));
    }


    public String getUltimoPanel() {
        return ultimoPanel;
    }

    public JProgressBar getProgressBarMain() {
        return this.uiMain.progressBarMain;
    }

    public void mostrarBarraDeProgresso() {
        this.getProgressBarMain().setVisible(true);
        this.getProgressBarMain().setIndeterminate(true);
    }

    public void esconderBarraDeProgresso() {
        this.getProgressBarMain().setVisible(false);
        this.getProgressBarMain().setIndeterminate(false);
    }

    public JFrame getMainFrame() {
        return this.uiMain.mainFrame;
    }

    /**
     * @Metodo para mostrar os dados do funcionário ativo
     */
    private void showFuncionarioData(JLabel jLabel) {
        jLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (funcionario.getCargo() != null && funcionario.getCpf() != null) {
                    JPanel container = new ClienteFuncionarioTemplateController(UIMainController.this, funcionario).getContainer();
                    UIMainController.this.changePanel(container, FUNCIONARIO_DESCRICAO_PANEL);
                }
            }
        });
    }

    /**
     * @Metodo que efetua o logoff do usuário ativo.
     */
    private void setLogoff() {
        this.uiMain.userLogOff.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                int dialogButton = JOptionPane.YES_NO_OPTION;
                int dialogResult = JOptionPane.showConfirmDialog(uiMain.mainFrame, "Deseja efetuar logoff?", "Efetuar logoff", dialogButton);
                if (dialogResult == 0) {
                    uiMain.userActivePanel.setVisible(false);
                    funcionario = null;
                    new AutenticacaoController(UIMainController.this);
                    notifyObservers(AUTENTICACAO_PANEL);
                }
            }
        });
    }

    /**
     * @Metode para inserir as informações do funcionario ao top da tela.
     */
    public void setUserIcon() {
        ImageIcon imageIcon = null;//get the image from file chooser and scale it to match JLabel size
        try {
            File imageFile;
            if (funcionario.getImage() == null) {
                imageIcon = getImageIcon();
            } else {
                try {
                    imageFile = new File(FUNCIONARIOS_IMAGE_DIRECTORY.concat(funcionario.getImage()));
                    imageIcon = new ImageIcon(scaleImage(60, 60, ImageIO.read(imageFile)));
                } catch (IOException e) {
                    imageIcon = getImageIcon();
                    e.printStackTrace();
                }
            }
            this.uiMain.userImage.setIcon(imageIcon);
            this.uiMain.userName.setText(funcionario.getNome());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.uiMain.userActivePanel.setVisible(true);
    }

    /**
     * @Metodo que terona uma ImageIcon de uma imagem de dentro do projeto.
     */
    private ImageIcon getImageIcon() throws Exception {
        return new ImageIcon(scaleImage(60, 60, ImageIO.read(getClass().getResource("/br/com/ufmg/coltec/locamais/resources/admin-icon.png"))));
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    @Override
    public void notifyObservers(Object arg) {
            super.setChanged();
        String rota = (String) arg;
        if (funcionario != null && !rota.equals(AUTENTICACAO_PANEL)) {
            super.notifyObservers(arg);
        } else {
            super.notifyObservers(AUTENTICACAO_PANEL);
        }
    }

    @Override
    public synchronized void deleteObserver(Observer observer) {
        super.deleteObserver(observer);
    }


    /**
     * @Metodo principal para efetuar trocar de paineis.
     * @Analiza se a um funcionário autenticavel logado.
     */
    public void changePanel(JPanel newPanel, String activePanelName) {
        this.uiMain.mainPanelContainer.removeAll();
        this.ultimoPanel = activePanelName;
        this.uiMain.mainPanelContainer.setLayout(new GridLayout());
        this.uiMain.mainPanelContainer.add(newPanel);

        this.uiMain.mainPanelContainer.revalidate();
    }
}
