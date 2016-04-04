package com.hida.dao;


import com.hida.model.UsedSetting;
import java.util.List;

/**
 *
 * @author lruffin
 */
public interface UsedSettingDao {
    public void save(UsedSetting setting);    
    
    public UsedSetting findUsedSettingById(int id);
    
    public List<UsedSetting> findAllUsedSettings();    
    public UsedSetting findUsedSetting(UsedSetting setting);
    
    
   
}
