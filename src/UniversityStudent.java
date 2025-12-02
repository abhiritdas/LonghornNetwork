import java.util.*;

/**
 * Represents a university student with complete academic and social information.
 * Extends the Student class and includes features for roommate assignment,
 * internship tracking, and connection strength calculations based on shared preferences.
 */
public class UniversityStudent extends Student {
    protected UniversityStudent roommate;

    public UniversityStudent(String name, int age, String gender, int year, String major, double gpa, List<String> roommateList, List<String> internshipList) {
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.year = year;
        this.major = major;
        this.gpa = gpa;
        this.roommatePreferences = roommateList;
        this.previousInternships = internshipList;
        this.roommate = null;
    }

    public UniversityStudent getRoommate() {
        return this.roommate;
    }

    public void setRoommate(UniversityStudent roommate) {
        this.roommate = roommate;
    }

    /**
     * Calculates the connection strength between this student and another student.
     * The strength is based on mutual preferences, shared interests, and academic compatibility.
     *
     * @param other the Student to calculate connection strength with
     * @return an integer representing the strength of connection (higher = stronger)
     */
    @Override
    public int calculateConnectionStrength(Student other) {
        int strength = 0;

        if(other instanceof UniversityStudent) {
            // Add 4 if they are roommates.
            if(this.roommate != null && this.roommate == other) {
                strength += 4;
            }

            // Add 3 for each shared internship.
            for(String i : this.previousInternships) {
                if(other.previousInternships.contains(i)) {
                    strength += 3;
                }
            }

            // Add 2 if they share the same major.
            if(this.major.equals(other.major)) {
                strength += 2;
            }

            //  Add 1 if they are the same age.
            if(this.age == other.age) {
                strength += 1;
            }
        }
        return strength;
    }
}
