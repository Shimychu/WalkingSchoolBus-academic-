package com.group.peach.thewalkingschoolbus.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.group.peach.thewalkingschoolbus.R;

import java.util.Collections;
import java.util.List;

public class GroupDialogRecyclerAdapter extends RecyclerView.Adapter<GroupDialogRecyclerAdapter.GroupNameViewHolder>{

    Context context;
    static List<User> groupCollection;
    static int clickedUserId;
    View view;

    public GroupDialogRecyclerAdapter(Context context, List<User> children) {
        this.context = context;
        this.groupCollection = children;
        Collections.sort(children, new User.CompareByUserName());
        clickedUserId = -1;
    }

    @Override
    public GroupNameViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        view = LayoutInflater.from(context).inflate(R.layout.add_to_group_item, parent, false);
        return new GroupNameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GroupNameViewHolder holder, int position) {
        holder.txt_childName.setText(groupCollection.get(position).getName());
        holder.txt_childEmail.setText(groupCollection.get(position).getEmail());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedUserId = position;
                Toast.makeText(view.getContext(),holder.txt_childName.getText().toString() + "has been selected", Toast.LENGTH_SHORT)
                        .show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupCollection.size();
    }

    public class GroupNameViewHolder extends RecyclerView.ViewHolder{
        private TextView txt_childName;
        private TextView txt_childEmail;

        public GroupNameViewHolder(View itemView) {
            super(itemView);
            txt_childName = itemView.findViewById(R.id.txt_childName);
            txt_childEmail = itemView.findViewById(R.id.txt_childEmail);
        }
    }

    public static Long getClickedUserId(){
        if(clickedUserId < 0)
            return -1L;
        return (groupCollection.get(clickedUserId).getId());
    }


}
