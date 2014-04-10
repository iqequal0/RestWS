/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.creditcloud.shopws.config;

import com.creditcloud.shopws.service.CustomerResource;
import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.core.Application;

public class ShoppingApplication extends Application {

    private final Set<Object> singletons = new HashSet<>();
    private final Set<Class<?>> empty;

    public ShoppingApplication() {
        this.empty = new HashSet<>();
        singletons.add(new CustomerResource());
    }

    @Override
    public Set<Class<?>> getClasses() {
        return empty;
    }

    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
