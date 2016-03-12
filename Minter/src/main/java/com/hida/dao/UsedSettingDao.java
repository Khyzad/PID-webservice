package com.hida.dao;


import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import java.util.List;

/**
 *
 * @author lruffin
 */
public interface UsedSettingDao {
    public void save(UsedSetting setting);    
    public void deleteSetting(UsedSetting setting);
    
    public UsedSetting findUsedSettingById(int id);
    
    public List<UsedSetting> findAllUsedSettings();    
    public List<UsedSetting> findUsedSetting(String Prefix, TokenType TokenType, String CharMap,
            int RootLength, boolean SansVowels);
    
    
   
}
