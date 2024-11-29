//
// ========================================================================
// Copyright (c) 1995 Mort Bay Consulting Pty Ltd and others.
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License v. 2.0 which is available at
// https://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
// ========================================================================
//

package org.eclipse.jetty.http3.client.transport;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.transport.HttpClientConnectionFactory;
import org.eclipse.jetty.client.transport.HttpClientTransportDynamic;
import org.eclipse.jetty.http3.client.HTTP3Client;
import org.eclipse.jetty.http3.client.HTTP3ClientConnectionFactory;
import org.eclipse.jetty.http3.client.transport.internal.SessionClientListener;
import org.eclipse.jetty.io.ClientConnectionFactory;
import org.eclipse.jetty.io.EndPoint;
import org.eclipse.jetty.io.Transport;
import org.eclipse.jetty.quic.client.QuicTransport;
import org.eclipse.jetty.quic.common.ProtocolSession;
import org.eclipse.jetty.quic.common.QuicSession;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.thread.Invocable;

public class ClientConnectionFactoryOverHTTP3 extends ContainerLifeCycle implements ClientConnectionFactory, HttpClient.Aware, Invocable
{
    private final HTTP3ClientConnectionFactory factory = new HTTP3ClientConnectionFactory();
    private final HTTP3Client http3Client;
    private InvocationType invocationType = InvocationType.BLOCKING;

    public ClientConnectionFactoryOverHTTP3(HTTP3Client http3Client)
    {
        this.http3Client = http3Client;
        installBean(http3Client);
    }

    @Override
    public void setHttpClient(HttpClient httpClient)
    {
        HttpClientTransportOverHTTP3.configure(httpClient, http3Client);
    }

    @Override
    public org.eclipse.jetty.io.Connection newConnection(EndPoint endPoint, Map<String, Object> context)
    {
        return factory.newConnection(endPoint, context);
    }

    @Override
    public InvocationType getInvocationType()
    {
        return invocationType;
    }

    public void setInvocationType(InvocationType invocationType)
    {
        this.invocationType = Objects.requireNonNull(invocationType);
    }

    /**
     * <p>Representation of the {@code HTTP/3} application protocol used by {@link HttpClientTransportDynamic}.</p>
     *
     * @see HttpClientConnectionFactory#HTTP11
     */
    public static class HTTP3 extends Info implements ProtocolSession.Factory
    {
        private static final List<String> protocols = List.of("h3");

        public HTTP3(HTTP3Client client)
        {
            this(new ClientConnectionFactoryOverHTTP3(client));
        }

        public HTTP3(ClientConnectionFactoryOverHTTP3 connectionFactory)
        {
            super(connectionFactory);
        }

        @Override
        public List<String> getProtocols(boolean secure)
        {
            return protocols;
        }

        @Override
        public Transport newTransport()
        {
            ClientConnectionFactoryOverHTTP3 http3 = (ClientConnectionFactoryOverHTTP3)getClientConnectionFactory();
            return new QuicTransport(http3.http3Client.getQuicConfiguration());
        }

        @Override
        public ProtocolSession newProtocolSession(QuicSession quicSession, Map<String, Object> context)
        {
            ClientConnectionFactoryOverHTTP3 http3 = (ClientConnectionFactoryOverHTTP3)getClientConnectionFactory();
            context.put(HTTP3Client.CLIENT_CONTEXT_KEY, http3.http3Client);
            SessionClientListener listener = new SessionClientListener(context);
            context.put(HTTP3Client.SESSION_LISTENER_CONTEXT_KEY, listener);
            return http3.factory.newProtocolSession(quicSession, context);
        }

        @Override
        public String toString()
        {
            return String.format("%s@%x", getClass().getSimpleName(), hashCode());
        }
    }
}
