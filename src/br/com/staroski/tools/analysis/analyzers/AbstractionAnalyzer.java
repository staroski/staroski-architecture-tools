package br.com.staroski.tools.analysis.analyzers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Set;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.MetricsVisitor;
import br.com.staroski.tools.analysis.MetricsVisitors;
import br.com.staroski.tools.analysis.Project;

/**
 * This class iterates over a {@link Set} of {@link ProjectImpl} and computes its number of <b>abstract types</b> ("Na") and <b>concrete types</b> ("Nc").
 *
 * @author Staroski, Ricardo Artur
 */
public final class AbstractionAnalyzer {

    private final class InternalListener implements AbstractionAnalyzerListener {

        @Override
        public void onAbstractionAnalysisStarted(AbstractionAnalysisEvent event) {
            System.out.println("Abstraction analysis of name \"" + event.getProject().getName() + "\" started...");
        }

        public void onAbstractionAnalysisFinished(AbstractionAnalysisEvent event) {
            System.out.println("Abstraction analysis of name \"" + event.getProject().getName() + "\" done!");
        }

        @Override
        public void onFileParsingStarted(AbstractionAnalysisEvent event) {
            System.out.println("    Parsing \"" + event.getFile().getName() + "\"...");
        }

        @Override
        public void onFileParsingFinished(AbstractionAnalysisEvent event) {
            System.out.println("    Parsing \"" + event.getFile().getName() + "\" finished!");
        }
    }

    private AbstractionAnalyzerListener listener = new InternalListener();

    public void addAbstractionAnalyzerListener(AbstractionAnalyzerListener listener) {
        this.listener = Listeners.addAbstractionAnalyzerListener(this.listener, listener);
    }

    public void removeAbstractionAnalyzerListener(AbstractionAnalyzerListener listener) {
        this.listener = Listeners.removeAbstractionAnalyzerListener(this.listener, listener);
    }

    public void analyze(Set<Project> projects) {
        for (Project project : projects) {
            listener.onAbstractionAnalysisStarted(new AbstractionAnalysisEvent(project));

            scanSourceFiles(project);

            listener.onAbstractionAnalysisFinished(new AbstractionAnalysisEvent(project));
        }
    }

    private void scanDirectory(Project project, File directory) throws IOException {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    scanDirectory(project, file); // Recurse into subdirectory
                } else if (file.isFile() && file.getName().endsWith(".java")
                        && !file.getName().equals("module-info.java")) {
                    // Process .java files
                    updateStats(project, file.toPath());
                }
            }
        }
    }

    private void scanSourceFiles(Project project) {
        File directory = project.getDirectory();
        try {
            // Start the recursive scan
            scanDirectory(project, directory);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("deprecation")
    private void updateStats(Project project, Path sourcePath) {
        try {
            File file = sourcePath.toFile();

            listener.onFileParsingStarted(new AbstractionAnalysisEvent(project, file));

            CompilationUnit compilationUnit = StaticJavaParser.parse(sourcePath, StandardCharsets.ISO_8859_1);

            listener.onFileParsingFinished(new AbstractionAnalysisEvent(project, file));

            // Update name stats
            final Metrics metrics = project.getMetrics();
            compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classOrInterface -> {
                boolean isAbstract = classOrInterface.isInterface() || classOrInterface.isAbstract();
                MetricsVisitor visitor = isAbstract //
                        ? MetricsVisitors.incrementAbstractTypes()//
                        : MetricsVisitors.incrementConcreteTypes();
                metrics.accept(visitor);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
