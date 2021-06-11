package br.com.ufmg.coltec.locamais.model;

import static br.com.ufmg.coltec.locamais.model.FuncionariosCargos.DIRETOR;
import static br.com.ufmg.coltec.locamais.model.FuncionariosCargos.GERENTE;

/**
 * Created by elias on 14/11/18.
 */
public class FuncionarioAutenticavel extends Funcionario implements Autentica {

    public FuncionarioAutenticavel(String funcionarioType) {
        super(funcionarioType);
    }


    @Override
    public boolean autentica(String senha) {
        return super.getCargo().equals(GERENTE) || super.getCargo().equals(DIRETOR);
    }
}
