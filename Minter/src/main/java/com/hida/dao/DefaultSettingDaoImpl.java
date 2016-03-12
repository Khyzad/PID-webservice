package com.hida.dao;

import com.hida.model.DefaultSetting;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * missing javadoc
 *
 * @author lruffin
 */
@Repository("defaultSettingDao")
public class DefaultSettingDaoImpl extends AbstractDao<Integer, DefaultSetting>
        implements DefaultSettingDao {

    /**
     * missing javadoc
     *
     * @param setting
     */
    @Override
    public void save(DefaultSetting setting) {
        persist(setting);
    }

    /**
     * missing javadoc
     *
     * @param setting
     */
    @Override
    public void deleteSetting(DefaultSetting setting) {
        this.delete(setting);
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @Override
    public DefaultSetting getDefaultSetting() {
        Criteria criteria = createEntityCriteria();
        criteria.add(Restrictions.eq("id", 0));
        return (DefaultSetting) criteria.uniqueResult();
    }

}
