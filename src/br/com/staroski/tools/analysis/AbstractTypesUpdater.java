package br.com.staroski.tools.analysis;

final class AbstractTypesUpdater implements MetricsVisitor {

    @Override
    public void visit(Metrics metrics) {
        MetricsImpl implementation = (MetricsImpl) metrics;
        implementation.incrementAbstractTypes();
    }

}
