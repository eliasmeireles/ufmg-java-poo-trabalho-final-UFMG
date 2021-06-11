package br.com.ufmg.coltec.locamais.ui.inputvalidation;

import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_SENHA;

/**
 * Created by elias on 14/11/18.
 */
public class TextFieldValidation {

    /**
     * @Metodo que informa que o campo é obrigatório, não poder estar em branco.
     * */
    public static void validacaoTextField(JTextField textField, JLabel targeInfo, String message) {
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    targeInfo.setText(message);
                } else {
                    targeInfo.setText("");
                }
            }
        });
    }


    /**
     * @Metodo que avalida campos de texto efetuando a analize de padrões, limitando assim
     * a nescessidade do usuário informar os dados mais corretos possível.
     * */
    public static boolean inputValidationMatches(JTextField jTextField, JLabel jLabelWarningTarget, String regex, String warningMessage) {
        boolean[] isValid = {false};
        jTextField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {

            }

            @Override
            public void focusLost(FocusEvent e) {
                if (!jTextField.getText().matches(regex)) {
                    jLabelWarningTarget.setText(warningMessage);
                    isValid[0] = false;
                } else {
                    jLabelWarningTarget.setText("");
                    isValid[0] = true;
                }
            }
        });
        return isValid[0];
    }

    /**
     * @Metodo para validação de campos de textos, informando ao usuário que esse mesmo campo é
     * obrigatório ou não está de acordo com o dados esperados.
     * */

    public static boolean inputIsValid(JTextField jTextField, JLabel jLabelWarningTarget, String warningMessage, String regex) {
        if (regex != null && !jTextField.getText().isEmpty()) {
            if (jTextField.getText().matches(regex)) {
                return true;
            }
            return setErrorMessage(jTextField, jLabelWarningTarget, warningMessage);
        } else if (jTextField.getText().isEmpty()) {
            return setErrorMessage(jTextField, jLabelWarningTarget, warningMessage);
        }
        jLabelWarningTarget.setText("");
        return true;
    }

    private static boolean setErrorMessage(JTextField jTextField, JLabel jLabelWarningTarget, String warningMessage) {
        jLabelWarningTarget.setText(warningMessage);
        jTextField.requestFocus();
        return false;
    }


    /**
     * @Metodo que efetua a validação do campo senha,
     * e informa erros apos as avaliações e validações.
     * */
    public static boolean inputPasswordIsValid(JPasswordField passwordField, int minimu, int maximo, JLabel jLabelWarningTarget) {
        String passwordValue = String.valueOf(passwordField.getPassword());
        if (passwordValue.isEmpty()) {
            return setPasswordMessage(passwordField, jLabelWarningTarget, "Senha não pode estar vazia!");
        } else if (passwordValue.length() < minimu) {
            return setPasswordMessage(passwordField, jLabelWarningTarget, String.format("Senha requer no minimo %d caracter", minimu));
        } else if (passwordValue.length() > maximo) {
            return setPasswordMessage(passwordField, jLabelWarningTarget, String.format("Senha requer no maxnimo %d caracter", maximo));
        } else if (!passwordValue.matches(REGEX_SENHA)) {
            return setPasswordMessage(passwordField, jLabelWarningTarget, "Senha não pode haver caracters especiais!");
        }
        jLabelWarningTarget.setText("");
        return true;
    }

    private static boolean setPasswordMessage(JPasswordField passwordField, JLabel jLabelWarningTarget, String message) {
        jLabelWarningTarget.setText(message);
        passwordField.requestFocus();
        passwordField.setText("");
        return false;
    }
}
