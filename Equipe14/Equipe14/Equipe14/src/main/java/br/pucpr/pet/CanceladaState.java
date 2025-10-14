package br.pucpr.pet;

import javafx.stage.Stage;

public class CanceladaState implements ConsultaState {

    @Override
    public void iniciar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Não é possível iniciar uma consulta cancelada.");
    }

    @Override
    public void finalizar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Não é possível finalizar uma consulta cancelada.");
    }

    @Override
    public void cancelar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A consulta já está cancelada.");
    }

    @Override
    public void reagendar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarDialogoReagendamento(consulta, fluxoStage);
    }

    @Override
    public void reabrir(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Para reabrir, reagende a consulta.");
    }

    @Override
    public void abrirFichaAtendimento(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Não há ficha de atendimento para uma consulta cancelada.");
    }

    @Override
    public void gerarRelatorio(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Não há relatório para uma consulta cancelada.");
    }

    @Override
    public void visualizarDiagnostico(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Não há diagnóstico para uma consulta cancelada.");
    }

    @Override
    public StatusConsulta getStatusEnum() {
        return StatusConsulta.CANCELADA;
    }
}
