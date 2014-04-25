package org.symqle.modeler.metadata;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;

/**
 * @author lvovich
 */
public class NaturalTypeMapping {

    private static final NaturalTypeMapping instance = new NaturalTypeMapping();

    private NaturalTypeMapping() {
    }

    public static NaturalTypeMapping getInstance() {
        return instance;
    }


    private final String typeVoid = "Void";
    private final String typeLong = "Long";
    private final String typeByteArray = "byte[]";
    private final String typeBoolean = "Boolean";
    private final String typeString = "String";
    private final String typeDate = "Date";
    private final String typeBigDecimal = "BigDecimal";
    private final String typeDouble = "Double";
    private final String typeFloat = "Float";
    private final String typeInteger = "Integer";
    private final String typeShort = "Short";
    private final String typeTime = "Time";
    private final String typeTimestamp = "Timestamp";
    private final String typeByte = "Byte";
    
    private final Map<Integer, String> typeMap = new HashMap<>();
    {
        typeMap.put(Types.ARRAY, typeVoid);
        typeMap.put(Types.BIGINT, typeLong);
        typeMap.put(Types.BINARY, typeByteArray);
        typeMap.put(Types.BIT, typeLong);
        typeMap.put(Types.BLOB, typeByteArray);
        typeMap.put(Types.BOOLEAN, typeBoolean);
        typeMap.put(Types.CHAR, typeString);
        typeMap.put(Types.CLOB, typeString);
        typeMap.put(Types.DATALINK, typeVoid);
        typeMap.put(Types.DATE, typeDate);
        typeMap.put(Types.DECIMAL, typeBigDecimal);
        typeMap.put(Types.DISTINCT, typeVoid);
        typeMap.put(Types.DOUBLE, typeDouble);
        typeMap.put(Types.FLOAT, typeFloat);
        typeMap.put(Types.INTEGER, typeInteger);
        typeMap.put(Types.JAVA_OBJECT, typeVoid);
        typeMap.put(Types.LONGNVARCHAR, typeString);
        typeMap.put(Types.LONGVARBINARY, typeByteArray);
        typeMap.put(Types.LONGVARCHAR, typeString);
        typeMap.put(Types.NCHAR, typeString);
        typeMap.put(Types.NCLOB, typeString);
        typeMap.put(Types.NULL, typeVoid);
        typeMap.put(Types.NUMERIC, typeBigDecimal);
        typeMap.put(Types.NVARCHAR, typeString);
        typeMap.put(Types.OTHER, typeVoid);
        typeMap.put(Types.REAL, typeFloat);
        typeMap.put(Types.REF, typeVoid);
        typeMap.put(Types.ROWID, typeVoid);
        typeMap.put(Types.SMALLINT, typeShort);
        typeMap.put(Types.SQLXML, typeString);
        typeMap.put(Types.STRUCT, typeVoid);
        typeMap.put(Types.TIME, typeTime);
        typeMap.put(Types.TIMESTAMP, typeTimestamp);
        typeMap.put(Types.TINYINT, typeByte);
        typeMap.put(Types.VARBINARY, typeByteArray);
        typeMap.put(Types.VARCHAR, typeString);
    }
    
    private final Map<String, String> importMap = new HashMap<>();
    {
        importMap.put("Time", "java.sql.Time");
        importMap.put("Timestamp", "java.sql.Timestamp");
        importMap.put("Date", "java.sql.Date");
        importMap.put("BigDecimal", "java.math.BigDecimal");
    }
    
    private final Map<String, String> mapperMap = new HashMap<>();
    {
        mapperMap.put(typeVoid, "VOID");
        mapperMap.put(typeLong, "LONG");
        mapperMap.put(typeByteArray, "BYTES");
        mapperMap.put(typeBoolean, "BOOLEAN");
        mapperMap.put(typeString, "STRING");
        mapperMap.put(typeDate, "DATE");
        mapperMap.put(typeBigDecimal, "DECIMAL");
        mapperMap.put(typeDouble, "DOUBLE");
        mapperMap.put(typeFloat, "FLOAT");
        mapperMap.put(typeInteger, "INTEGER");
        mapperMap.put(typeShort, "SHORT");
        mapperMap.put(typeTime, "TIME");
        mapperMap.put(typeTimestamp, "TIMESTAMP");
        mapperMap.put(typeByte, "BYTE");
        
    }



    public String getJavaType(final int databaseType) {
        final String found = typeMap.get(databaseType);
        return found != null ? found : typeVoid;
    }
    
    public String getFqnForImport(final String className) {
        return importMap.get(className);
    }
        
    public String getMapper(final String type) {
        return mapperMap.get(type);
    }
}
