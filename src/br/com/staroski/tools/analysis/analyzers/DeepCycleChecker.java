package br.com.staroski.tools.analysis.analyzers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.staroski.tools.analysis.Cycle;
import br.com.staroski.tools.analysis.Project;

/**
 * This class implements a DFS (Depth First Search) algorithm to check for circular dependencies on a {@link Project}.<br/>
 * Its's a deep scan so it will also find inner cycles and not just the ones that point back to the starting point of the graph..
 *
 * @author Staroski, Ricardo Artur
 */
public final class DeepCycleChecker {

    // Class to keep track of colors
    private static final class ColorTracker {
        Map<Project, Integer> colors;

        ColorTracker() {
            this.colors = new HashMap<>();
            
        }

        int getColor(Project v) {
            return colors.getOrDefault(v, WHITE);
        }

        void setColor(Project v, int color) {
            colors.put(v, color);
        }
    }

    // Defining the colors
    private static final int WHITE = 0;
    private static final int GRAY = 1;
    private static final int BLACK = 2;

    // Method to get the found cycles
    public List<Cycle> getCycles(List<Project> graph) {
        // Mapping the projects to the visited state
        Map<Project, Boolean> visited = new HashMap<>();

        // Creating a ColorTracker object
        ColorTracker tracker = new ColorTracker();

        // List to store the found cycles
        List<Cycle> cycles = new ArrayList<>();

        // Iterating over all projects in the graph
        for (Project u : graph) {
            // If the vertex has not been visited, call dfs
            if (!visited.containsKey(u)) {
                List<Project> cycle = new ArrayList<>();
                dfs(u, visited, tracker, cycle, cycles);
            }
        }

        return cycles;
    }

    public List<Cycle> getCycles(Project project) {
        return getCycles(Arrays.asList(project));
    }

    // Function to check if the graph is a DAG (Directed Acyclic Graph)
    public boolean isAcyclic(List<Project> graph) {
        // Mapping the projects to the visited state
        Map<Project, Boolean> visited = new HashMap<>();

        // Creating a ColorTracker object
        ColorTracker tracker = new ColorTracker();

        // List to store the found cycles
        List<Cycle> cycles = new ArrayList<>();

        // Iterating over all projects in the graph
        for (Project u : graph) {
            // If the vertex has not been visited, call dfs
            if (!visited.containsKey(u)) {
                List<Project> cycle = new ArrayList<>();
                if (dfs(u, visited, tracker, cycle, cycles))
                    return false; // Returns false when a cycle is found
            }
        }
        return true; // Returns true only if no cycle is found
    }

    public boolean isAcyclic(Project project) {
        return isAcyclic(Arrays.asList(project));
    }

    // Recursive DFS function
    private boolean dfs(Project u, Map<Project, Boolean> visited, ColorTracker tracker, List<Project> cycle,
            List<Cycle> cycles) {
        // Marking the current vertex as visited
        visited.put(u, true);

        // Updating the vertex color to GRAY to indicate it is being visited
        tracker.setColor(u, GRAY);

        // Adding the vertex to the current path
        cycle.add(u);

        // Iterating over the neighbors of the current vertex
        for (Project v : u.getProjectDependencies()) {
            // If the neighbor has not been visited, recursively call dfs
            if (!visited.containsKey(v)) {
                if (dfs(v, visited, tracker, cycle, cycles))
                    return true; // Returns true when a cycle is found
            }
            // If we find a gray vertex, there is a cycle in the graph
            else if (tracker.getColor(v) == GRAY) {
                // Adding the found cycle to the list of cycles
                int start = cycle.indexOf(v);
                if (start >= 0) { // Checks if the index is valid
                    Cycle foundCycle = new Cycle();
                    for (int i = start; i < cycle.size(); i++) {
                        foundCycle.addProject(cycle.get(i));
                    }
                    cycles.add(foundCycle);
                }
                return true; // Returns true when a cycle is found
            }
        }
        // Updating the vertex color to BLACK to indicate it has been completely visited
        tracker.setColor(u, BLACK);
        // Removing the vertex from the current path
        cycle.remove(u);
        return false; // Returns false when no cycle is found
    }
}
