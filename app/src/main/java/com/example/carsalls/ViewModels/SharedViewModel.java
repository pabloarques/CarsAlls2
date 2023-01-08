package com.example.carsalls.ViewModels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;

public class SharedViewModel extends AndroidViewModel {

    private MutableLiveData<FirebaseUser> user;
    private final Application app;

    public SharedViewModel(@NonNull Application application) {
        super(application);
        this.app = application;

        user = new MutableLiveData<>();
    }

    public MutableLiveData<FirebaseUser> getUser() {
        return user;
    }

    public void setUser(FirebaseUser passedUser){
        user.postValue(passedUser);
    }
}
