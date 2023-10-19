package com.setianjay.database;

import com.setianjay.database.entity.Customer;
import com.setianjay.database.entity.User;
import com.setianjay.database.util.ConnectionUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Implement Statement test to know what different between {@link Statement} and {@link PreparedStatement}.
 * <br />
 * <br />
 * {@link Statement} are vulnerable to sql injection, so use them for queries that do not require parameters.
 * <br />
 * <br />
 * {@link PreparedStatement} is an instance of Statement where it comes with sql injection prevention. so use
 * PreparedStatement for queries that require user parameters.
 */
@TestMethodOrder(value = MethodOrderer.OrderAnnotation.class)
class StatementTest {

    /**
     * This test will insert some {@link Customer} into table customer.
     * */
    @Test
    @Order(value = 1)
    void testInsertStatement() {
        try (Connection connection = ConnectionUtil.getHikariDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            String insertCustomers = """
                    INSERT INTO customer(id, name, email) VALUES ('CST-001', 'Hari Setiaji', 'hari.setiaji@gmail.com'),
                    ('CST-002', 'Gurindo Sekti', 'gurindo.sekti@gmail.com'),
                    ('CST-003', 'Setyarto', 'setyarto@gmail.com');
                    """;

            int rowAffected = statement.executeUpdate(insertCustomers);
            assertEquals(3, rowAffected);

        } catch (SQLException exception) {
            exception.printStackTrace();
            fail(exception);
        } finally {
            ConnectionUtil.close();
        }
    }

    /**
     * This test will update {@link Customer} data based on its id.
     * */
    @Test
    @Order(value = 2)
    void testUpdateStatement() {
        String updateOneCustomer = " UPDATE customer SET name = ? WHERE id = ?;";

        try (Connection connection = ConnectionUtil.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(updateOneCustomer)) {
            statement.setString(1, "Hari Setiaji S. Kom.");
            statement.setString(2, "CST-001");

            int rowAffected = statement.executeUpdate();
            assertEquals(1, rowAffected);
        } catch (SQLException exception) {
            exception.printStackTrace();
            fail(exception);
        } finally {
            ConnectionUtil.close();
        }
    }

    /**
     * This test will delete {@link Customer} data based on its id.
     * */
    @Test
    @Order(value = 3)
    void testDeleteStatement() {
        String deleteOneCustomer = "DELETE FROM customer WHERE id = ?;";

        try (Connection connection = ConnectionUtil.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(deleteOneCustomer)) {
            statement.setString(1, "CST-003");

            int rowAffected = statement.executeUpdate();
            assertEquals(1, rowAffected);
        } catch (SQLException exception) {
            exception.printStackTrace();
            fail(exception);
        } finally {
            ConnectionUtil.close();
        }
    }

    /**
     * This test will read all {@link Customer} data in table customer.
     * */
    @Test
    @Order(value = 4)
    void testSelectAllStatement() {
        try (Connection connection = ConnectionUtil.getHikariDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            String selectAllCustomer = "SELECT * FROM customer";

            ResultSet dataRows = statement.executeQuery(selectAllCustomer);
            List<Customer> customers = new ArrayList<>();

            while (dataRows.next()) {
                Customer customer = new Customer(
                        dataRows.getString("id"),
                        dataRows.getString("name"),
                        dataRows.getString("email")
                );

                customers.add(customer);
            }

            System.out.println(customers);
            assertEquals(2, customers.size());
        } catch (SQLException exception) {
            exception.printStackTrace();
            fail(exception);
        } finally {
            ConnectionUtil.close();
        }
    }

    /**
     * This test will select {@link Customer} data based on its id.
     * */
    @Test
    @Order(value = 5)
    void testSelectByIdStatement() {
        String selectCustomerById = "SELECT * FROM customer WHERE id = ?";
        try (Connection connection = ConnectionUtil.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(selectCustomerById)) {

            statement.setString(1, "CST-002");
            ResultSet dataRow = statement.executeQuery();
            if (dataRow.next()) {
                Customer customer = new Customer(
                        dataRow.getString("id"),
                        dataRow.getString("name"),
                        dataRow.getString("email")
                );
                System.out.println(customer);
                assertEquals("CST-002", customer.id());
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            fail(exception);
        } finally {
            ConnectionUtil.close();
        }
    }

    /**
     * This login test will run successfully even if the username and password submitted are incorrect, this is because
     * {@link Statement} cannot prevent sql injection.
     * <br />
     * username = admin; # is the cause, so the query that will be executed later will change
     * <br />
     * <br />
     * FROM THIS: <br />
     * "SELECT * FROM user WHERE username = 'admin' AND password = 'admin';" <br /> <br />
     * TO THIS: <br />
     * "SELECT * FROM user WHERE username = 'admin'; #' AND PASSWORD = 'wrong password';
     * <br />
     * <br />
     * The # sign in the given username will make the query after the # sign as a comment and sql will ignore it, hence
     * this test will be successful for sql injection.
     * -----------------------------------------------------------------------------------------------------------------
     * Note: to prevent that, use {@link PreparedStatement} instead of using {@link Statement}.
     */
    @Test
    @Order(value = 6)
    void testSuccessfullySqlInjectionWithStatementInLoginCase() {
        String username = "admin'; #";
        String password = "wrong password";

        try (Connection connection = ConnectionUtil.getHikariDataSource().getConnection();
             Statement statement = connection.createStatement()) {
            String queryLogin = "SELECT * FROM user WHERE username ='" + username + "' AND password = '" + password + "';";
            System.out.println(queryLogin);
            ResultSet dataRow = statement.executeQuery(queryLogin);
            if (dataRow.next()) {
                System.out.println("Welcome " + username);
                User user = new User(dataRow.getString("username"), dataRow.getString("password"));
                assertNotEquals(username, user.username());
                assertNotEquals(password, user.password());
            } else {
                System.out.println("Login failed, please enter correct username and password");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            fail(exception);
        }
    }

    @Test
    @Order(value = 7)
    void testPreventSqlInjectionWithPreparedStatementInLoginCase() {
        String username = "admin'; #";
        String password = "wrong password";
        String queryLogin = "SELECT * FROM user WHERE username = ? AND password = ?";

        try (Connection connection = ConnectionUtil.getHikariDataSource().getConnection();
             PreparedStatement statement = connection.prepareStatement(queryLogin)) {

            statement.setString(1, username);
            statement.setString(2, password);

            ResultSet dataRow = statement.executeQuery();

            if (dataRow.next()) {
                System.out.println("Welcome " + username);
                User user = new User(dataRow.getString("username"), dataRow.getString("password"));
                assertEquals(username, user.username());
                assertEquals(password, user.password());
            } else {
                System.out.println("Login failed, please enter correct username and password");
            }
        } catch (SQLException exception) {
            exception.printStackTrace();
            fail(exception);
        }
    }
}
