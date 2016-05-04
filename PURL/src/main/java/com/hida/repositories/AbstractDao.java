package com.hida.repositories;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * AbstractDao acts as base implementation for all Dao classes and provides
 * wrapper methods for all Hibernate operations
 *
 * @author lruffin
 * @param <PK> Persistent Key of the calling POJO
 * @param <T> The POJO
 */
public abstract class AbstractDao<PK extends Serializable, T> {

    /**
     * The class T
     */
    private final Class<T> persistentClass;

    /**
     * Constructor
     */
    @SuppressWarnings("unchecked")
    public AbstractDao() {
        this.persistentClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[1];
    }

    @Autowired
    private SessionFactory sessionFactory;

    /**
     * Returns the current session associated with the current transaction
     *
     * @return the current session
     */
    protected Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    /**
     * Retrieves a persisted object using a persistent key
     *
     * @param key The persistent key
     * @return persisted object
     */
    @SuppressWarnings("unchecked")
    public T getByKey(PK key) {
        return (T) getSession().get(persistentClass, key);
    }

    /**
     * Persists an object
     *
     * @param entity Object to be saved
     */
    public void persist(T entity) {
        getSession().persist(entity);
    }

    /**
     * Removes an object from persistence
     *
     * @param entity Object to be removed
     */
    public void delete(T entity) {
        getSession().delete(entity);
    }

    /**
     * Creates and returns a Criteria object that can be used to filter
     * persisted objects
     *
     * @return Criteria
     */
    protected Criteria createEntityCriteria() {
        return getSession().createCriteria(persistentClass);
    }

}
