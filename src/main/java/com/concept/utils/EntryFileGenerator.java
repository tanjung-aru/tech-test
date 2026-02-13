package com.concept.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.UUID;

public class EntryFileGenerator {

    private static final Path ENTRY_FILE_NAME = Path.of("./EntryFile.txt");
    private static final int RECORD_COUNT = 1000;
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final String[] FIRST_NAMES = {
            "James", "Oliver", "Thomas", "Harry", "George", "Isla", "Ava", "Emily", "Sophia", "Mia"
    };
    private static final String[] SURNAMES = {
            "Smith", "Jones", "Williams", "Brown", "Taylor", "Wilson", "Davies", "Evans", "Thomas", "Johnson"
    };
    private static final String[] FRUITS = {
            "Apple", "Banana", "Orange", "Mango", "Pear", "Pineapple", "Strawberry", "Grapes", "Peach", "Watermelon"
    };
    private static final String[] TRANSPORT = {
            "Rides A Bike", "Drives an SUV", "Rides A Scooter"
    };

    public static void main(String[] args) throws IOException {

        try (BufferedWriter bw = Files.newBufferedWriter(ENTRY_FILE_NAME)) {
            for (int i = 0; i < RECORD_COUNT; i++) {
                bw.write(generateLine());
                bw.write('\n');
            }
        }

        System.out.println("Generated entry file at: " + ENTRY_FILE_NAME + " with " + RECORD_COUNT + " records.");
    }

    private static String generateLine() {
        return generateUuid() + "|"
                + generateId() + "|"
                + generateName() + "|"
                + generateLikes() + "|"
                + generateTransport() + "|"
                + generateAvgSpeed() + "|"
                + generateTopSpeed();
    }

    private static String generateUuid() {
        return UUID.randomUUID().toString();
    }

    private static String generateId() {
        final StringBuilder sb = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            final int index = RANDOM.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }
        return sb.toString();
    }

    private static String generateName() {
        return FIRST_NAMES[RANDOM.nextInt(FIRST_NAMES.length)] + " " + SURNAMES[RANDOM.nextInt(SURNAMES.length)];
    }

    private static String generateLikes() {
        return "Likes " + FRUITS[RANDOM.nextInt(FRUITS.length)];
    }

    private static String generateTransport() {
        return TRANSPORT[RANDOM.nextInt(TRANSPORT.length)];
    }

    private static String generateAvgSpeed() {
        return String.format("%.1f", RANDOM.nextDouble() * 10);
    }

    private static String generateTopSpeed() {
        return String.format("%.1f", RANDOM.nextDouble() * 100);
    }
}
