package br.pucpr.pet;

import java.io.Serializable;

// A classe implementa a interface Serializable para permitir que seus objetos sejam salvos em arquivo.
public class Pet implements Serializable {

    // É uma boa prática de programação adicionar este campo para controlar a versão da classe.
    private static final long serialVersionUID = 1L;

    private int id_pet;
    private String nome;
    private String especie;
    private String raca;
    private int data_nascimento;
    private int peso;
    private String sexo;
    private int id_tutor;
    private String nomeTutor;

    // Construtor padrão
    public Pet() {}

    // Construtor com todos os parâmetros
    public Pet(int id_pet, String nome, String especie, String raca, int data_nascimento, int peso, String sexo, int id_tutor, String nomeTutor) {
        this.id_pet = id_pet;
        this.nome = nome;
        this.especie = especie;
        this.raca = raca;
        this.data_nascimento = data_nascimento;
        this.peso = peso;
        this.sexo = sexo;
        this.id_tutor = id_tutor;
        this.nomeTutor = nomeTutor;
    }

    // --- Getters e Setters ---
    public int getIdPet() { return id_pet; }
    public void setIdPet(int id_pet) { this.id_pet = id_pet; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEspecie() { return especie; }
    public void setEspecie(String especie) { this.especie = especie; }
    public String getRaca() { return raca; }
    public void setRaca(String raca) { this.raca = raca; }
    public int getDataNascimento() { return data_nascimento; }
    public void setDataNascimento(int data) { this.data_nascimento = data; }
    public int getPeso() { return peso; }
    public void setPeso(int peso) { this.peso = peso; }
    public String getSexo() { return sexo; }
    public void setSexo(String sexo) { this.sexo = sexo; }
    public int getIdTutor() { return id_tutor; }
    public void setIdTutor(int id_tutor) { this.id_tutor = id_tutor; }
    public String getNomeTutor() { return nomeTutor; }
    public void setNomeTutor(String nomeTutor) { this.nomeTutor = nomeTutor; }
}