/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.servicerestful.service;

import com.creditcloud.servicerestful.domain.Customer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author sobranie
 */
public class CustomerResourceService implements CustomerResource {

    private final Map<Integer, Customer> customerDB = new ConcurrentHashMap<>();
    private final AtomicInteger idCounter = new AtomicInteger();

    @Override
    public Response createCustomer(InputStream is) {
        Customer customer = readCustomer(is);
        customer.setId(idCounter.incrementAndGet());
        System.out.println("Created customer is :" + customer.getId());
        return Response.created(URI.create("/customers/" + customer.getId())).build();
    }

    private Customer readCustomer(InputStream is) {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            Element root = doc.getDocumentElement();
            Customer cust = new Customer();
            if (root.getAttribute("id") != null
                    && !root.getAttribute("id").trim().equals("")) {
                cust.setId(Integer.valueOf(root.getAttribute("id")));
            }

            NodeList nodes = root.getChildNodes();
            for (int i = 0; i < nodes.getLength(); i++) {
                Element element = (Element) nodes.item(i);
                if (element.getTagName().equals("first-name")) {
                    cust.setFirstName(element.getTextContent());
                } else if (element.getTagName().equals("last-name")) {
                    cust.setLastName(element.getTextContent());
                } else if (element.getTagName().equals("street")) {
                    cust.setStreet(element.getTextContent());
                } else if (element.getTagName().equals("city")) {
                    cust.setCity(element.getTextContent());
                } else if (element.getTagName().equals("state")) {
                    cust.setState(element.getTextContent());
                } else if (element.getTagName().equals("zip")) {
                    cust.setZip(element.getTextContent());
                } else if (element.getTagName().equals("country")) {
                    cust.setCountry(element.getTextContent());
                }
            }
            return cust;
        } catch (Exception e) {
            throw new WebApplicationException(e, Response.Status.BAD_REQUEST);
        }

    }

    @Override
    public StreamingOutput getCustomer(@PathParam("id") int id) {
        final Customer customer = customerDB.get(id);
        if (customer == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        return new StreamingOutput() {
            @Override
            public void write(OutputStream output) throws IOException, WebApplicationException {
                outputCustomer(output, customer);
            }
        };
    }

    private void outputCustomer(OutputStream output, Customer customer) throws IOException {
        PrintStream writer = new PrintStream(output);
        writer.println("<customer id=\"" + customer.getId() + "\">");
        writer.println(" <first-name>" + customer.getFirstName()
                + "</first-name>");
        writer.println(" <last-name>" + customer.getLastName()
                + "</last-name>");
        writer.println("<street>" + customer.getStreet() + "</street>");
        writer.println("<city> " + customer.getCity() + "</city >");
        writer.println("<state>" + customer.getState() + "</state>");
        writer.println("zip>" + customer.getZip() + "</zip>");
        writer.println("<country>" + customer.getCountry() + "</country>");
    }

    @Override
    public void updateCustomer(@PathParam("id") int id, InputStream is) {
        Customer update = readCustomer(is);
        Customer current = customerDB.get(id);
        if (current == null) {
            throw new WebApplicationException(Response.Status.NOT_FOUND);
        }

        current.setFirstName(update.getFirstName());
        current.setLastName(update.getLastName());
        current.setStreet(update.getStreet());
        current.setState(update.getState());
        current.setZip(update.getZip());
        current.setCountry(update.getCountry());
    }

}
