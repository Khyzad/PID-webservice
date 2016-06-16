package com.hida.repositories;

import com.hida.model.DefaultSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Allows the use of CRUD operations on DefaultSetting objects
 *
 * @author lruffin
 */
public interface DefaultSettingRepository extends CrudRepository<DefaultSetting, Integer> {

    @Query("select s from DefaultSetting s where s.id_ = 1")
    public DefaultSetting findCurrentDefaultSetting();
}
