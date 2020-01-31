package com.group.peach.thewalkingschoolbus;

import android.app.AlertDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.getbase.floatingactionbutton.FloatingActionButton;
import com.group.peach.thewalkingschoolbus.model.Group;
import com.group.peach.thewalkingschoolbus.model.GroupDialogRecyclerAdapter;
import com.group.peach.thewalkingschoolbus.model.GroupRecyclerView;
import com.group.peach.thewalkingschoolbus.model.Leader;
import com.group.peach.thewalkingschoolbus.model.Message;
import com.group.peach.thewalkingschoolbus.model.ObjectIDModel;
import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

import static java.security.AccessController.getContext;

public class GroupActivity extends AppCompatActivity {

    private static final String TAG = "GROUP_ACTIVITY";
    private long groupID;
    private WGServerProxy proxy;
    private User user;
    private Group group;
    private RecyclerView recyclerView;
    private GroupRecyclerView adapter;
    private List<User> user_list = new ArrayList<>();
    private Map<String, Object> payload;


    //Views
    private FloatingActionButton fabEditMembers;
    private FloatingActionButton fabMsgMembers;
    private FloatingActionButton fabEditGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        Intent intent = getIntent();
        //Passed in value of intent
        groupID = intent.getLongExtra("groupID",0);

        //Grab instance of user
        user = User.getInstance();
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey),user.getUserToken());
        grabViews();
        grabGroupMembers();
        buttonListener();
    }

    //Grab views
    private void grabViews(){
        recyclerView = findViewById(R.id.groupRecyclerView);
        fabEditMembers = findViewById(R.id.btn_editChildren);
        fabMsgMembers = findViewById(R.id.btn_messageGroup);
        fabEditGroup = findViewById(R.id.btn_editGroup);
    }

    //Set up the recycler view
    private void recyclerViewSetup(){
        adapter = new GroupRecyclerView(user_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void buttonListener(){
        fabEditMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        fabMsgMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                messageDialog();
            }
        });
        fabEditGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestLeader();
            }
        });
    }

    private void requestLeader() {
        Call<Group> caller = proxy.getGroupDetail(groupID);
        ProxyBuilder.callProxy(GroupActivity.this,caller,returnGroup->groupResponse(returnGroup));
    }

    //Grab response group and edit the leader
    //Then resend the group to the server via editGroup call.
    private void groupResponse(Group g){
        Leader leader = new Leader();
        leader.setId(User.getInstance().getId());
        g.setLeader(leader);
        Call<Group> caller = proxy.editGroup(g.getId(),g);
        ProxyBuilder.callProxy(GroupActivity.this,caller,returnGroup->editResponse(returnGroup));
    }

    private void editResponse(Group returnGroup) {
    }


    private void messageDialog() {
        //inflate the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
        View dialogView = this.getLayoutInflater().inflate(R.layout.new_message_dialog, null);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.show();

        //setup the views in the dialog
        EditText txtMsgContent = dialog.findViewById(R.id.txt_messageContent);
        Button btnOK = dialogView.findViewById(R.id.msg_dialog_confirm);
        Button btnCancel = dialogView.findViewById(R.id.msg_dialog_cancel);

        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get the message body
                String input = txtMsgContent.getText().toString();
                System.out.println(input);

                //create payload that send to server
                payload = new HashMap<>();
                payload.put("text", input);
                payload.put("emergency", false);

                grabGroupDetails();

                //finally dismiss the dialog
                dialog.dismiss();
            }
        });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }


    /*
    Grabs the gruop details
     */

    private void grabGroupDetails(){
        Call<Group> caller = proxy.getGroupDetail(groupID);
        ProxyBuilder.callProxy(GroupActivity.this,caller,returnGroup->groupResponse(returnGroup, payload));
    }

    /*
    Response for the group
     */
    private void groupResponse(Group returnGroup, Map<String, Object> payload) {
        Long leaderId = returnGroup.getLeader().getId();
        if(leaderId == user.getId()){
            //If I'm leader my message will be sent to :
            //make the server call
            Call<Void> groupCaller = proxy.sendMsgToGroup(groupID, payload);
            ProxyBuilder.callProxy(GroupActivity.this, groupCaller, nothing-> emptyResponse(nothing));
        } else{
            //If i'm NOT leader my message will be sent to :
            Call<Void> parentCaller = proxy.sendMsgToParent(User.getInstance().getId(), payload);
            ProxyBuilder.callProxy(GroupActivity.this, parentCaller, nothing-> emptyResponse(nothing));
        }
    }

    private void emptyResponse(Void nothing) {
    }


    /*
    Call server to get all group members
     */

    private void grabGroupMembers(){
        Call<List<User>> caller = proxy.getGroupMembers(groupID);
        ProxyBuilder.callProxy(GroupActivity.this,caller,returnGroup->response(returnGroup));
    }


    //Calling server with groupId getting group details back.
    private void response(List<User> returnGroup) {
        user_list.clear();
        user_list = returnGroup;
        recyclerViewSetup();
    }

    /*
    Dialogs to add remove or edit users onto group
     */
    private void openDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(GroupActivity.this);
        View dialogView = this.getLayoutInflater().inflate(R.layout.add_to_group_dialog,null);
        //setup recycler view
        RecyclerView recyclerView = dialogView.findViewById(R.id.dialogRecyclerView);
        GroupDialogRecyclerAdapter recyclerViewAdapter = new GroupDialogRecyclerAdapter(getApplicationContext(), User.getInstance().getChildren());

        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(recyclerViewAdapter);

        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.show();

        //setup OK button
        Button btn_OK = dialogView.findViewById(R.id.btn_groupConfirm);
        Button btn_REMOVE = dialogView.findViewById(R.id.btn_groupRemove);
        Button btn_CANCEL = dialogView.findViewById(R.id.btn_groupCancel);

        btn_OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Build the server proxy
                proxy = ProxyBuilder.getProxy(getString(R.string.apikey), user.getUserToken());
                System.out.println(GroupDialogRecyclerAdapter.getClickedUserId());
                //Select a user
                if (GroupDialogRecyclerAdapter.getClickedUserId() >= 0) {
                    ObjectIDModel objectIDModel = new ObjectIDModel();
                    objectIDModel.setId(GroupDialogRecyclerAdapter.getClickedUserId());
                    Call<List<User>> addNewGroupMember = proxy.addNewGroupMember(groupID, objectIDModel);
                    ProxyBuilder.callProxy(GroupActivity.this, addNewGroupMember, returnGroups -> addMemberResponse(returnGroups));
                    dialog.dismiss();
                } else {
                    //If no one is selected, select myself.
                    ObjectIDModel objectIDModel = new ObjectIDModel();
                    objectIDModel.setId(user.getId());
                    Call<List<User>> addNewGroupMember = proxy.addNewGroupMember(groupID, objectIDModel);
                    ProxyBuilder.callProxy(GroupActivity.this, addNewGroupMember, returnGroups -> addMemberResponse(returnGroups));
                    dialog.dismiss();
                }
                grabGroupMembers();
            }
        });

        btn_REMOVE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // Build the server proxy
                if (GroupDialogRecyclerAdapter.getClickedUserId() >= 0) {
                    Call<Void> removeGroupMemberCall = proxy.removeGroupMember(groupID, GroupDialogRecyclerAdapter.getClickedUserId());
                    ProxyBuilder.callProxy(GroupActivity.this, removeGroupMemberCall, returnNothing -> response(returnNothing));
                    dialog.dismiss();
                } else {
                    Call<Void> removeGroupMemberCall = proxy.removeGroupMember(groupID, user.getId());
                    ProxyBuilder.callProxy(GroupActivity.this, removeGroupMemberCall, returnNothing -> response(returnNothing));
                    dialog.dismiss();
                }
            }
        });

        btn_CANCEL.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        }));
    }

    private void addMemberResponse(List<User> groups) {
        Log.w(TAG, "Server replied with user: " + groups.toString());
        grabGroupMembers();
    }

    private void response(Void returnedNothing) {
        Log.w(TAG, "Server replied to login request (no content was expected).");
        grabGroupMembers();
    }



}
