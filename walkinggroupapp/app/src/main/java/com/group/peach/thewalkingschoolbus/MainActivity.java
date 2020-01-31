package com.group.peach.thewalkingschoolbus;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.group.peach.thewalkingschoolbus.model.Message;
import com.group.peach.thewalkingschoolbus.model.User;
import com.group.peach.thewalkingschoolbus.proxy.ProxyBuilder;
import com.group.peach.thewalkingschoolbus.proxy.WGServerProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    //To remember what page the user was last on
    private static int pageNumber;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawerLayout;

    private int numUnread = 0;

    private WGServerProxy proxy;

    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.NoActionBarT1);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), User.getInstance().getUserToken());

        setViewPager();

        setActionBar();
        preferences = this.getSharedPreferences(getString(R.string.app_preferences), MODE_PRIVATE);
        int rewardSetup = preferences.getInt(RewardActivity.REWARD,0);
        toolbar = findViewById(R.id.action_bar);
        if(rewardSetup == 1){
            toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.Sprout)));
            toolbar.setTitle("Bulbasaur Leafs");
        } else if(rewardSetup == 2){
            toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.Squirtle)));
            toolbar.setTitle("Squirtle Squad");
        } else if(rewardSetup == 3){
            toolbar.setBackground(new ColorDrawable(getResources().getColor(R.color.Charmander)));
            toolbar.setTitle("Charmander Flame");
        } else {
            toolbar.setTitle("Walking School Bus");
        }
        setSupportActionBar(toolbar);

        checkNewMail();

        //Grab the navigation bar view and  set up buttons to open new activities.
        setNavigationBar();
    }

    private void setNavigationBar() {
        navigationView = findViewById(R.id.drawer_nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.isChecked())
                    item.setChecked(false);
                else item.setChecked(true);
                //Closing Drawer on item Click
                drawerLayout.closeDrawers();

                Intent intent;
                switch(item.getItemId()){
                    case R.id.nav_reward:
                        intent = new Intent(MainActivity.this,RewardActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_leaderboard:
                        intent = new Intent(MainActivity.this,LeaderBoardActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_permissions:
                        intent = new Intent(MainActivity.this,PermissionActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nav_logout:
                        preferences = getSharedPreferences(getString(R.string.app_preferences), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.clear();
                        editor.apply();
                        Intent logoutIntent = new Intent(getApplicationContext(),LoginActivity.class);
                        startActivity(logoutIntent);
                        User.clearUser();
                        finish();
                    default:
                        return true;

                }
            }
        });

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void checkNewMail() {
        final int oneMin = 1000 * 60;
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override public void run() {
                        setActionBar();
                    }
                });
            }
        };
        timer.schedule(task, 0, oneMin);
    }

    private void setActionBar() {
        Call<List<Message>> call = proxy.getUnread(User.getInstance().getId(), "unread");
        ProxyBuilder.callProxy(getApplicationContext(), call, messages->responseNumUnread(messages));
    }

    private void responseNumUnread(List<Message> messages){
        numUnread = messages.size();

    }
    //setup the layout of action bar for this activity
    //the mailbox icon would appear with no unread mail
    //the number of unreal mail would appear when there are unread mails
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater mainMenuInflater = getMenuInflater();
        mainMenuInflater.inflate(R.menu.actionbar_menu, menu);
        if (numUnread == 0){
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_actionbar_mailbox));
        }else{
            menu.getItem(0).setTitle(numUnread + " UNREAD");
        }

        return true;
    }

    //setup the action bar button able to launch new Inbox Activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        String msg;
        switch (item.getItemId()){
            case R.id.mailbox:
                startActivity(InboxActivity.launchIntent(getApplicationContext()));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getViews() {

    }

    /*
    Set up the 3 page tab layout
     */
    private void setViewPager(){
        tabLayout = findViewById(R.id.tab_layout);

        //Adding tabs to the tab layout
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_account_circle_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_home_black_24dp));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.ic_map_black_24dp));

        //Set up view pager
        viewPager = findViewById(R.id.viewpager);

        viewPager.setAdapter(makeAdapter());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pageNumber = position;
                tabLayout.setScrollPosition(position,0f,true);
                //If the page selected is maps activity
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setOffscreenPageLimit(2);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }

    /*
    Anon class for viewPagerAdapter
     */
    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }


    private ViewPagerAdapter makeAdapter() {
        MainActivity.ViewPagerAdapter adapter = new MainActivity.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new ProfileFragment());
        adapter.addFragment(new MonitorFragment());
        adapter.addFragment(new MapsFragment());
        return adapter;
    }

}
