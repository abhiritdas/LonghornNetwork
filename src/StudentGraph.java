import java.util.*;

/**
 * Represents a graph of students with weighted edges based on connection strength.
 * Edges are undirected and weighted by the compatibility between two students.
 */
public class StudentGraph {
    private Map<UniversityStudent, List<Edge>> adjList;

    /**
     * Represents an edge in the student graph with a neighbor and connection weight.
     */
    public static class Edge {
        private final UniversityStudent neighborStudent;
        private final int weight;

        /**
         * Constructs an edge to a neighboring student with a specified weight.
         * @param neighborStudent the adjacent student
         * @param weight the connection strength between students
         */
        public Edge(UniversityStudent neighborStudent, int weight) {
            this.neighborStudent = neighborStudent;
            this.weight = weight;
        }

        /**
         * Returns the neighboring student for this edge.
         * @return the adjacent UniversityStudent
         */
        public UniversityStudent getNeighbor() {
            return neighborStudent;
        }

        /**
         * Returns the weight of this edge.
         * @return the connection strength between students
         */
        public int getWeight() {
            return weight;
        }

        @Override
        public String toString() {
            return "(" + neighborStudent.name + ", " + weight + ")";
        }
    }


    /**
     * Constructs an empty student graph.
     */
    public StudentGraph() {
        adjList = new HashMap<>();
    }

    /**
     * Constructs a student graph from a list of students with edges based on connection strength.
     * @param students the list of students to add to the graph
     */
    public StudentGraph(List<UniversityStudent> students) {
        adjList = new HashMap<>();
        if(students == null) {
            return;
        }
        for(int i=0; i<students.size(); i++) {
            adjList.putIfAbsent(students.get(i), new ArrayList<Edge>());
        }
        
        // Create edges for every student connection. No edge between two student implies connection strength is zero.
        for(int i=0; i<students.size(); i++) {
            for(int j=i+1; j<students.size(); j++) {
                UniversityStudent s1 = students.get(i);
                UniversityStudent s2 = students.get(j);
                int weight = s1.calculateConnectionStrength(s2);
                if(weight > 0) {
                    addEdge(s1, s2, weight);
                }
            }
        }
    }

    /**
     * adds an undirected edge between two students.
     * 
     * @param student one student object.
     * @param neighborStudent another student object which is a neighbor of the other student.
     * @param weight edge weight.
     */
    public void addEdge(UniversityStudent student, UniversityStudent neighborStudent, int weight) {
        if(student == null || neighborStudent == null) {
            return;
        }

        adjList.putIfAbsent(student, new ArrayList<Edge>());
        adjList.putIfAbsent(neighborStudent, new ArrayList<Edge>());

        adjList.get(student).add(new Edge(neighborStudent, weight));
        adjList.get(neighborStudent).add(new Edge(student, weight));
    }
    
    /**
     * Returns all neighbors of a given student.
     * @param student the student to query
     * @return list of edges representing connections, or null if student is not in graph
     */
    public List<StudentGraph.Edge> getNeighbors(UniversityStudent student) {
        if(student == null || adjList.get(student) == null) {
            return null;
        }

        List<StudentGraph.Edge> neighborList = new ArrayList<>();
        for(int i=0; i<adjList.get(student).size(); i++) {
            neighborList.add(adjList.get(student).get(i));
        }

        return neighborList;
    }

    /**
     * Returns all students in the graph.
     * @return set of all UniversityStudent nodes
     */
    public Set<UniversityStudent> getAllNodes() {
        return adjList.keySet();
    }

    /**
     * Displays student graph on graphical interface.
     */
    public void displayGraph() {

    }
}
