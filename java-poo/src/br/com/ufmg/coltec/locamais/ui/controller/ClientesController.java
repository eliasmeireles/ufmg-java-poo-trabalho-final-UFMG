package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.Cliente;
import br.com.ufmg.coltec.locamais.ui.template.ClienteFuncionarioTemplate;
import br.com.ufmg.coltec.locamais.ui.template.controller.ClienteFuncionarioTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.Clientes;
import br.com.ufmg.coltec.locamais.ui.view.EfetuaLocacao;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.Document;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static br.com.ufmg.coltec.locamais.color.Colors.RED;
import static br.com.ufmg.coltec.locamais.filemanager.ClienteFileManager.getClientes;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.CLIENTE_LISTAR;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.FAZER_LOCACAO;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_CPF;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.inputIsValid;

public class ClientesController implements Observer {

    private List<Cliente> clientes;
    private Clientes clientesView;
    private UIMainController uiMainController;
    private String ultimoCpfLocalizado;


    public ClientesController(UIMainController uiMainController) {
        clientesView = new Clientes();
        this.uiMainController = uiMainController;
        this.uiMainController.addObserver(this);
        this.clientesView.clienteNaoEncotradoInfo.setForeground(RED);
        this.clientesView.atualizarLista.setMargin(new Insets(3, 0, 3, 0));
        this.ultimoCpfLocalizado = "";
        this.clientes = getClientes();
        setClientes(this.clientes);
        setFormattedTextField();
        fazerFiltroPorCpf();
        atualizar();
    }


    /**
     * @Metodo que atualizar a lista de clientes fazendo uma nova pesquisa por dados e mostrando na tela.
     */
    private void atualizar() {
        this.clientesView.atualizarLista.addActionListener(al -> {
            this.clientesView.clienteCpfValidation.setText("");
            this.clientesView.clienteNaoEncotradoInfo.setText("");
            this.clientesView.clienteNaoEncotradoInfo.setVisible(false);
            this.clientesView.clienteCpf.setValue("");
            this.clientesView.clienteCpf.requestFocus();
            this.ultimoCpfLocalizado = "";
            setClientes(null);
        });
    }

    /**
     * @Metodo que filtra o CPF de um cliente dentro da lista de clientes já cadastrados no sistema.
     */
    private void fazerFiltroPorCpf() {
        this.clientesView.filtrarCliente.addActionListener(al -> {
            if (isCpfValid()) {
                uiMainController.mostrarBarraDeProgresso();
                new SwingWorker() {
                    @Override
                    protected Object doInBackground() {
                        clientesView.clienteCpfValidation.setText("");
                        if (!ultimoCpfLocalizado.equals(clientesView.clienteCpf.getText())) {
                            ArrayList<Cliente> arrayListClientes = new ArrayList<>();
                            clientes.forEach(consumer -> {
                                if (clientesView.clienteCpf.getText().equals(consumer.getCpf())) {
                                    arrayListClientes.add(consumer);
                                    return;
                                }
                            });
                            if (arrayListClientes.size() > 0) {
                                setClientes(arrayListClientes);
                               clientesView.clienteNaoEncotradoInfo.setText("");
                            } else {
                                dadosNaoEncontrados();
                            }
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        super.done();
                        uiMainController.esconderBarraDeProgresso();
                    }
                }.execute();
            }

        });
    }

    /**
     * @Metodo que informa que não ha dados encontrados.
     */
    private void dadosNaoEncontrados() {
        clientesView.clienteNaoEncotradoInfo.setForeground(RED);
        clientesView.clienteNaoEncotradoInfo.setText("Nenhum cliente encontrado!");
    }

    /**
     * @Metodo que insere um JFormattedTextField na tela para inserir o CPF desejado.
     */
    private void setFormattedTextField() {
        try {
            this.clientesView.clienteCpfFiltroPanel.setLayout(new GridLayout());
            this.clientesView.clienteCpf = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("###.###.###-##")));
            this.clientesView.clienteCpf.setMargin(new Insets(3, 0, 3, 0));
            Font font = this.clientesView.labelCPF.getFont();
            this.clientesView.clienteCpf.setFont(font);
            this.clientesView.clienteCpfFiltroPanel.add(this.clientesView.clienteCpf);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /**
     * @Metodo apenas para validação do CPF informado.
     */
    private boolean isCpfValid() {
        return inputIsValid(this.clientesView.clienteCpf, clientesView.clienteCpfValidation, "Cpf inválido!", REGEX_CPF);
    }


    /**
     * @metodo que execulta em uma nova Thread o carregamento da lista de clientes
     * caso seu parametro seja passado como null.
     */
    private void setClientes(java.util.List<Cliente> clienteList) {
        uiMainController.mostrarBarraDeProgresso();
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Document doInBackground() {
                List<Cliente> clientes = clienteList;
                if (clienteList == null) {
                    clientes = getClientes();
                }
                clientesView.clientesContainer.removeAll();
                clientesView.clientesContainer.setLayout(new GridBagLayout());
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridy = 0;
                double[] counter = {0};
                clientes.forEach(cliente -> {
                    try {
                        Thread.sleep(125);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (counter[0] == 2) {
                        constraints.gridy++;
                        constraints.gridx = 0;
                        counter[0] = 0;
                    } else {
                        constraints.gridx++;
                    }
                    JPanel container = new ClienteFuncionarioTemplateController(uiMainController, cliente).getContainer();
                    container.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            super.mouseClicked(e);
                            new EfetuaLocacaoController(uiMainController, cliente);
                            uiMainController.notifyObservers(FAZER_LOCACAO);
                        }
                    });
                    clientesView.clientesContainer.add(container, constraints);
                    counter[0]++;
                });

                if (clientes.size() == 0) {
                    dadosNaoEncontrados();
                }
                return null;
            }

            @Override
            protected void done() {
                super.done();
                clientesView.clientesContainer.revalidate();
                uiMainController.esconderBarraDeProgresso();
            }
        };

        swingWorker.execute();
    }


    @Override
    public void update(Observable o, Object arg) {
        String rota = (String) arg;
        if (!this.uiMainController.getUltimoPanel().equals(CLIENTE_LISTAR) && rota.equals(CLIENTE_LISTAR)) {
            this.uiMainController.changePanel(this.clientesView.container, CLIENTE_LISTAR);
            this.uiMainController.deleteObserver(this);
        }
    }
}
