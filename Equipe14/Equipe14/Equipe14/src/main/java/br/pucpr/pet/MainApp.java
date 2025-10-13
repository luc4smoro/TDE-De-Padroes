package br.pucpr.pet;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Sistema de Gerenciamento");

        Button btnPet = new Button("Cadastro de Pet");
        btnPet.setPrefWidth(200);
        btnPet.setOnAction(_ -> {
            PetController petController = new PetController();
            Stage petStage = new Stage();
            try {
                petController.start(petStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        Button btnTutor = new Button("Cadastro de Tutor");
        btnTutor.setPrefWidth(200);
        btnTutor.setOnAction(_ -> {
            TutorController tutorController = new TutorController();
            Stage tutorStage = new Stage();
            try {
                tutorController.start(tutorStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button btnVeterinario = new Button("Cadastro de Veterinário");
        btnVeterinario.setPrefWidth(200);
        btnVeterinario.setOnAction(_ -> {
            VeterinarioController veterinarioController = new VeterinarioController();
            Stage veterinarioStage = new Stage();
            try {
                veterinarioController.start(veterinarioStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button btnDiagnostico = new Button("Criar Diagnostico");
        btnDiagnostico.setPrefWidth(200);
        btnDiagnostico.setOnAction(_ -> {
            DiagnosticoController diagnosticoController = new DiagnosticoController();
            Stage diagnosticoStage = new Stage();
            try {
                diagnosticoController.start(diagnosticoStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        Button btnConsulta = new Button("Criar Consulta");
        btnConsulta.setPrefWidth(200);
        btnConsulta.setOnAction(_ -> {
            ConsultaController consultaController = new ConsultaController();
            Stage consultaStage = new Stage();
            try {
                consultaController.start(consultaStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });





        VBox layout = new VBox(15);
        layout.setStyle("-fx-padding: 30; -fx-alignment: center;");
        layout.getChildren().addAll(btnPet, btnTutor, btnVeterinario, btnDiagnostico, btnConsulta);


        Scene scene = new Scene(layout, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
