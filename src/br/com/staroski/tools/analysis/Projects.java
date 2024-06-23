package br.com.staroski.tools.analysis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class to obtain {@link Project} instances.
 *
 * @author Staroski, Ricardo Artur
 */
public final class Projects {

    // cache to store projects by its directory
    private static final Map<String, Project> directoryCache = new HashMap<>();

    /**
     * Gets a {@link Project} instance for the specified directory.
     *
     * @param directory The name's directory.
     * @return AN {@link Project}instance.
     * @throws IOException              if some IO exception occurs.
     * @throws IllegalArgumentException If the specified directory is not a name folder.
     */
    public static Project get(File directory) throws IOException {
        if (!Projects.isProjectFolder(directory)) {
            throw new IllegalArgumentException("\"" + directory + "\" is not a valid eclipse name folder");
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
                    project = new ProjectImpl(directory);
                    directoryCache.put(path, project);
                }
            }
        }
        return project;
    }

    /**
     * Recursively scans the specified directory tree for projects.
     *
     * @param directory THe directory tree.
     * @return A {@link Set} of {@link ProjectImpl}
     * @throws IOException If some IO error occurs.
     */
    public static Set<Project> scan(File directory) throws IOException {
        System.out.print("Scanning projects in \"" + directory.getAbsolutePath() + "\"...");
        Set<Project> projects = scanRecursively(directory);
        System.out.println("    Done!");
        return projects;
    }

    /**
     * Returns <tt>true</tt> if the specified directory contains a <tt>.project</tt> file and a <tt>.classpath</tt> file.
     */
    private static boolean isProjectFolder(File directory) {
        if (!directory.isDirectory()) {
            return false;
        }
        File[] files = directory.listFiles();
        boolean containsProjectFile = false;
        boolean containsClasspathFile = false;
        for (File file : files) {
            String name = file.getName();
            if (name.equals(ProjectImpl.PROJECT_FILE)) {
                containsProjectFile = true;
            }
            if (name.equals(ProjectImpl.CLASSPATH_FILE)) {
                containsClasspathFile = true;
            }
        }
        return containsProjectFile && containsClasspathFile;
    }

    private static Set<Project> scanRecursively(File directory) throws IOException {
        Set<Project> projects = new TreeSet<>();

        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    projects.addAll(scanRecursively(file));
                } else if (file.getName().equals(ProjectImpl.CLASSPATH_FILE)) {
                    Project project = Projects.get(directory);
                    projects.add(project);
                }
            }
        }
        return projects;
    }

    // non instantiable
    private Projects() {}
}
