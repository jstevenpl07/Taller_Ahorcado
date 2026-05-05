import java.util.*;
import java.io.*;

public class JuegoAhorcado {

    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        while (true) {

            mostrarMenu();
            System.out.print("Selecciona una opción: ");
            int opcion = sc.nextInt();
            sc.nextLine();

            switch (opcion) {
                case 1:
                    jugar();
                    break;
                case 2:
                    System.out.println("\n🏆 RANKING:");
                    mostrarRanking("puntajes.csv");
                    break;
                case 3:
                    System.out.println("👋 Gracias por jugar");
                    return;
                default:
                    System.out.println("❌ Opción inválida");
            }
        }
    }

    // 🎮 JUEGO PRINCIPAL
    public static void jugar() {

        Map<String, List<String>> categorias = cargarCSV("palabras.csv");

        System.out.print("👤 Ingresa tu nombre: ");
        String nombre = sc.nextLine();

        System.out.println("\n=== CATEGORÍAS ===");
        for (String cat : categorias.keySet()) {
            System.out.println("- " + cat);
        }

        System.out.print("Elige categoría: ");
        String categoria = sc.nextLine();

        if (!categorias.containsKey(categoria)) {
            System.out.println("❌ Categoría inválida");
            return;
        }

        String palabra = seleccionarPalabraAleatoria(categorias.get(categoria));

        char[] estado = new char[palabra.length()];
        Arrays.fill(estado, '_');

        int errores = 0;
        boolean usoPista = false;

        while (errores < 6 && !estaCompleta(estado)) {

            mostrarAhorcado(errores);
            mostrarPalabra(estado);

            System.out.println("💡 '*' = pista (cuesta 1 intento)");
            System.out.print("Letra: ");
            String input = sc.next();

            if (input.equals("*") && !usoPista) {
                darPista(palabra, estado);
                errores++;
                usoPista = true;
                continue;
            }

            char letra = input.charAt(0);

            if (!validarLetra(letra, palabra, estado)) {
                errores++;
            }
        }

        mostrarAhorcado(errores);

        int gano = 0;

        if (estaCompleta(estado)) {
            System.out.println("🎉 Ganaste!");
            gano = 1;
        } else {
            System.out.println("💀 Perdiste. Era: " + palabra);
        }

        guardarPuntaje("puntajes.csv", nombre, gano);
    }

    // 📂 CSV → categorías
    public static Map<String, List<String>> cargarCSV(String archivo) {
        Map<String, List<String>> mapa = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",");
                mapa.putIfAbsent(p[0], new ArrayList<>());
                mapa.get(p[0]).add(p[1]);
            }
        } catch (Exception e) {
            System.out.println("Error leyendo CSV");
        }

        return mapa;
    }

    // 💾 Guardar puntaje
    public static void guardarPuntaje(String archivo, String nombre, int gano) {
        try (FileWriter fw = new FileWriter(archivo, true)) {
            fw.write(nombre + "," + gano + "\n");
        } catch (IOException e) {
            System.out.println("Error guardando puntaje");
        }
    }

    // 🏆 Ranking
    public static void mostrarRanking(String archivo) {
        Map<String, Integer> ranking = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",");
                ranking.put(p[0], ranking.getOrDefault(p[0], 0) + Integer.parseInt(p[1]));
            }

        } catch (Exception e) {
            System.out.println("Error leyendo ranking");
        }

        ranking.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.println(e.getKey() + " - " + e.getValue() + " pts"));
    }

    // 💡 Pista
    public static void darPista(String palabra, char[] estado) {
        for (int i = 0; i < palabra.length(); i++) {
            if (estado[i] == '_') {
                estado[i] = palabra.charAt(i);
                System.out.println("💡 Se reveló una letra!");
                break;
            }
        }
    }

    public static String seleccionarPalabraAleatoria(List<String> lista) {
        return lista.get((int)(Math.random() * lista.size()));
    }

    public static void mostrarPalabra(char[] estado) {
        for (char c : estado) System.out.print(c + " ");
        System.out.println();
    }

    public static boolean validarLetra(char letra, String palabra, char[] estado) {
        boolean ok = false;

        for (int i = 0; i < palabra.length(); i++) {
            if (palabra.charAt(i) == letra) {
                estado[i] = letra;
                ok = true;
            }
        }
        return ok;
    }

    public static boolean estaCompleta(char[] estado) {
        for (char c : estado) if (c == '_') return false;
        return true;
    }

    // 🎨 AHORCADO BONITO
    public static void mostrarAhorcado(int e) {

        String[] estados = {
            "   +--------+\n   |        |\n            |\n            |\n            |\n            |\n  ==========\n",
            "   +--------+\n   |        |\n   O        |\n            |\n            |\n            |\n  ==========\n",
            "   +--------+\n   |        |\n   O        |\n   |        |\n            |\n            |\n  ==========\n",
            "   +--------+\n   |        |\n   O        |\n  /|        |\n            |\n            |\n  ==========\n",
            "   +--------+\n   |        |\n   O        |\n  /|\\       |\n            |\n            |\n  ==========\n",
            "   +--------+\n   |        |\n   O        |\n  /|\\       |\n  /         |\n            |\n  ==========\n",
            "   +--------+\n   |        |\n   O        |\n  /|\\       |\n  / \\       |\n            |\n  ==========\n"
        };

        System.out.println(estados[e]);
    }

    // 🎮 MENÚ ASCII
    public static void mostrarMenu() {
        System.out.println(
            "╔══════════════════════════════════════╗\n" +
            "║          🎮 AHORCADO PRO 🎮          ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║         +--------+                   ║\n" +
            "║         |        |                   ║\n" +
            "║         O        |                   ║\n" +
            "║        /|\\       |                   ║\n" +
            "║        / \\       |                   ║\n" +
            "║                                      ║\n" +
            "║       ¡Adivina la palabra! 😎        ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║  1. Jugar                            ║\n" +
            "║  2. Ver ranking                      ║\n" +
            "║  3. Salir                            ║\n" +
            "╚══════════════════════════════════════╝"
        );
    }
}