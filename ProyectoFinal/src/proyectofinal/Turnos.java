/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectofinal;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
 * Modulo para mostrar las opciones de la gestion de turnos.
 */


public class Turnos {

    private static final String ARCHIVO_ATENDIDOS = "pacientes_atendidos.txt";
    private static final String ARCHIVO_LISTA_ESPERA = "lista_espera.txt";
    private static final int MAX_TURNOS = 50;

    // Cola de prioridad usando arreglo nativo
    private Turno[] cola;
    private int cantidad;

    public Turnos() {
        cola = new Turno[MAX_TURNOS];
        cantidad = 0;
    }

    // ===================== MENÚ TURNOS =====================
    public void menuTurnos(JFrame ventana) {
        int opcion = -1;

        do {
            String input = JOptionPane.showInputDialog(ventana,
                    "MÓDULO GESTIÓN DE TURNOS MÉDICOS\n\n"
                    + "1. Agregar Turno\n"
                    + "2. Atender Paciente\n"
                    + "3. Ver Pacientes Atendidos\n"
                    + "4. Ver Pacientes en Espera\n"
                    + "5. Eliminar paciente de la lista atendidos\n"
                    + "0. Volver al menú principal\n\n"
                    + "Seleccione una opción:");

            if (input == null) return;

            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    agregarTurno(ventana);
                    break;
                case 2:
                    atenderPaciente(ventana);
                    break;
                case 3:
                    pacientesAtendidos(ventana);
                    break;
                case 4:
                    pacientesEspera(ventana);
                    break;

                case 5:
                    eliminarPacienteAtendidoPorCedula(ventana);
                    break;

                case 0:
                    JOptionPane.showMessageDialog(ventana, "Volviendo al menú principal...");
                    break;
                default:
                    JOptionPane.showMessageDialog(ventana, "Opción inválida.");
            }
        } while (opcion != 0);
    }

    // ===================== OPCIÓN 1: AGREGAR TURNO =====================

private void agregarTurno(JFrame ventana) {
    // Pedir cédula para buscar paciente
    String cedula = JOptionPane.showInputDialog(ventana, "Ingrese la cédula del paciente:");
    if (cedula == null || cedula.trim().isEmpty()) return;
    cedula = cedula.trim();

    // Buscar paciente en ArbolPacientes
    Paciente p = new GestionPacientes().buscarPorCedulaTurnos(cedula);
    if (p == null) {
        JOptionPane.showMessageDialog(ventana, "No se encontró paciente con esa cédula.");
        return;
    }

    // Prioridad
    String[] opcionesPrioridad = {"Alta", "Media", "Baja"};
    String prioridadStr = (String) JOptionPane.showInputDialog(
            ventana,
            "Seleccione prioridad del turno:",
            "Prioridad",
            JOptionPane.PLAIN_MESSAGE,
            null,
            opcionesPrioridad,
            "Media"
    );
    if (prioridadStr == null) return;
    int prioridad = prioridadStr.equals("Alta") ? 3 : prioridadStr.equals("Media") ? 2 : 1;

    // Tipo de atención
    String[] tipos = {"Emergencia", "Consulta"};
    String tipo = (String) JOptionPane.showInputDialog(
            ventana,
            "Seleccione tipo de atención:",
            "Tipo de atención",
            JOptionPane.PLAIN_MESSAGE,
            null,
            tipos,
            "Consulta"
    );
    if (tipo == null) return;

    Turno t = new Turno(p, prioridad, tipo, LocalDateTime.now());
    encolar(t);

    // Registrar en lista de espera
    registrarEnListaEspera(t);

    JOptionPane.showMessageDialog(ventana, "Turno agregado:\n" + t);
}


//Registrar en lista de espera

    private void registrarEnListaEspera(Turno t) {
            File archivo = new File(ARCHIVO_LISTA_ESPERA);
            boolean nuevoArchivo = !archivo.exists();

            try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
                if (nuevoArchivo) {
                    bw.write(String.format("%-12s %-20s %-20s %-5s %-10s %-10s %-10s %-20s\n",
                            "Cédula", "Nombre", "Diagnóstico", "Edad", "Teléfono", "Prioridad", "Tipo", "Fecha/Hora"));
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                String fecha = t.getFecha().format(dtf);
                Paciente p = t.getPaciente();
                String prioridadTexto = (t.getPrioridad() == 3) ? "Alta" :
                                        (t.getPrioridad() == 2) ? "Media" : "Baja";

                bw.write(String.format("%-12s %-20s %-20s %-5d %-10s %-10s %-10s %-20s\n",
                        p.getCedula(), p.getNombre(), p.getDiagnostico(), p.getEdad(),
                        p.getTelefono(), prioridadTexto, t.getTipoAtencion(), fecha));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Eliminar de la lista de espera una vez atendido.
        
        private void eliminarDeListaEspera(Turno t) {
            File archivo = new File(ARCHIVO_LISTA_ESPERA);
            File temp = new File("temp_lista.txt");

            if (!archivo.exists()) return;

            boolean eliminado = false;
            String cedulaBuscada = t.getPaciente().getCedula();

            try (
                BufferedReader br = new BufferedReader(new FileReader(archivo));
                BufferedWriter bw = new BufferedWriter(new FileWriter(temp))
            ) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    // Mantener cabecera
                    if (linea.startsWith("Cédula")) {
                        bw.write(linea);
                        bw.newLine();
                        continue;
                    }

                    // Eliminar solo la primera coincidencia
                    String cedulaCol = linea.length() >= 12 ? linea.substring(0, 12).trim() : "";
                    if (!eliminado && cedulaCol.equals(cedulaBuscada)) {
                        eliminado = true;
                        continue;
                    }

                    bw.write(linea);
                    bw.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                java.nio.file.Files.delete(archivo.toPath());
                java.nio.file.Files.move(temp.toPath(), archivo.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        
    // ===================== OPCIÓN 2: ATENDER PACIENTE =====================

        
    private void atenderPaciente(JFrame ventana) {
        
        // Cargar pacientes desde lista_espera.txt a la cola
        cargarListaEsperaDesdeArchivo();

        if (cantidad == 0) {
            JOptionPane.showMessageDialog(ventana, "No hay pacientes en espera.");
            return;
        }

        boolean continuar = true;

        while (continuar && cantidad > 0) {
            Turno siguiente = verSiguiente();

            String prioridadTexto = (siguiente.getPrioridad() == 3) ? "Alta" :
                                    (siguiente.getPrioridad() == 2) ? "Media" : "Baja";

            String msg = "Paciente siguiente:\n\n"
                    + "Nombre: " + siguiente.getPaciente().getNombre() + "\n"
                    + "Cédula: " + siguiente.getPaciente().getCedula() + "\n"
                    + "Prioridad: " + prioridadTexto + "\n"
                    + "Tipo de atención: " + siguiente.getTipoAtencion() + "\n\n"
                    + "¿Desea atender a este paciente?";

            Object[] opciones = {"Sí", "No"};
            int opcion = JOptionPane.showOptionDialog(
                    ventana, msg, "Confirmar atención",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, opciones, opciones[0]
            );

            if (opcion == JOptionPane.NO_OPTION) return;

            Turno atendido = desencolar();
            registrarAtencion(atendido);
            eliminarDeListaEspera(atendido);

            JOptionPane.showMessageDialog(ventana,
                    "Paciente atendido correctamente:\n" + atendido.getPaciente().getNombre());

            if (cantidad > 0) {
                int siguienteAccion = JOptionPane.showOptionDialog(
                        ventana, "¿Qué desea hacer ahora?", "Continuar atención",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                        null, new String[]{"Atender siguiente", "Volver al menú"}, "Atender siguiente"
                );
                if (siguienteAccion == 1) continuar = false;
            } else {
                JOptionPane.showMessageDialog(ventana, "No quedan más pacientes en espera.");
            }
        }
    }


    //Funcion para cargar de lista_espera directo a la cola
    
    private void cargarListaEsperaDesdeArchivo() {
        
        File archivo = new File(ARCHIVO_LISTA_ESPERA);
        if (!archivo.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int fila = 0;
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            while ((linea = br.readLine()) != null) {
                if (fila == 0) { // Saltar cabecera
                    fila++;
                    continue;
                }
                if (linea.trim().isEmpty()) continue;

                String[] cols = linea.split("\\s{2,}");
                if (cols.length < 8) continue;

                String cedula = cols[0].trim();
                String prioridadTxt = cols[5].trim();
                String tipo = cols[6].trim();
                String fechaStr = cols[7].trim();

                int prioridad = prioridadTxt.equalsIgnoreCase("Alta") ? 3 :
                                prioridadTxt.equalsIgnoreCase("Media") ? 2 : 1;

                LocalDateTime fecha;
                try {
                    fecha = LocalDateTime.parse(fechaStr, dtf);
                } catch (Exception e) {
                    fecha = LocalDateTime.now();
                }

                Paciente p = new GestionPacientes().buscarPorCedulaTurnos(cedula);
                if (p != null) {
                    Turno t = new Turno(p, prioridad, tipo, fecha);
                    encolar(t);
                }
                fila++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ===================== OPCIÓN 3: VER PACIENTES ATENDIDOS =====================
    private void pacientesAtendidos(JFrame ventana) {
        StringBuilder sb = new StringBuilder();
        File archivo = new File(ARCHIVO_ATENDIDOS);
        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(ventana, "No hay pacientes atendidos.");
            return;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                sb.append(linea).append("\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ventana, "Error al leer archivo.");
        }

        JOptionPane.showMessageDialog(ventana, sb.toString());
    }

    
    // Crea lista_espera.txt con titulos si no existe
    private void asegurarArchivoListaEspera() {
        File archivo = new File(ARCHIVO_LISTA_ESPERA);
        if (archivo.exists()) return;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write(String.format("%-12s %-20s %-20s %-5s %-10s %-10s %-10s %-20s\n",
                    "Cédula", "Nombre", "Diagnóstico", "Edad", "Teléfono", "Prioridad", "Tipo", "Fecha/Hora"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ===================== OPCIÓN 4: VER PACIENTES EN ESPERA =====================
    
    private void pacientesEspera(JFrame ventana) {
        
        // Asegurar que el archivo exista con cabecera
        asegurarArchivoListaEspera();

        File archivo = new File(ARCHIVO_LISTA_ESPERA);

        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            boolean tieneDatos = false;
            int fila = 0;

            while ((linea = br.readLine()) != null) {
                // Mostrar cabecera tal cual y detectar si hay datos debajo
                if (fila == 0) {
                    sb.append(linea).append("\n");
                    // Línea de separación similar a tu estilo
                    sb.append("----------------------------------------------------------------------------------------------\n");
                } else {
                    // Si hay al menos una línea de datos, marcar
                    if (!linea.trim().isEmpty()) {
                        tieneDatos = true;
                        sb.append(linea).append("\n");
                    }
                }
                fila++;
            }

            if (!tieneDatos) {
                JOptionPane.showMessageDialog(ventana, "No hay pacientes en espera.");
                return;
            }

        } catch (IOException e) {
            JOptionPane.showMessageDialog(ventana, "Error al leer la lista de espera.");
            return;
        }

        JOptionPane.showMessageDialog(ventana, sb.toString());
    }


    // ===================== OPCIÓN 5: Eliminar pacientes atendidos de la lista =====================
    /*
     * Elimina un paciente atendido del archivo pacientes_atendidos.txt
     * según su cédula.
     */
    private void eliminarPacienteAtendidoPorCedula(JFrame ventana) {
        String cedulaBuscada = JOptionPane.showInputDialog(ventana, "Ingrese la cédula del paciente a eliminar:");
        if (cedulaBuscada == null || cedulaBuscada.trim().isEmpty()) {
            JOptionPane.showMessageDialog(ventana, "Debe ingresar una cédula válida.");
            return;
        }
        cedulaBuscada = cedulaBuscada.trim();

        File archivo = new File(ARCHIVO_ATENDIDOS);
        File temp = new File("temp.txt");

        if (!archivo.exists()) {
            JOptionPane.showMessageDialog(ventana, "No hay pacientes atendidos registrados.");
            return;
        }

        boolean eliminado = false;

        try (
            BufferedReader br = new BufferedReader(new FileReader(archivo));
            BufferedWriter bw = new BufferedWriter(new FileWriter(temp))
        ) {
            String linea;
            while ((linea = br.readLine()) != null) {

                // Mantener la cabecera sin tocar
                if (linea.startsWith("Cédula")) {
                    bw.write(linea);
                    bw.newLine();
                    continue;
                }

                // Eliminar SOLO la primera coincidencia
                if (!eliminado && linea.contains(cedulaBuscada)) {
                    eliminado = true;   // marcar que ya eliminamos una vez
                    continue;           // NO escribir esta línea (la "eliminamos")
                }

                // Escribir todas las demás líneas normalmente
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ventana, "Error al procesar el archivo.");
            return;
        }

        // Reemplazar archivo original por el temporal
        try {
            java.nio.file.Files.delete(archivo.toPath());
            java.nio.file.Files.move(temp.toPath(), archivo.toPath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(ventana, "Error al actualizar el archivo.");
            return;
        }

        if (eliminado) {
            JOptionPane.showMessageDialog(ventana, "Se eliminó el primer registro con cédula: " + cedulaBuscada);
        } else {
            JOptionPane.showMessageDialog(ventana, "No se encontró un registro con esa cédula.");
        }
    }



    // ===================== FUNCIONES DE COLA =====================
    
    private void encolar(Turno t) {
        if (cantidad >= MAX_TURNOS) return;

        int i = cantidad - 1;
        // Insertar según tipo y prioridad:
        // Emergencia > Consulta, y dentro de cada tipo, prioridad Alta > Media > Baja
        while (i >= 0) {
            Turno actual = cola[i];

            boolean tEsEmergencia = t.getTipoAtencion().equals("Emergencia");
            boolean actualEsEmergencia = actual.getTipoAtencion().equals("Emergencia");

            if (actualEsEmergencia && !tEsEmergencia) {
                // Si el actual es Emergencia y el nuevo es Consulta, dejamos actual primero
                break;
            } else if (!actualEsEmergencia && tEsEmergencia) {
                // Si el nuevo es Emergencia y el actual es Consulta, mover actual hacia adelante
                cola[i + 1] = actual;
                i--;
            } else {
                // Ambos son del mismo tipo, ordenar por prioridad
                if (actual.getPrioridad() < t.getPrioridad()) {
                    cola[i + 1] = actual;
                    i--;
                } else {
                    break;
                }
            }
        }

        cola[i + 1] = t;
        cantidad++;
    }


    private Turno desencolar() {
        if (cantidad == 0) return null;
        Turno t = cola[0];
        for (int i = 1; i < cantidad; i++) {
            cola[i - 1] = cola[i];
        }
        cantidad--;
        return t;
    }

    private Turno verSiguiente() {
        if (cantidad == 0) return null;
        return cola[0];
    }

    //

    // ===================== REGISTRAR ATENCIÓN EN ARCHIVO =====================
    private void registrarAtencion(Turno t) {
        File archivo = new File(ARCHIVO_ATENDIDOS);
        boolean nuevoArchivo = !archivo.exists();

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo, true))) {
            if (nuevoArchivo) {
                bw.write(String.format("%-12s %-20s %-20s %-5s %-10s %-20s\n",
                        "Cédula", "Nombre", "Diagnóstico", "Edad", "Teléfono", "Fecha/Hora"));
            }
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            String fecha = t.getFecha().format(dtf);
            Paciente p = t.getPaciente();
            bw.write(String.format("%-12s %-20s %-20s %-5d %-10s %-20s\n",
                    p.getCedula(), p.getNombre(), p.getDiagnostico(), p.getEdad(), p.getTelefono(), fecha));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ===================== CLASE INTERNA TURNO =====================
    private class Turno {
        private Paciente paciente;
        private int prioridad; // 1=Baja, 2=Media ,3=Alta
        private String tipoAtencion;
        private LocalDateTime fecha;

        public Turno(Paciente paciente, int prioridad, String tipoAtencion, LocalDateTime fecha) {
            this.paciente = paciente;
            this.prioridad = prioridad;
            this.tipoAtencion = tipoAtencion;
            this.fecha = fecha;
        }

        public Paciente getPaciente() {   // Obtiene la infomacion del paciente.
            return paciente;
        }

        public int getPrioridad() {   // Para saber si es emergencia o consulta.
            return prioridad;
        }

        public String getTipoAtencion() {   // Para saber si es emergencia o consulta.
        return tipoAtencion;
        }

        public LocalDateTime getFecha() {   // Para saber si es emergencia o consulta.
            return fecha;
        }

        @Override
        public String toString() {
            return String.format("%-12s %-20s %-20s %-5d %-10s %-10s %-10s",
                    paciente.getCedula(),
                    paciente.getNombre(),
                    paciente.getDiagnostico(),
                    paciente.getEdad(),
                    paciente.getTelefono(),
                    prioridad == 3 ? "Alta" : prioridad == 2 ? "Media" : "Baja",
                    tipoAtencion);
        }
    }
}

