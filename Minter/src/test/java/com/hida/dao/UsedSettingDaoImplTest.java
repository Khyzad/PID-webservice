package com.hida.dao;

import com.hida.configuration.HsqlDataTypeFactory;
import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import java.sql.Connection;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.operation.DatabaseOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author lruffin
 */
public class UsedSettingDaoImplTest extends EntityDaoImplTest {

    @Autowired
    UsedSettingDao UsedSettingDao;

    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("UsedSetting.xml"));

        return dataSet;
    }

    @Test
    public void saveTest() {        
        UsedSettingDao.save(getSampleUsedSetting());
        Assert.assertEquals(UsedSettingDao.findAllUsedSettings().size(), 3);
    }   

    @Test
    public void findUsedSettingByIdTest() {
        UsedSetting entity = UsedSettingDao.findUsedSettingById(1);
        Assert.assertNotNull(entity);
    }

    @Test
    public void findAllUsedSettingsTest() {
        int size = UsedSettingDao.findAllUsedSettings().size();
        Assert.assertEquals(size, 2);
    }

    @Test
    public void findUsedSetting() {
        Assert.fail("unimplemented");
    }

    private UsedSetting getSampleUsedSetting() {
        UsedSetting setting = new UsedSetting("",
                TokenType.DIGIT,
                "d",
                1,
                true,
                1);

        return setting;
    }

}
