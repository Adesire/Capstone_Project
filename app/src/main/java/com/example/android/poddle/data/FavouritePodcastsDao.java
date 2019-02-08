package com.example.android.poddle.data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface FavouritePodcastsDao {
    @Query("SELECT * FROM favourites_table ORDER BY id")
    LiveData<List<FavouritePodcasts>> loadFavourites();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertFav(FavouritePodcasts podcast);

    @Delete
    void deletePodcastEntry(FavouritePodcasts podcast);

    @Query("DELETE FROM favourites_table WHERE pid = :pid")
    void deletePodcast(String pid);
}
