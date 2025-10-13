package br.pucpr.pet;

/**
 * Concrete Decorator B
 * Adiciona a responsabilidade de uma vacinação.
 */
public class VacinacaoDecorator extends ServicoAdicionalDecorator {

    public VacinacaoDecorator(ServicoConsulta servicoDecorado) {
        super(servicoDecorado);
    }

    @Override
    public String getDescricao() {
        return servicoDecorado.getDescricao() + ", com Vacinação";
    }

    @Override
    public double getCusto() {
        return servicoDecorado.getCusto() + 50.0; // Custo adicional da vacina
    }
}
