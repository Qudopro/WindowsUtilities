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
        while(true){
            System.out.println("Indique el directorio a recorrer");
            String directoryInfo = input.nextLine();

            if(!StringUtils.isBlank(directoryInfo) && Files.isDirectory(Path.of(directoryInfo))){
                System.out.println("Desea cambiar el nombre de los ficheros? (Y/n)");
                String option = input.next();
                boolean replaceNames = false;
                if(!StringUtils.isBlank(option) && option.equalsIgnoreCase("y"))
                    replaceNames = true;

                setProperties(Path.of(directoryInfo), replaceNames);
                displayMessage("Procesando directorio " + directoryInfo);
            }
            else{
                break;
            }
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
    private static void setProperties(Path directoryInfo, boolean replaceNames){
        WindowsFileSystem windowsFileSystem = new WindowsFileSystem(replaceNames);
        windowsFileSystem.setPropertiesToElements(directoryInfo);
    }

    /**
     * Método encargado de mostrar un mensaje de texto en pantalla
     * @param message
     */
    private static void displayMessage(String message){
        logger.info("Procesando directorio: {}", message);
    }
}
