package com.example.rest.endpoint;

import com.example.dao.panache.AbstractDaoFacade;
import com.example.panache.dao.PersonDao;
import com.example.panache.entity.Person;
import com.example.serializer.JSONFormatter;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 28/11/2019
 */
@Path("persons")
public class PersonEndpoint extends AbstractRestFacade<Person, Long> {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response test() {
        Person person = personDao.create(new Person("Mahdi"));
        System.err.println("person: "+person);
        return Response.ok(JSONFormatter.toJSON(personDao.findAllT())).build();
    }

    //--------------
    @GET
    @Path("time")
    @Produces(MediaType.TEXT_PLAIN)
    public Response myDateTime() {
        return Response.ok(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZ").format(new Date())).build();
    }
    @Inject
    private PersonDao personDao;


    public PersonEndpoint() {
        super(Person.class);
    }

    @Override
    protected AbstractDaoFacade<Person,Long> getDao() {
        return personDao;
    }

    @Override
    protected Class getEndpoint() {
        return PersonEndpoint.class;
    }

}
