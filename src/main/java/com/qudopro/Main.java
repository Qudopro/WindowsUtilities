package com.qudopro;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

import com.qudopro.files.WindowsFileSystem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Scanner input = new Scanner(System.in);


    public static void main(String[] arsg){
        System.out.println("Indique el directorio a recorrer");
        String directoryInfo = input.nextLine();

        System.out.println("1) Modificar propiedades de los ficheros?");
        System.out.println("2) Cambiar UNICAMENTE EL nombre a los ficheros");
        String option = input.next();

        if(!StringUtils.isBlank(option)){
            switch (option){
                case "1":
                    setProperties(directoryInfo);
                    break;
                case "2":
                    onlyChangeNames(directoryInfo);
                    break;
            }
        }
    }

    private static void onlyChangeNames(String directoryInfo){
        WindowsFileSystem windowsFileSystem = new WindowsFileSystem();
        windowsFileSystem.replaceFileNames(Path.of(directoryInfo));
    }

    /**
     * Método que permite cambiar las propiedades manualmente
     * @param directoryInfo
     */
    private static void setProperties(String directoryInfo){
        if(!StringUtils.isBlank(directoryInfo) && Files.isDirectory(Path.of(directoryInfo))){
            System.out.println("Desea cambiar el nombre de los ficheros ANTES DE CAMBIAR LAS PROPIEDADES? (Y/n)");
            String option = input.next();
            boolean replaceNames = false;
            if(!StringUtils.isBlank(option) && option.equalsIgnoreCase("y"))
                replaceNames = true;

            System.out.println("Desea agregar las propiedades manualmente? (Y/n)");
            option = input.next();
            if(!StringUtils.isBlank(option) && option.equalsIgnoreCase("y")){
                setPropertiesManually(Path.of(directoryInfo), replaceNames);
                displayMessage("Procesando directorio " + directoryInfo);
            }else if((!StringUtils.isBlank(option) && option.equalsIgnoreCase("n")))
                setDefaultProperties(Path.of(directoryInfo), replaceNames);
            else
                System.out.println("Modo no especificado");

        }
    }

    /**
     * Permite cambiar las propiedades de los ficheros reemplazando,
     * primero el nombre de los ficheros básandose en código Hash 256
     * pero únicamente utilizando los primeros 12 caracteres
     * para poder establecer un estándar en los nombres de los mismos.
     * sólo si replaceNames es true. Si es false, no modifica el nombre de los ficheros
     * Las propiedades que se pueden cambiar son:
     * comment -> basándose en una actualización + fecha
     * rating -> (1-5) que se muestra en forma de estrellas
     * ratingPercentage  -> escala numérica equivalente
     * @param directoryInfo
     * @param
     */
    private static void setPropertiesManually(Path directoryInfo, boolean replaceNames){
        WindowsFileSystem windowsFileSystem = new WindowsFileSystem(replaceNames);
        windowsFileSystem.setPropertiesToElements(directoryInfo);
    }

    private static void setDefaultProperties(Path directoryInfo, boolean replaceNames){
        WindowsFileSystem windowsFileSystem = new WindowsFileSystem(replaceNames);
        windowsFileSystem.setDefaultProperties(directoryInfo);
    }

    /**
     * Método encargado de mostrar un mensaje de texto en pantalla
     * @param message
     */
    private static void displayMessage(String message){
        logger.info("Procesando directorio: {}", message);
    }
}
