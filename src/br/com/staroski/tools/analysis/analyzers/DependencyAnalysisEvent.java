package br.com.staroski.tools.analysis.analyzers;

import br.com.staroski.tools.analysis.Project;

/**
 * Event that the {@link DependencyAnalyzer} sends to the {@link DependencyAnalyzerListener}.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class DependencyAnalysisEvent {

    private Project project;
    private int cycles;

    DependencyAnalysisEvent(Project project) {
        this(project, 0);
    }

    DependencyAnalysisEvent(Project project, int cycles) {
        this.project = project;
        this.cycles = cycles;
    }

    public Project getProject() {
        return project;
    }

    public int getCycles() {
        return cycles;
    }
}
