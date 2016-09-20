package com.epam.server.multitenancy;

import org.springframework.stereotype.Component;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.epam.common.constants.TenantConstants.H2_DEFAULT_SCHEMA_NAME;

@Component
public class H2MultiTenancyConnectionStrategy implements MultiTenancyConnectionStrategy {
    private static final String DEFAULT_DUMP_FILENAME = "h2_default.sql";
    private ReentrantReadWriteLock reentrantReadWriteLock = new ReentrantReadWriteLock();

    @Override
    public Connection setSchema(Connection connection, String tenantId) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            reentrantReadWriteLock.readLock().lock();
            try {
                statement.execute("SET SCHEMA " + tenantId);
            } finally {
                reentrantReadWriteLock.readLock().unlock();
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 90079) {
                reentrantReadWriteLock.writeLock().lock();
                try {
                    createSchema(connection, tenantId);
                } finally {
                    reentrantReadWriteLock.writeLock().unlock();
                }
            } else {
                throw e;
            }
        }
        return connection;
    }

    private void createSchema(Connection connection, String tenantId) throws SQLException {
        File file = new File(tenantId + ".sql");
        if (!file.exists()) {
            try (Statement statement = connection.createStatement()) {
                File genFile = generateSqlDump(statement);
                generateTenantBasedSqlDump(tenantId, file, genFile);
                statement.execute(String.format("CREATE SCHEMA IF NOT EXISTS %s;%s%s;%s'%s';",
                        tenantId,
                        "SET SCHEMA ",
                        tenantId,
                        "RUNSCRIPT FROM ",
                        file.getName()));
                file.deleteOnExit();
            }
        }
    }

    private void generateTenantBasedSqlDump(String tenantId, File file, File genFile) throws SQLException {
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(genFile));
             BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            String line, replacementSchema = tenantId + ".";
            while ((line = bufferedReader.readLine()) != null) {
                bufferedWriter.append(line.replaceAll(H2_DEFAULT_SCHEMA_NAME + "\\.", replacementSchema));
                bufferedWriter.newLine();
            }
        } catch (IOException e1) {
            throw new SQLException("Unable to generate schema");
        }
    }

    private File generateSqlDump(Statement statement) throws SQLException {
        File genFile = new File(DEFAULT_DUMP_FILENAME);
        if (!genFile.exists()) {
            statement.execute(String.format("SCRIPT TO '%s' SCHEMA \"%s\"", DEFAULT_DUMP_FILENAME, H2_DEFAULT_SCHEMA_NAME));
            genFile.deleteOnExit();
        }
        return genFile;
    }
}
