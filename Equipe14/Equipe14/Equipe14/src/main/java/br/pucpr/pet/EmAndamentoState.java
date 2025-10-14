package br.pucpr.pet;

import javafx.stage.Stage;
import java.time.LocalDateTime;

public class EmAndamentoState implements ConsultaState {

    @Override
    public void iniciar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A consulta já está em andamento.");
    }

    @Override
    public void finalizar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        consulta.setDataFimReal(LocalDateTime.now());
        consulta.setStatus(StatusConsulta.FINALIZADA);
        controller.atualizarConsultaNoDataManager(consulta, fluxoStage);
        controller.mostrarSucesso("Consulta finalizada com sucesso!");
        fluxoStage.close();
    }

    @Override
    public void cancelar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarDialogoCancelamento(consulta, fluxoStage);
    }

    @Override
    public void reagendar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Não é possível reagendar uma consulta que está em andamento. Cancele-a primeiro.");
    }

    @Override
    public void reabrir(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A consulta já está em andamento. Não pode ser reaberta.");
    }

    @Override
    public void abrirFichaAtendimento(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.abrirDiagnosticoController(consulta.getId_consulta()); // Chama o método para abrir a tela de diagnóstico
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
        return StatusConsulta.EM_ANDAMENTO;
    }
}
