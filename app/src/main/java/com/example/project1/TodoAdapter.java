package com.example.project1;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


final class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.TodoViewHolder> {

    private SQLiteDatabase todoDB;
    Context context;

    // ViewHolder : 각 view를 보관하는 holder 객체.
    public class TodoViewHolder extends RecyclerView.ViewHolder {

        protected TextView num;         // 총 할일 개수
        protected TextView title;       // 할일
        protected TextView date;        // 날짜
        protected ImageButton emotion;  // 체크박스
        protected TextView birth;       // 생성일
        LinearLayout textField;         // 체크박스를 제외한 공간. 클릭시 디테일 창으로 이동한다.
                                        // 체크박스의 이미지가 순서대로 저장되어 있다.
        int[] emotions = {R.drawable.ic_emotion_0_no,
                R.drawable.ic_emotion_1_smile,
                R.drawable.ic_emotion_2_angry,
                R.drawable.ic_emotion_3_sad};
        Animation rotation = AnimationUtils.loadAnimation(context, R.anim.bounce);

        public TodoViewHolder(@NonNull View itemView) {
            super(itemView);

            this.num = (TextView) itemView.findViewById(R.id.num);
            this.title = (TextView) itemView.findViewById(R.id.title);
            this.date = (TextView) itemView.findViewById(R.id.date);
            this.emotion = (ImageButton) itemView.findViewById(R.id.emotionButton);
            this.birth = (TextView) itemView.findViewById(R.id.birth);

            // 체크박스 클릭시 순서대로 이미지 변경
            emotion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();                    // 클릭된 아이템의 index
                    changeEmotion(position);

                    emotion.startAnimation(rotation);
                    emotion.setImageResource(emotions[getEmotion(position)]);
                }
            });

            textField = itemView.findViewById(R.id.textField);
            textField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TodoDetailActivity.class);
                    intent.putExtra("num", num.getText());
                    intent.putExtra("title", title.getText());
                    intent.putExtra("date", date.getText());
                    intent.putExtra("todoDB", String.valueOf(todoDB));

                    context.startActivity(intent);
                    ((MainActivity)context).overridePendingTransition(R.anim.sliding_up, R.anim.stay);
                }
            });
        }
    }

    public TodoAdapter(Context context, SQLiteDatabase DB) {
        this.context = context;
        this.todoDB = DB;
    }

    @NonNull
    @Override
    // 새로운 View를 생성할 때 실행되어 ViewHolder를 반환
    public TodoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // convertView가 null 일 때 inflate 하고 ViewHolder를 생성
        // -> 각 요소를 findViewById를 통해 저장
        // -> 그리고 앞으로는 ViewHolder를 getTag로 불러와서 재사용한다. 즉 find~를 다시 안 해도 된다.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.todoitem, parent, false);
        TodoViewHolder viewHolder = new TodoViewHolder(view);

        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    // ViewHolder의 내용을 변경
    public void onBindViewHolder(@NonNull TodoViewHolder holder, int position) {

        if(position < 10) holder.num.setText("0" + position);
        else holder.num.setText(Integer.toString(position));
        holder.title.setText(getTitle(position));
        holder.date.setText(getDate(position));
        holder.birth.setText(getBirth(position));
        switch(getEmotion(position)) {
            case 0:
                holder.emotion.setImageResource(R.drawable.ic_emotion_0_no); break;
            case 1:
                holder.emotion.setImageResource(R.drawable.ic_emotion_1_smile); break;
            case 2:
                holder.emotion.setImageResource(R.drawable.ic_emotion_2_angry); break;
            case 3:
                holder.emotion.setImageResource(R.drawable.ic_emotion_3_sad); break;
        }
    }

    @Override
    public int getItemCount() {
        //Log.d("hamApp TodoAdapter", "getItemCount");
        if(todoDB == null) return 0;

        String sqlQueryTbl = "SELECT * FROM CONTACT";
        Cursor cursor = todoDB.rawQuery(sqlQueryTbl, null);
        //Log.d("hamApp TodoAdapter", Integer.toString(cursor.getCount()));

        return cursor.getCount();
    }

    String getTitle(int position) {
        if(todoDB == null) return null;

        String sqlInsert = "SELECT * FROM CONTACT " +
                "WHERE NUM = " +
                Integer.toString(position);
        Cursor cursor = todoDB.rawQuery(sqlInsert, null);
        cursor.moveToNext();
        //Log.d("hamApp TodoAdapter", sqlInsert);

        return cursor.getString(1);
    }

    String getDate(int position) {
        //Log.d("hamApp TodoAdapter", "getDate");
        if(todoDB == null) return null;

        String sqlInsert = "SELECT * FROM CONTACT " +
                "WHERE NUM = " + Integer.toString(position);
        Cursor cursor = todoDB.rawQuery(sqlInsert, null);
        cursor.moveToNext();

        return cursor.getString(2);
    }

    int getEmotion(int position) {
        Log.d("hamApp TodoAdapter", "getEmotion:");

        String sqlInsert = "SELECT * FROM CONTACT " +
                "WHERE NUM = " +
                Integer.toString(position);

        Cursor cursor = todoDB.rawQuery(sqlInsert, null);
        cursor.moveToNext();

        Log.d("hamApp TodoAdapter", Integer.toString(cursor.getInt(3)));
        return cursor.getInt(3);
    }

    String getBirth(int position) {
        if(todoDB == null) return null;
        String sqlQuery = "SELECT * FROM CONTACT " +
                "WHERE NUM = " +
                Integer.toString(position);

        Cursor cursor = todoDB.rawQuery(sqlQuery, null);
        cursor.moveToNext();

        return cursor.getString(4);
    }

    void changeEmotion(int position) {
        String sqlInsert = "UPDATE CONTACT " +
                "SET EMOTION = (EMOTION+1) % 4 " +
                "WHERE NUM = " +
                Integer.toString(position);

        Log.d("hamApp TodoAdapter", "changeEmotion: "+sqlInsert);
        todoDB.execSQL(sqlInsert);
    }
}