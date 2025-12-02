import java.util.*;

/**
 * Implements the Gale-Shapley algorithm for stable roommate assignment.
 * This algorithm finds a stable matching between students based on their preferences,
 * ensuring that no pair of unmatched students would both prefer each other over their current matches.
 */
public class GaleShapley {
    /**
     * Assigns roommates to students using the Gale-Shapley algorithm.
     * Creates reciprocal roommate pairings based on mutual preferences.
     * Students with no preferences remain unmatched.
     *
     * @param students the list of UniversityStudent objects to assign roommates for
     */
    public static void assignRoommates(List<UniversityStudent> students) {
        if(students == null || students.isEmpty()) {
            return;
        }

        Map<String, UniversityStudent> stringToUniversityStudent = new HashMap<>();
        Map<UniversityStudent, Integer> nextIndex = new HashMap<>();
        Map<UniversityStudent, UniversityStudent> matchMap = new HashMap<>();
        Queue<UniversityStudent> freeQueue = new ArrayDeque<>();
        
        // A rank map to organize the inverse of the proposer preference list.
        // Each proposing student is mapped to another map holding the proposing student's rankMap
        // for O(1) lookups of other students and their respective ranks.
        Map<UniversityStudent, Map<UniversityStudent, Integer>> mapOfRankMaps = new HashMap<>();

        // Initialize string to UniversityStudent conversion early.
        for(UniversityStudent s : students) {
            stringToUniversityStudent.put(s.name, s);
            matchMap.put(s, null);
            nextIndex.put(s, 0);
        }

        // Initialize gale-shapley data structures.
        for(UniversityStudent s : students) {
            Map<UniversityStudent, Integer> rankMap = new HashMap<>();

            if(s.roommatePreferences == null) {
                mapOfRankMaps.put(s, rankMap);
                continue;
            }

            freeQueue.offer(s);
           
            for(int i=0; i<s.roommatePreferences.size(); i++) {
                UniversityStudent rankedStudent = stringToUniversityStudent.get(s.roommatePreferences.get(i));
                if(rankedStudent != null) {
                    rankMap.put(rankedStudent, i);
                }
            }
            mapOfRankMaps.put(s, rankMap);
        }

        while(!freeQueue.isEmpty()) {
            UniversityStudent proposer = freeQueue.poll();
            int index = nextIndex.get(proposer);

            // Do we skip if proposer is matched according to matchMap?
            if(proposer.getRoommate() != null) {
                continue;
            }

            if(index >= proposer.roommatePreferences.size()) {
                continue;
            }

            UniversityStudent receiver = stringToUniversityStudent.get(proposer.roommatePreferences.get(index));
            nextIndex.put(proposer, index+1);
            // if the receiver is non existent, push proposer back into queue to try again later.
            if(receiver == null) {
                if(nextIndex.get(proposer) < proposer.roommatePreferences.size()) {
                    freeQueue.offer(proposer);
                }
                continue;
            }

            UniversityStudent receiverCurrent = matchMap.get(receiver);
            if(receiverCurrent == null) {
                matchMap.put(proposer, receiver);
                matchMap.put(receiver, proposer);
                proposer.setRoommate(receiver);
                receiver.setRoommate(proposer);
            }
            else {
                Map<UniversityStudent, Integer> receiverRankMap = mapOfRankMaps.get(receiver);
                if(receiverRankMap==null) {
                    receiverRankMap = new HashMap<>();
                }
                // Evaluate whether the receiver prefers its current match or the new proposer.
                int rankOfNew = receiverRankMap.getOrDefault(proposer, Integer.MAX_VALUE);
                int rankOfCurrent = receiverRankMap.getOrDefault(receiverCurrent, Integer.MAX_VALUE);
                if(rankOfNew < rankOfCurrent) {
                    // Receiver prefers the new proposer. Dump current and match with proposer.
                    UniversityStudent proposerCurrent = matchMap.get(proposer);
                    if(proposerCurrent != null) {
                        matchMap.put(proposerCurrent, null);
                    }
                    matchMap.put(proposer, receiver);
                    matchMap.put(receiver, proposer);
                    matchMap.put(receiverCurrent, null);
                    if(nextIndex.get(receiverCurrent) < receiverCurrent.roommatePreferences.size()) {
                        freeQueue.offer(receiverCurrent);
                    }
                    proposer.setRoommate(receiver);
                    receiver.setRoommate(proposer);
                }
                else {
                    // Receiver does not prefer new proposer, push proposer back into queue to try again later.
                    if(nextIndex.get(proposer) < proposer.roommatePreferences.size()) {
                        freeQueue.offer(proposer);
                    }
                }
            }
        }
        for(UniversityStudent student : students) {
            if(student.getRoommate() != null && student.name.compareTo(student.getRoommate().name) < 0) {
                System.out.println(student.name + " <-> " + student.getRoommate().name);
            }
        }
    }
}
