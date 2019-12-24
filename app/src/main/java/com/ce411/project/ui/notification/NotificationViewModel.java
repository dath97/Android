package com.ce411.project.ui.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
public class NotificationViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public NotificationViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Hiện chưa có thông báo");
    }

    public LiveData<String> getText() {
        return mText;
    }
}