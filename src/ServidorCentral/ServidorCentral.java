package ServidorCentral;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;

import Cliente.SCServiciosAbstracto;

public class ServidorCentral {

    private static int PUERTO_SC;
    private static String IP_SC;
    private static int PUERTO_SH;
    private static String IP_SH;
    private static int PUERTO_SP;
    private static String IP_SP;

    private static ConcurrentHashMap<String, String> historialHoroscopo;
    private static ConcurrentHashMap<String, String> historialPronosticoClima;
 
 public static void main(String[] args) {
    
    String configFilePath = "config_servidorCentral.txt";

    try {
        BufferedReader configReader = new BufferedReader(new FileReader(configFilePath));    
        PUERTO_SC = Integer.parseInt(configReader.readLine());
        PUERTO_SP = Integer.parseInt(configReader.readLine());
        PUERTO_SH = Integer.parseInt(configReader.readLine());
        IP_SC = configReader.readLine();
        IP_SH = configReader.readLine();
        IP_SP = configReader.readLine();
        configReader.close();

    } catch (Exception e) {
        System.out.println("Error de conexion");}
        

        try { 
            historialHoroscopo = new ConcurrentHashMap<String, String>();
            historialPronosticoClima = new ConcurrentHashMap<String, String>();
            
            SCServicios serviciosSC = new SCServicios(IP_SP, PUERTO_SP, IP_SH, PUERTO_SH, historialHoroscopo, historialPronosticoClima);
    
            
            Naming.rebind("rmi://" + IP_SC + ":" + PUERTO_SC + "/SCServicios", serviciosSC);
        } catch (Exception e) {
            System.out.println("Error de conexion2");
        }
     
        
   
    }

}




