package br.com.staroski.tools.analysis.analyzers;

import br.com.staroski.tools.analysis.Project;

/**
 * Event that the {@link DependencyAnalyzer} sends to the {@link DependencyAnalyzerListener}.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class DependencyAnalysisEvent {

    private Project project;

    DependencyAnalysisEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
