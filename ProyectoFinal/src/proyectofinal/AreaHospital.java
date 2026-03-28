/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

/**
 *
 * @author PC
 */
public class AreaHospital {
    private int id;          // índice dentro del arreglo
    private String nombre;   // nombre del área (Ej: "Emergencias", "Rayos X")

    // Constructor
    public AreaHospital(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    // Para mostrar el área como texto
    @Override
    public String toString() {
        return id + " - " + nombre;
    }
    
      
          
}


