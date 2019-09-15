package com.lovelycoding.whatapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.lovelycoding.whatapp.R;
import com.lovelycoding.whatapp.ui.fileshared.FileShared;

import de.hdodenhof.circleimageview.CircleImageView;

public class FileSharingDialogFragment extends DialogFragment implements View.OnClickListener {


    private CircleImageView btCamera,btGallery,btDocument;
    FileShared mFileShared;
    public FileSharingDialogFragment(FileShared mFileShared) {
        super();
        this.mFileShared=mFileShared;

    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.file_sharing_dialog_fragment,container,false);
        initView(view);
        btCamera.setOnClickListener(this);
        btDocument.setOnClickListener(this);
        btGallery.setOnClickListener(this);
        return view;
    }
    private void initView(View view) {
        btCamera=view.findViewById(R.id.file_shared_camera);
        btDocument=view.findViewById(R.id.file_shared_document);
        btGallery=view.findViewById(R.id.file_shared_galley);
    }

    @Override
    public void onResume() {

        super.onResume();
        WindowManager.LayoutParams wmlp = getDialog().getWindow().getAttributes();

        Window window = getDialog().getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, 600);
        window.setGravity(Gravity.BOTTOM);
    }



    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.file_shared_camera:
                mFileShared.getCamera();
                break;
            case R.id.file_shared_galley:
                mFileShared.getGallery();
                break;
            case R.id.file_shared_document:
                mFileShared.getDocument();
                break;

        }
    }
}
