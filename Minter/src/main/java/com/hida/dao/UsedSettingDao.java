package com.hida.dao;

import com.hida.model.Setting;
import com.hida.model.TokenType;
import java.util.List;

/**
 *
 * @author lruffin
 */
public interface SettingDao {
    public void save(Setting setting);    
    public void deleteSetting(Setting setting);
    public List<Setting> findAllSettings();
    public Setting findSettingById(int id);
    public List<Setting> findSetting(String Prefix, TokenType TokenType, String CharMap, int RootLength,
            boolean SansVowels);
}
