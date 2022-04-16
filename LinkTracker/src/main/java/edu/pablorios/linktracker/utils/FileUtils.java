package edu.pablorios.linktracker.utils;

import edu.pablorios.linktracker.model.WebPage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

// Clase para leer el archivo de texto proporcionado
public class FileUtils {

    // Método para cargar los archivos en una lista de Objetos WebPage
    public static List<WebPage> loadPages(Path file){
        List <WebPage> listaWebs=new ArrayList<>();

        // Instanciamos el buffered reader
        BufferedReader br = null;
        try {
            // Indicamos el archivo a leer (Proporcionado en el argumento)
            br = new BufferedReader(new FileReader(String.valueOf(file)));
            String lineaLeida = br.readLine();

            while(lineaLeida != null)
            {   // Leemos el archivo separándolo con ";" y guardando las web y URL en nuestro objeto webpage
                String[] arrayStringsLineas=lineaLeida.split(";");
                WebPage webPageNueva=new WebPage(arrayStringsLineas[0],arrayStringsLineas[1]);
                listaWebs.add(webPageNueva);
                lineaLeida = br.readLine();
            }
        } catch(Exception e) { System.out.println("Error: "+e.getMessage()); }
        finally {
            try {  if(br != null)
                    br.close();
            } catch (Exception e) { System.out.println("Error: "+e.getMessage());}
        }

        return listaWebs;
    }
}
