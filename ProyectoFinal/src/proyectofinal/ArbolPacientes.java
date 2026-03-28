/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectofinal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/*
 * Árbol binario de búsqueda para gestionar pacientes.
 * Se ordena por cédula.
 */
public class ArbolPacientes {

    private NodoPaciente raiz;
    private int siguienteId = 0; // para IDs internos

    // Nombre del archivo donde se almacenan los pacientes
    private static final String ARCHIVO_PACIENTES = "pacientes.txt";

    public ArbolPacientes() {
        this.raiz = null;
    }

    // ========= INSERCIÓN EN BST (ORDENADO POR CÉDULA) =========

    public void insertar(Paciente paciente) {
        raiz = insertarRec(raiz, paciente);
    }

    private NodoPaciente insertarRec(NodoPaciente actual, Paciente p) {
        if (actual == null) {
            return new NodoPaciente(p);
        }

        int cmp = p.getCedula().compareTo(actual.dato.getCedula());

        if (cmp < 0) {
            actual.izquierdo = insertarRec(actual.izquierdo, p);
        } else if (cmp > 0) {
            actual.derecho = insertarRec(actual.derecho, p);
        } else {
            // Cédula duplicada: de momento solo avisamos por consola
            System.out.println("La cédula " + p.getCedula() + " ya existe. No se insertó.");
        }
        return actual;
    }

    // ========= CREACIÓN DE PACIENTE CON ID AUTO =========

    /*
     * Crea un paciente asignando un ID interno secuencial.
     */
    public Paciente crearPaciente(String cedula, String nombre,
                                  String diagnostico, int edad, String telefono) {
        Paciente p = new Paciente(siguienteId, cedula, nombre, diagnostico, edad, telefono);
        siguienteId++;
        return p;
    }

    // ========= BÚSQUEDA POR CÉDULA =========

    public Paciente buscarPorCedula(String cedula) {
        NodoPaciente nodo = buscarPorCedulaRec(raiz, cedula);
        return (nodo != null) ? nodo.dato : null;
    }

    private NodoPaciente buscarPorCedulaRec(NodoPaciente actual, String cedula) {
        if (actual == null) {
            return null;
        }

        int cmp = cedula.compareTo(actual.dato.getCedula());

        if (cmp == 0) {
            return actual;
        } else if (cmp < 0) {
            return buscarPorCedulaRec(actual.izquierdo, cedula);
        } else {
            return buscarPorCedulaRec(actual.derecho, cedula);
        }
    }

    // ========= BÚSQUEDA POR NOMBRE (RECORRIDO COMPLETO) =========

    public String buscarPorNombreComoTexto(String nombreBuscado) {
        StringBuilder sb = new StringBuilder();
        buscarPorNombreRec(raiz, nombreBuscado.toLowerCase(), sb);
        return sb.toString();
    }

    private void buscarPorNombreRec(NodoPaciente actual, String nombreBuscado, StringBuilder sb) {
        if (actual == null) return;

        buscarPorNombreRec(actual.izquierdo, nombreBuscado, sb);

        if (actual.dato.getNombre().toLowerCase().contains(nombreBuscado)) {
            sb.append(actual.dato.toString()).append("\n");
        }

        buscarPorNombreRec(actual.derecho, nombreBuscado, sb);
    }

    // ========= LISTADO IN-ORDER (ORDENADO POR CÉDULA) =========

    public String listarInOrderComoTexto() {
        StringBuilder sb = new StringBuilder();
        listarInOrderRec(raiz, sb);
        return sb.toString();
    }

    private void listarInOrderRec(NodoPaciente actual, StringBuilder sb) {
        if (actual == null) return;

        listarInOrderRec(actual.izquierdo, sb);
        sb.append(actual.dato.toString()).append("\n");
        listarInOrderRec(actual.derecho, sb);
    }

    // ========= CARGA DESDE ARCHIVO =========

    /*
     * Carga los pacientes desde pacientes.txt (si existe)
     * y los inserta en el árbol.
     */
    public void cargarDesdeArchivo() {
    try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_PACIENTES))) {
        String linea;
        int maxId = -1;

        while ((linea = br.readLine()) != null) {
            // Formato: id \t cedula \t nombre \t diagnostico \t edad \t telefono
            String[] partes = linea.split("\t");
            if (partes.length >= 6) {
                int id = Integer.parseInt(partes[0]);
                String cedula = partes[1];
                String nombre = partes[2];
                String diagnostico = partes[3];
                int edad = Integer.parseInt(partes[4]);
                String telefono = partes[5];

                Paciente p = new Paciente(id, cedula, nombre, diagnostico, edad, telefono);
                insertar(p);

                if (id > maxId) {
                    maxId = id;
                }
            }
        }

        // Ajustar siguienteId para no repetir IDs
        siguienteId = maxId + 1;

    } catch (IOException e) {
        // Si el archivo no existe aún, simplemente no cargamos nada
        System.out.println("No se pudo cargar pacientes.txt (posiblemente no existe aún).");
    }
}


    // ========= GUARDAR UN PACIENTE NUEVO EN ARCHIVO (APPEND) =========

    /*
     * Guarda un paciente nuevo al final del archivo pacientes.txt
     */
    public void guardarPacienteEnArchivo(Paciente p) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_PACIENTES, true))) {
        bw.write(p.toFileString());   // <-- AQUI EL CAMBIO
        bw.newLine();
    } catch (IOException e) {
        System.out.println("Error al guardar paciente en archivo: " + e.getMessage());
    }
}

public void guardarTodosEnArchivo() {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(ARCHIVO_PACIENTES))) {
        guardarTodosEnArchivoRec(raiz, bw);
    } catch (IOException e) {
        System.out.println("Error al guardar todos los pacientes: " + e.getMessage());
    }
}

private void guardarTodosEnArchivoRec(NodoPaciente actual, BufferedWriter bw) throws IOException {
    if (actual == null) return;

    guardarTodosEnArchivoRec(actual.izquierdo, bw);
    bw.write(actual.dato.toFileString());   // <-- AQUI TAMBIEN
    bw.newLine();
    guardarTodosEnArchivoRec(actual.derecho, bw);
}

    
    // ========= ELIMINAR PACIENTE POR CÉDULA =========

    private boolean eliminado; // bandera interna para saber si se eliminó algo

    public boolean eliminarPorCedula(String cedula) {
        eliminado = false;
        raiz = eliminarRec(raiz, cedula);
        if (eliminado) {
            guardarTodosEnArchivo(); // reescribir archivo con el árbol actualizado
        }
        return eliminado;
    }

    private NodoPaciente eliminarRec(NodoPaciente actual, String cedula) {
        if (actual == null) {
            return null;
        }

        int cmp = cedula.compareTo(actual.dato.getCedula());

        if (cmp < 0) {
            actual.izquierdo = eliminarRec(actual.izquierdo, cedula);
        } else if (cmp > 0) {
            actual.derecho = eliminarRec(actual.derecho, cedula);
        } else {
            // Encontramos el nodo a eliminar
            eliminado = true;

            // Caso 1: solo hijo derecho o ninguno
            if (actual.izquierdo == null) {
                return actual.derecho;
            }
            // Caso 2: solo hijo izquierdo
            else if (actual.derecho == null) {
                return actual.izquierdo;
            }
            // Caso 3: dos hijos -> usar sucesor in-order (mínimo del subárbol derecho)
            else {
                NodoPaciente sucesor = encontrarMin(actual.derecho);
                actual.dato = sucesor.dato;
                actual.derecho = eliminarRec(actual.derecho, sucesor.dato.getCedula());
            }
        }
        return actual;
    }

    private NodoPaciente encontrarMin(NodoPaciente nodo) {
        while (nodo.izquierdo != null) {
            nodo = nodo.izquierdo;
        }
        return nodo;
    }

}
