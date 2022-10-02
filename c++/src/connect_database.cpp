#include "cassandra.h"
#include <stdio.h>

#include <string>
#include <iostream>

int firstConnect();

int main(int argc, char* argv[]) {
    std::cout << "Hello Cassandra!" << std::endl;

    return firstConnect();

    // return 0;
 }

/**
 * Adapted from the code sample on the Connect page of the database page (https://astra.datastax.com)
 */
int firstConnect() {

     /* Setup and connect to cluster */
    CassCluster* cluster = cass_cluster_new();
    CassSession* session = cass_session_new();

    // folder containing the connection bundle zip file
     std::string bundle_folder = "X:\\dev\\astradb\\c++\\";
     
     // replace these with YOUR client id and secret (taken from the application token JSON file)
     const char* client_id = "<<CLIENT ID>>";
     const char* client_secret = "<<CLIENT SECRET>>";
     
     /* Setup driver to connect to the cloud using the secure connection bundle */
     std::string secure_connect_bundle_str = bundle_folder + "secure-connect-firstdb.zip";
     const char* secure_connect_bundle = secure_connect_bundle_str.c_str();

     printf("Connecting to database using bundle: '%s'\n", secure_connect_bundle);

     if (cass_cluster_set_cloud_secure_connection_bundle(cluster, secure_connect_bundle) != CASS_OK) {
         fprintf(stderr, "Unable to configure cloud using the secure connection bundle: %s\n",
                 secure_connect_bundle);
         return 1;
     }

     /* Set credentials provided when creating your database */
     cass_cluster_set_credentials(cluster, client_id, client_secret);

     /* Increase the connection timeout */
     cass_cluster_set_connect_timeout(cluster, 10000);

     CassFuture* connect_future = cass_session_connect(session, cluster);

     if (cass_future_error_code(connect_future) == CASS_OK) {

         /* Use the session to run queries */
         /* Build statement and execute query */

         const char* query = "SELECT release_version FROM system.local";
         CassStatement* statement = cass_statement_new(query, 0);

         CassFuture* result_future = cass_session_execute(session, statement);

         if (cass_future_error_code(result_future) == CASS_OK) {
             /* Retrieve result set and get the first row */
             const CassResult* result = cass_future_get_result(result_future);
             const CassRow* row = cass_result_first_row(result);

             if (row) {
                 const CassValue* value = cass_row_get_column_by_name(row, "release_version");

                 const char* release_version;
                 size_t release_version_length;
                 cass_value_get_string(value, &release_version, &release_version_length);
                 printf("release_version: '%.*s'\n", (int)release_version_length, release_version);
             } else {
                printf("No release_version found\n");
             }

             cass_result_free(result);
         } else {
             /* Handle error */
             const char* message;
             size_t message_length;
             cass_future_error_message(result_future, &message, &message_length);
             fprintf(stderr, "Unable to run query: '%.*s'\n", (int)message_length, message);
         }

         cass_statement_free(statement);
         cass_future_free(result_future);

     } else {
         /* Handle error */
     }

     cass_future_free(connect_future);
     cass_cluster_free(cluster);
     cass_session_free(session);

     return 0;
}