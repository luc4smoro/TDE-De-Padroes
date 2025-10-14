package br.pucpr.pet;

import javafx.stage.Stage;

public interface ConsultaState {
    void iniciar(Consulta consulta, ConsultaController controller, Stage fluxoStage);
    void finalizar(Consulta consulta, ConsultaController controller, Stage fluxoStage);
    void cancelar(Consulta consulta, ConsultaController controller, Stage fluxoStage);
    void reagendar(Consulta consulta, ConsultaController controller, Stage fluxoStage);
    void reabrir(Consulta consulta, ConsultaController controller, Stage fluxoStage);
    void abrirFichaAtendimento(Consulta consulta, ConsultaController controller, Stage fluxoStage);
    void gerarRelatorio(Consulta consulta, ConsultaController controller, Stage fluxoStage);
    void visualizarDiagnostico(Consulta consulta, ConsultaController controller, Stage fluxoStage);

    // Método para obter o StatusConsulta associado a este estado
    StatusConsulta getStatusEnum();
}
