/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.dao.panache;

import com.example.panache.common.Parameters;
import com.example.panache.common.Sort;

import javax.ejb.Asynchronous;
import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * @param <T>
 * @author me-sharifi
 * @author Stéphane Épardaud from Quarkus
 */
public abstract class AbstractDaoFacade<T extends PanacheEntity, ID> implements Serializable {
    private final static int pageSize = 10;
    Logger logger = Logger.getLogger(AbstractDaoFacade.class + "");

    // ======================================
    // =             Attributes             =
    // ======================================
    private Class<T> entityClass;

    // ======================================
    // =         Constructor Methods         =
    // ======================================

    public AbstractDaoFacade(final Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    // ======================================
    // =           Abstract Methods           =
    // ======================================

    public static void persist(EntityManager em, Object entity) {
        if (!em.contains(entity)) {
            em.persist(entity);
        }
    }

    public static void persist(EntityManager em, Iterable<?> entities) {
        for (Object entity : entities) {
            persist(em, entity);
        }
    }

    public static void persist(EntityManager em, Object firstEntity, Object... entities) {
        persist(em, firstEntity);
        for (Object entity : entities) {
            persist(em, entity);
        }
    }
    public  void persist(Stream<?> entities) {
        entities.forEach(entity -> persist(getEntityManager(), entity));
    }

    public  void delete(Object entity) {
        getEntityManager().remove(entity);
    }

    public  boolean isPersistent( T entity) {
        return getEntityManager().contains(entity);
    }

    public static void flush(EntityManager em) {
        em.flush();
    }

    public static Query bindParameters(Query query, Object[] params) {
        if (params == null || params.length == 0)
            return query;
        for (int i = 0; i < params.length; i++) {
            query.setParameter(i + 1, params[i]);
        }
        return query;
    }

    public static Query bindParameters(Query query, Map<String, Object> params) {
        if (params == null || params.size() == 0)
            return query;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }

    private static int paramCount(Object[] params) {
        return params != null ? params.length : 0;
    }

    private static int paramCount(Map<String, Object> params) {
        return params != null ? params.size() : 0;
    }

    private static String getEntityName(Class<?> entityClass) {
        return entityClass.getSimpleName();
    }

    private static String createFindQuery(Class<?> entityClass, String query, int paramCount) {
        if (query == null)
            return "FROM " + getEntityName(entityClass);

        String trimmed = query.trim();
        if (trimmed.isEmpty())
            return "FROM " + getEntityName(entityClass);

        String trimmedLc = trimmed.toLowerCase();
        if (trimmedLc.startsWith("from ") || trimmedLc.startsWith("select ")) {
            return query;
        }
        if (trimmedLc.startsWith("order by ")) {
            return "FROM " + getEntityName(entityClass) + " " + query;
        }
        if (trimmedLc.indexOf(' ') == -1 && trimmedLc.indexOf('=') == -1 && paramCount == 1) {
            query += " = ?1";
        }
        return "FROM " + getEntityName(entityClass) + " WHERE " + query;
    }

    private static String createCountQuery(Class<?> entityClass, String query, int paramCount) {
        if (query == null)
            return "SELECT COUNT(*) FROM " + getEntityName(entityClass);

        String trimmed = query.trim();
        if (trimmed.isEmpty())
            return "SELECT COUNT(*) FROM " + getEntityName(entityClass);

        String trimmedLc = trimmed.toLowerCase();
        if (trimmedLc.startsWith("from ")) {
            return "SELECT COUNT(*) " + query;
        }
        if (trimmedLc.startsWith("order by ")) {
            // ignore it
            return "SELECT COUNT(*) FROM " + getEntityName(entityClass);
        }
        if (trimmedLc.indexOf(' ') == -1 && trimmedLc.indexOf('=') == -1 && paramCount == 1) {
            query += " = ?1";
        }
        return "SELECT COUNT(*) FROM " + getEntityName(entityClass) + " WHERE " + query;
    }

    private static String createDeleteQuery(Class<?> entityClass, String query, int paramCount) {
        if (query == null)
            return "DELETE FROM " + getEntityName(entityClass);

        String trimmed = query.trim();
        if (trimmed.isEmpty())
            return "DELETE FROM " + getEntityName(entityClass);

        String trimmedLc = trimmed.toLowerCase();
        if (trimmedLc.startsWith("from ")) {
            return "DELETE " + query;
        }
        if (trimmedLc.startsWith("order by ")) {
            // ignore it
            return "DELETE FROM " + getEntityName(entityClass);
        }
        if (trimmedLc.indexOf(' ') == -1 && trimmedLc.indexOf('=') == -1 && paramCount == 1) {
            query += " = ?1";
        }
        return "DELETE FROM " + getEntityName(entityClass) + " WHERE " + query;
    }

    public static String toOrderBy(Sort sort) {
        StringBuilder sb = new StringBuilder(" ORDER BY ");
        for (int i = 0; i < sort.getColumns().size(); i++) {
            Sort.Column column = sort.getColumns().get(i);
            if (i > 0)
                sb.append(" , ");
            sb.append(column.getName());
            if (column.getDirection() != Sort.Direction.Ascending)
                sb.append(" DESC");
        }
        return sb.toString();
    }

    @SuppressWarnings("rawtypes")
    public static PanacheQuery<?> findAll(EntityManager em, Class<?> entityClass) {
        String query = "FROM " + getEntityName(entityClass);
        return new PanacheQueryImpl(em, em.createQuery(query), query, null);
    }

    @SuppressWarnings("rawtypes")
    public static PanacheQuery<?> findAll(EntityManager em, Class<?> entityClass, Sort sort) {
        String query = "FROM " + getEntityName(entityClass);
        String sortedQuery = query + toOrderBy(sort);
        return new PanacheQueryImpl(em, em.createQuery(sortedQuery), query, null);
    }

    public static List<?> listAll(EntityManager em, Class<?> entityClass, Sort sort) {
        return findAll(em, entityClass, sort).list();
    }

    public static long count(EntityManager em, Class<?> entityClass) {
        return (long) em.createQuery("SELECT COUNT(*) FROM " + getEntityName(entityClass)).getSingleResult();
    }

    public static long count(EntityManager em, Class<?> entityClass, String query, Object... params) {
        return (long) bindParameters(em.createQuery(createCountQuery(entityClass, query, paramCount(params))),
                params).getSingleResult();
    }

    public static long count(EntityManager em, Class<?> entityClass, String query, Map<String, Object> params) {
        return (long) bindParameters(em.createQuery(createCountQuery(entityClass, query, paramCount(params))),
                params).getSingleResult();
    }

    public static long count(EntityManager em, Class<?> entityClass, String query, Parameters params) {
        return count(em, entityClass, query, params.map());
    }

    public static boolean exists(EntityManager em, Class<?> entityClass) {
        return count(em, entityClass) > 0;
    }

    public static boolean exists(EntityManager em, Class<?> entityClass, String query, Object... params) {
        return count(em, entityClass, query, params) > 0;
    }

    public static boolean exists(EntityManager em, Class<?> entityClass, String query, Map<String, Object> params) {
        return count(em, entityClass, query, params) > 0;
    }

    public static boolean exists(EntityManager em, Class<?> entityClass, String query, Parameters params) {
        return count(em, entityClass, query, params) > 0;
    }

    public static long delete(EntityManager em, Class<?> entityClass, String query, Object... params) {
        return bindParameters(em.createQuery(createDeleteQuery(entityClass, query, paramCount(params))), params)
                .executeUpdate();
    }

    public static long delete(EntityManager em, Class<?> entityClass, String query, Map<String, Object> params) {
        return bindParameters(em.createQuery(createDeleteQuery(entityClass, query, paramCount(params))), params)
                .executeUpdate();
    }

    public static long delete(EntityManager em, Class<?> entityClass, String query, Parameters params) {
        return delete(em, entityClass, query, params.map());
    }

    public static IllegalStateException implementationInjectionMissing() {
        return new IllegalStateException(
                "This method is normally automatically overridden in subclasses: did you forget to annotate your entity with @Entity?");
    }

    public static int executeUpdate(EntityManager em, String query, Object... params) {
        Query jpaQuery = em.createQuery(query);
        bindParameters(jpaQuery, params);
        return jpaQuery.executeUpdate();
    }

    public static int executeUpdate(EntityManager em, String query, Map<String, Object> params) {
        Query jpaQuery = em.createQuery(query);
        bindParameters(jpaQuery, params);
        return jpaQuery.executeUpdate();
    }

    public static void setRollbackOnly() {
//        try {
//            getTransactionManager().setRollbackOnly();
//        } catch (SystemException e) {
//            throw new IllegalStateException(e);
//        }
    }

    abstract protected EntityManager getEntityManager();

    //++++++++++++++++++++++++++++ panache ++++++++++++++++++++++++++++++++++++++++++
    public void persist(Object entity) {
        persist(getEntityManager(), entity);
    }

    public void persist(Iterable<?> entities) {
        persist(getEntityManager(), entities);
    }

    public void persist(Object firstEntity, Object... entities) {
        persist(getEntityManager(), firstEntity, entities);
    }


    public void flush() {
        getEntityManager().flush();
    }

    private String getEntityName() {
        return entityClass.getSimpleName();//TODO
    }

    private String createFindQuery(String query, int paramCount) {
        return createFindQuery(entityClass, query, paramCount);
    }

    private String createCountQuery(String query, int paramCount) {
        return createCountQuery(entityClass, query, paramCount);
    }

    private String createDeleteQuery(String query, int paramCount) {
        return createDeleteQuery(entityClass, query, paramCount);
    }

    // Queries
    public T findById(Object id) throws Exception {
        T entity = getEntityManager().find(entityClass, id);
        if (entity == null)
            throw new EntityNotFoundException("Entity Not Found! id: " + id);
        return entity;

    }

    public PanacheQuery<T> find(String query, Object... params) {
        return find(query, null, params);
    }

    @SuppressWarnings("rawtypes")
    public PanacheQuery<T> find(String query, Sort sort, Object... params) {
        String findQuery = createFindQuery(entityClass, query, paramCount(params));
        // FIXME: check for duplicate ORDER BY clause?
        Query jpaQuery = getEntityManager().createQuery(sort != null ? findQuery + toOrderBy(sort) : findQuery);
        bindParameters(jpaQuery, params);
        return new PanacheQueryImpl<T>(getEntityManager(), jpaQuery, findQuery, params);
    }

    public PanacheQuery<?> find(String query, Map<String, Object> params) {
        return find(query, params);
    }

//    public static Stream<?> streamAll(Class<?> entityClass) {
//        return findAll(entityClass).stream();
//    }
//
//    public static Stream<?> streamAll(Class<?> entityClass, Sort sort) {
//        return findAll(entityClass, sort).stream();
//    }

    @SuppressWarnings("rawtypes")
    public PanacheQuery<?> find(String query, Sort sort, Map<String, Object> params) {
        String findQuery = createFindQuery(entityClass, query, paramCount(params));
        // FIXME: check for duplicate ORDER BY clause?
        Query jpaQuery = getEntityManager().createQuery(sort != null ? findQuery + toOrderBy(sort) : findQuery);
        bindParameters(jpaQuery, params);
        return new PanacheQueryImpl(getEntityManager(), jpaQuery, findQuery, params);
    }

    public PanacheQuery<?> find(String query, Parameters params) {
        return find(query, null, params);
    }

    public PanacheQuery<?> find(String query, Sort sort, Parameters params) {
        return find(query, sort, params.map());
    }

    public List<?> list(String query, Object... params) {
        return find(query, params).list();
    }

    public List<?> list(String query, Sort sort, Object... params) {
        return find(query, sort, params).list();
    }

    public List<?> list(String query, Map<String, Object> params) {
        return find(query, params).list();
    }

    public List<?> list(String query, Sort sort, Map<String, Object> params) {
        return find(query, sort, params).list();
    }

    public List<?> list(String query, Parameters params) {
        return find(query, params).list();
    }

    public List<?> list(String query, Sort sort, Parameters params) {
        return find(query, sort, params).list();
    }

//    public List<T> findAllT() {
//        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
//        cq.select(cq.from(entityClass));
//        TypedQuery<T>  query= getEntityManager().createQuery(cq);
//        return query.getResultList();
//    }
    public List<T> findAllT() {//SELECT c FROM Account c ,typedQuery: Account sql="SELECT PK_ACOUNT_NO, AGE, CREATE_AT FROM T_ACCOUNT"
//        StringBuilder sb;// Populate count
//        sb = new StringBuilder("SELECT c FROM " + entityClass.getSimpleName() + " c ORDER BY PK_ACOUNT_NO DESC");// Populate pageItems
//        String sortedQuery = sb.toString() ;
//        System.out.println(" ,sortedQuery: "+sortedQuery);
//        TypedQuery<T>  typedQuery = getEntityManager().createQuery(sortedQuery, entityClass);
//        System.out.println(sortedQuery+" ,typedQuery: "+typedQuery);
//        return typedQuery.getResultList();
        String jql = "SELECT c FROM Account as c order by c.accountNo desc";
        Query sortQuery =  getEntityManager().createQuery(jql);
        return sortQuery.getResultList();
    }
    public  List<T> findAllT(Sort sort) {
        StringBuilder sb;// Populate count
        sb = new StringBuilder("SELECT c FROM " + entityClass.getSimpleName() + " as c ");//"SELECT c FROM Account as c order by c.accountNo desc";
        System.out.println("toOrderBy(sort): "+toOrderBy(sort));
        String sortedQuery = sb.toString() + toOrderBy(sort);
        System.out.println(" ,sortedQuery: "+sortedQuery);
        TypedQuery<T>  typedQuery = getEntityManager().createQuery(sortedQuery, entityClass);
        System.out.println(sortedQuery+" ,typedQuery: "+typedQuery);
        return typedQuery.getResultList();
    }

    public T findOne() throws Exception {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return (T) getEntityManager().createQuery(cq).getSingleResult();
    }

    @SuppressWarnings("rawtypes")
    public PanacheQuery<?> findAll() {
        return findAll(getEntityManager(), entityClass);
    }

    public PanacheQuery<?> findAll(Sort sort) {
        return findAll(getEntityManager(), entityClass, sort);
    }

    public List<?> listAll() {
        return findAll().list();
    }

    public List<?> listAll(Sort sort) {
        return findAll(sort).list();
    }

    public long countMy() throws Exception {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public long count() {
        return count(getEntityManager(), entityClass);
    }

    public long count(String query, Object... params) {
        return count(getEntityManager(), entityClass, query, params);
    }

    public long count(String query, Map<String, Object> params) {
        return count(getEntityManager(), entityClass, query, params);
    }

    public long count(String query, Parameters params) {
        return count(getEntityManager(), entityClass, query, params.map());
    }

    public boolean exists() {
        return count() > 0;
    }

    public boolean exists(String query, Object... params) {
        return exists(getEntityManager(), entityClass, query, params);
    }

    public boolean exists(String query, Map<String, Object> params) {
        return exists(getEntityManager(), entityClass, query, params);
    }

    public boolean exists(String query, Parameters params) {
        return exists(getEntityManager(), entityClass, query, params);
    }

    public long deleteAll() {
        return deleteAll(getEntityManager(), entityClass);
    }

    public long deleteAll(EntityManager em, Class<?> entityClass) {
        return em.createQuery("DELETE FROM " + getEntityName(entityClass)).executeUpdate();
    }

    public long delete(String query, Object... params) {
        return delete(getEntityManager(), entityClass, query, params);
    }

    public long delete(String query, Map<String, Object> params) {
        return delete(getEntityManager(), entityClass, query, params);
    }

    public long delete(String query, Parameters params) {
        return delete(getEntityManager(), entityClass, query, params);
    }

    public int executeUpdate(String query, Object... params) {
        return executeUpdate(getEntityManager(), query, params);
    }

    public int executeUpdate(String query, Map<String, Object> params) {
        return executeUpdate(getEntityManager(), query, params);
    }

    //+++++++++++++++++++ end Quarkus Panache ++++++++++++++++++++++++++++++++++++++++++++++++++
    // ======================================
    // =           Public Methods           =
    // ======================================

    public List<T> execNamedQuery(String namedQuery, String mobileNo) throws Exception {
        List<T> list = null;
        try {
            list = getEntityManager().createNamedQuery(namedQuery, entityClass) //941126
                    //            list = (List<T>) getEntityManager().createNamedQuery("StatusRepository.findByMobileNo", StatusRepository.class)
                    .setParameter("mobileNo", mobileNo)
                    .setMaxResults(1)
                    .getResultList();//ERROR Get Single REQUEST!
        } catch (Exception ex) {
            throw new Exception("Cant execNamedQuery mobileNo " + mobileNo, ex);
        }
        return list;
    }

    //    public void create(T entity) throws Exception {
//        getEntityManager().persist(entity);
//    }
    public T create(T t) {
        getEntityManager().persist(t);
        getEntityManager().flush();
        getEntityManager().refresh(t);
        return t;
    }

    @Asynchronous
    public void createNotReturn(T t) {
        getEntityManager().persist(t);
    }

    public T edit(T entity) throws Exception {
        entity = getEntityManager().merge(entity);
        return entity;
    }

    public void remove(T entity) throws Exception {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    /**
     * find record then remove it
     *
     * @param id
     * @throws Exception
     */
    public void deleteById(Object id) throws Exception {
        T entity = findById(id);
        if (entity == null) {
            throw new EntityNotFoundException("Not Found Entity With Id: " + id);
        }
        remove(entity);
    }


    /**
     * @param startPosition
     * @param maxResult
     * @return
     * @throws Exception
     */
    public List<T> findRange(Integer startPosition, Integer maxResult) throws Exception {
//    public List<T> findRange(int[] range) throws Exception {
//        Integer startPosition = range[0];
//        Integer maxResult = range[1];
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        if (startPosition != null)
            q.setFirstResult(startPosition);
        if (maxResult != null)
            //q.setMaxResults(maxResult - startPosition + 1);
            q.setMaxResults(maxResult);
        return q.getResultList();
    }


    public List<T> findWithNamedQuery(String namedQueryName) {
        return getEntityManager().createNamedQuery(namedQueryName).getResultList();
    }

    /**
     * Cachable
     *
     * @param namedQueryName
     * @param parameters
     * @param cacheable
     * @return
     */
    public List findWithNamedQuery(String namedQueryName, Map parameters, boolean cacheable) {
        return findWithNamedQuery(namedQueryName, parameters, 0, cacheable);
    }

    public List findWithNamedQuery(String namedQueryName, Map parameters) {
        return findWithNamedQuery(namedQueryName, parameters, 0, false);
    }

    public List findWithNamedQuery(String queryName, int resultLimit) {
        return getEntityManager().createNamedQuery(queryName).
                setMaxResults(resultLimit).
                getResultList();
    }

    public List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit) {
        return findWithNamedQuery(namedQueryName, parameters, resultLimit, true);
    }

    //http://www.adam-bien.com/roller/abien/entry/generic_crud_service_aka_dao
    public List findWithNamedQuery(String namedQueryName, Map<String, Object> parameters, int resultLimit, boolean cacheable) {

        Query query = getEntityManager().createNamedQuery(namedQueryName);
        if (resultLimit > 0) {
            query.setMaxResults(resultLimit);
        }

        if (parameters != null) {
            Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
            for (Map.Entry<String, Object> entry : rawParameters) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        if (!cacheable)
            query.setHint("javax.persistence.cache.storeMode", "REFRESH");
        return query.getResultList();
    }

    public List<T> findWithQuery(String queryName) {
        return getEntityManager().createQuery(queryName).getResultList();
    }

    public List<T> findByNativeQuery(String sql) {
        return getEntityManager().createNativeQuery(sql, entityClass).getResultList();
    }

    public T findSingleWithNamedQuery(String namedQueryName) throws Exception {
        T result = null;
        try {
            result = (T) getEntityManager().createNamedQuery(namedQueryName).getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("Cant find " + entityClass.getSimpleName());
        }
        return result;
    }

    public T findSingleWithNamedQuery(String namedQueryName, Map<String, Object> parameters) throws NoResultException {
        return findSingleWithNamedQuery(namedQueryName, parameters, false);
    }

    /**
     * Ability Cachable
     *
     * @param namedQueryName
     * @param parameters
     * @param cacheable
     * @return
     * @throws NoResultException
     */
    public T findSingleWithNamedQuery(String namedQueryName, Map<String, Object> parameters, boolean cacheable) throws NoResultException {

        Query query = getEntityManager().createNamedQuery(namedQueryName);

        if (parameters != null) {
            Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
            for (Map.Entry<String, Object> entry : rawParameters) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        T result = null;
        try {
            if (!cacheable)
                query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            result = (T) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("Cant find Entity!");
        }
        return result;
    }

    public long countBetweenDate(String namedQueryName, Date startDate, Date endDate) throws Exception {
        if (!endDate.after(startDate)) {
            throw new Exception("Start Date must be before end date! start Date is:" + startDate + " ,end Date: " + endDate);
        }
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("startDate", startDate);
        parameter.put("endDate", endDate);
        return countBetweenDateWithNamedQuery(namedQueryName, parameter);
    }

    public long countBetweenDateAndHost(String namedQueryName, Date startDate, Date endDate, long hostId) throws Exception {
        if (!endDate.after(startDate)) {
            throw new Exception("Start Date must be before end date! start Date is:" + startDate + " ,end Date: " + endDate);
        }
        Map<String, Object> parameter = new HashMap<>();
        parameter.put("startDate", startDate);
        parameter.put("endDate", endDate);
        parameter.put("host", hostId);
        return countBetweenDateWithNamedQuery(namedQueryName, parameter);
    }


    public long countBetweenDateWithNamedQuery(String namedQueryName, Map<String, Object> parameters) throws Exception {
        return countBetweenDateWithNamedQuery(namedQueryName, parameters, false);
    }

    public long countBetweenDateWithNamedQuery(String namedQueryName, Map<String, Object> parameters, boolean cacheable) throws Exception {
        Query query = getEntityManager().createNamedQuery(namedQueryName);

        if (parameters != null) {
            Set<Map.Entry<String, Object>> rawParameters = parameters.entrySet();
            for (Map.Entry<String, Object> entry : rawParameters) {
                query.setParameter(entry.getKey(), entry.getValue());
            }
        }
        long result;
        try {
            if (!cacheable)
                query.setHint("javax.persistence.cache.storeMode", "REFRESH");
            result = (long) query.getSingleResult();
        } catch (NoResultException e) {
            throw new NoResultException("Cant count Entity!");
        }
        return result;
    }

    //---------------------------950916---------------------------------
//    http://stackoverflow.com/questions/4050111/best-practice-to-generate-a-jpa-dynamic-typed-query
    public <T> List<T> findAllEntitiesOrderedBy(String orderByColumn, boolean ascending) {
        CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery criteria = builder.createQuery();
        Root<T> entityRoot = criteria.from(entityClass);
        criteria.select(entityRoot);
        javax.persistence.criteria.Order order = ascending ? builder.asc(entityRoot.get(orderByColumn))
                : builder.desc(entityRoot.get(orderByColumn));
        criteria.orderBy(order);
        return getEntityManager().createQuery(criteria).getResultList();
    }

    public String createWhereClause(Object object) throws Exception {
        final StringBuilder sb = new StringBuilder(" WHERE 1 = 1");
        BeanInfo infoDsc = Introspector.getBeanInfo(object.getClass());
        for (PropertyDescriptor pdDsc : infoDsc.getPropertyDescriptors()) {
            Method readerSrc = pdDsc.getReadMethod();
            String fieldName = pdDsc.getName();
            if (readerSrc != null && !fieldName.equalsIgnoreCase("class") && !"".equals(readerSrc)) {
                Object[] paramesReader = new Object[]{};
                Object value = readerSrc.invoke(object, paramesReader);
                if (value != null && !"".equals(value)) {
//                    LOGGER.debug("****name: " + fieldName + " ,readerSrc: " + readerSrc + " ,invok: " + value);
                    if (pdDsc.getPropertyType().getName().toLowerCase().contains("boolean")) {
                        sb.append(" AND c." + fieldName + " = " + value + "");
                    } else
                        sb.append(" AND LOWER(c." + fieldName + ") LIKE '%" + value + "%'");
                }
            }
        }
        return sb.toString();
    }

    private TypedQuery<T> createQueryFormObject(Object object) throws Exception {
        StringBuilder sb;// Populate count
        sb = new StringBuilder("SELECT c FROM " + entityClass.getSimpleName() + " c");// Populate pageItems
        sb.append(createWhereClause(object));
//        LOGGER.debug("***Query: " + sb);
        return getEntityManager().createQuery(sb.toString(), entityClass);
    }

    public List<T> paginate(int page, Object object) throws Exception {
        TypedQuery<T> query = createQueryFormObject(object);
        query.setFirstResult(page * pageSize).setMaxResults(pageSize);
        List<T> list = query.getResultList();
        return list;
    }

    public long paginateCount(Object object) throws Exception {
        StringBuilder sb;// Populate count
//        System.out.println("***object: "+object +" ,entityClass: "+entityClass);
        sb = new StringBuilder("SELECT count(c) FROM " + entityClass.getSimpleName() + " c");// Populate pageItems
        sb.append(createWhereClause(object));
//        System.out.println("***Query Count: "+sb);
        TypedQuery<Long> countCriteria = getEntityManager().createQuery(sb.toString(), Long.class);
        return countCriteria.getSingleResult();
    }

    public List<T> paginate(int page, String whereClause) throws Exception {
        StringBuilder sb;// Populate count
        sb = new StringBuilder("SELECT c FROM " + entityClass.getSimpleName() + " c");// Populate pageItems
        sb.append(whereClause);
        TypedQuery<T> query = getEntityManager().createQuery(sb.toString(), entityClass);
        query.setFirstResult(page * pageSize).setMaxResults(pageSize);
        return query.getResultList();
    }

    //--------
    public List<T> paginate3(int page) throws Exception {
        TypedQuery<T> query = createQueryFromPaginate();
        query.setFirstResult(page).setMaxResults(3);
        List<T> list = query.getResultList();
        return list;
    }

    public List<T> paginate(int page) throws Exception {
        TypedQuery<T> query = createQueryFromPaginate();
        query.setFirstResult(page * pageSize).setMaxResults(pageSize);
        List<T> list = query.getResultList();
        return list;
    }

    private TypedQuery<T> createQueryFromPaginate() throws Exception {
        StringBuilder sb;// Populate count
        sb = new StringBuilder("SELECT c FROM " + entityClass.getSimpleName() + " c");// Populate pageItems
        return getEntityManager().createQuery(sb.toString(), entityClass);
    }


}

