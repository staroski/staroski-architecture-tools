package br.com.staroski.tools.analysis;

import java.io.File;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

/**
 * This is a common superclass for {@link Project} implementations.<br>
 * 
 * @author Staroski, Ricardo Artur
 */
abstract class AbstractProject implements Project {

    /**
     * Compares projects by its <tt>name</tt>.
     */
    public static final Comparator<Project> NAME_COMPARATOR = new Comparator<>() {

        @Override
        public int compare(Project a, Project b) {
            return a.getName().compareToIgnoreCase(b.getName());
        };
    };

    private final File folder;
    private final String name;
    private final Set<Dependency> srcDependencies = new TreeSet<>(Dependency.KIND_COMPARATOR);
    private final Set<Dependency> libDependencies = new TreeSet<>(Dependency.KIND_COMPARATOR);
    private final Set<Dependency> allDependencies = new TreeSet<>(Dependency.KIND_COMPARATOR);

    private Set<Project> projectDependencies;

    private final Metrics metrics;

    /**
     * @param projectFolder The project's folder
     */
    AbstractProject(File projectFolder) {
        folder = Objects.requireNonNull(projectFolder, "projectFolder cannot be null");
        name = folder.getName();
        metrics = new Metrics(this);
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
            projectDependencies = new TreeSet<>(AbstractProject.NAME_COMPARATOR);
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
}
