package com.hida.service;

import com.hida.model.DefaultSetting;
import com.hida.model.Pid;
import java.util.Set;

/**
 *
 * @author lruffin
 */
public interface MinterService {                                    
    public Set<Pid> mint(long amount, DefaultSetting setting);
    
    public DefaultSetting getCurrentSetting();
    public void updateCurrentSetting(DefaultSetting newSetting);
    
}
