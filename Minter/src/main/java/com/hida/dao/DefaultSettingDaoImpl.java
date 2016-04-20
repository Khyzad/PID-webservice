package com.hida.dao;

import com.hida.model.DefaultSetting;
import org.springframework.stereotype.Repository;

/**
 * Programmatic implementation DefaultSettingDao
 *
 * @author lruffin
 */
@Repository("defaultSettingDao")
public class DefaultSettingDaoImpl extends AbstractDao<Integer, DefaultSetting>
        implements DefaultSettingDao {

    /**
     * Saves a DefaultSetting object to a database
     *
     * @param setting The DefaultSetting object to be saved
     */
    @Override
    public void save(DefaultSetting setting) {
        persist(setting);
    }

    /**
     * Finds a DefaultSetting object in a database
     *
     * @param id The unique id of the DefaultSetting object
     * @return The DefaultSetting with the unique id
     */
    private DefaultSetting findById(int id) {
        return getByKey(id);
    }

    /**
     * Returns the DefaultSetting of the Minter
     *
     * @return DefaultSetting
     */
    @Override
    public DefaultSetting getDefaultSetting() {
        // An Id of 1 is sought after because they're automatically generated from 1
        return findById(1);
    }

}
