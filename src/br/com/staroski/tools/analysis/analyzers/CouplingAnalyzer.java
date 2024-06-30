package br.com.staroski.tools.analysis.analyzers;

import java.util.Set;

import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.MetricsVisitors;
import br.com.staroski.tools.analysis.Project;

/**
 * This class iterates over a {@link Set} of {@link ProjectImpl} and computes its <b>efferent coupling</b> ("Ce" - output dependencies) and <b>afferent
 * coupling</b> ("Ca" - input dependencies).<br>
 * For short:<br>
 * "Ce" means "how many components I depend on".<br>
 * "Ca" means "how many components depend on me".
 *
 * @author Staroski, Ricardo Artur
 */
public final class CouplingAnalyzer {

    private final class InternalListener implements CouplingAnalyzerListener {

        @Override
        public void onCouplingAnalysisStarted(CouplingAnalysisEvent event) {
            System.out.println("Coupling analysis of project \"" + event.getProject().getName() + "\" started...");
        }

        @Override
        public void onCouplingAnalysisFinished(CouplingAnalysisEvent event) {
            System.out.println("Coupling analysis of project \"" + event.getProject().getName() + "\" finished!");
        }
    }

    private CouplingAnalyzerListener listener = new InternalListener();

    public void addCouplingAnalyzerListener(CouplingAnalyzerListener listener) {
        this.listener = Listeners.addCouplingAnalyzerListener(this.listener, listener);
    }

    public void removeCouplingAnalyzerListener(CouplingAnalyzerListener listener) {
        this.listener = Listeners.removeCouplingAnalyzerListener(this.listener, listener);
    }

    public void analyze(Set<Project> projects) {
        for (Project project : projects) {
            updateStats(project, projects);
        }
    }

    private void updateStats(Project project, Set<Project> allProjects) {

        listener.onCouplingAnalysisStarted(new CouplingAnalysisEvent(project));

        Metrics stats = project.getMetrics();

        // compute efferent coupling
        int outputDependencies = project.getProjectDependencies().size();
        for (int i = 0; i < outputDependencies; i++) {
            stats.accept(MetricsVisitors.incrementOutputDependencies());
        }

        // compute afferent coupling
        for (Project otherProject : allProjects) {
            if (project.equals(otherProject)) {
                continue;
            }
            if (otherProject.getProjectDependencies().contains(project)) {
                stats.accept(MetricsVisitors.incrementInputDependencies());
            }
        }

        listener.onCouplingAnalysisFinished(new CouplingAnalysisEvent(project));
    }
}
