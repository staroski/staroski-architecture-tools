package br.com.staroski.tools.analysis;

/**
 * {@link MetricsVisitor} implementation that updates the number of abstract types os a {@link Project}'s {@link Metrics}.
 * 
 * @author Staroski, Ricardo Artur
 */
final class AbstractTypesUpdater implements MetricsVisitor {

    @Override
    public void visit(Metrics metrics) {
        metrics.incrementAbstractTypes();
    }
}
