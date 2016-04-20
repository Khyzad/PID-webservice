package com.hida.configuration;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.hsqldb.types.Types;

/**
 * There is currently a bug with HSQL where booleans created by HSQL cannot be
 * read by Hibernate. This class intercepts the creation of entities and changes
 * the HSQL-accepted booleans to Hibernate-accepted booleans.
 *
 * @author lruffin
 */
public class HsqlDataTypeFactory extends DefaultDataTypeFactory {

    @Override
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if (sqlType == Types.BOOLEAN) {
            return DataType.BOOLEAN;
        }

        return super.createDataType(sqlType, sqlTypeName);
    }
}
