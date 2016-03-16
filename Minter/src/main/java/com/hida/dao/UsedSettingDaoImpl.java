package com.hida.dao;

import com.hida.model.TokenType;
import com.hida.model.UsedSetting;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Repository;


/**
 * missing javadoc
 *
 * @author lruffin
 */
@Repository("usedSettingDao")
public class UsedSettingDaoImpl extends AbstractDao<Integer, UsedSetting> implements UsedSettingDao {

    /**
     * missing javadoc
     *
     * @param setting
     */
    @Override
    public void save(UsedSetting setting) {
        persist(setting);
    }

    /**
     * missing javadoc
     *
     * @param setting
     */
    @Override
    public void deleteSetting(UsedSetting setting) {
        delete(setting);
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @Override
    public List<UsedSetting> findAllUsedSettings() {
        Criteria criteria = createEntityCriteria();
        return (List<UsedSetting>) criteria.list();
    }

    /**
     * missing javadoc
     *
     * @param id
     * @return
     */
    @Override
    public UsedSetting findUsedSettingById(int id) {
        Criteria criteria = createEntityCriteria();
        criteria.add(Restrictions.eq("Id", id));
        return (UsedSetting) criteria.uniqueResult();
    }

    /**
     * missing javadoc
     *
     * @param Prefix
     * @param TokenType
     * @param CharMap
     * @param RootLength
     * @param SansVowels
     * @return
     */
    @Override
    public UsedSetting findUsedSetting(String Prefix, TokenType TokenType, String CharMap,
            int RootLength, boolean SansVowels) {
        Query query = this.getSession().createSQLQuery(
                String.format("Select Id from USED_SETTING where "
                        + "Pid_Prefix='%s' and "
                        + "TokenType='%s' and "
                        + "Charmap='%s' and "
                        + "RootLength='%d' and "
                        + "SansVowels='%b'",
                        Prefix, TokenType, CharMap, RootLength, SansVowels));

        List list = query.list();
        if (list.isEmpty()) {
            return findUsedSettingById(-1);
        }
        else {
            int id = (int) list.get(0);
            return findUsedSettingById(id);
        }
    }

   
        

}
