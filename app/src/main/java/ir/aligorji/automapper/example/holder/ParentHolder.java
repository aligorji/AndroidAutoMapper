package ir.aligorji.automapper.example.holder;

import android.graphics.Bitmap;

import ir.aligorji.automapper.AutoMapperOption;


public class ParentHolder
{

    public int id;
    public String name;

    public long fxxxxxxxxx1;
    public int fxxxxxxxxx2;
    public double fxxxxxxxxx3;
    public Void fxxxxxxxxx4;
    public float fxxxxxxxxx5;

    //@AutoMapperOption(from = "name", postfix = " rial")
    public String zzz;

    @AutoMapperOption(ignore = true)
    public Bitmap bitmap;
}
