package com.example.clickline.Model;

public class Comment_List {

    String comment, commenter, date;


    public Comment_List(String comment, String commenter, String date) {
        this.comment = comment;
        this.commenter = commenter;
        this.date = date;
    }

    public Comment_List() {
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCommenter() {
        return commenter;
    }

    public void setCommenter(String commenter) {
        this.commenter = commenter;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
