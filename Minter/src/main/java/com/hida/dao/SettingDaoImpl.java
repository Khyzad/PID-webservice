package com.hida.dao;

import com.hida.model.Setting;
import com.hida.model.TokenType;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * missing javadoc
 *
 * @author lruffin
 */
@Service("settingDao")
@Transactional
public class SettingDaoImpl extends AbstractDao<Integer, Setting> implements SettingDao {

    /**
     * missing javadoc
     *
     * @param setting
     */
    @Override
    public void save(Setting setting) {
        persist(setting);
    }

    /**
     * missing javadoc
     *
     * @param setting
     */
    @Override
    public void deleteSetting(Setting setting) {
        delete(setting);
    }

    /**
     * missing javadoc
     *
     * @return
     */
    @Override
    public List<Setting> findAllSettings() {
        Criteria criteria = createEntityCriteria();
        return (List<Setting>) criteria.list();
    }

    /**
     * missing javadoc
     *
     * @param id
     * @return
     */
    @Override
    public Setting findSettingById(int id) {
        Criteria criteria = createEntityCriteria();
        criteria.add(Restrictions.eq("id", id));
        return (Setting) criteria.uniqueResult();
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
    public List<Setting> findSetting(String Prefix, TokenType TokenType, String CharMap,
            int RootLength, boolean SansVowels) {
        Query query = this.getSession().createSQLQuery(
                String.format("Select from USED_SETTING where prefix='%s' and tokenType='%s' "
                        + "and charmap='%s' and rootlength='%d' and sansvowels='%b'",
                        Prefix, TokenType, CharMap, RootLength, SansVowels));

        return query.list();
    }
        

}
