package com.group.peach.thewalkingschoolbus.model;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.group.peach.thewalkingschoolbus.R;
import com.group.peach.thewalkingschoolbus.ViewProfileActivity;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Pheni on 3/23/2018.
 */

public class GroupRecyclerView extends RecyclerView.Adapter<GroupRecyclerView.ViewHolder> {

    private Group group;
    private List<User> user_list = new ArrayList<>();

    public GroupRecyclerView(List<User> user_list){
        this.user_list = user_list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_list,parent,false);
        return new ViewHolder(cardView);
    }

    /*
    Binds card view
     */

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final CardView cardView = holder.cardView;
        TextView displayname = cardView.findViewById(R.id.tv_groupMembersName);
        CircleImageView imageView = cardView.findViewById(R.id.civ_groupMember);
        String name = String.valueOf(user_list.get(position).getName());
        displayname.setText(name);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ViewProfileActivity.class);
                long targetId = user_list.get(position).getId();
                intent.putExtra("targetId", targetId);
                intent.putExtra("isChild", false);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(user_list == null)
            return 0;
        return user_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CardView cardView;

        public ViewHolder(CardView v) {
            super(v);
            cardView = v;
        }
    }

}
