package ServidorPronosticoClima;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;

import ServidorCentral.SPServiciosAbstracto;


public class ServidorPronosticoClima {
    private static int PUERTO_SP;
    private static String IP_SP;
    
    public static void main(String[] args) {
        String configFilePath = "config_servidorClima.txt";
        try {
            BufferedReader configReader = new BufferedReader(new FileReader(configFilePath));    
            PUERTO_SP = Integer.parseInt(configReader.readLine());
            IP_SP = configReader.readLine();
            configReader.close();

            SPServiciosAbstracto serverPronostico= new SPServicios();
            Naming.rebind("rmi://" + IP_SP + ":" + PUERTO_SP + "/SPServicios", serverPronostico);
            System.out.println("Servidor Horoscopo> Activo.");
            System.out.println("Servidor Horoscopo> Esperando nuevas consultas...");

        } catch (Exception e) {
            System.out.println("Error de conexion");
        }
    }

}