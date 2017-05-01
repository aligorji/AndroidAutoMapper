package ir.hfj.automapper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import ir.hfj.automapper.holder.ParentHolder;
import ir.hfj.automapper.holder.UserHolder;
import ir.hfj.automapper.library.AutoMapper;
import ir.hfj.automapper.model.ParentModel;
import ir.hfj.automapper.model.UserModel;


public class MainActivity extends AppCompatActivity
{

    private TextView txtLog;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        UserModel model = new UserModel();
        model.id = 1;
        model.name = "ali";
        model.price = 1000;
        model.parent = new ParentModel();
        model.parent.id = 8;
        model.parent.name = "Gorji";

        UserHolder holder = new UserHolder();
        holder.id = 2;
        holder.extra = "xexexexexexe";

        List<UserModel> mm = new ArrayList<>();
        mm.add(model);
        mm.add(model);

        List<UserHolder> userHolders = AutoMapper.mapList(mm, UserHolder.class);

        UserHolder holder2 = AutoMapper.map(model, holder);

        holder.toString();


        //=========================================================

        txtLog = (TextView) findViewById(R.id.text);


    }

    public void Test(View v)
    {
        String log = "";

        int countTest = 1;

        for (int i = 1; i <= 18; i++)
        {
            //log += "_" + countTest + "\t" + testMapperManual(countTest) + "\n";
            log += "_" + countTest + "\t" + testMapper2(countTest) + "\n";

            countTest *= 2;
        }

        log.toString();
        Log.i("AAA", log);
    }

    private long testMapper2(int countTest)
    {

        AutoMapper.clear();

        AutoMapper.createMap(UserModel.class, UserHolder.class).usingAuto(new AutoMapper.Action<UserModel, UserHolder>()
        {
            @Override
            public void before(@NonNull UserModel source, @NonNull UserHolder destination)
            {

            }
            @Override
            public void after(@NonNull UserModel source, @NonNull UserHolder destination)
            {
                destination.extra = source.id + source.name;
            }
        });

        AutoMapper.createMap(ParentModel.class, ParentHolder.class).usingAuto(new AutoMapper.Action<ParentModel, ParentHolder>()
        {
            @Override
            public void before(@NonNull ParentModel source, @NonNull ParentHolder destination)
            {

            }
            @Override
            public void after(@NonNull ParentModel source, @NonNull ParentHolder destination)
            {
                destination.bitmap = null;
            }
        });

        AutoMapper.build();

        long startTime = Calendar.getInstance().getTimeInMillis();


        for (int i = 0; i < countTest; i++)
        {

            UserHolder holder = AutoMapper.map(createRandomModel(), UserHolder.class);

            holder.toString();

        }

        long endTime = Calendar.getInstance().getTimeInMillis();
        long sumTime = (endTime - startTime);


        txtLog.append("AutoMapper2 : Time(" + countTest + ")" + "  : \t" + sumTime);
        txtLog.append("\n");
        return sumTime;
    }

    private long testMapperManual(int countTest)
    {

        AutoMapper.clear();


        AutoMapper.createMap(UserModel.class, UserHolder.class).using(new AutoMapper.Mapper<UserModel, UserHolder>()
        {

            @Override
            public void onMap(@NonNull UserModel source, @NonNull UserHolder destination)
            {
                destination.id = source.id;
                destination.name = source.name;
                destination.family = source.family;
                destination.gender = source.gender;
                destination.price = source.price + "";
                destination.fxxxxxxxxx1 = source.fxxxxxxxxx1;
                destination.fxxxxxxxxx2 = source.fxxxxxxxxx2;
                destination.fxxxxxxxxx3 = source.fxxxxxxxxx3;
                destination.fxxxxxxxxx4 = source.fxxxxxxxxx4;
                destination.fxxxxxxxxx5 = source.fxxxxxxxxx5;
                destination.parent = AutoMapper.map(source.parent, ParentHolder.class);

                destination.extra = source.id + source.name;
            }
        });

        AutoMapper.createMap(ParentModel.class, ParentHolder.class).using(new AutoMapper.Mapper<ParentModel, ParentHolder>()
        {

            @Override
            public void onMap(@NonNull ParentModel source, @NonNull ParentHolder destination)
            {
                destination.id = source.id;
                destination.name = source.name;

                destination.fxxxxxxxxx1 = source.fxxxxxxxxx1;
                destination.fxxxxxxxxx2 = source.fxxxxxxxxx2;
                destination.fxxxxxxxxx3 = source.fxxxxxxxxx3;
                destination.fxxxxxxxxx4 = source.fxxxxxxxxx4;
                destination.fxxxxxxxxx5 = source.fxxxxxxxxx5;

                destination.bitmap = null;//source.bitmap;
            }
        });


        long startTime = Calendar.getInstance().getTimeInMillis();


        for (int i = 0; i < countTest; i++)
        {

            UserHolder holder = AutoMapper.map(createRandomModel(), UserHolder.class);

            holder.toString();

        }

        long endTime = Calendar.getInstance().getTimeInMillis();
        long sumTime = (endTime - startTime);


        txtLog.append("ManualMapper : Time(" + countTest + ")" + "  : \t" + sumTime);
        txtLog.append("\n");
        return sumTime;
    }

    Random random = new Random();


    private UserModel createRandomModel()
    {
        UserModel model = new UserModel();
        model.id = random.nextInt(11111111);
        model.name = "ali" + random.nextInt(11111111);
        model.family = "ali" + random.nextInt(11111111);
        model.gender = true;
        model.price = random.nextInt(11111111);
        model.fxxxxxxxxx1 = random.nextLong();
        model.fxxxxxxxxx2 = random.nextInt(11111111);
        model.fxxxxxxxxx3 = random.nextDouble();
        model.fxxxxxxxxx4 = null;
        model.fxxxxxxxxx5 = random.nextFloat();


        model.parent = new ParentModel();
        model.parent.id = random.nextInt(11111111);
        ;
        model.parent.name = "a" + random.nextInt(11111111);
        model.parent.fxxxxxxxxx1 = random.nextLong();
        model.parent.fxxxxxxxxx2 = random.nextInt(11111111);
        model.parent.fxxxxxxxxx3 = random.nextDouble();
        model.parent.fxxxxxxxxx4 = null;
        model.parent.fxxxxxxxxx5 = random.nextFloat();
        //model.parent.bitmap = "ddddddd";

        return model;
    }

}
