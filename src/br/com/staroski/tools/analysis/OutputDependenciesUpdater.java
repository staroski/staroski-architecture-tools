package br.com.staroski.tools.analysis;

final class OutputDependenciesUpdater implements MetricsVisitor {

    @Override
    public void visit(Metrics metrics) {
        DefaultMetrics implementation = (DefaultMetrics) metrics;
        implementation.incrementOutputDependencies();
    }
}
