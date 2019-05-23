// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2019 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.cart;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/")
public class CartResource {

    @POST
    // tag::endpointCartItemPrice[]
    @Path("cart/{item}&{price}")
    // end::endpointCartItemPrice[]
    @Produces(MediaType.TEXT_PLAIN)
    @APIResponse(responseCode = "200", description = "Item successfully added to cart.")
    @Operation(summary = "Add a new item to cart.")
    // tag::addToCart[]
    public String addToCart(@Context HttpServletRequest request,
                    @Parameter(description = "Item you need for intergalatic travel.",
                               required = true)
                    // tag::item[]
                    @PathParam("item") String item,
                    // end::item[]
                    @Parameter(description = "Price for this item.",
                               required = true)
                    // tag::price[]
                    @PathParam("price") double price) {
                    // end::price[]
        // tag::getSession[]
        HttpSession session = request.getSession();
        // end::getSession[]
        // tag::setAttribute[]
        session.setAttribute(item, price);
        // end::setAttribute[]
        return item + " added to your cart and costs $" + price;
    }
    // end::addToCart[]

    @GET
    // tag::endpointCart[]
    @Path("cart")
    // end::endpointCart[]
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponse(responseCode = "200",
        description = "Items successfully retrieved from your cart.")
    @Operation(summary = "Return an JsonObject instance which contains " +
                         "the items in your cart and the subtotal.")
    // tag::getCart[]
    public JsonObject getCart(@Context HttpServletRequest request) {
        HttpSession session = request.getSession();
        Enumeration<String> names = sess.getAttributeNames();
        JsonObjectBuilder builder = Json.createObjectBuilder();
        // tag::podname[]
        builder.add("pod-name", getHostname());
        // end::podname[]
        // tag::sessionid[]
        builder.add("session-id", session.getId());
        // end::sessionid[]
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        Double subtotal = 0.0;
        while (names.hasMoreElements()) {
            String name = names.nextElement();
            String price = session.getAttribute(name).toString();
            arrayBuilder.add(name + " | $" + price);
            subtotal += Double.valueOf(price).doubleValue();
        }
        // tag::cart[]
        builder.add("cart", arrayBuilder);
        // end::cart[]
        builder.add("subtotal", subtotal);
        return builder.build();
    }
    // end::getCart[]

    private String getHostname() {
        String hostname = System.getenv("HOSTNAME");
        if (hostname == null)
        	hostname = "localhost";
		    return hostname;
    }
}
