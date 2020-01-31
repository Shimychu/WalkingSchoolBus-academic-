package com.group.peach.thewalkingschoolbus;


import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.Objects;

import retrofit2.Call;

import static android.content.ContentValues.TAG;



/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {
    private SharedPreferences preferences;
    public static final int Request_Code_getuser = 100;
    View rootView;
    private User user=User.getInstance();

    private WGServerProxy proxy;

    //private User user;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Build the server proxy
//        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);
        rootView=inflater.inflate(R.layout.fragment_profile, container, false);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), User.getInstance().getUserToken());
        settextview();
        setupUser();
        setupeditbtn();
        setuplogoutbtn();

        return rootView;
    }


    private void setupUser() {
        Call<User> IDCaller = proxy.getUserByEmail(User.getInstance().getEmail());
        ProxyBuilder.callProxy(rootView.getContext(), IDCaller, user -> responseID(user));
    }
    private void responseID(User user) {
        User.getInstance().setId(user.getId());
        User.getInstance().setName(user.getName());
        Log.w(TAG, "Server replied with user: " + user.getId() + " " + user.getEmail() + " " + user.getPassword());
        Log.w(TAG, "App replied with user: " + User.getInstance().getId() + " " + User.getInstance().getEmail() + " " + User.getInstance().getPassword());
        settextview();
    }

    private void settextview() {
        TextView username = (TextView) rootView.findViewById(R.id.text_username);
        username.setText(user.getName());
        TextView useremail=(TextView) rootView.findViewById(R.id.text_useremail);
        useremail.setText(user.getEmail());
        ImageView userpic=(ImageView) rootView.findViewById(R.id.image_profilepicture);
        setUpdatedUserpic(userpic);

        //set text for server update
        TextView userBirthday=(TextView) rootView.findViewById(R.id.text_userdate);
        if(!Objects.equals(user.getBirthMonth(), null) || !Objects.equals(user.getBirthYear(), null)) {
            String userdate = "MONTH: " + user.getBirthMonth() + ", YEAR: " + user.getBirthYear();
            userBirthday.setText(userdate);
        }
        TextView userhphone=(TextView) rootView.findViewById(R.id.text_userhphone);
        if(!Objects.equals(user.getHomePhone(), null))
        {
            userhphone.setText(user.getHomePhone());
        }
        TextView usercphone=(TextView) rootView.findViewById(R.id.text_usercphone);
        if(!Objects.equals(user.getCellPhone(), null))
        {
            userhphone.setText(user.getCellPhone());
        }
        TextView useraddress=(TextView) rootView.findViewById(R.id.text_useraddress);
        if(!Objects.equals(user.getAddress(), null))
        {
            useraddress.setText(user.getAddress());
        }
        TextView useremergency=(TextView) rootView.findViewById(R.id.text_useremergency);
        if(!Objects.equals(user.getEmergencyContactInfo(),null))
        {
            useremergency.setText(user.getEmergencyContactInfo());
        }
        TextView userteacher=(TextView) rootView.findViewById(R.id.text_userteacher);
        if(!Objects.equals(user.getTeacherName(),null))
        {
            userteacher.setText(user.getTeacherName());
        }
        TextView usergrade=(TextView) rootView.findViewById(R.id.text_usergrade);
        if(!Objects.equals(user.getGrade(),null))
        {
            String grade=user.getGrade();
            usergrade.setText(grade);
        }

      //  userpic.setImageAlpha(user.getPhoto());
    }

    private void setUpdatedUserpic(ImageView userpic) {
        SharedPreferences preferences = getActivity().getSharedPreferences(getString(R.string.app_preferences), getActivity().getApplicationContext().MODE_PRIVATE);
        int rewardSetup = preferences.getInt(RewardActivity.REWARD,0);
        if(rewardSetup == 1){
            userpic.setImageDrawable(getResources().getDrawable(R.drawable.t1_96));
        } else if(rewardSetup == 2){
            userpic.setImageDrawable(getResources().getDrawable(R.drawable.t2_96));
        } else if(rewardSetup == 3){
            userpic.setImageDrawable(getResources().getDrawable(R.drawable.t3_96));
        } else {

        }
    }

    private void setupeditbtn() {
        Button editbtn=(Button) rootView.findViewById(R.id.button_edit);
        editbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO: go to edit activity
                System.out.println("editclicked");
                Intent intent = new Intent(getContext(),EditProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void setuplogoutbtn() {
        Button logoutbtn = (Button) rootView.findViewById(R.id.button_logout);
        logoutbtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                // TODO: produce logout activity

                preferences = getActivity().getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.clear();
                editor.apply();
                Intent intent = new Intent(getContext(),LoginActivity.class);
                startActivity(intent);
                User.clearUser();
                getActivity().finish();
            }
        });

    }

    private void response(User user) {
        Log.w(TAG, "Server replied with user: " + user.toString());
    }
}
