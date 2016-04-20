package com.hida.service;

import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import java.util.Set;

/**
 * This class is used to define the possible operations that Hibernate can
 * perform on MinterService objects
 *
 * @author lruffin
 */
public interface MinterService {

    public Set<Pid> mint(long amount, DefaultSetting setting);

    public DefaultSetting getCurrentSetting();

    public void updateCurrentSetting(DefaultSetting newSetting);

}
