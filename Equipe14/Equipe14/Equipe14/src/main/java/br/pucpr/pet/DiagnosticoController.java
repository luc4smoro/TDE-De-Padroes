package br.pucpr.pet;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DiagnosticoController extends Application {

    private final ObservableList<Diagnostico> masterDiagnosticosList = FXCollections.observableArrayList();
    private final ObservableList<Diagnostico> displayedDiagnosticos = FXCollections.observableArrayList();

    private TextField idDiagnosticoField, idConsultaField, nomeDiagnosticoField;
    private DatePicker dataDiagnosticoPicker;
    private TextArea tratamentoArea, medicamentosArea;
    private TableView<Diagnostico> table = new TableView<>();

    private final DiagnosticoDataManager dataManager = DiagnosticoDataManager.getInstance();
    private int nextId = 1;
    private Integer initialConsultaId;

    public void setInitialConsultaId(int consultaId) {
        this.initialConsultaId = consultaId;
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        carregarDiagnosticos();

        // --- UI Setup ---
        setupUI();

        // --- Filtering Logic ---
        if (initialConsultaId != null) {
            idConsultaField.setText(String.valueOf(initialConsultaId));
            idConsultaField.setEditable(false);

            List<Diagnostico> filtered = masterDiagnosticosList.stream()
                .filter(d -> Integer.valueOf(d.getId_consulta()).equals(initialConsultaId))
                .collect(Collectors.toList());

            displayedDiagnosticos.setAll(filtered);
        } else {
            displayedDiagnosticos.setAll(masterDiagnosticosList);
        }

        // --- Final Scene Setup ---
        VBox form = createForm();
        VBox tableBox = new VBox(10, new Label("Lista de Diagnósticos:"), table);
        tableBox.setPadding(new Insets(10));
        HBox root = new HBox(10, form, tableBox);
        root.setPadding(new Insets(10));
        stage.setTitle("Gerenciamento de Diagnósticos");
        stage.setScene(new Scene(root, 1000, 500));
        stage.show();

        if (displayedDiagnosticos.size() == 1) {
            table.getSelectionModel().selectFirst();
            preencherCampos();
        }
    }

    private void setupUI() {
        idDiagnosticoField = new TextField();
        idDiagnosticoField.setPromptText("ID Diagnóstico (Gerado)");
        idDiagnosticoField.setEditable(false);

        idConsultaField = new TextField();
        idConsultaField.setPromptText("ID da Consulta");

        nomeDiagnosticoField = new TextField();
        nomeDiagnosticoField.setPromptText("Nome do Diagnóstico");

        dataDiagnosticoPicker = new DatePicker();
        dataDiagnosticoPicker.setPromptText("Data do Diagnóstico");

        tratamentoArea = new TextArea();
        tratamentoArea.setPromptText("Descrição do Tratamento");
        tratamentoArea.setPrefRowCount(3);

        medicamentosArea = new TextArea();
        medicamentosArea.setPromptText("Medicamentos Prescritos");
        medicamentosArea.setPrefRowCount(3);

        setupTable();
    }

    private void setupTable() {
        TableColumn<Diagnostico, Integer> colIdDiagnostico = new TableColumn<>("ID Diagnóstico");
        colIdDiagnostico.setCellValueFactory(new PropertyValueFactory<>("id_diagnostico"));

        TableColumn<Diagnostico, Integer> colIdConsulta = new TableColumn<>("ID Consulta");
        colIdConsulta.setCellValueFactory(new PropertyValueFactory<>("id_consulta"));

        TableColumn<Diagnostico, String> colNomeDiagnostico = new TableColumn<>("Nome");
        colNomeDiagnostico.setCellValueFactory(new PropertyValueFactory<>("nomeDiagnostico"));

        TableColumn<Diagnostico, LocalDate> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataDiagnostico"));
        colData.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });

        table.getColumns().setAll(colIdDiagnostico, colIdConsulta, colNomeDiagnostico, colData);
        table.setItems(displayedDiagnosticos);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setOnMouseClicked(e -> preencherCampos());
    }

    private VBox createForm() {
        VBox form = new VBox(10);
        form.setPadding(new Insets(10));
        Label titleLabel = new Label("Cadastro de Diagnóstico");
        titleLabel.setStyle("-fx-font-weight: bold;");
        form.getChildren().addAll(
                titleLabel,
                new Label("ID Diagnóstico:"), idDiagnosticoField,
                new Label("ID Consulta:"), idConsultaField,
                new Label("Nome Diagnóstico:"), nomeDiagnosticoField,
                new Label("Data Diagnóstico:"), dataDiagnosticoPicker,
                new Label("Tratamento:"), tratamentoArea,
                new Label("Medicamentos Prescritos:"), medicamentosArea
        );

        Button btnAdd = new Button("Adicionar");
        btnAdd.setOnAction(e -> adicionarDiagnostico());
        Button btnEdit = new Button("Editar");
        btnEdit.setOnAction(e -> editarDiagnostico());
        Button btnRemove = new Button("Remover");
        btnRemove.setOnAction(e -> removerDiagnostico());
        Button btnClear = new Button("Limpar");
        btnClear.setOnAction(e -> limparCampos());
        HBox botoes = new HBox(10, btnAdd, btnEdit, btnRemove, btnClear);
        form.getChildren().add(botoes);
        return form;
    }

    private void preencherCampos() {
        Diagnostico diagnostico = table.getSelectionModel().getSelectedItem();
        if (diagnostico != null) {
            idDiagnosticoField.setText(String.valueOf(diagnostico.getId_diagnostico()));
            idConsultaField.setText(String.valueOf(diagnostico.getId_consulta()));
            nomeDiagnosticoField.setText(diagnostico.getNomeDiagnostico());
            dataDiagnosticoPicker.setValue(diagnostico.getDataDiagnostico());
            tratamentoArea.setText(diagnostico.getTratamento());
            medicamentosArea.setText(diagnostico.getMedicamentosPrescritos());
        }
    }

    private void adicionarDiagnostico() {
        if (!validarCampos()) return;
        try {
            int idConsulta = Integer.parseInt(idConsultaField.getText().trim());
            if (initialConsultaId != null && idConsulta != initialConsultaId) {
                mostrarAlerta("Erro de Validação", "O ID da Consulta não pode ser alterado nesta visualização.");
                return;
            }

            Diagnostico novoDiagnostico = new Diagnostico(
                0, // ID será gerado
                idConsulta,
                nomeDiagnosticoField.getText().trim(),
                dataDiagnosticoPicker.getValue(),
                tratamentoArea.getText().trim(),
                medicamentosArea.getText().trim()
            );

            nextId = masterDiagnosticosList.stream().mapToInt(Diagnostico::getId_diagnostico).max().orElse(0) + 1;
            novoDiagnostico.setId_diagnostico(nextId);

            masterDiagnosticosList.add(novoDiagnostico);
            if (initialConsultaId == null || initialConsultaId.equals(idConsulta)) {
                displayedDiagnosticos.add(novoDiagnostico);
            }

            salvarDiagnosticos();
            limparCampos();
            mostrarAlerta("Sucesso", "Diagnóstico adicionado com sucesso!");
        } catch (Exception e) {
            mostrarAlerta("Erro Inesperado", "Ocorreu um erro ao adicionar o diagnóstico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void editarDiagnostico() {
        Diagnostico selecionado = table.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Atenção", "Selecione um diagnóstico para editar.");
            return;
        }
        if (!validarCampos()) return;

        try {
            selecionado.setNomeDiagnostico(nomeDiagnosticoField.getText().trim());
            selecionado.setDataDiagnostico(dataDiagnosticoPicker.getValue());
            selecionado.setTratamento(tratamentoArea.getText().trim());
            selecionado.setMedicamentosPrescritos(medicamentosArea.getText().trim());

            table.refresh();
            salvarDiagnosticos();
            limparCampos();
            mostrarAlerta("Sucesso", "Diagnóstico editado com sucesso!");
        } catch (Exception e) {
            mostrarAlerta("Erro Inesperado", "Ocorreu um erro ao editar o diagnóstico: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void removerDiagnostico() {
        Diagnostico selecionado = table.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta("Atenção", "Selecione um diagnóstico para remover.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja remover o diagnóstico?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.YES) {
            masterDiagnosticosList.remove(selecionado);
            displayedDiagnosticos.remove(selecionado);
            salvarDiagnosticos();
            limparCampos();
            mostrarAlerta("Sucesso", "Diagnóstico removido com sucesso!");
        }
    }

    private void limparCampos() {
        idDiagnosticoField.clear();
        nomeDiagnosticoField.clear();
        dataDiagnosticoPicker.setValue(null);
        tratamentoArea.clear();
        medicamentosArea.clear();
        table.getSelectionModel().clearSelection();

        if (initialConsultaId == null) {
            idConsultaField.clear();
            idConsultaField.setEditable(true);
        } else {
            idConsultaField.setText(String.valueOf(initialConsultaId));
        }
    }

    private boolean validarCampos() {
        if (idConsultaField.getText().trim().isEmpty() || nomeDiagnosticoField.getText().trim().isEmpty() || dataDiagnosticoPicker.getValue() == null) {
            mostrarAlerta("Erro de Validação", "ID da Consulta, Nome do Diagnóstico e Data são obrigatórios.");
            return false;
        }
        return true;
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void salvarDiagnosticos() {
        try {
            dataManager.salvarDiagnosticos(masterDiagnosticosList);
        } catch (IOException e) {
            mostrarAlerta("Erro de Salvamento", "Não foi possível salvar os dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarDiagnosticos() {
        try {
            masterDiagnosticosList.setAll(dataManager.carregarDiagnosticos());
        } catch (IOException | ClassNotFoundException e) {
            mostrarAlerta("Erro de Carregamento", "Não foi possível carregar os dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
