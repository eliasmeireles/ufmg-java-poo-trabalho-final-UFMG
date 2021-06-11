package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.Locacao;
import br.com.ufmg.coltec.locamais.ui.template.LocacaoTemplate;
import br.com.ufmg.coltec.locamais.ui.template.controller.LocacaoTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.Locacoes;

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
import static br.com.ufmg.coltec.locamais.filemanager.LocacaoFileManager.getLocacoes;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.LISTAR_LOCACOES;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_CPF;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.inputIsValid;

public class LocacoesController implements Observer {

    private UIMainController uiMainController;
    private List<Locacao> locacoes;
    private String ultimoCpfLocalizado;
    private Locacoes locacoesView;

    public LocacoesController(UIMainController uiMain) {
        this.uiMainController = uiMain;
        this.locacoesView = new Locacoes();
        this.uiMainController.addObserver(this);
        this.ultimoCpfLocalizado = "";
    }

    /**
     * @Metodo que atualizar a lista de locações fazendo uma nova pesquisa por dados e mostrando na tela.
     */
    private void atualizar() {
        locacoesView.atualizarLista.addActionListener(al -> {
            locacoesView.clienteCpfValidation.setText("");
            locacoesView.clienteNaoEncotradoInfo.setText("");
            locacoesView.clienteNaoEncotradoInfo.setVisible(false);
            locacoesView.clienteCpf.setValue("");
            locacoesView.clienteCpf.requestFocus();
            ultimoCpfLocalizado = "";
            selecionarLocacoes();
        });
    }

    /**
     * @Metodo que filtra o CPF de um cliente dentro da lista de locações já cadastrados no sistema.
     */
    private void fazerFiltroPorCpf() {
        locacoesView.filtrarCliente.addActionListener(al -> {

            if (isCpfValid()) {
                new SwingWorker() {

                    @Override
                    protected Object doInBackground() {
                        locacoesView.clienteCpfValidation.setText("");
                        if (!ultimoCpfLocalizado.equals(locacoesView.clienteCpf.getText())) {
                            ArrayList<Locacao> listaDeLocacoes = new ArrayList<>();
                            locacoes.forEach(consumer -> {
                                if (locacoesView.clienteCpf.getText().equals(consumer.getCliente().getCpf())) {
                                    listaDeLocacoes.add(consumer);
                                }
                            });
                            if (listaDeLocacoes.size() > 0) {
                                setListaDeLocacoes(listaDeLocacoes);
                                locacoesView.clienteNaoEncotradoInfo.setText("");
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
        locacoesView.clienteNaoEncotradoInfo.setForeground(RED);
        locacoesView.clienteNaoEncotradoInfo.setText("Nenhum registro encontrado!");
    }

    /**
     * @Metodo que insere um JFormattedTextField na tela para inserir o CPF desejado.
     */
    private void setFormattedTextField() {
        try {
            Font font = locacoesView.labelCPF.getFont();
            locacoesView.clienteCpfFiltroPanel.setLayout(new GridLayout());
            locacoesView.clienteCpf = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("###.###.###-##")));
            locacoesView.clienteCpf.setMargin(new Insets(3, 0, 3, 0));
            locacoesView.atualizarLista.setMargin(new Insets(3, 0, 3, 0));
            locacoesView.clienteCpf.setFont(font);
            locacoesView.clienteCpfFiltroPanel.add(locacoesView.clienteCpf);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Metodo apenas para validação do CPF informado.
     */
    private boolean isCpfValid() {
        return inputIsValid(locacoesView.clienteCpf, locacoesView.clienteCpfValidation, "Cpf inválido!", REGEX_CPF);
    }

    /**
     * @Metodo que percorre a lista de locações e as
     * mostram na tela, o carregamento dos dados na tela
     * seram efetuados em background, sendo assim, não
     * havera o congelamento da tela caso a lista de
     * locações seja muito grande.
     */
    private void setListaDeLocacoes(List<Locacao> locacoesList) {
        uiMainController.mostrarBarraDeProgresso();
        SwingWorker<Object, Object> swingWorker = new SwingWorker<Object, Object>() {

            @Override
            protected Object doInBackground() {
                locacoesView.locacoesPanel.removeAll();
                locacoesView.locacoesPanel.setLayout(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridy = 0;
                double[] counter = {0};
                locacoesList.forEach(consumer -> {

                    try {
                        Thread.sleep(125);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (counter[0] == 1) {
                        constraints.gridy++;
                        constraints.gridx = 0;
                        counter[0] = 0;
                    } else {
                        constraints.gridx++;
                    }

                    if (locacoesList.indexOf(consumer) == locacoesList.size() - 1) {
                        getLocacao(constraints, consumer, false);
                    } else {
                        getLocacao(constraints, consumer, true);
                    }
                    counter[0]++;

                });
                return null;
            }

            @Override
            protected void done() {
                super.done();
                locacoesView.locacoesPanel.revalidate();
                uiMainController.esconderBarraDeProgresso();
            }
        };

        swingWorker.execute();
    }

    private void getLocacao(GridBagConstraints constraints, Locacao consumer, boolean b) {
        LocacaoTemplateController locacaoTemplateController = new LocacaoTemplateController(this.uiMainController, consumer, b, true);
        locacoesView.locacoesPanel.add(locacaoTemplateController.getContainer(), constraints);
    }

    @Override
    public void update(Observable o, Object arg) {
        String rota = (String) arg;
        if (!this.uiMainController.getUltimoPanel().equals(LISTAR_LOCACOES) && rota.equals(LISTAR_LOCACOES)) {
            atualizar();
            fazerFiltroPorCpf();
            setFormattedTextField();
            selecionarLocacoes();
            this.uiMainController.changePanel(locacoesView.container, LISTAR_LOCACOES);
            this.uiMainController.deleteObserver(this);
        }
    }

    private void selecionarLocacoes() {
        new SwingWorker() {

            @Override
            protected Object doInBackground() {
                locacoes = getLocacoes();
                if (locacoes.size() == 0) {
                    dadosNaoEncontrados();
                }
                setListaDeLocacoes(locacoes);
                return null;
            }
        }.execute();
    }
}
