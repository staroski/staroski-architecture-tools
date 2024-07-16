package br.com.staroski.tools.analysis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class to obtain {@link Project} instances.
 *
 * @author Staroski, Ricardo Artur
 */
public final class Projects {

    private static final ProjectScanListener EMPTY_LISTENER = new ProjectScanListener() {

        @Override
        public void onDirectoryEnter(File directory) {}

        @Override
        public void onDirectoryExit(File directory) {}

        @Override
        public void onProjectFound(Project project) {}

        @Override
        public void onProjectScanFinished(File directory) {}

        @Override
        public void onProjectScanStarted(File directory) {}
    };

    // cache to store projects by its directory
    private static final Map<String, Project> directoryCache = new HashMap<>();

    /**
     * Gets a {@link Project} instance for the specified directory.
     *
     * @param directory The projects's directory.
     * @return A {@link Project} instance.
     * @throws IOException              if some IO exception occurs.
     * @throws IllegalArgumentException If the specified directory is not a project folder.
     */
    public static Project get(File directory) throws IOException {
        if (!Projects.isProjectFolder(directory)) {
            throw new IllegalArgumentException("\"" + directory + "\" is not a valid project folder");
        }
        final String path = directory.getCanonicalPath();
        // you are not crazy the following two 'if' are equal
        // one 'if' before the synchronized block
        // another 'if' after the synchronized block
        // this is called double-checked locking
        Project project = directoryCache.get(path);
        if (project == null) {
            synchronized (Projects.class) {
                project = directoryCache.get(path);
                if (project == null) {
                    project = createProjectInstance(directory);
                    directoryCache.put(path, project);
                }
            }
        }
        return project;
    }

    /**
     * Given a set of {@link Project}s, returns a CSV representation of its metrics.
     * 
     * @param projects The set of projects whose metrics should be read.
     * @return A CSV text containing the metrics of the projects.
     */
    public static String getMetricsCsv(Set<Project> projects) {
        StringBuilder csv = new StringBuilder("Name,D,I,A,Na,Nc,Ce,Ca,DAG");
        final Locale en_US = new Locale("en", "US"); // default locale for double parsing
        for (Project project : projects) {
            Metrics m = project.getMetrics();
            String name = project.getName();
            String d = String.format(en_US, "%.2f", m.getDistance());
            String i = String.format(en_US, "%.2f", m.getInstability());
            String a = String.format(en_US, "%.2f", m.getAbstractness());
            int na = m.getAbstractTypes();
            int nc = m.getConcreteTypes();
            int ce = m.getOutputDependencies();
            int ca = m.getInputDependencies();
            int dag = m.isAcyclic() ? 1 : 0;
            csv.append(String.format(en_US, "%n%s,%s,%s,%s,%d,%d,%d,%d,%d", name, d, i, a, na, nc, ce, ca, dag));
        }
        return csv.toString();
    }

    /**
     * Recursively scans the specified directory tree for projects.
     *
     * @param directory THe directory tree.
     * @return A {@link Set} of {@link Project}
     * @throws IOException If some IO error occurs.
     */
    public static Set<Project> scan(File directory) throws IOException {
        return scan(directory, EMPTY_LISTENER);
    }

    /**
     * Recursively scans the specified directory tree for projects.
     *
     * @param directory The directory tree.
     * @param listener  The scan listener.
     * @return A {@link Set} of {@link Project}
     * @throws IOException If some IO error occurs.
     */
    public static Set<Project> scan(File directory, ProjectScanListener listener) throws IOException {
        directory = Objects.requireNonNull(directory, "Directory cannot be null!");
        final String canonicalPath = directory.getCanonicalPath();
        if (!directory.isDirectory()) {
            throw new IllegalArgumentException("\"" + canonicalPath + "\" is not a directory!");
        }
        if (listener == null) {
            listener = EMPTY_LISTENER;
        }

        directoryCache.clear();
        
        System.out.print("Scanning projects in \"" + canonicalPath + "\"...");
        listener.onProjectScanStarted(directory);

        Set<Project> projects = scanRecursively(directory, listener);

        System.out.println("    Done!");
        listener.onProjectScanFinished(directory);
        return projects;
    }

    private static Project createProjectInstance(File directory) throws IOException {
        if (isEclipseProject(directory)) {
            return new EclipseProject(directory);
        }
        if (isNetBeansProject(directory)) {
            return new NetBeansProject(directory);
        }
        if (isMavenProject(directory)) {
            return new MavenProject(directory);
        }
        throw new UnsupportedOperationException("Could not determine project kind for directory \"" + directory.getCanonicalPath() + "\"");
    }

    private static boolean isEclipseProject(File directory) {
        File projectFile = new File(directory, EclipseProject.PROJECT_FILE);
        File classpathFile = new File(directory, EclipseProject.CLASSPATH_FILE);
        return projectFile.exists() && projectFile.isFile()
                && classpathFile.exists() && classpathFile.isFile();
    }

    private static boolean isMavenProject(File directory) {
        File pomFile = new File(directory, MavenProject.POM_FILE);
        return pomFile.exists() && pomFile.isFile();
    }

    private static boolean isNetBeansProject(File directory) {
        File nbProjectDir = new File(directory, NetBeansProject.NBPROJECT_FOLDER);
        return nbProjectDir.exists() && nbProjectDir.isDirectory();
    }

    private static boolean isProjectFolder(File directory) {
        if (!directory.isDirectory()) {
            return false;
        }
        return isEclipseProject(directory)
                || isNetBeansProject(directory)
                || isMavenProject(directory);
    }

    private static Set<Project> scanRecursively(File directory, ProjectScanListener listener) throws IOException {
        assert listener != null;
        listener.onDirectoryEnter(directory);
        Set<Project> projects = new TreeSet<>();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    if (isProjectFolder(file)) {
                        Project project = Projects.get(file);
                        projects.add(project);
                        listener.onProjectFound(project);
                    } else {
                        projects.addAll(scanRecursively(file, listener));
                    }
                }
            }
        }
        listener.onDirectoryExit(directory);
        return projects;
    }

    // non instantiable
    private Projects() {}
}
