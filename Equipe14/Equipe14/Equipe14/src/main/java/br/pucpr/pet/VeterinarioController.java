package br.pucpr.pet;


import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Optional;

public class VeterinarioController extends Application {

    private final ObservableList<Veterinario> listaVeterinarios = FXCollections.observableArrayList();

    private TextField idField = new TextField();
    private TextField nomeField = new TextField();
    private TextField crmvField = new TextField();
    private TextField especialidadeField = new TextField();
    private TableView<Veterinario> table = new TableView<>();

    private final String ARQUIVO = "veterinarios.dat";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        carregarVeterinarios();

        VBox form = new VBox(10);
        form.setPadding(new Insets(10));

        idField.setPromptText("ID");
        nomeField.setPromptText("Nome");
        crmvField.setPromptText("CRMV");
        especialidadeField.setPromptText("Especialidade");

        form.getChildren().addAll(
                new Label("Cadastro de Veterinário:"),
                idField, nomeField, crmvField, especialidadeField
        );

        Button btnAdd = new Button("Adicionar");
        Button btnEdit = new Button("Editar");
        Button btnRemove = new Button("Remover");
        Button btnClear = new Button("Limpar");

        HBox botoes = new HBox(10, btnAdd, btnEdit, btnRemove, btnClear);
        form.getChildren().add(botoes);

        TableColumn<Veterinario, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idVeterinario"));

        TableColumn<Veterinario, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Veterinario, String> colCrmv = new TableColumn<>("CRMV");
        colCrmv.setCellValueFactory(new PropertyValueFactory<>("crmv"));

        TableColumn<Veterinario, String> colEspecialidade = new TableColumn<>("Especialidade");
        colEspecialidade.setCellValueFactory(new PropertyValueFactory<>("especialidade"));

        table.getColumns().addAll(colId, colNome, colCrmv, colEspecialidade);
        table.setItems(listaVeterinarios);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setOnMouseClicked(e -> preencherCampos());

        btnAdd.setOnAction(e -> adicionarVeterinario());
        btnEdit.setOnAction(e -> editarVeterinario());
        btnRemove.setOnAction(e -> removerVeterinario());
        btnClear.setOnAction(e -> limparCampos());

        HBox root = new HBox(10, form, table);
        root.setPadding(new Insets(10));

        stage.setTitle("Cadastro de Veterinários");
        stage.setScene(new Scene(root, 800, 400));
        stage.show();
    }

    private void adicionarVeterinario() {
        try {
            int id = Integer.parseInt(idField.getText());
            String nome = nomeField.getText();
            String crmv = crmvField.getText();
            String especialidade = especialidadeField.getText();

            for (Veterinario v : listaVeterinarios) {
                if (v.getIdVeterinario() == id) {
                    mostrarAlerta("Erro", "ID já existente.");
                    return;
                }
            }

            Veterinario v = new Veterinario(id, nome, crmv, especialidade);
            listaVeterinarios.add(v);
            salvarVeterinarios();
            limparCampos();
        } catch (NumberFormatException ex) {
            mostrarAlerta("Erro", "ID inválido.");
        }
    }

    private void editarVeterinario() {
        Veterinario selecionado = table.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            try {
                int id = Integer.parseInt(idField.getText());
                selecionado.setIdVeterinario(id);
                selecionado.setNome(nomeField.getText());
                selecionado.setCrmv(crmvField.getText());
                selecionado.setEspecialidade(especialidadeField.getText());
                table.refresh();
                salvarVeterinarios();
                limparCampos();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Erro", "ID inválido.");
            }
        } else {
            mostrarAlerta("Atenção", "Selecione um veterinário para editar.");
        }
    }

    private void removerVeterinario() {
        Veterinario selecionado = table.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja remover este veterinário?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                listaVeterinarios.remove(selecionado);
                salvarVeterinarios();
                limparCampos();
            }
        } else {
            mostrarAlerta("Atenção", "Selecione um veterinário para remover.");
        }
    }

    private void limparCampos() {
        idField.clear();
        nomeField.clear();
        crmvField.clear();
        especialidadeField.clear();
        table.getSelectionModel().clearSelection();
    }

    private void preencherCampos() {
        Veterinario v = table.getSelectionModel().getSelectedItem();
        if (v != null) {
            idField.setText(String.valueOf(v.getIdVeterinario()));
            nomeField.setText(v.getNome());
            crmvField.setText(v.getCrmv());
            especialidadeField.setText(v.getEspecialidade());
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void salvarVeterinarios() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(ARQUIVO))) {
            out.writeObject(new ArrayList<>(listaVeterinarios));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void carregarVeterinarios() {
        File file = new File(ARQUIVO);
        if (file.exists()) {
            try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = in.readObject();
                if (obj instanceof ArrayList<?>) {
                    ArrayList<?> list = (ArrayList<?>) obj;
                    for (Object o : list) {
                        if (o instanceof Veterinario) {
                            listaVeterinarios.add((Veterinario) o);
                        }
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Classe interna se preferir deixar tudo no mesmo arquivo
    public static class Veterinario implements Serializable {
        private int idVeterinario;
        private String nome;
        private String crmv;
        private String especialidade;

        public Veterinario(int idVeterinario, String nome, String crmv, String especialidade) {
            this.idVeterinario = idVeterinario;
            this.nome = nome;
            this.crmv = crmv;
            this.especialidade = especialidade;
        }

        public int getIdVeterinario() { return idVeterinario; }
        public void setIdVeterinario(int idVeterinario) { this.idVeterinario = idVeterinario; }

        public String getNome() { return nome; }
        public void setNome(String nome) { this.nome = nome; }

        public String getCrmv() { return crmv; }
        public void setCrmv(String crmv) { this.crmv = crmv; }

        public String getEspecialidade() { return especialidade; }
        public void setEspecialidade(String especialidade) { this.especialidade = especialidade; }
    }
}
