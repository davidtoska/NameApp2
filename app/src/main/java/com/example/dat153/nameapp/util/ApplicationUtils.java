package com.example.dat153.nameapp.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;

import com.example.dat153.nameapp.Database.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by david on 02.02.2018.
 */

public class ApplicationUtils {

    /**
     * Method that fetches image from internal storage and decodes it to a bitmap
     *
     * @return a bitmap
     */
    /*public static Bitmap decodeImage(Context context, String ImgName){
        Bitmap bitmap;
        String pattern = "\\d*";

        if(ImgName.matches(pattern)){
            // Image is in resources
            int res = Integer.parseInt(ImgName);
            BitmapDrawable temp = (BitmapDrawable) context.getApplicationContext().getResources().getDrawable(res);
            bitmap = temp.getBitmap();
        }
        else{
            // Image is store in internal storage
            bitmap = BitmapFactory.decodeFile(ImgName);
        }
        return bitmap;
    }*/

    public static List<String> getStudentNames(@NonNull List<User> users){
        List<String> result =  new ArrayList<>();
        for (User user: users) {
            result.add(user.getName());
        }
        return result;
    }
}
