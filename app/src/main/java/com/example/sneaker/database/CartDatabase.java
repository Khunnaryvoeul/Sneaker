package com.example.sneaker.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.sneaker.dao.CartDAO;
import com.example.sneaker.utils.model.ShoeCart;

@Database(entities = {ShoeCart.class}, version = 1, exportSchema = false)
public abstract class CartDatabase extends RoomDatabase {

    public abstract CartDAO cartDAO();
    private static CartDatabase instance;

    public static synchronized  CartDatabase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext()
                            , CartDatabase.class , "ShoeDatabase")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}