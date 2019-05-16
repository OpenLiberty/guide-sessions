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
package it.io.openliberty.guides.cart;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;

import org.apache.cxf.jaxrs.provider.jsrjsonp.JsrJsonpProvider;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CartSessionTest {
    private Client client;
    private static String serverport = System.getProperty("liberty.server.port");
    private static final String ITEM = "SpaceShip";
    private static final String PRICE = "20.0";
    private static final String POST = "POST";
    private static final String GET = "GET";

    @Before
    public void setup() {
        client = ClientBuilder.newClient();
        client.register(JsrJsonpProvider.class);
    }

    @After
    public void teardown() {
        client.close();
    }

    @Test
    public void testEmptyCart() {
        Response response = getResponse(GET, serverport, null);
        assertResponse(getURL(GET, serverport), response);

        JsonObject obj = response.readEntity(JsonObject.class);
        assertTrue("The cart should be empty on application start but was not",
                    obj.getJsonArray("cart").isEmpty());

        response.close();
    }

    @Test
    public void testOneServer() {
        Response addToCartResponse = getResponse(POST, serverport, null);
        assertResponse(getURL(POST, serverport), addToCartResponse);

        Map<String, NewCookie> cookies = addToCartResponse.getCookies();
        Cookie cookie = ((NewCookie) cookies.values().iterator().next()).toCookie();
        
        Response getCartResponse = getResponse(GET, serverport, cookie);
        assertResponse(getURL(POST, serverport), getCartResponse);
        
        String actualAddToCart = addToCartResponse.readEntity(String.class);
        String expectedAddToCart = ITEM + " added to your cart and costs $" + PRICE;

        JsonObject actualGetCart = getCartResponse.readEntity(JsonObject.class);
        String expectedGetCart =  ITEM + " | $" + PRICE;

        assertEquals("Adding item to cart response failed", expectedAddToCart,
            actualAddToCart);
        assertEquals("Cart response did not match expected string", expectedGetCart,
        	actualGetCart.getJsonArray("cart").getString(0));
        assertEquals("Cart response did not match expected subtotal",
        	actualGetCart.getJsonNumber("subtotal").doubleValue(), 20.0, 0.0);

        addToCartResponse.close();
        getCartResponse.close();
    }

    private Response getResponse(String method, String port, Cookie cookie) {
        Response result = null;
        String url = getURL(method, port);
        switch (method) {
        case POST:
            Form form = new Form().param(ITEM, PRICE);
            result = client.target(url).request().post(Entity.form(form));
            break;
        case GET:
        	WebTarget target = client.target(url);
        	Builder builder = target.request(MediaType.APPLICATION_JSON_TYPE);
            if (cookie == null) {
                result = builder.get(); 
            } else {
                result = builder.cookie(cookie).get();
            }
            break;
        }
        return result;
    }

    private String getURL(String method, String port) {
        String result = null;
        switch (method) {
        case POST:
            result = "http://localhost:" + port + "/SessionsGuide/cart/" + ITEM + "&"
                            + PRICE;
            break;
        case GET:
            result = "http://localhost:" + port + "/SessionsGuide/cart";
            break;
        }
        return result;
    }

    private void assertResponse(String url, Response response) {
        assertEquals("Incorrect response code from " + url, 200, response.getStatus());
    }
}
