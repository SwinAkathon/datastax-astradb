package dev.astradb.firstapp;

import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.stargate.grpc.StargateBearerToken;
import io.stargate.proto.QueryOuterClass;
import io.stargate.proto.QueryOuterClass.Row;
import io.stargate.proto.StargateGrpc;

/**
 * @overview Demonstrate how to connect to AstraDB using the cloud-native
 *           (recommended) coding style.
 * 
 *           <p>
 *           Dependencies: <code>
 *  <dependencies>
      <dependency>
        <groupId>io.stargate.grpc</groupId>
        <artifactId>grpc-proto</artifactId>
        <version>1.0.41</version>
      </dependency>
      <dependency>
        <groupId>io.grpc</groupId>
        <artifactId>grpc-netty-shaded</artifactId>
        <version>1.41.0</version>
      </dependency>
    </dependencies>
 *  </code>
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class FirstAppCloudNative {

  // obtained from the list of Active Databases in the Dashboard
  private static final String ASTRA_DB_ID = "<<DB_ID>>";
  private static final String ASTRA_DB_REGION = "<<DB_REGION>>";
  private static final String ASTRA_TOKEN = "<<YOUR_APP_TOKEN>>";
  private static final String ASTRA_KEYSPACE = "<<KEY_SPACE>>";

  public static void main(String[] args) {

    // -------------------------------------
    // 1. Initializing Connectivity
    // -------------------------------------
    ManagedChannel channel = ManagedChannelBuilder.forAddress(
        ASTRA_DB_ID + "-" + ASTRA_DB_REGION + ".apps.astra.datastax.com", 443)
        .useTransportSecurity().build();

    // blocking stub version
    StargateGrpc.StargateBlockingStub blockingStub = StargateGrpc
        .newBlockingStub(channel).withDeadlineAfter(10, TimeUnit.SECONDS)
        .withCallCredentials(new StargateBearerToken(ASTRA_TOKEN));

    // execute query
    QueryOuterClass.Response queryString = blockingStub
        .executeQuery(QueryOuterClass.Query.newBuilder()
            .setCql(
                "select release_version from system.local"
                //"SELECT firstname, lastname FROM " + ASTRA_KEYSPACE + ".users"
                )
            .build());
    // process the result set
    QueryOuterClass.ResultSet rs = queryString.getResultSet();

    Row row = rs.getRows(0);
    // Print the results of the CQL query to the console:
    if (row != null) {
      System.out.printf("Release version: %s%n", row.getValues(0).getString());
    } else {
      System.out.println("An error occurred.");
    }
    
    /** general code
    for (Row row : rs.getRowsList()) {
        System.out.println(""
                + "FirstName=" + row.getValues(0).getString() + ", "
                + "lastname=" + row.getValues(1).getString());
    }
    */
  }
}
