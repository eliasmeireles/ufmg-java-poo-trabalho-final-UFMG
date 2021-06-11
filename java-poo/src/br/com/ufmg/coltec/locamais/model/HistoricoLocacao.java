package br.com.ufmg.coltec.locamais.model;

public class HistoricoLocacao {

    private Locacao locacao;
    private String descricao;
    private Double valorFinalPago;
    private Double valorAdcional;
    private Integer kilometragemUtilizada;
    private String dataDaDevolucao;


    public Locacao getLocacao() {
        return locacao;
    }

    public void setLocacao(Locacao locacao) {
        this.locacao = locacao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Double getValorFinalPago() {
        return valorFinalPago;
    }

    public void setValorFinalPago(Double valorFinalPago) {
        this.valorFinalPago = valorFinalPago;
    }

    public Double getValorAdcional() {
        return valorAdcional;
    }

    public void setValorAdcional(Double valorAdcional) {
        this.valorAdcional = valorAdcional;
    }

    public Integer getKilometragemUtilizada() {
        return kilometragemUtilizada;
    }

    public void setKilometragemUtilizada(Integer kilometragemUtilizada) {
        this.kilometragemUtilizada = kilometragemUtilizada;
    }

    public String getDataDaDevolucao() {
        return dataDaDevolucao;
    }

    public void setDataDaDevolucao(String dataDaDevolucao) {
        this.dataDaDevolucao = dataDaDevolucao;
    }

}
