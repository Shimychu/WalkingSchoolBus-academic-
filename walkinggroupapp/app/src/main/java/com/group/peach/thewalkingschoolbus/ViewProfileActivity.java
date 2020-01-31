package com.group.peach.thewalkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import retrofit2.Call;

public class ViewProfileActivity extends AppCompatActivity {
    private boolean isChild;
    private long userId;
    ProfileViewHolder holder;
    WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_profile);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), User.getInstance().getUserToken());

        getExtra();
        holder = new ProfileViewHolder();

        fillTextView();
        seteditButton();
    }

    private void seteditButton() {
            Button editbtn=(Button) findViewById(R.id.btn_edit_other_profile);
            editbtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    // TODO: go to edit activity
                    System.out.println("editclicked");
                    Intent intent = new Intent(ViewProfileActivity.this,EditChildActivity.class);
                    intent.putExtra("childId", userId);
                    intent.putExtra("isChild", isChild);
                    startActivity(intent);
                }
            });

    }

    private void fillTextView() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(getApplicationContext(), caller, targetUser->response(targetUser));
    }

    private void response(User user){
        holder.txt_name.setText(user.getName());
        holder.txt_email.setText(user.getEmail());
        holder.txt_birthday.setText(user.getBirthYear() + " " + user.getBirthMonth());
        holder.txt_homephone.setText(user.getHomePhone());
        holder.txt_cellphone.setText(user.getCellPhone());
        holder.txt_address.setText(user.getAddress());
        holder.txt_emg.setText(user.getEmergencyContactInfo());
        holder.txt_grade.setText(user.getGrade());
        holder.txt_teacher.setText(user.getTeacherName());
        if (!isChild){
            holder.btn_edit.setVisibility(View.GONE);
        }

    }

    private void getExtra() {
        Intent intent = getIntent();
        userId = intent.getLongExtra("targetId", 0l);
        isChild = intent.getBooleanExtra("isChild", false);
    }

    class ProfileViewHolder{
        private TextView txt_name;
        private TextView txt_email;
        private TextView txt_birthday;
        private TextView txt_homephone;
        private TextView txt_cellphone;
        private TextView txt_address;
        private TextView txt_emg;
        private TextView txt_grade;
        private TextView txt_teacher;
        private Button btn_edit;

        ProfileViewHolder(){
            txt_name = findViewById(R.id.txt_other_name);
            txt_email = findViewById(R.id.txt_other_email);
            txt_birthday = findViewById(R.id.txt_other_birthday);
            txt_homephone = findViewById(R.id.txt_other_homephone);
            txt_cellphone = findViewById(R.id.txt_other_cellphone);
            txt_address = findViewById(R.id.txt_other_address);
            txt_emg = findViewById(R.id.txt_other_emg);
            txt_grade = findViewById(R.id.txt_other_grade);
            txt_teacher = findViewById(R.id.txt_other_teacher);
            btn_edit = findViewById(R.id.btn_edit_other_profile);
        }

    }

}
