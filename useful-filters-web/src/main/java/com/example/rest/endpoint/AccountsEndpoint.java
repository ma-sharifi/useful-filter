package com.example.rest.endpoint;

import com.example.dao.panache.AbstractDaoFacade;
import com.example.dao.panache.PanacheQuery;
import com.example.dto.ResponseDto;
import com.example.panache.common.Sort;
import com.example.panache.dao.AccountDao;
import com.example.panache.entity.Account;
import com.example.serializer.JSONFormatter;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author Mahdi Sharifi
 * @version 1.0.0
 * https://www.linkedin.com/in/mahdisharifi/
 * @since 17/11/2019
 */
@Path("accounts")
public class AccountsEndpoint extends AbstractRestFacade<Account, String> {

    // ======================================
    // =          Injection Points          =
    // ======================================

    @Inject
    private AccountDao accountDao;

    public AccountsEndpoint() {
        super(Account.class);
    }

    @GET
    @Path("test")
    @Produces(MediaType.TEXT_PLAIN)
    public Response test(){
        Set<String> treeSet = new TreeSet<>(Comparator.comparing(String::length));
        Set<String> syncTreeSet = Collections.synchronizedSet(treeSet);
        Map map=new TreeMap();


//        System.out.println("#account_no: "+accountDao.find("account_no","1"));
        return  Response.ok(JSONFormatter.toJSON(accountDao.findAllT(Sort.by("c.accountNo").descending()))).build();
    }

    //
//    @POST
//    @Path("{account-no}/to-account/{destination-account-no}/transfer")
//    @AllowedAmountNeeded(amountLimit = 10, timeWindow = 20, timeUnit = TimeUnit.SECONDS, parameterName = "account-no", parameterType = ParameterType.PATH_PARAM)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response test(@NotNull @HeaderParam("Amount") String amount) {
//        Response.ResponseBuilder builder = Response.status(Response.Status.OK);
//        return Response.ok("{\"name\":1}").build();
//    }
//
//    @POST
//    @Path("{account-no}/to-account/{destination-account-no}/transfer2")
//    @AllowedAmountNeeded(amountLimit = 20, timeWindow = 50, timeUnit = TimeUnit.SECONDS, parameterName = "account-no", parameterType = ParameterType.PATH_PARAM)
//    @Produces(MediaType.APPLICATION_JSON)
//    public Response test2(@NotNull @HeaderParam("Amount") String amount) {
//        Response.ResponseBuilder builder = Response.status(Response.Status.OK);
//        return Response.ok("{\"name\":1}").build();
//    }

    @Override
    protected AbstractDaoFacade<Account, String> getDao() {
        return accountDao;
    }

    @Override
    protected Class getEndpoint() {
        return AccountsEndpoint.class;
    }
}

