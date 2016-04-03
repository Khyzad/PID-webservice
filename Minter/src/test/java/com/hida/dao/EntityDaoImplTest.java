package com.hida.dao;

import javax.sql.DataSource;
 
import org.dbunit.database.DatabaseDataSourceConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
 
import com.hida.configuration.HibernateTestConfiguration;
import com.hida.configuration.HsqlDataTypeFactory;
import org.dbunit.database.DatabaseConfig;
 
 
/**
 * 
 * @author lruffin
 */
@ContextConfiguration(classes = { HibernateTestConfiguration.class })
public abstract class EntityDaoImplTest extends AbstractTransactionalTestNGSpringContextTests {
 
    @Autowired
    DataSource dataSource;
 
    @BeforeMethod
    public void setUp() throws Exception {
        IDatabaseConnection dbConn = new DatabaseDataSourceConnection(dataSource);
        
        DatabaseConfig config = dbConn.getConfig();
	config.setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new HsqlDataTypeFactory());
        
        DatabaseOperation.CLEAN_INSERT.execute(dbConn, getDataSet());
    }
     
    protected abstract IDataSet getDataSet() throws Exception;
 
}
