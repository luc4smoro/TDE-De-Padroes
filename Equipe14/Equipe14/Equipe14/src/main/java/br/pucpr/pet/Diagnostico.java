package br.pucpr.pet; // Certifique-se que o pacote seja 'br.pucpr.pet' para coincidir

import java.io.Serializable;
import java.time.LocalDate;

public class Diagnostico implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id_diagnostico;
    private int id_consulta;
    private String nomeDiagnostico;
    private LocalDate dataDiagnostico;
    private String tratamento;
    private String medicamentosPrescritos;

    // Construtor completo (com ID, para carregamento ou edição)
    public Diagnostico(int id_diagnostico, int id_consulta, String nomeDiagnostico, LocalDate dataDiagnostico, String tratamento, String medicamentosPrescritos) {
        this.id_diagnostico = id_diagnostico;
        this.id_consulta = id_consulta;
        this.nomeDiagnostico = nomeDiagnostico;
        this.dataDiagnostico = dataDiagnostico;
        this.tratamento = tratamento;
        this.medicamentosPrescritos = medicamentosPrescritos;
    }

    // Construtor para novos diagnósticos (sem ID, que será gerado, ID 0 como placeholder)
    public Diagnostico(int id_consulta, String nomeDiagnostico, LocalDate dataDiagnostico, String tratamento, String medicamentosPrescritos) {
        this(0, id_consulta, nomeDiagnostico, dataDiagnostico, tratamento, medicamentosPrescritos);
    }

    // Getters
    public int getId_diagnostico() { return id_diagnostico; }
    public int getId_consulta() { return id_consulta; }
    public String getNomeDiagnostico() { return nomeDiagnostico; }
    public LocalDate getDataDiagnostico() { return dataDiagnostico; }
    public String getTratamento() { return tratamento; }
    public String getMedicamentosPrescritos() { return medicamentosPrescritos; }

    // Setters
    public void setId_diagnostico(int id_diagnostico) { this.id_diagnostico = id_diagnostico; }
    public void setId_consulta(int id_consulta) { this.id_consulta = id_consulta; }
    public void setNomeDiagnostico(String nomeDiagnostico) { this.nomeDiagnostico = nomeDiagnostico; }
    public void setDataDiagnostico(LocalDate dataDiagnostico) { this.dataDiagnostico = dataDiagnostico; }
    public void setTratamento(String tratamento) { this.tratamento = tratamento; }
    public void setMedicamentosPrescritos(String medicamentosPrescritos) { this.medicamentosPrescritos = medicamentosPrescritos; }

    @Override
    public String toString() {
        return "Diagnostico [ID=" + id_diagnostico +
                ", Consulta=" + id_consulta +
                ", Nome='" + nomeDiagnostico +
                "', Data=" + dataDiagnostico +
                ", Tratamento='" + tratamento +
                "', Medicamentos='" + medicamentosPrescritos + "']";
    }
}