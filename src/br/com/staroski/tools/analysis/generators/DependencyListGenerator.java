package br.com.staroski.tools.analysis.generators;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import br.com.staroski.tools.analysis.Dependency;
import br.com.staroski.tools.analysis.Project;
import br.com.staroski.tools.analysis.Projects;
import br.com.staroski.utils.Arguments;

/**
 * Traverses a directory tree and generate a list of dependencies for each {@link Project} found.
 *
 * @author Staroski, Ricardo Artur
 */
public final class DependencyListGenerator {

    private static final String PARAM_REPOSITORY = "-r";
    private static final String PARAM_OUTPUT = "-o";

    public static void main(String[] args) {
        try {
            Arguments arguments = new Arguments(args, PARAM_REPOSITORY, PARAM_OUTPUT);

            File repository = new File(arguments.getArgument(PARAM_REPOSITORY));
            File output = new File(arguments.getArgument(PARAM_OUTPUT));

            DependencyListGenerator program = new DependencyListGenerator();
            String programName = program.getClass().getSimpleName();
            System.out.printf("Starting %s...%n", programName);

            Duration elapsed = program.execute(repository, output);

            System.out.printf("Finished %s in %02d:%02d:%02d%n", programName, elapsed.toHours(),
                    elapsed.toMinutesPart(), elapsed.toSecondsPart());
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static String packageName(String entry) {
        entry = entry.replace('/', '.');
        return entry.substring(0, entry.length() - 1);
    }

    private static Set<Project> scanProjects(File folder) throws IOException {
        Set<Project> projects = new TreeSet<>();

        File[] files = folder.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    projects.addAll(scanProjects(file));
                } else if (file.isFile() && file.getName().equals(".classpath")) {
                    Project project = Projects.get(folder);
                    projects.add(project);
                }
            }
        }
        return projects;
    }

    private DependencyListGenerator() {}

    public Duration execute(File repository, File output) throws Exception {
        final Instant start = Instant.now();

        DependencyListGenerator scanner = new DependencyListGenerator();
        List<String> allDependencies = scanner.scan(repository);

        System.out.printf("Generating file \"%s\"...%n", output.getCanonicalPath());
        PrintWriter outputFile = new PrintWriter(output);

        for (String dependency : allDependencies) {
            System.out.println(dependency);
            outputFile.println(dependency);
        }

        outputFile.flush();
        outputFile.close();
        System.out.printf("File \"%s\" successfully generated!%n", output.getCanonicalPath());

        Instant end = Instant.now();
        return Duration.between(start, end);
    }

    private List<String> scan(File repository) throws Exception {
        final List<String> dependencies = new ArrayList<>();

        System.out.print("Scanning projects in \"" + repository.getAbsolutePath() + "\"...");
        Set<Project> projects = scanProjects(repository);
        System.out.println("    Done!");

        System.out.print("Generating dependency list...");
        for (Project project : projects) {
            dependencies.add(project.getName());

            // list dependencies found
            for (Dependency dependency : project.getAllDependencies()) {
                StringBuilder current = new StringBuilder();
                String kind = dependency.getKind();
                // list entries per dependency
                if ("lib".equals(kind)) {
                    current.append("    jar        " + dependency.getName());
                    Set<String> jarEntries = dependency.getLibEntries();
                    if (jarEntries.isEmpty()) {
                        current.append("    []");
                    } else {
                        Predicate<? super String> notMetaInf = entry -> !entry.startsWith("META-INF/");
                        Predicate<? super String> moreThan2levels = entry -> entry.chars().filter(ch -> ch == '/')
                                .count() > 1;
                        List<String> entries = jarEntries.stream().filter(notMetaInf).filter(moreThan2levels)
                                .collect(Collectors.toList());
                        entries.sort((a, b) -> a.compareTo(b));
                        for (String entry : entries) {
                            current.append("    [" + packageName(entry) + "]");
                            break;
                        }
                    }
                } else {
                    current.append("    name    " + dependency.getName());
                }
                dependencies.add(current.toString());
            }
            dependencies.add("");
        }
        System.out.println("    Done!");
        return dependencies;
    }
}
