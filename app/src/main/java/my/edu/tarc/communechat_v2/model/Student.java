package my.edu.tarc.communechat_v2.model;

public class Student extends User {
    private String student_id;
    private String faculty;
    private String course;
    private int tutorial_group;
    private String intake;
    private int academic_year;
    private int user_id;


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
