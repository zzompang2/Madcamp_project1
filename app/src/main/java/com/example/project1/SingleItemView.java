package com.example.project1;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SingleItemView extends RelativeLayout {

    TextView classifier;
    TextView contact_name;
    TextView contact_item_background;

    public static char last_character;

    public TextView getContact_item_background() {
        return contact_item_background;
    }

    public void setContact_item_background(TextView contact_item_background) {
        this.contact_item_background = contact_item_background;
    }

    public TextView getClassifier() {
        return classifier;
    }

    public void setClassifier(TextView classifier) {
        this.classifier = classifier;
    }

    public TextView getContact_name() {
        return contact_name;
    }

    public void setContact_name(TextView contact_name) {
        this.contact_name = contact_name;
    }

    public static char getLast_character() {
        return last_character;
    }

    public static void setLast_character(char last_character) {
        SingleItemView.last_character = last_character;
    }

    public SingleItemView(Context context) {
        super(context);
        init(context);
    }

    public SingleItemView(Context context, AttributeSet attrs){
        super(context,attrs);
        init(context);
    }

    public void init(Context context){

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.contact_item,this,true);

        // item 이라고 해서, 하나의 뷰를 xml로 정의할 수 있지만,
        // 연락처 탭에서 필요한건 이름만으로 구성하였다. 전화번호는 onclick 후 세부사항에서 확인 할 수 있다.
        // 넣는다면 넣어줄 수는 있슴.
        classifier  = (TextView) findViewById(R.id.contact_item_classifier);
        contact_name = (TextView) findViewById(R.id.contact_item_text);
    }

}
