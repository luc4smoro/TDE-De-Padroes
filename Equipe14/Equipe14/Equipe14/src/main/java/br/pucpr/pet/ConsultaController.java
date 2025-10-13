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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

    // Botões
    private Button btnInserir = new Button("Inserir");
    private Button btnAtualizar = new Button("Atualizar");
    private Button btnExcluir = new Button("Excluir");
    private Button btnLimpar = new Button("Limpar");
    private Button btnAtualizar_lista = new Button("Atualizar Lista");

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema CRUD - Consultas Veterinárias");

        // Layout principal
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));

        // Formulário
        VBox formulario = criarFormulario();
        root.setLeft(formulario);

        // Tabela
        VBox areaTabela = criarAreaTabela();
        root.setCenter(areaTabela);

        // Configurar eventos dos botões
        configurarEventos();

        // Carregar dados iniciais
        atualizarTabela();

        Scene scene = new Scene(root, 900, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox criarFormulario() {
        VBox formulario = new VBox(10);
        formulario.setPadding(new Insets(10));
        formulario.setPrefWidth(300);
        formulario.setStyle("-fx-border-color: #ccc; -fx-border-width: 1; -fx-border-radius: 5;");

        Label titulo = new Label("Cadastro de Consultas");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Campos do formulário
        formulario.getChildren().addAll(
                titulo,
                new Label("ID Consulta:"),
                txtId,
                new Label("ID Pet:"),
                txtIdPet,
                new Label("ID Veterinário:"),
                txtIdVeterinario,
                new Label("Data:"),
                datePicker,
                new Label("Hora (HH:mm):"),
                txtHora
        );

        // Configurar campos
        txtId.setPromptText("ID será gerado automaticamente");
        txtId.setDisable(true);
        txtIdPet.setPromptText("Ex: 1");
        txtIdVeterinario.setPromptText("Ex: 1");
        txtHora.setPromptText("Ex: 14:30");

        // Área dos botões
        HBox areaBotoes = new HBox(5);
        areaBotoes.setAlignment(Pos.CENTER);
        areaBotoes.getChildren().addAll(btnInserir, btnAtualizar, btnExcluir, btnLimpar);

        formulario.getChildren().add(areaBotoes);

        return formulario;
    }

    private VBox criarAreaTabela() {
        VBox areaTabela = new VBox(10);
        areaTabela.setPadding(new Insets(10));

        Label titulo = new Label("Lista de Consultas");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Configurar colunas da tabela
        TableColumn<Consulta, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id_consulta"));
        colId.setPrefWidth(50);

        TableColumn<Consulta, Integer> colIdPet = new TableColumn<>("ID Pet");
        colIdPet.setCellValueFactory(new PropertyValueFactory<>("id_pet"));
        colIdPet.setPrefWidth(80);

        TableColumn<Consulta, Integer> colIdVet = new TableColumn<>("ID Veterinário");
        colIdVet.setCellValueFactory(new PropertyValueFactory<>("id_veterinario"));
        colIdVet.setPrefWidth(120);

        TableColumn<Consulta, String> colData = new TableColumn<>("Data");
        colData.setCellValueFactory(cellData -> {
            LocalDateTime data = cellData.getValue().getData();
            if (data != null) {
                return new javafx.beans.property.SimpleStringProperty(
                        data.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            }
            return new javafx.beans.property.SimpleStringProperty("N/A");
        });
        colData.setPrefWidth(100);

        TableColumn<Consulta, String> colHora = new TableColumn<>("Hora");
        colHora.setCellValueFactory(new PropertyValueFactory<>("hora"));
        colHora.setPrefWidth(80);

        tabelaConsultas.getColumns().addAll(colId, colIdPet, colIdVet, colData, colHora);

        // Evento de seleção na tabela
        tabelaConsultas.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        preencherFormulario(newSelection);
                    }
                }
        );

        HBox botaoAtualizar = new HBox();
        botaoAtualizar.setAlignment(Pos.CENTER);
        botaoAtualizar.getChildren().add(btnAtualizar_lista);

        areaTabela.getChildren().addAll(titulo, tabelaConsultas, botaoAtualizar);

        return areaTabela;
    }

    private void configurarEventos() {
        btnInserir.setOnAction(e -> inserirConsulta());
        btnAtualizar.setOnAction(e -> atualizarConsulta());
        btnExcluir.setOnAction(e -> excluirConsulta());
        btnLimpar.setOnAction(e -> limparFormulario());
        btnAtualizar_lista.setOnAction(e -> atualizarTabela());
    }

    private void inserirConsulta() {
        try {
            Consulta novaConsulta = criarConsultaDoFormulario();

            List<Consulta> consultasExistentes = dataManager.carregarConsultas();
            int novoId = consultasExistentes.stream()
                    .mapToInt(Consulta::getId_consulta)
                    .max()
                    .orElse(0) + 1;
            novaConsulta.setId_consulta(novoId);

            consultasExistentes.add(novaConsulta);
            dataManager.salvarConsultas(consultasExistentes);

            mostrarSucesso("Consulta inserida com sucesso!");
            limparFormulario();
            atualizarTabela();

        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        } catch (IOException | ClassNotFoundException ex) {
            mostrarErro("Erro ao inserir consulta: " + ex.getMessage());
        }
    }

    private void atualizarConsulta() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                mostrarAviso("Selecione uma consulta na tabela para atualizar!");
                return;
            }

            Consulta consultaAtualizada = criarConsultaDoFormulario();
            consultaAtualizada.setId_consulta(Integer.parseInt(txtId.getText()));

            List<Consulta> consultasExistentes = dataManager.carregarConsultas();
            boolean encontrada = false;
            for (int i = 0; i < consultasExistentes.size(); i++) {
                if (consultasExistentes.get(i).getId_consulta() == consultaAtualizada.getId_consulta()) {
                    consultasExistentes.set(i, consultaAtualizada);
                    encontrada = true;
                    break;
                }
            }

            if (encontrada) {
                dataManager.salvarConsultas(consultasExistentes);
                mostrarSucesso("Consulta atualizada com sucesso!");
                limparFormulario();
                atualizarTabela();
            } else {
                mostrarErro("Consulta não encontrada!");
            }

        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        } catch (IOException | ClassNotFoundException ex) {
            mostrarErro("Erro ao atualizar consulta: " + ex.getMessage());
        }
    }

    private void excluirConsulta() {
        try {
            if (txtId.getText().trim().isEmpty()) {
                mostrarAviso("Selecione uma consulta na tabela para excluir!");
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacao.setTitle("Confirmar Exclusão");
            confirmacao.setHeaderText("Deseja realmente excluir esta consulta?");
            confirmacao.setContentText("Esta ação não pode ser desfeita!");

            Optional<ButtonType> resultado = confirmacao.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                int idParaExcluir = Integer.parseInt(txtId.getText());

                List<Consulta> consultasExistentes = dataManager.carregarConsultas();
                boolean removido = consultasExistentes.removeIf(c -> c.getId_consulta() == idParaExcluir);

                if (removido) {
                    dataManager.salvarConsultas(consultasExistentes);
                    mostrarSucesso("Consulta excluída com sucesso!");
                    limparFormulario();
                    atualizarTabela();
                } else {
                    mostrarErro("Consulta não encontrada!");
                }
            }

        } catch (IllegalArgumentException ex) {
            mostrarAviso(ex.getMessage());
        } catch (IOException | ClassNotFoundException ex) {
            mostrarErro("Erro ao excluir consulta: " + ex.getMessage());
        }
    }

    private Consulta criarConsultaDoFormulario() {
        // Validações básicas
        if (txtIdPet.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do Pet é obrigatório!");
        }
        if (txtIdVeterinario.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("ID do Veterinário é obrigatório!");
        }
        if (datePicker.getValue() == null) {
            throw new IllegalArgumentException("Data é obrigatória!");
        }
        if (txtHora.getText().trim().isEmpty()) {
            throw new IllegalArgumentException("Hora é obrigatória!");
        }

        Consulta consulta = new Consulta();
        consulta.setId_pet(Integer.parseInt(txtIdPet.getText().trim()));
        consulta.setId_veterinario(Integer.parseInt(txtIdVeterinario.getText().trim()));

        // Converter data do DatePicker para LocalDateTime
        LocalDateTime dataHora = datePicker.getValue().atStartOfDay();
        consulta.setData(dataHora);

        consulta.setHora(txtHora.getText().trim());

        return consulta;
    }

    private void preencherFormulario(Consulta consulta) {
        txtId.setText(String.valueOf(consulta.getId_consulta()));
        txtIdPet.setText(String.valueOf(consulta.getId_pet()));
        txtIdVeterinario.setText(String.valueOf(consulta.getId_veterinario()));

        if (consulta.getData() != null) {
            datePicker.setValue(consulta.getData().toLocalDate());
        }

        txtHora.setText(consulta.getHora());
    }

    private void limparFormulario() {
        txtId.clear();
        txtIdPet.clear();
        txtIdVeterinario.clear();
        datePicker.setValue(null);
        txtHora.clear();
        tabelaConsultas.getSelectionModel().clearSelection();
    }

    private void atualizarTabela() {
        try {
            List<Consulta> consultas = dataManager.carregarConsultas();
            listaConsultas.setAll(consultas);
            tabelaConsultas.setItems(listaConsultas);
        } catch (IOException | ClassNotFoundException ex) {
            mostrarErro("Erro ao carregar consultas: " + ex.getMessage());
        }
    }

    private void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
