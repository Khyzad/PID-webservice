package com.hida.dao;

import com.hida.model.Pid;
import java.util.List;

/**
 * This class is used to define the possible operations that Hibernate can
 * perform on Pid objects
 *
 * @author lruffin
 */
public interface PidDao {

    public Pid findByName(String name);

    public void savePid(Pid pid);

    public List<Pid> findAllPids();

    public Pid findPidByRegex(String regex);

}
