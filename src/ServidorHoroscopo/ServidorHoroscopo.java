package ServidorHoroscopo;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;

import ServidorCentral.SHServiciosAbstracto;

public class ServidorHoroscopo {

    private static String IP_SH;
    private static int PUERTO_SH;

    public static void main(String[] args) {
        final String CONFIG_FILE_PATH = "config_ServidorHoroscopo.txt";

        // Se leen desde archivo los datos de ip y puerto del ServidorHoroscopo
        try {
            System.out.println("ServidorHoroscopo> Iniciando ServidorHoroscopo...");

            BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));    
            PUERTO_SH = Integer.parseInt(configReader.readLine());
            IP_SH = configReader.readLine();
            configReader.close();

            // Creacion del objeto interfaz que referenciara a la implementacion del servicio de ServidorHoroscopo
            SHServiciosAbstracto serverHoroscopo = new SHServicios();

            // Vinculamos dicho objeto al registro, con la direccion y puerto propios del ServidorHoroscopo. De esta manera, el ServidorCentral puede acceder al servicio remotamente
            Naming.rebind("rmi://" + IP_SH + ":" + PUERTO_SH + "/SHServicios", serverHoroscopo);

            System.out.println("ServidorHoroscopo> ServidorHoroscopo activo.");
        } catch (Exception e) {
            System.out.println("ServidorHoroscopo> Error: " + e.getMessage());
        }
    }
}