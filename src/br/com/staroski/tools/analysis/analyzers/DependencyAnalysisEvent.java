package br.com.staroski.tools.analysis.analyzers;

import java.util.Collections;
import java.util.List;

import br.com.staroski.tools.analysis.Cycle;
import br.com.staroski.tools.analysis.Project;

/**
 * Event that the {@link DependencyAnalyzer} sends to the {@link DependencyAnalyzerListener}.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class DependencyAnalysisEvent {

    private Project project;
    private List<Cycle> cycles;

    DependencyAnalysisEvent(Project project) {
        this(project, Collections.emptyList());
    }

    DependencyAnalysisEvent(Project project, List<Cycle> cycles) {
        this.project = project;
        this.cycles = cycles;
    }

    public Project getProject() {
        return project;
    }

    public List<Cycle> getCycles() {
        return cycles;
    }
}
