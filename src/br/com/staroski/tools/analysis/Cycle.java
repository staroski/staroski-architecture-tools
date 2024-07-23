package br.com.staroski.tools.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class represents a cycle o {@link Project} dependencies.
 * 
 * @author Staroski, Ricardo Artur
 */
public final class Cycle {

    private final List<Project> projects;

    public Cycle() {
        this.projects = new ArrayList<>();
    }

    public void addProject(Project project) {
        projects.add(project);
    }

    public List<Project> getProjects() {
        return Collections.unmodifiableList(projects);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Project v : projects) {
            sb.append("\"");
            sb.append(v.getName());
            sb.append("\"");
            sb.append(" -> ");
        }
        if (projects.size() > 1) {
            sb.append("\"");
            sb.append(projects.get(0).getName()); // close the cycle
            sb.append("\"");
        }
        return sb.toString();
    }
}