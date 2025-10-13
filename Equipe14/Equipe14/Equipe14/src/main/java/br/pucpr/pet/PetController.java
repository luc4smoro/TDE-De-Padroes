package br.pucpr.pet;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.Year;
import java.util.List;
import java.util.Optional;

public class PetController extends Application {

    private final int ANO_ATUAL = Year.now().getValue();

    private final ObservableList<Pet> petList = FXCollections.observableArrayList();
    private final TableView<Pet> tableView = new TableView<>();
    private TextField txtNome, txtEspecie, txtRaca, txtAnoNascimento, txtPeso, txtIdTutor, txtNomeTutor;
    private ComboBox<String> cmbSexo;
    private int nextId = 1;

    private final PetDataManager dataManager = PetDataManager.getInstance();

    @Override
    public void start(Stage primaryStage) {
        carregarDados();

        primaryStage.setTitle("Sistema de Gerenciamento de Pets");
        BorderPane root = new BorderPane();

        VBox formBox = createForm();
        VBox tableBox = createTable();
        HBox buttonBox = createButtons();

        VBox leftPanel = new VBox(10, formBox, buttonBox);
        leftPanel.setPadding(new Insets(10));
        leftPanel.setPrefWidth(350);

        root.setLeft(leftPanel);
        root.setCenter(tableBox);

        if (petList.isEmpty()) {
            addSampleData();
        }

        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                carregarPetNoFormulario(newSelection);
            }
        });

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void salvarDados() {
        try {
            dataManager.salvarDados(petList);
        } catch (IOException e) {
            mostrarErro("Falha ao salvar os dados: " + e.getMessage());
        }
    }

    private void carregarDados() {
        try {
            List<Pet> petsCarregados = dataManager.carregarDados();
            petList.setAll(petsCarregados);
            if (!petList.isEmpty()) {
                nextId = petList.stream().mapToInt(Pet::getIdPet).max().orElse(0) + 1;
            }
        } catch (IOException | ClassNotFoundException e) {
            mostrarErro("Falha ao carregar os dados: " + e.getMessage());
        }
    }

    private VBox createForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(15));
        form.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: #f9f9f9;");
        Label titleLabel = new Label("Cadastro de Pet");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        txtNome = new TextField();
        txtNome.setPromptText("Nome do pet");
        txtEspecie = new TextField();
        txtEspecie.setPromptText("Espécie (ex: Cão, Gato)");
        txtRaca = new TextField();
        txtRaca.setPromptText("Raça");
        txtAnoNascimento = new TextField();
        txtAnoNascimento.setPromptText("Ano de nascimento (ex: 2020)");
        txtPeso = new TextField();
        txtPeso.setPromptText("Peso em kg (ex: 15)");
        cmbSexo = new ComboBox<>();
        cmbSexo.getItems().addAll("Macho", "Fêmea");
        cmbSexo.setPromptText("Selecione o sexo");
        cmbSexo.setPrefWidth(Double.MAX_VALUE);
        txtIdTutor = new TextField();
        txtIdTutor.setPromptText("ID do tutor (número)");
        txtNomeTutor = new TextField();
        txtNomeTutor.setPromptText("Nome do tutor");

        form.getChildren().addAll(titleLabel, new Label("Nome:"), txtNome, new Label("Espécie:"), txtEspecie,
                new Label("Raça:"), txtRaca, new Label("Ano de Nascimento:"), txtAnoNascimento, new Label("Peso (kg):"),
                txtPeso, new Label("Sexo:"), cmbSexo, new Label("ID do Tutor:"), txtIdTutor,
                new Label("Nome do Tutor:"), txtNomeTutor);
        return form;
    }

    private VBox createTable() {
        VBox tableBox = new VBox(10);
        tableBox.setPadding(new Insets(10));
        Label tableTitle = new Label("Lista de Pets Cadastrados");
        tableTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #333;");

        TableColumn<Pet, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idPet"));
        TableColumn<Pet, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        TableColumn<Pet, String> colEspecie = new TableColumn<>("Espécie");
        colEspecie.setCellValueFactory(new PropertyValueFactory<>("especie"));
        TableColumn<Pet, String> colRaca = new TableColumn<>("Raça");
        colRaca.setCellValueFactory(new PropertyValueFactory<>("raca"));
        TableColumn<Pet, Integer> colAno = new TableColumn<>("Ano Nasc.");
        colAno.setCellValueFactory(new PropertyValueFactory<>("dataNascimento"));
        TableColumn<Pet, Integer> colPeso = new TableColumn<>("Peso");
        colPeso.setCellValueFactory(new PropertyValueFactory<>("peso"));
        TableColumn<Pet, String> colSexo = new TableColumn<>("Sexo");
        colSexo.setCellValueFactory(new PropertyValueFactory<>("sexo"));
        TableColumn<Pet, Integer> colIdTutor = new TableColumn<>("ID Tutor");
        colIdTutor.setCellValueFactory(new PropertyValueFactory<>("idTutor"));
        TableColumn<Pet, String> colNomeTutor = new TableColumn<>("Nome Tutor");
        colNomeTutor.setCellValueFactory(new PropertyValueFactory<>("nomeTutor"));

        tableView.getColumns().addAll(colId, colNome, colEspecie, colRaca, colAno, colPeso, colSexo, colIdTutor, colNomeTutor);
        tableView.setItems(petList);
        VBox.setVgrow(tableView, Priority.ALWAYS);
        tableBox.getChildren().addAll(tableTitle, tableView);
        return tableBox;
    }

    private HBox createButtons() {
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));
        Button btnAdicionar = new Button("Adicionar");
        btnAdicionar.setOnAction(e -> adicionarPet());
        Button btnEditar = new Button("Editar");
        btnEditar.setOnAction(e -> editarPet());
        Button btnRemover = new Button("Remover");
        btnRemover.setOnAction(e -> removerPet());
        Button btnLimpar = new Button("Limpar");
        btnLimpar.setOnAction(e -> limparCampos());
        buttonBox.getChildren().addAll(btnAdicionar, btnEditar, btnRemover, btnLimpar);
        return buttonBox;
    }

    private void carregarPetNoFormulario(Pet pet) {
        txtNome.setText(pet.getNome());
        txtEspecie.setText(pet.getEspecie());
        txtRaca.setText(pet.getRaca());
        txtAnoNascimento.setText(String.valueOf(pet.getDataNascimento()));
        txtPeso.setText(String.valueOf(pet.getPeso()));
        cmbSexo.setValue(pet.getSexo());
        txtIdTutor.setText(String.valueOf(pet.getIdTutor()));
        txtNomeTutor.setText(pet.getNomeTutor());
    }

    private void adicionarPet() {
        if (!validarCampos()) return;
        try {
            Pet pet = new Pet(nextId++, txtNome.getText().trim(), txtEspecie.getText().trim(),
                    txtRaca.getText().trim(), Integer.parseInt(txtAnoNascimento.getText().trim()),
                    Integer.parseInt(txtPeso.getText().trim()), cmbSexo.getValue(),
                    Integer.parseInt(txtIdTutor.getText().trim()), txtNomeTutor.getText().trim());
            petList.add(pet);
            limparCampos();
            mostrarSucesso("Pet adicionado com sucesso!");
            salvarDados();
        } catch (NumberFormatException e) {
            mostrarErro("Ano, peso e ID do tutor devem ser números válidos.");
        }
    }

    private void editarPet() {
        Pet selectedPet = tableView.getSelectionModel().getSelectedItem();
        if (selectedPet == null) {
            mostrarErro("Selecione um pet na tabela para editar.");
            return;
        }
        if (!validarCampos()) return;
        try {
            selectedPet.setNome(txtNome.getText().trim());
            selectedPet.setEspecie(txtEspecie.getText().trim());
            selectedPet.setRaca(txtRaca.getText().trim());
            selectedPet.setDataNascimento(Integer.parseInt(txtAnoNascimento.getText().trim()));
            selectedPet.setPeso(Integer.parseInt(txtPeso.getText().trim()));
            selectedPet.setSexo(cmbSexo.getValue());
            selectedPet.setIdTutor(Integer.parseInt(txtIdTutor.getText().trim()));
            selectedPet.setNomeTutor(txtNomeTutor.getText().trim());
            tableView.refresh();
            limparCampos();
            mostrarSucesso("Pet editado com sucesso!");
            salvarDados();
        } catch (NumberFormatException e) {
            mostrarErro("Ano, peso e ID do tutor devem ser números válidos.");
        }
    }

    private void removerPet() {
        Pet selectedPet = tableView.getSelectionModel().getSelectedItem();
        if (selectedPet == null) {
            mostrarErro("Selecione um pet na tabela para remover.");
            return;
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmação de Remoção");
        alert.setHeaderText("Remover o pet '" + selectedPet.getNome() + "'?");
        alert.setContentText("Esta ação não pode ser desfeita.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            petList.remove(selectedPet);
            limparCampos();
            mostrarSucesso("Pet removido com sucesso!");
            salvarDados();
        }
    }

    private void limparCampos() {
        txtNome.clear();
        txtEspecie.clear();
        txtRaca.clear();
        txtAnoNascimento.clear();
        txtPeso.clear();
        cmbSexo.getSelectionModel().clearSelection();
        txtIdTutor.clear();
        txtNomeTutor.clear();
        tableView.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();
        if (txtNome.getText().trim().isEmpty()) erros.append("- Nome do pet é obrigatório.\n");
        if (txtEspecie.getText().trim().isEmpty()) erros.append("- Espécie é obrigatória.\n");
        if (txtRaca.getText().trim().isEmpty()) erros.append("- Raça é obrigatória.\n");
        if (cmbSexo.getValue() == null) erros.append("- Sexo é obrigatório.\n");
        if (txtNomeTutor.getText().trim().isEmpty()) erros.append("- Nome do tutor é obrigatório.\n");

        try {
            int ano = Integer.parseInt(txtAnoNascimento.getText().trim());
            if (ano < 1980 || ano > ANO_ATUAL) erros.append("- Ano de nascimento deve ser entre 1980 e ").append(ANO_ATUAL).append(".\n");
        } catch (NumberFormatException e) {
            erros.append("- Ano de nascimento deve ser um número válido.\n");
        }
        try {
            int peso = Integer.parseInt(txtPeso.getText().trim());
            if (peso <= 0) erros.append("- Peso deve ser um número positivo.\n");
        } catch (NumberFormatException e) {
            erros.append("- Peso deve ser um número válido.\n");
        }
        try {
            int idTutor = Integer.parseInt(txtIdTutor.getText().trim());
            if (idTutor <= 0) erros.append("- ID do Tutor deve ser um número positivo.\n");
        } catch (NumberFormatException e) {
            erros.append("- ID do Tutor deve ser um número válido.\n");
        }

        if (!erros.isEmpty()) {
            mostrarErro("Corrija os seguintes erros:\n\n" + erros);
            return false;
        }
        return true;
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void addSampleData() {
        petList.addAll(
                new Pet(nextId++, "Rex", "Cão", "Labrador", 2020, 25, "Macho", 101, "Carlos Silva"),
                new Pet(nextId++, "Mimi", "Gato", "Persa", 2019, 4, "Fêmea", 102, "Ana Pereira")
        );
    }

    public static void main(String[] args) {
        launch(args);
    }
}
