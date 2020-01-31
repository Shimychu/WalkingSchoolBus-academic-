package com.group.peach.thewalkingschoolbus;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.Intent;
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

import static android.content.ContentValues.TAG;

public class LoginActivity extends Activity {
    Button btnLogin,btnRegister;
    EditText edUserEmail,edUserPassword;
    private WGServerProxy proxy;
    String userEmail;
    String userPassword;
    SharedPreferences preferences;
    private User user;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppThemeT1);
        super.onCreate(savedInstanceState);
        // Build the server proxy
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), null);

        preferences = this.getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        String savedEmail = preferences.getString(getString(R.string.shared_pref_email),null);
        String savedPassword =preferences.getString(getString(R.string.shared_pref_password),null);
        if(savedEmail != null && savedPassword != null) {
                userEmail = savedEmail;
                userPassword = savedPassword;
                login(savedEmail,savedPassword);
        }
        setContentView(R.layout.activity_login);


        edUserEmail = findViewById(R.id.EmailEnter);
        edUserPassword = findViewById(R.id.PasswordEnter);
        //set login button
        setupLoginButton();
        setupRegButton();
    }

    /*
    Save user data
     */
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(getString(R.string.shared_pref_email), userEmail);
        editor.putString(getString(R.string.shared_pref_password), userPassword);
        editor.apply();
        getData();

    }

    /*
        Request user details via email
     */
    public void getData(){
       Call<User> caller=proxy.getUserByEmail(userEmail);
       ProxyBuilder.callProxy(getApplicationContext(),caller,returnedUser->response(returnedUser));
    }

    /*
    Get user data and store it into user instance
     */

    private void response(User user)
    {
        User.setUser(user);
        user = User.getInstance();
        user.setUserToken(token);
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /*
    Sets login
     */

    private void setupLoginButton()
    {
        btnLogin = (Button)findViewById(R.id.Loginbtn);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check ID and password
                userEmail  = edUserEmail.getText().toString();
                userPassword = edUserPassword.getText().toString();
                login(userEmail,userPassword);
            }
        });
    }

    private void login(String email,String password){
        //ID and pin correct
        user = User.getInstance();
        user.setEmail(email);
        user.setPassword(password);
        // Register for token received:
        ProxyBuilder.setOnTokenReceiveCallback(token -> onReceiveToken(token));

        // Make call
        Call<Void> caller = proxy.login(user);
        ProxyBuilder.callProxy(LoginActivity.this, caller, returnedNothing -> response(returnedNothing));
    }

    private void onReceiveToken(String token) {
        // Replace the current proxy with one that uses the token!
        //Log.w("", "   --> NOW HAVE TOKEN: " + token);
        System.out.println("Token Recieved");
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
        this.token=token;
        saveData();
    }
    private void response(Void returnedNothing) {
        Log.w(TAG, "Server replied to login request (no content was expected).");
    }

    private void setupRegButton(){
        btnRegister = (Button)findViewById(R.id.Registerbtn);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Start Register Activity
                Intent intent= new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
