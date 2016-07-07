/*
 * Copyright 2016 Lawrence Ruffin, Leland Lopez, Brittany Cruz, Stephen Anspach
 *
 * Developed in collaboration with the Hawaii State Digital Archives.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
