package com.hida.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * missing javadocs
 *
 * @author lruffin
 * @param <PK>
 * @param <T>
 */
public abstract class AbstractDao<PK extends Serializable, T> {

    /**
     *      
     */
    private final Class<T> persistentClass;

    /**
     * 
     */
    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    /**
     * 
     */
    @Autowired
    private SessionFactory sessionFactory;

    /**
     * 
     * @return 
     */
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * 
     * @param key
     * @return 
     */
    @SuppressWarnings("unchecked")
    public T getByKey(PK key) {
        return (T) getSession().get(persistentClass, key);
    }

    /**
     * 
     * @param entity 
     */
        
    public void persist(T entity) {
        getSession().persist(entity);
    }

    /**
     * 
     * @param entity 
     */
    public void delete(T entity) {
        getSession().delete(entity);
    }

    /**
     * 
     * @return 
     */
    protected Criteria createEntityCriteria() {
        return getSession().createCriteria(persistentClass);
    }

}
