package com.example.dao.panache.transients;

import lombok.Data;

/**
 * The href element contains the complete target URL, or link,
 * to use in combination with the HTTP method to make the related call.
 * href is the key HATEOAS component that links a completed call with a subsequent call.
 */
@Data
public class Link {
    //https://developer.paypal.com/docs/api/hateoas-links/
    private String href;//The target URL.
    private String rel;//The link relationship type.
    private String type;
    private String hreflang;
    private String method;//The HTTP method. Default is GET.

    public Link() {
    }

    public Link(String href, String method) {
        this.href = href;
        this.method = method;
    }

}