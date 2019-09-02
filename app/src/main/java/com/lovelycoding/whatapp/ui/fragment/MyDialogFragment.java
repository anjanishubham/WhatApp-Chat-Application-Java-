package com.lovelycoding.whatapp.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.permission.RunTimePermission;
import com.lovelycoding.whatapp.ui.activity.MainActivity;
import com.lovelycoding.whatapp.ui.activity.SettingActivity;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class MyDialogFragment extends DialogFragment implements View.OnClickListener {

    public interface onSomeEventListener {
        public void someEvent(String s);
    }

    CircleImageView camera,gallery;
    TextView cancel;
    onSomeEventListener someEventListener;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            someEventListener = (onSomeEventListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement onSomeEventListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.layout_profile_setting_dailog, container, false);
        initView(view);
        cancel.setOnClickListener(this);
        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);

        return view;
    }


    private void initView(View view) {
        camera=view.findViewById(R.id.camera);
        gallery=view.findViewById(R.id.galley);
        cancel=view.findViewById(R.id.cancel);

    }

    @Override
    public void onResume() {

        super.onResume();
        WindowManager.LayoutParams wmlp=getDialog().getWindow().getAttributes();

        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 600);
        window.setGravity(Gravity.BOTTOM);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.cancel:
                dismiss();
                break;
            case R.id.galley:
            {
               // SELECT_OPTION="gallery";
                RunTimePermission.cameraPermission(getActivity());
                someEventListener.someEvent("gallery");

                dismiss();
            }
                break;
            case R.id.camera:
            {
                //SELECT_OPTION="camera";
                RunTimePermission.cameraPermission(getActivity());
                someEventListener.someEvent("camera");

                dismiss();
                break;
            }

        }
    }








}
