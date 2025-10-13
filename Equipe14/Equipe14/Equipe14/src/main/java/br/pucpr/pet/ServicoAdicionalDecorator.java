package br.pucpr.pet;

/**
 * Abstract Decorator
 * Mantém uma referência ao objeto Component e define uma interface que se conforma com a interface do Component.
 */
public abstract class ServicoAdicionalDecorator implements ServicoConsulta {
    protected ServicoConsulta servicoDecorado;

    public ServicoAdicionalDecorator(ServicoConsulta servicoDecorado) {
        this.servicoDecorado = servicoDecorado;
    }

    @Override
    public String getDescricao() {
        return servicoDecorado.getDescricao();
    }

    @Override
    public double getCusto() {
        return servicoDecorado.getCusto();
    }
}
