package com.group.peach.thewalkingschoolbus;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.group.peach.thewalkingschoolbus.model.EarnedRewards;
import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import retrofit2.Call;

public class RewardActivity extends AppCompatActivity {

    private ImageButton btnT0;
    private ImageButton btnT1;
    private ImageButton btnT2;
    private ImageButton btnT3;
    private TextView tvReward;

    private static final Long reqT1 = 500L;
    private static final Long reqT2 = 1500L;
    private static final Long reqT3 = 3000L;

    private WGServerProxy proxy;
    private User user;

    private SharedPreferences preferences;

    public static final String REWARD = "reward";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward);

        user = User.getInstance();
        // Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), user.getUserToken());
        updateUserPoints();
        preferences = this.getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        getViews();


    }


    private void getViews() {
        tvReward = findViewById(R.id.tvPoints);
        btnT0 = findViewById(R.id.no_reward);
        btnT1 = findViewById(R.id.reward_t1);
        btnT2 = findViewById(R.id.reward_t2);
        btnT3 = findViewById(R.id.reward_t3);
    }

    private void buttonListener() {
        SharedPreferences.Editor editor = preferences.edit();
        Long userPoints = 0L;
        if(user.getTotalPointsEarned() != null){
            userPoints = user.getTotalPointsEarned();
        }
        Long finalUserPoints = userPoints;
        //Tier 0 reward button
        btnT0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalUserPoints >= reqT1){
                    btnT1.setBackgroundResource(0);
                    //Change theme of app
                    editor.putInt(RewardActivity.REWARD,0);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Set group to default",Toast.LENGTH_SHORT).show();
                    finishActivity(0);
                } else{
                    Toast.makeText(getApplicationContext(),"Require " + reqT1 + " points",Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(userPoints >= reqT1) {
            btnT1.setBackgroundResource(0);
        }
        //Tier 1 reward button

        btnT1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalUserPoints >= reqT1){
                    btnT1.setBackgroundResource(0);
                    //Change theme of app
                    editor.putInt(RewardActivity.REWARD,1);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Set group to Bulbasaur Leafs.",Toast.LENGTH_SHORT).show();
                    finishActivity(1);
                } else{
                    Toast.makeText(getApplicationContext(),"Require " + reqT1 + " points",Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(finalUserPoints >= reqT2) {
            btnT2.setBackgroundResource(0);
        }
        //Tier 2 reward button
        btnT2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalUserPoints >= reqT2){
                    //Change theme of app
                    editor.putInt(RewardActivity.REWARD,2);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Set group to Squritle Squad.",Toast.LENGTH_SHORT).show();
                    finishActivity(2);
                } else{
                    Toast.makeText(getApplicationContext(),"Require " + reqT2 + " points",Toast.LENGTH_SHORT).show();
                }
            }
        });
        if(finalUserPoints >= reqT3) {
            btnT3.setBackgroundResource(0);
        }
        //Tier 3 reward button
        btnT3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalUserPoints >= reqT3){
                    //Change theme of app
                    editor.putInt(RewardActivity.REWARD,3);
                    editor.apply();
                    Toast.makeText(getApplicationContext(),"Set group to Charmander Flame",Toast.LENGTH_SHORT).show();
                    finishActivity(3);
                } else{
                    Toast.makeText(getApplicationContext(),"Require " + reqT3 + " points",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void finishActivity(int tier){
        EarnedRewards rewards = new EarnedRewards();
        rewards.setTier(tier);
        user.setRewards(rewards);
        Call<User> caller = proxy.editUser(user.getId(),user);
        ProxyBuilder.callProxy(getApplicationContext(),caller,returnUser->responseReward(returnUser));
        Intent intent = new Intent(RewardActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void responseReward(User returnUser) {
    }

    private void updateUserPoints() {
        Call<User> caller=proxy.getUserByEmail(user.getEmail());
        ProxyBuilder.callProxy(getApplicationContext(),caller, returnedUser->response(returnedUser));
    }
    /*
    Get user data and store it into user instance
     */

    private void response(User updateUser)
    {
        this.user.setTotalPointsEarned(updateUser.getTotalPointsEarned());
        if(user.getTotalPointsEarned() == null){

            tvReward.setText("Current Points: 0");
        } else {
            tvReward.setText("Current Points: " + user.getTotalPointsEarned());
        }
        buttonListener();
    }

}
