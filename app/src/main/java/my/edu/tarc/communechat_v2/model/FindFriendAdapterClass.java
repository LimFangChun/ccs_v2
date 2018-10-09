package my.edu.tarc.communechat_v2.model;

public class FindFriendAdapterClass {
    private int id;
    private String header;
    private String description;

    public FindFriendAdapterClass(){}

    public FindFriendAdapterClass(int id, String header, String description) {
        this.id = id;
        this.header = header;
        this.description = description;
    }

    public int getImageID() {
        return id;
    }

    public void setImageID(int imageID) {
        this.id = imageID;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
