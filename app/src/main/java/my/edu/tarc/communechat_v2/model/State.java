package my.edu.tarc.communechat_v2.model;

public class State {
    public static final String COL_STATE_ID = "state_id";
    public static final String COL_STATE_NAME = "state_name";

    private String state_id;
    private String state_name;

    public State(){}

    public State(String state_id, String state_name) {
        this.state_id = state_id;
        this.state_name = state_name;
    }

    public String getState_id() {
        return state_id;
    }

    public void setState_id(String state_id) {
        this.state_id = state_id;
    }

    public String getState_name() {
        return state_name;
    }

    public void setState_name(String state_name) {
        this.state_name = state_name;
    }
}
