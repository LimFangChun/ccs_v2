package my.edu.tarc.communechat_v2.model;

public class City {
    public static final String COL_CITY_ID = "city_id";
    public static final String COL_CITY_NAME = "city_name";
    public static final String COL_STATE_ID = "state_id";

    private String city_id;
    private String city_name;
    private String state_id;

    public City(){}

    public City(String city_id, String city_name, String state_id) {
        this.city_id = city_id;
        this.city_name = city_name;
        this.state_id = state_id;
    }
    public String getCity_id() {
        return city_id;
    }

    public void setCity_id(String city_id) {
        this.city_id = city_id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }
}
