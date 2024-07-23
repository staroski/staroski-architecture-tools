package br.com.staroski.tools.analysis;

import java.util.List;

/**
 * {@link MetricsVisitor} implementation that updates the {@link Cycle}s of a {@link Project}'s {@link Metrics}.
 * 
 * @author Staroski, Ricardo Artur
 */
final class CyclesUpdater implements MetricsVisitor {

    private final List<Cycle> cycles;

    CyclesUpdater(List<Cycle> cycles) {
        this.cycles = cycles;
    }

    @Override
    public void visit(Metrics metrics) {
        metrics.setCycles(cycles);
    }
}
