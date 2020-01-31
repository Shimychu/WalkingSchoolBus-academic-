package com.group.peach.thewalkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.group.peach.thewalkingschoolbus.model.Message;
import com.group.peach.thewalkingschoolbus.model.MessagesRecyclerAdapter;
import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class InboxActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Message> allMsg = new ArrayList<>();
    private List<Message> allEmg = new ArrayList<>();
    private List<Message> allUnread = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshContainer;
    MessagesRecyclerAdapter recyclerAdapter;



    private WGServerProxy proxy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), User.getInstance().getUserToken());

        setupRefresh();
        receiveMails();
    }

    private void receiveMails() {
        receiveAllMail();
    }

    private void setupRefresh() {
        swipeRefreshContainer = findViewById(R.id.inboxRefresh);
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerAdapter.notifyDataSetChanged();
                swipeRefreshContainer.setRefreshing(false);
            }
        });
    }


    private void receiveAllMail() {
        Call<List<Message>> call = proxy.getAllMsg(User.getInstance().getId());
        ProxyBuilder.callProxy(getApplicationContext(), call, messages->responseReceiveAll(messages));
    }
    private void responseReceiveAll(List<Message> messages) {
        allMsg.addAll(messages);
        receiveUnread();
    }


    private void receiveUnread() {
        Call<List<Message>> call = proxy.getUnread(User.getInstance().getId(), "unread");
        ProxyBuilder.callProxy(getApplicationContext(), call, messages->responseNumUnread(messages));
    }
    private void responseNumUnread(List<Message> messages) {
        allUnread.addAll(messages);
        for (Message eachMsg : allUnread){
            System.out.println("console msg here:" + eachMsg.getText());
        }
        receiveEmg();

    }


    private void receiveEmg() {
        Call<List<Message>> call = proxy.getUnreadEmg(User.getInstance().getId(), "unread", "true");
        ProxyBuilder.callProxy(getApplicationContext(), call, messages->responseEmg(messages));
    }
    private void responseEmg(List<Message> messages) {
        allEmg.addAll(messages);

        setupMessageRecycler();

    }


    private void setupMessageRecycler() {
        recyclerView = findViewById(R.id.message_recycler);
        recyclerAdapter = new MessagesRecyclerAdapter(
                getApplicationContext(),
                allMsg,
                allUnread,
                allEmg
        );
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(recyclerAdapter);
    }

    public static Intent launchIntent(Context caller){
        Intent intent = new Intent(caller, InboxActivity.class);
        return intent;
    }
}
