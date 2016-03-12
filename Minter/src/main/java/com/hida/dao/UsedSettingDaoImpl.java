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
        criteria.add(Restrictions.eq("id", id));
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
    public List<UsedSetting> findUsedSetting(String Prefix, TokenType TokenType, String CharMap,
            int RootLength, boolean SansVowels) {
        Query query = this.getSession().createSQLQuery(
                String.format("Select from USED_SETTING where prefix='%s' and tokenType='%s' "
                        + "and charmap='%s' and rootlength='%d' and sansvowels='%b'",
                        Prefix, TokenType, CharMap, RootLength, SansVowels));

        return query.list();
    }

   
        

}
