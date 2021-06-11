package br.com.ufmg.coltec.locamais.ui.template.controller;

import br.com.ufmg.coltec.locamais.model.HistoricoLocacao;
import br.com.ufmg.coltec.locamais.ui.controller.UIMainController;
import br.com.ufmg.coltec.locamais.ui.template.HistoricoLocacaoTemplate;
import br.com.ufmg.coltec.locamais.ui.template.LocacaoTemplate;

import javax.swing.*;
import java.awt.*;

import static br.com.ufmg.coltec.locamais.color.Colors.SEPARADOR;
import static br.com.ufmg.coltec.locamais.converter.Converter.doubleToRealString;

public class HistoricoLocacaoTemplateController {

    private UIMainController uiMainController;
    private HistoricoLocacaoTemplate historicoLocacaoTemplateView;
    private HistoricoLocacao historicoLocacao;
    private boolean mostrarSeparador;

    public HistoricoLocacaoTemplateController(UIMainController uiMainController, HistoricoLocacao historicoLocacao, boolean mostrarSeparador) {
        this.uiMainController = uiMainController;
        this.historicoLocacao = historicoLocacao;
        this.mostrarSeparador = mostrarSeparador;
        this.historicoLocacaoTemplateView = new HistoricoLocacaoTemplate();
        historicoLocacaoTemplateView.separador.setBackground(SEPARADOR);
        historicoLocacaoTemplateView.separador.setVisible(false);
        preencherDadosNaTela();
    }

    /**
     * @Metodo para preencher os dados do histórico de
     * locações finalizadas no sistema.
     */
    private void preencherDadosNaTela() {
        historicoLocacaoTemplateView.locacaoPanel.setLayout(new GridLayout());
        historicoLocacaoTemplateView.locacaoPanel.add(new LocacaoTemplateController(uiMainController, this.historicoLocacao.getLocacao(), false, false).getContainer());
        if (this.mostrarSeparador) {
            historicoLocacaoTemplateView.separador.setVisible(true);
        }

        historicoLocacaoTemplateView.kilometragemUtilizada.setText("KM/Ultilizada: ".concat(String.valueOf(this.historicoLocacao.getKilometragemUtilizada())));
        historicoLocacaoTemplateView.descricao.setText("<html>".concat(this.historicoLocacao.getDescricao()).concat("</html>"));
        historicoLocacaoTemplateView.valorFinalPago.setText("Valor final: ".concat(doubleToRealString(this.historicoLocacao.getValorFinalPago())));
        historicoLocacaoTemplateView.valorAdicional.setText("Valor Adicional: ".concat(doubleToRealString(this.historicoLocacao.getValorAdcional())));
        historicoLocacaoTemplateView.dadtaDaFinalizacao.setText("Finalizada em: ".concat(this.historicoLocacao.getDataDaDevolucao()));
    }


    /**
     * @Metodo que retorna o painel já carregado
     * com todas a informaçoes.
     * */
    public JPanel getContainer() {
        return historicoLocacaoTemplateView.container;
    }
}
