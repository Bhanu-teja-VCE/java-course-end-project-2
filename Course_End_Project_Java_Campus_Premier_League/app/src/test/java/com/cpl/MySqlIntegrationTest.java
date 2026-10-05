package com.cpl;

import com.cpl.db.Database;
import com.cpl.db.DbConfig;
import com.cpl.db.MySqlTournamentRepository;
import com.cpl.model.Team;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MySqlIntegrationTest {

    private static DbConfig testConfig;
    private static boolean mysqlReachable = false;

    @BeforeAll
    static void checkDatabaseReachability() {
        testConfig = new DbConfig("localhost", 3306, "campus_premier_league", "root", "root123");
        mysqlReachable = Database.testConnection(testConfig);
    }

    @Test
    void testMySqlConnectionAndQuery() throws Exception {
        Assumptions.assumeTrue(mysqlReachable, "Skipping MySQL integration test because MySQL server is offline");

        try (Connection conn = Database.getConnection(testConfig)) {
            assertNotNull(conn);
            assertFalse(conn.isClosed());
        }

        MySqlTournamentRepository repo = new MySqlTournamentRepository();
        List<Team> teams = repo.loadTeams();
        assertNotNull(teams);
    }
}
