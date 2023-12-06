package com.example.sneaker.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.sneaker.dao.CartDAO;
import com.example.sneaker.database.CartDatabase;
import com.example.sneaker.utils.model.ShoeCart;

import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CartRepo {

    private final CartDAO cartDAO;
    private final LiveData<List<ShoeCart>> allCartItemsLiveData;
    private final Executor executor = Executors.newSingleThreadExecutor();

    public LiveData<List<ShoeCart>> getAllCartItemsLiveData() {
        return allCartItemsLiveData;
    }

    public CartRepo(Application application){
        cartDAO = CartDatabase.getInstance(application).cartDAO();
        allCartItemsLiveData = cartDAO.getAllCartItems();
    }

    public void insertCartItem(ShoeCart shoeCart){
        executor.execute(() -> cartDAO.insertCartItem(shoeCart));
    }

    public void deleteCartItem(ShoeCart shoeCart){
        executor.execute(() -> cartDAO.deleteCartItem(shoeCart));
    }

    public void updateQuantity(int id , int quantity) {
        executor.execute(() -> cartDAO.updateQuantity(id, quantity));
    }

    public void updatePrice(int id , double price){
        executor.execute(() -> cartDAO.updatePrice(id , price));
    }

    public void deleteAllCartItems(){
        executor.execute(cartDAO::deleteAllItems);
    }
}