package br.com.ufmg.coltec.locamais.model;

import java.io.Serializable;

/**
 * Created by elias on 10/11/18.
 */
public class Fabricante implements Comparable<Fabricante> {

    public Fabricante(String nome) {
        this.nome = nome;
    }

    public Fabricante() {
    }

    private String nome;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public int compareTo(Fabricante other) {
        return this.nome.compareTo(other.getNome());
    }
}
