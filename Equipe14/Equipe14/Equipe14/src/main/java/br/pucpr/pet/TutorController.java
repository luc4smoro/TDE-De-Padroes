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

import java.io.IOException;
import java.util.Optional;

/**
 * Interface gráfica para o cadastro e gerenciamento de tutores.
 * Permite adicionar, editar, remover e listar tutores em uma tabela.
 * Os dados são salvos e carregados de um arquivo local binário.
 *
 * Utiliza JavaFX para construção da interface e serialização de objetos para persistência.
 *
 * @author gskrast
 */
public class TutorController extends Application {
    /** Lista observável que armazena os tutores exibidos na tabela. */
    private final ObservableList<Tutor> listaTutores = FXCollections.observableArrayList();

    // Campos de entrada
    private TextField idField = new TextField();
    private TextField nomeField = new TextField();
    private TextField telefoneField = new TextField();
    private TextField enderecoField = new TextField();
    private TextField emailField = new TextField();

    /** Tabela que exibe os tutores cadastrados. */
    private TableView<Tutor> table = new TableView<>();

    private final TutorDataManager dataManager = TutorDataManager.getInstance();

    /**
     * Método principal que inicia a aplicação.
     *
     * @param args Argumentos da linha de comando.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Método que configura e exibe a interface gráfica da aplicação.
     *
     * @param stage Janela principal do JavaFX.
     */
    @Override
    public void start(Stage stage) {
        carregarTutores();

        // Layout dos campos de cadastro
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));

        // Campos de entrada
        idField.setPromptText("ID");
        nomeField.setPromptText("Nome");
        telefoneField.setPromptText("Telefone");
        enderecoField.setPromptText("Endereço");
        emailField.setPromptText("Email");

        form.getChildren().addAll(new Label("Cadastro de Tutor:"), idField, nomeField, telefoneField, enderecoField, emailField);

        // Botões de ação
        Button btnAdd = new Button("Adicionar");
        Button btnEdit = new Button("Editar");
        Button btnRemove = new Button("Remover");
        Button btnClear = new Button("Limpar");

        HBox botoes = new HBox(10, btnAdd, btnEdit, btnRemove, btnClear);
        form.getChildren().add(botoes);

        // Colunas da tabela
        TableColumn<Tutor, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_tutor"));

        TableColumn<Tutor, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Tutor, String> colTelefone = new TableColumn<>("Telefone");
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));

        TableColumn<Tutor, String> colEndereco = new TableColumn<>("Endereço");
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));

        TableColumn<Tutor, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        table.getColumns().addAll(colId, colNome, colTelefone, colEndereco, colEmail);
        table.setItems(listaTutores);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setOnMouseClicked(e -> preencherCampos());

        // Ações dos botões
        btnAdd.setOnAction(e -> adicionarTutor());
        btnEdit.setOnAction(e -> editarTutor());
        btnRemove.setOnAction(e -> removerTutor());
        btnClear.setOnAction(e -> limparCampos());

        // Layout principal
        HBox root = new HBox(10, form, table);
        root.setPadding(new Insets(10));

        stage.setTitle("Cadastro de Tutores");
        stage.setScene(new Scene(root, 900, 400));
        stage.show();
    }

    /**
     * Adiciona um novo tutor à lista e salva no arquivo.
     */
    private void adicionarTutor() {
        try {
            int id = Integer.parseInt(idField.getText());
            String nome = nomeField.getText();
            String telefone = telefoneField.getText();
            String endereco = enderecoField.getText();
            String email = emailField.getText();

            for (Tutor t : listaTutores) {
                if (t.getId_tutor() == id) {
                    mostrarAlerta("Erro", "ID já existe.");
                    return;
                }
            }

            Tutor tutor = new Tutor(id, nome, telefone, endereco, email);
            listaTutores.add(tutor);
            salvarTutores();
            limparCampos();
        } catch (NumberFormatException ex) {
            mostrarAlerta("Erro", "ID inválido.");
        }
    }

    /**
     * Edita os dados de um tutor selecionado na tabela.
     */
    private void editarTutor() {
        Tutor selecionado = table.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            try {
                int id = Integer.parseInt(idField.getText());
                selecionado.setId_tutor(id);
                selecionado.setNome(nomeField.getText());
                selecionado.setTelefone(telefoneField.getText());
                selecionado.setEndereco(enderecoField.getText());
                selecionado.setEmail(emailField.getText());
                table.refresh();
                salvarTutores();
                limparCampos();
            } catch (NumberFormatException ex) {
                mostrarAlerta("Erro", "ID inválido.");
            }
        } else {
            mostrarAlerta("Atenção", "Selecione um tutor para editar.");
        }
    }

    /**
     * Remove o tutor selecionado da tabela após confirmação do usuário.
     */
    private void removerTutor() {
        Tutor selecionado = table.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja remover este tutor?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                listaTutores.remove(selecionado);
                salvarTutores();
                limparCampos();
            }
        } else {
            mostrarAlerta("Atenção", "Selecione um tutor para remover.");
        }
    }

    /**
     * Limpa os campos de entrada e deseleciona a tabela.
     */
    private void limparCampos() {
        idField.clear();
        nomeField.clear();
        telefoneField.clear();
        enderecoField.clear();
        emailField.clear();
        table.getSelectionModel().clearSelection();
    }

    /**
     * Preenche os campos de entrada com os dados do tutor selecionado na tabela.
     */
    private void preencherCampos() {
        Tutor t = table.getSelectionModel().getSelectedItem();
        if (t != null) {
            idField.setText(String.valueOf(t.getId_tutor()));
            nomeField.setText(t.getNome());
            telefoneField.setText(t.getTelefone());
            enderecoField.setText(t.getEndereco());
            emailField.setText(t.getEmail());
        }
    }

    /**
     * Exibe uma janela de alerta com uma mensagem.
     *
     * @param titulo   Título da janela.
     * @param mensagem Mensagem a ser exibida.
     */
    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    /**
     * Salva a lista de tutores no arquivo binário.
     */
    private void salvarTutores() {
        try {
            dataManager.salvarTutores(listaTutores);
        } catch (IOException e) {
            mostrarAlerta("Erro", "Falha ao salvar os dados: " + e.getMessage());
        }
    }

    /**
     * Carrega a lista de tutores do arquivo binário, se existir.
     */
    private void carregarTutores() {
        try {
            listaTutores.setAll(dataManager.carregarTutores());
        } catch (IOException | ClassNotFoundException e) {
            mostrarAlerta("Erro", "Falha ao carregar os dados: " + e.getMessage());
        }
    }
}
