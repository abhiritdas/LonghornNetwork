import java.io.*;
import java.util.*;

/**
 * Utility class for parsing student data from external files.
 * Provides functionality to read and validate student information
 * and convert it into UniversityStudent objects.
 */
public class DataParser {
    /**
     * Parses student data from a file and creates UniversityStudent objects.
     * Expected file format: Each line contains student data with fields separated by commas.
     *
     * @param filename the path to the file containing student data
     * @return a list of UniversityStudent objects parsed from the file
     * @throws IOException if the file cannot be read or accessed
     */
    public static List<UniversityStudent> parseStudents(String filename) throws IOException {
        List<UniversityStudent> students = new ArrayList<>();

        // Open the file to read using BufferedReader.
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            // Divide each "student block" up and call parseStudentBlock().
            List<String> block = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    if (!block.isEmpty()) {
                        students.add(parseStudentBlock(block));
                        block.clear();
                    }
                }
                else {
                    block.add(line);
                }
            }

            // Handle the last "student block".
            if (!block.isEmpty()) {
                students.add(parseStudentBlock(block));
            }
        }

        return students;
    }

    /**
     * Parse a block of lines that describe a single student.
     *
     * @param lines the non-empty lines describing one student
     * @return a UniversityStudent instance
     * @throws IOException if the fields cannot be read or accessed
     */
    private static UniversityStudent parseStudentBlock(List<String> lines) throws IOException {
        // Organize student parameters with a HashMap.
        Map<String, String> map = new HashMap<>();
        for (String raw : lines) {
            String s = raw.trim();
            int idx = s.indexOf(':');
            if (idx <= 0) {
                continue;
            }
            String key = s.substring(0, idx).trim().toLowerCase();
            String value = s.substring(idx + 1).trim();
            map.put(key, value);
        }

        String name = map.get("name");
        String ageStr = map.get("age");
        String gender = map.get("gender");
        String yearStr = map.get("year");
        String major = map.get("major");
        String gpaStr = map.get("gpa");
        String roommates = map.getOrDefault("roommatepreferences", "");
        String internships = map.getOrDefault("previousinternships", "");

        // Basic validation (AI)
        List<String> missing = new ArrayList<>();
        if (name == null || name.isEmpty()) missing.add("name");
        if (ageStr == null || ageStr.isEmpty()) missing.add("age");
        if (gender == null || gender.isEmpty()) missing.add("gender");
        if (yearStr == null || yearStr.isEmpty()) missing.add("year");
        if (major == null || major.isEmpty()) missing.add("major");
        if (gpaStr == null || gpaStr.isEmpty()) missing.add("gpa");
        if (!missing.isEmpty()) {
            throw new IOException("Missing required student fields: " + String.join(", ", missing));
        }

        int age;
        int year;
        double gpa;
        try {
            age = Integer.parseInt(ageStr);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid age: '" + ageStr + "'");
        }
        try {
            year = Integer.parseInt(yearStr);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid year: '" + yearStr + "'");
        }
        try {
            gpa = Double.parseDouble(gpaStr);
        } catch (NumberFormatException e) {
            throw new IOException("Invalid GPA: '" + gpaStr + "'");
        }

        List<String> roommateList = new ArrayList<>();
        if (!roommates.isEmpty()) {
            String[] parts = roommates.split(",");
            for (String p : parts) {
                String t = p.trim();
                if (!t.isEmpty()) roommateList.add(t);
            }
        }

        List<String> internshipList = new ArrayList<>();
        if (!internships.isEmpty()) {
            String[] parts = internships.split(",");
            for (String p : parts) {
                String t = p.trim();
                if (!t.isEmpty()) internshipList.add(t);
            }
        }

        return new UniversityStudent(name, age, gender, year, major, gpa, roommateList, internshipList);
    }
}
