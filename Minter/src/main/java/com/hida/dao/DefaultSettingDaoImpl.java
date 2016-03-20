package com.hida.dao;

import com.hida.model.DefaultSetting;
import org.springframework.stereotype.Repository;

/**
 * missing javadoc
 *
 * @author lruffin
 */
@Repository("defaultSettingDao")
public class DefaultSettingDaoImpl extends AbstractDao<Integer, DefaultSetting>
        implements DefaultSettingDao {

    /**
     * missing javadoc
     *
     * @param setting
     */
    @Override
    public void save(DefaultSetting setting) {
        persist(setting);
    }

    /**
     * missing javadoc
     *
     * @param setting
     */
    @Override
    public void deleteSetting(DefaultSetting setting) {
        this.delete(setting);
    }

    /**
     * missing javadoc
     *
     * @param id
     * @param name
     * @return
     */
    private DefaultSetting findById(int id) {
        return getByKey(id);
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @Override
    public DefaultSetting getDefaultSetting() {
        // An Id of 1 is sought after because they're automatically generated from 1
        return findById(1);
    }

}
