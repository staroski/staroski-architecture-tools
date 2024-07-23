package br.com.staroski.tools.analysis;

/**
 * {@link MetricsVisitor} implementation that updates the input dependencies of a {@link Project}'s {@link Metrics}.
 * 
 * @author Staroski, Ricardo Artur
 */
final class InputDependenciesUpdater implements MetricsVisitor {

    @Override
    public void visit(Metrics metrics) {
        metrics.incrementInputDependencies();
    }
}
