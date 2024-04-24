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

package org.eclipse.jetty.quic.quiche.foreign;

import java.lang.foreign.MemoryLayout;

import static org.eclipse.jetty.quic.quiche.foreign.NativeHelper.C_LONG;

public class timespec
{
    private static final MemoryLayout LAYOUT = MemoryLayout.structLayout(
        C_LONG.withName("tv_sec"),
        C_LONG.withName("tv_nsec")
    );

    public static MemoryLayout layout()
    {
        return LAYOUT;
    }
}

