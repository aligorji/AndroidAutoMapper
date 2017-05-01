package ir.aligorji.automapper.example;

import android.app.Application;
import android.util.Log;

import ir.aligorji.automapper.example.mapper.Mapper;
import ir.aligorji.automapper.example.model.UserModel;


public class App extends Application
{

    @Override
    public void onCreate()
    {
        super.onCreate();
        showLogCat("AAA", "message");
        Mapper.config();



    }

    public static void showLogCat(String tag, String msg)
    {

        Log.i(tag + "", "(" + UserModel.class.getSimpleName() + ".java:1" + ")");

    }

}
