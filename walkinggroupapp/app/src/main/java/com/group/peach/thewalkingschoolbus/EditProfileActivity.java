package com.group.peach.thewalkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import org.w3c.dom.Text;

import java.util.Objects;

import retrofit2.Call;

import static android.content.ContentValues.TAG;

public class EditProfileActivity extends AppCompatActivity {

    private User user;
    private WGServerProxy proxy;
    View view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        user = User.getInstance();
        Intent intent = getIntent();
        Long id = intent.getLongExtra("id",0);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), user.getUserToken());
        if (id != 0L) {
            Call<User> caller = proxy.getUserById(id);
            ProxyBuilder.callProxy(EditProfileActivity.this,caller,returnUsed->response(returnUsed));
        } else {
            //set first screen before changing profile
            setuserinfo();
            //set up changes to user profile
            setsavebtn();
        }
    }

    private void response(User user) {
        Log.w(TAG, "Server replied with user: " + user.toString());
        this.user = user;
        //set first screen before changing profile
        setuserinfo();
        //set up changes to user profile
        setsavebtn();
    }

    private void setuserinfo() {

            EditText username = (EditText) findViewById(R.id.text_username);
            username.setText(user.getName());

        EditText useremail=(EditText) findViewById(R.id.text_useremail);
            useremail.setText(user.getEmail());
            ImageView userpic=(ImageView) findViewById(R.id.image_profilepicture);

        EditText useryear=(EditText) findViewById(R.id.text_useryear);
        if(!Objects.equals(user.getBirthYear(), null)) {
            useryear.setText(Long.toString(user.getBirthYear()));
        }
        EditText usermonth=(EditText) findViewById(R.id.text_usermonth);
        if(!Objects.equals(user.getBirthMonth(),null)) {
            usermonth.setText(user.getBirthMonth());
        }
        EditText userhphone=(EditText) findViewById(R.id.text_userhphone);
        if(!Objects.equals(user.getHomePhone(), null))
        {
            userhphone.setText(user.getHomePhone());
        }
        EditText usercphone=(EditText) findViewById(R.id.text_usercphone);
        if(!Objects.equals(user.getCellPhone(), null))
        {
            userhphone.setText(user.getCellPhone());
        }
        EditText useraddress=(EditText) findViewById(R.id.text_useraddress);
        if(!Objects.equals(user.getAddress(), null))
        {
            useraddress.setText(user.getAddress());
        }
        EditText useremergency=(EditText) findViewById(R.id.text_useremergency);
        if(!Objects.equals(user.getEmergencyContactInfo(),null))
        {
            useremergency.setText(user.getEmergencyContactInfo());
        }
        EditText userteacher=(EditText) findViewById(R.id.text_userteacher);
        if(!Objects.equals(user.getTeacherName(),null))
        {
            userteacher.setText(user.getTeacherName());
        }
        EditText usergrade=(EditText) findViewById(R.id.text_usergrade);
        if(!Objects.equals(user.getGrade(),null))
        {
            String grade=user.getGrade();
            usergrade.setText(grade);
        }
    }

    private void changeuserinfo() {
        EditText editname=(EditText) findViewById(R.id.text_username);
        if(!editname.getText().toString().isEmpty())
            user.setName(editname.getText().toString());

        EditText editemail=(EditText) findViewById(R.id.text_useremail);
        if(!editemail.getText().toString().isEmpty())
            user.setEmail(editemail.getText().toString());

        EditText edityear=(EditText) findViewById(R.id.text_useryear);
        String birthyear=edityear.getText().toString();
        if(!edityear.getText().toString().isEmpty())
            user.setBirthYear(Long.parseLong(birthyear));

        EditText editmonth=(EditText) findViewById(R.id.text_usermonth);
        if(!editmonth.getText().toString().isEmpty())
             user.setBirthMonth(editmonth.getText().toString());


        EditText edithomephone=(EditText) findViewById(R.id.text_userhphone);
        if(!edithomephone.getText().toString().isEmpty())
            user.setHomePhone(edithomephone.getText().toString());

        EditText editcellphone=(EditText) findViewById(R.id.text_usercphone);
        if(!editcellphone.getText().toString().isEmpty())
             user.setCellPhone(editcellphone.getText().toString());

        EditText editaddress=(EditText) findViewById(R.id.text_useraddress);
        if(!editaddress.getText().toString().isEmpty())
            user.setAddress(editaddress.getText().toString());

        EditText editemergency=(EditText) findViewById(R.id.text_useremergency);
        if(!editemergency.getText().toString().isEmpty())
            user.setEmergencyContactInfo(editemergency.getText().toString());

        EditText editgrade=(EditText) findViewById(R.id.text_usergrade);
        if(!Objects.equals(editgrade.getText().toString(), "(student only)"))
            user.setGrade(editgrade.getText().toString());

        EditText editteacher=(EditText) findViewById(R.id.text_userteacher);
        if(!Objects.equals(editteacher.getText().toString(), "(student only)"))
            user.setTeacherName(editteacher.getText().toString());
    }

    private void setsavebtn() {

        Button button=findViewById(R.id.button_save);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeuserinfo();
                Call<User> caller = proxy.editUser(user.getId(),user);
                ProxyBuilder.callProxy(EditProfileActivity.this, caller, returnedUser -> response(returnedUser));
                Intent intent = new Intent(EditProfileActivity.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
