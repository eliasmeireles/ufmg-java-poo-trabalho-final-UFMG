package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.Cliente;
import br.com.ufmg.coltec.locamais.model.Locacao;
import br.com.ufmg.coltec.locamais.model.Veiculo;
import br.com.ufmg.coltec.locamais.ui.template.controller.ClienteFuncionarioTemplateController;
import br.com.ufmg.coltec.locamais.ui.template.controller.LocacaoTemplateController;
import br.com.ufmg.coltec.locamais.ui.template.controller.VeiculoTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.EfetuaLocacao;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.io.IOException;
import java.text.ParseException;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static br.com.ufmg.coltec.locamais.color.Colors.GREEN;
import static br.com.ufmg.coltec.locamais.color.Colors.RED;
import static br.com.ufmg.coltec.locamais.converter.Converter.stringToLocalDate;
import static br.com.ufmg.coltec.locamais.converter.Converter.LocalDateBRString;
import static br.com.ufmg.coltec.locamais.filemanager.LocacaoFileManager.getLocacoes;
import static br.com.ufmg.coltec.locamais.filemanager.LocacaoFileManager.salvarLocacoes;
import static br.com.ufmg.coltec.locamais.filemanager.VeiculosFileManager.getVeiculos;
import static br.com.ufmg.coltec.locamais.filemanager.VeiculosFileManager.savarVeiculo;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.FAZER_LOCACAO;
import static br.com.ufmg.coltec.locamais.ui.helper.UiHelper.getNumberFormatter;
import static br.com.ufmg.coltec.locamais.ui.helper.VeiculoSituacao.INDISPONIVEL;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_DATA_STRING;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.*;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.inputIsValid;

public class EfetuaLocacaoController implements Observer {

    private UIMainController uiMainController;
    private EfetuaLocacao efetuaLocacaoView;
    private Cliente cliente;
    private Veiculo veiculo;
    private List<Locacao> locacoes;
    private List<Veiculo> veiculos;
    private Locacao locacao;

    public EfetuaLocacaoController(UIMainController uiMainController, Cliente cliente) {
        this.uiMainController = uiMainController;
        this.cliente = cliente;
        this.efetuaLocacaoView = new EfetuaLocacao();
        this.uiMainController.addObserver(this);
        carregarDados();
    }

    /**
     * @Metodo que carrega os dados desse painel
     * em segundo plano.
     */
    private void carregarDados() {
        uiMainController.mostrarBarraDeProgresso();
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                locacoes = getLocacoes();
                veiculos = getVeiculos();
                efetuaLocacaoView.entregaLocacaoEstimadaInfo.setVisible(false);
                efetuaLocacaoView.locacaoAnalizePanel.setVisible(false);
                efetuaLocacaoView.locacaoFinalizadaEstatus.setVisible(false);
                mostrarDadosCliente();
                mostrarListaDeVeiculosDiponiveis();
                setMask();

                setInputValidation();
                analizarLocacaoDados();
                salvarLocacao();
                alterarDadosDaLocacao();
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
     * @Metodo que mostrará a analize estimada de pagamento
     * de acordo com os dados informados.
     */
    private void analizarLocacaoDados() {
        efetuaLocacaoView.concluirDados.addActionListener(al -> {
            if (isValid()) {
                this.locacao = new Locacao();
                this.locacao.setCliente(cliente);
                this.locacao.setVeiculo(this.veiculo);
                this.locacao.setKilometragemEstimadaDeEntrega(Integer.parseInt(efetuaLocacaoView.kilometragemEstimada.getValue().toString()));
                this.locacao.setDataEstimadaDeEntrega(efetuaLocacaoView.dataEstimadaEntrega.getValue().toString());
                this.locacao.setDataDaLocacao(LocalDateBRString(LocalDate.now()));

                setInformacoesPreviaDePagamento();
                efetuaLocacaoView.listaVeiculosContainer.setVisible(false);
                efetuaLocacaoView.locacaoAnalizePanel.setVisible(true);
                efetuaLocacaoView.entregaLocacaoEstimadaInfo.setVisible(false);
            }
        });
    }

    /**
     * @Metodo para informa uma previa do pagamento no
     * ato da entrega do veículo.
     */
    private void setInformacoesPreviaDePagamento() {
        efetuaLocacaoView.informacaoPreviaDePagamento.removeAll();
        efetuaLocacaoView.informacaoPreviaDePagamento.setLayout(new GridLayout());
        efetuaLocacaoView.informacaoPreviaDePagamento.add(new LocacaoTemplateController(this.uiMainController, this.locacao, false, false, false).getContainer());
        efetuaLocacaoView.informacaoPreviaDePagamento.revalidate();
    }


    /**
     * @Metodo para alterar os dados da locação,
     * ao clicar no botão alterar, a lista de veículos será
     * mostrada novamente e os campos de estimativa de entrega.
     */
    private void alterarDadosDaLocacao() {
        efetuaLocacaoView.alterarDadosDaLocacao.addActionListener(al -> {
            efetuaLocacaoView.listaVeiculosContainer.setVisible(true);
            efetuaLocacaoView.locacaoAnalizePanel.setVisible(false);
            efetuaLocacaoView.entregaLocacaoEstimadaInfo.setVisible(true);
        });
    }

    /**
     * @Metodo que finaliza a locação armazenao os dados no sistema.
     */
    private void salvarLocacao() {
        efetuaLocacaoView.finalizarLocacaoButton.addActionListener(al -> {
            SwingWorker swingWorker = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    efetuaLocacaoView.finalizarLocacaoButton.setVisible(false);
                    efetuaLocacaoView.alterarDadosDaLocacao.setVisible(false);
                    locacoes = getLocacoes();
                    veiculos = getVeiculos();

                    alteraVeiculoSituacao();
                    locacao.setAtiva(true);
                    locacao.setCodigo(System.currentTimeMillis());
                    locacoes.add(locacao);
                    tentarSalvarDados();
                    return null;
                }
            };
            swingWorker.execute();

        });
    }

    /**
     * @Metodo que irá tentar salvar os dados da locação,
     * informando se foi ou não bem sucedido o salvamento
     * da informações.
     */
    private void tentarSalvarDados() {
        try {
            savarVeiculo(this.veiculos);
            salvarLocacoes(this.locacoes);
            setResultadoInfo("Locação foi efetuada con sucesso!", GREEN);
        } catch (IOException e) {
            setResultadoInfo("Erro inesperado, tente novamente!", RED);
            e.printStackTrace();
        }
    }

    /**
     * @Metodo para informar o estado final da codastro da locação.
     */
    private void setResultadoInfo(String text, Color green) {
        efetuaLocacaoView.locacaoFinalizadaEstatus.setText(text);
        efetuaLocacaoView.locacaoFinalizadaEstatus.setForeground(green);
        efetuaLocacaoView.locacaoFinalizadaEstatus.setVisible(true);
    }


    /**
     * @Metodo que altera a sitação do veículo, tornando o memso
     * indiponivel para uma nova locação até que a locação atual
     * seja finalizada.
     */
    private void alteraVeiculoSituacao() {
        veiculos.forEach(consummer -> {
            if (consummer.getPlaca().equals(this.veiculo.getPlaca())) {
                consummer.setSituacao(INDISPONIVEL);
            }
        });
    }

    /**
     * @Metodo que mostra os veículos disponiveis para locação.
     */
    private void mostrarListaDeVeiculosDiponiveis() {
        efetuaLocacaoView.veiculosPanel.setLayout(new GridLayout());
        VeiculosController veiculosController = new VeiculosController(uiMainController, true);
        veiculosController.addObserver(this);
        efetuaLocacaoView.veiculosPanel.add(veiculosController.getContainer());
        efetuaLocacaoView.veiculosPanel.revalidate();
    }


    /**
     * @Metodo que mostra os dados do cliente na tela.
     */
    private void mostrarDadosCliente() {
        efetuaLocacaoView.clientePanel.setLayout(new GridLayout());
        efetuaLocacaoView.clientePanel.add(new ClienteFuncionarioTemplateController(uiMainController, cliente).getContainer());
        efetuaLocacaoView.clientePanel.revalidate();
    }

    /**
     * @Metodo que mostra o veículo selecionada ao lado
     * do das informaçoes do cliente.
     */
    private void mostrarVeiculoSelecionado(Veiculo arg) {
        Veiculo veiculo = arg;
        efetuaLocacaoView.veiculoSelecionadoPanel.removeAll();
        efetuaLocacaoView.veiculoSelecionadoPanel.setLayout(new GridLayout());
        VeiculoTemplateController veiculoTemplateController = new VeiculoTemplateController(veiculo);
        veiculoTemplateController.addObserver(this);
        efetuaLocacaoView.veiculoSelecionadoPanel.add(veiculoTemplateController.getMainPanel());
        efetuaLocacaoView.veiculoSelecionadoPanel.revalidate();
    }

    /**
     * @Metodo para configurar a máscara do campo de data para o formato
     * de data Brasileiro do formulário de cadastro de veículo.
     */
    private void setMask() {
        try {
            NumberFormatter formatter = getNumberFormatter(-1, 5000);
            efetuaLocacaoView.kilometragemEstimada = new JFormattedTextField(formatter);

            Insets insets = new Insets(5, 0, 5, 0);
            efetuaLocacaoView.kilometragemEstimada.setFont(efetuaLocacaoView.concluirDados.getFont());
            efetuaLocacaoView.kilometragemEstimada.setMargin(insets);
            efetuaLocacaoView.dataEstimadaEntrega = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("##/##/####")));
            efetuaLocacaoView.kilometragemEstimadaPanel.setLayout(new GridLayout());
            efetuaLocacaoView.kilometragemEstimadaPanel.add(efetuaLocacaoView.kilometragemEstimada);
            efetuaLocacaoView.dataEstimadaEntrega.setMargin(insets);
            efetuaLocacaoView.dataEstimadaEntrega.setFont(efetuaLocacaoView.concluirDados.getFont());
            efetuaLocacaoView.dataEstimadaEntregaPanel.setLayout(new GridLayout());
            efetuaLocacaoView.dataEstimadaEntregaPanel.add(efetuaLocacaoView.dataEstimadaEntrega);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * @Metodo para validar os campos de entrada de informações sobre
     * a data e kilometragem estimada para a entrega do veículo
     * logo apos a perda de seu foco.
     */
    private void setInputValidation() {
        String message = "Campo abrigatório!";
        validacaoTextField(efetuaLocacaoView.kilometragemEstimada, efetuaLocacaoView.kilometragemValidation, message);
        inputValidationMatches(efetuaLocacaoView.dataEstimadaEntrega, efetuaLocacaoView.dataValidation, REGEX_DATA_STRING, "Data inválida!");
    }

    /**
     * @Metodo para validar os campos de entrada de informações sobre
     * a data e kilometragem estimada para a entrega do veículo.
     */
    public boolean isValid() {
        if (!dataValida()) {
            efetuaLocacaoView.dataEstimadaEntrega.setValue("");
        }
        String message = "Campo abrigatório!";
        return inputIsValid(efetuaLocacaoView.kilometragemEstimada, efetuaLocacaoView.kilometragemValidation, message, null)
                && inputIsValid(efetuaLocacaoView.dataEstimadaEntrega, efetuaLocacaoView.dataValidation, "Data inválida!", REGEX_DATA_STRING);
    }

    /**
     * @Metodo que avália se a data estimada para a entrega do
     * veículo e superior a data atual
     */
    private boolean dataValida() {
        LocalDate today = LocalDate.now();
        try {
            return today.isBefore(stringToLocalDate(efetuaLocacaoView.dataEstimadaEntrega.getText()));
        } catch (DateTimeException e) {
            return false;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            String rota = (String) arg;
            if (!this.uiMainController.getUltimoPanel().equals(FAZER_LOCACAO) && rota.equals(FAZER_LOCACAO)) {
                this.uiMainController.changePanel(efetuaLocacaoView.container, FAZER_LOCACAO);
                this.uiMainController.deleteObserver(this);
            }
        } catch (ClassCastException e) {

        }

        try {
            this.veiculo = (Veiculo) arg;
            efetuaLocacaoView.entregaLocacaoEstimadaInfo.setVisible(true);
            mostrarVeiculoSelecionado(veiculo);
        } catch (ClassCastException e) {
        }

    }

}
