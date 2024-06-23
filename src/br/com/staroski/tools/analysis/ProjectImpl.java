package br.com.staroski.tools.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class represents a eclipse name.<br>
 * Instances of this class are created through the static method {@link Projects#get(File)} passing the name directory as parameter.<br>
 * You can also get a list of projects inside a directory tree using the static method {@link Projects#scan(File)}.
 *
 * @author Staroski, Ricardo Artur
 */
final class ProjectImpl implements Project {

    /**
     * Represents a cycle of {@link Project} dependencies.
     */
    public static final class Cycle {

        private List<Project> projects;

        private Cycle() {
            this.projects = new ArrayList<>();
        }

        public void addProject(Project v) {
            projects.add(v);
        }

        public List<Project> getProjects() {
            return projects;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (Project v : projects) {
                sb.append(v.getName()).append(" -> ");
            }
            sb.append(projects.get(0).getName()); // Adds the first vertex again to close the cycle
            return sb.toString();
        }
    }

    /**
     * Compares projects by its <tt>name</tt>.
     */
    public static final Comparator<Project> NAME_COMPARATOR = new Comparator<>() {

        @Override
        public int compare(Project a, Project b) {
            return a.getName().compareToIgnoreCase(b.getName());
        };
    };

    public static final String PROJECT_FILE = ".project";
    public static final String CLASSPATH_FILE = ".classpath";

    private final File folder;
    private final String name;
    private final Set<Dependency> srcDependencies = new TreeSet<>(Dependency.KIND_COMPARATOR);
    private final Set<Dependency> libDependencies = new TreeSet<>(Dependency.KIND_COMPARATOR);
    private final Set<Dependency> allDependencies = new TreeSet<>(Dependency.KIND_COMPARATOR);

    private Set<Project> projectDependencies;

    private final Metrics metrics;

    // Private constructor, use the 'get' factory method
    ProjectImpl(File projectFolder) {
        folder = Objects.requireNonNull(projectFolder, "projectFolder cannot be null");
        name = folder.getName();
        File classpathFile = projectFolder.listFiles((folder, file) -> file.equals(CLASSPATH_FILE))[0];
        readClasspathFile(projectFolder, classpathFile);
        metrics = new MetricsImpl(this);
    }

    @Override
    public int compareTo(Project other) {
        return getName().toLowerCase().compareTo(other.getName().toLowerCase());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Project) {
            Project that = (Project) other;
            return Objects.equals(this.getName(), that.getName());
        }
        return false;
    }

    @Override
    public Set<Dependency> getAllDependencies() {
        return allDependencies;
    }

    @Override
    public File getDirectory() {
        return folder;
    }

    @Override
    public Set<Dependency> getLibDependencies() {
        return libDependencies;
    }

    @Override
    public Metrics getMetrics() {
        return metrics;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set<Project> getProjectDependencies() {
        if (projectDependencies == null) {
            projectDependencies = new TreeSet<>(ProjectImpl.NAME_COMPARATOR);
            for (Dependency src : getSrcDependencies()) {
                try {
                    Project project = src.asProject();
                    projectDependencies.add(project);
                } catch (IllegalStateException e) {
                    // ignore because some 'src' dependencies may not represent projects
                }
            }
        }
        return projectDependencies;
    }

    @Override
    public Set<Dependency> getSrcDependencies() {
        return srcDependencies;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return name;
    }

    private void readClasspathFile(File projectFolder, File classpathFile) {

        try (BufferedReader reader = new BufferedReader(new FileReader(classpathFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("path=\"src\"")) {
                    continue;
                } else if (line.contains("kind=\"lib\"")) {
                    Optional<Dependency> result = readDependency(projectFolder, line, Dependency.KIND_LIB);
                    if (result.isPresent()) {
                        Dependency dependency = result.get();
                        libDependencies.add(dependency);
                        allDependencies.add(dependency);
                    }
                } else if (line.contains("kind=\"src\"")) {
                    Optional<Dependency> result = readDependency(projectFolder, line, Dependency.KIND_SRC);
                    if (result.isPresent()) {
                        Dependency dependency = result.get();
                        srcDependencies.add(dependency);
                        allDependencies.add(dependency);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Optional<Dependency> readDependency(File projectFolder, String line, String kind) throws IOException {
        final File repository = projectFolder.getParentFile();
        Dependency value = null;
        int begin = line.indexOf("path=\"") + 6;
        int end = line.indexOf("\"", begin);
        String path = line.substring(begin, end);
        try {
            if (path.startsWith("/")) {
                path = repository.getCanonicalPath() + path;
            } else {
                path = projectFolder.getCanonicalPath() + "/" + path;
            }
            path = path.replace('\\', File.separatorChar);
            path = path.replace('/', File.separatorChar);
            File artifact = new File(path);
            value = new Dependency(kind, artifact);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(value);
    }
}
