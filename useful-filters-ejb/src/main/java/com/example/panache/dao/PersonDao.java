package com.example.panache.dao;

import com.example.dao.panache.AbstractDaoFacade;
import com.example.panache.entity.Person;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 28/11/2019
 */
@ApplicationScoped
@Transactional
public class PersonDao extends AbstractDaoFacade<Person,Long> {
    @Inject
    private EntityManager entityManager;

    public PersonDao() {
        super(Person.class);
    }

    public  Person findByName(String name) {
        return (Person) find("name", name).firstResult();
    }

    public  void deleteStefs() {
        delete("name", "Stef");
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
