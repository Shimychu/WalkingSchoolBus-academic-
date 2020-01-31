package com.group.peach.thewalkingschoolbus;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import retrofit2.Call;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "Register";
    private EditText etName;
    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button button;

    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);

        getViews();

    }

    /*
    Grabs views as well as set listener for button
     */
    private void getViews(){
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        button = findViewById(R.id.bTEST);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = etName.getText().toString();
                String email = etEmail.getText().toString();
                if(!isValidEmail(email)){
                    Toast.makeText(getApplicationContext(), "Invalid email address.", Toast.LENGTH_SHORT).show();
                    return;
                }
                String password = etPassword.getText().toString();
                String confirmpassword = etConfirmPassword.getText().toString();
                if(!isValidPassword(password,confirmpassword)){
                    Toast.makeText(getApplicationContext(), "Invalid password. \n Please enter length between 1 and 16 with only letters or numbers.", Toast.LENGTH_SHORT).show();
                    return;
                }
                User user = User.getInstance();
                user.setName(name);
                user.setEmail(email);
                user.setPassword(password);

                // Make call
                Call<User> caller = proxy.createNewUser(user);
                ProxyBuilder.callProxy(RegisterActivity.this, caller, returnedUser -> response(returnedUser));
            }
        });
    }

    public  boolean isValidEmail(CharSequence target) {
        if (target == null)
            return false;
        return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    private boolean isValidPassword(String password, String confirmPassword) {
        if(password.equals(null))
            return false;
        return (password.length() >= 1 && password.length() <= 16 && password.equals(confirmPassword));
    }

    /*
    If user is sucessfully create then go back to login activity.
     */
    private void response(User user) {
        Toast.makeText(RegisterActivity.this,"Successfully created user",Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
        Log.i(TAG, "Server replied with user: " + user.toString());
    }


}
