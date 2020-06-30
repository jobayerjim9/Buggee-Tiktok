package com.android.buggee.Comments;

public class LiveCommentData {
    private String user_id, comment_text, first_name, last_name, comment_id, profile_pic;

    public LiveCommentData() {
    }

    public LiveCommentData(String user_id, String comment_text, String first_name, String last_name, String comment_id, String profile_pic) {
        this.user_id = user_id;
        this.comment_text = comment_text;
        this.first_name = first_name;
        this.last_name = last_name;
        this.comment_id = comment_id;
        this.profile_pic = profile_pic;
    }

    public String getProfile_pic() {
        return profile_pic;
    }

    public void setProfile_pic(String profile_pic) {
        this.profile_pic = profile_pic;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getComment_text() {
        return comment_text;
    }

    public void setComment_text(String comment_text) {
        this.comment_text = comment_text;
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

    public String getComment_id() {
        return comment_id;
    }

    public void setComment_id(String comment_id) {
        this.comment_id = comment_id;
    }
}
