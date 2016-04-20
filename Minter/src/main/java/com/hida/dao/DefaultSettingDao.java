package com.hida.dao;

import com.hida.model.DefaultSetting;

/**
 * This class is used to define the possible operations that Hibernate can
 * perform on DefaultSetting objects
 *
 * @author lruffin
 */
public interface DefaultSettingDao {

    public void save(DefaultSetting setting);

    public DefaultSetting getDefaultSetting();

}
