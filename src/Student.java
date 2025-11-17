import java.util.*;

/**
 * Abstract base class representing a student in the LonghornNetwork system.
 * This class defines the core attributes and behaviors common to all student types.
 * Subclasses must implement the calculateConnectionStrength method to define
 * relationship strength calculations based on specific criteria.
 */
public abstract class Student {
    protected String name;
    protected int age;
    protected String gender;
    protected int year;
    protected String major;
    protected double gpa;
    protected List<String> roommatePreferences;
    protected List<String> previousInternships;

    /**
     * Calculates the connection strength between this student and another student.
     * The strength is based on mutual preferences, shared interests, and academic compatibility.
     *
     * @param other the Student to calculate connection strength with
     * @return an integer representing the strength of connection (higher = stronger)
     */
    public abstract int calculateConnectionStrength(Student other);
}
