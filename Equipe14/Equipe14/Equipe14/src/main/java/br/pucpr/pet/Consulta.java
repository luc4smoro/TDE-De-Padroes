package br.pucpr.pet;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class Consulta implements Serializable {
    private static final long serialVersionUID = 1L; // Manter para compatibilidade de serialização

    private int id_consulta;
    private int id_pet;
    private int id_veterinario;
    private LocalDateTime data; // Data e hora agendada
    private String hora; // Hora agendada (redundante com LocalDateTime, mas mantido por compatibilidade)
    private StatusConsulta status;

    // Novos campos para o fluxo de estados
    private LocalDateTime dataInicioReal; // Data e hora real de início da consulta
    private LocalDateTime dataFimReal;    // Data e hora real de fim da consulta
    private Map<StatusConsulta, String> observacoesPorStatus;           // Observações rápidas durante o atendimento ou gerais
    private String motivoCancelamento;    // Motivo se a consulta for cancelada
    private int contadorReagendamentos;   // Quantas vezes a consulta foi reagendada
    private LocalDateTime dataCancelamento; // Data e hora do cancelamento

    public Consulta() {
        this.status = StatusConsulta.CRIADA;
        this.observacoesPorStatus = new HashMap<>();
        this.motivoCancelamento = "";
        this.contadorReagendamentos = 0;
    }

    public Consulta(int id_consulta, int id_pet, int id_veterinario, LocalDateTime data, String hora) {
        this(id_consulta, id_pet, id_veterinario, data, hora, StatusConsulta.CRIADA);
    }

    public Consulta(int id_consulta, int id_pet, int id_veterinario, LocalDateTime data, String hora, StatusConsulta status) {
        this.id_consulta = id_consulta;
        this.id_pet = id_pet;
        this.id_veterinario = id_veterinario;
        this.data = data;
        this.hora = hora;
        this.status = status;
        this.observacoesPorStatus = new HashMap<>();
        this.motivoCancelamento = "";
        this.contadorReagendamentos = 0;
    }

    // Getters e Setters existentes
    public int getId_consulta() { return id_consulta; }
    public void setId_consulta(int id_consulta) { this.id_consulta = id_consulta; }

    public int getId_pet() { return id_pet; }
    public void setId_pet(int id_pet) { this.id_pet = id_pet; }

    public int getId_veterinario() { return id_veterinario; }
    public void setId_veterinario(int id_veterinario) { this.id_veterinario = id_veterinario; }

    public LocalDateTime getData() { return data; }
    public void setData(LocalDateTime data) { this.data = data; }

    public String getHora() { return hora; }
    public void setHora(String hora) { this.hora = hora; }

    public StatusConsulta getStatus() { return status; }
    public void setStatus(StatusConsulta status) { this.status = status; }

    // Getters e Setters para os novos campos
    public LocalDateTime getDataInicioReal() { return dataInicioReal; }
    public void setDataInicioReal(LocalDateTime dataInicioReal) { this.dataInicioReal = dataInicioReal; }

    public LocalDateTime getDataFimReal() { return dataFimReal; }
    public void setDataFimReal(LocalDateTime dataFimReal) { this.dataFimReal = dataFimReal; }

    public String getObservacoes() {
        return getObservacoes(this.status);
    }

    public String getObservacoes(StatusConsulta status) {
        if (observacoesPorStatus == null) {
            observacoesPorStatus = new HashMap<>();
        }
        return observacoesPorStatus.getOrDefault(status, "");
    }

    public void setObservacoes(String observacoes) {
        setObservacoes(this.status, observacoes);
    }

    public void setObservacoes(StatusConsulta status, String observacoes) {
        if (observacoesPorStatus == null) {
            observacoesPorStatus = new HashMap<>();
        }
        this.observacoesPorStatus.put(status, observacoes);
    }

    public String getMotivoCancelamento() { return motivoCancelamento; }
    public void setMotivoCancelamento(String motivoCancelamento) { this.motivoCancelamento = motivoCancelamento; }

    public int getContadorReagendamentos() { return contadorReagendamentos; }
    public void setContadorReagendamentos(int contadorReagendamentos) { this.contadorReagendamentos = contadorReagendamentos; }
    public void incrementarContadorReagendamentos() { this.contadorReagendamentos++; }

    public LocalDateTime getDataCancelamento() { return dataCancelamento; }
    public void setDataCancelamento(LocalDateTime dataCancelamento) { this.dataCancelamento = dataCancelamento; }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        if (observacoesPorStatus == null) {
            observacoesPorStatus = new HashMap<>();
        }
    }


    @Override
    public String toString() {
        return String.format("Consulta ID: %d | Pet: %d | Veterinário: %d | Data: %s | Hora: %s | Status: %s",
                id_consulta, id_pet, id_veterinario,
                data != null ? data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                hora,
                status.getDescricao());
    }
}
