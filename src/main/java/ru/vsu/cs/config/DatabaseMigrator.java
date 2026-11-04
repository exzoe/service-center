package ru.vsu.cs.config;

import org.flywaydb.core.Flyway;

public final class DatabaseMigrator {
    private static final String MIGRATIONS_LOCATION = "classpath:db/migration";

    private DatabaseMigrator() {
    }

    public static void migrate(DatabaseConfig config) {
        Flyway.configure()
                .dataSource(config.url(), config.user(), config.password())
                .locations(MIGRATIONS_LOCATION)
                // существующая схема проекта уже соответствует первой миграции
                .baselineOnMigrate(true)
                .baselineVersion("1")
                .load()
                .migrate();
    }
}
