package com.example.panache.dao;

import com.example.dao.panache.AbstractDaoFacade;
import com.example.panache.entity.Account;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 28/11/2019
 */
@ApplicationScoped
@Transactional
public class AccountDao extends AbstractDaoFacade<Account,String> {

    @Inject
    private EntityManager entityManager;

    public AccountDao() {
        super(Account.class);
    }

//    public Account findBy(String string) throws NoResultException {
//        Map<String, Object> parameter = new HashMap<>();
//        parameter.put("appID", appId);
//        return findSingleWithNamedQuery(Account.FIND_BY_APPID, parameter);
//    }


    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
