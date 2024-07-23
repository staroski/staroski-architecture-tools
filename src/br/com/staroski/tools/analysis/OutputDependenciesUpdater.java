package br.com.staroski.tools.analysis;

/**
 * {@link MetricsVisitor} implementation that updates the number of output dependencies of a {@link Project}'s {@link Metrics}.
 * 
 * @author Staroski, Ricardo Artur
 */
final class OutputDependenciesUpdater implements MetricsVisitor {

    @Override
    public void visit(Metrics metrics) {
        metrics.incrementOutputDependencies();
    }
}
