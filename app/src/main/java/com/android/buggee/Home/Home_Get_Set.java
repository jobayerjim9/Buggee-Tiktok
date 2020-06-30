package com.android.buggee.Home;

import java.io.Serializable;

/**
 * Created by AQEEL on 2/18/2019.
 */

public class Home_Get_Set implements Serializable {
    public String fb_id, username, first_name, last_name, profile_pic, account_type, message_privacy, comment_privacy, live_privacy;
    public String video_id, video_description, video_url, gif, thum, created_date;
    public int followed;
    public String sound_id, sound_name, sound_pic;
    public boolean isFriend;
    public String liked, like_count, video_comment_count, views;

}
