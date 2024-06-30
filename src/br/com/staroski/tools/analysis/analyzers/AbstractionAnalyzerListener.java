package br.com.staroski.tools.analysis.analyzers;

/**
 * Listener interface for the {@link AbstractionAnalyzer}.
 * 
 * @author Staroski, Ricardo Artur
 */
public interface AbstractionAnalyzerListener {

    void onAbstractionAnalysisStarted(AbstractionAnalysisEvent event);

    void onAbstractionAnalysisFinished(AbstractionAnalysisEvent event);

    void onFileParsingStarted(AbstractionAnalysisEvent event);

    void onFileParsingFinished(AbstractionAnalysisEvent event);

}
