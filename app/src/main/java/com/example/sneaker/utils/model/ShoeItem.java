package com.example.sneaker.utils.model;

import android.os.Parcel;
import android.os.Parcelable;

public class ShoeItem implements Parcelable {

    private final String shoeName;
    private final String shoeBrandName;
    private final int shoeImage;
    private final double shoePrice;

    public ShoeItem(String shoeName, String shoeBrandName, int shoeImage, double shoePrice) {
        this.shoeName = shoeName;
        this.shoeBrandName = shoeBrandName;
        this.shoeImage = shoeImage;
        this.shoePrice = shoePrice;
    }

    protected ShoeItem(Parcel in) {
        shoeName = in.readString();
        shoeBrandName = in.readString();
        shoeImage = in.readInt();
        shoePrice = in.readDouble();
    }

    public static final Creator<ShoeItem> CREATOR = new Creator<ShoeItem>() {
        @Override
        public ShoeItem createFromParcel(Parcel in) {
            return new ShoeItem(in);
        }

        @Override
        public ShoeItem[] newArray(int size) {
            return new ShoeItem[size];
        }
    };

    public String getShoeName() {
        return shoeName;
    }

    public String getShoeBrandName() {
        return shoeBrandName;
    }

    public int getShoeImage() {
        return shoeImage;
    }

    public double getShoePrice() {
        return shoePrice;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(shoeName);
        parcel.writeString(shoeBrandName);
        parcel.writeInt(shoeImage);
        parcel.writeDouble(shoePrice);
    }
}