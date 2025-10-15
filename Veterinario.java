package br.pucpr.pet;

public class Veterinario {
    private int id_veterinario;
    private String nome;
    private String crmv;
    private String especialidade;
    private int telefone;
    private String email;

    public Veterinario(int id_veterinario, String nome, String crmv, String especialidade, int telefone, String email) {
        this.id_veterinario = id_veterinario;
        this.nome = nome;
        this.crmv = crmv;
        this.especialidade = especialidade;
        this.telefone = telefone;
        this.email = email;
    }

    public int getIdVeterinario() {
        return id_veterinario;
    }

    public String getNome() {
        return nome;
    }

    public String getCrmv() {
        return crmv;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public int getTelefone() {
        return telefone;
    }

    public String getEmail() {
        return email;
    }

    public void setIdVeterinario(int id_veterinario) {
        this.id_veterinario = id_veterinario;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCrmv(String crmv) {
        this.crmv = crmv;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }

    public void setEmail(String email) {
        this.email = email;
    }}
