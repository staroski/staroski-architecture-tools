package br.com.staroski.tools.analysis.generators;

import java.io.File;
import java.time.Duration;
import java.time.Instant;
import java.util.Set;

import br.com.staroski.tools.analysis.Metrics;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;
import br.com.staroski.tools.analysis.analyzers.CouplingAnalyzer;
import br.com.staroski.tools.analysis.analyzers.CycleAnalyzer;

/**
 * Generates content for <a href="https://graphviz.org/doc/info/lang.html">.dot</a> files to be used with
 * <a href="https://graphviz.org/documentation/">Grapviz</a> tool.<br>
 * Example to generate a PDF with Graphviz:
 *
 * <pre>
 * dot -Tpdf -Kcirco input-file.dot -o output-file.pdf
 * </pre>
 *
 * @author Staroski, Ricardo Artur
 */
public final class GraphvizDotFileGenerator implements Generator {

    public static void main(String[] args) {
        try {
            Generator program = new GraphvizDotFileGenerator();
            String programName = program.getClass().getSimpleName();
            System.out.printf("Starting %s...%n", programName);

            File repository = new File("S:\\workspaces\\staroski\\example-system");
            Duration elapsed = program.execute(repository);

            System.out.printf("Finished %s in %02d:%02d:%02d%n", programName, elapsed.toHours(),
                    elapsed.toMinutesPart(), elapsed.toSecondsPart());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private boolean debug = true;

    private final CycleAnalyzer cycleAnalyzer = new CycleAnalyzer();

    private GraphvizDotFileGenerator() {}

    @Override
    public Duration execute(File repository) throws Exception {
        final Instant start = Instant.now();

        final Set<Project> projects = Projects.scan(repository);

        final CouplingAnalyzer couplingAnalyzer = new CouplingAnalyzer();
        couplingAnalyzer.analyze(projects);

        final String fullGraph = generateFullGraph(projects);
        final String cycleGraph = generateCycleGraph(projects);

        System.out.println(fullGraph);
        System.out.println(cycleGraph);

        final Instant end = Instant.now();
        return Duration.between(start, end);
    }

    private void debugln(Object message) {
        if (debug) {
            System.out.println(message);
        }
    }

    private String generateCycleGraph(final Set<Project> projects) {
        System.out.println("Generating cycle graph...");

        String graphTitle = "Circular Dependencies";
        String nodeShape = "rectangle";
        String nodeColor = "yellow";

        StringBuilder sb = new StringBuilder("digraph NGG {\n\n");

        sb.append("    label=\"").append(graphTitle).append("\";\n");
        sb.append("    labelloc=\"t\";\n");
        sb.append("    node [shape=").append(nodeShape).append("];\n");
        sb.append("    rankdir=LR;\n\n");

        // setup colors
        for (Project p : projects) {

            if (hasNoCycles("name", p, projects)) {
                continue;
            }

            sb.append("    \"").append(p.getName()).append("\" [fillcolor=\"").append(nodeColor)
                    .append("\", style=\"filled\"];\n");
        }
        sb.append("\n");

        // create graph
        for (Project p : projects) {

            if (hasNoCycles("name", p, projects)) {
                continue;
            }

            debugln("    Cycle: " + cycleAnalyzer.getCycles(p));

            sb.append("    ").append("\"" + p.getName() + "\"").append(" -> ").append("{");

            for (Project d : p.getProjectDependencies()) {

                if (hasNoCycles("dependency", d, projects)) {
                    continue;
                }

                sb.append(" ").append(" \"" + d.getName() + "\"");
            }
            sb.append(" };\n");
        }
        sb.append("}");
        System.out.println("Generating cycle graph done!");
        return sb.toString();
    }

    private String generateFullGraph(final Set<Project> projects) {
        System.out.println("Generating full graph...");

        String graphTitle = "All Dependencies";
        String nodeShape = "rectangle";

        StringBuilder sb = new StringBuilder("digraph NGG {\n\n");

        sb.append("    label=\"").append(graphTitle).append("\";\n");
        sb.append("    labelloc=\"t\";\n");
        sb.append("    node [shape=").append(nodeShape).append("];\n");
        sb.append("    rankdir=LR;\n\n");

        // setup colors
        for (Project p : projects) {

            String nodeColor = hasNoCycles("name", p, projects) ? "cyan" : "yellow";

            sb.append("    \"").append(p.getName()).append("\" [fillcolor=\"").append(nodeColor)
                    .append("\", style=\"filled\"];\n");
        }
        sb.append("\n");

        // create graph
        for (Project p : projects) {

            sb.append("    ").append("\"" + p.getName() + "\"").append(" -> ").append("{");

            for (Project d : p.getProjectDependencies()) {

                sb.append(" ").append(" \"" + d.getName() + "\"");
            }
            sb.append(" };\n");
        }
        sb.append("}");
        System.out.println("Generating full graph done!");
        return sb.toString();
    }

    private boolean hasNoCycles(String what, Project project, Set<Project> projects) {
        final String name = project.getName();

        Metrics stats = project.getMetrics();

        final int ac = stats.getInputDependencies();
        final int ec = stats.getOutputDependencies();

        if (ac < 1) {
            debugln("    ignoring " + what + " with afferent coupling " + ac + " \"" + name + "\"");
            return true;
        }
        if (ec < 2) {
            debugln("    ignoring " + what + " with efferent coupling " + ec + " \"" + name + "\"");
            return true;
        }

        if (cycleAnalyzer.isAcyclic(project)) {
            debugln("    ignoring acyclic " + what + " \"" + name + "\"");
            return true;
        }
        return false;
    }
}
