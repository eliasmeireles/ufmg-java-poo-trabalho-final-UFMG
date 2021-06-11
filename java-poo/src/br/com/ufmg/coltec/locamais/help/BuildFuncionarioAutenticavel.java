package br.com.ufmg.coltec.locamais.help;

import br.com.ufmg.coltec.locamais.model.*;

import static br.com.ufmg.coltec.locamais.model.FuncionariosCargos.ADMINISTRADOR;
import static br.com.ufmg.coltec.locamais.model.FuncionariosCargos.DIRETOR;
import static br.com.ufmg.coltec.locamais.model.FuncionariosCargos.GERENTE;

/**
 * Created by elias on 15/11/18.
 */
public class BuildFuncionarioAutenticavel {

    /**
     * Metodo Extrai os dados de um Funcionário para um funcionário autenticável
     **/
    public static FuncionarioAutenticavel getFuncionarioAutenticavel(Funcionario funcionario) throws NullPointerException {
        FuncionarioAutenticavel autenticavel = getFuncionario(funcionario);
        autenticavel.setMatricula(funcionario.getMatricula());
        autenticavel.setCargo(funcionario.getCargo());
        autenticavel.setNome(funcionario.getNome());
        autenticavel.setCelular(funcionario.getCelular());
        autenticavel.setTelefone(funcionario.getTelefone());
        autenticavel.setSenha(funcionario.getSenha());
        autenticavel.setEndereco(funcionario.getEndereco());
        autenticavel.setCpf(funcionario.getCpf());
        autenticavel.setImage(funcionario.getImage());
        return autenticavel;
    }


    /**
     * Metodo verifica qual o tipo de funcionario
     **/
    private static FuncionarioAutenticavel getFuncionario(Funcionario funcionario) throws NullPointerException {
        switch (funcionario.getCargo()) {
            case GERENTE:
                return new Gerente();
            case DIRETOR:
                return new Diretor();
            case ADMINISTRADOR:
                return new Adiministrador();
            default:
                return null;
        }
    }
}
