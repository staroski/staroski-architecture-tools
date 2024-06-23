package br.com.staroski.tools.analysis;

final class AcyclicUpdater implements MetricsVisitor {

    private final boolean acyclic;

    AcyclicUpdater(boolean acyclic) {
        this.acyclic = acyclic;
    }

    @Override
    public void visit(Metrics metrics) {
        MetricsImpl implementation = (MetricsImpl) metrics;
        implementation.setAcyclic(acyclic);
    }

}
