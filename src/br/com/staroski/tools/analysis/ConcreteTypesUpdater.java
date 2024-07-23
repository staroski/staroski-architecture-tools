package br.com.staroski.tools.analysis;

/**
 * {@link MetricsVisitor} implementation that updates the number of concrete types of a {@link Project}'s {@link Metrics}.
 * 
 * @author Staroski, Ricardo Artur
 */
final class ConcreteTypesUpdater implements MetricsVisitor {

    @Override
    public void visit(Metrics metrics) {
        metrics.incrementConcreteTypes();
    }

}
