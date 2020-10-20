package com.android.buggee.Comments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.buggee.R;
import com.android.buggee.SimpleClasses.ApiRequest;
import com.android.buggee.SimpleClasses.Callback;
import com.android.buggee.SimpleClasses.Functions;
import com.android.buggee.SimpleClasses.Variables;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by AQEEL on 3/20/2018.
 */

public class Comments_Adapter extends RecyclerView.Adapter<Comments_Adapter.CustomViewHolder > {

    public Context context;
    private Comments_Adapter.OnItemClickListener listener;
    private ArrayList<Comment_Get_Set> dataList;



    // meker the onitemclick listener interface and this interface is impliment in Chatinbox activity
    // for to do action when user click on item
    public interface OnItemClickListener {
        void onItemClick(int positon, Comment_Get_Set item, View view);
    }

    public Comments_Adapter(Context context, ArrayList<Comment_Get_Set> dataList, Comments_Adapter.OnItemClickListener listener) {
        this.context = context;
        this.dataList = dataList;
        this.listener = listener;

    }

    @Override
    public Comments_Adapter.CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewtype) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_comment_layout,null);
        view.setLayoutParams(new RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
        Comments_Adapter.CustomViewHolder viewHolder = new Comments_Adapter.CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public int getItemCount() {
       return dataList.size();
    }


    @Override
    public void onBindViewHolder(final Comments_Adapter.CustomViewHolder holder, final int i) {
        final Comment_Get_Set item= dataList.get(i);


        holder.username.setText(item.first_name+" "+item.last_name);

        try{
        Picasso.with(context).
                load(item.profile_pic)
                .resize(50,50)
                .placeholder(context.getResources().getDrawable(R.drawable.profile_image_placeholder))
                .into(holder.user_pic);

       }catch (Exception e){

       }

        holder.message.setText(item.comments);


        holder.bind(i,item,listener);

   }


    class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView username,message;
        ImageView user_pic;
        RelativeLayout mainlayout;

        public CustomViewHolder(View view) {
            super(view);

            username=view.findViewById(R.id.username);
            user_pic=view.findViewById(R.id.user_pic);
            message = view.findViewById(R.id.message);
            mainlayout = view.findViewById(R.id.mainlayout);

        }

        public void bind(final int postion, final Comment_Get_Set item, final Comments_Adapter.OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(postion, item, v);
                }
            });
            mainlayout.setOnTouchListener(new View.OnTouchListener() {

                private GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {


                    @Override
                    public void onLongPress(MotionEvent e) {
                        super.onLongPress(e);
                        showReportOption(item);
                    }
                });

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });

        }


    }

    private void showReportOption(final Comment_Get_Set comment_get_set) {

        final CharSequence[] options = {"Report Video", "Cancel"};

        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.AlertDialogCustom);

        builder.setTitle(null);

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @Override

            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Report Video")) {
                    //dialog.dismiss();

                    reportComment(comment_get_set);

                } else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });
        if (!comment_get_set.fb_id.equals(Variables.sharedPreferences.getString(Variables.u_id, ""))) {
            builder.show();
        }

    }

    private void reportComment(final Comment_Get_Set comment_get_set) {
        new AlertDialog.Builder(context)
                .setTitle("Are You Sure?")
                .setMessage("Action May Take Within 24 Hour!")
                .setPositiveButton("Report!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        JSONObject parameters = new JSONObject();
                        try {
                            parameters.put("content_id", comment_get_set.id);
                            parameters.put("reported_by", Variables.sharedPreferences.getString(Variables.u_id, ""));
                            parameters.put("type", "comment");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Functions.Show_loader(context, false, false);
                        ApiRequest.Call_Api(context, Variables.report, parameters, new Callback() {
                            @Override
                            public void Responce(String resp) {
                                Functions.cancel_loader();
                                try {
                                    JSONObject jsonObject = new JSONObject(resp);
                                    boolean success = jsonObject.optBoolean("success");
                                    if (success) {
                                        Functions.showToast((Activity) context, "Reported!");
                                    } else {
                                        String message = jsonObject.optString("msg");
                                        Functions.showToast((Activity) context, message);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();


    }


}