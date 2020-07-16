package com.example.project1;

import android.Manifest;
import android.app.ActivityManager;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.ACTIVITY_SERVICE;

public class photoFragment extends Fragment {

    ImageView selectedView;
    ConstraintLayout fullPhoto;
    Button button;
    GridView gridView;
    // PhotoAdapter adapter;
    // int MY_REQUEST_PERMISSIONS = 1234; // 내 임의로 정한 값

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("hamApp main", "onCreateView");
        Log.d("hamApp", "onCreateView_photo");

        final ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_photo, container, false);

        gridView = (GridView) rootView.findViewById(R.id.gridView);
        fullPhoto = (ConstraintLayout) rootView.findViewById(R.id.fullPhoto);
        selectedView = (ImageView) rootView.findViewById(R.id.selectedView);
        button = (Button) rootView.findViewById(R.id.backButton);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("hamApp", "onItemClick1");
                Glide.with(getActivity()).load(gridView.getAdapter().getItem(i)).into(selectedView);
                fullPhoto.setVisibility(View.VISIBLE);
                gridView.setVisibility(View.INVISIBLE);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fullPhoto.setVisibility(View.INVISIBLE);
                gridView.setVisibility(View.VISIBLE);
            }
        });

        return rootView;
    }

    /*
    private static final String[] PERMISSIONS = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS
    };

    private boolean arePermissionsDenied() {
        for (int i = 0; i < 4; i++) {
            // 두 요청사항 중 하나라도 허용하지 않은 상태일 경우
            if (ContextCompat.checkSelfPermission(getContext(), PERMISSIONS[i]) != PackageManager.PERMISSION_GRANTED)
                return true; }
        // 두 요청사항이 모두 허용된 경우
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    // (requestPermissions 으로) 사용자가 앱 권한 요청에 응답하면
    // 시스템은 앱의 onRequestPermissionsResult() 메서드를 호출하여 사용자 응답을 전달한다.
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("hamApp", "onRequestPermissionsResult");
        if(requestCode == MY_REQUEST_PERMISSIONS && grantResults.length > 0) {
            if(!arePermissionsDenied()) {
                Log.d("hamApp", "permission good");
                // permission이 승인되었다! 유후!
                onResume();
            } else {
                Log.d("hamApp", "permission denied");
                // permission이 거부(denied)...
                ((ActivityManager) getActivity().getSystemService(ACTIVITY_SERVICE)).clearApplicationUserData(); // 계속 묻지 않고 앱종료
                // recreate();
            }
        }
    }*/

    private List<String> filesList;

    @Override
    public void onResume() {
        super.onResume();
        Log.d("hamApp", "onResume_photo");
        // 기기의 SDK version 체크 (Build.VERSION_CODES.M : Marshmallow)
        // [1] permission이 거부(denied)되어 있는 경우
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ((MainActivity)getActivity()).arePermissionsDenied()){
            requestPermissions(((MainActivity)getActivity()).PERMISSIONS, ((MainActivity)getActivity()).MY_REQUEST_PERMISSIONS);
            return;
        }

        // [2] permission이 허용되어 있는 경우
        // app을 initialize 한다.
        filesList = new ArrayList<>();
        addImagesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)));

        //LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //View view = inflater.inflate(R.layout.fragment_photo, null);

        GalleryAdapter galleryAdapter = new GalleryAdapter();

        galleryAdapter.setData(filesList);
        gridView.setAdapter(galleryAdapter);
    }

    private void addImagesFrom(String dirPath){
        final File imagesDir = new File(dirPath);
        final File[] files = imagesDir.listFiles();

        for (File file : files) {
            final String path = file.getAbsolutePath();
            if (path.endsWith(".jpg") || path.endsWith(".png")) {
                filesList.add(path);
                //Log.d("hamApp", path);
            }
        }
    }

    final class GalleryAdapter extends BaseAdapter {

        private List<String> data = new ArrayList<>();

        void setData(List<String> data){
            //Log.d("hamApp", "setData size:");
            if(this.data.size() > 0){
                this.data.clear();
                //Log.d("hamApp setData", data.get(1));
            }
            this.data.addAll(data);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int i) {
            Log.d("hamApp", "getItem");
            return data.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            //Log.d("hamApp", "getView");
            final ImageView imageView;
            if(view == null) {
                imageView = (ImageView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.photoitem, viewGroup, false);
            } else {
                imageView = (ImageView) view;
            }
            Glide.with(getActivity()).load(data.get(i)).centerCrop().into(imageView);

            // 화면 크기에 맞게 이미지를 출력하기 위해 디스플레이의 사이즈를 구함
            Display display = getActivity().getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            // 구한 사이즈를 view에 적용
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(size.x/3, size.x/3);
            imageView.setLayoutParams(params);

            return imageView;
        }
    }
}


