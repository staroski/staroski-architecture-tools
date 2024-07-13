package br.com.staroski.tools.analysis.analyzers;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import br.com.staroski.tools.analysis.Project;

/**
 * This is an empty implementation of the {@link MetricsAnalyzerListener}.
 * 
 * @author Staroski, Ricardo Artur
 */
public class DefaultMetricsAnalyzerListener implements MetricsAnalyzerListener {

    @Override
    public void onProjectScanStarted(File directory) {}

    @Override
    public void onDirectoryEnter(File directory) {}

    @Override
    public void onProjectFound(Project project) {}

    @Override
    public void onDirectoryExit(File directory) {}

    @Override
    public void onProjectScanFinished(File directory) {}

    @Override
    public void onCouplingAnalysisStarted(DependencyAnalysisEvent event) {}

    @Override
    public void onCouplingAnalysisFinished(DependencyAnalysisEvent event) {}

    @Override
    public void onCycleAnalysisStarted(DependencyAnalysisEvent event) {}

    @Override
    public void onCycleAnalysisFinished(DependencyAnalysisEvent event) {}

    @Override
    public void onAbstractionAnalysisStarted(AbstractionAnalysisEvent event) {}

    @Override
    public void onAbstractionAnalysisFinished(AbstractionAnalysisEvent event) {}

    @Override
    public void onFileParsingStarted(AbstractionAnalysisEvent event) {}

    @Override
    public void onFileParsingFinished(AbstractionAnalysisEvent event) {}

    @Override
    public void onMetricsAnalysisStarted(Instant start) {}

    @Override
    public void onMetricsCollected(Set<Project> projects) {}

    @Override
    public void onMetricsAnalysisFinished(Instant end, Duration elapsed) {}

}
