package br.com.staroski.tools.analysis.analyzers;

/**
 * This is an empty implementation of the {@link AbstractionAnalyzerListener}
 * 
 * @author Staroski, Ricardo Artur
 */
public class DefaultAbstractionAnalyzerListener implements AbstractionAnalyzerListener {

    @Override
    public void onAbstractionAnalysisStarted(AbstractionAnalysisEvent event) {}

    @Override
    public void onAbstractionAnalysisFinished(AbstractionAnalysisEvent event) {}

    @Override
    public void onFileParsingStarted(AbstractionAnalysisEvent event) {}

    @Override
    public void onFileParsingFinished(AbstractionAnalysisEvent event) {}

}
