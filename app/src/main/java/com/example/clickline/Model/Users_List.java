package com.example.clickline.Model;

public class Users_List {

    String full_name, display_name, email, password, User_id, profile;


    public Users_List(String fullName, String displayName, String email, String password, String uid, String profile) {
        this.full_name = fullName;
        this.display_name = displayName;
        this.email = email;
        this.password = password;
        this.User_id = uid;
        this.profile = profile;
    }

    public Users_List(){

    }


    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String display_name) {
        this.display_name = display_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser_id() {
        return User_id;
    }

    public void setUser_id(String user_id) {
        this.User_id = user_id;
    }

    public String getProfile(){
        return profile;
    }

    public void setProfile(String profile){
        this.profile = profile;

    }

}
