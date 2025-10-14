package br.pucpr.pet;

import javafx.stage.Stage;

public class FinalizadaState implements ConsultaState {

    @Override
    public void iniciar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A consulta já foi finalizada. Não pode ser iniciada novamente.");
    }

    @Override
    public void finalizar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("A consulta já está finalizada.");
    }

    @Override
    public void cancelar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Não é possível cancelar uma consulta finalizada.");
    }

    @Override
    public void reagendar(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAviso("Não é possível reagendar uma consulta finalizada.");
    }

    @Override
    public void reabrir(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        // Permite reabrir a consulta, voltando para o estado EM_ANDAMENTO
        consulta.setStatus(StatusConsulta.EM_ANDAMENTO);
        controller.atualizarConsultaNoDataManager(consulta, fluxoStage);
        controller.mostrarSucesso("Consulta reaberta para 'Em Andamento'.");
        fluxoStage.close();
    }

    @Override
    public void abrirFichaAtendimento(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.abrirDiagnosticoController(consulta.getId_consulta()); // Pode abrir para visualização ou edição se reaberta
    }

    @Override
    public void gerarRelatorio(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAlerta("Funcionalidade", "Gerar Relatório/Recibo - Não implementado.");
    }

    @Override
    public void visualizarDiagnostico(Consulta consulta, ConsultaController controller, Stage fluxoStage) {
        controller.mostrarAlerta("Funcionalidade", "Visualizar Diagnóstico - Não implementado.");
    }

    @Override
    public StatusConsulta getStatusEnum() {
        return StatusConsulta.FINALIZADA;
    }
}
