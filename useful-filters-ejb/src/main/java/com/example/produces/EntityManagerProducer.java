package com.example.produces;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
//https://in.relation.to/2019/01/23/testing-cdi-beans-and-persistence-layer-under-java-se/
//https://stackoverflow.com/questions/19431423/getting-a-reference-to-entitymanager-in-java-ee-applications-using-cdi
//https://docs.jboss.org/cdi/spec/1.2/cdi-spec.html
/**
 * A resource is a bean that represents a reference to a resource, persistence context, persistence unit,
 * remote EJB or web service in the Java EE component environment.
 * By declaring a resource, we enable an object from the Java EE component environment to be injected
 * by specifying only its type and qualifiers at the injection point.
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 26/11/2019
 */
public class EntityManagerProducer {

    @PersistenceContext(unitName = "PU_SQL")
    private EntityManager entityManager;

    @Produces
    @RequestScoped
    public EntityManager getEntityManager() {
        return entityManager;
    }

//    public void close(@Disposes EntityManager entityManager) { dont need this, because container close it
//        entityManager.close();
//    }
}
