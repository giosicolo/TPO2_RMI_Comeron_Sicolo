package ServidorCentral;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.concurrent.ConcurrentHashMap;

import Cliente.SCServiciosAbstracto;

public class ServidorCentral {

    private static String IP_SC;
    private static String IP_SH;
    private static String IP_SP;
    private static int PUERTO_SC;
    private static int PUERTO_SH;
    private static int PUERTO_SP;
    private static ConcurrentHashMap<String, String> historialHoroscopo;
    private static ConcurrentHashMap<String, String> historialPronosticoClima;
 
    public static void main(String[] args) {
        final String CONFIG_FILE_PATH = "config_servidorCentral.txt";

        // Se leen desde archivo los datos de: ip y puerto ServidorCentral, ip ServidorPronosticoClima y ServidorHoroscopo, asi como tambien sus puertos
        try {
            System.out.println("ServidorCentral> Iniciando ServidorCentral...");

            BufferedReader configReader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));    
            PUERTO_SC = Integer.parseInt(configReader.readLine());
            PUERTO_SP = Integer.parseInt(configReader.readLine());
            PUERTO_SH = Integer.parseInt(configReader.readLine());
            IP_SC = configReader.readLine();
            IP_SP = configReader.readLine();
            IP_SH = configReader.readLine();
            configReader.close();

        } catch (Exception e) {
            System.out.println("ServidorCentral> Error: " + e.getMessage());
        }
            
        try { 
            // Se crean las caches para almacenar consultas que vayan realizandose
            historialHoroscopo = new ConcurrentHashMap<String, String>();
            historialPronosticoClima = new ConcurrentHashMap<String, String>();

            // Creacion del objeto interfaz que referenciara a la implementacion del ServidorCentral      
            SCServiciosAbstracto serviciosSC = new SCServicios(IP_SP, PUERTO_SP, IP_SH, PUERTO_SH, historialHoroscopo, historialPronosticoClima);

            // Vinculamos dicho objeto al registro, con la direccion y puerto propios del ServidorCentral. De esta manera, el Cliente puede acceder al servicio remotamente
            Naming.rebind("rmi://" + IP_SC + ":" + PUERTO_SC + "/SCServicios", serviciosSC);

            System.out.println("ServidorCentral> ServidorCentral activo.");
        } catch (Exception e) {
            System.out.println("ServidorCentral> Error: " + e.getMessage());
        }
    }
}