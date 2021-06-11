package br.com.ufmg.coltec.locamais.ui.template.controller;

import br.com.ufmg.coltec.locamais.model.Endereco;
import br.com.ufmg.coltec.locamais.ui.template.EnderecoTemplate;

import javax.swing.*;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;

import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_CEP;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.Regex.REGEX_NOME;
import static br.com.ufmg.coltec.locamais.ui.inputvalidation.TextFieldValidation.*;

public class EnderecoTemplateController {

    private Endereco endereco;
    private EnderecoTemplate enderecoTemplateView;

    public EnderecoTemplateController() {
        this.endereco = new Endereco();
        this.enderecoTemplateView = new EnderecoTemplate();
        cepFormatted();
        validation();
    }

    /**
     * @Metodo que insera o campo de cep na tela com sua formatação adequada para um cep
     * Brasileiro.
     */
    private void cepFormatted() {
        try {
            enderecoTemplateView.cep = new JFormattedTextField(new DefaultFormatterFactory(new MaskFormatter("#####-###")));
            Font font = enderecoTemplateView.logradouro.getFont();
            enderecoTemplateView.cep.setFont(font);

            enderecoTemplateView.cepPanel.setLayout(new GridLayout(1, 1));
            enderecoTemplateView.cepPanel.add(enderecoTemplateView.cep);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * @Metodo que avalia os dados informados, tornando a obrigatóriedade de que todos os
     * campos para o endereço sejam preenchidos.
     */
    public boolean isValid() {
        try {
            String message = "Campo obrigatório!";
            return inputIsValid(enderecoTemplateView.logradouro, enderecoTemplateView.logradouroValidation, message, REGEX_NOME)
                    && inputIsValid(enderecoTemplateView.cep, enderecoTemplateView.cepValidation, message, REGEX_CEP)
                    && inputIsValid(enderecoTemplateView.bairro, enderecoTemplateView.bairroValidation, message, null)
                    && inputIsValid(enderecoTemplateView.numero, enderecoTemplateView.numeroValidation, message, null)
                    && inputIsValid(enderecoTemplateView.cidade, enderecoTemplateView.cidadeValidation, message, null)
                    && inputIsValid(enderecoTemplateView.estado, enderecoTemplateView.estadoValidation, message, null)
                    && inputIsValid(enderecoTemplateView.pais, enderecoTemplateView.paisValidation, message, null);
        } catch (NullPointerException e) {
            return false;
        }
    }


    /**
     * @Metodo que avalia os dados de campo logo apos a perda de foco se próprio.
     */
    private void validation() {
        String message = "Campo obrigatório!";
        validacaoTextField(enderecoTemplateView.logradouro, enderecoTemplateView.logradouroValidation, message);
        validacaoTextField(enderecoTemplateView.bairro, enderecoTemplateView.bairroValidation, message);
        validacaoTextField(enderecoTemplateView.numero, enderecoTemplateView.numeroValidation, message);
        validacaoTextField(enderecoTemplateView.estado, enderecoTemplateView.estadoValidation, message);
        validacaoTextField(enderecoTemplateView.cidade, enderecoTemplateView.cidadeValidation, message);
        validacaoTextField(enderecoTemplateView.pais, enderecoTemplateView.paisValidation, message);

        inputValidationMatches(enderecoTemplateView.cep, enderecoTemplateView.cepValidation, REGEX_CEP, "Cep inválido!");
    }


    /**
     *@Metodo que retorna um objeto do tipo endereço já
     * com seu atributos preenchidos.
     * */
    public Endereco getEndereco() {
        setEnderecoData();
        return this.endereco;
    }

    /**
     * @Metodo que preenche os atributos do objeto endereço antes de efetuar o retorno
     * do mesmo.
     * */
    private void setEnderecoData() {
        this.endereco.setLogradouro(enderecoTemplateView.logradouro.getText());
        this.endereco.setNumero(enderecoTemplateView.numero.getText());
        this.endereco.setCep(enderecoTemplateView.cep.getText());
        this.endereco.setCidade(enderecoTemplateView.cidade.getText());
        this.endereco.setEstado(enderecoTemplateView.estado.getText());
        this.endereco.setBairro(enderecoTemplateView.bairro.getText());
        this.endereco.setPais(enderecoTemplateView.pais.getText());
    }

    /**
     * @Metodo que retorna o formulário de um endereço a ser mostrado na tela.
     * */
    public JPanel getContainer() {
        return enderecoTemplateView.container;
    }

}
