//
//  ========================================================================
//  Copyright (c) 1995-2012 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

/*
 * Copyright (c) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.eclipse.jetty.util;

/**
 * <p>A callback abstraction that handles completed/failed events of asynchronous operations.</p>
 *
 * @param <C> the type of the context object
 */
public interface Callback<C>
{
    /**
     * <p>Callback invoked when the operation completes.</p>
     *
     * @param context the context
     * @see #failed(Object, Throwable)
     */
    public abstract void completed(C context);

    /**
     * <p>Callback invoked when the operation fails.</p>
     *
     * @param context the context
     * @param x the reason for the operation failure
     */
    public void failed(C context, Throwable x);

    /**
     * <p>Empty implementation of {@link Callback}</p>
     *
     * @param <C> the type of the context object
     */
    public static class Empty<C> implements Callback<C>
    {
        @Override
        public void completed(C context)
        {
        }

        @Override
        public void failed(C context, Throwable x)
        {
            throw new RuntimeException(x);
        }
    }
}
