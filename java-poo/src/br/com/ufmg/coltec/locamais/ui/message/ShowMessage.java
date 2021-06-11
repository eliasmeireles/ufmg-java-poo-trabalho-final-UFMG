package br.com.ufmg.coltec.locamais.ui.message;

import javax.swing.*;

/**
 * Created by elias on 13/11/18.
 */
public class ShowMessage {

    /**
     * @Metodo que mostra uma mensagem, recebe um JFrame e a mensagem a ser mostrar por paramentro.
     * */
    public static void messageShow(JFrame jFrame, String message) {
        JOptionPane.showMessageDialog(jFrame, message);
    }
}
