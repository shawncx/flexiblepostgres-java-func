package com.function;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;

/**
 * Azure Functions with HTTP Trigger.
 */
public class Function {
    /**
     * This function listens at endpoint "/api/HttpExample". Two ways to invoke it using "curl" command in bash:
     * 1. curl -d "HTTP Body" {your host}/api/HttpExample
     * 2. curl "{your host}/api/HttpExample?name=HTTP%20Query"
     */
    @FunctionName("FlexiblePostgresHttpExample")
    public HttpResponseMessage run(
            @HttpTrigger(
                name = "req",
                methods = {HttpMethod.GET, HttpMethod.POST},
                authLevel = AuthorizationLevel.ANONYMOUS)
                HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {
        context.getLogger().info("Java HTTP trigger processed a request.");

        try {
            Date date = null;
            String connStr = System.getenv("POSTGRESQLCONNSTR_RESOURCECONNECTOR_TESTJAVAFUNCSECRETCONN_CONNECTIONSTRING");
            Connection conn = DriverManager.getConnection(connStr);
            PreparedStatement stat = conn.prepareStatement("SELECT NOW();");
            ResultSet resultSet = stat.executeQuery();
            resultSet.next();
            date = resultSet.getDate(1);
            return request.createResponseBuilder(HttpStatus.OK).body("Time from db: " + date.toString()).build();
        } catch (SQLException e) {
            return request.createResponseBuilder(HttpStatus.OK).body("Error, " + e).build();
        }
    }
}
