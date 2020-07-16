package com.example.project1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class phoneFragment extends Fragment {

    private ListView listView;
    private phoneFragment.ContactAdapter adapter;
    public String searched_text = "";
    private List<SingleItem> loaded_items;


    public String getSearched_text() {
        return searched_text;
    }

    public void setSearched_text(String searched_text) {
        this.searched_text = searched_text;
    }

    @Override
    public void onResume() {

        // View Group
        super.onResume();
        listView = (ListView) getView().findViewById(R.id.contact_list_view);

        // Contact APP 에 접근해서, 연락처 정보를 쿼리를 통해 LIST 형태로 가져온다.
        loaded_items = SingleItem.getContacts(getActivity());
        // System.out.println(loaded_items.size() + " elements loaded by getContacts()"); // debugging.

        // Custom adapter Object 생성
        adapter = new ContactAdapter();
        Bundle bundle;

        // Custom adapter Object 에 load한 연락처 아이템 하나하나를 추가해준다.
        // addItem() on class "SingleItem" which is sub-class of current class.
        for (int i = 0; i < loaded_items.size(); i++) {
            // System.out.println(loaded_items.get(i));
            adapter.addItem(new SingleItem(
                    loaded_items.get(i).getPHOTO_URI(),
                    loaded_items.get(i).getDISPLAY_NAME(),
                    loaded_items.get(i).getCOMPANY(),
                    loaded_items.get(i).getNUMBER(),
                    loaded_items.get(i).getADDRESS(),
                    loaded_items.get(i).getWEBPAGE(),
                    loaded_items.get(i).getNOTE(),
                    loaded_items.get(i).getSTARRED(),
                    loaded_items.get(i).getCONTACT_ID()));
        }

        // Object 의 추가가 완료되면, adapter 를 설정해준다.
        listView.setAdapter(adapter);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_phone, container, false);


        // SEARCH result filtering implementation
        EditText editText = null;
        editText = (EditText) view.findViewById(R.id.contact_search);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                setSearched_text(charSequence.toString());
                onResume();
            }
        });

        // LIST VIEW

        listView = (ListView) view.findViewById(R.id.contact_list_view);

        // Contact APP 에 접근해서, 연락처 정보를 쿼리를 통해 LIST 형태로 가져온다.
        loaded_items = SingleItem.getContacts(getActivity());
        // System.out.println(loaded_items.size() + " elements loaded by getContacts()"); // debugging.

        // Custom adapter Object 생성
        adapter = new ContactAdapter();
        Bundle bundle;

        // Custom adapter Object 에 load한 연락처 아이템 하나하나를 추가해준다.
        // addItem() on class "SingleItem" which is sub-class of current class.
        for (int i = 0; i < loaded_items.size(); i++) {
            // System.out.println(loaded_items.get(i));
            adapter.addItem(new SingleItem(
                    loaded_items.get(i).getPHOTO_URI(),
                    loaded_items.get(i).getDISPLAY_NAME(),
                    loaded_items.get(i).getCOMPANY(),
                    loaded_items.get(i).getNUMBER(),
                    loaded_items.get(i).getADDRESS(),
                    loaded_items.get(i).getWEBPAGE(),
                    loaded_items.get(i).getNOTE(),
                    loaded_items.get(i).getSTARRED(),
                    loaded_items.get(i).getCONTACT_ID()));
        }

        // Object 의 추가가 완료되면, adapter 를 설정해준다.
        listView.setAdapter(adapter);

        // listView 에는, 각 item 이 눌리는 것을 event 로 처리해줄 수 있는데,
        // Toast 메시지를 출력할 수 있게 설정해준 상태이다.
        // list 자체가 눌리는 이벤트를 처리해주는 함수는 setOnClickListener 이다.
        // 우선순위는 list 자체가 눌리는 이벤트가 우선인 것 같다. 그래서 정의해주지 않았다.
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @SuppressLint("LongLogTag")
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                // 여기 position 가지고, 가져올 수 있는 것 같다.
                SingleItem item = (SingleItem) adapter.getItem(position);
                String string_item = item.toString();
                // Log.d("to_stringed item",string_item);

                // Toast.makeText(getActivity(),"선택 : " + item.getName(), Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getActivity(), profile_description.class);

                // Log.d("auaicn","Starting activity succeed");

                if (intent.putExtra("profile_in_detail",string_item) == null)
                    Log.d("phoneFrag to profileDescription Activity","intent-putting extra failed");
                else
                    Log.d("phoneFrag to profileDescription Activity","intent-putting extra succeed");

                startActivity(intent);
                Log.d("phone Fragment","Starting activity succeed");
            }
        });

        /*
        // Swipe Delete Implementation

        TextView contact_item_text_view = (TextView) getActivity().findViewById(R.id.contact_item_text);

        contact_item_text_view.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeTop() {
                Toast.makeText(getActivity(), "top", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeRight() {
                Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft() {
                Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeBottom() {
                Toast.makeText(getActivity(), "bottom", Toast.LENGTH_SHORT).show();
            }

        });
        */

        return view;
    }

    public class ContactAdapter extends BaseAdapter{
        // mandatory override
        // 1. getCount()
        // 2. getItem()
        // 3. getItemId()
        // 4. getView()

        @Override
        public int getCount() {
            return items.size();
        }

        @Override
        public Object getItem(int position) {
            return items.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        ArrayList<SingleItem> items = new ArrayList<SingleItem>();

        public void addItem(SingleItem item){
            items.add(item);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            SingleItemView view = new SingleItemView(getActivity());

            SingleItem item = items.get(position);
            String profile_name = item.getDISPLAY_NAME();

            Log.d("get View called","get View called");

            if(searched_text.equals("")){
            //if(profile_name.substring(0,searched_text.length()).equals("") == true) {

                // 원래대로 진행해!
                if (item.getSTARRED().equals("1")) {
                    if (position == 0)
                        view.getClassifier().setText("즐겨찾기");
                    else {
                        //view.getClassifier().setTextSize(0);
                        view.getClassifier().setVisibility(View.GONE);
                    }
                } else {
                    if (profile_name.charAt(0) == view.getLast_character()) {
                        //view.getClassifier().setTextSize(0);
                        view.getClassifier().setVisibility(View.GONE);
                    } else {
                        view.getClassifier().setText(profile_name.substring(0, 1));
                    }
                    view.setLast_character(profile_name.charAt(0));
                }
            }else{
                // search 에 어떤 텍스트가 올라오긴 한거거든?
                // 그럼 이제 classifier, contact_item_background는 없애줌.
                //view.getClassifier().setTextSize(0);
                view.getClassifier().setVisibility(View.GONE);
                // 했고, 이제 prefix 가 같은지 확인 해줄것.
                if(profile_name.substring(0,searched_text.length()).equals(searched_text) == false) {
                    // 얘는 표시하면 안됨.
                    //view.getContact_name().setTextSize(0);
                    view.setVisibility(View.GONE);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                    //params.setMargins(0,0,0,0);
                    view.getContact_name().setLayoutParams(params);
                    // 공간도 차지하면 안될것. 줄도. 없어야 할텐데.
                }
            }

            view.getContact_name().setText(profile_name);

            return view;
        }
    }

}