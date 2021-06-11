package br.com.ufmg.coltec.locamais.ui.builder;

import br.com.ufmg.coltec.locamais.ui.controller.*;

import javax.swing.*;
import java.awt.*;

import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.*;
import static br.com.ufmg.coltec.locamais.ui.message.ShowMessage.messageShow;

/**
 * Created by elias on 12/11/18.
 */
public class MenuBuilder {

    private UIMainController uiMainController;

    public MenuBuilder(UIMainController uiMainController) {
        this.uiMainController = uiMainController;
    }

    /**
     * @Metodo que gera o MENU principal da aplicação.
     */
    public void setAppMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(getVeiculoMenu());
        menuBar.add(getFuncionarioMenu());
        menuBar.add(getClientesMenu());
        menuBar.add(getFabricantesMenu());
        menuBar.add(getSairAplicacao());
        uiMainController.getMainFrame().setJMenuBar(menuBar);
    }

    /**
     * @Metdo que retorna o menu dos funcionários.
     */
    private JMenu getFuncionarioMenu() {
        JMenu funcionario = getJMenu("Funcionario");
        setMenuFont(funcionario);
        JMenuItem cadastrar = getJMenuItem("Cadastrar");
        cadastrar.addActionListener(al -> {
            new CadastroFuncionarioController(this.uiMainController);
            this.uiMainController.notifyObservers(FUNCIONARIO_FORMULARIO_CADASTRO);
            notifica();
        });

        funcionario.add(cadastrar);
        return funcionario;
    }

    /**
     * @Metodo que retorna o MENU Veículos e seu diversos sub menus.
     */
    private JMenu getVeiculoMenu() {
        JMenu veiculo = getJMenu("Veículo");
        setMenuFont(veiculo);
        JMenuItem cadastrar = getJMenuItem("Cadastrar");
        cadastrar.addActionListener(al -> {
            new CadastroVeiculoController(this.uiMainController);
            this.uiMainController.notifyObservers(VEICULO_FORMULARIO_CADASTRO);
            notifica();
        });
        JMenuItem listarTodos = getJMenuItem("Lista");
        listarTodos.addActionListener(al -> {
            new VeiculosController(this.uiMainController, false);
            this.uiMainController.notifyObservers(LISTAR_VEICULOS);
            notifica();
        });

        veiculo.add(cadastrar);
        veiculo.add(listarTodos);
        return veiculo;
    }

    /**
     * @Metodo que retorna o MENU Fabricantes e seu diversos sub menus.
     */
    private JMenu getFabricantesMenu() {
        JMenu fabricante = getJMenu("Fabricantes");
        setMenuFont(fabricante);
        JMenuItem cadastrar = getJMenuItem("Cadastrar");
        cadastrar.addActionListener(al -> {
            new CadastrarFabricantesController(this.uiMainController);
            this.uiMainController.notifyObservers(FABRICANTE_FORMULARIO_CADASTRO);
            notifica();
        });

        fabricante.add(cadastrar);
        return fabricante;
    }


    /**
     * @Metodo que retorna o MENU Clientes e seu diversos sub menus.
     */
    private JMenu getClientesMenu() {
        JMenu clientes = getJMenu("Clientes");
        setMenuFont(clientes);
        JMenuItem cadastrar = getJMenuItem("Cadastrar");
        JMenu locacao = getJMenu("Locação");
        JMenuItem locacoes = getJMenuItem("Listar Locações");
        JMenuItem efetuarLocacao = getJMenuItem("Efetuar Locação");
        JMenuItem historicoLocacao = getJMenuItem("Histórico de locações");

        locacao.add(locacoes);
        locacao.add(efetuarLocacao);
        locacao.add(historicoLocacao);

        efetuarLocacao.addActionListener(al -> {
            new ClientesController(this.uiMainController);
            this.uiMainController.notifyObservers(CLIENTE_LISTAR);
            notifica();
        });
        cadastrar.addActionListener(al -> {
            new ClienteCadastroController(this.uiMainController);
            this.uiMainController.notifyObservers(CLIENTE_FORMULARIO_CADASTRO);
            notifica();
        });
        locacoes.addActionListener(al -> {
            new LocacoesController(this.uiMainController);
            this.uiMainController.notifyObservers(LISTAR_LOCACOES);
            notifica();
        });
        historicoLocacao.addActionListener(al -> {
            new HistoricoLocacoesController(this.uiMainController);
            this.uiMainController.notifyObservers(HISTORICO_LOCACAO);
            notifica();
        });


        clientes.add(cadastrar);
        clientes.add(locacao);
        return clientes;
    }


    /**
     * @Metodo que retorna um Menu com suas margins configuradas.
     */
    private JMenu getJMenu(String s) {
        JMenu jMenu = new JMenu(s);
        jMenu.setMargin(new Insets(8, 0, 8, 10));
        setMenuFont(jMenu);
        return jMenu;
    }


    /**
     * @Metodo que retorna e um sub menu já com suas margins configuradas.
     */
    private JMenuItem getJMenuItem(String text) {
        JMenuItem jMenuItem = new JMenuItem(text);
        jMenuItem.setMargin(new Insets(5, 0, 5, 5));
        setMenuFont(jMenuItem);
        return jMenuItem;
    }

    /**
     * @Metodo que retorna o Menu com seu sub menu para finalizar a aplicação.
     */
    private JMenu getSairAplicacao() {
        JMenu exit = getJMenu("Sair");
        setMenuFont(exit);
        JMenuItem sair = getJMenuItem("Sair do Sistema!");
        setMenuFont(sair);
        sair.addActionListener(e -> System.exit(0));
        exit.add(sair);
        return exit;
    }

    /**
     * @Metodo para setar a fonto dos sub menus.
     */
    private void setMenuFont(JMenuItem item) {
        item.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
    }

    /**
     * @Metodo de notificação de acesso ao menu.
     */
    private void notifica() {
        if (uiMainController.getFuncionario() == null) {
            messageShow(uiMainController.getMainFrame(), "É preciso um funcionário autenticado para \nter acesso ao sistema!");
        }
    }

}
