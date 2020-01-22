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

package org.eclipse.jetty.util.log;

public class Red
{
    private static final Logger LOG = Log.getLogger(Red.class);

    public void generateLogs()
    {
        LOG.debug("My color is {}", Red.class.getSimpleName());
        LOG.info("I represent the emotion Love");
        LOG.warn("I can also mean Anger");
        LOG.ignore(new RuntimeException("Nom"));
    }
}
