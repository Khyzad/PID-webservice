
package com.hida.dao;

import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author lruffin
 */
public class UsedSettingDaoImplTest extends EntityDaoImplTest{

    @Autowired
    UsedSettingDao UsedSettingDao;
    
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().
                getResourceAsStream("UsedSetting.xml"));
        return dataSet; 
    }
    
    @Test
    public void save(){
        Assert.fail("unimplemented");
    } 
    
    @Test
    public void deleteSetting(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void findUsedSettingById(){
        Assert.fail("unimplemented");
    }
    
    @Test
    public void findAllUsedSettings(){
        Assert.fail("unimplemented");
    } 
    
    @Test
    public void findUsedSetting(){
        Assert.fail("unimplemented");
    }
    
  
}
