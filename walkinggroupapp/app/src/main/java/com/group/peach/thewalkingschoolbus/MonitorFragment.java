package com.group.peach.thewalkingschoolbus;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.group.peach.thewalkingschoolbus.model.MonitorRecyclerAdapter;
import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class MonitorFragment extends Fragment {
    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshContainer;
    private MonitorRecyclerAdapter recyclerViewAdapter;
    WGServerProxy proxy;

    final String TAG = "MonitorFrag";

    public MonitorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_monitor, container, false);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), User.getInstance().getUserToken());


        setupUser();

        return view;
    }

    //setup views:
    private void setupRefresh() {
        swipeRefreshContainer = view.findViewById(R.id.swipeContainer);
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                recyclerViewAdapter.notifyDataSetChanged();
                swipeRefreshContainer.setRefreshing(false);
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView = view.findViewById(R.id.monitorRecyclerView);
        recyclerViewAdapter = new MonitorRecyclerAdapter(getContext(), User.getInstance().getAllContact());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    private void setupFloatingMenu() {
        view.findViewById(R.id.btn_addChild).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddChildDialog();
            }
        });

        view.findViewById(R.id.btn_addParent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddParentDialog();
            }
        });
    }

    private void showAddChildDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.monitor_new_contact_dialog,null);
        final EditText editEmail = dialogView.findViewById(R.id.edit_searchEmail);

        builder.setView(dialogView);
        final AlertDialog popupDialog = builder.create();
        popupDialog.show();

        Button confirmButton = dialogView.findViewById(R.id.btnDialogAdd);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //If email isn't empty then add user
                if(!editEmail.getText().toString().isEmpty()){

                    Call<User> caller = proxy.getUserByEmail(editEmail.getText().toString());
                    ProxyBuilder.callProxy(view.getContext(), caller, returnedUser -> responseFindChild(returnedUser));

                    Toast.makeText(getActivity().getApplicationContext(), "added user " + editEmail.getText().toString(), Toast.LENGTH_SHORT)
                            .show();

                    popupDialog.dismiss();
                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter a valid email", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        Button cancelButton = dialogView.findViewById(R.id.btnDialogCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDialog.dismiss();
            }
        });

    }

    private void showAddParentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.monitor_new_contact_dialog,null);
        final EditText editEmail = dialogView.findViewById(R.id.edit_searchEmail);


        builder.setView(dialogView);
        final AlertDialog popupDialog = builder.create();
        popupDialog.show();

        Button confirmButton = dialogView.findViewById(R.id.btnDialogAdd);
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editEmail.getText().toString().isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(), "added user " + editEmail.getText().toString(), Toast.LENGTH_SHORT)
                            .show();

                    Call<User> caller = proxy.getUserByEmail(editEmail.getText().toString());
                    ProxyBuilder.callProxy(view.getContext(), caller, returnedUser -> responseFindParent(returnedUser));

                    popupDialog.dismiss();

                }else{
                    Toast.makeText(getActivity().getApplicationContext(), "Please enter a valid email", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        });
        Button cancelButton = dialogView.findViewById(R.id.btnDialogCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popupDialog.dismiss();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    //setup network callbacks:
    private void setupUser() {
        Call<User> IDCaller = proxy.getUserByEmail(User.getInstance().getEmail());
        ProxyBuilder.callProxy(view.getContext(), IDCaller, user -> responseID(user));
    }
    private void responseID(User user) {
        User.getInstance().setId(user.getId());
        Log.w(TAG, "Server replied with user: " + user.getId() + " " + user.getEmail());
        Log.w(TAG, "App replied with user: " + User.getInstance().getId() + " " + User.getInstance().getEmail());
        setupChildren();
        setupParents();
    }


    private void setupChildren() {
        Call<List<User>> childrenCaller = proxy.getChildren(User.getInstance().getId());
        ProxyBuilder.callProxy(view.getContext(), childrenCaller, idCollection -> responseChildrenList(idCollection));
    }
    private void responseChildrenList(List<User> children){
        User.getInstance().getChildren().addAll(children);
        for (User eachUser : User.getInstance().getChildren()){
            Log.d(TAG, "AllChildrenForInstance: " + eachUser.getEmail());
        }
        setupRecyclerView();
        setupFloatingMenu();
        setupRefresh();
    }


    private void setupParents() {
        Call<List<User>> caller = proxy.getParents(User.getInstance().getId());
        ProxyBuilder.callProxy(view.getContext(),caller, parents -> responseParentList(parents));
    }
    private void responseParentList(List<User> parents){
        User.getInstance().getParents().addAll(parents);
        for (User eachUser : User.getInstance().getParents()){
            Log.d(TAG, "AllParentsForInstance: " + eachUser.getEmail());
        }
        setupRecyclerView();
        setupFloatingMenu();
        setupRefresh();
    }


    private void responseFindChild(User child) {
        Log.w(TAG, "Server replied with child: " + child.getName() + " " + child.getEmail() + " id " + child.getId());
        User.getInstance().addChild(child);

        //update user's children
        Map<String, Long> newChildPayload = new HashMap<>();
        newChildPayload.put("id", child.getId());
        Call<Void> caller = proxy.addChild(User.getInstance().getId(), newChildPayload);
        ProxyBuilder.callProxy(view.getContext(), caller, nothing -> responseEmpty(nothing));

        //update target user's parents
        Map<String, Long> newParentPayload = new HashMap<>();
        newParentPayload.put("id", User.getInstance().getId());
        caller = proxy.addParent(child.getId(), newParentPayload);
        ProxyBuilder.callProxy(view.getContext(), caller, nothing->responseEmpty(nothing));

        setupRecyclerView();
        setupFloatingMenu();
        setupRefresh();
    }


    private void responseFindParent(User parent) {
        Log.w(TAG, "Server replied with parent: " + parent.getName() + " " + parent.getEmail());
        User.getInstance().addParent(parent);

        //update user's parent
        Map<String, Long> parentPayload = new HashMap<>();
        parentPayload.put("id", parent.getId());
        Call<Void> caller = proxy.addParent(User.getInstance().getId(), parentPayload);
        ProxyBuilder.callProxy(view.getContext(), caller, nothing -> responseEmpty(nothing));

        //update target user's children
        Map<String, Long> childPayload = new HashMap<>();
        childPayload.put("id", User.getInstance().getId());
        caller = proxy.addChild(parent.getId(), childPayload);
        ProxyBuilder.callProxy(view.getContext(), caller, nothing->responseEmpty(nothing));

        setupRecyclerView();
        setupFloatingMenu();
        setupRefresh();
    }

    private void responseEmpty(Void nothing){
        Log.w(TAG, "updated user's contact");
    }
}
