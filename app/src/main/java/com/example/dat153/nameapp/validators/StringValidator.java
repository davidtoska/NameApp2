package com.example.dat153.nameapp.validators;


/**
 * Created by cecilie on 02.02.2018.
 */

public class StringValidator {

    public static boolean validName(String name){
        if(name.length() > 2 && name.matches("^[a-zA-Z_ æøåÆØÅ]+$")){
            return true;
        }
        return false;
    }
}
