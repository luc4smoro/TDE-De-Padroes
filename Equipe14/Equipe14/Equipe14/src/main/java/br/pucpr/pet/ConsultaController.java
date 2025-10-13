package br.pucpr.pet;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ConsultaController extends Application {

    private final ObservableList<Consulta> listaConsultas = FXCollections.observableArrayList();
    private final ConsultaDataManager dataManager = ConsultaDataManager.getInstance();
    private TableView<Consulta> tabelaConsultas = new TableView<>();

    // Campos de entrada
    private TextField txtId = new TextField();
    private TextField txtIdPet = new TextField();
    private TextField txtIdVeterinario = new TextField();
    private DatePicker datePicker = new DatePicker();
    private TextField txtHora = new TextField();

    // --- DECORATOR UI ---
    private CheckBox chkExame = new CheckBox("Adicionar Exame de Sangue (R$ 80,00)");
    private CheckBox chkVacina = new CheckBox("Adicionar Vacinação (R$ 50,00)");
    private Label lblDescricaoFinal = new Label("Descrição: Consulta Padrão");
    private Label lblCustoFinal = new Label("Custo Total: R$ 150,00");

    // Botões
    private Button btnInserir = new Button("Inserir");
    private Button btnAtualizar = new Button("Atualizar");
    private Button btnExcluir = new Button("Excluir");
    private Button btnLimpar = new Button("Limpar");
    private Button btnAtualizar_lista = new Button("Atualizar Lista");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema CRUD - Consultas Veterinárias");
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setLeft(criarFormulario());
        root.setCenter(criarAreaTabela());
        configurarEventos();
        atualizarTabela();
        Scene scene = new Scene(root, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox criarFormulario() {
        VBox formulario = new VBox(10);
        formulario.setPadding(new Insets(10));
        formulario.setPrefWidth(350);
        formulario.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5;");

        Label titulo = new Label("Cadastro de Consultas");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        formulario.getChildren().addAll(
                titulo,
                new Label("ID Consulta:"), txtId,
                new Label("ID Pet:"), txtIdPet,
                new Label("ID Veterinário:"), txtIdVeterinario,
                new Label("Data:"), datePicker,
                new Label("Hora (HH:mm):"), txtHora
        );
        txtId.setDisable(true);

        Separator separator = new Separator();
        Label lblServicosAdicionais = new Label("Serviços Adicionais");
        lblServicosAdicionais.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        VBox decoratorBox = new VBox(5, lblServicosAdicionais, chkExame, chkVacina, lblDescricaoFinal, lblCustoFinal);
        decoratorBox.setPadding(new Insets(10, 0, 10, 0));
        formulario.getChildren().addAll(separator, decoratorBox);

        HBox areaBotoes = new HBox(5, btnInserir, btnAtualizar, btnExcluir, btnLimpar);
        areaBotoes.setAlignment(Pos.CENTER);
        formulario.getChildren().add(areaBotoes);

        return formulario;
    }

    private VBox criarAreaTabela() {
        VBox areaTabela = new VBox(10);
        areaTabela.setPadding(new Insets(10));

        // ... Colunas existentes ...
        TableColumn<Consulta, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_consulta"));

        TableColumn<Consulta, Integer> colIdPet = new TableColumn<>("ID Pet");
        colIdPet.setCellValueFactory(new PropertyValueFactory<>("id_pet"));

        TableColumn<Consulta, Integer> colIdVet = new TableColumn<>("ID Vet");
        colIdVet.setCellValueFactory(new PropertyValueFactory<>("id_veterinario"));

        TableColumn<Consulta, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getData().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));

        TableColumn<Consulta, String> colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));

        // --- NOVA COLUNA DE CUSTO TOTAL ---
        TableColumn<Consulta, Double> colCusto = new TableColumn<>("Custo Total");
        colCusto.setCellValueFactory(new PropertyValueFactory<>("custoTotal"));
        colCusto.setCellFactory(tc -> new TableCell<>() {
            private final NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(format.format(price));
                }
            }
        });

        tabelaConsultas.getColumns().addAll(colId, colIdPet, colIdVet, colData, colHora, colCusto);
        tabelaConsultas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tabelaConsultas.getSelectionModel().selectedItemProperty().addListener((obs, old, sel) -> {
            if (sel != null) preencherFormulario(sel);
        });

        HBox botaoAtualizar = new HBox(btnAtualizar_lista);
        botaoAtualizar.setAlignment(Pos.CENTER);
        areaTabela.getChildren().addAll(new Label("Lista de Consultas"), tabelaConsultas, botaoAtualizar);

        return areaTabela;
    }

    private void configurarEventos() {
        btnInserir.setOnAction(e -> salvarConsulta(false));
        btnAtualizar.setOnAction(e -> salvarConsulta(true));
        btnExcluir.setOnAction(e -> excluirConsulta());
        btnLimpar.setOnAction(e -> limparFormulario());
        btnAtualizar_lista.setOnAction(e -> atualizarTabela());
        chkExame.setOnAction(e -> atualizarDecoratorUI());
        chkVacina.setOnAction(e -> atualizarDecoratorUI());
    }

    private void atualizarDecoratorUI() {
        ServicoConsulta consulta = new Consulta();
        if (chkExame.isSelected()) consulta = new ExameDeSangueDecorator(consulta);
        if (chkVacina.isSelected()) consulta = new VacinacaoDecorator(consulta);
        lblDescricaoFinal.setText("Descrição: " + consulta.getDescricao());
        lblCustoFinal.setText("Custo Total: R$ " + String.format("%.2f", consulta.getCusto()));
    }

    private void salvarConsulta(boolean isUpdate) {
        try {
            Consulta consulta = criarConsultaDoFormulario();
            
            // --- LÓGICA DO DECORATOR ANTES DE SALVAR ---
            ServicoConsulta servico = new Consulta(); // Objeto temporário para cálculo
            if (chkExame.isSelected()) servico = new ExameDeSangueDecorator(servico);
            if (chkVacina.isSelected()) servico = new VacinacaoDecorator(servico);
            consulta.setCustoTotal(servico.getCusto()); // Define o custo final no objeto a ser salvo
            // ------------------------------------------

            List<Consulta> consultas = dataManager.carregarConsultas();

            if (isUpdate) {
                if (txtId.getText().trim().isEmpty()) {
                    mostrarAviso("Selecione uma consulta para atualizar!");
                    return;
                }
                consulta.setId_consulta(Integer.parseInt(txtId.getText()));
                int index = consultas.indexOf(consultas.stream().filter(c -> c.getId_consulta() == consulta.getId_consulta()).findFirst().orElse(null));
                if (index != -1) {
                    consultas.set(index, consulta);
                    dataManager.salvarConsultas(consultas);
                    mostrarSucesso("Consulta atualizada com sucesso!");
                } else {
                    mostrarErro("Consulta não encontrada para atualizar!");
                    return;
                }
            } else {
                int novoId = consultas.stream().mapToInt(Consulta::getId_consulta).max().orElse(0) + 1;
                consulta.setId_consulta(novoId);
                consultas.add(consulta);
                dataManager.salvarConsultas(consultas);
                mostrarSucesso("Consulta inserida com sucesso!");
            }
            limparFormulario();
            atualizarTabela();
        } catch (Exception ex) {
            mostrarErro("Erro ao salvar consulta: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void excluirConsulta() {
        // (Código de exclusão permanece o mesmo)
        if (txtId.getText().trim().isEmpty()) {
            mostrarAviso("Selecione uma consulta para excluir!");
            return;
        }
        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir?", ButtonType.YES, ButtonType.NO);
        confirmacao.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                try {
                    int id = Integer.parseInt(txtId.getText());
                    List<Consulta> consultas = dataManager.carregarConsultas();
                    if (consultas.removeIf(c -> c.getId_consulta() == id)) {
                        dataManager.salvarConsultas(consultas);
                        mostrarSucesso("Consulta excluída!");
                        limparFormulario();
                        atualizarTabela();
                    } else {
                        mostrarErro("Consulta não encontrada!");
                    }
                } catch (Exception ex) {
                    mostrarErro("Erro ao excluir: " + ex.getMessage());
                }
            }
        });
    }

    private Consulta criarConsultaDoFormulario() {
        if (txtIdPet.getText().trim().isEmpty() || txtIdVeterinario.getText().trim().isEmpty() || datePicker.getValue() == null || txtHora.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos (exceto ID) são obrigatórios!");
        }
        return new Consulta(
                0, Integer.parseInt(txtIdPet.getText()), Integer.parseInt(txtIdVeterinario.getText()),
                datePicker.getValue().atStartOfDay(), txtHora.getText().trim()
        );
    }

    private void preencherFormulario(Consulta consulta) {
        txtId.setText(String.valueOf(consulta.getId_consulta()));
        txtIdPet.setText(String.valueOf(consulta.getId_pet()));
        txtIdVeterinario.setText(String.valueOf(consulta.getId_veterinario()));
        if (consulta.getData() != null) datePicker.setValue(consulta.getData().toLocalDate());
        txtHora.setText(consulta.getHora());
        chkExame.setSelected(false);
        chkVacina.setSelected(false);
        atualizarDecoratorUI();
    }

    private void limparFormulario() {
        txtId.clear();
        txtIdPet.clear();
        txtIdVeterinario.clear();
        datePicker.setValue(null);
        txtHora.clear();
        tabelaConsultas.getSelectionModel().clearSelection();
        chkExame.setSelected(false);
        chkVacina.setSelected(false);
        atualizarDecoratorUI();
    }

    private void atualizarTabela() {
        try {
            listaConsultas.setAll(dataManager.carregarConsultas());
            tabelaConsultas.setItems(listaConsultas);
        } catch (Exception ex) {
            mostrarErro("Erro ao carregar consultas: " + ex.getMessage());
        }
    }

    private void mostrarSucesso(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }
    private void mostrarErro(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }
    private void mostrarAviso(String msg) { new Alert(Alert.AlertType.WARNING, msg).showAndWait(); }

    public static void main(String[] args) {
        launch(args);
    }
}
