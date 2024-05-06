package ServidorPronosticoClima;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;

import ServidorCentral.SPServiciosAbstracto;

public class ServidorPronosticoClima {

    private static String IP_SP;
    private static int PUERTO_SP;
    
    public static void main(String[] args) {
        final String CONFIG_FILE_PATH = "config_servidorClima.txt";

        // Se leen desde archivo los datos de ip y puerto del ServidorPronosticoClima
        try {
            System.out.println("ServidorPronosticoClima> Iniciando ServidorPronosticoClima...");

            BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));    
            PUERTO_SP = Integer.parseInt(configReader.readLine());
            IP_SP = configReader.readLine();
            configReader.close();

            // Creacion del objeto interfaz que referenciara a la implementacion del servicio de ServidorPronosticoClima
            SPServiciosAbstracto serverPronostico = new SPServicios();

            // Vinculamos dicho objeto al registro, con la direccion y puerto propios del ServidorPronosticoClima. De esta manera, el ServidorCentral puede acceder al servicio remotamente
            Naming.rebind("rmi://" + IP_SP + ":" + PUERTO_SP + "/SPServicios", serverPronostico);

            System.out.println("ServidorPronosticoClima> ServidorPronosticoClima activo.");
        } catch (Exception e) {
            System.out.println("ServidorPronosticoClima> Error: " + e.getMessage());
        }
    }
}