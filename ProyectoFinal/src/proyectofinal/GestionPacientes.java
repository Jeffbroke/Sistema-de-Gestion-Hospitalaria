/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectofinal;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/*
 * Módulo de gestión de pacientes.
 * Usa un árbol binario de búsqueda y archivos .txt
 */
public class GestionPacientes {

    private ArbolPacientes arbol;

    public GestionPacientes() {
        arbol = new ArbolPacientes();
        arbol.cargarDesdeArchivo(); // Cargar pacientes previos si existen
    }

    // ===================== MENÚ PRINCIPAL DEL MÓDULO =====================

       public void menuPacientes(JFrame ventana) {
        int opcion = -1;

        // Mostrar pacientes previos (si hay)
        String existentes = arbol.listarInOrderComoTexto();
        if (existentes == null || existentes.trim().isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay pacientes registrados todavía.",
                    "Pacientes existentes",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            String tabla = formatearComoTabla(existentes);
            JOptionPane.showMessageDialog(ventana,
                    tabla,
                    "Pacientes existentes",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        do {
            String input = JOptionPane.showInputDialog(
                    ventana,
                    "MÓDULO DE GESTIÓN DE PACIENTES\n\n"
                    + "1. Registrar nuevo paciente\n"
                    + "2. Buscar paciente por cédula\n"
                    + "3. Buscar pacientes por nombre\n"
                    + "4. Listar todos los pacientes\n"
                    + "5. Modificar datos de un paciente\n"
                    + "6. Eliminar paciente\n"
                    + "0. Volver al menú principal\n\n"
                    + "Seleccione una opción:"
            );

            if (input == null) {
                return; // cancelar = volver al menú principal
            }

            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    registrarPaciente(ventana);
                    break;
                case 2:
                    buscarPorCedula(ventana);
                    break;
                case 3:
                    buscarPorNombre(ventana);
                    break;
                case 4:
                    listarPacientes(ventana);
                    break;
                case 5:
                    modificarPaciente(ventana);
                    break;
                case 6:
                    eliminarPaciente(ventana);
                    break;
                case 0:
                    JOptionPane.showMessageDialog(ventana,
                            "Volviendo al menú principal...");
                    break;
                default:
                    JOptionPane.showMessageDialog(ventana,
                            "Opción inválida.");
            }

        } while (opcion != 0);
    }


    // ===================== OPCIÓN 1: REGISTRAR PACIENTE =====================

       private void registrarPaciente(JFrame ventana) {

    // ========== CÉDULA ==========
    String cedula;
    while (true) {
        String input = JOptionPane.showInputDialog(ventana, "Cédula del paciente (9 dígitos):");
        if (input == null) return;  // cancelar = salir del registro

        cedula = input.trim();

        if (!cedula.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(ventana,
                    "La cédula debe tener exactamente 9 dígitos numéricos.");
            continue;   // vuelve a pedir cédula
        }

        if (arbol.buscarPorCedula(cedula) != null) {
            JOptionPane.showMessageDialog(ventana,
                    "Ya existe un paciente con esta cédula.");
            continue;   // vuelve a pedir cédula
        }

        break; // cédula válida
    }

    // ========== NOMBRE ==========
    String nombre;
    while (true) {
        String input = JOptionPane.showInputDialog(ventana, "Nombre completo del paciente:");
        if (input == null) return;

        nombre = input.trim();
        if (!nombre.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,40}")) {
            JOptionPane.showMessageDialog(ventana,
                    "El nombre solo puede contener letras y espacios (2–40 caracteres).");
            continue;
        }
        break;
    }
    

    // ========== DIAGNÓSTICO ==========
    String diagnostico;
    while (true) {
        String input = JOptionPane.showInputDialog(ventana, "Diagnóstico o condición médica:");
        if (input == null) return;

        diagnostico = input.trim();
        if (!diagnostico.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,50}")) {
            JOptionPane.showMessageDialog(ventana,
                    "El diagnóstico solo puede contener letras y espacios (3–50 caracteres).");
            continue;
        }
        break;
    }
    
    

    // ========== EDAD ==========
    int edad;
    while (true) {
        String input = JOptionPane.showInputDialog(ventana, "Edad del paciente:");
        if (input == null) return;

        try {
            edad = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(ventana,
                    "La edad debe ser un número entero.");
            continue;
        }

        if (edad < 0 || edad > 120) {
            JOptionPane.showMessageDialog(ventana,
                    "Edad fuera de rango (0–120).");
            continue;
        }
        break;
    }
    
    

    // ========== TELÉFONO ==========
    String telefono;
    while (true) {
        String input = JOptionPane.showInputDialog(ventana, "Teléfono de contacto (8 dígitos):");
        if (input == null) return;

        telefono = input.trim();
        if (!telefono.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(ventana,
                    "El teléfono debe tener exactamente 8 dígitos numéricos.");
            continue;
        }
        break;
    }
    
    

    // Crear e insertar paciente
    Paciente nuevo = arbol.crearPaciente(cedula, nombre,
        diagnostico, edad, telefono);
arbol.insertar(nuevo);
arbol.guardarPacienteEnArchivo(nuevo);

    
    
    String detalle =
        "Cédula:  " + nuevo.getCedula() + "\n" +
        "Nombre:   " + nuevo.getNombre() + "\n" +
        "Diagnóstico:  " + nuevo.getDiagnostico() + "\n" +
        "Edad:   " + nuevo.getEdad() + "\n" +
        "Teléfono:   " + nuevo.getTelefono();

JOptionPane.showMessageDialog(
        ventana,
        "Paciente registrado correctamente:\n\n" + detalle
);

}



    // ===================== OPCIÓN 2: BUSCAR POR CÉDULA =====================

    private void buscarPorCedula(JFrame ventana) {
        String cedula = JOptionPane.showInputDialog(ventana, "Digite la cédula a buscar:");
        if (cedula == null || cedula.trim().isEmpty()) {
            return;
        }

        Paciente p = arbol.buscarPorCedula(cedula.trim());

        if (p == null) {
            JOptionPane.showMessageDialog(ventana,
                    "No se encontró paciente con cédula: " + cedula);
        } else {
            JOptionPane.showMessageDialog(ventana,
                    "Paciente encontrado:\n\n" + p.toString());
        }
    }

    // ===================== OPCIÓN 3: BUSCAR POR NOMBRE =====================

    private void buscarPorNombre(JFrame ventana) {
        String nombre = JOptionPane.showInputDialog(ventana,
                "Digite parte del nombre a buscar:");
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }

        String resultado = arbol.buscarPorNombreComoTexto(nombre.trim());

        if (resultado == null || resultado.trim().isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No se encontraron pacientes con ese nombre.");
        } else {
            JOptionPane.showMessageDialog(ventana,
                    "Pacientes encontrados:\n\n" + resultado);
        }
    }
    
    ///////////
    
    private void modificarPaciente(JFrame ventana) {

    // ========== PEDIR CÉDULA A MODIFICAR ==========
    String cedula;
    while (true) {
        String input = JOptionPane.showInputDialog(
                ventana,
                "Digite la cédula del paciente a modificar (9 dígitos):"
        );
        if (input == null) return; // cancelar

        cedula = input.trim();
        if (!cedula.matches("\\d{9}")) {
            JOptionPane.showMessageDialog(ventana,
                    "La cédula debe tener exactamente 9 dígitos numéricos.");
            continue;
        }
        break;
    }

    Paciente p = arbol.buscarPorCedula(cedula);
    if (p == null) {
        JOptionPane.showMessageDialog(ventana,
                "No se encontró paciente con esa cédula.");
        return;
    }

    JOptionPane.showMessageDialog(ventana,
            "Datos actuales del paciente:\n\n" + p.toString());

    // ========== NOMBRE ==========
    String nuevoNombre = p.getNombre();
    while (true) {
        String input = (String) JOptionPane.showInputDialog(
                ventana,
                "Nuevo nombre completo del paciente:",
                "Modificar paciente",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                nuevoNombre
        );

        if (input == null) return; // cancelar modificación

        input = input.trim();
        if (!input.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,40}")) {
            JOptionPane.showMessageDialog(ventana,
                    "El nombre solo puede contener letras y espacios (2–40 caracteres).");
            continue;
        }

        nuevoNombre = input;
        break;
    }

    // ========== DIAGNÓSTICO ==========
    String nuevoDiagnostico = p.getDiagnostico();
    while (true) {
        String input = (String) JOptionPane.showInputDialog(
                ventana,
                "Nuevo diagnóstico o condición médica:",
                "Modificar paciente",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                nuevoDiagnostico
        );

        if (input == null) return;

        input = input.trim();
        if (!input.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{3,50}")) {
            JOptionPane.showMessageDialog(ventana,
                    "El diagnóstico solo puede contener letras y espacios (3–50 caracteres).");
            continue;
        }

        nuevoDiagnostico = input;
        break;
    }

    // ========== EDAD ==========
    int nuevaEdad = p.getEdad();
    while (true) {
        String input = (String) JOptionPane.showInputDialog(
                ventana,
                "Nueva edad del paciente:",
                "Modificar paciente",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                String.valueOf(nuevaEdad)
        );

        if (input == null) return;

        int edadTmp;
        try {
            edadTmp = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(ventana,
                    "La edad debe ser un número entero.");
            continue;
        }

        if (edadTmp < 0 || edadTmp > 120) {
            JOptionPane.showMessageDialog(ventana,
                    "Edad fuera de rango (0–120).");
            continue;
        }

        nuevaEdad = edadTmp;
        break;
    }

    // ========== TELÉFONO ==========
    String nuevoTelefono = p.getTelefono();
    while (true) {
        String input = (String) JOptionPane.showInputDialog(
                ventana,
                "Nuevo teléfono de contacto (8 dígitos):",
                "Modificar paciente",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                nuevoTelefono
        );

        if (input == null) return;

        input = input.trim();
        if (!input.matches("\\d{8}")) {
            JOptionPane.showMessageDialog(ventana,
                    "El teléfono debe tener exactamente 8 dígitos numéricos.");
            continue;
        }

        nuevoTelefono = input;
        break;
    }

    // ========== APLICAR CAMBIOS ==========
    p.setNombre(nuevoNombre);
    p.setDiagnostico(nuevoDiagnostico);
    p.setEdad(nuevaEdad);
    p.setTelefono(nuevoTelefono);

    // Guardar todo el árbol en archivo para reflejar cambios
    arbol.guardarTodosEnArchivo();

    JOptionPane.showMessageDialog(ventana,
            "Paciente actualizado correctamente:\n\n" + p.toString());
}



    // ===================== OPCIÓN 4: LISTAR TODOS =====================

    private void listarPacientes(JFrame ventana) {
        String listado = arbol.listarInOrderComoTexto();

        if (listado == null || listado.trim().isEmpty()) {
            JOptionPane.showMessageDialog(ventana,
                    "No hay pacientes registrados.");
        } else {
            String tabla = formatearComoTabla(listado);
    JOptionPane.showMessageDialog(ventana,
            tabla,
            "Listado de pacientes (ordenados por cédula)",
            JOptionPane.INFORMATION_MESSAGE);
        }
    }
 /*
 * Genera una tabla bonita con encabezados y líneas separadoras.
 */
private String formatearComoTabla(String contenido) {

    if (contenido == null || contenido.trim().isEmpty()) {
        return "No hay datos.";
    }

    String header =
            String.format("%-12s %-20s %-20s %-5s %-10s\n",
                    "Cédula", "Nombre", "Diagnóstico", "Edad", "Teléfono")
            + "-------------------------------------------------------------------------------\n";

    return header + contenido;
}
    private void eliminarPaciente(JFrame ventana) {
        String cedula = JOptionPane.showInputDialog(ventana,
                "Digite la cédula del paciente a eliminar:");
        if (cedula == null) return;
        cedula = cedula.trim();

        if (!cedula.matches("\\d{5,12}")) {
            JOptionPane.showMessageDialog(ventana,
                    "Cédula inválida.");
            return;
        }

        Paciente p = arbol.buscarPorCedula(cedula);
        if (p == null) {
            JOptionPane.showMessageDialog(ventana,
                    "No se encontró paciente con esa cédula.");
            return;
        }

        int confirmar = JOptionPane.showConfirmDialog(
                ventana,
                "¿Seguro que desea eliminar al siguiente paciente?\n\n" + p.toString(),
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirmar != JOptionPane.YES_OPTION) {
            return;
        }

        boolean eliminado = arbol.eliminarPorCedula(cedula);

        if (eliminado) {
            JOptionPane.showMessageDialog(ventana,
                    "Paciente eliminado correctamente.");
        } else {
            JOptionPane.showMessageDialog(ventana,
                    "No se pudo eliminar al paciente (cédula no encontrada).");
        }
    }

    
    // Auxiliar ya que la variable arbol es private y no permite
    //llamarla desde otra clase. Asi no rompemos la encapsulacion.
    //Llamamos en Turnos.
    public Paciente buscarPorCedulaTurnos(String cedula) {
        if (arbol == null) return null;
        return arbol.buscarPorCedula(cedula);
    }


}
