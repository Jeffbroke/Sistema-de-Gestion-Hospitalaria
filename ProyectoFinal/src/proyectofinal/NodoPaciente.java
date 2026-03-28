/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectofinal;

/*
 * Nodo del árbol binario de búsqueda de pacientes.
 */
public class NodoPaciente {

    Paciente dato;
    NodoPaciente izquierdo;
    NodoPaciente derecho;

    public NodoPaciente(Paciente dato) {
        this.dato = dato;
        this.izquierdo = null;
        this.derecho = null;
    }
}

