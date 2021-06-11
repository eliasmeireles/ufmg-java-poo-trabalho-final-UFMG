package br.com.ufmg.coltec.locamais.ui.controller;

import br.com.ufmg.coltec.locamais.model.Fabricante;
import br.com.ufmg.coltec.locamais.ui.view.CadastrarFabricantes;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static br.com.ufmg.coltec.locamais.filemanager.FabricanteFileManager.saveFrabricantes;
import static br.com.ufmg.coltec.locamais.ui.constant.SystemRoute.FABRICANTE_FORMULARIO_CADASTRO;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_NOME_FABRICANTES;
import static br.com.ufmg.coltec.locamais.ui.message.ShowMessage.messageShow;

public class CadastrarFabricantesController implements Observer {

    private CadastrarFabricantes cadastarFabricantesView;
    private List<Fabricante> fabricantes;
    private UIMainController uiMainController;

    public CadastrarFabricantesController(UIMainController uiMainController) {
        this.uiMainController = uiMainController;
        this.cadastarFabricantesView = new CadastrarFabricantes();
        this.uiMainController.addObserver(this);
        this.fabricantes = new ArrayList<>();
        cadastarFabricantes();

    }

    /**
     * @Metodo que salva os seguintes nome de fabricantes no sistema.
     */
    private void cadastarFabricantes() {
        cadastarFabricantesView.cadastrarFabricantesButton.addActionListener(al -> {
            uiMainController.mostrarBarraDeProgresso();
            SwingWorker swingWorker = new SwingWorker() {
                @Override
                protected Object doInBackground() {

                    String[] split = cadastarFabricantesView.fabricantesNomes.getText().split(",");
                    ArrayList<String> errorsFound = new ArrayList<>();

                    for (int i = 0; i < split.length; i++) {
                        Fabricante fabricante = new Fabricante();
                        String trim = split[i].trim();
                        if (!trim.isEmpty() && trim.length() < 20 && trim.matches(REGEX_NOME_FABRICANTES)) {
                            fabricante.setNome(trim);
                            fabricantes.add(fabricante);
                        } else {
                            errorsFound.add(trim);
                        }
                    }


                    if (errorsFound.size() == 0) {
                        try {
                            saveFrabricantes(fabricantes);
                            cadastarFabricantesView.fabricantesNomes.setText("");
                            cadastarFabricantesView.fabricantesNomes.requestFocus();
                            messageShow(uiMainController.getMainFrame(), "Fabricantes salvos com sucesso.");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        messageShow(uiMainController.getMainFrame(), "Falha ao na validação do dados informados "
                                .concat(errorsFound.toString())
                                .concat(", caracteres especiais não são permitidos!"));
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

        });
    }


    @Override
    public void update(Observable o, Object arg) {
        String rota = (String) arg;
        if (!this.uiMainController.getUltimoPanel().equals(FABRICANTE_FORMULARIO_CADASTRO) && rota.equals(FABRICANTE_FORMULARIO_CADASTRO)) {
            this.uiMainController.changePanel(cadastarFabricantesView.container, FABRICANTE_FORMULARIO_CADASTRO);
            this.uiMainController.deleteObserver(this);
        }
    }
}
