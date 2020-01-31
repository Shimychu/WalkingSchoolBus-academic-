package com.group.peach.thewalkingschoolbus.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.group.peach.thewalkingschoolbus.MainActivity;
import com.group.peach.thewalkingschoolbus.R;
import com.group.peach.thewalkingschoolbus.ViewProfileActivity;

import java.util.List;

/**
 * Recyclever view that populates a list of childrens/parents
 */

public class MonitorRecyclerAdapter extends RecyclerView.Adapter<MonitorRecyclerAdapter.MonitorViewHolder>{

    private Context context;
    private List<User> contactCollection;

    public MonitorRecyclerAdapter(Context context, List<User> userCollection) {
        this.context = context;
        this.contactCollection = userCollection;
    }

    @Override
    public MonitorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.monitor_contact_item,parent,false);
        return new MonitorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MonitorViewHolder holder, final int position) {
        User targetUser = contactCollection.get(position);

        //setup basic Views
        holder.name.setText(targetUser.getName());
        holder.email.setText(targetUser.getEmail());
        holder.img.setImageResource(R.drawable.ic_account_circle_black_24dp);
        if (User.getInstance().isChild(targetUser)){
            holder.status.setText(context.getString(R.string.child_status));
        }else{
            holder.status.setText(context.getString(R.string.parent_status));
        }

        setupOption(holder, position, targetUser);

    }


    private void setupOption(MonitorViewHolder holder, final int position, final User targetUser) {
        holder.optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                setupPopup(view, targetUser, position);
            }
        });
    }

    private void setupPopup(View view, User targetUser, int position) {
        PopupMenu popupMenu = new PopupMenu(view.getContext(),view, Gravity.RIGHT);
        popupMenu.inflate(R.menu.monitor_popup_menu);
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return setupPopupItem(menuItem, view, targetUser, position);
            }
        });
        popupMenu.show();
    }

    private boolean setupPopupItem(MenuItem menuItem, View view, User targetUser, int position) {
        switch (menuItem.getItemId()){
            case R.id.itemViewProfile:
                Intent intent = new Intent(view.getContext(), ViewProfileActivity.class);
                long targetId = targetUser.getId();
                boolean isChild = User.getInstance().isChild(targetUser);
                intent.putExtra("targetId", targetId);
                intent.putExtra("isChild", isChild);
                view.getContext().startActivity(intent);
                break;

            case R.id.itemDelete:
                Toast.makeText(view.getContext(), "Deleted " + targetUser.getName() + " from list.", Toast.LENGTH_SHORT)
                        .show();
                User.getInstance().removeContactByID(targetUser.getId());
                contactCollection.remove(position);
                notifyDataSetChanged();
                break;

            default:
                break;
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return contactCollection.size();
    }

    class MonitorViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView email;
        private ImageView img;
        private TextView status;
        private ImageButton optionButton;

        MonitorViewHolder(final View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.txt_name);
            email = itemView.findViewById(R.id.txt_email);
            img = itemView.findViewById(R.id.person_img);
            status = itemView.findViewById(R.id.txt_status);
            optionButton = itemView.findViewById(R.id.imgBtnMoreOption);
        }

    }
}

