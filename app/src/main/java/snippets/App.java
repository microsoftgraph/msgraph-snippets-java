// Copyright (c) Microsoft Corporation.
// Licensed under the MIT license.

package snippets;

import java.io.IOException;
import java.util.InputMismatchException;
import java.util.Objects;
import java.util.Properties;
import java.util.Scanner;

import com.microsoft.graph.models.User;
import com.microsoft.graph.serviceclient.GraphServiceClient;

import okhttp3.Request;

public class App {
    public static Properties getProperties() {
        final Properties oAuthProperties = new Properties();
        try {
            oAuthProperties.load(App.class.getResourceAsStream("app.properties"));
        } catch (IOException e) {
            System.out.println(
                "Unable to read app configuration. Make sure you have a properly formatted app.properties file. See README for details.");
            return null;
        }

        final Properties devProperties = new Properties();
        try {
            devProperties.load(App.class.getResourceAsStream("app.dev.properties"));
        } catch (IOException e) {
            // File not required
        }

        final Properties merged = new Properties();
        merged.putAll(oAuthProperties);
        // This will overwrite any values that are in the dev file
        merged.putAll(devProperties);

        return merged;
    }

    public static void main(String[] args) {
        final Properties properties = App.getProperties();
        if (null == properties) {
            System.out.println("Failed to load properties, exiting...");
            return;
        }

        GraphServiceClient userClient;
        try {
            userClient = GraphHelper.getGraphClientForUser(properties,
                challenge -> System.out.println(challenge.getMessage()));
        } catch (Exception e) {

            System.out.println("Error initializing Graph for user auth");
            System.out.println(e.getMessage());
            return;
        }

        final User user = userClient.me().get();
        System.out.println("Hello " + Objects.requireNonNull(user).getDisplayName() + "!");

        final String largeFilePath = properties.getProperty("app.largeFilePath");

        Scanner input = new Scanner(System.in);
        int choice = -1;

        while (0 != choice) {
            System.out.println("Please choose one of the following options:");
            System.out.println("0. Exit");
            System.out.println("1. Run batch samples");
            System.out.println("2. Run create request samples");
            System.out.println("3. Run upload samples");
            System.out.println("4. Run paging samples");

            try {
                choice = input.nextInt();
            } catch (InputMismatchException e) {
                // Skip non-integer input
            }

            input.nextLine();
            try {
                switch (choice) {
                case 0:
                    // Exit the program
                    System.out.println("Goodbye...");
                    break;
                case 1:
                    BatchRequests.runSamples(userClient);
                    break;
                case 2:
                    CreateRequests.runSamples(userClient);
                    break;
                case 3:
                    LargeFileUpload.runSamples(userClient, largeFilePath);
                    break;
                case 4:
                    Paging.runAllSamples(userClient);
                    break;
                default:
                    System.out.println("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }

        input.close();
    }
}
