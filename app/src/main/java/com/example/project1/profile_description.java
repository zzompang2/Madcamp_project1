package com.example.project1;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class profile_description extends AppCompatActivity {

    Button go_back;
    Button go_edit;
    ImageView profile_image;
    TextView text_DISPLAY_NAME;
    TextView text_COMPANY;
    TextView text_NUMBER;
    TextView text_ADDRESS;
    TextView text_URL;
    TextView text_NOTE;
    TextView text_ToggleButton_Below;
    ImageView image_Starred;

    ImageView direct_call;
    ImageView direct_email;
    ImageView direct_message;

    private String contact_id;
    private String whether_starred;

    public void erase(){
        getContentResolver().delete(
                ContactsContract.Data.CONTENT_URI,
                ContactsContract.Data.CONTACT_ID + " = " + contact_id,
                null);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_description);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String loaded_string;
        if (bundle != null) {
            Log.d("auaicn", "bundle not empty");
            if ((loaded_string = bundle.getString("profile_in_detail")) == null) {
                Log.d("auaicn", "information loading from main activity to description activity failed");
                finish();
            }
        }else
            loaded_string = null;

        //Log.d("auaicn", "bundle empty");
        //Log.d("auaicn", loaded_string);

        String[] parsed_string = loaded_string.split("__");

        text_DISPLAY_NAME = findViewById(R.id.contact_full_name);
        text_COMPANY = findViewById(R.id.contact_company);
        text_NUMBER = findViewById(R.id.contact_phone_number);
        text_ADDRESS = findViewById(R.id.contact_email_address);
        text_URL = findViewById(R.id.contact_homepage);
        text_NOTE = findViewById(R.id.contact_notes);

        text_DISPLAY_NAME.setText(parsed_string[1]);
        text_COMPANY.setText(parsed_string[2]);
        text_NUMBER.setText(parsed_string[3]);
        text_ADDRESS.setText(parsed_string[4]);
        text_URL.setText(parsed_string[5]);
        text_NOTE.setText(parsed_string[6]);

        // Local variable setting for toggling and resolver operation (ex. Insert, Delete, Update)
        contact_id = parsed_string[8];
        whether_starred = parsed_string[7];

        // Image decoded here.
        ImageDecoder.Source profile_image_source = null;
        if (parsed_string[0] != null){
            Log.d("image setting","has image");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                profile_image_source = ImageDecoder.createSource(getContentResolver(), Uri.parse(parsed_string[0]));
                profile_image.setImageURI(Uri.parse(parsed_string[0]));
            }
        }
        else
            Log.d("image setting","no profile image");


        // Whether Starred, star-Image right of profile image differs (filled, not filled)
        image_Starred = findViewById(R.id.contact_starred);
        text_ToggleButton_Below = findViewById(R.id.contact_toggle_star_button);

        if(parsed_string[7].equals("1") == true){
            image_Starred.setImageResource(R.drawable.starred_true);
            text_ToggleButton_Below.setText("즐겨찾기에서 제거");
        }
        else {
            image_Starred.setImageResource(R.drawable.starred_false);
            text_ToggleButton_Below.setText("+ 즐겨찾기에 추가");
        }

        // GO BACK to CONTACTS button
        go_back = findViewById(R.id.button_contacts_back);
        go_back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        // EDIT button
        go_edit = findViewById(R.id.button_change_to_edit_mode);
        go_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setVisible(true);

            }
        });

        // [Setting] Two Ways to toggle Favorite
        Button Star_Top_image = findViewById(R.id.star_button);
        Button Star_bottom = findViewById(R.id.contact_toggle_star_button);

        // Starred Toggle Event
        Star_bottom.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                ContentValues update_contents = new ContentValues();

                String message;

                if(whether_starred.equals("1")) {
                    message = "unStarred";
                    update_contents.put("starred", "0");
                }
                else {
                    message = "Starred";
                    update_contents.put("starred", "1");
                }

                Uri target_uri = ContactsContract.Contacts.CONTENT_URI;

                // need contact ID
                getContentResolver().update(
                        target_uri,
                        update_contents,
                        ContactsContract.Data._ID + " = ?",
                        new String[]{contact_id});

                Toast.makeText(view.getContext(),
                        message
                        ,new Integer(1000)).show();

                // refresh manually.
                 finish();
            }
        });

        // Starred Toggle Event
        Star_Top_image.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View view) {
                ContentValues update_contents = new ContentValues();

                String message;

                if(whether_starred.equals("1")) {
                    message = "unStarred";
                    update_contents.put("starred", "0");
                }
                else {
                    message = "Starred";
                    update_contents.put("starred", "1");
                }

                Uri target_uri = ContactsContract.Contacts.CONTENT_URI;

                // need contact ID
                getContentResolver().update(
                        target_uri,
                        update_contents,
                        ContactsContract.Data._ID + " = ?",
                        new String[]{contact_id});

                Toast.makeText(view.getContext(),
                        message
                        ,new Integer(1000)).show();

                // refresh manually.
                 finish();
            }
        });


        // Direction
        direct_call = findViewById(R.id.dial_button);
        direct_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Profile Description","On Click. Text is [" + text_NUMBER.getText().toString().split("\n")[0] + "]");
                if(text_NUMBER.getText().equals("")){
                    // Toast.makeText(getActivity(),"선택 : " + item.getName(), Toast.LENGTH_LONG).show();
                    Log.d("Profile Description","On Direct call click, no Phone number. Send Toast message");
                    Toast.makeText(view.getContext(),
                            "전화번호가 없습니다."
                            ,new Integer(1000)).show();
                }else{
                    Log.d("Profile Description","On Direct call click, Phone number exists. Starting Activity");
                    String primary_contact_number = "tel:" + text_NUMBER.getText().toString().split("\n")[0];
                    startActivity(new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + text_NUMBER.getText().toString().split("\n")[0].replaceAll("[a-b,A-Z, ,-]",""))));
                }
            }
        });

        EditText message_fragment;
        direct_message = findViewById(R.id.message_button);
        direct_message.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(text_NUMBER.getText()==""){
                    // Toast.makeText(getActivity(),"선택 : " + item.getName(), Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),
                            "전화번호가 없습니다."
                            ,new Integer(1000)).show();
                }else{

                    //message_fragment

                    String message = "보낼 내용";
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("smsto:" + text_NUMBER.getText().toString().split("\n")[0]));

                    intent.putExtra("sms_body", message );
                    intent.setType("vnd.android-dir/mms-sms");
                    startActivity(intent);

                    startActivity(new Intent("android.intent.action.MESSAGE",Uri.parse(text_NUMBER.getText().toString().split("\n")[0])));
                    finish();
                }
            }
        });

        direct_email = findViewById(R.id.email_button);
        direct_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(text_ADDRESS.getText().equals("")){
                    Toast.makeText(getApplicationContext(),
                            "이메일 정보가 없습니다."
                            ,new Integer(1000)).show();
                }else{
                    Log.d("email Direction",text_ADDRESS.getText().toString().split("\n")[0]);
                    startActivity(new Intent(Intent.ACTION_SENDTO,  Uri.parse("mailto:" + text_ADDRESS.getText().toString().split("\n")[0])));
                }
            }
        });

        /*
        ContentObserver phone_observer;
        getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, true, phone_observer = new ContentObserver(null) {
            @Override
            public void onChange(boolean self) {
                // refresh manually.
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        */

        /*
        ContentObserver data_observer;
        getContentResolver().registerContentObserver(ContactsContract.Data.CONTENT_URI, true, data_observer = new ContentObserver(null) {
            @Override
            public void onChange(boolean self) {
                // refresh manually.
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });
        */

        /*
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // 여기 position 가지고, 가져올 수 있는 것 같다.
                SingleItem item = (SingleItem) adapter.getItem(position);
                String string_item = item.toString();
                Log.d("to_stringed item",string_item);

                // Toast.makeText(getActivity(),"선택 : " + item.getName(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), profile_description.class);
                Log.d("auaicn","Starting activity succeed");

                if (intent.putExtra("profile_in_detail",string_item) == null)
                    Log.d("auaicn","intent putting extra failed");
                else
                    Log.d("auaicn","intent putting extra succeed");
                startActivity(intent);
                Log.d("auaicn","Starting activity succeed");
            }
        });

        //text_IS_PRIMARY.setOnClickListener(new OnClickLis);
        */
    }

}