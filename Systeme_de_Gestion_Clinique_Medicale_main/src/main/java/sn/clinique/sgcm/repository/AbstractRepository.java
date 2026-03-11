package sn.clinique.sgcm.repository;

import jakarta.persistence.EntityManager;
import sn.clinique.sgcm.util.configs.JpaUtil;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;

public abstract class AbstractRepository<T, ID> implements GenericRepository<T, ID> {

    protected final Class<T> entityClass;

    @SuppressWarnings("unchecked")
    public AbstractRepository() {
        this.entityClass = (Class<T>)
                ((ParameterizedType) getClass().getGenericSuperclass())
                        .getActualTypeArguments()[0];
    }

    @Override
    public T save(T entity) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            return entity;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public T update(T entity) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            T updated = em.merge(entity);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(ID id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            T entity = em.find(entityClass, id);
            if (entity != null) em.remove(entity);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw e;
        } finally {
            em.close();
        }
    }

    @Override
    public Optional<T> findById(ID id) {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return Optional.ofNullable(em.find(entityClass, id));
        } finally {
            em.close();
        }
    }

    @Override
    public List<T> findAll() {
        EntityManager em = JpaUtil.getEntityManager();
        try {
            return em.createQuery("SELECT e FROM " + entityClass.getSimpleName() + " e", entityClass)
                    .getResultList();
        } finally {
            em.close();
        }
    }
}