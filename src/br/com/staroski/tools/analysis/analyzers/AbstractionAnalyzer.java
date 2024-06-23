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
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.MetricsVisitor;
import br.com.staroski.tools.analysis.MetricsVisitors;

/**
 * This class iterates over a {@link Set} of {@link ProjectImpl} and computes its number of <b>abstract types</b> ("Na") and <b>concrete types</b> ("Nc").
 *
 * @author Staroski, Ricardo Artur
 */
public final class AbstractionAnalyzer {

    public void analyze(Set<Project> projects) {
        for (Project project : projects) {
            System.out.println("Abstraction analysis of name \"" + project.getName() + "\" started...");

            scanSourceFiles(project);

            System.out.println("Abstraction analysis of name \"" + project.getName() + "\" done!");
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
    private void updateStats(Project project, Path file) {
        try {
            System.out.print("    Parsing \"" + file.toFile().getName() + "\"...");
            CompilationUnit compilationUnit = StaticJavaParser.parse(file, StandardCharsets.ISO_8859_1);
            System.out.println("    Done!");

            // Update name stats
            final Metrics stats = project.getMetrics();
            compilationUnit.findAll(ClassOrInterfaceDeclaration.class).forEach(classOrInterface -> {
                boolean isAbstract = classOrInterface.isInterface() || classOrInterface.isAbstract();
                MetricsVisitor visitor = isAbstract //
                        ? MetricsVisitors.incrementAbstractTypes()//
                        : MetricsVisitors.incrementConcreteTypes();
                stats.accept(visitor);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
