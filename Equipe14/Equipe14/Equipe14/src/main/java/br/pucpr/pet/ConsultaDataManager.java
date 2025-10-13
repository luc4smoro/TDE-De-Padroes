package br.pucpr.pet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDataManager {

    private static final String ARQUIVO_DE_DADOS = "consultas.dat";
    private static ConsultaDataManager instance;

    private ConsultaDataManager() {
        // Construtor privado para evitar instanciação.
    }

    public static synchronized ConsultaDataManager getInstance() {
        if (instance == null) {
            instance = new ConsultaDataManager();
        }
        return instance;
    }

    public void salvarConsultas(List<Consulta> consultaList) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DE_DADOS))) {
            oos.writeObject(new ArrayList<>(consultaList));
        }
    }

    @SuppressWarnings("unchecked")
    public List<Consulta> carregarConsultas() throws IOException, ClassNotFoundException {
        File arquivo = new File(ARQUIVO_DE_DADOS);
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_DE_DADOS))) {
            return (List<Consulta>) ois.readObject();
        }
    }
}
