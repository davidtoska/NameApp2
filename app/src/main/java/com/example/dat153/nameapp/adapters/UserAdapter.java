package com.example.dat153.nameapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dat153.nameapp.Database.User;
import com.example.dat153.nameapp.R;
import com.example.dat153.nameapp.util.ApplicationUtils;

import java.util.List;

/**
 * Created by david on 02.02.2018.
 */

public class UserAdapter extends ArrayAdapter<User> {

    public UserAdapter(@NonNull Context context,
                       @NonNull List<User> objects) {
            super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(this.getContext()).
                    inflate(R.layout.list_student_view, parent, false);
        }

        User thisUser = this.getItem(position);
        TextView userNameTextView = listItemView.findViewById(R.id.student_name);
        ImageView userImageView = listItemView.findViewById(R.id.student_image);
        if(thisUser != null){
            userImageView.setImageBitmap(decodeImage(thisUser.getImgName()));
            userNameTextView.setText(thisUser.getName());
        }

        return listItemView;
    }

    public Bitmap decodeImage(String ImgName){
        Bitmap bitmap;
        String pattern = "\\d*";

        if(ImgName.matches(pattern)){
            // Image is in resources
            int res = Integer.parseInt(ImgName);
            BitmapDrawable temp = (BitmapDrawable) this.getContext().getApplicationContext().getResources().getDrawable(res);
            bitmap = temp.getBitmap();
        }
        else{
            // Image is store in internal storage
            bitmap = BitmapFactory.decodeFile(ImgName);
        }
        return bitmap;
    }

}

