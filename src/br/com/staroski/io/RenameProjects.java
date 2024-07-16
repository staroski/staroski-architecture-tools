package br.com.staroski.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class RenameProjects {
    private class Project {
        String name;
        String distance;
        String instability;
        String abstraction;
        String outputs;
        String inputs;
        String concretes;
        String abstracts;
        String dag;
    }

    public static void main(String[] args) {
        try {
            new RenameProjects().execute();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void execute() {
        List<String> dotFile = readDot();
        List<Project> projects = readCsv();

        List<String> animals = readAnimals();
        Map<String, String> map = map(projects, animals);

        writeNewCsv(projects, map);
        writeNewDot(dotFile, map);
    }

    private Map<String, String> map(List<Project> projects, List<String> animals) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < projects.size(); i++) {
            String name = projects.get(i).name;
            String animal = animals.get(i);
            map.put(name, animal);
            System.out.println("Mapping \"" + name + "\" to \"" + animal + "\"");
        }
        return map;
    }

    private List<String> readAnimals() {
        List<String> allData = new ArrayList<>();
        InputStream txt = getClass().getResourceAsStream("/data-files/animals-with-adjectives.txt");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(txt))) {
            String line = null;
            while ((line = br.readLine()) != null) { // data
                allData.add(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allData;
    }

    private List<Project> readCsv() {
        List<Project> allData = new ArrayList<>();
        InputStream csv = getClass().getResourceAsStream("/data-files/projects-metrics.csv");
        final String separator = ";";
        // ID; Name; D; I; A; Ce; Ca; Nc; Na; DAG
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csv))) {
            String line = br.readLine(); // title
            while ((line = br.readLine()) != null) { // data
                String[] values = line.replace(',', '.').split(separator);
                Project p = new Project();
                int col = 0;
                col++; // ignore id
                p.name = values[col++].trim();
                p.distance = values[col++].trim();
                p.instability = values[col++].trim();
                p.abstraction = values[col++].trim();
                p.outputs = values[col++].trim();
                p.inputs = values[col++].trim();
                p.concretes = values[col++].trim();
                p.abstracts = values[col++].trim();
                p.dag = values[col++].trim();
                allData.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allData;
    }

    private List<String> readDot() {
        List<String> allData = new ArrayList<>();
        InputStream csv = getClass().getResourceAsStream("/data-files/projects-dependencies.dot");
        try (BufferedReader br = new BufferedReader(new InputStreamReader(csv))) {
            String line = null;
            while ((line = br.readLine()) != null) { // data
                allData.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allData;
    }

    private void writeNewCsv(List<Project> projects, Map<String, String> map) {
        try {
            URL oldCsv = getClass().getResource("/data-files/projects-metrics.csv");
            File dir = new File(oldCsv.toURI());
            dir = dir.getParentFile();
            dir = dir.getParentFile();
            dir = dir.getParentFile();
            dir = new File(dir, "resources");
            dir = new File(dir, "data-files");
            File file = new File(dir, "new-projects-metrics.csv");

            System.out.println("Generating \"" + file.getAbsolutePath() + "\"");
            PrintWriter out = new PrintWriter(file);

            out.println("Name; D; I; A; Ce; Ca; Nc; Na; DAG");
            for (Project p : projects) {
                String name = p.name;
                String newName = map.get(name);
                String d = p.distance;
                String i = p.instability;
                String a = p.abstraction;
                String ce = p.outputs;
                String ca = p.inputs;
                String nc = p.concretes;
                String na = p.abstracts;
                String dag = p.dag;
                out.printf("%s; %s; %s; %s; %s; %s; %s; %s; %s%n", newName, d, i, a, ce, ca, nc, na, dag);
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeNewDot(List<String> originalDot, Map<String, String> map) {
        try {
            URL oldDot = getClass().getResource("/data-files/projects-dependencies.dot");
            File dir = new File(oldDot.toURI());
            dir = dir.getParentFile();
            dir = dir.getParentFile();
            dir = dir.getParentFile();
            dir = new File(dir, "resources");
            dir = new File(dir, "data-files");
            File file = new File(dir, "new-projects-dependencies.dot");

            System.out.println("Generating \"" + file.getAbsolutePath() + "\"");
            PrintWriter out = new PrintWriter(file);

            for (String line : originalDot) {
                String newLine = line;
                for (Entry<String, String> entry : map.entrySet()) {
                    String oldName = entry.getKey();
                    if (newLine.contains(oldName)) {
                        String newName = entry.getValue();
                        newLine = newLine.replaceAll(Pattern.quote(oldName), newName);
                    }
                }
                out.println(newLine);
            }
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
