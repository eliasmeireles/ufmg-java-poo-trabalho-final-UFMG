package br.com.ufmg.coltec.locamais.ui.template.controller;

import br.com.ufmg.coltec.locamais.model.Cliente;
import br.com.ufmg.coltec.locamais.model.Funcionario;
import br.com.ufmg.coltec.locamais.ui.controller.UIMainController;
import br.com.ufmg.coltec.locamais.ui.template.ClienteFuncionarioTemplate;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;

import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.CLIENTES_IMAGE_DIRECTORY;
import static br.com.ufmg.coltec.locamais.filemanager.SystemFolderAndFilesURL.FUNCIONARIOS_IMAGE_DIRECTORY;
import static br.com.ufmg.coltec.locamais.image.ImageScale.scaleImage;

public class ClienteFuncionarioTemplateController {

    private UIMainController uiMainController;
    private Cliente cliente;
    private Funcionario funcionario;
    private ClienteFuncionarioTemplate clienteFuncionarioTemplateView;


    /**
     * @Contrutor a ser chamado quando a descrição a ser mostrada para
     * um funcionário.
     * */
    public ClienteFuncionarioTemplateController(UIMainController uiMainController, Funcionario funcionario) {
        this.uiMainController = uiMainController;
        this.funcionario = funcionario;
        setFuncionarioDadosOnView();
    }

    /**
     * @Contrutor a ser chamado quando a descrição a ser mostrada para
     * um cliente, o parametro "mostrarVeiculos", serve para carregar a lista
     * de veículos abaixo da descrição desse cliente.
     * */
    public ClienteFuncionarioTemplateController(UIMainController uiMainController, Cliente cliente) {
        this.uiMainController = uiMainController;
        this.cliente = cliente;
        setClienteDadosOnView();
    }

    /**
     * @Metodo para processar os dados do cliente e mostralos na tela.
     * */
    private void setClienteDadosOnView() throws NullPointerException {
        this.clienteFuncionarioTemplateView = new ClienteFuncionarioTemplate();
        clienteFuncionarioTemplateView.nome.setText(this.cliente.getNome());
        clienteFuncionarioTemplateView.cargo.setVisible(false);
        clienteFuncionarioTemplateView.matricula.setText("N/Matrícula: ".concat(String.valueOf(this.cliente.getMatricula())));
        clienteFuncionarioTemplateView.cpf.setText("Cpf: ".concat(this.cliente.getCpf()));
        clienteFuncionarioTemplateView.email.setText("Email: ".concat(this.cliente.getEmail()));
        clienteFuncionarioTemplateView.celular.setText("Celular: ".concat(this.cliente.getCelular()));
        clienteFuncionarioTemplateView.telefoneFixo.setVisible(false);
        clienteFuncionarioTemplateView.logradouro.setText("<html>Logradouro: ".concat(this.cliente.getEndereco().getLogradouro()).concat("</html>"));
        clienteFuncionarioTemplateView.numero.setText("Número: ".concat(this.cliente.getEndereco().getNumero()));
        clienteFuncionarioTemplateView.cep.setText("Cep: ".concat(this.cliente.getEndereco().getCep()));
        clienteFuncionarioTemplateView.bairro.setText("Bairro: ".concat(this.cliente.getEndereco().getBairro()));
        clienteFuncionarioTemplateView.cidade.setText("Cidade: ".concat(this.cliente.getEndereco().getCidade()));
        clienteFuncionarioTemplateView.estado.setText("Estado: ".concat(this.cliente.getEndereco().getEstado()).concat("/").concat(this.cliente.getEndereco().getPais()));

        try {
            File image = new File(CLIENTES_IMAGE_DIRECTORY.concat(cliente.getImage()));
            ImageIcon imageIcon = new ImageIcon(scaleImage(300, 284, ImageIO.read(image)));
            clienteFuncionarioTemplateView.image.setIcon(imageIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Metodo que processa os dados do funcionário e mostralos na tela.
     * */
    private void setFuncionarioDadosOnView() throws NullPointerException {
        this.clienteFuncionarioTemplateView = new ClienteFuncionarioTemplate();
        clienteFuncionarioTemplateView.nome.setText(this.funcionario.getNome());
        clienteFuncionarioTemplateView.cargo.setText("Cargo: ".concat(this.funcionario.getCargo()));
        clienteFuncionarioTemplateView.matricula.setText("N/Matrícula: ".concat(String.valueOf(this.funcionario.getMatricula())));
        clienteFuncionarioTemplateView.cpf.setText("Cpf: ".concat(this.funcionario.getCpf()));
        clienteFuncionarioTemplateView.email.setVisible(false);
        clienteFuncionarioTemplateView.celular.setText("Celular: ".concat(this.funcionario.getCelular()));
        clienteFuncionarioTemplateView.telefoneFixo.setText("T/Fixo: ".concat(this.funcionario.getTelefone()));
        clienteFuncionarioTemplateView.logradouro.setText("<html>Logradouro: ".concat(this.funcionario.getEndereco().getLogradouro()).concat("</html>"));
        clienteFuncionarioTemplateView.bairro.setText("Bairro: ".concat(this.funcionario.getEndereco().getBairro()));
        clienteFuncionarioTemplateView.numero.setText("Número: ".concat(this.funcionario.getEndereco().getNumero()));
        clienteFuncionarioTemplateView.cep.setText("Cep: ".concat(this.funcionario.getEndereco().getCep()));
        clienteFuncionarioTemplateView.cidade.setText("Cidade: ".concat(this.funcionario.getEndereco().getCidade()));
        clienteFuncionarioTemplateView.estado.setText("Estado: ".concat(this.funcionario.getEndereco().getEstado()).concat("/").concat(this.funcionario.getEndereco().getPais()));

        try {
            File image = new File(FUNCIONARIOS_IMAGE_DIRECTORY.concat(funcionario.getImage()));
            ImageIcon imageIcon = new ImageIcon(scaleImage(300, 284, ImageIO.read(image)));
            clienteFuncionarioTemplateView.image.setIcon(imageIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @Metodo que retorna o painel com todos as informações já preechidas na tela.
     * */
    public JPanel getContainer() {
        return clienteFuncionarioTemplateView.container;
    }
}
