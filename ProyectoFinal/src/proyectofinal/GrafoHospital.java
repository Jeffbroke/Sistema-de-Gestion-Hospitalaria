package proyectofinal;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class GrafoHospital {

    private static final int MAX_AREAS = 20;

    private AreaHospital[] areas;
    private int[][] adyacencia;
    private int cantidadAreas;

    public GrafoHospital() {
        areas = new AreaHospital[MAX_AREAS];
        adyacencia = new int[MAX_AREAS][MAX_AREAS];
        cantidadAreas = 0;

        // Inicializar matriz sin conexiones
        for (int i = 0; i < MAX_AREAS; i++) {
            for (int j = 0; j < MAX_AREAS; j++) {
                adyacencia[i][j] = 0;
            }
        }
    }

    // -------------------------------------------------------------------------
    // MENÚ PRINCIPAL DEL MÓDULO DE ÁREAS (USA VENTANA)
    // -------------------------------------------------------------------------

    public void menuGrafo(JFrame ventana) {
        int opcion = -1;

        do {
            String input = JOptionPane.showInputDialog(
                    ventana,
                    "MÓDULO ÁREAS DEL HOSPITAL\n\n"
                    + "1. Agregar área\n"
                    + "2. Mostrar áreas\n"
                    + "3. Conectar áreas\n"
                    + "4. Mostrar conexiones\n"
                    + "5. Buscar ruta entre áreas\n"
                    + "0. Volver al menú principal\n\n"
                    + "Seleccione una opción:"
            );

            if (input == null) return;

            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    agregarArea(ventana);
                    break;
                case 2:
                    mostrarAreas(ventana);
                    break;
                case 3:
                    conectarAreas(ventana);
                    break;
                case 4:
                    mostrarConexiones(ventana);
                    break;
                case 5:
                    buscarRuta(ventana);
                    break;
                case 0:
                    JOptionPane.showMessageDialog(ventana, "Volviendo al menú principal...");
                    break;
                default:
                    JOptionPane.showMessageDialog(ventana, "Opción inválida.");
            }

        } while (opcion != 0);
    }

    // -------------------------------------------------------------------------
    // AGREGAR ÁREA
    // -------------------------------------------------------------------------

    public void agregarArea(JFrame ventana) {
        if (cantidadAreas >= MAX_AREAS) {
            JOptionPane.showMessageDialog(ventana, "No se pueden agregar más áreas.");
            return;
        }

        String nombre = JOptionPane.showInputDialog(ventana, "Nombre del área:");
        if (nombre == null || nombre.trim().isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Nombre inválido.");
            return;
        }

        AreaHospital nueva = new AreaHospital(cantidadAreas, nombre.trim());
        areas[cantidadAreas] = nueva;
        cantidadAreas++;

        JOptionPane.showMessageDialog(ventana, "Área agregada: " + nueva);
    }

    // -------------------------------------------------------------------------
    // MOSTRAR ÁREAS
    // -------------------------------------------------------------------------

    public void mostrarAreas(JFrame ventana) {
        if (cantidadAreas == 0) {
            JOptionPane.showMessageDialog(ventana, "No hay áreas registradas.");
            return;
        }

        StringBuilder sb = new StringBuilder("Áreas registradas:\n\n");
        for (int i = 0; i < cantidadAreas; i++) {
            sb.append(areas[i].toString()).append("\n");
        }

        JOptionPane.showMessageDialog(ventana, sb.toString());
    }

    // -------------------------------------------------------------------------
    // CONECTAR ÁREAS
    // -------------------------------------------------------------------------

    public void conectarAreas(JFrame ventana) {
        if (cantidadAreas < 2) {
            JOptionPane.showMessageDialog(ventana, "Debe haber al menos dos áreas para conectarlas.");
            return;
        }

        mostrarAreas(ventana);

        String origenStr = JOptionPane.showInputDialog(ventana, "Digite el ID del área ORIGEN:");
        String destinoStr = JOptionPane.showInputDialog(ventana, "Digite el ID del área DESTINO:");

        if (origenStr == null || destinoStr == null) return;

        int origen, destino;
        try {
            origen = Integer.parseInt(origenStr);
            destino = Integer.parseInt(destinoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(ventana, "IDs inválidos.");
            return;
        }

        if (!validarIdArea(origen) || !validarIdArea(destino)) {
            JOptionPane.showMessageDialog(ventana, "Algún ID no existe.");
            return;
        }

        // Peso de 1 por defecto (puede extenderse)
        int peso = 1;
        adyacencia[origen][destino] = peso; // grafo dirigido

        JOptionPane.showMessageDialog(
                ventana,
                "Conexión creada:\n"
                + areas[origen].getNombre()
                + "  →  "
                + areas[destino].getNombre()
        );
    }

    private boolean validarIdArea(int id) {
        return id >= 0 && id < cantidadAreas && areas[id] != null;
    }

    // -------------------------------------------------------------------------
    // MOSTRAR CONEXIONES
    // -------------------------------------------------------------------------

    public void mostrarConexiones(JFrame ventana) {
        if (cantidadAreas == 0) {
            JOptionPane.showMessageDialog(ventana, "No hay áreas registradas.");
            return;
        }

        StringBuilder sb = new StringBuilder("Conexiones del hospital:\n\n");

        for (int i = 0; i < cantidadAreas; i++) {
            sb.append(areas[i].getNombre()).append(" → ");

            boolean tieneSalidas = false;

            for (int j = 0; j < cantidadAreas; j++) {
                if (adyacencia[i][j] != 0) {
                    sb.append(areas[j].getNombre()).append("   ");
                    tieneSalidas = true;
                }
            }

            if (!tieneSalidas) {
                sb.append("(sin conexiones)");
            }

            sb.append("\n");
        }

        JOptionPane.showMessageDialog(ventana, sb.toString());
    }

    // -------------------------------------------------------------------------
    // BUSCAR RUTA (BFS)
    // -------------------------------------------------------------------------

    public void buscarRuta(JFrame ventana) {
        if (cantidadAreas < 2) {
            JOptionPane.showMessageDialog(ventana, "Debe haber al menos dos áreas.");
            return;
        }

        mostrarAreas(ventana);

        String origenStr = JOptionPane.showInputDialog(ventana, "ID área ORIGEN:");
        String destinoStr = JOptionPane.showInputDialog(ventana, "ID área DESTINO:");

        if (origenStr == null || destinoStr == null) return;

        int origen, destino;
        try {
            origen = Integer.parseInt(origenStr);
            destino = Integer.parseInt(destinoStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(ventana, "IDs inválidos.");
            return;
        }

        if (!validarIdArea(origen) || !validarIdArea(destino)) {
            JOptionPane.showMessageDialog(ventana, "Algún ID no existe.");
            return;
        }

        String ruta = bfs(origen, destino);

        if (ruta == null) {
            JOptionPane.showMessageDialog(ventana, "No existe ruta entre esas áreas.");
        } else {
            JOptionPane.showMessageDialog(ventana, "Ruta encontrada:\n" + ruta);
        }
    }

    private String bfs(int inicio, int fin) {
        boolean[] visitado = new boolean[MAX_AREAS];
        int[] padre = new int[MAX_AREAS];

        for (int i = 0; i < MAX_AREAS; i++) {
            padre[i] = -1;
        }

        int[] cola = new int[MAX_AREAS];
        int frente = 0;
        int finCola = 0;

        cola[finCola++] = inicio;
        visitado[inicio] = true;

        while (frente < finCola) {
            int actual = cola[frente++];

            if (actual == fin) break;

            for (int vecino = 0; vecino < cantidadAreas; vecino++) {
                if (adyacencia[actual][vecino] != 0 && !visitado[vecino]) {
                    visitado[vecino] = true;
                    padre[vecino] = actual;
                    cola[finCola++] = vecino;
                }
            }
        }

        if (!visitado[fin]) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        int nodo = fin;

        while (nodo != -1) {
            sb.insert(0, areas[nodo].getNombre());
            nodo = padre[nodo];
            if (nodo != -1) sb.insert(0, " → ");
        }

        return sb.toString();
    }
}
