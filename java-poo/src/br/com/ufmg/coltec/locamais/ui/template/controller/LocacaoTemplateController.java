package br.com.ufmg.coltec.locamais.ui.template.controller;

import br.com.ufmg.coltec.locamais.model.Cliente;
import br.com.ufmg.coltec.locamais.model.Locacao;
import br.com.ufmg.coltec.locamais.model.Veiculo;
import br.com.ufmg.coltec.locamais.ui.controller.FinalizarLocacaoController;
import br.com.ufmg.coltec.locamais.ui.controller.UIMainController;
import br.com.ufmg.coltec.locamais.ui.template.LocacaoTemplate;
import br.com.ufmg.coltec.locamais.ui.template.VeiculoTemplate;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static br.com.ufmg.coltec.locamais.color.Colors.SEPARADOR;
import static br.com.ufmg.coltec.locamais.converter.Converter.doubleToRealString;
import static br.com.ufmg.coltec.locamais.filemanager.ClienteFileManager.getClientes;
import static br.com.ufmg.coltec.locamais.filemanager.VeiculosFileManager.getVeiculos;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.FINALIZAR_LOCACAO;
import static br.com.ufmg.coltec.locamais.ui.helper.UiHelper.valorASerPagorPorKilometragemRodada;

public class LocacaoTemplateController {

    private List<Cliente> clientes;
    private List<Veiculo> veiculos;
    private Cliente cliente;
    private Veiculo veiculo;
    private Locacao locacao;
    private boolean mostrarSeparador;
    private UIMainController uiMainController;
    private boolean mostrarBotaoFinalizarLocacao;
    private boolean mostrarClienteEVeiculo;
    private LocacaoTemplate locacaoTemplateView;


    /**
     * @Contrutor que carrega informações do cliente
     * e do veículo.
     */
    public LocacaoTemplateController(UIMainController uiMainController, Locacao locacao, boolean mostrarSeparador, boolean mostrarBotaoFinalizarLocacao) {
        this.uiMainController = uiMainController;
        this.mostrarBotaoFinalizarLocacao = mostrarBotaoFinalizarLocacao;
        this.mostrarClienteEVeiculo = true;
        incializarComponentes(locacao, mostrarSeparador);
    }


    /**
     * @Contrutor que carrega informações previa de pagamento.
     */
    public LocacaoTemplateController(UIMainController uiMainController, Locacao locacao, boolean mostrarSeparador, boolean mostrarBotaoFinalizarLocacao, boolean mostrarClienteEVeiculo) {
        this.uiMainController = uiMainController;
        this.mostrarBotaoFinalizarLocacao = mostrarBotaoFinalizarLocacao;
        this.mostrarClienteEVeiculo = mostrarClienteEVeiculo;
        incializarComponentes(locacao, mostrarSeparador);
    }

    /**
     * @Metodo que inicializa os componetes e ações.
     */
    private void incializarComponentes(Locacao locacao, boolean mostrarSeparador) {
        this.locacaoTemplateView = new LocacaoTemplate();
        this.locacao = locacao;
        this.mostrarSeparador = mostrarSeparador;
        locacaoTemplateView.finalizarLocacao.setVisible(false);
        this.veiculos = getVeiculos();
        this.clientes = getClientes();
        locacaoTemplateView.separador.setBackground(SEPARADOR);
        if (!this.mostrarSeparador) locacaoTemplateView.separador.setVisible(false);

        this.veiculo = locacao.getVeiculo();
        this.cliente = locacao.getCliente();
        if (mostrarClienteEVeiculo) {
            setClienteOnPanel();
            setVeiculoOnPanel();
        }

        setAnalizeDeEntragaDoVeiculo();

        locacaoTemplateView.finalizarLocacao.addActionListener(al -> {
            this.uiMainController.addObserver(new FinalizarLocacaoController(this.uiMainController, locacao));
            uiMainController.notifyObservers(FINALIZAR_LOCACAO);
        });
    }

    /**
     * @Metodo que seta as informações de analize previa do pagamento
     * no ato da entrega do veículo.
     */
    private void setAnalizeDeEntragaDoVeiculo() {
        locacaoTemplateView.kilometragemEstimadaDevolucaoInfo.setText("KM/Estimada: ".concat(String.valueOf(this.locacao.getKilometragemEstimadaDeEntrega())));
        locacaoTemplateView.dataEstimadaDevolucaoInfo.setText("Devolução: ".concat(this.locacao.getDataEstimadaDeEntrega()));
        locacaoTemplateView.dataDaLocacao.setText("Efetuada em: ".concat(this.locacao.getDataDaLocacao()));
        locacaoTemplateView.valorEstimadoAhPagar.setText("Valor estimado: ".concat(doubleToRealString(valorASerPagorPorKilometragemRodada(this.locacao.getKilometragemEstimadaDeEntrega(), this.veiculo))));
        if (this.locacao.isAtiva() && this.mostrarBotaoFinalizarLocacao) {
            locacaoTemplateView.finalizarLocacao.setVisible(true);
        }
    }


    /**
     * @Metodo para mostrar as informações do veículo
     * na tela.
     */
    private void setVeiculoOnPanel() {
        locacaoTemplateView.veiculoPanel.setLayout(new GridLayout());
        locacaoTemplateView.veiculoPanel.add(new VeiculoTemplateController(this.veiculo).getMainPanel());
        locacaoTemplateView.veiculoPanel.revalidate();
    }

    /**
     * @Metodo para mostrar as informações do cliente
     * na tela.
     */
    private void setClienteOnPanel() {
        locacaoTemplateView.clientePanel.setLayout(new GridLayout());
        locacaoTemplateView.clientePanel.add(new ClienteFuncionarioTemplateController(uiMainController, cliente).getContainer());
        locacaoTemplateView.clientePanel.revalidate();
    }

    /**
     * @Metodo que retonar o painel ja carredo com todos
     * as informações na tela.
     */
    public JPanel getContainer() {
        return locacaoTemplateView.container;
    }

}
