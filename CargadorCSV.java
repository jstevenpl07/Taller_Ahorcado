import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CargadorCSV {

    public static ArrayList<String> cargarPalabras(String rutaArchivo) {
        ArrayList<String> palabras = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            br.readLine(); 
            while ((linea = br.readLine()) != null) {
                String[] partes = linea.split(",");
                for (String palabra : partes) {
                    palabras.add(palabra.trim());
                }
            }

        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }

        return palabras;
    
    }
} 


    
