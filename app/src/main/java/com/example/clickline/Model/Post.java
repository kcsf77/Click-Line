package com.example.clickline.Model;

public class Post {


    String postid, postImage, description, publisher, timestamp;

    public Post(String postid, String postImage, String description, String publisher, String timestamp) {
        this.postid = postid;
        this.postImage = postImage;
        this.description = description;
        this.publisher = publisher;
        this.timestamp = timestamp;
    }

    public Post() {



    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostid() {
        return postid;
    }

    public String getPostImage() {
        return postImage;
    }

    public String getDescription() {
        return description;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getTimestamp() {
        return timestamp;
    }
}
