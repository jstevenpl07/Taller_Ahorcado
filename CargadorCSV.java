import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CargadorCSV {

    public static ArrayList<String> cargarPalabras(String rutaArchivo) {
        ArrayList<String> palabras = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            br.readLine(); // Error 1: Saltar el encabezado para no jugar con la palabra "categoria" o "palabra"

            while ((linea = br.readLine()) != null) {
                // Suponiendo que cada línea tiene una palabra
                // o que están separadas por comas
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
} // Error 2: Se agregó la llave de cierre de la clase que faltaba


    
