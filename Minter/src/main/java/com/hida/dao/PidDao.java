package com.hida.dao;

import com.hida.model.Pid;
import java.util.List;

/**
 *
 * @author lruffin
 */
public interface PidDao {
    
    public Pid findByName(String name);
      
    public void savePid(Pid pid);
 
    public List<Pid> findAllPids();
 
    public Pid findPidByRegex(String regex);
    
    
}
