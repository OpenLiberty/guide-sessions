
// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2018 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
 // end::copyright[]
package io.openliberty.guides.sessions;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/")
public class SessionEndpoint {
	// tag::addToCart[]
	@POST
	@Path("cart/{item}&{price}")
	@Produces(MediaType.TEXT_PLAIN)
	@APIResponse(responseCode = "200", description = "Item successfully added to cart.")
	@Operation(summary = "Add a new item to cart.")
	public String addToCart(@Context HttpServletRequest request,
			@Parameter(description = "Item you need for intergalatic travel.", required = true)
			@PathParam("item") String item,
			@Parameter(description = "Price for this item.", required = true)
			@PathParam("price") double price) {

		HttpSession sess = request.getSession();
		sess.setAttribute(item, price);
		return item + " added to your cart and costs $" + price;
	}
	// end::addToCart[]
	// tag::getCart[]
	@GET
	@Path("cart")
	@Produces(MediaType.TEXT_PLAIN)
	@APIResponse(responseCode = "200", description = "Item successfully retrieved from cart.")
	@Operation(summary = "Return list of items in your cart.")
	public String getCart(@Context HttpServletRequest request) {
		HttpSession sess = request.getSession();
		Enumeration<String> names = sess.getAttributeNames();
		ArrayList<String> results = new ArrayList<>();

		while(names.hasMoreElements()) {
			String name = names.nextElement();
			String price = sess.getAttribute(name).toString();
			results.add(name + " | $" + price);
		}
		return results.toString();
	}
	// end::getCart[]
}
