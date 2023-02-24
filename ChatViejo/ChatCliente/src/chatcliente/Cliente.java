/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatcliente;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Rene
 */
public class Cliente {

    Socket S;
    ExecutorService E;
    private final int SERVIDOR;
    private final String HOST, NOMBRE;
    JTextArea TRespuesta;
    JTextField TEntrada;
    ObjectInputStream entrada;
    ObjectOutputStream salida;

    public Cliente(String nombre, int SERVIDOR, String HOST, JTextArea TRespuesta, JTextField TEntrada) throws IOException {
        //this.PUERTO = PUERTO;
        this.SERVIDOR = SERVIDOR;
        this.HOST = HOST;
        NOMBRE = nombre;
        this.TRespuesta = TRespuesta;
        this.TEntrada = TEntrada;
        // System.out.println("1111");
        S = new Socket(HOST, SERVIDOR);
     //   System.out.println("22222");

        //   System.out.println("aaaaaaa");
        E = Executors.newCachedThreadPool();
        E.execute(new ObtenerConeccion());
        E.execute(new EscucharServidor());
        //  System.out.println("bbbbbbbb");
        //mensajeAServidor("conectado");
        this.TEntrada.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (e.getActionCommand().equals("END")) {
                        mensajeAServidor("desconectado");
                        mensajeTerminar();
                        vaciarEntrada();
                        cerrar();
                    } else {
                        mensajeAServidor(e.getActionCommand());
                    }
                    vaciarEntrada();
                } catch (IOException ex) {
                    Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

    }

    private synchronized void vaciarEntrada() {
        TEntrada.setText("");
    }

    private synchronized void mensajeAServidor(String mensaje) throws IOException {
        //   System.out.println("ddddd");
        //  salida = new ObjectOutputStream(S.getOutputStream());
        // System.out.println("eeeeee");
        salida.writeObject(NOMBRE + " >> " + mensaje);
        salida.flush();
    }

    private synchronized void mensajeTerminar() throws IOException {
        salida.writeObject("END");
        salida.flush();
    }

    private void cerrar() throws IOException {
        salida.close();
        entrada.close();
        S.close();
    }

    private synchronized void imprimirMensaje(String m) {
        TRespuesta.append(m + "\n");
    }

    private class ObtenerConeccion implements Runnable {

        @Override
        public void run() {
            try {
                salida = new ObjectOutputStream(S.getOutputStream());
                salida.flush();
                mensajeAServidor("conectado");
               // mensajeAServidor("provar 2");
            //    entrada = new ObjectInputStream(S.getInputStream());

            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private class EscucharServidor implements Runnable {

        @Override
        public void run() {
            try {
//                salida = new ObjectOutputStream(S.getOutputStream());
//                salida.flush();
//                mensajeAServidor("conectado");
//                 mensajeAServidor("provar 2");
                entrada = new ObjectInputStream(S.getInputStream());

            } catch (IOException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            String mensaje = "";
            do {
                try {
                    System.out.println("va a leer");
                    mensaje = (String) entrada.readObject();
                    System.out.println("recivio=" + mensaje);
                    imprimirMensaje(mensaje);
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (ClassNotFoundException ex) {
                    ex.printStackTrace();
                }
            } while (!mensaje.equals("END"));
            imprimirMensaje("se desconecto el servidor");
        }

    }

}
