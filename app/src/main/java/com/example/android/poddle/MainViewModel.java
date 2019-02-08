package com.example.android.poddle;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.poddle.data.AppDatabase;
import com.example.android.poddle.data.FavouritePodcasts;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private LiveData<List<FavouritePodcasts>> podcast;

    public MainViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(this.getApplication());
        podcast = db.favouriteDao().loadFavourites();
    }

    public LiveData<List<FavouritePodcasts>> getPodcast() {
        return podcast;
    }
}
