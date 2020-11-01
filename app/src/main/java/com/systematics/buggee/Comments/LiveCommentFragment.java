package com.systematics.buggee.Comments;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.systematics.buggee.R;

public class LiveCommentFragment extends Fragment {
    private Context context;
    private String user_id;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_live_comment, container, false);

//        final String id = user_id.replace(".", "");
//        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
//        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.setHasFixedSize(false);
//        liveCommentAdapter = new LiveCommentAdapter(context, liveCommentData);
//        recyclerView.setAdapter(liveCommentAdapter);
//        DatabaseReference liveRef = FirebaseDatabase.getInstance().getReference().child("liveComment").child(id);
//
//
//        liveRef.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                liveCommentData.clear();
//                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
//                    try {
//
//                        LiveCommentData temp = dataSnapshot1.getValue(LiveCommentData.class);
//                        if (temp != null) {
//                            Log.d("comment", temp.getComment_text());
//                            liveCommentData.add(temp);
//                            liveCommentAdapter.notifyDataSetChanged();
//                            recyclerView.scrollToPosition(liveCommentData.size() - 1);
//                        }
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//
//
//        send_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (Variables.sharedPreferences.getBoolean(Variables.islogin, false)) {
//                    String message = message_edit.getText().toString();
//                    if (!TextUtils.isEmpty(message)) {
//                        SharedPreferences sharedPreferences = context.getSharedPreferences(Variables.pref_name, MODE_PRIVATE);
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("liveComment").child(id);
//                        String commentId = databaseReference.push().getKey();
//                        final LiveCommentData liveComment = new LiveCommentData(Variables.user_id, message, sharedPreferences.getString(Variables.f_name, null), sharedPreferences.getString(Variables.l_name, null), commentId, Variables.user_pic);
//                        databaseReference.child(commentId).setValue(liveComment).addOnCompleteListener(new OnCompleteListener<Void>() {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task) {
//                                if (!task.isSuccessful()) {
//                                    Toast.makeText(context, "Failed To Post Comment!", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    recyclerView.scrollToPosition(liveCommentData.size() - 1);
//                                }
//                            }
//                        });
//                    }
//                } else {
//                    Toast.makeText(context, "You Have To Login!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });


        return v;
    }
}