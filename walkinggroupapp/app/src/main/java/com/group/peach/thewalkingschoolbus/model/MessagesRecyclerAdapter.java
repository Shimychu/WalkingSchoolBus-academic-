package com.group.peach.thewalkingschoolbus.model;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.group.peach.thewalkingschoolbus.R;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.List;

import retrofit2.Call;

/**responsible for setting up a Recycler View
 * given an array list of Message objects*/
public class MessagesRecyclerAdapter extends RecyclerView.Adapter<MessagesRecyclerAdapter.MessageViewHolder>{
    private Context context;
    private List<Message> allMsg;
    private List<Message> unreadMsg;
    private List<Message> emergencyMsg;

    private WGServerProxy proxy;


    public MessagesRecyclerAdapter(Context context, List<Message> allMsg, List<Message> unreadMsg, List<Message> emgMsg) {
        this.context = context;
        this.allMsg = allMsg;
        this.unreadMsg = unreadMsg;
        this.emergencyMsg = emgMsg;
        this.proxy = ProxyBuilder.getProxy("D3CC767C-22FE-47FC-8287-8E1AA3611CB8", User.getInstance().getUserToken());
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.inbox_message_item,parent,false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        Message currentMessage = allMsg.get(position);
        System.out.println(currentMessage.getText());

        holder.msgContent.setText(currentMessage.getText());
        //setup default
        holder.msgStatusIcon.setImageResource(R.drawable.ic_open_mail);
        holder.msgStatusTxt.setText("");
        holder.msgStatusTxt.setTextColor((ContextCompat.getColor(context, R.color.colorPrimaryDark)));


        //default: message_popup_menu is read
        //change the message_popup_menu's status icon to show UNREAD state
        if (isUnread(currentMessage)){
            holder.msgStatusIcon.setImageResource(R.drawable.ic_unread_mail);
            holder.msgStatusTxt.setText(R.string.unread_status);
        }

        //default: message_popup_menu content is in black text
        //change the message_popup_menu's status to RED if it is EMERGENCY
        if (isEmergency(currentMessage)){
            holder.msgStatusIcon.setImageResource(R.drawable.ic_emg_mail);
            holder.msgStatusTxt.setTextColor(ContextCompat.getColor(context, R.color.red_emergency));
        }

        //setup the OPTION image button
        holder.btnOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(view.getContext(),view, Gravity.RIGHT);
                popupMenu.inflate(R.menu.inbox_popup_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.item_mark_as_read:
                                unreadMsg.remove(currentMessage);
                                markAsRead(currentMessage, view);
                                notifyDataSetChanged();
                                break;

                            case R.id.item_message_delete:
                                deleteMsg(currentMessage, view);
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void markAsRead(Message message, View view){
        Call<Void> call = proxy.markMessageRead(message.getId(), User.getInstance().getId(), true);
        ProxyBuilder.callProxy(view.getContext(), call, nothing->responseRead(nothing));
    }
    private void responseRead(Void nothing){
        notifyDataSetChanged();
    }


    private void deleteMsg(Message currentMessage, View view) {
        //check if the target collections contain the message, then remove
        if (allMsg.contains(currentMessage)){
            allMsg.remove(currentMessage);
        }

        if (unreadMsg.contains(currentMessage)){
            unreadMsg.remove(currentMessage);
        }

        if (emergencyMsg.contains(currentMessage)){
            emergencyMsg.remove(currentMessage);
        }

        Call<Void> call = proxy.deleteMessageById(currentMessage.getId());
        ProxyBuilder.callProxy(view.getContext(), call, nothing->responseEmpty(nothing));
    }
    private void responseEmpty(Void nothing){
        notifyDataSetChanged();
    }

    private boolean isUnread(Message message) {
        for (Message eachMessage : unreadMsg){
            if (message.getId().equals(eachMessage.getId())){
                return true;
            }
        }
        return false;
    }

    private boolean isEmergency(Message message){
        if (message.getEmergency()){
            return true;
        }
        for (Message eachMessage : emergencyMsg){
            if (message.getId().equals(eachMessage.getId())){
                return true;
            }
        }
        return false;
    }

    @Override
    public int getItemCount() {
        return allMsg.size();
    }

    //View Holder class dedicated to MessageRecyclerAdapter
    class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView msgStatusIcon;
        TextView msgContent;
        TextView msgStatusTxt;
        ImageButton btnOption;

        MessageViewHolder(View itemView) {
            super(itemView);

            msgStatusIcon = itemView.findViewById(R.id.img_message_status);
            msgContent = itemView.findViewById(R.id.txt_message_content);
            msgStatusTxt = itemView.findViewById(R.id.txt_message_status);
            btnOption = itemView.findViewById(R.id.btnMoreMessageOption);

        }
    }
}
