package com.example.project1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;

    private phoneFragment phoneFragment;
    private photoFragment photoFragment;
    private todoFragment todoFragment;

    int MY_REQUEST_PERMISSIONS;
    public static String[] PERMISSIONS = new String[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d("hamApp main", "onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        MY_REQUEST_PERMISSIONS = intent.getExtras().getInt("myPermissionCode");
        PERMISSIONS = intent.getExtras().getStringArray("permissionList");

        //setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        photoFragment = new photoFragment();
        phoneFragment = new phoneFragment();
        todoFragment = new todoFragment();
        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(phoneFragment, getString(R.string.title_menu1));
        viewPagerAdapter.addFragment(photoFragment, getString(R.string.title_menu2));
        viewPagerAdapter.addFragment(todoFragment, getString(R.string.title_menu3));
        viewPager.setAdapter(viewPagerAdapter);

        todoFragment.todoDB = init_database("todoList.db");
        init_tables(todoFragment.todoDB, "CONTACT");
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private List<Fragment> fragments = new ArrayList<>();
        private List<String> fragmentTitle = new ArrayList<>();

        public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
            super(fm, behavior);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            fragmentTitle.add(title);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position){
            return fragmentTitle.get(position);
        }
    }

    public SQLiteDatabase init_database(String databaseFileName) {
        SQLiteDatabase db = null;
        File file = new File(getFilesDir(), databaseFileName);

        Log.d("hamApp", "PATH: " + file.toString());
        //System.out.println("PATH: " + file.toString());

        try {
            db = SQLiteDatabase.openOrCreateDatabase(file, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (db == null) { System.out.println("DB creation failed. " + file.getAbsolutePath()); }

        return db;
    }

    private void init_tables(SQLiteDatabase sqliteDB, String tableName) {
        if(sqliteDB == null) return;

        String sqlCreateTbl = "CREATE TABLE IF NOT EXISTS "+ tableName + " (" +
                "NUM "      + "INTEGER NOT NULL," +
                "TITLE "    + "STRING NOT NULL," +
                "DATE "     + "STRING NOT NULL," +
                "EMOTION "  + "INTEGER NOT NULL," +
                "BIRTH "    + "STRING NOT NULL" + ")";
        System.out.println(sqlCreateTbl);
        sqliteDB.execSQL(sqlCreateTbl);
    }

    public boolean arePermissionsDenied() {

        Log.d("hamApp main", "arePermissionsDenied");

        for (int i = 0; i < 5; i++) {
            // 5개의 요청사항 중 하나라도 허용하지 않은 상태일 경우
            // fragment에서는 this 대신 getContext()
            if (ContextCompat.checkSelfPermission(this, PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED) {
                return true;
            }
        }
        // 5개 요청사항이 모두 허용된 경우
        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("hamApp", "onActivityResult");
        String code = data.getStringExtra("code");
        Toast.makeText(getApplicationContext(), code, Toast.LENGTH_SHORT).show();
        if(code.equals("edit")) {
            Toast.makeText(getApplicationContext(), "edit", Toast.LENGTH_SHORT).show();
        }
        else if(code.equals("delete")) {
            Toast.makeText(getApplicationContext(), "delete", Toast.LENGTH_SHORT).show();
        }

    }
}