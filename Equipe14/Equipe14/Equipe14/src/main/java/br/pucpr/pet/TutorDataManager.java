package br.pucpr.pet;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TutorDataManager {

    private static final String ARQUIVO_DE_DADOS = "tutores.dat";
    private static TutorDataManager instance;

    private TutorDataManager() {
        // Construtor privado para evitar instanciação.
    }

    public static synchronized TutorDataManager getInstance() {
        if (instance == null) {
            instance = new TutorDataManager();
        }
        return instance;
    }

    public void salvarTutores(List<Tutor> tutorList) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(ARQUIVO_DE_DADOS))) {
            oos.writeObject(new ArrayList<>(tutorList));
        }
    }

    @SuppressWarnings("unchecked")
    public List<Tutor> carregarTutores() throws IOException, ClassNotFoundException {
        File arquivo = new File(ARQUIVO_DE_DADOS);
        if (!arquivo.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_DE_DADOS))) {
            return (List<Tutor>) ois.readObject();
        }
    }
}
