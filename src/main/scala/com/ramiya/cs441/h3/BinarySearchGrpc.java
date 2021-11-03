package com.ramiya.cs441.h3;

import java.io.*;
import java.io.File;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.sql.Time;
import java.util.Scanner;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.sql.Timestamp;
import java.time.LocalTime;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ResponseHeaderOverrides;
import com.amazonaws.services.s3.model.S3Object;
import com.twitter.util.Local;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class BinarySearchGrpc {

    public static void main(String[] args) throws Exception {
        findTime("17:12:54.300");
    }

    public static boolean findTime(String time) throws IOException {

        String bucketName = "logfilegrpcrest";
        String key = "LogFileGenerator.2021-10-18.log";

        System.out.println("inside binarysearch" + time);

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

            String text = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));

            return findTimeInLog(text, time);
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
        }
        return false;
    }

    private static boolean findTimeInLog(String lines, String time) throws IOException {
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

            int comparison = midTime.compareTo(LocalTime.parse(time));

            if (comparison == 0) {
                found = true;
                break;
            } else if (comparison < 1) {
                start = middle + 1;
            } else {
                end = middle - 1;
            }
        }
        if (found) {
            System.out.println("yes");
            System.out.println("FINAL ANS" + " " + middle);
        } else {
            System.out.println("no");
        }
        return found;
    }
}

