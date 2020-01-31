package com.group.peach.thewalkingschoolbus.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.group.peach.thewalkingschoolbus.R;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;



/**populate each item in the recycler view on Permission Activity
 * only authorized users are able to see the deny and approve button for each permission
 * each permission item shows its permission status: DENY, APPROVED, or PENDING
 * only accept one ArrayList of PermissionRequest object to populate the recycler view*/
public class PermissionRecyclerAdapter extends RecyclerView.Adapter<PermissionRecyclerAdapter.PermissionViewHolder> {
    private Context context;
    private List<PermissionRequest> permissionList;

    //constructor
    public PermissionRecyclerAdapter(Context context, List<PermissionRequest> permissionList) {
        this.context = context;
        this.permissionList = permissionList;

        Collections.reverse(permissionList);
    }

    //prepare for the server call for APPROVE and DENY a permission
    private WGServerProxy proxy = ProxyBuilder.getProxy(
            "D3CC767C-22FE-47FC-8287-8E1AA3611CB8",
            User.getInstance().getUserToken()
    );


    //setup the specific layout the recycler view is using for its items
    @Override
    public PermissionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.permission_item,parent,false);
        return new PermissionViewHolder(view);
    }

    //passing data to each recycler view item
    //populate each item's views
    @Override
    public void onBindViewHolder(PermissionViewHolder holder, int position) {
        PermissionRequest currentPermission = permissionList.get(position);

        holder.permissionMsg.setText(currentPermission.getMessage());

        if (currentPermission.getStatus() == WGServerProxy.PermissionStatus.APPROVED){
            holder.permissionStatus.setText(WGServerProxy.PermissionStatus.APPROVED.toString());
            hideApproveDenyBtn(holder);

        }else if (currentPermission.getStatus() == WGServerProxy.PermissionStatus.DENIED){
            holder.permissionStatus.setText(WGServerProxy.PermissionStatus.DENIED.toString());
            hideApproveDenyBtn(holder);

        }else if (currentPermission.getStatus() == WGServerProxy.PermissionStatus.PENDING){
            holder.permissionStatus.setText(WGServerProxy.PermissionStatus.PENDING.toString());
            setupApproveButton(holder, currentPermission);
            setupRejectButton(holder,currentPermission);

            if (!isQualified(currentPermission)){
                holder.notAuthorized.setText("You are not authorized to process the action");
                hideApproveDenyBtn(holder);
            }

        }

    }

    //check whether THIS User is authorized to DENY or APPROVE the permission
    //check UserA and UserB, the two children involved in the permission
    private boolean isQualified(PermissionRequest request){
        boolean result = true;
        if (request.getUserA() != null) {
            if (request.getUserA().getId().equals(User.getInstance().getId())) {
                return false;
            }
        }

        if (request.getUserB() != null) {
            if (request.getUserB().getId().equals(User.getInstance().getId())) {
                return false;
            }
        }
        return result;
    }

    //Reject permission request with server call by clicking REJECT button on one itemView
    private void setupRejectButton(PermissionViewHolder holder, PermissionRequest currentPermission) {
        holder.btnReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<PermissionRequest> PermissionCaller = proxy.approveOrDenyPermissionRequest(
                        currentPermission.getId(),
                        WGServerProxy.PermissionStatus.DENIED
                );
                ProxyBuilder.callProxy(view.getContext(), PermissionCaller, response->emptyResponse(response));
                Toast.makeText(context, "You just denied the request", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    //Approve permission request using server call by clicking REJECT button on one itemView
    private void setupApproveButton(PermissionViewHolder holder, PermissionRequest currentPermission) {
        holder.btnApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<PermissionRequest> PermissionCaller = proxy.approveOrDenyPermissionRequest(
                        currentPermission.getId(),
                        WGServerProxy.PermissionStatus.APPROVED
                );
                ProxyBuilder.callProxy(view.getContext(), PermissionCaller, response->emptyResponse(response));
                Toast.makeText(context, "You just approved the request", Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void emptyResponse(PermissionRequest response) {
        System.out.println("system out:" + response.getStatus().toString());
    }

    //make sure APPROVE and DENY button are always being hidden together
    private void hideApproveDenyBtn(PermissionViewHolder holder){
        holder.btnApprove.setVisibility(View.GONE);
        holder.btnReject.setVisibility(View.GONE);
    }


    //required function for Android recycler view itself
    @Override
    public int getItemCount() {
        return permissionList.size();
    }

    class PermissionViewHolder extends RecyclerView.ViewHolder {
        TextView permissionMsg;
        TextView permissionStatus;
        TextView notAuthorized;
        Button btnReject;
        Button btnApprove;

        //locate all the Views contained in the permission_item layout file
        public PermissionViewHolder(View itemView) {
            super(itemView);

            permissionMsg = itemView.findViewById(R.id.txt_permission_msg);
            permissionStatus = itemView.findViewById(R.id.txt_permission_status);
            notAuthorized = itemView.findViewById(R.id.txt_not_authorized);
            btnReject = itemView.findViewById(R.id.btn_reject);
            btnApprove = itemView.findViewById(R.id.btn_approve);
        }
    }
}
