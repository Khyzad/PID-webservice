
package com.hida.configuration;

import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;
import org.hsqldb.types.Types;

/**
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
