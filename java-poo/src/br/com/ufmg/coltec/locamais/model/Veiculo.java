package br.com.ufmg.coltec.locamais.model;

import java.util.List;

/**
 * Created by elias on 10/11/18.
 */
public class Veiculo implements Comparable<Veiculo> {

    private String nome;
    private Fabricante fabricante;
    private String placa;
    private String cor;
    private String anoFabricacao;
    private String situacao;
    private Integer kilometragem;
    private Double precoPor100Km;
    private List<String> fotos;


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Fabricante getFabricante() {
        return fabricante;
    }

    public void setFabricante(Fabricante fabricante) {
        this.fabricante = fabricante;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public String getAnoFabricacao() {
        return anoFabricacao;
    }

    public void setAnoFabricacao(String anoFabricacao) {
        this.anoFabricacao = anoFabricacao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Integer getKilometragem() {
        return kilometragem;
    }

    public void setKilometragem(Integer kilometragem) {
        this.kilometragem = kilometragem;
    }

    public Double getPrecoPor100Km() {
        return precoPor100Km;
    }

    public void setPrecoPor100Km(Double precoPor100Km) {
        this.precoPor100Km = precoPor100Km;
    }

    public List<String> getFotos() {
        return fotos;
    }

    public void setFotos(List<String> fotos) {
        this.fotos = fotos;
    }

    @Override
    public int compareTo(Veiculo other) {
        return this.fabricante.getNome().compareTo(other.fabricante.getNome());
    }
}
