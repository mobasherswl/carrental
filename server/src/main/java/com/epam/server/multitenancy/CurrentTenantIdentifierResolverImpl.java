package com.epam.server.multitenancy;

import com.epam.server.threadlocal.TenantIdThreadLocal;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {
    @Override
    public String resolveCurrentTenantIdentifier() {
        return TenantIdThreadLocal.getInstance().get();
    }

    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
