package com.hida.dao;

import com.hida.model.DefaultSetting;
import com.hida.model.TokenType;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the functionality of DefaultSettingDaoImpl.
 *
 * @author lruffin
 */
public class DefaultSettingDaoImplTest extends EntityDaoImplTest {

    @Autowired
    DefaultSettingDao DefaultSettingDao;

    /**
     * Returns a data set that Hibernate assumes to be in persistence.
     *
     * @return
     * @throws Exception
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("DefaultSetting.xml"));
        return dataSet;
    }

    /**
     * Tests the functionality of DefaultSettingDao save.
     */
    @Test
    public void saveTest() {
        DefaultSettingDao.save(getSampleDefaultSetting());
        DefaultSetting entity = DefaultSettingDao.getDefaultSetting();
        Assert.assertNotNull(entity);
    }

    /**
     * Tests to see if DefaultSettingDao can find an entity with an id of 1.
     */
    @Test
    public void getDefaultSettingTest() {
        DefaultSetting entity = DefaultSettingDao.getDefaultSetting();
        Assert.assertNotNull(entity);
    }

    /**
     * Returns a sample DefaultSetting entity.
     *
     * @return
     */
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
