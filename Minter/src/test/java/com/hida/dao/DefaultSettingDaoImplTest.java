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
    public void save() {
        Assert.fail("unimplemented");
    }

    @Test
    public void deleteSetting() {
        Assert.fail("unimplemented");
    }

    @Test
    public void getDefaultSetting() {
        Assert.fail("unimplemented");
    }

}
