package br.pucpr.pet;

/**
 * Concrete Decorator A
 * Adiciona a responsabilidade de um exame de sangue.
 */
public class ExameDeSangueDecorator extends ServicoAdicionalDecorator {

    public ExameDeSangueDecorator(ServicoConsulta servicoDecorado) {
        super(servicoDecorado);
    }

    @Override
    public String getDescricao() {
        return servicoDecorado.getDescricao() + ", com Exame de Sangue";
    }

    @Override
    public double getCusto() {
        return servicoDecorado.getCusto() + 80.0; // Custo adicional do exame
    }
}
