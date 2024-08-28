package com.bajaj;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class Test{

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar Test.jar <PRN Number> <path to JSON file>");
            return;
        }

        String prnNumber = args[0];
        String jsonFilePath = args[1];

        try {
            // Read JSON file
            String jsonContent = readFile(jsonFilePath);

            // Parse JSON
            JSONObject jsonObject = new JSONObject(jsonContent);

            // Traverse JSON to find the first instance of the key "destination"
            String destinationValue = findDestinationValue(jsonObject);
            if (destinationValue == null) {
                System.out.println("Key 'destination' not found in the JSON file.");
                return;
            }

            // Generate random alphanumeric string
            String randomString = generateRandomString(8);

            // Concatenate PRN number, destination value, and random string
            String concatenatedString = prnNumber + destinationValue + randomString;

            // Generate MD5 hash
            String md5Hash = generateMD5Hash(concatenatedString);

            // Output the result
            System.out.println(md5Hash + ";" + randomString);

        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private static String readFile(String filePath) throws IOException {
        StringBuilder content = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line);
            }
        }
        return content.toString();
    }

    private static String findDestinationValue(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            Object value = jsonObject.get(key);
            if (key.equals("destination")) {
                return value.toString();
            }
            if (value instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) value);
                if (result != null) {
                    return result;
                }
            } else if (value instanceof JSONArray) {
                String result = findDestinationValue((JSONArray) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String findDestinationValue(JSONArray jsonArray) {
        for (Object item : jsonArray) {
            if (item instanceof JSONObject) {
                String result = findDestinationValue((JSONObject) item);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : hashBytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}

