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
public class PidDaoImplTest extends EntityDaoImplTest {

    @Autowired
    PidDao PidDao;

    /**
     *
     * @return @throws Exception
     */
    @Override
    protected IDataSet getDataSet() throws Exception {
        IDataSet dataSet = new FlatXmlDataSet(this.getClass().getClassLoader().getResourceAsStream("Pid.xml"));
        return dataSet;
    }

    /**
     *
     */
    @Test
    public void testFindByName() {
        Assert.fail("unimplemented");
    }

    /**
     * missing javadoc
     *
     */
    @Test
    public void savePid() {
        Assert.fail("unimplemented");
    }

    /**
     * missing javadoc
     *
     */
    @Test
    public void findAllPids() {
        Assert.fail("unimplemented");
    }

}
