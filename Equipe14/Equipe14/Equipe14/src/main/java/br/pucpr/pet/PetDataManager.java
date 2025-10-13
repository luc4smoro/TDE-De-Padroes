package br.pucpr.pet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PetDataManager {

    private static final String ARQUIVO_DE_DADOS = "pets.dat";
    private static PetDataManager instance;

    private PetDataManager() {
        // Construtor privado para evitar instanciação.
    }

    public static synchronized PetDataManager getInstance() {
        if (instance == null) {
            instance = new PetDataManager();
        }
        return instance;
    }

    public void salvarDados(List<Pet> petList) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DE_DADOS))) {
            oos.writeObject(new ArrayList<>(petList));
        }
    }

    @SuppressWarnings("unchecked")
    public List<Pet> carregarDados() throws IOException, ClassNotFoundException {
        File arquivo = new File(ARQUIVO_DE_DADOS);
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_DE_DADOS))) {
            return (List<Pet>) ois.readObject();
        }
    }
}
