package br.pucpr.pet; // Certifique-se que o pacote seja 'br.pucpr.pet' para coincidir

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
import java.util.Optional;

public class DiagnosticoController extends Application {

    private final ObservableList<Diagnostico> listaDiagnosticos = FXCollections.observableArrayList();

    // Campos de entrada da UI
    private TextField idDiagnosticoField;
    private TextField idConsultaField;
    private TextField nomeDiagnosticoField;
    private DatePicker dataDiagnosticoPicker;
    private TextArea tratamentoArea;
    private TextArea medicamentosArea;

    private TableView<Diagnostico> table = new TableView<>();

    private final DiagnosticoDataManager dataManager = DiagnosticoDataManager.getInstance();
    private int nextId = 1; // Para simular auto-incremento de ID

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        carregarDiagnosticos();

        // --- Configuração dos campos de entrada ---
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

        // Layout dos campos de cadastro
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

        // --- Botões de ação ---
        Button btnAdd = new Button("Adicionar");
        Button btnEdit = new Button("Editar");
        Button btnRemove = new Button("Remover");
        Button btnClear = new Button("Limpar");

        HBox botoes = new HBox(10, btnAdd, btnEdit, btnRemove, btnClear);
        form.getChildren().add(botoes);

        // --- Configuração das colunas da tabela ---
        TableColumn<Diagnostico, Integer> colIdDiagnostico = new TableColumn<>("ID Diagnóstico");
        colIdDiagnostico.setCellValueFactory(new PropertyValueFactory<>("id_diagnostico"));

        TableColumn<Diagnostico, Integer> colIdConsulta = new TableColumn<>("ID Consulta");
        colIdConsulta.setCellValueFactory(new PropertyValueFactory<>("id_consulta"));

        TableColumn<Diagnostico, String> colNomeDiagnostico = new TableColumn<>("Nome Diagnostico");
        colNomeDiagnostico.setCellValueFactory(new PropertyValueFactory<>("nomeDiagnostico"));

        TableColumn<Diagnostico, LocalDate> colDataDiagnostico = new TableColumn<>("Data");
        colDataDiagnostico.setCellValueFactory(new PropertyValueFactory<>("dataDiagnostico"));
        colDataDiagnostico.setCellFactory(column -> new TableCell<Diagnostico, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : formatter.format(item));
            }
        });

        TableColumn<Diagnostico, String> colTratamento = new TableColumn<>("Tratamento");
        colTratamento.setCellValueFactory(new PropertyValueFactory<>("tratamento"));

        TableColumn<Diagnostico, String> colMedicamentos = new TableColumn<>("Medicamentos");
        colMedicamentos.setCellValueFactory(new PropertyValueFactory<>("medicamentosPrescritos"));

        table.getColumns().addAll(colIdDiagnostico, colIdConsulta, colNomeDiagnostico, colDataDiagnostico, colTratamento, colMedicamentos);
        table.setItems(listaDiagnosticos);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        table.setOnMouseClicked(e -> preencherCampos());

        // --- Ações dos botões ---
        btnAdd.setOnAction(e -> adicionarDiagnostico());
        btnEdit.setOnAction(e -> editarDiagnostico());
        btnRemove.setOnAction(e -> removerDiagnostico());
        btnClear.setOnAction(e -> limparCampos());

        // --- Layout principal ---
        VBox tableBox = new VBox(10, new Label("Lista de Diagnósticos:"), table);
        tableBox.setPadding(new Insets(10));

        HBox root = new HBox(10, form, tableBox);
        root.setPadding(new Insets(10));

        stage.setTitle("Gerenciamento de Diagnósticos");
        stage.setScene(new Scene(root, 1000, 500));
        stage.show();
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
        if (validarCampos()) {
            try {
                int idConsulta = Integer.parseInt(idConsultaField.getText().trim());
                String nomeDiagnostico = nomeDiagnosticoField.getText().trim();
                LocalDate dataDiagnostico = dataDiagnosticoPicker.getValue();
                String tratamento = tratamentoArea.getText().trim();
                String medicamentosPrescritos = medicamentosArea.getText().trim();

                if (listaDiagnosticos.stream().anyMatch(d -> d.getId_diagnostico() == nextId)) {
                    nextId = listaDiagnosticos.stream().mapToInt(Diagnostico::getId_diagnostico).max().orElse(0) + 1;
                }

                Diagnostico novoDiagnostico = new Diagnostico(nextId++, idConsulta, nomeDiagnostico, dataDiagnostico, tratamento, medicamentosPrescritos);
                listaDiagnosticos.add(novoDiagnostico);
                salvarDiagnosticos();
                limparCampos();
                mostrarAlerta("Sucesso", "Diagnóstico adicionado com sucesso!");
            } catch (NumberFormatException e) {
                mostrarAlerta("Erro de Entrada", "ID da Consulta deve ser um número válido.");
            } catch (Exception e) {
                mostrarAlerta("Erro Inesperado", "Ocorreu um erro ao adicionar o diagnóstico: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void editarDiagnostico() {
        Diagnostico selecionado = table.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            if (validarCampos()) {
                try {
                    selecionado.setId_consulta(Integer.parseInt(idConsultaField.getText().trim()));
                    selecionado.setNomeDiagnostico(nomeDiagnosticoField.getText().trim());
                    selecionado.setDataDiagnostico(dataDiagnosticoPicker.getValue());
                    selecionado.setTratamento(tratamentoArea.getText().trim());
                    selecionado.setMedicamentosPrescritos(medicamentosArea.getText().trim());

                    table.refresh();
                    salvarDiagnosticos();
                    limparCampos();
                    mostrarAlerta("Sucesso", "Diagnóstico editado com sucesso!");
                } catch (NumberFormatException e) {
                    mostrarAlerta("Erro de Entrada", "ID da Consulta deve ser um número válido.");
                } catch (Exception e) {
                    mostrarAlerta("Erro Inesperado", "Ocorreu um erro ao editar o diagnóstico: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } else {
            mostrarAlerta("Atenção", "Selecione um diagnóstico para editar.");
        }
    }

    private void removerDiagnostico() {
        Diagnostico selecionado = table.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja remover o diagnóstico de '" + selecionado.getNomeDiagnostico() + "'?", ButtonType.YES, ButtonType.NO);
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.YES) {
                listaDiagnosticos.remove(selecionado);
                salvarDiagnosticos();
                limparCampos();
                mostrarAlerta("Sucesso", "Diagnóstico removido com sucesso!");
            }
        } else {
            mostrarAlerta("Atenção", "Selecione um diagnóstico para remover.");
        }
    }

    private void limparCampos() {
        idDiagnosticoField.clear();
        idConsultaField.clear();
        nomeDiagnosticoField.clear();
        dataDiagnosticoPicker.setValue(null);
        tratamentoArea.clear();
        medicamentosArea.clear();
        table.getSelectionModel().clearSelection();
    }

    private boolean validarCampos() {
        StringBuilder erros = new StringBuilder();

        if (idConsultaField.getText() == null || idConsultaField.getText().trim().isEmpty()) {
            erros.append("- ID da Consulta é obrigatório.\n");
        } else {
            try {
                int id = Integer.parseInt(idConsultaField.getText().trim());
                if (id <= 0) {
                    erros.append("- ID da Consulta deve ser um número positivo.\n");
                }
            } catch (NumberFormatException e) {
                erros.append("- ID da Consulta deve ser um número válido.\n");
            }
        }

        if (nomeDiagnosticoField.getText() == null || nomeDiagnosticoField.getText().trim().isEmpty()) {
            erros.append("- Nome do Diagnóstico é obrigatório.\n");
        }

        if (dataDiagnosticoPicker.getValue() == null) {
            erros.append("- Data do Diagnóstico é obrigatória.\n");
        }

        if (tratamentoArea.getText() == null || tratamentoArea.getText().trim().isEmpty()) {
            erros.append("- Tratamento é obrigatório.\n");
        }

        if (medicamentosArea.getText() == null || medicamentosArea.getText().trim().isEmpty()) {
            erros.append("- Medicamentos Prescritos são obrigatórios.\n");
        }

        if (erros.length() > 0) {
            mostrarAlerta("Erro de Validação", "Por favor, corrija os seguintes problemas:\n\n" + erros.toString());
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
            dataManager.salvarDiagnosticos(listaDiagnosticos);
        } catch (IOException e) {
            mostrarAlerta("Erro de Salvar", "Não foi possível salvar os dados dos diagnósticos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void carregarDiagnosticos() {
        try {
            listaDiagnosticos.setAll(dataManager.carregarDiagnosticos());
            nextId = listaDiagnosticos.stream()
                    .mapToInt(Diagnostico::getId_diagnostico)
                    .max()
                    .orElse(0) + 1;
        } catch (IOException | ClassNotFoundException e) {
            mostrarAlerta("Erro de Carregamento", "Não foi possível carregar os dados dos diagnósticos: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
