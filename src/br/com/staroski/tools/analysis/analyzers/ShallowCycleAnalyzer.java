package br.com.staroski.tools.analysis.analyzers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import br.com.staroski.tools.analysis.Project;

/**
 * This class search for cycles on the dependencies of a {@link Project}.<br/>
 * It performs a shaloow check, that is it doesn't analyze inner cycles, only cycles that point back to the origin vertez of the graph
 *
 * @author Staroski, Ricardo Artur
 */
public final class ShallowCycleAnalyzer {

    // Class to represent a cycle
    public static final class Cycle {

        private final List<Project> projects;

        Cycle() {
            this.projects = new ArrayList<>();
        }

        void addProject(Project project) {
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

    public List<Cycle> analyze(Project project) {
        Set<Project> visited = new HashSet<>();
        Stack<Project> stack = new Stack<>();
        List<Cycle> cycles = findCycles(project, project, visited, stack, new HashSet<>());
        if (cycles.isEmpty()) {
            System.out.println("No cycles");
        } else {
            System.out.println("Cycles found {");
            for (Cycle cycle : cycles) {
                System.out.println("    " + cycle);
            }
            System.out.println("}");
        }
        return cycles;
    }

    private Cycle createCycle(Stack<Project> stack, Project initial) {
        Cycle cycle = new Cycle();
        boolean cycleStarted = false;
        for (Project p : stack) {
            if (p.equals(initial)) {
                cycleStarted = true;
            }
            if (cycleStarted) {
                cycle.addProject(p);
            }
        }
        return cycle;
    }

    private List<Cycle> findCycles(Project initial, Project current, Set<Project> visited, Stack<Project> stack, Set<Project> recStack) {
        List<Cycle> cycles = new ArrayList<>();
        if (recStack.contains(current)) {
            if (current.equals(initial)) {
                cycles.add(createCycle(stack, initial));
            }
            return cycles;
        }

        if (visited.contains(current)) {
            return cycles;
        }

        visited.add(current);
        recStack.add(current);
        stack.push(current);

        for (Project dependency : current.getProjectDependencies()) {
            cycles.addAll(findCycles(initial, dependency, visited, stack, recStack));
        }

        stack.pop();
        recStack.remove(current);
        return cycles;
    }
}