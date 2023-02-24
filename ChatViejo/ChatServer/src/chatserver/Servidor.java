/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JTextArea;

/**
 *
 * @author Rene
 */
public class Servidor {

    ServerSocket S;
    LinkedList<Socket> clientes;
    LinkedList<ObjectOutputStream> salidas;
    ExecutorService E;
    JTextArea T;

    public Servidor(int puerto, JTextArea T) throws IOException {
        this.T = T;
        clientes = new LinkedList<Socket>();
        salidas = new LinkedList<ObjectOutputStream>();
        S = new ServerSocket(puerto, 20);
        E = Executors.newCachedThreadPool();
        E.execute(new Escuchar());

    }

    private synchronized void actualizarPantalla(String mensaje) {
        T.append(mensaje + "\n");
    }

    private synchronized void mensajeATodos(String mensaje) throws IOException {
        for (int i = 0; i < clientes.size(); i++) {
            mensajeACliente(clientes.get(i), mensaje);
        }
    }

    private synchronized void mensajeACliente(Socket s, String mensaje) throws IOException {
        System.out.println(mensaje);
//        ObjectOutputStream salida = new ObjectOutputStream(s.getOutputStream());
//       salida.flush();
        salidas.get(clientes.indexOf(s)).writeObject(mensaje);
        salidas.get(clientes.indexOf(s)).flush();
        actualizarPantalla(mensaje);
    }

    private class Escuchar implements Runnable {

        @Override
        public void run() {
            try {
                while (true) {
                    Socket entradaClientes = S.accept();
                    clientes.add(entradaClientes);
                    ObjectOutputStream salida = new ObjectOutputStream(entradaClientes.getOutputStream());
                    
                    salida.flush();
                    salidas.add(salida);
                    E.execute(new ProcesarCliente(entradaClientes));
                }
            } catch (IOException ex) {
                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private class ProcesarCliente implements Runnable {

        Socket s;
        //ObjectOutputStream salida;
        ObjectInputStream entrada;

        public ProcesarCliente(Socket s) throws IOException {
            this.s = s;
            //  salida = new ObjectOutputStream(s.getOutputStream());
            entrada = new ObjectInputStream(s.getInputStream());
        }

        @Override
        public void run() {
            String mensaje = "";
            do {
                try {
                    mensaje = (String) entrada.readObject();
                    mensajeATodos(mensaje);
                } catch (IOException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
                }
            } while (!mensaje.equals("END"));
//            try {
//                entrada.close();
//                s.close();
//            } catch (IOException ex) {
//                Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
//            }

            clientes.remove(s);
        }

    }

    private class Responder implements Runnable {

        @Override
        public void run() {

        }

    }
}
