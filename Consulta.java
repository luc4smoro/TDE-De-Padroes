package br.pucpr.pet;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Consulta implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id_consulta;
    private int id_pet;
    private int id_veterinario;
    private LocalDateTime data;
    private String hora;


    public Consulta() {}


    public Consulta(int id_consulta, int id_pet, int id_veterinario, LocalDateTime data, String hora) {
        this.id_consulta = id_consulta;
        this.id_pet = id_pet;
        this.id_veterinario = id_veterinario;
        this.data = data;
        this.hora = hora;
    }


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

    @Override
    public String toString() {
        return String.format("Consulta ID: %d | Pet: %d | Veterinário: %d | Data: %s | Hora: %s",
                id_consulta, id_pet, id_veterinario,
                data != null ? data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) : "N/A",
                hora);
    }
}