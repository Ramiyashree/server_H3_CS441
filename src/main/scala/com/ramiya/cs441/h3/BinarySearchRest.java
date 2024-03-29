package com.ramiya.cs441.h3;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;
import java.util.stream.Collectors;
import java.time.LocalTime;
import java.util.regex.Pattern;


import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.security.NoSuchAlgorithmException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BinarySearchRest {

    public static void main(String[] args) throws Exception {

        LocalTime time = LocalTime.parse("17:12:58.745");
        LocalTime dT = LocalTime.parse("00:00:01.00");
        IntervalTime(time, dT);
    }

    public static List<byte[]> IntervalTime(LocalTime time, LocalTime dT) throws IOException {

        String bucketName = "logfilegrpcrest";
        String key = "LogFileGenerator.2021-10-18.log";

        Config conf = ConfigFactory.load("application.conf");
        String pattern = conf.getString("serverConfig.pattern");
        System.out.println("pattern" + pattern);

        StringBuilder sb = new StringBuilder();
        List<byte[]> HashedMessages = new ArrayList<byte[]>();

        S3Object fullObject = null, objectPortion = null, headerOverrideObject = null;

        try {
            AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                    .withRegion("us-east-2")
                    .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                    .build();

            // Get an object and print its contents.
            System.out.println("Downloading an object");

            fullObject = s3Client.getObject(new GetObjectRequest(bucketName, key));

            InputStream inputStream = fullObject.getObjectContent();

            String textFile = fullObject.getObjectContent().toString();


            String text = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            LocalTime lowerIntervalTime = time.plusHours(dT.getHour()).plusMinutes(dT.getMinute()).plusSeconds(dT.getSecond()).plusNanos(dT.getNano());
            System.out.println("lower" + lowerIntervalTime);

            LocalTime upperIntervalTime = time.minusHours(dT.getHour()).minusMinutes(dT.getMinute()).minusSeconds(dT.getSecond()).minusNanos(dT.getNano());
            System.out.println("upper" + upperIntervalTime);

            int startInterval = findTimesInInterval(text, upperIntervalTime, true);

            System.out.println("upperIndex" + startInterval);

                int endInterval = findTimesInInterval(text, lowerIntervalTime, false);
                System.out.println("endIndex" + endInterval);

                int count = endInterval - startInterval;
                String[] stringIntervalLines = text.split("\n");

            if(startInterval == 1 && endInterval == 0)
                    System.out.println("nointervalmessages");
                else {
                    for (int i = startInterval; i < endInterval; i++) {
                        int n = stringIntervalLines[i].split(" ").length;
                       System.out.println(stringIntervalLines[i].split(" ")[n-1]);
                        if(Pattern.matches(pattern, stringIntervalLines[i].split(" ")[n - 1])){
                            byte[] bytesOfMessage = stringIntervalLines[i].split(" ")[n - 1].getBytes("UTF-8");
                            MessageDigest md = MessageDigest.getInstance("MD5");
                            byte[] theMD5digest = md.digest(bytesOfMessage);
                            sb.append(theMD5digest);
                            HashedMessages.add(theMD5digest);
                        }
                    }
                    System.out.println("M5HASHMESSAGE" + HashedMessages);
                }
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return HashedMessages;
    }

    private static int findTimesInInterval(String lines, LocalTime time, boolean isUpper) throws IOException {
        // Read the text input stream one line at a time and display each line

        int count = lines.split("\n").length - 1;

        String[] stringLines = lines.split("\n");

        int start = 0;
        int end = count;
        int middle = 0;
        boolean found = false;

        while (start <= end) {
            middle = (start + end) / 2;

            String strMidTime = stringLines[middle].split(" ")[0];
            LocalTime midTime = LocalTime.parse(strMidTime);
            int comparison = midTime.compareTo(time);

            if (comparison == 0) {
                found = true;
                break;
            } else if (comparison < 1) {
                start = middle + 1;
            } else {
                end = middle - 1;
            }
        }

        return isUpper ? start + 1: end + 1;
    }
}

