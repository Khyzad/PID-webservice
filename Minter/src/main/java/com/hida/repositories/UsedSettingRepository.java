package com.hida.repositories;

import com.hida.model.Token;
import com.hida.model.UsedSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Allows the use of CRUD operations on UsedSetting objects
 *
 * @author lruffin
 */
public interface UsedSettingRepository extends CrudRepository<UsedSetting, Integer> {

    @Query("select s from UsedSetting s where s.prefix_ = :prefix and "
            + "s.tokenType_ = :tokenType and "
            + "s.charMap_ = :charMap and "
            + "s.rootLength_ = :rootLength and "
            + "s.sansVowels_ = :sansVowel")
    public UsedSetting findUsedSetting(@Param("prefix") String prefix,
            @Param("tokenType") Token tokenType,
            @Param("charMap") String charMap,
            @Param("rootLength") int rootLength,
            @Param("sansVowel") boolean sansVowel);
}
