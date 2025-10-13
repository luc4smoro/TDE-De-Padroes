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
        if (!arquivo.exists() || arquivo.length() == 0) { // Adicionado verificação de arquivo vazio
            return new ArrayList<>();
        }

        List<Consulta> consultasCarregadas = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(ARQUIVO_DE_DADOS))) {
            consultasCarregadas = (List<Consulta>) ois.readObject();
        } catch (EOFException e) {
            // Arquivo pode estar vazio ou corrompido, retorna lista vazia
            System.err.println("EOFException ao carregar consultas: " + e.getMessage());
            return new ArrayList<>();
        }

        // Garante que todas as consultas carregadas tenham um status não nulo
        for (Consulta consulta : consultasCarregadas) {
            if (consulta.getStatus() == null) {
                consulta.setStatus(StatusConsulta.CRIADA); // Define um status padrão
            }
        }
        return consultasCarregadas;
    }
}
