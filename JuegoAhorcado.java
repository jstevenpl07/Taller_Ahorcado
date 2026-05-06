<<<<<<< HEAD
import java.util.*;
import java.io.*;

public class JuegoAhorcado {

    static Scanner sc = new Scanner(System.in);

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
                    System.out.println(">>> Hasta luego");
                    break;
                default:
                    System.out.println("[!] Opción inválida");
            }

        } while (!opcion.equals("4"));
    }

    // 🎮 MENÚ CON ASCII "AHORCADO"
    public static void mostrarMenu() {
        System.out.println(
            "\n" +
            " █████╗ ██╗  ██╗ ██████╗ ██████╗  ██████╗ █████╗ ██████╗  ██████╗ \n" +
            "██╔══██╗██║  ██║██╔═══██╗██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔═══██╗\n" +
            "███████║███████║██║   ██║██████╔╝██║     ███████║██║  ██║██║   ██║\n" +
            "██╔══██║██╔══██║██║   ██║██╔══██╗██║     ██╔══██║██║  ██║██║   ██║\n" +
            "██║  ██║██║  ██║╚██████╔╝██║  ██║╚██████╗██║  ██║██████╔╝╚██████╔╝\n" +
            "╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝╚═════╝ ╚═════╝ \n" +

            "\n╔══════════════════════════════════════╗\n" +
            "║          AHORCADO PRO MAX            ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║ 1. Jugar                             ║\n" +
            "║ 2. Instrucciones                     ║\n" +
            "║ 3. Ranking                           ║\n" +
            "║ 4. Salir                             ║\n" +
            "╚══════════════════════════════════════╝\n"
        );
    }

    // 📘 INSTRUCCIONES
    public static void mostrarInstrucciones() {
        System.out.println(
            "\n[INFO] INSTRUCCIONES:\n" +
            "- Adivina la palabra letra por letra\n" +
            "- Tienes 6 intentos\n" +
            "- Usa '*' para obtener una pista (-1 intento, solo una vez)\n"
        );
    }

    // 🎮 JUEGO
    public static void jugar() {

        Map<String, List<String>> categorias = cargarCSV("palabras.csv");

        if (categorias.isEmpty()) {
            System.out.println("[!] No hay categorías disponibles");
            return;
        }

        System.out.print("> Nombre: ");
        String nombre = sc.nextLine();

        boolean modoDios = nombre.equalsIgnoreCase("XACARANA");

        if (modoDios) {
            animacionModoDios();
            mostrarCelebracion();
        }

        System.out.println("\nCategorías:");
        for (String cat : categorias.keySet()) {
            System.out.println("- " + cat);
        }

        System.out.print("Elige categoría: ");
        String input = sc.nextLine();

        String categoria = buscarCategoria(categorias, input);

        if (categoria == null) {
            System.out.println("[!] Categoría inválida");
            return;
        }

        String palabra = seleccionarPalabra(categorias.get(categoria));
        String estado = "_".repeat(palabra.length());

        int errores = 0;
        boolean usoPista = false;

        // Bonus modo dios: revela primera letra
        if (modoDios && palabra.length() > 0) {
            estado = actualizarEstado(palabra, estado, palabra.charAt(0));
        }

        while (errores < 6 && !estaCompleta(estado)) {

            mostrarAhorcado(errores);
            mostrarEstado(estado);

            System.out.println("(i) Escribe '*' para usar una pista (-1 intento)");
            System.out.print("Letra: ");
            String entrada = sc.nextLine();

            // VALIDACIONES
            if (entrada.isEmpty()) {
                System.out.println("[!] Ingresa una letra");
                continue;
            }

            // PISTA
            if (entrada.equals("*")) {
                if (!usoPista) {
                    estado = darPista(palabra, estado);
                    errores++;
                    usoPista = true;
                    System.out.println("(i) Pista usada (-1 intento)");
                } else {
                    System.out.println("[!] Ya usaste la pista");
                }
                continue;
            }

            // SOLO UNA LETRA
            if (entrada.length() > 1) {
                System.out.println("[!] Solo puedes ingresar una letra");
                continue;
            }

            char letra = entrada.charAt(0);

            if (!Character.isLetter(letra)) {
                System.out.println("[!] Ingresa solo letras");
                continue;
            }

            String nuevo = actualizarEstado(palabra, estado, letra);

            if (nuevo.equals(estado)) {
                errores++;
                System.out.println("[X] Letra incorrecta");
            } else {
                estado = nuevo;
                System.out.println("[OK] Bien");
            }
        }

        mostrarAhorcado(errores);

        if (estaCompleta(estado)) {
            System.out.println(">>> GANASTE <<<");
            guardarPuntaje("puntajes.csv", nombre, 1);
        } else {
            System.out.println("--- PERDISTE --- Palabra: " + palabra);
            guardarPuntaje("puntajes.csv", nombre, 0);
        }
    }

    // 🥚 ANIMACIÓN MODO DIOS
    public static void animacionModoDios() {
        System.out.print(">>> Activando modo Dios");
        try {
            for (int i = 0; i < 3; i++) {
                Thread.sleep(500);
                System.out.print(".");
            }
        } catch (Exception e) {}
        System.out.println("\n");
    }

    // 🥚 ASCII MODO DIOS
    public static void mostrarCelebracion() {
        System.out.println(
            "\n" +
            "╔══════════════════════════════════════════════╗\n" +
            "║          *** MODO DIOS ACTIVADO ***          ║\n" +
            "╚══════════════════════════════════════════════╝\n" +

            "        ***********************\n" +
            "        *                     *\n" +
            "        *      XACARANA       *\n" +
            "        *                     *\n" +
            "        ***********************\n" +

            "\n" +
            "            PODER DESBLOQUEADO\n" +
            "     + INTELIGENCIA      + SUERTE\n" +
            "     + VELOCIDAD         + PRECISION\n" +
            "\n" +
            "        ¡ERES UN JUGADOR LEGENDARIO!\n"
        );
    }

    // 🔤 LÓGICA DE CADENAS
    public static String actualizarEstado(String palabra, String estado, char letra) {
        StringBuilder nuevo = new StringBuilder();
        for (int i = 0; i < palabra.length(); i++) {
            if (palabra.charAt(i) == letra) {
                nuevo.append(letra);
            } else {
                nuevo.append(estado.charAt(i));
            }
        }
        return nuevo.toString();
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

    public static String buscarCategoria(Map<String, List<String>> categorias, String input) {
        for (String cat : categorias.keySet()) {
            if (cat.equalsIgnoreCase(input)) return cat;
        }
        return null;
    }

    // 📂 CSV (robusto)
    public static Map<String, List<String>> cargarCSV(String archivo) {
        Map<String, List<String>> mapa = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            br.readLine(); // encabezado

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] p = linea.split(",");
                if (p.length < 2) continue;

                String categoria = p[0].trim();
                String palabra = p[1].trim();

                mapa.putIfAbsent(categoria, new ArrayList<>());
                mapa.get(categoria).add(palabra);
            }
        } catch (Exception e) {
            System.out.println("[!] Error leyendo CSV");
        }

        return mapa;
    }

    // RANKING
    public static void mostrarRanking(String archivo) {
        Map<String, Integer> ranking = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] p = linea.split(",");
                if (p.length < 2) continue;
                String nombre = p[0];
                int puntos = Integer.parseInt(p[1]);
                ranking.put(nombre, ranking.getOrDefault(nombre, 0) + puntos);
            }
        } catch (Exception e) {
            System.out.println("Error ranking");
        }

        System.out.println("\n[RANKING] Puntajes:");
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
=======
import java.util.*;
import java.io.*;

public class JuegoAhorcado {

    static Scanner sc = new Scanner(System.in);

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

    // 🎮 MENÚ CON ASCII "AHORCADO"
    public static void mostrarMenu() {
        System.out.println(
            "\n" +
            " █████╗ ██╗  ██╗ ██████╗ ██████╗  ██████╗ █████╗ ██████╗  ██████╗ \n" +
            "██╔══██╗██║  ██║██╔═══██╗██╔══██╗██╔════╝██╔══██╗██╔══██╗██╔═══██╗\n" +
            "███████║███████║██║   ██║██████╔╝██║     ███████║██████╔╝██║   ██║\n" +
            "██╔══██║██╔══██║██║   ██║██╔══██╗██║     ██╔══██║██╔══██╗██║   ██║\n" +
            "██║  ██║██║  ██║╚██████╔╝██║  ██║╚██████╗██║  ██║██║  ██║╚██████╔╝\n" +
            "╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ ╚═╝  ╚═╝ ╚═════╝╚═╝  ╚═╝╚═╝  ╚═╝ ╚═════╝ \n" +

            "\n╔══════════════════════════════════════╗\n" +
            "║        🎮 AHORCADO PRO MAX 🎮        ║\n" +
            "╠══════════════════════════════════════╣\n" +
            "║ 1. Jugar                            ║\n" +
            "║ 2. Instrucciones                    ║\n" +
            "║ 3. Ranking                          ║\n" +
            "║ 4. Salir                            ║\n" +
            "╚══════════════════════════════════════╝\n"
        );
    }

    // 📘 INSTRUCCIONES
    public static void mostrarInstrucciones() {
        System.out.println(
            "\n📘 INSTRUCCIONES:\n" +
            "- Adivina la palabra letra por letra\n" +
            "- Tienes 6 intentos\n" +
            "- Usa '*' para obtener una pista (-1 intento, solo una vez)\n"
        );
    }

    // 🎮 JUEGO
    public static void jugar() {

        Map<String, List<String>> categorias = cargarCSV("palabras.csv");

        if (categorias.isEmpty()) {
            System.out.println("❌ No hay categorías disponibles");
            return;
        }

        System.out.print("👤 Nombre: ");
        String nombre = sc.nextLine();

        boolean modoDios = nombre.equalsIgnoreCase("XACARANA");

        if (modoDios) {
            animacionModoDios();
            mostrarCelebracion();
        }

        System.out.println("\nCategorías:");
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

        // Bonus modo dios: revela primera letra
        if (modoDios && palabra.length() > 0) {
            estado = actualizarEstado(palabra, estado, palabra.charAt(0));
        }

        while (errores < 6 && !estaCompleta(estado)) {

            mostrarAhorcado(errores);
            mostrarEstado(estado);

            System.out.println("💡 Escribe '*' para usar una pista (-1 intento)");
            System.out.print("Letra: ");
            String entrada = sc.nextLine();

            // VALIDACIONES
            if (entrada.isEmpty()) {
                System.out.println("⚠ Ingresa una letra");
                continue;
            }

            // PISTA
            if (entrada.equals("*")) {
                if (!usoPista) {
                    estado = darPista(palabra, estado);
                    errores++;
                    usoPista = true;
                    System.out.println("💡 Pista usada (-1 intento)");
                } else {
                    System.out.println("⚠ Ya usaste la pista");
                }
                continue;
            }

            // SOLO UNA LETRA
            if (entrada.length() > 1) {
                System.out.println("⚠ Solo puedes ingresar una letra");
                continue;
            }

            char letra = entrada.charAt(0);

            if (!Character.isLetter(letra)) {
                System.out.println("⚠ Ingresa solo letras");
                continue;
            }

            String nuevo = actualizarEstado(palabra, estado, letra);

            if (nuevo.equals(estado)) {
                errores++;
                System.out.println("❌ Letra incorrecta");
            } else {
                estado = nuevo;
                System.out.println("✅ Bien");
            }
        }

        mostrarAhorcado(errores);

        if (estaCompleta(estado)) {
            System.out.println("🎉 GANASTE");
            guardarPuntaje("puntajes.csv", nombre, 1);
        } else {
            System.out.println("💀 PERDISTE: " + palabra);
            guardarPuntaje("puntajes.csv", nombre, 0);
        }
    }

    // 🥚 ANIMACIÓN MODO DIOS
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

    // 🥚 ASCII MODO DIOS
    public static void mostrarCelebracion() {
        System.out.println(
            "\n" +
            "╔══════════════════════════════════════════════╗\n" +
            "║           🔥✨ MODO DIOS ACTIVADO ✨🔥         ║\n" +
            "╚══════════════════════════════════════════════╝\n" +

            "        ⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡\n" +
            "      ⚡               ⚡\n" +
            "    ⚡   😎  XACARANA  😎   ⚡\n" +
            "      ⚡               ⚡\n" +
            "        ⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡⚡\n" +

            "\n" +
            "        💥 PODER DESBLOQUEADO 💥\n" +
            "     🧠 +INTELIGENCIA  ⚔️ +SUERTE\n" +
            "        🚀 +VELOCIDAD  🎯 +PRECISIÓN\n" +
            "\n" +
            "🏆 ¡ERES UN JUGADOR LEGENDARIO! 🏆\n"
        );
    }

    // 🔤 LÓGICA DE CADENAS
    public static String actualizarEstado(String palabra, String estado, char letra) {
        StringBuilder nuevo = new StringBuilder();
        for (int i = 0; i < palabra.length(); i++) {
            if (palabra.charAt(i) == letra) {
                nuevo.append(letra);
            } else {
                nuevo.append(estado.charAt(i));
            }
        }
        return nuevo.toString();
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

    public static String buscarCategoria(Map<String, List<String>> categorias, String input) {
        for (String cat : categorias.keySet()) {
            if (cat.equalsIgnoreCase(input)) return cat;
        }
        return null;
    }

    // 📂 CSV (robusto)
    public static Map<String, List<String>> cargarCSV(String archivo) {
        Map<String, List<String>> mapa = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            br.readLine(); // encabezado

            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue;

                String[] p = linea.split(",");
                if (p.length < 2) continue;

                String categoria = p[0].trim();
                String palabra = p[1].trim();

                mapa.putIfAbsent(categoria, new ArrayList<>());
                mapa.get(categoria).add(palabra);
            }
        } catch (Exception e) {
            System.out.println("❌ Error leyendo CSV");
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
                if (p.length < 2) continue;
                String nombre = p[0];
                int puntos = Integer.parseInt(p[1]);
                ranking.put(nombre, ranking.getOrDefault(nombre, 0) + puntos);
            }
        } catch (Exception e) {
            System.out.println("Error ranking");
        }

        System.out.println("\n🏆 Ranking:");
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
>>>>>>> efb58cc901d04cff16f8585f6a3e223b1dc5cde1
}