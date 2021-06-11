package br.com.ufmg.coltec.locamais;

import br.com.ufmg.coltec.locamais.ui.controller.UIMainController;
import br.com.ufmg.coltec.locamais.ui.view.UIMain;

import javax.swing.*;

public class ApplicationMain {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            SwingUtilities.invokeLater(() -> new UIMainController());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
}
