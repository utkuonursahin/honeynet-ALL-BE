package com.umut.ssh.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DataLog {
    public void SaveLogEntriesToDatabase(String logMessage, String IP, String time) {
        try {
            // Log to the file as before
            File logFile = new File("log_entries.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new Date());
            writer.write("[" + currentTime + "] " + logMessage);
            writer.newLine();
            writer.close();
            // Save the log message to MongoDB
            MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017");
            MongoDatabase database = mongoClient.getDatabase("hooney");
            MongoCollection<Document> collection = database.getCollection("sshEntries");


            // Create a document to insert into the collection
            Document logDocument = new Document()
                    .append("_id", UUID.randomUUID().toString())
                    .append("msg",logMessage)
                    .append("time",time)
                    .append("ip",IP);

            // Insert the document into the collection
            collection.insertOne(logDocument);

            // Close the MongoDB client
            mongoClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void LogToFileDummyCommand(String logMessage) {
        try {
            File logFile = new File("log_dummycommand.txt");
            BufferedWriter writer = new BufferedWriter(new FileWriter(logFile, true));
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTime = dateFormat.format(new Date());
            writer.write("[" + currentTime + "] " + logMessage);
            writer.newLine();

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void getLocation(String ipAddress) throws IOException {
        String apiEndpoint = "http://ip-api.com/json/" + ipAddress;
        URL url = new URL(apiEndpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int responseCode = connection.getResponseCode();
        String ip = null;
        String country = null;
        String city = null;
        String lat = null;
        String lon = null;
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            JSONObject jsonResponse = new JSONObject(response.toString());

            ip = jsonResponse.getString("query");
            country = jsonResponse.getString("country");
            city = jsonResponse.getString("city");
            lat = String.valueOf(jsonResponse.getFloat("lat"));
            lon = String.valueOf(jsonResponse.getFloat("lon"));
        } else {
            System.out.println("Request failed with response code: " + responseCode);
        }
    }
}
