package com.example.project1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Date;

public class TodoDetailActivity extends AppCompatActivity {

    TextView numText;
    TextView titleText;
    EditText titleEditText;
    TextView dateText;
    SQLiteDatabase todoDB;
    DatePicker datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_detail);
        // title bar 없애기
        // requestWindowFeature(Window.FEATURE_NO_TITLE);

        numText = findViewById(R.id.num);
        titleText = findViewById(R.id.titleText);
        titleEditText = findViewById(R.id.titleEditText);
        dateText = findViewById(R.id.dateText);
        datePicker = findViewById(R.id.datePicker);

        Intent intent = getIntent();
        String num = intent.getStringExtra("num");
        numText.setText(num);
        String title = intent.getStringExtra("title");
        titleEditText.setText(title);
        String[] date = intent.getStringExtra("date").split("/");
        datePicker.updateDate(
                Integer.parseInt(date[0]),
                Integer.parseInt(date[1])-1,
                Integer.parseInt(date[2]));
        String test = intent.getStringExtra("todoDB");
        todoDB = SQLiteDatabase.openOrCreateDatabase(test.substring(17), null);
    }

    public void editClick(View view) {
        String newTitle = titleEditText.getText().toString();
        String newDate = datePicker.getYear() + "/" + (datePicker.getMonth()+1) + "/" + datePicker.getDayOfMonth();
        if(newTitle.replace(" ", "").equals("")) {
            Toast.makeText(this, "title이 비어있어요.", Toast.LENGTH_LONG).show();
            return;
        }

        String sqlInsert = "UPDATE CONTACT " +
                "SET TITLE = " + "'" + newTitle + "', " +
                "DATE = " + "'" + newDate + "' " +
                "WHERE NUM = " + numText.getText().toString();

        todoDB.execSQL(sqlInsert);

        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.sliding_down);
    }

    public void deleteClick(View view) {
        String sqlInsert = "DELETE FROM CONTACT " +
                "WHERE NUM = " + numText.getText().toString();
        todoDB.execSQL(sqlInsert);

        sqlInsert = "UPDATE CONTACT " +
                "SET NUM = NUM-1 " +
                "WHERE NUM > " + numText.getText().toString();
        todoDB.execSQL(sqlInsert);

        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.sliding_down);
    }

    public void closeClick(View view) {
        setResult(RESULT_OK);
        finish();
        overridePendingTransition(R.anim.stay, R.anim.sliding_down);
    }

    // 기기 back button 막기
    /*@Override
    public void onBackPressed() {
        return;
    }*/
}