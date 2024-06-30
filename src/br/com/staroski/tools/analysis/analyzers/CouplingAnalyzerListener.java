package br.com.staroski.tools.analysis.analyzers;

/**
 * Listener interface for the {@link CouplingAnalyzer}.
 * 
 * @author Staroski, Ricardo Artur
 */
public interface CouplingAnalyzerListener {

    void onCouplingAnalysisStarted(CouplingAnalysisEvent event);

    void onCouplingAnalysisFinished(CouplingAnalysisEvent event);
}
