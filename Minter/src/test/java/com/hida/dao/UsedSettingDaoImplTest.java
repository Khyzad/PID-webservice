package com.hida.dao;

import com.hida.configuration.RepositoryConfiguration;
import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import java.util.Iterator;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tests the functionality of UsedSettingDaoImpl.
 *
 * @author lruffin
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {RepositoryConfiguration.class})
public class UsedSettingDaoImplTest extends EntityDaoImplTest {
    
    //@Autowired
    private UsedSettingRepository UsedSettingRepo;
    
    @Autowired
    public void setUsedSettingRepository(UsedSettingRepository UsedSettingRepository) {
        this.UsedSettingRepo = UsedSettingRepository;
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
                getResourceAsStream("UsedSetting.xml"));

        return dataSet;
    }

    /**
     * Tests the functionality of UsedSettingDao save.
     */
    @Test
    public void saveTest() {
        final UsedSetting setting = getSampleUsedSetting();
        setting.setId(1);
        
        UsedSettingRepo.save(getSampleUsedSetting());
        UsedSetting entity = UsedSettingRepo.findOne(setting.getId());
        Assert.assertNotNull(entity);
    }

    /**
     * Tests to see if UsedSettingDao can find an entity by it's id.
     */
    @Test
    public void findUsedSettingByIdTest() {
        UsedSetting entity = UsedSettingRepo.findOne(1);
        Assert.assertNotNull(entity);
    }

    /**
     * Tests to see if UsedSettingDao can list and return all the UsedSetting
     * lists.
     */
    @Test
    public void findAllUsedSettingsTest() {
        Iterator<UsedSetting> iterator = UsedSettingRepo.findAll().iterator();
        
        int size = 0;
        while(iterator.hasNext()){
            iterator.next();
            size++;
        }
        
        Assert.assertEquals(size, 1);
    }

    /**
     * Attempts to find a UsedSetting by the fields. 
     */
    @Test
    public void findUsedSettingTest() {
        UsedSetting sampleSetting = getSampleUsedSetting();        
        UsedSetting entity = UsedSettingRepo.findUsedSetting(sampleSetting.getPrefix(),
                sampleSetting.getTokenType(),
                sampleSetting.getCharMap(),
                sampleSetting.getRootLength(),
                sampleSetting.isSansVowels());
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
