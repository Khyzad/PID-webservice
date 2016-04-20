package com.hida.dao;

import com.hida.model.UsedSetting;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

/**
 * Programmatic implementation of UsedSettingDao
 *
 * @author lruffin
 */
@Repository("usedSettingDao")
public class UsedSettingDaoImpl extends AbstractDao<Integer, UsedSetting> implements UsedSettingDao {

    /**
     * Saves a UsedSetting object
     *
     * @param setting
     */
    @Override
    public void save(UsedSetting setting) {
        persist(setting);
    }

    /**
     * Returns a list of all UsedSetting objects in the database
     *
     * @return List of UsedSetting objects
     */
    @Override
    public List<UsedSetting> findAllUsedSettings() {
        Criteria criteria = createEntityCriteria();
        return (List<UsedSetting>) criteria.list();
    }

    /**
     * Returns a UsedSetting object assigned the given unique id
     *
     * @param id Unique id
     * @return UsedSetting object of the given unique id
     */
    @Override
    public UsedSetting findUsedSettingById(int id) {
        return getByKey(id);
    }

    /**
     * Finds a UsedSetting entity with the matching values given by a
     * UsedSetting object. If an entity could not be found then a null value
     * is returned
     *
     * @param setting A UsedSetting object that contain sought-after values 
     * @return A matching UsedSetting entity, null otherwise
     */
    @Override
    public UsedSetting findUsedSetting(UsedSetting setting) {
        Query query = this.getSession().createSQLQuery(
                String.format("Select Id from USED_SETTING where "
                        + "Pid_Prefix='%s' and "
                        + "TokenType='%s' and "
                        + "Charmap='%s' and "
                        + "RootLength='%d' and "
                        + "SansVowels='%b'",
                        setting.getPrefix(), setting.getTokenType(), setting.getCharMap(),
                        setting.getRootLength(), setting.isSansVowels()));

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
