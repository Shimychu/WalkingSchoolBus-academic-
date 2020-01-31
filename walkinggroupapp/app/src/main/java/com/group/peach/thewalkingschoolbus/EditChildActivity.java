package com.group.peach.thewalkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.Objects;

import retrofit2.Call;

import static android.content.ContentValues.TAG;

public class EditChildActivity extends AppCompatActivity {

    private long userId;
    WGServerProxy proxy;
    ProfileHolder holder;
    private User childUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_child);
        getExtra();
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), User.getInstance().getUserToken());
        holder=new ProfileHolder();
        fillTextView();


        setupsavebtn();

    }

    /*
    Grab data value from intent to use in the class
     */

    private void getExtra() {
        Intent intent = getIntent();
        userId = intent.getLongExtra("childId", 0l);
    }


    private void fillTextView() {
        Call<User> caller = proxy.getUserById(userId);
        ProxyBuilder.callProxy(getApplicationContext(), caller, targetUser->response(targetUser));
    }

    /*
    Sets text fields with user detail
     */

    private void response(User user){
        childUser = user;
        holder.txt_name.setText(user.getName());
        holder.txt_email.setText(user.getEmail());
        holder.txt_birthday.setText(user.getBirthYear() + " " + user.getBirthMonth());
        holder.txt_homephone.setText(user.getHomePhone());
        holder.txt_cellphone.setText(user.getCellPhone());
        holder.txt_address.setText(user.getAddress());
        holder.txt_emg.setText(user.getEmergencyContactInfo());
        holder.txt_grade.setText(user.getGrade());
        holder.txt_teacher.setText(user.getTeacherName());

    }

    class ProfileHolder{
        private EditText txt_name;
        private EditText txt_email;
        private EditText txt_birthday;
        private EditText txt_homephone;
        private EditText txt_cellphone;
        private EditText txt_address;
        private EditText txt_emg;
        private EditText txt_grade;
        private EditText txt_teacher;
        private Button btn_edit;

        ProfileHolder(){
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

    /*
    Get edittext data
     */
    private void changeUserinfo() {
        if(!holder.txt_name.getText().toString().isEmpty())
            childUser.setName(holder.txt_name.getText().toString());


        if(!holder.txt_email.getText().toString().isEmpty())
            childUser.setEmail(holder.txt_email.getText().toString());


        String birthyear=holder.txt_birthday.getText().toString();
        if (birthyear == null){
            // do nothing
        }else {
//            if (!holder.txt_birthday.getText().toString().isEmpty())
//                user.setBirthYear(Long.parseLong(birthyear));
        }


        if(!holder.txt_homephone.getText().toString().isEmpty())
            childUser.setHomePhone(holder.txt_homephone.getText().toString());

        if(!holder.txt_cellphone.getText().toString().isEmpty())
            childUser.setCellPhone(holder.txt_cellphone.getText().toString());

        if(!holder.txt_address.getText().toString().isEmpty())
            childUser.setAddress(holder.txt_address.getText().toString());

        if(!holder.txt_emg.getText().toString().isEmpty())
            childUser.setEmergencyContactInfo(holder.txt_emg.getText().toString());

        if(!Objects.equals(holder.txt_grade.getText().toString(), "(student only)"))
            childUser.setGrade(holder.txt_grade.getText().toString());

        if(!Objects.equals(holder.txt_teacher.getText().toString(), "(student only)"))
            childUser.setTeacherName(holder.txt_teacher.getText().toString());
    }
    private void setupsavebtn() {
        Button button = findViewById(R.id.button_savechild);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserinfo();
                Call<User> caller = proxy.editUser(childUser.getId(),childUser);
                ProxyBuilder.callProxy(EditChildActivity.this, caller, returnedUser -> response(returnedUser));
                Intent intent = new Intent(EditChildActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


}
