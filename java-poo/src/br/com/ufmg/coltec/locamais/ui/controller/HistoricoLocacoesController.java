package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.HistoricoLocacao;
import br.com.ufmg.coltec.locamais.ui.template.HistoricoLocacaoTemplate;
import br.com.ufmg.coltec.locamais.ui.template.controller.HistoricoLocacaoTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.HistoricoLocacoes;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static br.com.ufmg.coltec.locamais.color.Colors.RED;
import static br.com.ufmg.coltec.locamais.filemanager.HistoricoLocacaoFileManager.getHistoricoLocacaoFromJsonFile;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.HISTORICO_LOCACAO;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_CPF;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.inputIsValid;

public class HistoricoLocacoesController implements Observer {
    private HistoricoLocacoes historicoLocacaosView;
    private UIMainController uiMainController;
    private List<HistoricoLocacao> historicoLocacaos;
    private String ultimoCpfLocalizado;


    public HistoricoLocacoesController(UIMainController uiMainController) {
        this.uiMainController = uiMainController;
        this.historicoLocacaosView = new HistoricoLocacoes();
        this.uiMainController.addObserver(this);
        this.ultimoCpfLocalizado = "";

        atualizar();
        fazerFiltroPorCpf();
        setFormattedTextField();
    }

    /**
     * @Metodo que atualizar a lista de histórico fazendo uma nova pesquisa por dados e mostrando na tela.
     */
    private void atualizar() {
        historicoLocacaosView.atualizarLista.addActionListener(al -> {
            historicoLocacaosView.clienteCpfValidation.setText("");
            historicoLocacaosView.clienteNaoEncotradoInfo.setText("");
            historicoLocacaosView.clienteNaoEncotradoInfo.setVisible(false);
            historicoLocacaosView.clienteCpf.setValue("");
            historicoLocacaosView.clienteCpf.requestFocus();
            this.ultimoCpfLocalizado = "";
            localizarHistoricos();
        });
    }

    /**
     * @Metodo que filtra o CPF de um cliente dentro da lista de histórico já cadastrados no sistema.
     */
    private void fazerFiltroPorCpf() {
        historicoLocacaosView.filtrarCliente.addActionListener(al -> {

            if (isCpfValid()) {

                new SwingWorker() {

                    @Override
                    protected Object doInBackground() {
                        historicoLocacaosView.clienteCpfValidation.setText("");
                        if (!ultimoCpfLocalizado.equals(historicoLocacaosView.clienteCpf.getText())) {
                            ArrayList<HistoricoLocacao> locacaoArrayList = new ArrayList<>();
                            historicoLocacaos.forEach(consumer -> {
                                if (historicoLocacaosView.clienteCpf.getText().equals(consumer.getLocacao().getCliente().getCpf())) {
                                    locacaoArrayList.add(consumer);
                                }
                            });
                            if (locacaoArrayList.size() > 0) {
                                carregarListaNaTela(locacaoArrayList);
                                historicoLocacaosView.clienteNaoEncotradoInfo.setText("");
                            } else {
                                dadosNaoEncontrados();
                            }
                        }
                        return null;
                    }
                }.execute();
            }

        });
    }

    /**
     * @Metodo que informa que não ha dados encontrados.
     * */
    private void dadosNaoEncontrados() {
        historicoLocacaosView.clienteNaoEncotradoInfo.setForeground(RED);
        historicoLocacaosView.clienteNaoEncotradoInfo.setText("Nenhum registro encontrado!");
    }

    /**
     * @Metodo que insere um JFormattedTextField na tela para inserir o CPF desejado.
     */
    private void setFormattedTextField() {
        try {
            Font font = historicoLocacaosView.labelCPF.getFont();
            historicoLocacaosView.clienteCpfFiltroPanel.setLayout(new GridLayout());
            historicoLocacaosView.clienteCpf = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("###.###.###-##")));
            historicoLocacaosView.clienteCpf.setMargin(new Insets(3, 0, 3, 0));
            historicoLocacaosView.atualizarLista.setMargin(new Insets(3, 0, 3, 0));
            historicoLocacaosView.clienteCpf.setFont(font);
            historicoLocacaosView.clienteCpfFiltroPanel.add(historicoLocacaosView.clienteCpf);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Metodo apenas para validação do CPF informado.
     */
    private boolean isCpfValid() {
        return inputIsValid(historicoLocacaosView.clienteCpf, historicoLocacaosView.clienteCpfValidation, "Cpf inválido!", REGEX_CPF);
    }

    /**
     * @Metodo que localiza a lista de histório de locações
     * e carrega na tela suas informações.
     */
    private void carregarListaNaTela(List<HistoricoLocacao> historicoLocacaoList) {
        this.uiMainController.mostrarBarraDeProgresso();
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                historicoLocacaosView.historicoLocacaoPanel.removeAll();
                historicoLocacaosView.historicoLocacaoPanel.setLayout(new GridLayout(historicoLocacaoList.size(), 1));
                historicoLocacaoList.forEach(historicoLocacao -> {
                    if (historicoLocacaoList.indexOf(historicoLocacao) == historicoLocacaoList.size() - 1) {
                        historicoLocacaosView.historicoLocacaoPanel.add(new HistoricoLocacaoTemplateController(uiMainController, historicoLocacao, false).getContainer());
                    } else {
                        historicoLocacaosView.historicoLocacaoPanel.add(new HistoricoLocacaoTemplateController(uiMainController, historicoLocacao, true).getContainer());
                    }
                    historicoLocacaosView.historicoLocacaoPanel.revalidate();
                    try {
                        Thread.sleep(125);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                });
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

    @Override
    public void update(Observable o, Object arg) {
        String rota = (String) arg;
        if (!this.uiMainController.getUltimoPanel().equals(HISTORICO_LOCACAO) && rota.equals(HISTORICO_LOCACAO)) {
            localizarHistoricos();
            this.uiMainController.changePanel(historicoLocacaosView.container, HISTORICO_LOCACAO);
            this.uiMainController.deleteObserver(this);
        }
    }

    private void localizarHistoricos() {
        new SwingWorker() {
            @Override
            protected Object doInBackground() {
                historicoLocacaos = getHistoricoLocacaoFromJsonFile();
                if (historicoLocacaos.size() == 0) {
                    dadosNaoEncontrados();
                }
                carregarListaNaTela(historicoLocacaos);
                return null;
            }
        }.execute();
    }

}
