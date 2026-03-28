/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package proyectofinal;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class SistemaHospital {

    private JFrame ventana;  
    private GrafoHospital grafoHospital = new GrafoHospital();
    private GestionPacientes gestionPacientes = new GestionPacientes();
    private Turnos gestionTurnos = new Turnos();

    public SistemaHospital() {
        ventana = new JFrame();
        ventana.setAlwaysOnTop(true);         // Siempre al frente
        ventana.setLocationRelativeTo(null);  // Centrar popups
    }

    public static void main(String[] args) {
        SistemaHospital sistema = new SistemaHospital();
        sistema.menuPrincipal();
    }

    public void menuPrincipal() {
        int opcion = -1;

        do {
            String input = JOptionPane.showInputDialog(
                    ventana,
                    "SISTEMA DE GESTIÓN HOSPITALARIA\n\n"
                    + "1. Gestión de pacientes\n"
                    + "2. Gestión de turnos médicos\n"
                    + "3. Áreas del hospital y rutas\n"
                    + "0. Salir\n\n"
                    + "Seleccione una opción:"
            );

            if (input == null) {
                return;
            }

            try {
                opcion = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                opcion = -1;
            }

            switch (opcion) {
                case 1:
                    gestionPacientes.menuPacientes(ventana);
                    break;

                case 2:
                    gestionTurnos.menuTurnos(ventana);
                    break;

                case 3:
                    grafoHospital.menuGrafo(ventana);
                    break;

                case 0:
                    JOptionPane.showMessageDialog(ventana, "Gracias por utilizar el sistema.");
                    break;

                default:
                    JOptionPane.showMessageDialog(ventana, "Opción inválida.");
            }

        } while (opcion != 0);
    }
}



