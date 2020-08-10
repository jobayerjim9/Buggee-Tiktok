package com.android.buggee.SimpleClasses;

import android.content.SharedPreferences;
import android.os.Environment;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by AQEEL on 2/15/2019.
 */

public class Variables {


    public static String device = "android";

    public static int screen_width;
    public static int screen_height;

    public static String SelectedAudio_MP3 = "SelectedAudio.mp3";
    public static String SelectedAudio_AAC = "SelectedAudio.aac";

    public static String root = Environment.getExternalStorageDirectory().toString();

    public static final boolean is_secure_info = false;
    public static boolean audio_selected = false;

    public static int max_recording_duration = 18000;
    public static int recording_duration = 18000;
    public static String app_folder = root + "/Buggee/";
    public static String outputfile = app_folder + "output.mp4";
    public static String outputfile2 = app_folder + "output2.mp4";
    public static String output_filter_file = app_folder + "output-filtered.mp4";
    public static String output_filter_file_final = app_folder + "output-filtered-final.mp4";
    public static String output_audio = app_folder + "SelectedAudio.aac";

    public static String gallery_trimed_video = app_folder + "gallery_trimed_video.mp4";
    public static String gallery_resize_video = app_folder + "gallery_resize_video.mp4";


    public static SharedPreferences sharedPreferences;
    public static String pref_name = "pref_name";
    public static String u_id = "u_id";
    public static String u_name = "u_name";
    public static String u_pic = "u_pic";
    public static String f_name = "f_name";
    public static String l_name = "l_name";
    public static String gender = "u_gender";
    public static String islogin = "is_login";
    public static String device_token = "device_token";
    public static String api_token = "api_token";
    public static String device_id = "device_id";
    public static String bio = "bio";
    public static String page_have = "page_have";
    public static String current = "device_id";
    public static String recordType = "record";


    public static String tag = "buggee_";

    public static String Selected_sound_id = "null";


    public static String gif_firstpart = "https://media.giphy.com/media/";
    public static String gif_secondpart = "/100w.gif";

    public static String gif_firstpart_chat="https://media.giphy.com/media/";
    public static String gif_secondpart_chat="/200w.gif";

    public static SimpleDateFormat df =
            new SimpleDateFormat("dd-MM-yyyy HH:mm:ssZZ", Locale.ENGLISH);
    public static SimpleDateFormat df2 =
            new SimpleDateFormat("dd-MM-yyyy HH:mmZZ", Locale.ENGLISH);


    public static String user_id;
    public static String user_name;
    public static String user_pic;


    public final static int permission_camera_code = 786;
    public final static int permission_write_data = 788;
    public final static int permission_Read_data = 789;
    public final static int permission_Recording_audio = 790;
    public final static int Pick_video_from_gallery = 791;


    public static String gif_api_key1 = "giphy_api_key_here";
    public static String privacy_policy = "https://www.privacypolicygenerator.info/live.php?";


    //    public static String base_url="https://api.buggee.app/API/";
    public static final String base_url = "https://edge.buggee.app/";
    public static final String domain = base_url + "index.php?p=";

    public static final String SignUp = domain + "signup";
    public static final String uploadVideo = domain + "uploadVideo";
    public static final String showAllVideos = domain + "showAllVideos";
    public static final String showMyAllVideos = domain + "showMyAllVideos";
    public static final String likeDislikeVideo = domain + "likeDislikeVideo";
    public static final String updateVideoView = domain + "updateVideoView";
    public static final String allSounds = domain + "allSounds";
    public static final String fav_sound = domain + "fav_sound";
    public static final String my_FavSound = domain + "my_FavSound";
    public static final String my_liked_video = domain + "my_liked_video";
    public static final String follow_users = domain + "follow_users";
    public static final String discover = domain + "discover";
    public static final String showVideoComments = domain + "showVideoComments";
    public static final String postComment = domain + "postComment";
    public static final String edit_profile = domain + "edit_profile";
    public static final String get_user_data = domain + "get_user_data";
    public static final String get_followers = domain + "get_followers";
    public static final String get_followings = domain + "get_followings";
    public static final String SearchByHashTag = domain + "SearchByHashTag";
    public static final String sendPushNotification = domain + "sendPushNotification";
    public static final String uploadImage = domain + "uploadImage";
    public static final String DeleteVideo = domain + "DeleteVideo";
    public static final String search = domain + "search";
    public static final String checkExist = domain + "userexist";
    public static final String checkUsernameExist = domain + "usernameexist";
    public static final String signUpEmail = domain + "signupemail";
    public static final String loginEmail = domain + "loginemail";
    public static final String loginUsername = domain + "loginusername";
    public static final String checkphoneExist = domain + "checkphoneexist";
    public static final String loginPhone = domain + "loginphone";
    public static final String changeAccountType = domain + "changeAccountType";
    public static final String messagePrivacy = domain + "messagePrivacy";
    public static final String commentPrivacy = domain + "commentPrivacy";
    public static final String livePrivacy = domain + "livePrivacy";
    public static final String getPrivacy = domain + "getPrivacy";
    public static final String createLive = domain + "createLive";
    public static final String deleteLive = domain + "deleteLive";
    public static final String getAllLive = domain + "getAllLive";
    public static final String getVerified = domain + "getVerified";
    public static final String updateBio = domain + "updateBio";
    public static final String createPage = domain + "createPage";
    public static final String getNotifications = domain + "getNotifications";
    public static final String getPageInfo = domain + "getPageInfo";
    public static final String updatePageName = domain + "updatePageName";
    public static final String updatePageDesc = domain + "updatePageDesc";
    public static final String updatePagePic = domain + "updatePagePic";
    public static final String report = domain + "report";
    public static final String showAllStory = domain + "showAllStory";
    public static final String reportProblem = domain + "reportProblem";
    public static final String updatePageCategory = domain + "updatePageCategory";
    public static final String updatePageButton = domain + "updatePageButton";


}
