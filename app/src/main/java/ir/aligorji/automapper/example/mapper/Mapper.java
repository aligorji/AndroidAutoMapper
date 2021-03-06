package ir.aligorji.automapper.example.mapper;

import android.support.annotation.NonNull;

import ir.aligorji.automapper.example.holder.ParentHolder;
import ir.aligorji.automapper.example.holder.UserHolder;
import ir.aligorji.automapper.AutoMapper;
import ir.aligorji.automapper.example.model.ParentModel;
import ir.aligorji.automapper.example.model.UserModel;


public final class Mapper
{

    public static void config()
    {

        AutoMapper.createMap(UserModel.class, UserHolder.class).usingAuto();

        AutoMapper.createMap(ParentModel.class, ParentHolder.class).usingAuto(new AutoMapper.Action<ParentModel, ParentHolder>()
        {
            @Override
            public void before(@NonNull ParentModel source, @NonNull ParentHolder destination)
            {

            }
            @Override
            public void after(@NonNull ParentModel source, @NonNull ParentHolder destination)
            {

            }
        });

        AutoMapper.build();

        /*AutoMapper.createMap(ParentModel.class, ParentHolder.class).using(new AutoMapper.Mapper<ParentModel, ParentHolder>()
        {
            @Override
            public void onMap(@NonNull ParentModel source, @NonNull ParentHolder destination)
            {
                destination.id = source.id;
            }
        });*/


    }

}
