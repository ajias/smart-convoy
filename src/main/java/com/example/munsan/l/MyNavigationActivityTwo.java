package com.example.munsan.l;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyNavigationActivityTwo extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_navigation_two);
        Toolbar toolbar2 = (Toolbar) findViewById(R.id.toolbar_two);
        setSupportActionBar(toolbar2);

        auth = FirebaseAuth.getInstance();

        DrawerLayout drawer2 = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle2 = new ActionBarDrawerToggle(
                this, drawer2, toolbar2, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer2.addDrawerListener(toggle2);
        toggle2.syncState();

        NavigationView navigationView2 = (NavigationView) findViewById(R.id.nav_view);
        navigationView2.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();

      if (id == R.id.nav_joinCircle) {

            Intent myintent = new Intent(MyNavigationActivityTwo.this, JoinCircleActivity.class);
            startActivity(myintent);

        } else if(id == R.id.nav_leaveCircle){
            Intent leaveircle = new Intent(MyNavigationActivityTwo.this, LeaveCircleNew.class);
            startActivity(leaveircle);
        }
        else if (id == R.id.nav_chat) {

            Intent chat = new Intent(MyNavigationActivityTwo.this, ChatActivity.class);
            startActivity(chat);

        } else if (id == R.id.nav_signout) {

            FirebaseUser user = auth.getCurrentUser();
            if (user != null) {
                auth.signOut();
                finish();
                Intent intent = new Intent(MyNavigationActivityTwo.this, IndexActivity.class);
                startActivity(intent);

            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
