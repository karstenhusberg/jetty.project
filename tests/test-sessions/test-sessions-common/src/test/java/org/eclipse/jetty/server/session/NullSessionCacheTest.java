//
// ========================================================================
// Copyright (c) 1995-2020 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under
// the terms of the Eclipse Public License 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0
//
// This Source Code may also be made available under the following
// Secondary Licenses when the conditions for such availability set
// forth in the Eclipse Public License, v. 2.0 are satisfied:
// the Apache License v2.0 which is available at
// https://www.apache.org/licenses/LICENSE-2.0
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.server.session;

import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * NullSessionCacheTest
 */
public class NullSessionCacheTest extends AbstractSessionCacheTest
{    
    @Override
    public AbstractSessionCacheFactory newSessionCacheFactory(int evictionPolicy, boolean saveOnCreate,
                                                              boolean saveOnInactiveEvict, boolean removeUnloadableSessions,
                                                              boolean flushOnResponseCommit)
    {
        NullSessionCacheFactory factory = new NullSessionCacheFactory();
        factory.setSaveOnCreate(saveOnCreate);
        factory.setRemoveUnloadableSessions(removeUnloadableSessions);
        factory.setFlushOnResponseCommit(flushOnResponseCommit);
        return factory;
    }
    
    @Test
    public void testShutdownWithSessionStore()
        throws Exception
    {
        Server server = new Server();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/test");
        context.setServer(server);

        AbstractSessionCacheFactory cacheFactory = newSessionCacheFactory(SessionCache.NEVER_EVICT, false, false, false, false);
        SessionCache cache = cacheFactory.getSessionCache(context.getSessionHandler());

        TestSessionDataStore store = new TestSessionDataStore(true);//fake passivation
        cache.setSessionDataStore(store);
        context.getSessionHandler().setSessionCache(cache);

        context.start();

        //put a session in the cache and store
        long now = System.currentTimeMillis();
        SessionData data = store.newSessionData("1234", now - 20, now - 10, now - 20, TimeUnit.MINUTES.toMillis(10));
        Session session = cache.newSession(data);
        TestSessionActivationListener listener = new TestSessionActivationListener();
        cache.add("1234", session);
        //cache never contains the session
        assertFalse(cache.contains("1234"));
        session.setAttribute("aaa", listener);
        //write session out on release
        cache.release("1234", session);
        assertEquals(1, store._numSaves.get());
        assertEquals(1, listener.passivateCalls);
        assertEquals(0, listener.activateCalls); //NullSessionCache always evicts on release, so never reactivates

        assertTrue(store.exists("1234"));
        //cache never contains session
        assertFalse(cache.contains("1234"));

        context.stop(); //calls shutdown

        //session should still exist in store
        assertTrue(store.exists("1234"));
        //cache never contains the session
        assertFalse(cache.contains("1234"));
        //shutdown does not save session
        assertEquals(1, listener.passivateCalls);
        assertEquals(0, listener.activateCalls);
    }
    
    @Test
    public void testNotCached() throws Exception
    {
        //Test the NullSessionCache never contains the session
        Server server = new Server();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/test");
        context.setServer(server);

        NullSessionCacheFactory cacheFactory = new NullSessionCacheFactory();

        NullSessionCache cache = (NullSessionCache)cacheFactory.getSessionCache(context.getSessionHandler());

        TestSessionDataStore store = new TestSessionDataStore();
        cache.setSessionDataStore(store);
        context.getSessionHandler().setSessionCache(cache);
        context.start();

        //make a session
        long now = System.currentTimeMillis();
        SessionData data = store.newSessionData("1234", now - 20, now - 10, now - 20, TimeUnit.MINUTES.toMillis(10));
        data.setExpiry(now + TimeUnit.DAYS.toMillis(1));
        Session session = cache.newSession(null, data); //mimic a request making a session
        cache.add("1234", session); 
        assertFalse(cache.contains("1234"));//null cache doesn't actually retain the session
        
        //mimic releasing the session after the request is finished
        cache.release("1234", session);
        assertTrue(store.exists("1234"));
        assertFalse(cache.contains("1234"));

        //simulate a new request using the previously created session
        session = cache.get("1234"); //get the session again
        session.access(now); //simulate a request
        cache.release("1234", session); //finish with the session
        assertFalse(cache.contains("1234"));
        assertFalse(session.isResident());
    }
    
    /**
     * Test contains method.
     */
    @Test
    public void testContains()
        throws Exception
    {
        Server server = new Server();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/test");
        context.setServer(server);

        SessionCacheFactory cacheFactory = newSessionCacheFactory(SessionCache.NEVER_EVICT, false, false, false, false);
        SessionCache cache = (SessionCache)cacheFactory.getSessionCache(context.getSessionHandler());

        TestSessionDataStore store = new TestSessionDataStore();
        cache.setSessionDataStore(store);
        context.getSessionHandler().setSessionCache(cache);
        context.start();

        //test one that doesn't exist
        assertFalse(cache.contains("1234"));

        //test one that exists
        long now = System.currentTimeMillis();
        SessionData data = store.newSessionData("1234", now - 20, now - 10, now - 20, TimeUnit.MINUTES.toMillis(10));
        Session session = cache.newSession(data);
        cache.add("1234", session);
        assertFalse(cache.contains("1234"));
    }
    
    /**
     * Test the exist method.
     */
    @Test
    public void testExists()
        throws Exception
    {
        Server server = new Server();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/test");
        context.setServer(server);

        SessionCacheFactory cacheFactory = newSessionCacheFactory(SessionCache.NEVER_EVICT, false, false, false, false);
        SessionCache cache = (SessionCache)cacheFactory.getSessionCache(context.getSessionHandler());

        TestSessionDataStore store = new TestSessionDataStore();
        cache.setSessionDataStore(store);
        context.getSessionHandler().setSessionCache(cache);
        context.start();

        //test one that doesn't exist anywhere at all
        assertFalse(cache.exists("1234"));

        //test one that only exists in the store
        long now = System.currentTimeMillis();
        SessionData data = store.newSessionData("1234", now - 20, now - 10, now - 20, TimeUnit.MINUTES.toMillis(10));
        store.store("1234", data);
        assertTrue(cache.exists("1234"));
    }
    
    /**
     * Test the delete method.
     */
    @Test
    public void testDelete()
        throws Exception
    {
        Server server = new Server();

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/test");
        context.setServer(server);

        SessionCacheFactory cacheFactory = newSessionCacheFactory(SessionCache.NEVER_EVICT, true, false, false, false);
        SessionCache cache = cacheFactory.getSessionCache(context.getSessionHandler());

        TestSessionDataStore store = new TestSessionDataStore();
        cache.setSessionDataStore(store);
        context.getSessionHandler().setSessionCache(cache);
        context.start();

        //test remove non-existent session
        Session session = cache.delete("1234");
        assertNull(session);

        //test remove of existing session in store only
        long now = System.currentTimeMillis();
        SessionData data = store.newSessionData("1234", now - 20, now - 10, now - 20, TimeUnit.MINUTES.toMillis(10));
        store.store("1234", data);
        session = cache.delete("1234");
        assertNull(session); //NullSessionCache never returns the session that was removed from the cache because it was never in the cache!
        assertFalse(store.exists("1234"));
        assertFalse(cache.contains("1234"));
    }
}
