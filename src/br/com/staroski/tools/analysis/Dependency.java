package br.com.staroski.tools.analysis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class represents a dependency of a {@link Project}, it can by of t types: {@link #KIND_SRC} or {@link #KIND_LIB}.
 *
 * @author Staroski, Ricardo Artur
 */
public final class Dependency implements Comparable<Dependency> {

    /**
     * A dependency of type <tt>"src"</tt>.
     */
    public static final String KIND_SRC = "src";

    /**
     * A dependency of type <tt>"lib"</tt>.
     */
    public static final String KIND_LIB = "lib";

    /**
     * Compares dependencies by its <tt>kind</tt>.<br>
     * If the kind is the same, compares by its <tt>name</tt>.
     */
    public static final Comparator<Dependency> KIND_COMPARATOR = new Comparator<>() {
        @Override
        public int compare(Dependency a, Dependency b) {
            String kindA = a.getKind();
            String kindB = b.getKind();
            int kindDiff = kindA.compareTo(kindB);
            if (kindDiff == 0) {
                return a.getName().compareToIgnoreCase(b.getName());
            }
            return KIND_SRC.equals(kindA) ? -1 : 1;
        };
    };

    private final String kind;
    private final File artifact;
    private final String name;

    private final Set<Dependency> dependencies = new TreeSet<>();

    public Dependency(String kind, File artifact) {
        this.kind = kind;
        this.artifact = artifact;
        String path = artifact.getAbsolutePath();
        this.name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
    }

    public void addDependency(Dependency name) {
        dependencies.add(name);
    }

    public Project asProject() {
        final String kind = getKind();
        if (KIND_SRC.equals(kind)) {
            final File artifact = getArtifact();
            try {
                return Projects.get(artifact);
            } catch (IllegalArgumentException | IOException e) {
                throw new IllegalStateException("Cannot get a " + Project.class.getSimpleName()
                        + " instance from artifact \"" + artifact + "\".", e);
            }
        }
        throw new IllegalStateException("Cannot get a " + Project.class.getSimpleName() + " instance from an "
                + Dependency.class + " of kind \"" + kind + "\".");
    }

    @Override
    public int compareTo(Dependency other) {
        if (other == null) {
            return 1;
        }
        int result = compareNullableStrings(this.kind, other.kind);
        if (result == 0) {
            result = compareNullableStrings(this.name, other.name);
        }
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof Dependency) {
            Dependency that = (Dependency) other;
            return Objects.equals(this.kind, that.kind) //
                    && Objects.equals(this.name, that.name);
        }
        return false;
    }

    public File getArtifact() {
        return artifact;
    }

    public Set<Dependency> getDependencies() {
        return dependencies;
    }

    public String getKind() {
        return kind;
    }

    public Set<String> getLibEntries() {
        if (!KIND_LIB.equals(getKind())) {
            return Collections.emptySet();
        }
        try {
            Set<String> entries = new TreeSet<>();
            ZipInputStream zip = new ZipInputStream(new FileInputStream(getArtifact()));
            ZipEntry entry = null;
            while ((entry = zip.getNextEntry()) != null) {
                if (entry.isDirectory()) {
                    entries.add(entry.getName());
                }
            }
            zip.close();
            return entries;
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptySet();
        }

    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(kind, name);
    }

    @Override
    public String toString() {
        return String.format("Dependency[kind=\"%s\"; name=\\\"%s\\\"]", getKind(), getName());

    }

    private int compareNullableStrings(String s1, String s2) {
        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 == null) {
            return -1; // s1 is greater than s2
        }
        if (s2 == null) {
            return 1; // s1 is lower than s2
        }
        return s1.compareTo(s2);
    }
}
