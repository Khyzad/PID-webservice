package com.hida.dao;

import com.hida.model.DefaultSetting;
import com.hida.model.TokenType;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author lruffin
 */
public class DefaultSettingDaoImplTest extends EntityDaoImplTest {

    @Autowired
    DefaultSettingDao DefaultSettingDao;

    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("DefaultSetting.xml"));
        return dataSet;
    }

    @Test
    public void saveTest() {
        DefaultSettingDao.save(getSampleDefaultSetting());
        DefaultSetting entity = DefaultSettingDao.getDefaultSetting();
        Assert.assertNotNull(entity);
    }
    
    @Test
    public void getDefaultSettingTest() {
        DefaultSetting entity = DefaultSettingDao.getDefaultSetting();
        Assert.assertNotNull(entity);
    }

    

    private DefaultSetting getSampleDefaultSetting() {
        DefaultSetting setting = new DefaultSetting("",
                "",
                TokenType.DIGIT,
                "d",
                1,
                true,
                true,
                true);

        return setting;
    }

}
