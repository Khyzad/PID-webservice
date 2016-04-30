package com.hida.dao;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.DefaultSetting;
import com.hida.model.TokenType;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the functionality of DefaultSettingDaoImpl.
 *
 * @author lruffin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepositoryConfiguration.class})
public class DefaultSettingDaoImplTest extends EntityDaoImplTest {
       
    //@Autowired
    private DefaultSettingRepository DefaultSettingRepo;
    
    @Autowired
    public void setDefaultSettingRepository(DefaultSettingRepository DefaultSettingRepo) {
        this.DefaultSettingRepo = DefaultSettingRepo;
    }

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
        DefaultSettingRepo.save(getSampleDefaultSetting());
        DefaultSetting entity = DefaultSettingRepo.findCurrentDefaultSetting();
        Assert.assertNotNull(entity);
    }

    /**
     * Tests to see if DefaultSettingDao can find an entity with an id of 1.
     */
    @Test
    public void getDefaultSettingTest() {
        DefaultSetting entity = DefaultSettingRepo.findCurrentDefaultSetting();
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
