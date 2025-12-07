import java.util.*;
import java.util.concurrent.*;
import java.io.FileWriter;
import java.io.IOException;

// Main.java - Self-contained testing & grading with multiple built‑in test cases.
public class Main {

    // Abhirit: shared resource used by ChatThread and FriendRequestThread to upload messages.
    public static List<String> executionLogs = new ArrayList<>();

    public static void main(String[] args) {
        // Create a list of test cases.
        List<List<UniversityStudent>> testCases = new ArrayList<>();
        testCases.add(generateTestCase1());
        testCases.add(generateTestCase2());
        testCases.add(generateTestCase3());

        int overallScore = 0;
        int count = 0;
        for (int i = 0; i < testCases.size(); i++) {
            System.out.println("\n========================================");
            System.out.println("=== Running Test Case " + (i + 1) + " ===");
            System.out.println("========================================");
            List<UniversityStudent> tc = testCases.get(i);

            System.out.println("\n--- Built-in Test Data for Test Case " + (i + 1) + " ---");
            for (UniversityStudent s : tc) {
                System.out.println(s);
            }

            int score = gradeLab(tc, i + 1);
            System.out.println("\nTest Case " + (i + 1) + " Final Score: " + score);
            overallScore += score;
            count++;
        }
        System.out.println("\n========================================");
        System.out.println("Average Score across all test cases: " + (overallScore / count));

        // Abhirit: Run and export the test cases to React folder
        String reactPath = "longhorn-gui/src/data.json";
        exportAllTestCasesToJSON(testCases, reactPath);
    }

    // Test Case 1: Two groups (Group 1 with four students having mutual preferences, Group 2 with a pair)
    public static List<UniversityStudent> generateTestCase1() {
        List<UniversityStudent> students = new ArrayList<>();

        // Group 1: 4 students with full mutual roommate preferences.
        students.add(new UniversityStudent(
                "Alice", 20, "Female", 2, "Computer Science", 3.5,
                Arrays.asList("Bob", "Charlie", "Frank"), Arrays.asList("Google")
        ));
        students.add(new UniversityStudent(
                "Bob", 21, "Male", 3, "Computer Science", 3.7,
                Arrays.asList("Alice", "Charlie", "Frank"), Arrays.asList("Google", "Microsoft")
        ));
        students.add(new UniversityStudent(
                "Charlie", 20, "Male", 2, "Mathematics", 3.2,
                Arrays.asList("Alice", "Bob", "Frank"), Arrays.asList("None")
        ));
        students.add(new UniversityStudent(
                "Frank", 23, "Male", 3, "Chemistry", 3.1,
                Arrays.asList("Alice", "Bob", "Charlie"), Arrays.asList()
        ));

        // Group 2: 2 students
        students.add(new UniversityStudent(
                "Dana", 22, "Female", 4, "Biology", 3.8,
                Arrays.asList("Evan"), Arrays.asList("Pfizer")
        ));
        students.add(new UniversityStudent(
                "Evan", 22, "Male", 4, "Biology", 3.6,
                Arrays.asList("Dana"), Arrays.asList("Moderna", "Pfizer")
        ));

        return students;
    }

    // Test Case 2: Three students in which one has "DummyCompany" as a previous internship.
    // This test case should yield a referral path when searching for "DummyCompany".
    public static List<UniversityStudent> generateTestCase2() {
        List<UniversityStudent> students = new ArrayList<>();

        students.add(new UniversityStudent(
                "Greg", 24, "Male", 4, "Economics", 3.4,
                Arrays.asList("Helen", "Ivy"), Arrays.asList("InternshipA")
        ));
        students.add(new UniversityStudent(
                "Helen", 24, "Female", 4, "Economics", 3.5,
                Arrays.asList("Greg", "Ivy"), Arrays.asList("InternshipB")
        ));
        students.add(new UniversityStudent(
                "Ivy", 25, "Female", 4, "Economics", 3.8,
                Arrays.asList("Helen", "Greg"), Arrays.asList("DummyCompany")
        ));

        return students;
    }

    // Test Case 3: Three students where one has no roommate preferences.
    // Two of them can be paired and one remains unpaired.
    public static List<UniversityStudent> generateTestCase3() {
        List<UniversityStudent> students = new ArrayList<>();

        students.add(new UniversityStudent(
                "Jack", 19, "Male", 1, "History", 3.0,
                Arrays.asList("Kim"), Arrays.asList("MuseumIntern")
        ));
        students.add(new UniversityStudent(
                "Kim", 19, "Female", 1, "History", 3.2,
                Arrays.asList("Jack"), Arrays.asList("MuseumIntern")
        ));
        students.add(new UniversityStudent(
                "Leo", 20, "Male", 1, "History", 3.5,
                Collections.emptyList(), Arrays.asList("None")
        ));

        return students;
    }

    /**
     * Automated test suite for grading a given test case.
     * Accepts a list of UniversityStudent objects and a testCaseNumber (for output clarity).
     * Returns an integer score.
     */
    public static int gradeLab(List<UniversityStudent> students, int testCaseNumber) {
        int score = 0;
        System.out.println("\n--- Automated Tests for Test Case " + testCaseNumber + " ---");

        // Test StudentGraph (30 pts)
        try {
            StudentGraph graph = new StudentGraph(students);
            // Verify that each edge is reciprocal.
            for (UniversityStudent s : graph.getAllNodes()) {
                List<StudentGraph.Edge> edges = graph.getNeighbors(s);
                for (StudentGraph.Edge edge : edges) {
                    UniversityStudent neighbor = edge.getNeighbor();
                    boolean reciprocalFound = false;
                    for (StudentGraph.Edge reverseEdge : graph.getNeighbors(neighbor)) {
                        if (reverseEdge.getNeighbor().equals(s) && reverseEdge.getWeight() == edge.getWeight()) {
                            reciprocalFound = true;
                            break;
                        }
                    }
                    if (!reciprocalFound) {
                        throw new Exception("Graph edge from " + s.name + " to " + neighbor.name + " is not reciprocal.");
                    }
                }
            }
            graph.displayGraph();
            score += 30;
            System.out.println("Test: StudentGraph passed (+30 pts).");
        } catch (Exception e) {
            System.out.println("Test: StudentGraph failed: " + e.getMessage());
        }

        // Test GaleShapley (20 pts)
        try {
            GaleShapley.assignRoommates(students);
            // Count unpaired students. In an even-sized group, there should be none;
            // in odd-sized groups, at most one can remain unpaired.
            int unpairedCount = 0;
            for (UniversityStudent s : students) {
                if (!s.roommatePreferences.isEmpty()) {
                    if (s.getRoommate() == null) {
                        unpairedCount++;
                    } else if (!s.getRoommate().getRoommate().equals(s)) {
                        throw new Exception("Roommate pairing for " + s.name + " is not reciprocal.");
                    }
                }
            }
            if (unpairedCount > 1) {
                throw new Exception("Too many unpaired students: " + unpairedCount);
            }
            score += 20;
            System.out.println("Test: GaleShapley passed (+20 pts).");
        } catch (Exception e) {
            System.out.println("Test: GaleShapley failed: " + e.getMessage());
        }

        // Test FriendRequestThread and ChatThread with semaphores (20 pts)
        try {
            if (students.size() >= 2) {
                ExecutorService executor = Executors.newFixedThreadPool(4);
                UniversityStudent s1 = students.get(0);
                UniversityStudent s2 = students.get(1);
                // Submit multiple concurrent tasks.
                executor.submit(new FriendRequestThread(s1, s2));
                executor.submit(new ChatThread(s1, s2, "Hello there!"));
                executor.submit(new FriendRequestThread(s2, s1));
                executor.submit(new ChatThread(s2, s1, "Hi back!"));
                executor.shutdown();
                if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                    throw new RuntimeException("Concurrency tasks did not finish in time.");
                }
                score += 20;
                System.out.println("Test: FriendRequestThread/ChatThread passed (+20 pts).");
            } else {
                System.out.println("Not enough students to test threads (0 pts).");
            }
        } catch (Exception e) {
            System.out.println("Test: FriendRequestThread/ChatThread failed: " + e.getMessage());
        }

        // Test ReferralPathFinder using PriorityQueue (10 pts)
        try {
            StudentGraph graph = new StudentGraph(students);
            ReferralPathFinder pathFinder = new ReferralPathFinder(graph);
            // For test case 2, we expect a non-empty referral path when searching for "DummyCompany".
            // For test cases that don't have that internship, the returned path may be empty.
            List<UniversityStudent> path = pathFinder.findReferralPath(students.get(0), "DummyCompany");
            System.out.println("ReferralPathFinder returned path: " + path);
            if (testCaseNumber == 2 && path.isEmpty()) {
                throw new Exception("Expected a referral path, but none was found.");
            }
            score += 10;
            System.out.println("Test: ReferralPathFinder passed (+10 pts).");
        } catch (Exception e) {
            System.out.println("Test: ReferralPathFinder failed: " + e.getMessage());
        }

        // Extra integration points (20 pts)
        score += 20;
        System.out.println("Test: Integration passed (+20 pts).");

        System.out.println("\nTotal Score for Test Case " + testCaseNumber + ": " + score);
        return score;
    }

    /**
     * Exports a LIST of Test Cases to a JSON Array.
     * Each element in the array is a self-contained object with nodes, links, and logs.
     */
    public static void exportAllTestCasesToJSON(List<List<UniversityStudent>> allTestCases, String filename) {
        StringBuilder json = new StringBuilder();
        json.append("[\n"); // Start JSON Array

        for (int i = 0; i < allTestCases.size(); i++) {
            List<UniversityStudent> students = allTestCases.get(i);

            // --- RE-RUN SIMULATION FOR VISUALIZATION ---
            // We re-run these to ensure the logs and roommates are fresh and isolated for this specific case.
            
            // 1. Clear previous logs so this case has its own clean history
            executionLogs.clear();

            // 2. Run Gale-Shapley (Ensures roommate assignments are set)
            GaleShapley.assignRoommates(students);

            // 3. Run Threads (To populate the chat logs for the UI)
            if (students.size() >= 2) {
                runThreadsForExport(students.get(0), students.get(1));
            }

            // --- BUILD JSON OBJECT ---
            json.append("  {\n");
            json.append("    \"caseId\": ").append(i + 1).append(",\n");
            json.append("    \"caseName\": \"Test Case ").append(i + 1).append("\",\n");

            // --- 1. Nodes ---
            json.append("    \"nodes\": [\n");
            for (int j = 0; j < students.size(); j++) {
                UniversityStudent s = students.get(j);
                String roommateName = (s.getRoommate() != null) ? s.getRoommate().name : "None";
                
                json.append("      {\n");
                json.append(String.format("        \"id\": \"%s\",\n", s.name));
                json.append(String.format("        \"group\": \"%s\",\n", s.major));
                json.append(String.format("        \"roommate\": \"%s\",\n", roommateName));
                json.append(String.format("        \"internships\": %s\n", listToJson(s.previousInternships)));
                json.append("      }");

                if (j < students.size() - 1) json.append(",\n");
                else json.append("\n");
            }
            json.append("    ],\n");

            // --- 2. Links ---
            json.append("    \"links\": [\n");
            StudentGraph graph = new StudentGraph(students);
            boolean firstLink = true;
            for (UniversityStudent s : students) {
                List<StudentGraph.Edge> edges = graph.getNeighbors(s);
                if (edges != null) {
                    for (StudentGraph.Edge e : edges) {
                        // Undirected graph check: only add if name < neighborName
                        if (s.name.compareTo(e.getNeighbor().name) < 0) {
                            if (!firstLink) json.append(",\n");
                            
                            json.append("      {\n");
                            json.append(String.format("        \"source\": \"%s\",\n", s.name));
                            json.append(String.format("        \"target\": \"%s\",\n", e.getNeighbor().name));
                            json.append(String.format("        \"value\": %d\n", e.getWeight()));
                            json.append("      }");
                            firstLink = false;
                        }
                    }
                }
            }
            json.append("\n    ],\n");

            // --- 3. Logs ---
            json.append("    \"logs\": [\n");
            if (executionLogs != null && !executionLogs.isEmpty()) {
                for (int k = 0; k < executionLogs.size(); k++) {
                    String log = executionLogs.get(k).replace("\"", "\\\""); // Escape quotes
                    json.append("      \"").append(log).append("\"");
                    if (k < executionLogs.size() - 1) json.append(",\n");
                    else json.append("\n");
                }
            }
            json.append("    ]\n");

            json.append("  }"); // End of this Test Case Object

            // Add comma if there are more test cases
            if (i < allTestCases.size() - 1) {
                json.append(",\n");
            }
        }

        json.append("\n]"); // End JSON Array

        // --- Write to File ---
        try (FileWriter file = new FileWriter(filename)) {
            file.write(json.toString());
            System.out.println("Successfully exported ALL test cases to " + filename);
        } catch (IOException e) {
            System.err.println("Error writing JSON file: " + e.getMessage());
        }
    }

    /**
     * Helper function to run threads safely during export.
     * We re-run this to capture logs specifically for the generated JSON.
     */
    private static void runThreadsForExport(UniversityStudent s1, UniversityStudent s2) {
        ExecutorService executor = Executors.newFixedThreadPool(4);
        try {
            executor.submit(new FriendRequestThread(s1, s2));
            executor.submit(new ChatThread(s1, s2, "Hello from " + s1.name));
            executor.submit(new FriendRequestThread(s2, s1));
            executor.submit(new ChatThread(s2, s1, "Hi back from " + s2.name));
            executor.shutdown();
            executor.awaitTermination(2, TimeUnit.SECONDS); // Wait for logs to populate
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }

    // --- Helper Method ---
    // Returns a simple inline JSON array: ["Google", "Amazon"]
    private static String listToJson(List<String> list) {
        if (list == null || list.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < list.size(); i++) {
            sb.append("\"").append(list.get(i)).append("\"");
            if (i < list.size() - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }
}
