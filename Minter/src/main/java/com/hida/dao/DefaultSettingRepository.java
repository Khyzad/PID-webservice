package com.hida.dao;

import com.hida.model.DefaultSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author lruffin
 */
public interface DefaultSettingRepository extends CrudRepository<DefaultSetting, Integer>{

    @Query("select s from DEFAULT_SETTING where s.Id = 1")
    public DefaultSetting findCurrentDefaultSetting();
}
