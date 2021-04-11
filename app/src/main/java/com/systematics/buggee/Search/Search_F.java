package com.systematics.buggee.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systematics.buggee.Home.Home_Get_Set;
import com.systematics.buggee.Main_Menu.MainMenuFragment;
import com.systematics.buggee.Main_Menu.RelateToFragment_OnBack.RootFragment;
import com.systematics.buggee.Profile.Profile_F;
import com.systematics.buggee.R;
import com.systematics.buggee.SimpleClasses.Adapter_Click_Listener;
import com.systematics.buggee.SimpleClasses.ApiRequest;
import com.systematics.buggee.SimpleClasses.Callback;
import com.systematics.buggee.SimpleClasses.Fragment_Callback;
import com.systematics.buggee.SimpleClasses.Functions;
import com.systematics.buggee.SimpleClasses.Variables;
import com.systematics.buggee.WatchVideos.WatchVideos_F;
import com.facebook.shimmer.ShimmerFrameLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Search_F extends RootFragment {

    View view;
    Context context;
    String type,key;
    ShimmerFrameLayout shimmerFrameLayout;
    RecyclerView recyclerView;

    public Search_F(String type) {
        this.type=type;
        this.key=key;
    }

    public Search_F() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_search, container, false);
        context=getContext();

        shimmerFrameLayout =view.findViewById(R.id.shimmer_view_container);
        shimmerFrameLayout.startShimmer();

        recyclerView=view.findViewById(R.id.recylerview);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);

        Call_Api();

        return view;
    }


  public void Call_Api(){

        JSONObject params=new JSONObject();
        try {
            params.put("type", type);
            params.put("keyword", Search_Main_F.search_edit.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ApiRequest.Call_Api(context, Variables.search, params, new Callback() {
            @Override
            public void Responce(String resp) {
                shimmerFrameLayout.stopShimmer();
                shimmerFrameLayout.setVisibility(View.GONE);

                if(type.equalsIgnoreCase("users"))
                Parse_users(resp);

                if(type.equals("video"))
                    Parse_video(resp);


            }
        });

    }



    ArrayList <Object> data_list;
    public void Parse_users(String responce){

        data_list=new ArrayList<>();

        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equalsIgnoreCase("200")){

                JSONArray msg=jsonObject.optJSONArray("msg");
                for (int i=0;i<msg.length();i++){
                    JSONObject data=msg.optJSONObject(i);

                    Users_Model user = new Users_Model();
                    user.fb_id = data.optString("fb_id");
                    user.username = data.optString("username");
                    user.first_name = data.optString("first_name");
                    user.last_name = data.optString("last_name");
                    user.gender = data.optString("gender");
                    user.profile_pic = data.optString("profile_pic");
                    user.signup_type = data.optString("signup_type");
                    user.videos = data.optString("videos");
                    user.account_type = data.optString("account_type");
                    user.message_privacy = data.optString("message_privacy");
                    user.comment_privacy = data.optString("comment_privacy");
                    user.verified = data.optString("verified");
                    user.live_privacy = data.optString("live_privacy");
                    data_list.add(user);


                }

                if(data_list.isEmpty()){
                    view.findViewById(R.id.no_data_image).setVisibility(View.VISIBLE);
                }else
                    view.findViewById(R.id.no_data_image).setVisibility(View.GONE);

                Users_Adapter adapter=new Users_Adapter(context, data_list, new Adapter_Click_Listener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {

                        Users_Model item=(Users_Model) object;
                        Open_Profile(item.fb_id, item.first_name, item.last_name, item.profile_pic, item.account_type, item.message_privacy, item.live_privacy, item.comment_privacy, item.verified);


                    }
                });
                recyclerView.setAdapter(adapter);


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public void Parse_video(String responce){

        data_list=new ArrayList<>();

        try {
            JSONObject jsonObject=new JSONObject(responce);
            String code=jsonObject.optString("code");
            if(code.equals("200")){
                JSONArray msgArray=jsonObject.getJSONArray("msg");
                for (int i=0;i<msgArray.length();i++) {
                    JSONObject itemdata = msgArray.optJSONObject(i);
                    Home_Get_Set item=new Home_Get_Set();
                    item.fb_id=itemdata.optString("fb_id");

                    JSONObject user_info=itemdata.optJSONObject("user_info");

                    item.first_name=user_info.optString("first_name",context.getResources().getString(R.string.app_name));
                    item.last_name=user_info.optString("last_name","User");
                    item.profile_pic=user_info.optString("profile_pic","null");

                    JSONObject sound_data=itemdata.optJSONObject("sound");
                    item.sound_id=sound_data.optString("id");
                    item.sound_name=sound_data.optString("sound_name");
                    item.sound_pic=sound_data.optString("thum");



                    JSONObject count=itemdata.optJSONObject("count");
                    item.like_count=count.optString("like_count");
                    item.video_comment_count=count.optString("video_comment_count");


                    item.video_id=itemdata.optString("id");
                    item.liked=itemdata.optString("liked");
                    item.video_url=itemdata.optString("video");
                    item.video_description=itemdata.optString("description");

                    item.thum=itemdata.optString("thum");
                    item.created_date=itemdata.optString("created");

                    data_list.add(item);
                }

                if(data_list.isEmpty()){
                    view.findViewById(R.id.no_data_image).setVisibility(View.VISIBLE);
                }else
                    view.findViewById(R.id.no_data_image).setVisibility(View.GONE);


                VideosList_Adapter adapter=new VideosList_Adapter(context, data_list, new Adapter_Click_Listener() {
                    @Override
                    public void onItemClick(View view, int pos, Object object) {

                        Home_Get_Set item=(Home_Get_Set) object;
                        if(view.getId()==R.id.watch_btn){
                            OpenWatchVideo(item.video_id);
                        }
                        else {
                            Open_Profile(item.fb_id, item.first_name, item.last_name, item.profile_pic, item.account_type, item.message_privacy, item.live_privacy, item.comment_privacy, item.verified);
                        }

                    }
                });
                recyclerView.setAdapter(adapter);


            }else {
                Functions.showToast(getActivity(), "" + jsonObject.optString("msg"));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void OpenWatchVideo(String video_id) {
        Intent intent=new Intent(getActivity(), WatchVideos_F.class);
        intent.putExtra("video_id", video_id);
        startActivity(intent);
    }

    public void Open_Profile(String fb_id, String first_name, String last_name, String profile_pic, String account_type, String message_privacy, String live_privacy, String comment_privacy, String verified) {
        if (Variables.sharedPreferences.getString(Variables.u_id, "0").equals(fb_id)) {

            MainMenuFragment.pager.setCurrentItem(3);

        } else {

            Profile_F profile_f = new Profile_F(new Fragment_Callback() {
                @Override
                public void Responce(Bundle bundle) {

                }
            });
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            transaction.setCustomAnimations(R.anim.in_from_right, R.anim.out_to_left, R.anim.in_from_left, R.anim.out_to_right);
            Bundle args = new Bundle();
            args.putString("user_id", fb_id);
            args.putString("user_name", first_name + " " + last_name);
            args.putString("user_pic", profile_pic);
            args.putString("account_type", account_type);
            args.putString("message_privacy", message_privacy);
            args.putString("live_privacy", live_privacy);
            args.putString("comment_privacy", comment_privacy);
            args.putString("verified", verified);
            profile_f.setArguments(args);
            transaction.addToBackStack(null);
            transaction.replace(R.id.Search_Main_F, profile_f).commit();

        }

    }

}