package my.edu.tarc.communechat_v2.model;

import java.util.Date;

public class Student extends User {
    //variables that define column name
    public static final String COL_STUDENT_ID = "student_id";
    public static final String COL_FACULTY = "faculty";
    public static final String COL_COURSE = "course";
    public static final String COL_TUTORIAL_GROUP = "tutorial_group";
    public static final String COL_INTAKE = "intake";
    public static final String COL_ACADEMIC_YEAR = "academic_year";
    public static final String COL_USER_ID = "user_id";

    //variables for encapsulation
    private String student_id;
    private String faculty;
    private String course;
    private int tutorial_group;
    private String intake;
    private int academic_year;
    private int user_id;

    public Student(){
    }

    public Student(String student_id, String faculty, String course, int tutorial_group,
                   String intake, int academic_year, int user_id) {
        this.student_id = student_id;
        this.faculty = faculty;
        this.course = course;
        this.tutorial_group = tutorial_group;
        this.intake = intake;
        this.academic_year = academic_year;
        this.user_id = user_id;
    }

    public Student(int user_id, String username, String display_name, String password, String position,
                   String gender, String nric, String phone_number, String email, String address,
                   String city_id, String status, Date last_online, String student_id, String faculty,
                   String course, int tutorial_group, String intake, int academic_year, int user_id1,
                   double last_longitude, double last_latitude) {
        super(user_id, username, display_name, password, position, gender, nric, phone_number, email,
                address, city_id, status, last_online, last_longitude, last_latitude);
        this.student_id = student_id;
        this.faculty = faculty;
        this.course = course;
        this.tutorial_group = tutorial_group;
        this.intake = intake;
        this.academic_year = academic_year;
        this.user_id = user_id1;
    }

    public String getStudent_id() {
        return student_id;
    }

    public void setStudent_id(String student_id) {
        this.student_id = student_id;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getTutorial_group() {
        return tutorial_group;
    }

    public void setTutorial_group(int tutorial_group) {
        this.tutorial_group = tutorial_group;
    }

    public String getIntake() {
        return intake;
    }

    public void setIntake(String intake) {
        this.intake = intake;
    }

    public int getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(int academic_year) {
        this.academic_year = academic_year;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
}
