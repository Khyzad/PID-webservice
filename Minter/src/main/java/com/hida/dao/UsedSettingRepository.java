package com.hida.dao;

import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author lruffin
 */
public interface UsedSettingRepository extends CrudRepository<UsedSetting, Integer>{
    
    @Query("select s from UsedSetting s where s.Prefix = :prefix and "
            + "s.TokenType = :tokenType and "
            + "s.CharMap = :charMap and "
            + "s.RootLength = :rootLength and "
            + "s.SansVowels = :sansVowel")
    public UsedSetting findUsedSetting(@Param("prefix") String prefix,
            @Param("tokenType") TokenType tokenType,
            @Param("charMap") String charMap,
            @Param("rootLength") int rootLength,
            @Param("sansVowel") boolean sansVowel);
}
