package br.com.ufmg.coltec.locamais.model;

/**
 * Created by elias on 12/11/18.
 */
public class Cliente implements Comparable<Cliente> {

    private Integer matricula;
    private String nome;
    private String email;
    private String cpf;
    private String celular;
    private String image;
    private Endereco endereco;

    public Integer getMatricula() {
        return matricula;
    }

    public void setMatricula(Integer matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Endereco getEndereco() {
        return endereco;
    }

    public void setEndereco(Endereco endereco) {
        this.endereco = endereco;
    }

    @Override
    public int compareTo(Cliente next) {
        return this.getMatricula().compareTo(next.getMatricula());
    }
}
