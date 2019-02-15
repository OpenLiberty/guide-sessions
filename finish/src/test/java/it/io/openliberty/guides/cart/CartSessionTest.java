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

import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class CartSessionTest {
    private Client client;
    private static String server1port = System.getProperty("liberty.server1.port");
    private static String server2port = System.getProperty("liberty.server2.port");
    private static final String ITEM = "SpaceShip";
    private static final String PRICE = "20.0";
    private static final String POST = "POST";
    private static final String GET = "GET";

    @Before
    public void setup() {
        client = ClientBuilder.newClient();
    }

    @After
    public void teardown() {
        client.close();
    }

    @Test
    public void testEmptyCart() {
        Response response = getResponse(GET, server1port, null);
        assertResponse(getURL(GET, server1port), response);

        String actual = response.readEntity(String.class);
        String expected = "[]";

        assertEquals("The cart should be empty on application start but was not",
                     expected, actual);

        response.close();
    }

    @Test
    public void testOneServer() {
        Response addToCartResponse = getResponse(POST, server1port, null);
        assertResponse(getURL(POST, server1port), addToCartResponse);

        Map<String, NewCookie> cookies = addToCartResponse.getCookies();
        Cookie cookie = ((NewCookie) cookies.values().iterator().next()).toCookie();
        Response getCartResponse = getResponse(GET, server1port, cookie);

        String actualAddToCart = addToCartResponse.readEntity(String.class);
        String expectedAddToCart = ITEM + " added to your cart and costs $" + PRICE;

        String actualGetCart = getCartResponse.readEntity(String.class);
        String expectedGetCart = "[" + ITEM + " | $" + PRICE + "]";

        assertEquals("Adding item to cart response failed", expectedAddToCart,
                     actualAddToCart);
        assertEquals("Cart response did not match expected string", expectedGetCart,
                     actualGetCart);

        addToCartResponse.close();
        getCartResponse.close();
    }

    @Test
    public void testTwoServers() throws Exception {
        Response addToCartResponse = getResponse(POST, server1port, null);
        assertResponse(getURL(POST, server1port), addToCartResponse);

        Map<String, NewCookie> cookies = addToCartResponse.getCookies();
        Cookie cookie = ((NewCookie) cookies.values().iterator().next()).toCookie();
        Response getCartResponse = getResponse(GET, server2port, cookie);

        String actualAddToCart = addToCartResponse.readEntity(String.class);
        String expectedAddToCart = ITEM + " added to your cart and costs $" + PRICE;

        String actualGetCart = getCartResponse.readEntity(String.class);
        String expectedGetCart = "[" + ITEM + " | $" + PRICE + "]";

        assertEquals("Adding item to cart response failed", expectedAddToCart,
                     actualAddToCart);
        assertEquals("Cart response did not match expected string", expectedGetCart,
                     actualGetCart);

        addToCartResponse.close();
        getCartResponse.close();
    }

    // tag::comment[]
    /**
     * Get response from server using the following configuration
     *
     * @param method
     *            GET or POST request
     * @param port
     *            for HTTP communication with server
     * @param cookie
     *            (OPTIONAL) provides identification to get session data
     * @return Response
     */
    // end::comment[]
    private Response getResponse(String method, String port, Cookie cookie) {
        Response result = null;
        switch (method) {
        case POST:
            Form form = new Form().param(ITEM, PRICE);
            result = client.target(getURL(method, port)).request()
                           .post(Entity.form(form));
            break;
        case GET:
            if (cookie == null) {
                result = client.target(getURL(method, port)).request().get();
            } else {
                result = client.target(getURL(method, port)).request().cookie(cookie)
                               .get();
            }
            break;
        }
        return result;
    }

    // tag::comment[]
    /**
     * Construct and return URL for requests
     *
     * @param method
     *            GET or POST request
     * @param port
     *            for HTTP communication with server
     * @return URL as a String
     */
    // end::comment[]
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
