
// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2017 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
 // end::copyright[]

// tag::SessionEndpoint[]
package io.openliberty.guides.sessions;

import java.util.ArrayList;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import javax.servlet.http.HttpSession;

@Path("/session")
public class SessionEndpoint {
	// tag::addToCart[]
	@GET
	@Path("/addToCart/{item}&{price}")
	@Produces(MediaType.TEXT_PLAIN)
	@APIResponse(responseCode = "200", description = "Execute to add an item to your cart.")
	@Operation(summary = "GET request to add an item to your cart.", description = "Adds an item to your cart.")
	public String addToCart(@Context HttpServletRequest request,
			@Parameter(description = "An item you need for intergalatic travel.", required = true)
			@PathParam("item") String item,
			@Parameter(description = "Price for this item.", required = true)
			@PathParam("price") double price) {

		HttpSession sess = request.getSession();
		sess.setAttribute(item, price);
		//sess.setMaxInactiveInterval(50);
		return item + " added to your cart and costs $" + price;
	}
	// end::addToCart[]
	// tag::getFromCart[]
	@GET
	@Path("/getFromCart")
	@Produces(MediaType.TEXT_PLAIN)
	@APIResponse(responseCode = "200", description = "Execute to request your cart details.")
	@Operation(summary = "GET request to view your cart")
	public String getFromCart(@Context HttpServletRequest request) {
		HttpSession sess = request.getSession();
		Enumeration<String> names = sess.getAttributeNames();
		ArrayList<String> results = new ArrayList<>();

		while(names.hasMoreElements()) {
			String name = names.nextElement();
			String price = sess.getAttribute(name).toString();
			results.add(name + " | $" + price);
		}
		//sess.invalidate();
		return results.toString();
	}
	// end::getFromCart[]
}
// end::SessionEndpoint[]
