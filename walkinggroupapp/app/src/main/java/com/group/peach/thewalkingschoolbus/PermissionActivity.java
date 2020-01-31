package com.group.peach.thewalkingschoolbus;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.group.peach.thewalkingschoolbus.model.PermissionRecyclerAdapter;
import com.group.peach.thewalkingschoolbus.model.PermissionRequest;
import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class PermissionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private PermissionRecyclerAdapter recyclerViewAdapter;
    private WGServerProxy proxy;
    private SwipeRefreshLayout swipeRefreshContainer;

    private List<PermissionRequest> allPermission = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), User.getInstance().getUserToken());

        setupRefresh();
        receivePermissions();
    }


    //make the recycler view refreshable
    //user can pull down to refresh instead of relaunch the activity
    private void setupRefresh() {
        swipeRefreshContainer = findViewById(R.id.inboxRefresh);
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                allPermission = new ArrayList<>();

                receivePermissions();
                recyclerViewAdapter.notifyDataSetChanged();
                swipeRefreshContainer.setRefreshing(false);
            }
        });
    }

    //connect and receive permission for THIS User from the server
    private void receivePermissions() {
        Call<List<PermissionRequest>> getPermissionCall = proxy.getPermissionByUserId(User.getInstance().getId());
        ProxyBuilder.callProxy(getApplicationContext(), getPermissionCall, permissions->receiveAll(permissions));
    }

    //copy the permission from the server to local
    private void receiveAll(List<PermissionRequest> permissions) {
        allPermission.addAll(permissions);
        setupRecyclerView();
    }

    //contained within receivePermission()
    //make sure the recycler view is setup after completing the server call
    private void setupRecyclerView() {
        recyclerView = findViewById(R.id.permission_recycler);
        recyclerViewAdapter = new PermissionRecyclerAdapter(getApplicationContext(), allPermission);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(recyclerViewAdapter);

    }
}
