/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hida.dao;

import com.hida.model.Pid;
import java.util.List;

/**
 *
 * @author lruffin
 */
public interface PidDao {
    
    public Pid findByName(String name);
      
    public void saveEmployee(Pid pid);
 
    public List<Pid> findAllPids();
 
    public Pid findPidByRegex(String regex);
}
