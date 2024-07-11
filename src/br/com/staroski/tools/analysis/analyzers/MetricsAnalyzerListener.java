package br.com.staroski.tools.analysis.analyzers;

import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.ProjectScanListener;

/**
 * Listener interface for the {@link MetricsAnalyzer}.
 * 
 * @author Staroski, Ricardo Artur
 */
public interface MetricsAnalyzerListener extends ProjectScanListener, DependencyAnalyzerListener, AbstractionAnalyzerListener {

    void onMetricsAnalysisStarted(Instant start);

    void onMetricsCollected(Set<Project> projects);

    void onMetricsAnalysisFinished(Instant end, Duration elapsed);

}
