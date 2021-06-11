package br.com.ufmg.coltec.locamais.model;

/**
 * Created by elias on 23/11/18.
 */
public class Locacao {

    private Long codigo;
    private Cliente cliente;
    private Veiculo veiculo;
    private Integer kilometragemEstimadaDeEntrega;
    private String dataEstimadaDeEntrega;
    private String dataDaLocacao;
    private boolean ativa;

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public void setVeiculo(Veiculo veiculo) {
        this.veiculo = veiculo;
    }

    public Integer getKilometragemEstimadaDeEntrega() {
        return kilometragemEstimadaDeEntrega;
    }

    public void setKilometragemEstimadaDeEntrega(Integer kilometragemEstimadaDeEntrega) {
        this.kilometragemEstimadaDeEntrega = kilometragemEstimadaDeEntrega;
    }

    public String getDataEstimadaDeEntrega() {
        return dataEstimadaDeEntrega;
    }

    public void setDataEstimadaDeEntrega(String dataEstimadaDeEntrega) {
        this.dataEstimadaDeEntrega = dataEstimadaDeEntrega;
    }

    public String getDataDaLocacao() {
        return dataDaLocacao;
    }

    public void setDataDaLocacao(String dataDaLocacao) {
        this.dataDaLocacao = dataDaLocacao;
    }

    public boolean isAtiva() {
        return ativa;
    }

    public void setAtiva(boolean ativa) {
        this.ativa = ativa;
    }
}
