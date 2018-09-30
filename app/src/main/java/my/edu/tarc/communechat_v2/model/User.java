package my.edu.tarc.communechat_v2.model;

/**
 * Created by Xeosz on 26-Sep-17.
 */

public class User {
    private int uid;
    private String username;
    private String nickname;
    private String password;
    private int gender;
    private int nric;
    private String phone_number;
    private String email;
    private String address;
    private String state;
    private String postal_code;
    private String town;
    private String country;
    private int birth_year;
    private int birth_month;
    private int birth_day;
    private float latitude;
    private float longitude;
    private String student_id;
    private String faculty;
    private String course;
    private int academic_year;
    private String intake;
    private int tutorial_group;
    private int last_online;
    private String status;

    public User() {

    }

    public User(int uid, String username, String nickname, String password, int gender, int nric, String phone_number, String email, String address, String state, String postal_code, String town, String country, int birth_year, int birth_month, int birth_day, float latitude, float longitude, String student_id, String faculty, String course, int academic_year, String intake, int tutorial_group, int last_online, String status) {
        this.uid = uid;
        this.username = username;
        this.nickname = nickname;
        this.password = password;
        this.gender = gender;
        this.nric = nric;
        this.phone_number = phone_number;
        this.email = email;
        this.address = address;
        this.state = state;
        this.postal_code = postal_code;
        this.town = town;
        this.country = country;
        this.birth_year = birth_year;
        this.birth_month = birth_month;
        this.birth_day = birth_day;
        this.latitude = latitude;
        this.longitude = longitude;
        this.student_id = student_id;
        this.faculty = faculty;
        this.course = course;
        this.academic_year = academic_year;
        this.intake = intake;
        this.tutorial_group = tutorial_group;
        this.last_online = last_online;
        this.status = status;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getNric() {
        return nric;
    }

    public void setNric(int nric) {
        this.nric = nric;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getBirth_year() {
        return birth_year;
    }

    public void setBirth_year(int birth_year) {
        this.birth_year = birth_year;
    }

    public int getBirth_month() {
        return birth_month;
    }

    public void setBirth_month(int birth_month) {
        this.birth_month = birth_month;
    }

    public int getBirth_day() {
        return birth_day;
    }

    public void setBirth_day(int birth_day) {
        this.birth_day = birth_day;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
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

    public int getAcademic_year() {
        return academic_year;
    }

    public void setAcademic_year(int academic_year) {
        this.academic_year = academic_year;
    }

    public String getIntake() {
        return intake;
    }

    public void setIntake(String intake) {
        this.intake = intake;
    }

    public int getTutorial_group() {
        return tutorial_group;
    }

    public void setTutorial_group(int tutorial_group) {
        this.tutorial_group = tutorial_group;
    }

    public int getLast_online() {
        return last_online;
    }

    public void setLast_online(int last_online) {
        this.last_online = last_online;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
