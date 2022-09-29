package dev.astradb.firstapp;

import java.net.URL;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;

/**
 * @overview
 *  Demonstrate how to connect to AstraDB using legacy-style coding style
 *  
 *  <p>Dependencies: 
 *  <tt>
 *  <dependency>
       <groupId>com.datastax.oss</groupId>
       <artifactId>java-driver-core</artifactId>
       <version>4.13.0</version>
    </dependency>
 *  </tt>
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class FirstAppLegacy {

  public static void main(String[] args) {
    // Create the CqlSession object:
    final String file = "secure-connect-firstdb.zip";
    //   "<<CLIENT ID>>"
    final String clientId = "IPCNZrLvOOGyTdMhlKaDMjcc";
    // "<<CLIENT SECRET>>"
    final String clientSecret = "OR-k5AfmZ8AiQFZA1SvMp-wnnRpXt1,H04yn8Pr3LM0uOESzIUKTt3UZNAtyFm,QM8OUM-GebUdZZIrHE,JKYtPmmsN-PNw.kdWZY6UMNbYlebC,o0aadLi6UjluisPI";
    
    URL connectBundleFile = FirstAppLegacy.class.getClassLoader().
        getResource(file);
    
    if (connectBundleFile == null) {
      throw new RuntimeException("Could not find or load the bundle file from the project's resources: " + file);
    }
      
    try (CqlSession session = CqlSession.builder()
        .withCloudSecureConnectBundle(connectBundleFile)
        .withAuthCredentials(clientId, clientSecret).build()) {
      // Select the release_version from the system.local table:
      System.out.println("Getting the release_version from the system.local table...");
      ResultSet rs = session
          .execute("select release_version from system.local");
      Row row = rs.one();
      // Print the results of the CQL query to the console:
      if (row != null) {
        System.out.printf("Release version: %s%n", row.getString("release_version"));
      } else {
        System.out.println("An error occurred.");
      }
    }
    System.exit(0);
  }
}
