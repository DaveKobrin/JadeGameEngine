package Utility;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.util.Map;

public class TypeClassMap {
    public enum Types {
        STRING,
        BOOLEAN,

        BYTE,
        SHORT,
        INT,
        LONG,

        FLOAT,
        DOUBLE,

        VECTOR2F,
        VECTOR3F,
        VECTOR4F,

        COLOR,
        COLORS,

        OBJECT;
    }


    private static final Map<Types, Class<?>> enum2class = Map.ofEntries(
            Map.entry(Types.STRING   , String.class),
            Map.entry(Types.BOOLEAN  , boolean.class),
            Map.entry(Types.BYTE     , Byte.class),
            Map.entry(Types.SHORT    , Short.class),
            Map.entry(Types.INT      , int.class),
            Map.entry(Types.LONG     , long.class),
            Map.entry(Types.FLOAT    , float.class),
            Map.entry(Types.DOUBLE   , double.class),
            Map.entry(Types.VECTOR2F , Vector2f.class),
            Map.entry(Types.VECTOR3F , Vector3f.class),
            Map.entry(Types.VECTOR4F , Vector4f.class),
            Map.entry(Types.COLOR    , Color.class),
            Map.entry(Types.COLORS   , Color.COLORS.class),
            Map.entry(Types.OBJECT   , Object.class)
    );


    public static Class<?> enumTypeToClass(Types type) {
        Class<?> result = (enum2class.getOrDefault(type, Object.class));
        return result;
    }

    public static Types classToEnumType(Class<?> jclass) {
        if (enum2class.containsValue(jclass)) {
            for (Map.Entry entry : enum2class.entrySet()) {
                if (jclass.equals(entry.getValue()))
                    return (Types) entry.getKey();
            }
        }
        return Types.OBJECT;
    }
}