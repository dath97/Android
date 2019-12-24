package com.ce411.project.ui.information;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class InformationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public InformationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Thông tin cá nhân");
    }

    public LiveData<String> getText() {
        return mText;
    }
}