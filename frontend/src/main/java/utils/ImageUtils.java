package utils;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.logging.*;

public class ImageUtils
{
    private static final Logger LOGGER = Logger.getLogger(ImageUtils.class.getName());

    private ImageUtils() {}

    public static String encodeFileToBase64(File file)
    {
        try
        {
            byte[] fileContent = Files.readAllBytes(file.toPath());
            return Base64.getEncoder().encodeToString(fileContent);
        }
        catch(IOException e)
        {
            LOGGER.log(Level.SEVERE, "Errore durante la conversione dell'immagine in Base64", e);
            return null;
        }
    }

    public static ImageIcon decodeBase64ToIcon(String base64String, int width, int height)
    {
        if(base64String == null || base64String.isBlank())
            return null;

        try
        {
            byte[] decodedBytes = Base64.getDecoder().decode(base64String);
            ImageIcon originalIcon = new ImageIcon(decodedBytes);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
        catch(IllegalArgumentException e)
        {
            LOGGER.log(Level.WARNING, "Stringa Base64 non valida", e);
            return null;
        }
    }

    public static ImageIcon loadResizedIcon(File file, int width, int height)
    {
        ImageIcon originalIcon = new ImageIcon(file.getAbsolutePath());
        Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaledImage);
    }
}