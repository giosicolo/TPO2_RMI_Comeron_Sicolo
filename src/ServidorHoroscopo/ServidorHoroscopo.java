package ServidorHoroscopo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;

import ServidorCentral.SHServiciosAbstracto;

public class ServidorHoroscopo {
    private static int PUERTO_SH;
    private static String IP_SH;


    public static void main(String[] args) {
        String configFilePath = "config_ServidorHoroscopo.txt";
        try {
            BufferedReader configReader = new BufferedReader(new FileReader(configFilePath));    
            PUERTO_SH = Integer.parseInt(configReader.readLine());
            IP_SH = configReader.readLine();
            configReader.close();

            SHServiciosAbstracto serverHoroscopo= new SHServicios();
            Naming.rebind("rmi://" + IP_SH + ":" + PUERTO_SH + "/SHServicios", serverHoroscopo);
            System.out.println("Servidor Horoscopo> Online.");
            System.out.println("Servidor Horoscopo> Esperando consultas...");

        } catch (Exception e) {
            System.out.println("Error de conexion");
        }

    }
}