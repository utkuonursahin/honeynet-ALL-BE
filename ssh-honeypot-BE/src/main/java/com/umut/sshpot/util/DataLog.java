package com.umut.sshpot.util;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.umut.sshpot.suspiciousactivity.Origin;
import org.bson.Document;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class DataLog {
    /*
    public String[] GeolocalizeIp(String IpAddress) throws IOException {
        try {
            // Send a GET request to the IP-API API to get the location of the IP address
            URL url = new URL("http://ip-api.com/json/" + IpAddress+"?fields=61439");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setConnectTimeout(5000);
            con.setReadTimeout(5000);


            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(content.toString());


            String query =json.getString("query");
            String status = json.getString("status");
            String country = json.getString("country");
            String city = json.getString("city");

            return new String[] {query,status,country,city};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }*/
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


       /*
                String[] IP_Data = new String[0];
                try {
                IP_Data = GeolocalizeIp(IP);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
*/
            // Create a document to insert into the collection
            Document logDocument = new Document()
                    .append("_id",UUID.randomUUID().toString())
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
}

/*
public void saveFileToDatabase(String entryTime,String ip) {
        try (MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017")) {
            MongoDatabase database = mongoClient.getDatabase("hooney");
            MongoCollection<Document> collection = database.getCollection("ssh");
            Document logDocument = new Document();
            logDocument.append("_id", UUID.randomUUID().toString())
                    .append("entryTime", entryTime)
                    .append("ip",ip);
            collection.insertOne(logDocument);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

 */
