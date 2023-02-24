/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatcliente;

import java.io.IOException;
import javax.swing.JOptionPane;

/**
 *
 * @author Rene
 */
public class ChatCliente {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        String ss=JOptionPane.showInputDialog("Escriba un su nombre");
        new Ventana(ss).setVisible(true);
    }
    
}
