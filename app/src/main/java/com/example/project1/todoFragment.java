package com.example.project1;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

public class todoFragment extends Fragment {

    RecyclerView todoView;
    RecyclerView.LayoutManager layoutManager;

    TodoAdapter adapter;

    SQLiteDatabase todoDB;

    TextView totalTodo;
    EditText editTextTitle;
    ImageButton addButton;
    Button dateButton;

    DatePickerDialog dialog;

    InputMethodManager imm;

    CheckBox orderByAdd;
    CheckBox orderByName;
    CheckBox orderByDate;
    CheckBox orderByEmotion;
    int checkedBox;

    String[] arrangeStandard = {"BIRTH", "TITLE", "DATE", "EMOTION"};

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("hamApp todoFragment", "onCreateView");

        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_todo, container, false);

        todoView = (RecyclerView) rootView.findViewById(R.id.todoView);
        // RecyclerView 에 LinearLayoutManager 를 연결
        layoutManager = new LinearLayoutManager(getContext()); // (참고) gridLayout도 있다!
        todoView.setLayoutManager(layoutManager);
        // 내가 만든 RecyclerView.Adapter 인 TodoAdapter 만들고
        // RecyclerView 에 연결
        adapter = new TodoAdapter(getContext(), todoDB);
        todoView.setAdapter(adapter);

        // 오늘 날짜 가져오기
        Calendar calendar = new GregorianCalendar();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HHmmss");
        final String curDateAndTime = sdf.format(calendar.getTime());
        final int curYear = Integer.parseInt(curDateAndTime.substring(0,4));
        final int curMonth = Integer.parseInt(curDateAndTime.substring(5,7));
        final int curDay = Integer.parseInt(curDateAndTime.substring(8,10));
        Log.d("hamApp", curDateAndTime + curYear + curMonth + curDay);

        // 오늘 날짜로 세팅된 DatePickerDialog를 만들고
        // dateButton 에도 초기화해 놓는다.
        // (중요) calendar.get(Calendar.MONTH);로 하면 month는 1~12가 아닌 0~11로 되어있다.
        dialog = new DatePickerDialog(Objects.requireNonNull(getContext()), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                // 캘린더에서 날짜 선택하면 button 값 변경
                dateButton.setText(i + "/" + (i1+1) + "/" + i2);
            }
        }, curYear, curMonth-1, curDay);

        // 객체들 초기값 설정
        totalTodo = rootView.findViewById(R.id.totalTodo);
        totalTodo.setText(numberOfItem() + "개");
        editTextTitle = rootView.findViewById(R.id.editTextTitle);
        addButton = rootView.findViewById(R.id.addButton);
        dateButton = rootView.findViewById(R.id.dateButton);
        dateButton.setText(curYear+ "/" + curMonth + "/" + curDay);

        // dateButton 클릭 -> 캘린더 show
        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
                // 다른 theme의 dialog 띄우는 방법!
                //DialogFragment dialogFragment = new DatePickerDialogTheme();
                //dialogFragment.show(getFragmentManager(), "Theme");
            }
        });

        // 입력란 반짝거리는 애니메이션
        // 입력란이 비어있으면 애니메이션 시작!
        final Animation blinkAnim = AnimationUtils.loadAnimation(getContext(), R.anim.blink);
        editTextTitle.startAnimation(blinkAnim);

        editTextTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editTextTitle.getText().length() == 0) {
                    editTextTitle.startAnimation(blinkAnim);
                } else {
                    editTextTitle.clearAnimation();
                }
            }
        });

        // 할일 추가할 때 키보드 내려가게 하기 위해 InputMethodManager 사용
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        // addButton 클릭하여 새로운 할일 추가
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = editTextTitle.getText().toString();
                String date = dateButton.getText().toString();
                // 입력란에 공백만 있는 경우
                if(title.replace(" ", "").equals("")) {
                    Toast.makeText(getContext(), "할일이 비어있어요.", Toast.LENGTH_LONG).show();
                    return;
                }

                String sqlQuery = "SELECT * FROM CONTACT WHERE TITLE = " + "'"+title+"'";
                Cursor cursor = todoDB.rawQuery(sqlQuery, null);
                if(cursor.getCount() > 0) {
                    Toast.makeText(getContext(), "다른 일과 중복되네요.", Toast.LENGTH_LONG).show();
                    return;
                }

                // 성공적으로 할일이 등록되며 회전하는 애니메이션 발생
                addButton.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.rotation));
                // 입력된 정보들로 TodoItem 만들고 datebase 에 insert
                int num = numberOfItem();
                TodoItem newItem = new TodoItem(num, title, date, 0, curDateAndTime);
                Log.d("hamApp", curDateAndTime);
                insertItem(num, newItem);
                // ListView 갱신
                adapter.notifyDataSetChanged();
                // 총 개수를 보여주는 textView 업데이트
                totalTodo.setText((num+1) + "개");
                // 입력란 비우기
                editTextTitle.setText("");
                // 키보드 내리기
                imm.hideSoftInputFromWindow(editTextTitle.getWindowToken(), 0);
                // 재배열 하기! rearrange
                if(checkedBox != -1) RearrangeDB(arrangeStandard[checkedBox]);
            }
        });

        // sorting 을 위한 checkbox 들 정의.
        // 하나만 체크되어 있을 수 있으며,
        // checkBox 는 현재 체크되어 있는 박스의 인덱스
        orderByAdd = rootView.findViewById(R.id.orderByAdd);
        orderByName = rootView.findViewById(R.id.orderByName);
        orderByDate = rootView.findViewById(R.id.orderByDate);
        orderByEmotion = rootView.findViewById(R.id.orderByEmotion);
        checkedBox = -1;

        orderByAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedBox == 0) return;
                switch(checkedBox) {
                    case 1: orderByName.setChecked(false); break;
                    case 2: orderByDate.setChecked(false); break;
                    case 3: orderByEmotion.setChecked(false); break;
                }
                checkedBox = 0;
                RearrangeDB("BIRTH");
            }
        });
        orderByName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedBox == 1) return;
                switch(checkedBox) {
                    case 0: orderByAdd.setChecked(false); break;
                    case 2: orderByDate.setChecked(false); break;
                    case 3: orderByEmotion.setChecked(false); break;
                }
                checkedBox = 1;
                RearrangeDB("TITLE");
            }
        });

        orderByDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedBox == 2) return;
                switch(checkedBox) {
                    case 0: orderByAdd.setChecked(false); break;
                    case 1: orderByName.setChecked(false); break;
                    case 3: orderByEmotion.setChecked(false); break;
                }
                checkedBox = 2;
                RearrangeDB("DATE");
            }
        });

        orderByEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkedBox == 3) return;
                switch(checkedBox) {
                    case 0: orderByAdd.setChecked(false); break;
                    case 1: orderByName.setChecked(false); break;
                    case 2: orderByDate.setChecked(false); break;
                }
                checkedBox = 3;
                RearrangeDB("EMOTION");
            }
        });

        // DB 모두 초기화
        //String sqlDeleteAll = "DELETE FROM CONTACT";
        //todoDB.execSQL(sqlDeleteAll);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        totalTodo.setText(numberOfItem() + "개");
    }

    public void RearrangeDB(String standard) {
        Log.d("hamApp rearrange",standard);
        String sqlCommand = "SELECT * FROM CONTACT ORDER BY " + standard + ", NUM ASC";
        Cursor cursor = todoDB.rawQuery(sqlCommand, null);
        if(cursor != null && cursor.getCount() != 0) {
            int count = 0;
            String title;
            while(cursor.moveToNext()) {
                title = cursor.getString(1);
                sqlCommand = "UPDATE CONTACT SET NUM = " + count + " WHERE TITLE = " + "'"+title+"'";
                //Log.d("hamApp", sqlCommand);
                todoDB.execSQL(sqlCommand);
                count++;
            }
            adapter.notifyDataSetChanged();
        }
    }

    public void insertItem(int num, TodoItem newItem) {
        //Log.d("hamApp todoFragment", "insertItem");
        if(todoDB == null) return;

        String sqlInsert = "INSERT INTO CONTACT " +
                "(NUM, TITLE, DATE, EMOTION, BIRTH) VALUES (" +
                Integer.toString(num) + ", " +
                "'" + newItem.getTitle() + "', " +
                "'" + newItem.getDate() + "', " +
                "0," +
                "'" + newItem.getBirth() + "'" + ")";

        //Log.d("hamApp todoFragment", sqlInsert);
        todoDB.execSQL(sqlInsert);

    }

    public int numberOfItem() {
        Log.d("hamApp todoFragment", "numberOfItem");
        if(todoDB == null) return 0;

        String sqlQueryTbl = "SELECT * FROM CONTACT";
        Cursor cursor = todoDB.rawQuery(sqlQueryTbl, null);

        return cursor.getCount();
    }

    /*
    public static class DatePickerDialogTheme extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
            dateButton.setText(i + "/" + (i1+1) + "/" + i2);
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
            Calendar calendar = new GregorianCalendar();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datepickerdialog = new DatePickerDialog(getActivity(),
                    AlertDialog.THEME_DEVICE_DEFAULT_DARK,this,year,month,day);

            return datepickerdialog;
        }
    }*/
}