package com.qudopro.files;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;

public class WindowsFileSystem {
    private static final Logger logger = LoggerFactory.getLogger(WindowsFileSystem.class);
    private boolean replaceName;

    public WindowsFileSystem(boolean replaceName){
        this.replaceName = replaceName;
    }

    /**
     * Método encargado de cambiar el nombre a los ficheros a un código SHA-256
     * @param directory Directorio a recorrer
     */
    public void replaceFileNames(Path directory){
        logger.info("Cambiando el nombre a los archivos...");
        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>(){
                        //Acciones a realizar cuando se encuentre con un archivo en el directorio
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            return generateNewName(file);
                        }
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private FileVisitResult generateNewName(Path file){
        try {
            String hash = calculateSHA256(file);
            String extension = obtenerExtension(file);
            Path nuevoNombre = file.resolveSibling(hash + extension);

            // Evita sobrescribir si ya existe un archivo con ese nombre (duplicado exacto)
            if (Files.exists(nuevoNombre)) {
                nuevoNombre = file.resolveSibling(hash + " (2) " + extension);
            }

            Files.move(file, nuevoNombre, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException | NoSuchAlgorithmException e) {
            logger.error("No se ha podido modificar el archivo: {}", file);
            logger.error("{}", e);
        }

        return FileVisitResult.CONTINUE;
    }

    /**
     * Método encargado de generar el código SHA256 equivalente para el fichero
     * @param file fichero a modificar
     * @return
     * @throws NoSuchAlgorithmException
     * @throws IOException
     */
    private String calculateSHA256(Path file) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (var in = Files.newInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesLeidos;
            while ((bytesLeidos = in.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesLeidos);
            }
        }
        return HexFormat.of().formatHex(digest.digest()).substring(0,12);
    }

    /**
     * Método encargado de obtener la extensión de un fichero
     * @param archivo
     * @return
     */
    private static String obtenerExtension(Path archivo) {
        String nombre = archivo.getFileName().toString();
        int puntoIdx = nombre.lastIndexOf('.');
        return (puntoIdx == -1) ? "" : nombre.substring(puntoIdx);
    }


    /**
     * Método encargado de establecer un nuevo valor a las propiedades y metadata de un archivo en un directorio
     * @param directory Directorio que se busca recorrer
     */
    public void setPropertiesToElements(Path directory){
        logger.info("Estableciendo propiedades a los archivos...");

        if(replaceName){
            replaceFileNames(directory);
        }

        try {
            Files.walkFileTree(directory, new SimpleFileVisitor<Path>(){
                        //Acciones a realizar cuando se encuentre con un archivo en el directorio
                        @Override
                        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                            return fileOperation(file);
                        }
                    }
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Se han terminado de modificar los archivos");
    }

    private FileVisitResult fileOperation(Path file){
        Map<String, String> tags = new HashMap<>();
        System.out.printf("Modificando archivo: %n%s%n", file.getFileName());
        try {
            String comentario = setComment();
            String ranking = setRanking();
            String rankingPercentage = setRankingPercentage(ranking);
            tags.put("Rating", ranking);
            tags.put("RatingPercent", rankingPercentage);
            tags.put("XPComment", comentario);
            Propeties.writeProperties(file, tags);
        } catch(IOException | InterruptedException e){
            logger.error("No se ha podido modificar el archivo: {}", file);
            logger.error("{}", e);
        }

        return FileVisitResult.CONTINUE;
    }

    private String setRanking() {
        Scanner entrada = new Scanner(System.in);
        System.out.println("Calificación en el rango del 1 a 5: ");
        String ranking = entrada.next();

        try{
            if(StringUtils.isBlank(ranking) && Integer.parseInt(ranking) < 1 && Integer.parseInt(ranking) > 5)
                ranking = "1";
        }catch (NumberFormatException e){
            ranking = "1";
        }

        return ranking;
    }

    private String setRankingPercentage(String ranking){
        String ratingPercentage = "";
        switch(Integer.parseInt(ranking)){
            case 1 -> ratingPercentage = "1";
            case 2 -> ratingPercentage = "25";
            case 3 -> ratingPercentage = "50";
            case 4 -> ratingPercentage = "75";
            case 5 -> ratingPercentage = "100";
        }
        return ratingPercentage;
    }

    private String setComment(){
        return "Actualizado el " + LocalDateTime.now().toString();
    }
}