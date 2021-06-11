package br.com.ufmg.coltec.locamais.ui.helper;

import br.com.ufmg.coltec.locamais.model.Cliente;
import br.com.ufmg.coltec.locamais.model.Locacao;
import br.com.ufmg.coltec.locamais.model.Veiculo;
import br.com.ufmg.coltec.locamais.ui.inputlimit.JTextFieldLimit;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.text.NumberFormat;
import java.util.List;

/**
 * Created by elias on 15/11/18.
 */
public class UiHelper {


    /**
     * @Metodo para limitar o número maximo de caracteres em um campo de texto.
     */
    public static void setLimit(JTextField jTextField, int limit) {
        jTextField.setDocument(new JTextFieldLimit(limit));
    }

    /**
     * @Metodo para formatar um JFormatedTextField aceitando apenas números.
     */
    public static NumberFormatter getNumberFormatter(Integer minValue, Integer maxValue) {
        NumberFormat format = NumberFormat.getInstance();
        format.setGroupingUsed(false);
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Integer.class);
        formatter.setMinimum(minValue);
        formatter.setMaximum(maxValue);
//        formatter.setAllowsInvalid(false);
        // If you want the value to be committed on each keystroke instead of focus lost
        formatter.setCommitsOnValidEdit(true);
        return formatter;
    }

    /**
     * @Metodo que calcula o valor a ser pago por kilometragem
     * utilizada.
     */
    public static Double valorASerPagorPorKilometragemRodada(Integer kilometragem, Veiculo veiculo) throws NullPointerException {
        Double valorPorKilometragem = veiculo.getPrecoPor100Km() / 100;
        Double valorEstimadoPorKilometragem = kilometragem * valorPorKilometragem;
        return valorEstimadoPorKilometragem;
    }
}
