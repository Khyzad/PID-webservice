package com.hida.dao;

import com.hida.model.Pid;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;

/**
 *
 * @author lruffin
 */
@Repository("pidDao")
public class PidDaoImpl extends AbstractDao<String, Pid> implements PidDao {
    
    /**
     * missing javadoc
     *
     * @param name
     * @return
     */
    @Override
    public Pid findByName(String name) {
        return getByKey(name);
    }
    
    /**
     * missing javadoc
     *
     * @param pid
     */
    @Override
    public void savePid(Pid pid) {
        persist(pid);
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<Pid> findAllPids() {
        Criteria criteria = createEntityCriteria();
        return (List<Pid>) criteria.list();
    }

    /**
     * missing javadoc; currently incomplete
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
