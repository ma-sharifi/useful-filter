package com.example.rest.endpoint;

import com.example.dao.panache.AbstractDaoFacade;
import com.example.dao.panache.PanacheEntity;
import org.apache.log4j.Logger;
import com.example.serializer.JSONFormatter;

import javax.persistence.NoResultException;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;
import java.util.List;

/**
 * Created by me-sharifi on 12/3/2016.
 */

public abstract class AbstractRestFacade<T extends PanacheEntity, ID> {

    private Logger LOGGER = Logger.getLogger(AbstractRestFacade.class);
    private Class<T> entityClass;

    AbstractRestFacade(final Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    abstract protected AbstractDaoFacade<T, ID> getDao();

    // ======================================
    // =         Constructor Methods         =
    // ======================================

    abstract protected Class getEndpoint();

    // ======================================
    // =           Public Methods           =
    // ======================================
//    @GET
//    @Path("query")
//    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
//    @Produces({ MediaType.APPLICATION_JSON })
//    public Response createBranchByQueryParam(@Context UriInfo ui) throws  Exception {
//        //https://docs.oracle.com/cd/E19798-01/821-1841/gipyw/index.html
//        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
//        Mapper mapper = new DozerBeanMapper();
//        MpcTest destObject = mapper.map(queryParams, MpcTest.class);
//        System.out.println("queryParams: "+queryParams);
//        return Response.ok(destObject).build();
//    }


    @GET
    @Path("all")
    @Produces({MediaType.APPLICATION_JSON})
    public Response listAll(@Context UriInfo uriInfo)  {
        List<T> list = getDao().findAllT();
        return Response.ok(JSONFormatter.toJSON(list)).build();
    }

    @GET
    @Path("paginate/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<T> listPaginate(@PathParam("page") Integer page, @Context UriInfo uriInfo) throws Exception {
        List<T> list = getDao().paginate(page+1);
        for (T entity : list) {
            if (entity != null)
                entity.addLink(getUriFor(uriInfo, entity), "self", "GET");
        }
        return list;
    }

    @GET
    @Path("page/{page}")
    @Produces(MediaType.APPLICATION_JSON)
    public T listPaginat3(@PathParam("page") Integer page, @Context UriInfo uriInfo) throws Exception {
        List<T> list = getDao().paginate3(page);

        T preEntity = list.get(0);
        T entity = list.get(1);
        T nextEntity = list.get(2);
        if (preEntity instanceof PanacheEntity)
            entity.addLink(getUriFor(uriInfo, preEntity), "prev", "GET");

        if (entity instanceof PanacheEntity)
            entity.addLink(getUriFor(uriInfo, entity), "self", "GET");

        if (nextEntity instanceof PanacheEntity)
            entity.addLink(getUriFor(uriInfo, nextEntity), "next", "GET");

//        for (T entity:list) {
//            if(entity instanceof AbstractEntity)
//                ((AbstractEntity) entity).addLink(getUriFor(uriInfo,entity),"self","GET");
//        }


        return entity;
    }

////    }

    @GET
    @Path("from/{start}/to/{max}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public List<T> findRange(@PathParam("start") Integer start, @PathParam("max") Integer maxResult, @Context UriInfo uriInfo) throws Exception {
        return getDao().findRange(start, maxResult);
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response create(T entity, @Context UriInfo uriInfo) throws Exception {//OK
        T result = getDao().create(entity);
        URI uri = uriInfo.getAbsolutePathBuilder()//http://localhost:8080/v1/api/test
                .path(result != null ? result.getId() + "" : "0")//Location →http://localhost:8080/v1/api/test/112
                .build();
        return Response.created(uri)
                .build();
    }

    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(T entity, @Context UriInfo uriInfo) throws Exception {//OK
        entity = getDao().edit(entity);
        URI uri = uriInfo.getAbsolutePathBuilder()//http://localhost:8080/v1/api/test
                .path(entity != null ? entity.getId() + "" : "0")//Location →http://localhost:8080/v1/api/test/112
                .build();
        return Response.noContent()
                .location(uri)
                .build();
        //        return Response.created(UriBuilder.fromResource(endPointClass).path(String.valueOf(entity.getId())).build()).build();
//        return Response.noContent().build(); //TODO SECURE
    }


    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response findById(@PathParam("id") ID id, @Context UriInfo uriInfo) throws Exception {//OK //http://localhost:8080/v1/api/mpc/5
        System.out.println("#findById: " + id + " ,");
        T entity = null;
        entity = getDao().findById(id);

        if (entity instanceof PanacheEntity)
            entity.addLink(getUriFor(uriInfo, entity), "self", "GET");
        return Response.ok(entity).build();
    }

    @GET
    @Path("count")
    @Produces(MediaType.TEXT_PLAIN)
    public Response count(@Context UriInfo uriInfo) throws Exception {
        Long result = 0L;
        try {
            result = getDao().count();
        } catch (NoResultException nre) {
            result = null;
        }
        if (result == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("is Empty").build();
        }
        return Response.ok(result).build();
    }

//    @PUT
//    @Path("/{id}")
//    @Produces(MediaType.APPLICATION_JSON)
//    @Consumes(MediaType.APPLICATION_JSON)
//    public Response updateById(@PathParam("id") String id, T entity ,@Context UriInfo uriInfo) throws Exception {//OK
//        T entity= getDao().findById(id);
//        entity = getDao().edit(entity);
//        //return Response.created(UriBuilder.fromResource(endPointClass).path(String.valueOf(entity.getId())).build()).build();
//        return Response.ok(entity).build();
//    }


    @DELETE
    @Path("{id}")
    public Response deleteById(@PathParam("id") Long id, @Context UriInfo uriInfo) throws Exception {//OK
        getDao().deleteById(id);
//        return Response.noContent().build();TODO SECURE
        return Response.status(Response.Status.GONE).build();//NOT SECURE
    }


    private String getUriFor(UriInfo uriInfo, T entity) throws Exception {
        String uri = uriInfo.getBaseUriBuilder()
                .path(getEndpoint())
                .path(entity.getId() + "")//TODO
                .build()
                .toString();
        return uri;
    }

}

