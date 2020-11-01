package com.systematics.buggee.Home;

import java.io.Serializable;

public class LiveData implements Serializable {
    public String live_id, live_name, live_details, thumbnail, verified;
    public boolean liked;
    public int likeCount;
    public String fb_id, username, first_name, last_name, profile_pic, block, account_type, message_privacy, comment_privacy, live_privacy;
}
