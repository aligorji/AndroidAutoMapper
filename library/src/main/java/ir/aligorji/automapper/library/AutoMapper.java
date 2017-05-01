package ir.aligorji.automapper.library;


import android.support.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class AutoMapper
{

    //source, destination
    private static Map<Class<?>, Map<Class<?>, MapperInfo>> mMapperDictionary = new HashMap<>();

    //=======================================================================================================
    //Config public methods
    //=======================================================================================================

    public static <TSrc, TDes> Converter<TSrc, TDes> createMap(final Class<TSrc> source, final Class<TDes> destination)
    {

        return new Converter<TSrc, TDes>()
        {

            private Action<TSrc, TDes> mThenResolverConverter = null;

            @Override
            public void using(Mapper<TSrc, TDes> mapper)
            {
                addMapper(mapper, false);
            }

            @Override
            public void usingAuto()
            {
                mThenResolverConverter = null;

                addMapper(mAutoMapper, true);
            }
            @Override
            public void usingAuto(Action<TSrc, TDes> action)
            {
                mThenResolverConverter = action;

                addMapper(mAutoMapper, true);
            }

            private void addMapper(Mapper mapper, boolean isAuto)
            {

                Map<Class<?>, MapperInfo> typeConverter = mMapperDictionary.get(source);

                if (typeConverter == null)
                {
                    typeConverter = new HashMap<>();
                    mMapperDictionary.put(source, typeConverter);
                }

                if (!typeConverter.containsKey(destination))
                {
                    typeConverter.put(destination, new MapperInfo(mapper, isAuto));
                }
                else
                {
                    throw new RuntimeException("##### Duplicate mapper for same type!, " + destination.toString());
                }

            }

            Mapper<TSrc, TDes> mAutoMapper = new Mapper<TSrc, TDes>()
            {
                @Override
                public void onMap(@NonNull final TSrc sourceObj, @NonNull final TDes destinationObj)
                {

                    if (mThenResolverConverter != null)
                    {
                        mThenResolverConverter.before(sourceObj, destinationObj);
                    }

                    try
                    {
                        MapperInfo mapperInfo = getMapperInfo(sourceObj.getClass(), destinationObj.getClass());

                        autoMap(sourceObj, destinationObj, mapperInfo);
                    }
                    catch (Throwable e)
                    {
                        throw new RuntimeException("##### AutoMapper [AutoMap reflection], " + e.getMessage());
                    }

                    if (mThenResolverConverter != null)
                    {
                        mThenResolverConverter.after(sourceObj, destinationObj);
                    }

                }


            };


        };
    }

    public static void build()
    {
        for (Class<?> sourceKey : mMapperDictionary.keySet())
        {
            for (Class<?> destinationKey : mMapperDictionary.get(sourceKey).keySet())
            {
                MapperInfo mapperInfo = getMapperInfo(sourceKey, destinationKey);

                if (mapperInfo.mapper == null)
                {
                    throw new RuntimeException("##### Mapper is null for [" + sourceKey + "] to [" + destinationKey + "]");
                }

                if (mapperInfo.isAuto)
                {
                    //auto mapper
                    mapperInfo.fields = createStructureTable(sourceKey, destinationKey);
                }
            }
        }
    }

    public static void clear()
    {
        for (Map<Class<?>, MapperInfo> item : mMapperDictionary.values())
        {
            item.clear();
        }
        mMapperDictionary.clear();
    }

    //=======================================================================================================
    //Public methods
    //=======================================================================================================


    public static <T> List<T> mapList(final List<?> sources, Class<T> destinationType)
    {

        if (sources == null)
        {
            return null;
        }

        if (sources.size() <= 0)
        {
            return new ArrayList<>();
        }

        Mapper converter = null;
        Class<?> sourceType = null;

        List<T> destinationList = new ArrayList<>();

        for (Object item : sources)
        {
            if (item != null)
            {
                if (converter == null || !sourceType.equals(item.getClass()))
                {
                    sourceType = item.getClass();
                    converter = getConverter(sourceType, destinationType);
                }
            }
            destinationList.add((T) mapByType(item, destinationType, converter));
        }

        return destinationList;

    }

    public static <T> T map(Object source, Class<T> destinationType)
    {
        if (source == null)
        {
            return null;
        }

        final T destinationObj;

        try
        {
            destinationObj = destinationType.newInstance();
        }
        catch (Throwable e)
        {
            throw new RuntimeException("##### AutoMapper [newInstance of destination], " + e.getMessage());
        }

        return (T) mapByInstance(source, destinationObj, getConverter(source.getClass(), destinationType));
    }

    public static <T> T map(Object source, Object destination)
    {
        if (source == null)
        {
            return null;
        }

        return (T) mapByInstance(source, destination, getConverter(source.getClass(), destination.getClass()));
    }

    //=======================================================================================================
    //Private methods
    //=======================================================================================================

    private static <T> T mapByInstance(Object source, T destination, Mapper<Object, T> converter)
    {
        if (source == null || converter == null)
        {
            return null;
        }

        converter.onMap(source, destination);

        return destination;
    }

    private static <T> T mapByType(Object source, Class<T> destinationType, Mapper<Object, T> converter)
    {
        if (source == null)
        {
            return null;
        }

        final T destinationObj;

        try
        {
            destinationObj = destinationType.newInstance();
        }
        catch (Throwable e)
        {
            throw new RuntimeException("##### AutoMapper [newInstance of destination], " + e.getMessage());
        }

        return mapByInstance(source, destinationObj, converter);
    }

    private static Mapper getConverter(Class<?> sourceType, Class<?> destinationType)
    {
        try
        {
            MapperInfo mapperInfo = getMapperInfo(sourceType, destinationType);

            if (mapperInfo.mapper == null)
            {
                throw new Throwable();
            }
            return mapperInfo.mapper;
        }
        catch (Throwable th)
        {
            throw new RuntimeException("##### Can not found mapper for [" + sourceType + "] to [" + destinationType + "]");
        }
    }


    private static boolean existMapperInfo(Class<?> sourceType, Class<?> destinationType)
    {
        Map<Class<?>, MapperInfo> x = mMapperDictionary.get(sourceType);
        return x != null && x.containsKey(destinationType);
    }

    private static MapperInfo getMapperInfo(Class<?> sourceType, Class<?> destinationType)
    {
        return mMapperDictionary.get(sourceType).get(destinationType);
    }

    private static void autoMap(final Object sourceObj, final Object destinationObj, MapperInfo mapperInfo) throws IllegalAccessException, InstantiationException
    {
        if (mapperInfo.fields == null)
        {
            throw new RuntimeException("##### Can not found table map structure, please call build() after create map!");
        }

        for (MapperInfo.FieldInfo info : mapperInfo.fields)
        {

            Object sourceValue = info.sourceField.get(sourceObj);

            if (sourceValue == null)
            {
                info.destinationField.set(destinationObj, null);
                continue;
            }

            if (info.isString)
            {
                sourceValue = ((info.prefix != null) ? info.prefix : "") +
                        sourceValue.toString() +
                        ((info.postfix != null) ? info.postfix : "");
            }

            if (info.mapType == MapperInfo.FieldInfo.MapType.PrimitiveType)
            {
                info.destinationField.set(destinationObj, sourceValue);
            }
            else if (info.mapType == MapperInfo.FieldInfo.MapType.ToString)
            {
                info.destinationField.set(destinationObj, sourceValue);
            }
            else
            {
                info.destinationField.set(destinationObj,
                                          map(sourceValue,
                                              info.destinationField.getType()));
            }
        }
    }

    private static List<MapperInfo.FieldInfo> createStructureTable(Class<?> source, Class<?> destination)
    {


        List<MapperInfo.FieldInfo> fieldInfos = new ArrayList<>();

        for (Field destinationField : destination.getFields())
        {

            AutoMapperOption annotation = destinationField.getAnnotation(AutoMapperOption.class);

            String destinationFieldName = null;

            if (annotation != null)
            {
                if (annotation.ignore())
                {
                    continue;
                }

                destinationFieldName = annotation.from();

            }

            if (destinationFieldName == null || destinationFieldName.isEmpty())
            {
                destinationFieldName = destinationField.getName();
            }

            //--------

            Field sourceField;
            try
            {
                sourceField = source.getField(destinationFieldName);
            }
            catch (NoSuchFieldException e)
            {
                continue;
            }

            if (sourceField != null)
            {

                MapperInfo.FieldInfo fieldInfo = new MapperInfo.FieldInfo();
                fieldInfo.sourceField = sourceField;
                fieldInfo.destinationField = destinationField;
                fieldInfo.isString = destinationField.getType().equals(String.class);

                if (annotation != null)
                {
                    fieldInfo.prefix = annotation.prefix();
                    fieldInfo.postfix = annotation.postfix();
                }


                if (existMapperInfo(sourceField.getType(), destinationField.getType()))
                {
                    //duplicate custom user type mapping
                    fieldInfo.mapType = MapperInfo.FieldInfo.MapType.Custom;
                    fieldInfos.add(fieldInfo);
                }
                else if (isPrimitiveType(sourceField.getType()))
                {
                    if (destinationField.getType().equals(sourceField.getType()))
                    {
                        //equals primitive type
                        fieldInfo.mapType = MapperInfo.FieldInfo.MapType.PrimitiveType;
                        fieldInfos.add(fieldInfo);
                    }
                    //else if (canConvertValue(sourceField.getType(), destinationField.getType()))
                    //{
                    //    fieldInfo.mapType = MapperInfo.FieldInfo.MapType.;
                    //    fieldInfos.add(fieldInfo);
                    //}
                    else if (destinationField.getType().equals(String.class))
                    {
                        //to string
                        fieldInfo.mapType = MapperInfo.FieldInfo.MapType.ToString;
                        fieldInfos.add(fieldInfo);
                    }
                    else
                    {
                        throw new RuntimeException("##### Can not map [" + sourceField.getName() + ":" + sourceField.getType() + "] to [" + destinationField.getName() + ":" + destinationField.getType() + "], please; use custom map | change name | change type | ignore");
                    }
                }
                else
                {
                    throw new RuntimeException("##### Can not find mapper for [" + sourceField.getName() + ":" + sourceField.getType() + "] to [" + destinationField.getName() + ":" + destinationField.getType() + "]");
                }


            }
        }

        return fieldInfos;
    }


    //=======================================================================================================
    //Utility
    //=======================================================================================================

    private static final List<String> PRIMITIVE_TYPES = getPrimitiveTypes();

    private static boolean isPrimitiveType(Class<?> clas)
    {
        return PRIMITIVE_TYPES.contains(clas.getName());
    }

    private static List<String> getPrimitiveTypes()
    {

        List<String> ret = new ArrayList<>();

        ret.add(String.class.getName());
        ret.add(Boolean.class.getName());
        ret.add(Character.class.getName());
        ret.add(Byte.class.getName());
        ret.add(Short.class.getName());
        ret.add(Integer.class.getName());
        ret.add(Long.class.getName());
        ret.add(Float.class.getName());
        ret.add(Double.class.getName());
        ret.add(Void.class.getName());
        ret.add(UUID.class.getName());
        ret.add("boolean");
        ret.add("char");
        ret.add("byte");
        ret.add("short");
        ret.add("int");
        ret.add("long");
        ret.add("float");
        ret.add("double");
        ret.add("void");

        return ret;
    }

    private static boolean canConvertValue(Class<?> source, Class<?> destination)
    {
        if (source.getName().equals("void") ||
                source.equals(Void.class) ||
                destination.getName().equals("void") ||
                destination.equals(Void.class))
        {
            return false;
        }

        if (source.equals(String.class))
        {
            return !destination.equals(Character.class) && !destination.getName().equals("char");
        }


        return false;
    }

    //=======================================================================================================
    //Interface
    //=======================================================================================================

    public interface Mapper<TSrc, TDes>
    {

        void onMap(@NonNull final TSrc source, @NonNull final TDes destination);

    }


    public interface Action<TSrc, TDes>
    {

        void before(@NonNull final TSrc source, @NonNull final TDes destination);

        void after(@NonNull final TSrc source, @NonNull final TDes destination);

    }

    public interface Converter<TSrc, TDes>
    {

        void using(Mapper<TSrc, TDes> mapper);

        void usingAuto();

        void usingAuto(Action<TSrc, TDes> action);

    }

    private static class MapperInfo
    {

        public final Mapper mapper;
        public List<FieldInfo> fields;
        public final boolean isAuto;

        public MapperInfo(Mapper mapper, boolean isAuto)
        {
            this.mapper = mapper;
            this.isAuto = isAuto;
            this.fields = null;
        }

        public static class FieldInfo
        {

            public Field sourceField;
            public Field destinationField;
            public MapType mapType;
            public String prefix;
            public String postfix;
            public boolean isString;

            public FieldInfo()
            {
                prefix = null;
                postfix = null;
                isString = false;
            }

            public enum MapType
            {
                PrimitiveType, ToString, Custom
            }

        }

    }

}
