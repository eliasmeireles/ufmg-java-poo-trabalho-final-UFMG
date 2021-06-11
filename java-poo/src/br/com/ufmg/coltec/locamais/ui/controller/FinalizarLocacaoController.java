package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.HistoricoLocacao;
import br.com.ufmg.coltec.locamais.model.Locacao;
import br.com.ufmg.coltec.locamais.model.Veiculo;
import br.com.ufmg.coltec.locamais.ui.template.controller.LocacaoTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.FinalizarLocacao;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static br.com.ufmg.coltec.locamais.converter.Converter.LocalDateBRString;
import static br.com.ufmg.coltec.locamais.filemanager.HistoricoLocacaoFileManager.getHistoricoLocacaoFromJsonFile;
import static br.com.ufmg.coltec.locamais.filemanager.HistoricoLocacaoFileManager.saveHistoricoLocacao;
import static br.com.ufmg.coltec.locamais.filemanager.LocacaoFileManager.getLocacoes;
import static br.com.ufmg.coltec.locamais.filemanager.LocacaoFileManager.salvarLocacoes;
import static br.com.ufmg.coltec.locamais.filemanager.VeiculosFileManager.getVeiculos;
import static br.com.ufmg.coltec.locamais.filemanager.VeiculosFileManager.savarVeiculo;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.FINALIZAR_LOCACAO;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.LISTAR_LOCACOES;
import static br.com.ufmg.coltec.locamais.ui.helper.UiHelper.getNumberFormatter;
import static br.com.ufmg.coltec.locamais.ui.helper.UiHelper.valorASerPagorPorKilometragemRodada;
import static br.com.ufmg.coltec.locamais.ui.helper.VeiculoSituacao.DISPONIVEL;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.inputIsValid;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.validacaoTextField;
import static br.com.ufmg.coltec.locamais.ui.message.ShowMessage.messageShow;

public class FinalizarLocacaoController implements Observer {

    private UIMainController uiMainController;
    private Locacao locacao;
    private List<Veiculo> veiculos;
    private List<HistoricoLocacao> historicoLocacaos;
    private List<Locacao> locacaos;
    private FinalizarLocacao finalizarLocacaoView;

    public FinalizarLocacaoController(UIMainController uiMainController, Locacao locacao) {
        this.uiMainController = uiMainController;
        this.finalizarLocacaoView = new FinalizarLocacao();
        this.locacao = locacao;
        carregarDados(locacao);
    }

    /**
     * @Metodo que carrega todos do dados e componentes da tela.
     */
    private void carregarDados(Locacao locacao) {
        this.uiMainController.mostrarBarraDeProgresso();
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() {

                finalizarLocacaoView.locacaoPanel.setLayout(new GridLayout());
                finalizarLocacaoView.locacaoPanel.add(new LocacaoTemplateController(uiMainController, locacao, true, false).getContainer());
                setMask();
                setInputValidation();

                finalizarLocacao();
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
     * @Metodo que efetuara a finalização da locação caso as
     * informações estejam corretas.
     */
    private void finalizarLocacao() {
        finalizarLocacaoView.finalizarLocacao.addActionListener(al -> {
            if (isValid()) {
                SwingWorker swingWorker = new SwingWorker() {
                    @Override
                    protected Object doInBackground() {
                        veiculos = getVeiculos();
                        HistoricoLocacao historicoLocacao = new HistoricoLocacao();
                        historicoLocacao.setDataDaDevolucao(LocalDateBRString(LocalDate.now()));
                        historicoLocacao.setDescricao(finalizarLocacaoView.descricaoDeDevolucaoDoVeiculo.getText().trim());
                        historicoLocacao.setKilometragemUtilizada(Integer.parseInt(String.valueOf(finalizarLocacaoView.kilometragemUtilizada.getValue())));
                        String valorAdicionalText = finalizarLocacaoView.valorAdicional.getText();

                        if (valorAdicionalText.isEmpty()) {
                            valorAdicionalText = "0";
                        }

                        historicoLocacao.setValorAdcional(Double.parseDouble(valorAdicionalText));
                        historicoLocacao.setLocacao(locacao);

                        double valorFinalAPagar = getValorFinalAPagar(historicoLocacao);
                        historicoLocacao.setValorFinalPago(valorFinalAPagar);
                        historicoLocacaos = getHistoricoLocacaoFromJsonFile();
                        historicoLocacaos.add(historicoLocacao);

                        try {
                            saveHistoricoLocacao(historicoLocacaos);
                            locacaos = getLocacoes();
                            locacao.setAtiva(false);

                            removerLocacao();

                            alteraSistuacaoVeiculo();

                            savarVeiculo(veiculos);
                            salvarLocacoes(locacaos);
                            messageShow(uiMainController.getMainFrame(), "A locação foi finalizada com sucesso!");
                            new LocacoesController(uiMainController);
                            uiMainController.notifyObservers(LISTAR_LOCACOES);

                        } catch (IOException e) {
                            e.printStackTrace();
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

            }
        });
    }

    /**
     * @Metodo que altera o estado da locação para
     * inativa.
     */
    private void removerLocacao() {
        int[] indexToBeRemoved = {-1};
        this.locacaos.forEach(consumer -> {
            if (locacao.getCodigo().equals(consumer.getCodigo())) {
                indexToBeRemoved[0] = locacaos.indexOf(consumer);
                return;
            }
        });

        if (indexToBeRemoved[0] != -1) {
            locacaos.remove(indexToBeRemoved[0]);
        }
    }

    /**
     * @Metodo que altera a situação do veículo para disponível
     * caso não haja erros ao salvar o histórico da locação.
     */
    private void alteraSistuacaoVeiculo() {
        this.veiculos.forEach(consumer -> {
            if (locacao.getVeiculo().getPlaca().equals(consumer.getPlaca())) {
                consumer.setSituacao(DISPONIVEL);
                consumer.setKilometragem(consumer.getKilometragem() + Integer.parseInt(finalizarLocacaoView.kilometragemUtilizada.getText()));
                return;
            }
        });
    }

    /**
     * @Metodo que retorna o valor final a ser pago.
     **/
    private double getValorFinalAPagar(HistoricoLocacao historicoLocacao) {
        double valorASerPagorPorKilometragemRodada = valorASerPagorPorKilometragemRodada(historicoLocacao.getKilometragemUtilizada(), locacao.getVeiculo());

        String valorAdicionarInformado = finalizarLocacaoView.valorAdicional.getText();
        if (valorAdicionarInformado == null || valorAdicionarInformado.isEmpty()) {
            return valorASerPagorPorKilometragemRodada;
        }

        return valorASerPagorPorKilometragemRodada + Double.parseDouble(valorAdicionarInformado);
    }

    /**
     * @Metodo para validar os campos de entrada de informações sobre
     * a data e kilometragem utilizada para a entrega do veículo.
     */
    public boolean isValid() {
        String message = "Campo abrigatório!";
        return inputIsValid(finalizarLocacaoView.kilometragemUtilizada, finalizarLocacaoView.kilometragemUtilizadaValidation, message, null);
    }

    /**
     * @Metodo para validar os campos de entrada de informações sobre
     * a kilometragem utilizada para a entrega do veículo
     * logo apos a perda de seu foco.
     */
    private void setInputValidation() {
        String message = "Campo abrigatório!";
        validacaoTextField(finalizarLocacaoView.kilometragemUtilizada, finalizarLocacaoView.kilometragemUtilizadaValidation, message);
    }


    /**
     * @Metodo para criar os campos de informações adicionais sobre
     * a entrega do veículo.
     */
    private void setMask() {
        NumberFormatter formatter = getNumberFormatter(-1, 99999);
        Font font = finalizarLocacaoView.finalizarLocacao.getFont();
        Insets insets = new Insets(5, 0, 5, 0);
        finalizarLocacaoView.kilometragemUtilizada = new JFormattedTextField(formatter);
        finalizarLocacaoView.valorAdicional = new JFormattedTextField(formatter);
        finalizarLocacaoView.kilometragemUtilizada.setFont(font);
        finalizarLocacaoView.kilometragemUtilizada.setMargin(insets);
        finalizarLocacaoView.kilometragemUtilizadaPanel.setLayout(new GridLayout());
        finalizarLocacaoView.kilometragemUtilizadaPanel.add(finalizarLocacaoView.kilometragemUtilizada);
        finalizarLocacaoView.valorAdicional.setFont(font);
        finalizarLocacaoView.valorAdicional.setMargin(insets);
        finalizarLocacaoView.valorAdicionalPanel.setLayout(new GridLayout());
        finalizarLocacaoView.valorAdicionalPanel.add(finalizarLocacaoView.valorAdicional);
        finalizarLocacaoView.descricaoDeDevolucaoDoVeiculo.setFont(font);
    }

    @Override
    public void update(Observable o, Object arg) {
        String rota = (String) arg;
        if (rota.equals(FINALIZAR_LOCACAO)) {
            this.uiMainController.changePanel(finalizarLocacaoView.container, FINALIZAR_LOCACAO);
            this.uiMainController.deleteObserver(this);
        }
    }
}
