package com.hida.dao;

import com.hida.model.Pid;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 * Programmatic implementation of PidDao
 * @author lruffin
 */
@Repository("pidDao")
public class PidDaoImpl extends AbstractDao<String, Pid> implements PidDao {
    
    /**
     * Finds a PID by its unique name 
     *
     * @param name 
     * @return PID
     */
    @Override
    public Pid findByName(String name) {
        return getByKey(name);
    }
    
    /**
     * Saves a PID 
     *
     * @param pid 
     */
    @Override
    public void savePid(Pid pid) {
        persist(pid);
    }

    /**
     * Lists all PIDs stored in the database
     *
     * @return List of all PIDs
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Pid> findAllPids() {
        Criteria criteria = createEntityCriteria();
        return (List<Pid>) criteria.list();
    }

    /**
     * Currently incomplete and should not be used, subject to deletion
     *
     * @param regex
     * @return
     */
    @Override
    public Pid findPidByRegex(String regex) {
        Criteria criteria = createEntityCriteria();
        criteria.add(Restrictions.eq("ssn", regex));
        return (Pid) criteria.uniqueResult();
    }

}
