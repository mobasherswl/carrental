package com.epam.server.multitenancy;

import java.sql.Connection;
import java.sql.SQLException;

public interface MultiTenancyConnectionStrategy {
    Connection setSchema(Connection connection, String tenantId) throws SQLException;
}
