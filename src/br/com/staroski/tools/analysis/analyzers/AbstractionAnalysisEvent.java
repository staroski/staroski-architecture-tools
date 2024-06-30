package br.com.staroski.tools.analysis.analyzers;

import java.io.File;

import br.com.staroski.tools.analysis.Project;

/**
 * Event that the {@link AbstractionAnalyzer} sends to the {@link AbstractionAnalyzerListener}.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class AbstractionAnalysisEvent {

    private Project project;
    private File file;

    AbstractionAnalysisEvent(Project project) {
        this.project = project;
    }

    AbstractionAnalysisEvent(Project project, File file) {
        this.project = project;
        this.file = file;
    }

    public Project getProject() {
        return project;
    }

    public File getFile() {
        return file;
    }
}
