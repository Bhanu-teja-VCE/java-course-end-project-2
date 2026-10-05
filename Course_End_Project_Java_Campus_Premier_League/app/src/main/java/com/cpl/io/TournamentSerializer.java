package com.cpl.io;

import com.cpl.tournament.Tournament;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Serializes and deserializes the entire tournament state to and from disk.
 * Demonstrates:
 * - Unit III: FileInputStream, FileOutputStream, ObjectInputStream, ObjectOutputStream, Serialization
 */
public class TournamentSerializer {

    public static void saveToFile(Tournament tournament, File file) throws IOException {
        // Unit III: FileOutputStream and ObjectOutputStream
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(tournament);
            oos.flush();
        }
    }

    public static Tournament loadFromFile(File file) throws IOException, ClassNotFoundException {
        // Unit III: FileInputStream and ObjectInputStream
        try (FileInputStream fis = new FileInputStream(file);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Object obj = ois.readObject();
            if (obj instanceof Tournament) {
                return (Tournament) obj;
            }
            throw new IOException("File does not contain a valid CPL Tournament instance");
        }
    }
}
