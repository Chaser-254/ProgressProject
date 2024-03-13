package com.example.lightpay;

import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    GoogleSignInOptions gso;
    GoogleSignInClient gsc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle;
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_home1);

        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_home1){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();
        } else if (item.getItemId() == R.id.nav_deposit) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new depositFragment()).commit();
        } else if (item.getItemId() == R.id.nav_pay) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PaybillFragment()).commit();
        } else if (item.getItemId() == R.id.nav_withdraw) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WithdrawFragment()).commit();
        } else if (item.getItemId() == R.id.nav_transfer1) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new TransactFragment()).commit();
        } else if (item.getItemId() == R.id.nav_wallet) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountsFragment()).commit();
        } else if (item.getItemId() == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new settingsFragment()).commit();
        } else if (item.getItemId() == R.id.nav_aboutInfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AboutFragment()).commit();
        } else if (item.getItemId() == R.id.nav_share) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new shareFragment()).commit();
        } else if (item.getItemId() == R.id.nav_logout) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new LogoutFragment()).commit();
        } else if (item.getItemId() == R.id.nav_support) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SupportFragment()).commit();
        } else {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else {
            Toast.makeText(this, "You need to be Authenticated inorder to continue with the app!", Toast.LENGTH_SHORT).show();
        }
    }
}