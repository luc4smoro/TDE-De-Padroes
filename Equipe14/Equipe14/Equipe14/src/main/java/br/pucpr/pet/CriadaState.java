package br.pucpr.pet;

import javafx.stage.Stage;
import java.time.LocalDateTime;

public class CriadaState implements ConsultaState {

    @Override
    public void iniciar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        consulta.setDataInicioReal(LocalDateTime.now());
        consulta.setStatus(StatusConsulta.EM_ANDAMENTO);
        controller.atualizarConsultaNoDataManager(consulta, fluxoStage);
        controller.mostrarSucesso("Consulta iniciada com sucesso!");
        fluxoStage.close();
    }

    @Override
    public void finalizar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A consulta ainda não foi iniciada.");
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
        controller.mostrarAviso("A consulta não pode ser reaberta do estado 'Criada'.");
    }

    @Override
    public void abrirFichaAtendimento(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A ficha de atendimento só pode ser aberta quando a consulta estiver 'Em Andamento'.");
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
        return StatusConsulta.CRIADA;
    }
}
