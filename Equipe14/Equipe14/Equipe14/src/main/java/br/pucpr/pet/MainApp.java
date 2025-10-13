package br.pucpr.pet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalDateTime;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema de Gerenciamento");

        // Botões existentes
        Button btnPet = new Button("Cadastro de Pet");
        btnPet.setPrefWidth(200);
        btnPet.setOnAction(_ -> new PetController().start(new Stage()));

        Button btnTutor = new Button("Cadastro de Tutor");
        btnTutor.setPrefWidth(200);
        btnTutor.setOnAction(_ -> new TutorController().start(new Stage()));

        Button btnVeterinario = new Button("Cadastro de Veterinário");
        btnVeterinario.setPrefWidth(200);
        btnVeterinario.setOnAction(_ -> new VeterinarioController().start(new Stage()));

        Button btnDiagnostico = new Button("Criar Diagnostico");
        btnDiagnostico.setPrefWidth(200);
        btnDiagnostico.setOnAction(_ -> new DiagnosticoController().start(new Stage()));

        Button btnConsulta = new Button("Criar Consulta");
        btnConsulta.setPrefWidth(200);
        btnConsulta.setOnAction(_ -> new ConsultaController().start(new Stage()));


        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center;");
        layout.getChildren().addAll(btnPet, btnTutor, btnVeterinario, btnDiagnostico, btnConsulta);

        Scene scene = new Scene(layout, 300, 350); // Aumentei a altura para o novo botão
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Cria e exibe uma nova janela para demonstrar o padrão Decorator de forma interativa.
     */
    private void showDecoratorDemoStage() {
        Stage demoStage = new Stage();
        demoStage.setTitle("Demonstração do Padrão Decorator");

        Label titleLabel = new Label("Monte sua Consulta:");
        titleLabel.setStyle("-fx-font-weight: bold;");

        // Checkboxes para os serviços adicionais (Decorators)
        CheckBox chkExame = new CheckBox("Adicionar Exame de Sangue (R$ 80,00)");
        CheckBox chkVacina = new CheckBox("Adicionar Vacinação (R$ 50,00)");

        Button btnCalcular = new Button("Calcular Custo e Descrição Final");

        Label descLabel = new Label("Descrição: ...");
        Label custoLabel = new Label("Custo Total: ...");

        // Ação do botão que aplica o padrão Decorator
        btnCalcular.setOnAction(e -> {
            // 1. Cria o objeto base (Componente Concreto)
            ServicoConsulta consulta = new Consulta(1, 1, 1, LocalDateTime.now(), "15:00");

            // 2. Verifica os checkboxes e "decora" o objeto dinamicamente
            if (chkExame.isSelected()) {
                consulta = new ExameDeSangueDecorator(consulta);
            }
            if (chkVacina.isSelected()) {
                consulta = new VacinacaoDecorator(consulta);
            }

            // 3. Exibe o resultado do objeto final, já decorado
            descLabel.setText("Descrição: " + consulta.getDescricao());
            custoLabel.setText("Custo Total: R$ " + String.format("%.2f", consulta.getCusto()));
        });

        VBox demoLayout = new VBox(10);
        demoLayout.setStyle("-fx-padding: 20;");
        demoLayout.getChildren().addAll(titleLabel, chkExame, chkVacina, btnCalcular, descLabel, custoLabel);

        Scene demoScene = new Scene(demoLayout, 400, 250);
        demoStage.setScene(demoScene);
        demoStage.show();
    }

    public static void main(String[] args) {
        // A demonstração no console foi removida.
        // Apenas inicia a aplicação JavaFX.
        launch(args);
    }
}
