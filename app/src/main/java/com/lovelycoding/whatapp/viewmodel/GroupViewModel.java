package com.lovelycoding.whatapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.lovelycoding.whatapp.repository.Repository;

import java.util.List;

public class GroupViewModel extends ViewModel {

    Repository mRepository;

    GroupViewModel() {
        mRepository=Repository.getRepositoryInstance();
    }


    public LiveData<List<String>> getGroupList() {

        return mRepository.getGroupList();
    }

}
