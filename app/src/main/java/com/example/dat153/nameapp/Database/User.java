package com.example.dat153.nameapp.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by mrreitet on 01.02.2018.
 */

@Entity
public class User {

    @NonNull
    @PrimaryKey
    private String name;

    private String imgPath;

    @Ignore
    public User(@NonNull String name, String imgPath) {
        this.name = name;
        this.imgPath = imgPath;
    }

    public User() {
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgName) {
        this.imgPath = imgName;
    }
}
