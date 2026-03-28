/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectofinal;

/*
 * Representa a un paciente del hospital.
 */
public class Paciente {

    private int id;             // ID interno autoincremental
    private String cedula;      // cédula o identificación
    private String nombre;      // nombre completo
    private String diagnostico; // diagnóstico o condición médica
    private int edad;           // edad del paciente
    private String telefono;    // teléfono de contacto

    public Paciente(int id, String cedula, String nombre,
                    String diagnostico, int edad, String telefono) {
        this.id = id;
        this.cedula = cedula;
        this.nombre = nombre;
        this.diagnostico = diagnostico;
        this.edad = edad;
        this.telefono = telefono;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCedula() {
        return cedula;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDiagnostico() {
        return diagnostico;
    }

    public int getEdad() {
        return edad;
    }

    public String getTelefono() {
        return telefono;
    }

    // Setters básicos
        // Setters básicos
    public void setDiagnostico(String diagnostico) {
        this.diagnostico = diagnostico;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    // ========= FORMATO PARA ARCHIVO (con TABs) =========
    public String toFileString() {
        // id \t cedula \t nombre \t diagnostico \t edad \t telefono
        return id + "\t" + cedula + "\t" + nombre + "\t"
                + diagnostico + "\t" + edad + "\t" + telefono;
    }

    // ========= FORMATO PARA MOSTRAR EN PANTALLA =========
    public String toDisplayString() {
        return String.format(
                "%-12s %-20s %-20s %-5d %-10s",
                cedula,
                nombre,
                diagnostico,
                edad,
                telefono
        );
    }

    @Override
    public String toString() {
        return String.format(
            "%-12s %-20s %-20s %-5d %-10s",
            cedula,
            nombre,
            diagnostico,
            edad,
            telefono
        );
    }

    }



    
