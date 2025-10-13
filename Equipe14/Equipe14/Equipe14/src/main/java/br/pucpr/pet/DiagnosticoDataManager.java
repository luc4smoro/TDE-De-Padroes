package br.pucpr.pet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DiagnosticoDataManager {

    private static final String ARQUIVO_DE_DADOS = "diagnosticos.dat";
    private static DiagnosticoDataManager instance;

    private DiagnosticoDataManager() {
        // Construtor privado para evitar instanciação.
    }

    public static synchronized DiagnosticoDataManager getInstance() {
        if (instance == null) {
            instance = new DiagnosticoDataManager();
        }
        return instance;
    }

    public void salvarDiagnosticos(List<Diagnostico> diagnosticoList) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DE_DADOS))) {
            oos.writeObject(new ArrayList<>(diagnosticoList));
        }
    }

    @SuppressWarnings("unchecked")
    public List<Diagnostico> carregarDiagnosticos() throws IOException, ClassNotFoundException {
        File arquivo = new File(ARQUIVO_DE_DADOS);
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_DE_DADOS))) {
            return (List<Diagnostico>) ois.readObject();
        }
    }
}
