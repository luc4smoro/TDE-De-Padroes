package br.pucpr.pet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class VeterinarioDataManager {

    private static final String ARQUIVO_DE_DADOS = "veterinarios.dat";
    private static VeterinarioDataManager instance;

    private VeterinarioDataManager() {
        // Construtor privado para evitar instanciação.
    }

    public static synchronized VeterinarioDataManager getInstance() {
        if (instance == null) {
            instance = new VeterinarioDataManager();
        }
        return instance;
    }

    public void salvarVeterinarios(List<Veterinario> veterinarioList) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DE_DADOS))) {
            oos.writeObject(new ArrayList<>(veterinarioList));
        }
    }

    @SuppressWarnings("unchecked")
    public List<Veterinario> carregarVeterinarios() throws IOException, ClassNotFoundException {
        File arquivo = new File(ARQUIVO_DE_DADOS);
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_DE_DADOS))) {
            return (List<Veterinario>) ois.readObject();
        }
    }
}
