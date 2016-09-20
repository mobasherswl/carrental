package com.epam.server.threadlocal;

import static com.epam.common.constants.TenantConstants.H2_DEFAULT_SCHEMA_NAME;

public final class TenantIdThreadLocal {

    private static final ThreadLocal<String> tenantId = ThreadLocal.withInitial(() -> H2_DEFAULT_SCHEMA_NAME);

    private TenantIdThreadLocal() {
        new RuntimeException();
    }

    public static ThreadLocal<String> getInstance() {
        return tenantId;
    }
}
