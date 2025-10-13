package br.pucpr.pet;

import java.io.Serializable;

/**
 * Representa um tutor (dono de pet) no sistema.
 * Esta classe contém informações básicas como nome, telefone, endereço e e-mail.
 * Implementa {@link Serializable} para permitir persistência em arquivos ou transmissão pela rede.
 *
 * @author gskrast
 */
public class Tutor implements Serializable {
    private static final long serialVersionUID = 1L;

    /** Identificador único do tutor. */
    private int id_tutor;

    /** Nome completo do tutor. */
    private String nome;

    /** Número de telefone do tutor. */
    private String telefone;

    /** Endereço residencial do tutor. */
    private String endereco;

    /** Endereço de e-mail do tutor. */
    private String email;

    /**
     * Construtor da classe Tutor.
     *
     * @param id_tutor Identificador do tutor.
     * @param nome Nome completo do tutor.
     * @param telefone Número de telefone.
     * @param endereco Endereço residencial.
     * @param email Endereço de e-mail.
     */
    public Tutor(int id_tutor, String nome, String telefone, String endereco, String email) {
        this.id_tutor = id_tutor;
        this.nome = nome;
        this.telefone = telefone;
        this.endereco = endereco;
        this.email = email;
    }

    /**
     * Retorna uma representação textual do tutor.
     *
     * @return Nome do tutor seguido do e-mail entre parênteses.
     */
    @Override
    public String toString() {
        return nome + " (" + email + ")";
    }

    /**
     * Obtém o ID do tutor.
     *
     * @return ID do tutor.
     */
    public int getId_tutor() {
        return id_tutor;
    }

    /**
     * Define o ID do tutor.
     *
     * @param id_tutor Novo ID do tutor.
     */
    public void setId_tutor(int id_tutor) {
        this.id_tutor = id_tutor;
    }

    /**
     * Obtém o nome do tutor.
     *
     * @return Nome do tutor.
     */
    public String getNome() {
        return nome;
    }

    /**
     * Define o nome do tutor.
     *
     * @param nome Novo nome do tutor.
     */
    public void setNome(String nome) {
        this.nome = nome;
    }

    /**
     * Obtém o telefone do tutor.
     *
     * @return Telefone do tutor.
     */
    public String getTelefone() {
        return telefone;
    }

    /**
     * Define o telefone do tutor.
     *
     * @param telefone Novo telefone do tutor.
     */
    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    /**
     * Obtém o endereço do tutor.
     *
     * @return Endereço do tutor.
     */
    public String getEndereco() {
        return endereco;
    }

    /**
     * Define o endereço do tutor.
     *
     * @param endereco Novo endereço do tutor.
     */
    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    /**
     * Obtém o e-mail do tutor.
     *
     * @return E-mail do tutor.
     */
    public String getEmail() {
        return email;
    }

    /**
     * Define o e-mail do tutor.
     *
     * @param email Novo e-mail do tutor.
     */
    public void setEmail(String email) {
        this.email = email;
    }
}
