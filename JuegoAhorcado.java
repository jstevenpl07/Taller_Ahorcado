import java.util.*;
import java.io.*;

public class JuegoAhorcado {

    static Scanner sc = new Scanner(System.in);

    // 🎨 COLORES ANSI
    static final String VERDE = "\u001B[32m";
    static final String ROJO = "\u001B[31m";
    static final String AMARILLO = "\u001B[33m";
    static final String RESET = "\u001B[0m";

    public static void main(String[] args) {

        String opcion;

        do {
            mostrarMenu();
            opcion = sc.nextLine();

            switch (opcion) {
                case "1":
                    jugar();
                    break;
                case "2":
                    mostrarInstrucciones();
                    break;
                case "3":
                    mostrarRanking("puntajes.csv");
                    break;
                case "4":
                    System.out.println("👋 Hasta luego");
                    break;
                default:
                    System.out.println("❌ Opción inválida");
            }

        } while (!opcion.equals("4"));
    }

    // 📋 MENÚ
    public static void mostrarMenu() {
        System.out.println(
            "\n╔══════════════════════════════════════╗\n" +
            "║        🎮 AHORCADO PRO MAX 🎮        ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║ 1. Jugar                            ║\n" +
            "║ 2. Instrucciones                    ║\n" +
            "║ 3. Tabla de récords                 ║\n" +
            "║ 4. Salir                            ║\n" +
            "╚══════════════════════════════════════╝"
        );
    }

    // 📘 INSTRUCCIONES
    public static void mostrarInstrucciones() {
        System.out.println(
            "\n📘 INSTRUCCIONES:\n" +
            "- Adivina la palabra letra por letra\n" +
            "- Tienes 6 intentos\n" +
            "- '*' = pista (-1 intento)\n" +
            "- Completa la palabra para ganar\n"
        );
    }

    // 🎮 JUEGO
    public static void jugar() {

        Map<String, List<String>> categorias = cargarCSV("palabras.csv");

        System.out.print("👤 Ingresa tu nombre: ");
        String nombre = sc.nextLine();

        boolean modoDios = verificarEasterEgg(nombre);

        if (modoDios) {
            animacionModoDios();
            mostrarCelebracion();

            categorias.put("Secretos", Arrays.asList(
                "darthvader", "matrix", "zelda", "tardis", "konami"
            ));
        }

        System.out.println("\n📂 CATEGORÍAS:");
        for (String cat : categorias.keySet()) {
            System.out.println("- " + cat);
        }

        System.out.print("Elige categoría: ");
        String input = sc.nextLine();

        String categoria = buscarCategoria(categorias, input);

        if (categoria == null) {
            System.out.println("❌ Categoría inválida");
            return;
        }

        String palabra = seleccionarPalabra(categorias.get(categoria));
        String estado = "_".repeat(palabra.length());

        int errores = 0;
        boolean usoPista = false;

        // BONUS: primera letra
        if (modoDios) {
            estado = actualizarEstado(palabra, estado, palabra.charAt(0));
        }

        while (errores < 6 && !estaCompleta(estado)) {

            mostrarAhorcado(errores);
            mostrarEstado(estado);

            System.out.print("Letra (* pista): ");
            String entrada = sc.nextLine();

            if (entrada.equals("*") && !usoPista) {
                estado = darPista(palabra, estado);
                errores++;
                usoPista = true;
                continue;
            }

            char letra = entrada.charAt(0);

            String nuevo = actualizarEstado(palabra, estado, letra);

            if (nuevo.equals(estado)) {
                errores++;
            } else {
                estado = nuevo;
            }
        }

        mostrarAhorcado(errores);

        if (estaCompleta(estado)) {
            System.out.println(VERDE + "🎉 GANASTE 🎉" + RESET);
            guardarPuntaje("puntajes.csv", nombre, 1);
        } else {
            System.out.println(ROJO + "💀 PERDISTE 💀 Palabra: " + palabra + RESET);
            guardarPuntaje("puntajes.csv", nombre, 0);
        }
    }

    // 🥚 EASTER EGG
    public static boolean verificarEasterEgg(String nombre) {
        return nombre.equalsIgnoreCase("XACARANA");
    }

    // ⚡ ANIMACIÓN
    public static void animacionModoDios() {
        System.out.print("⚡ Activando modo Dios");
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
        } catch (Exception e) {}
        System.out.println("\n");
    }

    // 🎨 ASCII PRO
    public static void mostrarCelebracion() {
        System.out.println(AMARILLO +
            "\n" +
            "╔══════════════════════════════════════════╗\n" +
            "║        ✨🔥 MODO DIOS ACTIVADO 🔥✨       ║\n" +
            "╚══════════════════════════════════════════╝\n" +

            "          ⚡⚡⚡⚡⚡⚡⚡⚡⚡\n" +
            "        ⚡             ⚡\n" +
            "      ⚡   😎  XACARANA  😎  ⚡\n" +
            "        ⚡             ⚡\n" +
            "          ⚡⚡⚡⚡⚡⚡⚡⚡⚡\n" +

            "\n" +
            "        💥 PODER DESBLOQUEADO 💥\n" +
            "     🧠 +INTELIGENCIA  ⚔️ +SUERTE\n" +
            "        🚀 +VELOCIDAD  🎯 +PRECISIÓN\n" +
            "\n" +
            "      🏆 ¡ERES UN JUGADOR LEGENDARIO! 🏆\n"
        + RESET);
    }

    // 🔤 STRING
    public static String actualizarEstado(String palabra, String estado, char letra) {
        String nuevo = "";

        for (int i = 0; i < palabra.length(); i++) {
            if (palabra.charAt(i) == letra) {
                nuevo += letra;
            } else {
                nuevo += estado.charAt(i);
            }
        }
        return nuevo;
    }

    public static String darPista(String palabra, String estado) {
        for (int i = 0; i < palabra.length(); i++) {
            if (estado.charAt(i) == '_') {
                return actualizarEstado(palabra, estado, palabra.charAt(i));
            }
        }
        return estado;
    }

    public static boolean estaCompleta(String estado) {
        return !estado.contains("_");
    }

    public static void mostrarEstado(String estado) {
        for (int i = 0; i < estado.length(); i++) {
            System.out.print(estado.charAt(i) + " ");
        }
        System.out.println();
    }

    public static String seleccionarPalabra(List<String> lista) {
        return lista.get((int)(Math.random() * lista.size()));
    }

    // 📂 CSV
    public static Map<String, List<String>> cargarCSV(String archivo) {
        Map<String, List<String>> mapa = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {

            String linea;
            br.readLine();

            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",");
                String categoria = p[0].trim();
                String palabra = p[1].trim();

                mapa.putIfAbsent(categoria, new ArrayList<>());
                mapa.get(categoria).add(palabra);
            }

        } catch (Exception e) {
            System.out.println("Error CSV");
        }

        return mapa;
    }

    // 🏆 RANKING
    public static void mostrarRanking(String archivo) {

        Map<String, Integer> ranking = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {

            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",");
                ranking.put(p[0], ranking.getOrDefault(p[0], 0) + Integer.parseInt(p[1]));
            }

        } catch (Exception e) {
            System.out.println("Error ranking");
        }

        System.out.println("\n🏆 RANKING:");
        ranking.entrySet()
                .stream()
                .sorted((a, b) -> b.getValue() - a.getValue())
                .forEach(e -> System.out.println(e.getKey() + " - " + e.getValue()));
    }

    public static void guardarPuntaje(String archivo, String nombre, int puntos) {
        try (FileWriter fw = new FileWriter(archivo, true)) {
            fw.write(nombre + "," + puntos + "\n");
        } catch (IOException e) {
            System.out.println("Error guardando");
        }
    }

    public static String buscarCategoria(Map<String, List<String>> categorias, String input) {
        for (String cat : categorias.keySet()) {
            if (cat.equalsIgnoreCase(input)) return cat;
        }
        return null;
    }

    // 🎨 AHORCADO
    public static void mostrarAhorcado(int e) {

        String[] estados = {
            " +---+\n |   |\n     |\n     |\n     |\n     |\n=======",
            " +---+\n |   |\n O   |\n     |\n     |\n     |\n=======",
            " +---+\n |   |\n O   |\n |   |\n     |\n     |\n=======",
            " +---+\n |   |\n O   |\n/|   |\n     |\n     |\n=======",
            " +---+\n |   |\n O   |\n/|\\  |\n     |\n     |\n=======",
            " +---+\n |   |\n O   |\n/|\\  |\n/    |\n     |\n=======",
            " +---+\n |   |\n O   |\n/|\\  |\n/ \\  |\n     |\n======="
        };

        System.out.println(estados[e]);
    }
}