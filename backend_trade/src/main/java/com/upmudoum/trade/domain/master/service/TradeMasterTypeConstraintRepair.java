package com.upmudoum.trade.domain.master.service;

import java.sql.DatabaseMetaData;
import java.sql.Connection;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class TradeMasterTypeConstraintRepair implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;
    private final DataSource dataSource;
    private final String schema;

    public TradeMasterTypeConstraintRepair(
            JdbcTemplate jdbcTemplate,
            DataSource dataSource,
            @Value("${spring.jpa.properties.hibernate.default_schema:trade_service}") String schema
    ) {
        this.jdbcTemplate = jdbcTemplate;
        this.dataSource = dataSource;
        this.schema = schema;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            if (!metaData.getDatabaseProductName().toLowerCase().contains("postgresql")) {
                return;
            }
        }
        dropMasterTypeCheckConstraints();
    }

    private void dropMasterTypeCheckConstraints() {
        String sql = """
                select n.nspname as schema_name, c.relname as table_name, con.conname as constraint_name
                from pg_constraint con
                join pg_class c on c.oid = con.conrelid
                join pg_namespace n on n.oid = c.relnamespace
                where con.contype = 'c'
                  and n.nspname = ?
                  and c.relname in (
                      'item_master',
                      'market_ranking_snapshot',
                      'trade_master_import_history',
                      'trade_master_import_lock'
                  )
                  and pg_get_constraintdef(con.oid) like '%master_type%'
                """;
        List<ConstraintRef> constraints = jdbcTemplate.query(sql, (rs, rowNum) -> new ConstraintRef(
                rs.getString("schema_name"),
                rs.getString("table_name"),
                rs.getString("constraint_name")
        ), schema);
        for (ConstraintRef constraint : constraints) {
            dropConstraint(constraint);
        }
    }

    private void dropConstraint(ConstraintRef constraint) {
        jdbcTemplate.execute("alter table "
                + identifier(constraint.schemaName()) + "." + identifier(constraint.tableName())
                + " drop constraint if exists " + identifier(constraint.constraintName()));
    }

    private String identifier(String value) {
        if (value == null || !value.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("invalid database identifier: " + value);
        }
        return "\"" + value + "\"";
    }

    private static class ConstraintRef {

        private final String schemaName;
        private final String tableName;
        private final String constraintName;

        ConstraintRef(String schemaName, String tableName, String constraintName) {
            this.schemaName = schemaName;
            this.tableName = tableName;
            this.constraintName = constraintName;
        }

        String schemaName() {
            return schemaName;
        }

        String tableName() {
            return tableName;
        }

        String constraintName() {
            return constraintName;
        }
    }
}
