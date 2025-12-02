import java.util.*;

/**
 * Finds referral paths for internship opportunities within the student network.
 * Uses graph traversal to identify connection chains from a starting student
 * to other students who have experience at a target company.
 */
public class ReferralPathFinder {
    private StudentGraph graph;

    /**
     * Constructs a ReferralPathFinder with a given student graph.
     *
     * @param graph the StudentGraph containing all students and their relationships
     */
    public ReferralPathFinder(StudentGraph graph) {
        // Constructor
        this.graph = graph;
    }

    /**
     * Finds a path of students from a starting student to another student
     * who has worked at the target company.
     * Uses breadth-first search or similar traversal to find the shortest path.
     *
     * @param start the UniversityStudent to start the search from
     * @param targetCompany the name of the company to find referral contacts for
     * @return a list of students representing the referral path, or an empty list if no path exists
     */
    public List<UniversityStudent> findReferralPath(UniversityStudent start, String targetCompany) {
        // Input validation.
        if(start==null || targetCompany==null || targetCompany.isEmpty() || graph==null) {
            return new ArrayList<>();
        }

        // Starting student already had an internship at the target company.
        if(start.previousInternships != null && start.previousInternships.contains(targetCompany)) {
            List<UniversityStudent> temp = new ArrayList<>();
            temp.add(start);
            return temp;
        }

        Map<UniversityStudent, Double> distMap = new HashMap<>();   // Map to store best known distance.
        Map<UniversityStudent, UniversityStudent> prevMap = new HashMap<>(); // Map to store previous node for path reconstruction.
        Set<UniversityStudent> visited = new HashSet<>();

        // Initialize all best known distances to infinity.
        for(UniversityStudent s : graph.getAllNodes()) {
            distMap.put(s, Double.MAX_VALUE);
            prevMap.put(s, null);
        }
        distMap.put(start, 0.0);

        // Each Node object contains the UniversityStudent and the distance to that student object.
        class Node {
            UniversityStudent s;
            double d;
            Node(UniversityStudent s, double d) {
                this.s = s;
                this.d = d;
            }
        }
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingDouble(n -> n.d));
        pq.add(new Node(start, 0.0));

        while(!pq.isEmpty()) {
            Node currNode = pq.poll();
            UniversityStudent currStudent = currNode.s;
            double currDist = currNode.d;

            if(visited.contains(currStudent)) {
                continue;
            }
            visited.add(currStudent);

            if(currStudent.previousInternships!=null && currStudent.previousInternships.contains(targetCompany)) {
                LinkedList<UniversityStudent> path = new LinkedList<>();
                UniversityStudent temp = currStudent;
                while(temp != null) {
                    path.add(temp);
                    temp = prevMap.get(temp);
                }
                Collections.reverse(path);
                return path;
            }

            // Relax edges.
            for (StudentGraph.Edge e : graph.getNeighbors(currStudent)) {
                UniversityStudent neighborStudent = e.getNeighbor();
                int weight = e.getWeight();
                if (neighborStudent == null) continue;

                // Convert weight to cost. This is because stronger weights correspond to shorter costs.
                // The weights were calculated in UniversityStudent class calculateConnectionStrength method.
                if (weight <= 0) continue;
                double cost = 1.0 / (weight + 1.0);
                double alt = currDist + cost;
                if (alt < distMap.getOrDefault(neighborStudent, Double.POSITIVE_INFINITY)) {
                    distMap.put(neighborStudent, alt);
                    prevMap.put(neighborStudent, currStudent);
                    pq.add(new Node(neighborStudent, alt));
                }
            }
        }

        return new ArrayList<>();
    }
}
