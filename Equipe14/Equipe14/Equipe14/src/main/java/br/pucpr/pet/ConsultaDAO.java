package br.pucpr.pet;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConsultaDAO {
    private static final String ARQUIVO = "consultas.txt";
    private static final String SEPARADOR = "|";
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    // Converter Consulta para linha de texto
    private String consultaParaTexto(Consulta consulta) {
        return consulta.getId_consulta() + SEPARADOR +
                consulta.getId_pet() + SEPARADOR +
                consulta.getId_veterinario() + SEPARADOR +
                (consulta.getData() != null ? consulta.getData().format(FORMATO_DATA) : "") + SEPARADOR +
                consulta.getHora();
    }

    // Converter linha de texto para Consulta
    private Consulta textoParaConsulta(String linha) {
        String[] partes = linha.split("\\" + SEPARADOR);

        if (partes.length != 5) {
            throw new IllegalArgumentException("Formato de linha inválido: " + linha);
        }

        Consulta consulta = new Consulta();
        consulta.setId_consulta(Integer.parseInt(partes[0]));
        consulta.setId_pet(Integer.parseInt(partes[1]));
        consulta.setId_veterinario(Integer.parseInt(partes[2]));

        // Converter data
        if (!partes[3].isEmpty()) {
            consulta.setData(LocalDateTime.parse(partes[3], FORMATO_DATA));
        }

        consulta.setHora(partes[4]);

        return consulta;
    }

    // Salvar todas as consultas no arquivo
    public void salvarConsultas(List<Consulta> consultas) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO))) {
            // Escrever cabeçalho
            writer.write("# Arquivo de Consultas Veterinárias");
            writer.newLine();
            writer.write("# Formato: ID_CONSULTA|ID_PET|ID_VETERINARIO|DATA_HORA|HORA");
            writer.newLine();
            writer.write("# Data: " + LocalDateTime.now().format(FORMATO_DATA));
            writer.newLine();
            writer.write("# Total de consultas: " + consultas.size());
            writer.newLine();
            writer.newLine();

            // Escrever dados
            for (Consulta consulta : consultas) {
                writer.write(consultaParaTexto(consulta));
                writer.newLine();
            }
        }
    }

    // Carregar consultas do arquivo
    public List<Consulta> carregarConsultas() throws IOException {
        List<Consulta> consultas = new ArrayList<>();
        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) {
            return consultas;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linha = linha.trim();

                // Ignorar linhas vazias e comentários
                if (linha.isEmpty() || linha.startsWith("#")) {
                    continue;
                }

                try {
                    consultas.add(textoParaConsulta(linha));
                } catch (Exception e) {
                    System.err.println("Erro ao processar linha: " + linha + " - " + e.getMessage());
                }
            }
        }

        return consultas;
    }

    // Inserir nova consulta
    public void inserir(Consulta consulta) throws IOException {
        List<Consulta> consultas = carregarConsultas();
        consultas.add(consulta);
        salvarConsultas(consultas);
        System.out.println("Consulta inserida: " + consulta.getId_consulta());
    }

    // Atualizar consulta existente
    public boolean atualizar(Consulta consultaAtualizada) throws IOException {
        List<Consulta> consultas = carregarConsultas();
        for (int i = 0; i < consultas.size(); i++) {
            if (consultas.get(i).getId_consulta() == consultaAtualizada.getId_consulta()) {
                consultas.set(i, consultaAtualizada);
                salvarConsultas(consultas);
                System.out.println("Consulta atualizada: " + consultaAtualizada.getId_consulta());
                return true;
            }
        }
        return false;
    }

    // Excluir consulta
    public boolean excluir(int id) throws IOException {
        List<Consulta> consultas = carregarConsultas();
        boolean removido = consultas.removeIf(c -> c.getId_consulta() == id);
        if (removido) {
            salvarConsultas(consultas);
            System.out.println("Consulta excluída: " + id);
        }
        return removido;
    }

    // Buscar consulta por ID
    public Consulta buscarPorId(int id) throws IOException {
        List<Consulta> consultas = carregarConsultas();
        return consultas.stream()
                .filter(c -> c.getId_consulta() == id)
                .findFirst()
                .orElse(null);
    }

    // Listar todas as consultas
    public List<Consulta> listarTodas() throws IOException {
        return carregarConsultas();
    }

    // Método para backup dos dados
    public void fazerBackup() throws IOException {
        List<Consulta> consultas = carregarConsultas();
        String nomeBackup = "backup_consultas_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                ".txt";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(nomeBackup))) {
            writer.write("=== BACKUP DE CONSULTAS ===");
            writer.newLine();
            writer.write("Data do backup: " + LocalDateTime.now().format(FORMATO_DATA));
            writer.newLine();
            writer.write("Total de consultas: " + consultas.size());
            writer.newLine();
            writer.newLine();

            for (Consulta consulta : consultas) {
                writer.write("ID: " + consulta.getId_consulta());
                writer.newLine();
                writer.write("Pet ID: " + consulta.getId_pet());
                writer.newLine();
                writer.write("Veterinário ID: " + consulta.getId_veterinario());
                writer.newLine();
                writer.write("Data: " + (consulta.getData() != null ?
                        consulta.getData().format(FORMATO_DATA) : "N/A"));
                writer.newLine();
                writer.write("Hora: " + consulta.getHora());
                writer.newLine();
                writer.write("------------------------");
                writer.newLine();
            }
        }

        System.out.println("Backup criado: " + nomeBackup);
    }

    // Verificar integridade dos dados
    public void verificarIntegridade() {
        try {
            File arquivo = new File(ARQUIVO);
            System.out.println("=== VERIFICAÇÃO DE INTEGRIDADE ===");
            System.out.println("Arquivo: " + ARQUIVO);
            System.out.println("Existe: " + arquivo.exists());

            if (arquivo.exists()) {
                System.out.println("Tamanho: " + arquivo.length() + " bytes");
                System.out.println("Última modificação: " + new java.util.Date(arquivo.lastModified()));

                List<Consulta> consultas = carregarConsultas();
                System.out.println("Consultas carregadas: " + consultas.size());

                if (!consultas.isEmpty()) {
                    System.out.println("Primeira consulta: ID " + consultas.get(0).getId_consulta());
                    System.out.println("Última consulta: ID " + consultas.get(consultas.size()-1).getId_consulta());
                }
            }
            System.out.println("==================================");

        } catch (Exception e) {
            System.err.println("Erro na verificação: " + e.getMessage());
        }
    }
}