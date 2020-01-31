package com.group.peach.thewalkingschoolbus;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;

public class LeaderBoardActivity extends AppCompatActivity {
    private WGServerProxy proxy;
    //private User user;
    // private List<User> user_list = new ArrayList<>();
    private ListView list_username;
    private ListView list_userpoints;
    private ListView list_userranking;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), User.getInstance().getUserToken());
        fillListView();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void fillListView() {
        Call<List<User>> caller = proxy.getUsers();
        ProxyBuilder.callProxy(getApplicationContext(), caller, targetUsers -> response(targetUsers));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void response(List<User> users) {

        //create list of top 100 users
        sortUsers(users);
       // List<User> leader_list = users.subList(0, 3);
        List<String> list_point = new ArrayList<String>();
        List<String> list_name = new ArrayList<String>();
        List<String> list_rank = new ArrayList<String>();

        list_point.add("POINTS");
        list_name.add("NAME");
        list_rank.add("RANKING");

        for (int j = 0; j < users.size(); ++j) {
            if(users.get(j).getTotalPointsEarned()==null)
                list_point.add("0");
            else
                list_point.add(Long.toString(users.get(j).getTotalPointsEarned()));
            list_name.add(users.get(j).getName());
            list_rank.add(Integer.toString(j + 1));
        }
        list_username = findViewById(R.id.list_Name);
        list_userpoints = findViewById(R.id.list_Points);
        list_userranking = findViewById(R.id.list_Rank);

        //create point list

        ArrayAdapter<String> adapterpoints = new ArrayAdapter<String>(
                this,   //context for the activity
                R.layout.layout_points,//layout to use
                list_point);
        list_userpoints.setAdapter(adapterpoints);

        //create name list

        ArrayAdapter<String> adaptername = new ArrayAdapter<String>(
                this,
                R.layout.layout_name,
                list_name);
        list_username.setAdapter(adaptername);

        //create rank list
        ArrayAdapter<String> adapterrank = new ArrayAdapter<String>(
                this,
                R.layout.layout_rank,
                list_rank);
        list_userranking.setAdapter(adapterrank);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void sortUsers(List<User> users) {
        Collections.sort(users, new User.CompareUserByScore());
    }
}
