package br.pucpr.pet;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.pucpr.pet.StatusConsulta;

public class ConsultaController extends Application {

    private final ObservableList<Consulta> listaConsultas = FXCollections.observableArrayList();
    private final ConsultaDataManager dataManager = ConsultaDataManager.getInstance();
    private final TableView<Consulta> tabelaConsultas = new TableView<>();

    // Campos de entrada
    private final TextField txtId = new TextField();
    private final TextField txtIdPet = new TextField();
    private final TextField txtIdVeterinario = new TextField();
    private final DatePicker datePicker = new DatePicker();
    private final TextField txtHora = new TextField();
    private final ComboBox<StatusConsulta> cmbStatus = new ComboBox<>(FXCollections.observableArrayList(StatusConsulta.values()));

    // Botões
    private final Button btnInserir = new Button("Inserir");
    private final Button btnAtualizar = new Button("Atualizar");
    private final Button btnExcluir = new Button("Excluir");
    private final Button btnLimpar = new Button("Limpar");
    private final Button btnAtualizar_lista = new Button("Atualizar Lista");

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

        Scene scene = new Scene(root, 1000, 600);
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

        // Configurar ComboBox de status
        cmbStatus.setPromptText("Selecione o Status");
        cmbStatus.setValue(StatusConsulta.CRIADA); // Valor padrão
        cmbStatus.setMaxWidth(Double.MAX_VALUE);
        cmbStatus.setDisable(true); // Desabilitado por padrão

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
                txtHora,
                new Label("Status:"),
                cmbStatus
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

        TableColumn<Consulta, StatusConsulta> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colStatus.setPrefWidth(120);

        tabelaConsultas.getColumns().addAll(colId, colIdPet, colIdVet, colData, colHora, colStatus);

        // Evento de seleção na tabela (clique simples para preencher formulário)
        tabelaConsultas.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        preencherFormulario(newSelection);
                    }
                }
        );

        // Evento de clique duplo para abrir o fluxo de estados
        tabelaConsultas.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && event.getButton() == MouseButton.PRIMARY) {
                Consulta selectedConsulta = tabelaConsultas.getSelectionModel().getSelectedItem();
                if (selectedConsulta != null) {
                    abrirFluxoConsulta(selectedConsulta);
                }
            }
        });

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
        // A validação de status nulo só se aplica na atualização
        if (!txtId.getText().trim().isEmpty() && cmbStatus.getValue() == null) {
            throw new IllegalArgumentException("Status é obrigatório!");
        }

        Consulta consulta = new Consulta();
        consulta.setId_pet(Integer.parseInt(txtIdPet.getText().trim()));
        consulta.setId_veterinario(Integer.parseInt(txtIdVeterinario.getText().trim()));

        // Converter data do DatePicker para LocalDateTime
        LocalDateTime dataHora = datePicker.getValue().atStartOfDay();
        consulta.setData(dataHora);

        consulta.setHora(txtHora.getText().trim());

        // Se for uma nova consulta (sem ID), o status é sempre CRIADA
        if (txtId.getText().trim().isEmpty()) {
            consulta.setStatus(StatusConsulta.CRIADA);
        } else {
            consulta.setStatus(cmbStatus.getValue());
        }

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
        // Garante que o status não é nulo ao preencher o ComboBox
        cmbStatus.setValue(consulta.getStatus() != null ? consulta.getStatus() : StatusConsulta.CRIADA);
        cmbStatus.setDisable(true); // Mantém o status desabilitado
    }

    private void limparFormulario() {
        txtId.clear();
        txtIdPet.clear();
        txtIdVeterinario.clear();
        datePicker.setValue(null);
        txtHora.clear();
        cmbStatus.setValue(StatusConsulta.CRIADA); // Resetar para o status padrão
        cmbStatus.setDisable(true); // Desabilita na criação de nova consulta
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

    public void mostrarSucesso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sucesso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public void mostrarErro(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erro");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public void mostrarAviso(String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    // Método para abrir a janela de fluxo da consulta
    private void abrirFluxoConsulta(Consulta consulta) {
        Stage fluxoStage = new Stage();
        fluxoStage.initModality(Modality.APPLICATION_MODAL);
        fluxoStage.setTitle("Fluxo da Consulta ID: " + consulta.getId_consulta());

        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_LEFT);

        Label lblTitulo = new Label("Gerenciar Fluxo da Consulta");
        lblTitulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        // Combina data e hora para exibição correta
        LocalDateTime dataHoraAgendada = getCombinedDateTime(consulta);
        String dataAgendadaStr = (dataHoraAgendada != null) ? dataHoraAgendada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";

        Label lblDetalhes = new Label(
                "Pet: " + consulta.getId_pet() +
                " | Veterinário: " + consulta.getId_veterinario() +
                " | Agendado para: " + dataAgendadaStr
        );
        lblDetalhes.setStyle("-fx-font-size: 14px;");

        // Painel para o conteúdo dinâmico de cada estado
        StackPane contentPane = new StackPane();
        contentPane.setPadding(new Insets(10, 0, 0, 0));

        // --- Layouts para cada estado ---
        VBox layoutCriada = criarLayoutCriada(consulta, fluxoStage);
        VBox layoutEmAndamento = criarLayoutEmAndamento(consulta, fluxoStage);
        VBox layoutFinalizada = criarLayoutFinalizada(consulta, fluxoStage);
        VBox layoutCancelada = criarLayoutCancelada(consulta, fluxoStage);
        VBox layoutReagendada = criarLayoutReagendada(consulta, fluxoStage);

        contentPane.getChildren().addAll(layoutCriada, layoutEmAndamento, layoutFinalizada, layoutCancelada, layoutReagendada);

        // Esconde todos e mostra apenas o layout do estado atual
        layoutCriada.setVisible(false);
        layoutEmAndamento.setVisible(false);
        layoutFinalizada.setVisible(false);
        layoutCancelada.setVisible(false);
        layoutReagendada.setVisible(false);

        // Garante que o status não é nulo para evitar NullPointerException
        StatusConsulta currentStatus = consulta.getStatus() != null ? consulta.getStatus() : StatusConsulta.CRIADA;

        switch (currentStatus) {
            case CRIADA:
                layoutCriada.setVisible(true);
                break;
            case EM_ANDAMENTO:
                layoutEmAndamento.setVisible(true);
                break;
            case FINALIZADA:
                layoutFinalizada.setVisible(true);
                break;
            case CANCELADA:
                layoutCancelada.setVisible(true);
                break;
            case REAGENDADA:
                layoutReagendada.setVisible(true);
                break;
        }

        root.getChildren().addAll(lblTitulo, lblDetalhes, contentPane);

        Scene scene = new Scene(root, 700, 500);
        fluxoStage.setScene(scene);
        fluxoStage.showAndWait();
    }

    private VBox criarLayoutCriada(Consulta consulta, Stage fluxoStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #e0f7fa; -fx-border-color: #00bcd4; -fx-border-radius: 5;");

        Label lblStatus = new Label("Status Atual: " + consulta.getStatus().getDescricao());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Combina data e hora para exibição e cálculo corretos
        LocalDateTime dataHoraAgendada = getCombinedDateTime(consulta);

        // Data e hora previstas
        String previsaoStr = (dataHoraAgendada != null) ? dataHoraAgendada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";
        Label lblPrevisao = new Label("Data: " + previsaoStr);

        // Campo para observações (edição)
        TextArea txtObservacoes = new TextArea(consulta.getObservacoes(StatusConsulta.CRIADA));
        txtObservacoes.setPromptText("Adicionar observações rápidas...");
        txtObservacoes.setPrefRowCount(2);

        Button btnSalvarObs = new Button("Salvar Observação");
        btnSalvarObs.setOnAction(e -> {
            consulta.setObservacoes(StatusConsulta.CRIADA, txtObservacoes.getText());
            atualizarConsultaNoDataManager(consulta, fluxoStage);
        });

        HBox botoesAcao = new HBox(10);
        botoesAcao.setAlignment(Pos.CENTER);

        Button btnIniciar = new Button("✅ Iniciar Consulta");
        Button btnCancelar = new Button("❌ Cancelar Consulta");
        Button btnReagendar = new Button("🔁 Reagendar");

        btnIniciar.setOnAction(e -> {
            consulta.setDataInicioReal(LocalDateTime.now()); // Registra o início real
            atualizarStatusConsulta(consulta, StatusConsulta.EM_ANDAMENTO, fluxoStage);
        });
        btnCancelar.setOnAction(e -> mostrarDialogoCancelamento(consulta, fluxoStage));
        btnReagendar.setOnAction(e -> mostrarDialogoReagendamento(consulta, fluxoStage));

        botoesAcao.getChildren().addAll(btnIniciar, btnCancelar, btnReagendar);

        layout.getChildren().addAll(lblStatus, lblPrevisao, new Label("Observações:"), txtObservacoes, btnSalvarObs, botoesAcao);
        return layout;
    }

    private VBox criarLayoutEmAndamento(Consulta consulta, Stage fluxoStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #fffde7; -fx-border-color: #ffeb3b; -fx-border-radius: 5;");

        Label lblStatus = new Label("Status Atual: " + consulta.getStatus().getDescricao());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Tempo decorrido
        String tempoDecorrido = "N/A";
        if (consulta.getDataInicioReal() != null) {
            Duration duration = Duration.between(consulta.getDataInicioReal(), LocalDateTime.now());
            long minutes = duration.toMinutes();
            tempoDecorrido = minutes + " min";
        }
        Label lblTempoDecorrido = new Label("Em atendimento há: " + tempoDecorrido);

        // Campo para observações rápidas
        TextArea txtObservacoes = new TextArea(consulta.getObservacoes(StatusConsulta.EM_ANDAMENTO));
        txtObservacoes.setPromptText("Observações durante o atendimento...");
        txtObservacoes.setPrefRowCount(3);

        Button btnSalvarObs = new Button("Salvar Observação");
        btnSalvarObs.setOnAction(e -> {
            consulta.setObservacoes(StatusConsulta.EM_ANDAMENTO, txtObservacoes.getText());
            atualizarConsultaNoDataManager(consulta, fluxoStage);
        });

        HBox botoesAcao = new HBox(10);
        botoesAcao.setAlignment(Pos.CENTER);

        Button btnAbrirDiagnostico = new Button("🧾 Abrir Ficha de Atendimento");
        Button btnFinalizar = new Button("✅ Finalizar Consulta");
        Button btnCancelar = new Button("❌ Cancelar");

        btnAbrirDiagnostico.setOnAction(e -> abrirDiagnosticoController(consulta.getId_consulta()));
        btnFinalizar.setOnAction(e -> {
            consulta.setDataFimReal(LocalDateTime.now()); // Registra o fim real
            atualizarStatusConsulta(consulta, StatusConsulta.FINALIZADA, fluxoStage);
        });
        btnCancelar.setOnAction(e -> mostrarDialogoCancelamento(consulta, fluxoStage));

        botoesAcao.getChildren().addAll(btnAbrirDiagnostico, btnFinalizar, btnCancelar);

        layout.getChildren().addAll(lblStatus, lblTempoDecorrido, new Label("Observações:"), txtObservacoes, btnSalvarObs, botoesAcao);
        return layout;
    }

    private VBox criarLayoutFinalizada(Consulta consulta, Stage fluxoStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #e8f5e9; -fx-border-color: #4caf50; -fx-border-radius: 5;");

        Label lblStatus = new Label("Status Atual: " + consulta.getStatus().getDescricao());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblInicioFim = new Label(
                "Início: " + (consulta.getDataInicioReal() != null ? consulta.getDataInicioReal().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A") +
                " | Fim: " + (consulta.getDataFimReal() != null ? consulta.getDataFimReal().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A")
        );

        Label lblVeterinario = new Label("Veterinário ID: " + consulta.getId_veterinario());

        // Carregar diagnóstico
        Optional<Diagnostico> diagnosticoOpt = carregarDiagnosticoDaConsulta(consulta.getId_consulta());
        String nomeDiagnostico = diagnosticoOpt.map(Diagnostico::getNomeDiagnostico).orElse("[Não encontrado]");
        String prescricoes = diagnosticoOpt.map(Diagnostico::getMedicamentosPrescritos).orElse("[Não encontrado]");

        TextArea txtObservacoes = new TextArea(consulta.getObservacoes(StatusConsulta.FINALIZADA));
        txtObservacoes.setEditable(false);
        txtObservacoes.setPrefRowCount(3);

        HBox botoesAcao = new HBox(10);
        botoesAcao.setAlignment(Pos.CENTER);

        Button btnGerarRelatorio = new Button("📄 Gerar Relatório");
        Button btnReabrir = new Button("↩️ Reabrir Consulta");

        btnGerarRelatorio.setOnAction(e -> gerarRelatorio(consulta, diagnosticoOpt));
         btnReabrir.setOnAction(e -> atualizarStatusConsulta(consulta, StatusConsulta.EM_ANDAMENTO, fluxoStage));

        botoesAcao.getChildren().addAll(btnGerarRelatorio, btnReabrir);

        layout.getChildren().addAll(lblStatus, lblInicioFim, lblVeterinario, new Label("Observações:"), txtObservacoes, botoesAcao);
        return layout;
    }

    private VBox criarLayoutCancelada(Consulta consulta, Stage fluxoStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #ffebee; -fx-border-color: #f44336; -fx-border-radius: 5;");

        Label lblStatus = new Label("Status Atual: " + consulta.getStatus().getDescricao());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        // Tratamento para motivoCancelamento que pode ser null
        String motivo = consulta.getMotivoCancelamento();
        Label lblMotivo = new Label("Motivo do Cancelamento: " + (motivo == null || motivo.isEmpty() ? "Não informado" : motivo));
        Label lblDataCancelamento = new Label("Cancelada em: " + (consulta.getDataCancelamento() != null ? consulta.getDataCancelamento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A"));

        HBox botoesAcao = new HBox(10);
        botoesAcao.setAlignment(Pos.CENTER);

        Button btnReagendar = new Button("🔁 Reagendar");
        btnReagendar.setOnAction(e -> mostrarDialogoReagendamento(consulta, fluxoStage));

        botoesAcao.getChildren().addAll(btnReagendar);

        layout.getChildren().addAll(lblStatus, lblMotivo, lblDataCancelamento, botoesAcao);
        return layout;
    }

    private VBox criarLayoutReagendada(Consulta consulta, Stage fluxoStage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.setStyle("-fx-background-color: #fff8e1; -fx-border-color: #ffc107; -fx-border-radius: 5;");

        Label lblStatus = new Label("Status Atual: " + consulta.getStatus().getDescricao());
        lblStatus.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblContador = new Label("Reagendada " + consulta.getContadorReagendamentos() + " vez(es).");
        
        LocalDateTime dataHoraAgendada = getCombinedDateTime(consulta);
        String novaDataStr = (dataHoraAgendada != null) ? dataHoraAgendada.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A";
        Label lblNovaData = new Label("Nova Data/Hora: " + novaDataStr);

        HBox botoesAcao = new HBox(10);
        botoesAcao.setAlignment(Pos.CENTER);

        Button btnConfirmar = new Button("✅ Confirmar Nova Data"); // Ação de confirmar já foi feita ao reagendar
        Button btnCancelarNovamente = new Button("❌ Cancelar Novamente");

        btnConfirmar.setDisable(true); // Já está reagendada, esta ação seria para uma nova data
        btnCancelarNovamente.setOnAction(e -> mostrarDialogoCancelamento(consulta, fluxoStage));

        botoesAcao.getChildren().addAll(btnConfirmar, btnCancelarNovamente);

        layout.getChildren().addAll(lblStatus, lblContador, lblNovaData, botoesAcao);
        return layout;
    }

    public void atualizarStatusConsulta(Consulta consulta, StatusConsulta novoStatus, Stage fluxoStage) {
        try {
            consulta.setStatus(novoStatus);
            List<Consulta> consultasExistentes = dataManager.carregarConsultas();
            boolean encontrada = false;
            for (int i = 0; i < consultasExistentes.size(); i++) {
                if (consultasExistentes.get(i).getId_consulta() == consulta.getId_consulta()) {
                    consultasExistentes.set(i, consulta);
                    encontrada = true;
                    break;
                }
            }

            if (encontrada) {
                dataManager.salvarConsultas(consultasExistentes);
                mostrarSucesso("Status da consulta atualizado para: " + novoStatus.getDescricao());
                atualizarTabela(); // Atualiza a tabela principal
                fluxoStage.close(); // Fecha a janela de fluxo
            } else {
                mostrarErro("Erro: Consulta não encontrada para atualização de status.");
            }

        } catch (IOException | ClassNotFoundException ex) {
            mostrarErro("Erro ao atualizar status da consulta: " + ex.getMessage());
        }
    }

    public void atualizarConsultaNoDataManager(Consulta consulta, Stage fluxoStage) {
        try {
            List<Consulta> consultasExistentes = dataManager.carregarConsultas();
            boolean encontrada = false;
            for (int i = 0; i < consultasExistentes.size(); i++) {
                if (consultasExistentes.get(i).getId_consulta() == consulta.getId_consulta()) {
                    consultasExistentes.set(i, consulta);
                    encontrada = true;
                    break;
                }
            }

            if (encontrada) {
                dataManager.salvarConsultas(consultasExistentes);
                atualizarTabela(); // Atualiza a tabela principal
                // Não fecha a janela de fluxo aqui, apenas atualiza o estado visual se necessário
            } else {
                mostrarErro("Erro: Consulta não encontrada para atualização de dados.");
            }

        } catch (IOException | ClassNotFoundException ex) {
            mostrarErro("Erro ao atualizar dados da consulta: " + ex.getMessage());
        }
    }

    public void mostrarDialogoCancelamento(Consulta consulta, Stage fluxoStage) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cancelar Consulta");
        dialog.setHeaderText("Informe o motivo do cancelamento da consulta ID: " + consulta.getId_consulta());
        dialog.setContentText("Motivo:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(motivo -> {
            consulta.setMotivoCancelamento(motivo);
            consulta.setDataCancelamento(LocalDateTime.now());
            atualizarStatusConsulta(consulta, StatusConsulta.CANCELADA, fluxoStage);
        });
    }

    public void mostrarDialogoReagendamento(Consulta consulta, Stage fluxoStage) {
        Dialog<LocalDateTime> dialog = new Dialog<>();
        dialog.setTitle("Reagendar Consulta");
        dialog.setHeaderText("Selecione a nova data e hora para a consulta ID: " + consulta.getId_consulta());

        ButtonType okButtonType = new ButtonType("Reagendar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        DatePicker novaDataPicker = new DatePicker(consulta.getData() != null ? consulta.getData().toLocalDate() : null);
        TextField novaHoraField = new TextField(consulta.getHora());
        novaHoraField.setPromptText("HH:mm");

        grid.add(new Label("Nova Data:"), 0, 0);
        grid.add(novaDataPicker, 1, 0);
        grid.add(new Label("Nova Hora:"), 0, 1);
        grid.add(novaHoraField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == okButtonType) {
                if (novaDataPicker.getValue() != null && !novaHoraField.getText().trim().isEmpty()) {
                    try {
                        LocalDateTime novaDataHora = novaDataPicker.getValue().atStartOfDay().withHour(Integer.parseInt(novaHoraField.getText().split(":")[0])).withMinute(Integer.parseInt(novaHoraField.getText().split(":")[1]));
                        return novaDataHora;
                    } catch (Exception e) {
                        mostrarErro("Formato de hora inválido. Use HH:mm.");
                        return null;
                    }
                }
            }
            return null;
        });

        Optional<LocalDateTime> result = dialog.showAndWait();
        result.ifPresent(novaDataHora -> {
            consulta.setData(novaDataHora); // Atualiza a data agendada
            consulta.setHora(novaDataHora.format(DateTimeFormatter.ofPattern("HH:mm")));
            consulta.incrementarContadorReagendamentos();
            atualizarStatusConsulta(consulta, StatusConsulta.REAGENDADA, fluxoStage);
        });
    }

    public void abrirDiagnosticoController(int consultaId) {
        try {
            DiagnosticoController diagnosticoController = new DiagnosticoController();
            diagnosticoController.setInitialConsultaId(consultaId);
            Stage stage = new Stage();
            diagnosticoController.start(stage);
        } catch (Exception e) {
            mostrarErro("Erro ao abrir a ficha de atendimento: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Optional<Diagnostico> carregarDiagnosticoDaConsulta(int consultaId) {
        try {
            List<Diagnostico> todosDiagnosticos = DiagnosticoDataManager.getInstance().carregarDiagnosticos();
            return todosDiagnosticos.stream()
                    .filter(d -> d.getId_consulta() == consultaId)
                    .findFirst();
        } catch (IOException | ClassNotFoundException e) {
            mostrarErro("Erro ao carregar diagnóstico: " + e.getMessage());
            return Optional.empty();
        }
    }

    private void visualizarDiagnostico(Optional<Diagnostico> diagnosticoOpt) {
        if (diagnosticoOpt.isPresent()) {
            Diagnostico d = diagnosticoOpt.get();
            String detalhes = String.format(
                "ID do Diagnóstico: %d\n" +
                "Nome: %s\n" +
                "Data: %s\n" +
                "Tratamento: %s\n" +
                "Medicamentos: %s",
                d.getId_diagnostico(),
                d.getNomeDiagnostico(),
                d.getDataDiagnostico().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                d.getTratamento(),
                d.getMedicamentosPrescritos()
            );
            mostrarAlerta("Detalhes do Diagnóstico", detalhes);
        } else {
            mostrarAviso("Nenhum diagnóstico encontrado para esta consulta.");
        }
    }

    private void gerarRelatorio(Consulta consulta, Optional<Diagnostico> diagnosticoOpt) {
        StringBuilder relatorio = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        relatorio.append("--- Relatório da Consulta ---\n");
        relatorio.append("ID da Consulta: ").append(consulta.getId_consulta()).append("\n");
        relatorio.append("ID do Pet: ").append(consulta.getId_pet()).append("\n");
        relatorio.append("ID do Veterinário: ").append(consulta.getId_veterinario()).append("\n");
        relatorio.append("Status: ").append(consulta.getStatus().getDescricao()).append("\n\n");

        relatorio.append("--- Histórico ---\n");
        LocalDateTime dataHoraAgendada = getCombinedDateTime(consulta);
        relatorio.append("Agendado para: ").append(dataHoraAgendada != null ? dataHoraAgendada.format(formatter) : "N/A").append("\n");
        relatorio.append("Início Real: ").append(consulta.getDataInicioReal() != null ? consulta.getDataInicioReal().format(formatter) : "N/A").append("\n");
        relatorio.append("Fim Real: ").append(consulta.getDataFimReal() != null ? consulta.getDataFimReal().format(formatter) : "N/A").append("\n");

        if (consulta.getStatus() == StatusConsulta.CANCELADA) {
            relatorio.append("Cancelada em: ").append(consulta.getDataCancelamento() != null ? consulta.getDataCancelamento().format(formatter) : "N/A").append("\n");
            relatorio.append("Motivo: ").append(consulta.getMotivoCancelamento()).append("\n");
        }

        relatorio.append("\n--- Observações ---\n");
        String obsCriada = consulta.getObservacoes(StatusConsulta.CRIADA);
        if (!obsCriada.isEmpty()) {
            relatorio.append("Na Criação: ").append(obsCriada).append("\n");
        }
        String obsEmAndamento = consulta.getObservacoes(StatusConsulta.EM_ANDAMENTO);
        if (!obsEmAndamento.isEmpty()) {
            relatorio.append("Em Andamento: ").append(obsEmAndamento).append("\n");
        }
        String obsFinalizada = consulta.getObservacoes(StatusConsulta.FINALIZADA);
        if (!obsFinalizada.isEmpty()) {
            relatorio.append("Na Finalização: ").append(obsFinalizada).append("\n");
        }

        relatorio.append("\n--- Diagnóstico ---\n");
        if (diagnosticoOpt.isPresent()) {
            Diagnostico d = diagnosticoOpt.get();
            relatorio.append("Nome: ").append(d.getNomeDiagnostico()).append("\n");
            relatorio.append("Data: ").append(d.getDataDiagnostico().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            relatorio.append("Tratamento: ").append(d.getTratamento()).append("\n");
            relatorio.append("Medicamentos: ").append(d.getMedicamentosPrescritos()).append("\n");
        } else {
            relatorio.append("Nenhum diagnóstico registrado para esta consulta.\n");
        }

        // Exibir o relatório em um diálogo com área de texto
        TextArea textArea = new TextArea(relatorio.toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);

        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(textArea, 0, 0);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Relatório da Consulta");
        alert.setHeaderText("Relatório completo da consulta ID: " + consulta.getId_consulta());
        alert.getDialogPane().setContent(gridPane);
        alert.setResizable(true);
        alert.showAndWait();
    }

    private LocalDateTime getCombinedDateTime(Consulta consulta) {
        if (consulta.getData() != null && consulta.getHora() != null && !consulta.getHora().isEmpty()) {
            try {
                String[] partesHora = consulta.getHora().split(":");
                int hora = Integer.parseInt(partesHora[0]);
                int minuto = Integer.parseInt(partesHora[1]);
                return consulta.getData().toLocalDate().atTime(hora, minuto);
            } catch (Exception e) {
                // Em caso de erro no parsing da hora, retorna apenas a data
                return consulta.getData().toLocalDate().atStartOfDay();
            }
        }
        return null;
    }


    // Método auxiliar para mostrar alertas de informação
    public void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
