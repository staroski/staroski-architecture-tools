package br.com.staroski.tools.analysis.analyzers;

/**
 * Listener interface for the {@link DependencyAnalyzer}.
 * 
 * @author Staroski, Ricardo Artur
 */
public interface DependencyAnalyzerListener {

    void onCouplingAnalysisStarted(DependencyAnalysisEvent event);

    void onCouplingAnalysisFinished(DependencyAnalysisEvent event);

    void onCycleAnalysisStarted(DependencyAnalysisEvent event);

    void onCycleAnalysisFinished(DependencyAnalysisEvent event);

}
