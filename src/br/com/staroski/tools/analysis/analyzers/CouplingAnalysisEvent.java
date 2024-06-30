package br.com.staroski.tools.analysis.analyzers;

import br.com.staroski.tools.analysis.Project;

/**
 * Event that the {@link CouplingAnalyzer} sends to the {@link CouplingAnalyzerListener}.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class CouplingAnalysisEvent {

    private Project project;

    CouplingAnalysisEvent(Project project) {
        this.project = project;
    }

    public Project getProject() {
        return project;
    }
}
