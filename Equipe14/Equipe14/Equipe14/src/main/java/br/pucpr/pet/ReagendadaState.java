package br.pucpr.pet;

import javafx.stage.Stage;

public class ReagendadaState implements ConsultaState {

    @Override
    public void iniciar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A consulta foi reagendada. Inicie-a na nova data/hora.");
        // Opcional: permitir iniciar se a nova data/hora for agora
    }

    @Override
    public void finalizar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A consulta reagendada ainda não foi iniciada para ser finalizada.");
    }

    @Override
    public void cancelar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarDialogoCancelamento(consulta, fluxoStage);
    }

    @Override
    public void reagendar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarDialogoReagendamento(consulta, fluxoStage);
    }

    @Override
    public void reabrir(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A consulta reagendada não pode ser reaberta diretamente.");
    }

    @Override
    public void abrirFichaAtendimento(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Não há ficha de atendimento para uma consulta reagendada que não foi iniciada.");
    }

    @Override
    public void gerarRelatorio(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Relatório só pode ser gerado para consultas 'Finalizadas'.");
    }

    @Override
    public void visualizarDiagnostico(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Diagnóstico só pode ser visualizado para consultas 'Finalizadas'.");
    }

    @Override
    public StatusConsulta getStatusEnum() {
        return StatusConsulta.REAGENDADA;
    }
}
