package com.hida.dao;

import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the functionality of UsedSettingDaoImpl.
 *
 * @author lruffin
 */
public class UsedSettingDaoImplTest extends EntityDaoImplTest {

    @Autowired
    UsedSettingDao UsedSettingDao;

    /**
     * Returns a data set that Hibernate assumes to be in persistence.
     *
     * @return
     * @throws Exception
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("UsedSetting.xml"));

        return dataSet;
    }

    /**
     * Tests the functionality of UsedSettingDao save.
     */
    @Test
    public void saveTest() {
        UsedSettingDao.save(getSampleUsedSetting());
        Assert.assertEquals(UsedSettingDao.findAllUsedSettings().size(), 2);
    }

    /**
     * Tests to see if UsedSettingDao can find an entity by it's id.
     */
    @Test
    public void findUsedSettingByIdTest() {
        UsedSetting entity = UsedSettingDao.findUsedSettingById(1);
        Assert.assertNotNull(entity);
    }

    /**
     * Tests to see if UsedSettingDao can list and return all the UsedSetting
     * lists.
     */
    @Test
    public void findAllUsedSettingsTest() {
        int size = UsedSettingDao.findAllUsedSettings().size();
        Assert.assertEquals(size, 1);
    }

    /**
     * Attempts to find a UsedSetting by the fields. 
     */
    @Test
    public void findUsedSettingTest() {
        UsedSetting sampleSetting = getSampleUsedSetting();
        UsedSetting entity = UsedSettingDao.findUsedSetting(sampleSetting);
        Assert.assertNotNull(entity);
    }

    /**
     * Returns a sample UsedSetting entity.
     *
     * @return
     */
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
