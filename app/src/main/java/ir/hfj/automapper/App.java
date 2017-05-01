package ir.hfj.automapper;

import android.app.Application;
import android.util.Log;

import ir.hfj.automapper.mapper.Mapper;
import ir.hfj.automapper.model.UserModel;


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
