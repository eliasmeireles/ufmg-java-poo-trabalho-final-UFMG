package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.Fabricante;
import br.com.ufmg.coltec.locamais.model.Veiculo;
import br.com.ufmg.coltec.locamais.ui.template.controller.VeiculoTemplateController;
import br.com.ufmg.coltec.locamais.ui.view.Veiculos;
import br.com.ufmg.coltec.locamais.ui.template.VeiculoTemplate;

import javax.swing.*;
import javax.swing.text.Document;
import java.awt.*;
import java.util.*;
import java.util.List;

import static br.com.ufmg.coltec.locamais.color.Colors.RED;
import static br.com.ufmg.coltec.locamais.filemanager.FabricanteFileManager.getFabricantesFromJsonFile;
import static br.com.ufmg.coltec.locamais.filemanager.VeiculosFileManager.getVeiculos;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.LISTAR_VEICULOS;
import static br.com.ufmg.coltec.locamais.ui.helper.VeiculoSituacao.DISPONIVEL;

public class VeiculosController extends Observable implements Observer {

    private Veiculos veiculosView;
    private UIMainController uiMainController;
    private List<Veiculo> veiculos;
    private List<Veiculo> backUpVeiculos;
    private boolean mostrarApenasDisponiveis;
    private boolean validateFilter;

    public VeiculosController(UIMainController uiMainController, boolean mostrarApenasDisponiveis) {
        this.uiMainController = uiMainController;
        this.mostrarApenasDisponiveis = mostrarApenasDisponiveis;
        this.uiMainController.addObserver(this);
        veiculosView = new Veiculos();
        incializar();
    }

    /**
     * @Metodo que incializa os componentes do painel
     * para listar os veículos já cadastrados no sistema.
     */
    private void incializar() {
        this.validateFilter = false;
        this.veiculosView.veiculoFiltroContainer.setVisible(false);
        this.veiculosView.veiculosNaoEncontrados.setForeground(RED);
        this.veiculosView.veiculosNaoEncontrados.setVisible(false);
        setFabricantesOnComboBox();

        mostrarVeiculosCadastrados();

        veiculoFiltroPorFabricante();
    }

    /**
     * @Metodo que analiza a qual tipo de lista de veículo deverá ser mostrada,
     * sendo todos os veículos ou apenas os disponivéis para locação.
     */
    private void mostrarVeiculosCadastrados() {
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                veiculos = getVeiculos();
                if (!mostrarApenasDisponiveis) {
                    setVeiculosOnView(veiculos);
                } else {
                    ArrayList<Veiculo> veiculosDisponiveis = new ArrayList<>();
                    veiculos.forEach(consumer -> {
                        if (consumer.getSituacao().equals(DISPONIVEL)) {
                            veiculosDisponiveis.add(consumer);
                        }
                    });
                    setVeiculosOnView(veiculosDisponiveis);
                }
                return null;
            }

            @Override
            protected void done() {
                super.done();
            }
        };
        swingWorker.execute();
    }

    /**
     * @Metodo que efetua um filtro de acordo com o nome do
     * fabricante escolhido pelo usuário.
     */
    private void veiculoFiltroPorFabricante() {
        this.veiculosView.fabricantesFilter.addActionListener(ac -> {
            SwingWorker swingWorker = new SwingWorker() {
                @Override
                protected Object doInBackground() {
                    if (veiculosView.fabricantesFilter.getSelectedIndex() > 0) {
                        validateFilter = true;
                        backUpVeiculos = new ArrayList<>();
                        String fabrincante = veiculosView.fabricantesFilter.getSelectedItem().toString();

                        veiculos.forEach(veiculo -> {
                            if (veiculo.getFabricante().getNome().equals(fabrincante)) {
                                if (mostrarApenasDisponiveis) {
                                    if (veiculo.getSituacao().equals(DISPONIVEL)) {
                                        backUpVeiculos.add(veiculo);
                                    }
                                } else {
                                    backUpVeiculos.add(veiculo);
                                }
                            }
                        });
                        if (backUpVeiculos.size() > 0) {
                            veiculosView.veiculosNaoEncontrados.setVisible(false);
                        } else {
                            veiculosView.veiculosNaoEncontrados.setVisible(true);
                        }
                        setVeiculosOnView(backUpVeiculos);
                    } else {
                        if (validateFilter) {
                            setVeiculosOnView(veiculos);
                        }
                        validateFilter = false;
                    }
                    return null;
                }
            };
            swingWorker.execute();
        });
    }

    /**
     * @Metodo que localiza e disponibiliza em um dropdown
     * todos os nome de fabricantes cadastrados no sistema.
     */
    private void setFabricantesOnComboBox() {
        SwingWorker swingWorker = new SwingWorker() {
            @Override
            protected Object doInBackground() {
                veiculosView.fabricantesFilter.addItem("Mostrar todos");
                List<Fabricante> fabricantesFromJsonFile = getFabricantesFromJsonFile();
                Collections.sort(fabricantesFromJsonFile);
                fabricantesFromJsonFile.forEach(fabricante -> veiculosView.fabricantesFilter.addItem(fabricante.getNome()));
                return null;
            }

            @Override
            protected void done() {
                super.done();
            }
        };
        swingWorker.execute();
    }

    /**
     * @Metodo que mostra na tela todos os veículos passados por parametro
     * do tipo List<Veiculo>, efetuando o carregamento da lista em segundo plano,
     * ou seja, será execultado em uma nova Thread sem causar o congelamento da aplicação.
     */
    private void setVeiculosOnView(List<Veiculo> veiculos) {
        uiMainController.mostrarBarraDeProgresso();
        if (veiculos.size() > 0) {
            veiculosView.veiculosNaoEncontrados.setVisible(false);
        } else {
            veiculosView.veiculosNaoEncontrados.setVisible(true);
        }
        SwingWorker worker = new SwingWorker<Object, Object>() {
            final int[] counter = {0};

            @Override
            public Document doInBackground() {
                veiculosView.listConainter.removeAll();
                if (veiculos.size() > 0) {
                    veiculosView.waitingInfo.setVisible(true);
                    veiculosView.listConainter.setLayout(new GridBagLayout());
                    GridBagConstraints constraints = new GridBagConstraints();
                    constraints.gridy = 0;
                    veiculos.forEach(veiculo -> {
                        try {
                            Thread.sleep(125);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (counter[0] == 3) {
                            constraints.gridy++;
                            constraints.gridx = 0;
                            counter[0] = 0;
                        } else {
                            constraints.gridx++;
                        }
                        VeiculoTemplateController veiculoTemplateController = new VeiculoTemplateController(veiculo);
                        veiculoTemplateController.addObserver(VeiculosController.this);
                        veiculosView.listConainter.add(veiculoTemplateController.getMainPanel(), constraints);
                        veiculosView.listConainter.revalidate();
                        counter[0]++;
                    });
                }
                return null;
            }

            @Override
            public void done() {
                super.done();
                veiculosView.waitingInfo.setVisible(false);
                veiculosView.veiculoFiltroContainer.setVisible(true);
                uiMainController.esconderBarraDeProgresso();

            }
        };

        // Call the SwingWorker from within the Swing thread
        worker.execute();
    }

    /**
     * @Metodo que retorna o painel carregado com as informações
     * carregadas.
     */
    public JPanel getContainer() {
        return veiculosView.container;
    }

    @Override
    public synchronized void addObserver(Observer observer) {
        super.addObserver(observer);
    }

    @Override
    public void notifyObservers(Object arg) {
        super.setChanged();
        super.notifyObservers(arg);
    }

    @Override
    public void update(Observable o, Object arg) {
        try {
            String rota = (String) arg;
            if (!this.uiMainController.getUltimoPanel().equals(LISTAR_VEICULOS) && rota.equals(LISTAR_VEICULOS)) {
                this.uiMainController.changePanel(veiculosView.container, LISTAR_VEICULOS);
                this.uiMainController.deleteObserver(this);
            }
        } catch (ClassCastException e) {

        }
        try {
            notifyObservers(arg);
        } catch (ClassCastException e) {

        }
    }

}
