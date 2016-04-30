package com.hida.dao;

import com.hida.model.UsedSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author lruffin
 */
public interface UsedSettingRepository extends CrudRepository<UsedSetting, Integer>{
    
    @Query("select s from USED_SETTING where s.Pid_Prefix = :setting.getPrefix() and "
            + "s.TokenType = :setting.getTokenType() and "
            + "s.Charmap = :setting.getCharMap() and "
            + "s.RootLength = :setting.getRootLength() and "
            + "s.SansVowels = :setting.getSansVowels()")
    public UsedSetting findUsedSetting(UsedSetting setting);
}
