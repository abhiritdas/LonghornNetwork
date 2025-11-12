# LonghornNetwork
## ECE 422C Lab 4, 5, and 6: Networking with Fellow Longhorns!

### Longhorn Network

### Overview
This project simulates a social network called **Longhorn Network**, where students are matched with roommates and find referral paths for internships.

### Key Objectives
1. Implement the **Gale-Shapley** algorithm for roommate assignment.
2. Find internship referral paths using **Dijkstra’s algorithm**.
3. Use **multithreading** to simulate real-time actions like friend requests and chatting.

---

### Getting Started

### IMPORTANT: Please read through the ENTIRE LAB. Implementation details for a later due date might be important now. In practice, it is important to plan ahead!

#### Folder Structure
- `src/`: Contains the main code files.
- `testing/`: Contains `input_sample.txt` and `output_sample.txt`: Sample input and output file format.
- `README.md`: Project instructions.

#### Prerequisites
- **Java**
- Basic knowledge of graph algorithms, threading, and file I/O.
- If you are unfamiliar with Gale-Shapley, you will find [these resources] (https://www.sanfoundry.com/java-program-gale-shapley-algorithm/) helpful. We should be going over other algorithms in class.
---

### Instructions

#### Step 1: Setting Up
1. **Fork** this repository to start working on your own copy. (Not necessary if you can do step 2-6 without any Forbidden Errors)
2. **Clone** the repository to your local machine:
   ```bash
   git clone https://github.com/<your-username>/LonghornNetwork.git
3. **Choose what you want to add and commit**
   ```bash
   git add <filepath/filename>
   git commit -m "commit message of your choosing"
4. **Push to your branch**, DO NOT COMMIT OR PUSH TO MAIN
   ```bash
   git status ##make sure that main pops up here
   git push
---

### Step 2: Generate UML Diagram, State Diagram and Javadoc, DUE DATE: Nov 17, 2025 11:59pm

- **Generate UML Diagram and State Diagram**:
  - Create a UML diagram based on the class and method signatures.
  - Include core relationships between classes, such as inheritance, aggregation, and associations.
  - For the state diagram show the behavior of Longhorn Network, and how each interaction from the core components will occur.
  
- **Write Javadoc**:
  - Generate Javadoc comments for each class and method based on the provided signatures.
  - Include descriptions of parameters and return types.
---

### Step 3: Implement Core Components DUE DATE FOR EVERYTHING BELOW: Dec 1, 2025 11:59pm

Follow the steps below to implement the core functionality of the Longhorn Network. Each section provides specific details, common edge cases, and additional clarifications to help you complete the assignment.

#### 1. Data Parsing (`DataParser.java`) and Student Graph ('StudentGraph.java')
- **Task**: Implement the `parseStudents` method to read student information from a file and create `UniversityStudent` objects. This will help begin implementing StudentGraph.java (see below for details). 
- **Details**:
  - Input file format is provided in `input_sample.txt`.
  - Parse all attributes (e.g., name, age, gender, major, GPA).
  - Store `roommatePreferences` and `previousInternships` as lists.
  - Validate input and handle missing or invalid data gracefully.

- **Edge Cases**:
  - Missing fields (e.g., no roommate preferences).
  - Incorrect formatting in input file (e.g., missing `:` separator).

---
### Step 4: Implement Core Components DUE DATE FOR EVERYTHING BELOW: Dec 1st, 2025 11:59pm

---

### Suggestions for `StudentGraph`

The `StudentGraph` class is intentionally left for you to design and implement. This component is critical for both   formation (using Prim’s algorithm) and referral path finding (using Dijkstra’s algorithm). Follow the steps below to implement it effectively.

---

#### 1. Purpose of the Graph
The `StudentGraph` represents the relationships between students as a weighted graph. Each student is a node, and the connection strength between two students is an edge with a corresponding weight.

---

#### 2. Key Requirements
The graph should support the following:
1. Adding students as nodes.
2. Adding edges between students with weights (connection strengths).
3. Efficient traversal for algorithms like Prim’s and Dijkstra’s.

---

#### 3. Recommended Design
- Use an **Adjacency List** for the graph representation:
  - Each student (node) maps to a list of edges, where each edge connects to another student and has a weight.
  - Example:
    ```
    Alice -> [(Bob, 7), (Charlie, 5)]
    Bob   -> [(Alice, 7), (Charlie, 2)]
    ```

---

#### 4. Methods to Implement
Here are the key methods you should include in the `StudentGraph` class:

1. **Constructor**:
   - Initialize the graph structure (e.g., an adjacency list).
   - Add all students to the graph.

2. **Add Edge**:
   - Add a weighted edge between two students.
   - Ensure the graph is undirected by adding the edge in both directions.

3. **Get Neighbors**:
   - Return a list of edges connected to a specific student.
   - Useful for traversal algorithms like Prim’s and Dijkstra’s.

4. **Get All Nodes**:
   - Return all nodes (students) in the graph.
   - Useful for initializing traversal algorithms.

---

#### 5. Implementation Steps
1. **Build the Graph**:
   - Iterate over all pairs of students.
   - For each pair, calculate the connection strength using `calculateConnectionStrength`.
   - Add an edge between the two students with the calculated weight.

2. **Example Scenario**:
   - Given students Alice, Bob, and Charlie:
     - If Alice has a connection strength of 7 with Bob, and 5 with Charlie:
       ```
       Alice -> [(Bob, 7), (Charlie, 5)]
       Bob   -> [(Alice, 7)]
       Charlie -> [(Alice, 5)]
       ```

---

#### 6. Testing the Graph
Test your graph implementation before using it in algorithms:
- Print the adjacency list to ensure edges and weights are correct.
- Test with edge cases:
  1. Students with no connections.
  2. Students with identical connection strengths to multiple others.

---


#### 7. Roommate Matching (`GaleShapley.java`)
- **Task**: Implement the Gale-Shapley stable matching algorithm to pair students based on roommate preferences.
- **Details**:
  - Each student has a list of preferred roommates. Mutual preferences are prioritized.
  - Students without preferences should remain unpaired.
  - Handle cases where preferences are incomplete or cyclic.

- **Steps**:
  - Parse preference lists for all students.
  - Use a queue for unpaired students to iterate through proposals.
  - A proposal is accepted if the receiver is unpaired or prefers the proposer over their current roommate.

- **Edge Cases**:
  - Students with empty or partial preference lists.
  - Cyclic or conflicting preferences (e.g., Alice prefers Bob, Bob prefers Charlie, Charlie prefers Alice).

---

#### 8. Referral Path Finding (`ReferralPathFinder.java`)
- **Task**: Use Dijkstra’s algorithm to find the shortest path (strongest connection) to a student who interned at a specific company.
- **Details**:
  - Stronger connections should be treated as "shorter" paths.
  - Allow user input to specify the target company.

- **Steps**:
  - Invert the connection weights (e.g., use `10 - weight` for stronger connections).
  - Traverse the graph using Dijkstra’s algorithm from the starting student.
  - Return the path if a student with the target internship is found.

- **Edge Cases**:
  - No student with the target internship.
  - Disconnected graphs with no path to a target.

---

#### 9. Connection Strength (`calculateConnectionStrength`)
- **Task**: Implement a formula to calculate the connection strength between two students based on the following criteria:
  - **Roommate**: Add 4 if they are roommates.
  - **Shared Internships**: Add 3 for each shared internship.
  - **Same Major**: Add 2 if they share the same major.
  - **Same Age**: Add 1 if they are the same age.

- **Details**:
  - This method will be implemented in the `UniversityStudent` class as an override of the abstract method in `Student`.
  - Ensure the method accurately accounts for all the above factors to return the correct connection strength.

- **Edge Cases**:
  - Students with no shared attributes (e.g., no roommate, no shared internships, etc.).
  - Students with no chat history or no defined   membership.
  - Students who are roommates but have no other connections.

---

#### 10. Multithreading (`FriendRequestThread` and `ChatThread`)
- **Task**: Simulate concurrent actions like sending friend requests and chatting between students.
- **Details**:
  - Use threads to manage these interactions concurrently, simulating real-time behavior.
  - Ensure thread-safe operations when updating shared resources such as `chatHistory`.

- **Steps**:
  1. Use `ExecutorService` to manage multiple threads efficiently.
  2. Implement thread-safe methods using synchronized blocks or concurrent data structures to handle:
     - Friend requests: Add students to each other’s friend lists.
     - Messaging: Update chat histories between students.
  3. Log interactions (e.g., "Alice sent a friend request to Bob") for debugging and verification.

- **Testing**:
  - Test with overlapping friend requests and chat threads to ensure thread safety and proper synchronization.
  - Verify that all friend requests and messages are processed without data corruption or missed updates.

---

#### 11. Testing and Validation
- **Sample Input**:
  - Use the provided `input_sample.txt` to verify your implementation.
  - Ensure all attributes in the input file (e.g., roommate preferences, internships) are parsed correctly.

- **Expected Output**:
  - Roommate Assignments:
    - Each student should be matched with their highest-priority roommate if mutually possible.
  - Referral Paths:
    - The path to a student with the specified internship should have the shortest total connection cost.

- **Edge Cases**:
  1. **Incomplete Data**:
     - Students with missing roommate preferences, no internships, or incomplete attributes.
  2. **Disconnected Graphs**:
     - Ensure that all connected components are handled independently.
  3. **Isolated Nodes**:
     - A single student with no connections should form their own  .
  4. **Multithreading**:
     - Overlapping threads for friend requests and messaging must not corrupt shared resources.
     - Ensure all threads finish execution within a reasonable time frame.

- **Validation**:
  - Compare the output of your implementation with the provided `output_sample.txt`.
  - Write your own additional test cases to ensure robustness and correctness.

### Step 5: Implement Core Components DUE DATE FOR EVERYTHING BELOW: Dec 8th, 2025 11:59pm

#### SWING UI

##### An implementation of an user interface using Swing UI. An easy implementation would be to visualize the student graph as either an adjacency list or adjacency matrix, as well as visualizing the roommates,   formations.

**Video Script for 2-4 minute video to be turned in on Apr 30th, 2024 11:59pm**
   - Loading data from Main.java. Use the given main.java to load data. This can be displayed by simply showing the different test cases being loaded once the UI loads up. [20 points]
   - Visualize the data as a graph. Display a graph that shows students and their names and connections as weighted edges. Must be displayed as a graph. [30 points]
   - Visualize roommates within the [15 points]
   - Visualize the referral path finder [15 points]
   - Visualize each student's friend request and chat history. If 'None' then show 'None'. [10 points]
   - Intuitive & Friendly User Interface. Is the user interface intuitive to use, are their load data, filter data by student, run buttons or equivalents? Is the user interface just one or two monotone colors or vibrant? [10 points]

Of note: the last point is subjective and thus is only worth 10 points out of the total 100 of your grade. Mainly we want to evaluate the amount of effort your provided to the final part of this lab.   

### Notes for Students
- The `StudentGraph` class provides the foundation for both   formation and referral path finding. Ensure your implementation is robust and efficient.
- Use the provided method signatures and adjust as needed to meet the requirements of Prim’s and Dijkstra’s algorithms.
- Ask questions during lab sessions or office hours if you’re stuck. Debugging the graph structure is critical for completing this assignment successfully.

## FAQs

### 1. Should we use inverted edge weights (10 - weight) in anywhere else?
- **No**, inverted edge weights are only used in the referral path finder to prioritize stronger connections as shorter paths.
- For   formation, use the **calculated connection strength** directly to minimize the total weight of the  s. This ensures that  s are formed based on the strongest relationships between students.

---

### 2. How do we get disconnected graphs as mentioned in the edge cases?
- Students are disconnected if their **connection strength is 0**, meaning they share no attributes such as internships,  s, etc.
- Disconnected graphs occur naturally when there are groups of students with no connections to each other.
  - For example:
    ```
    Component 1: Alice - Bob
    Component 2: Charlie
    ```
- **Important**: 
  - Do not add an edge for pairs of students with a connection strength of 0.
  - These students are simply not connected in the graph.

---

### 3. Can two students have the exact same name?
- **No**, all student names are guaranteed to be distinct in this assignment.
- You can safely use student names to uniquely identify nodes in the graph and validate roommate preferences.

---


