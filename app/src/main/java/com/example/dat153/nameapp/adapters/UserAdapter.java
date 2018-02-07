package com.example.dat153.nameapp.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dat153.nameapp.Database.User;
import com.example.dat153.nameapp.R;
import com.example.dat153.nameapp.util.ApplicationUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

import static android.graphics.BitmapFactory.decodeFile;

/**
 * Created by internal_david on 02.02.2018.
 */

public class UserAdapter extends ArrayAdapter<User> {

    private final String TAG = "UserAdapter";

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
            userImageView.setImageBitmap(decodeImage(thisUser.getImgPath(), 256));
            userNameTextView.setText(thisUser.getName());
        }

        return listItemView;
    }

    private Bitmap decodeImage(String imgPath, final int THUMBSIZE) {

        Uri uri = Uri.parse(imgPath);

        Bitmap thumbImage = null;
        Drawable temp = null;
        if (imgPath.contains("internal")) {
            try {
                InputStream inputStream = this.getContext().getApplicationContext().getContentResolver().openInputStream(Uri.parse(imgPath));
                temp = BitmapDrawable.createFromStream(inputStream, imgPath);
                Bitmap bitmap = ((BitmapDrawable) temp).getBitmap();
                thumbImage = ThumbnailUtils.extractThumbnail(bitmap, THUMBSIZE, THUMBSIZE);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        } else if (imgPath.contains("tmp")) {
            try {
                thumbImage = ThumbnailUtils.extractThumbnail(decodeFile(imgPath), THUMBSIZE, THUMBSIZE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return thumbImage;
    }
}

