package io.brickhack6.mobile.Model;

public class User {
    private static User user = null;
    private String uid;
    private String first_name;
    private String last_name;

    private User() {
    }

    public User(String uid, String first_name, String last_name) {
        this.uid = uid;
        this.first_name = first_name;
        this.last_name = last_name;
    }

    public static User getInstance() {
        if (user == null) {
            user = new User();
        }
        return user;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}
