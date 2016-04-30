package com.hida.dao;

import com.hida.model.Pid;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author lruffin
 */
public interface PidRepository extends CrudRepository<Pid, String>{
    
}
