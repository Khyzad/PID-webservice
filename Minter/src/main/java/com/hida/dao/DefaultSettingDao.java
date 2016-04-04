package com.hida.dao;


import com.hida.model.DefaultSetting;


/**
 *
 * @author lruffin
 */
public interface DefaultSettingDao {
    public void save(DefaultSetting setting);        
    
    public DefaultSetting getDefaultSetting();
    
   
}
