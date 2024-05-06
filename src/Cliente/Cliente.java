package Cliente;

import java.io.BufferedReader;
import java.io.FileReader;
import java.rmi.Naming;
import java.util.Scanner;

public class Cliente {

    private static String IP_SC;
    private static int PUERTO_SC;

    public static void main(String[] args) {
        
        final String CONFIG_FILE_PATH = "config_cliente.txt";
        BufferedReader configReader;
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;

        try {
            // Se lee desde archivo los datos de ip y puerto del SC
            configReader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));
            PUERTO_SC = Integer.parseInt(configReader.readLine());
            IP_SC = configReader.readLine();
            configReader.close();
 
            do {  
                System.out.println("Cliente> Por favor, ingrese su consulta del Horoscopo y Clima de la siguiente forma: 'signo fecha' --- Si desea salir, ingrese 'Exit'");
                System.out.println("Cliente> Ejemplo: cancer 07/05/2024");

                // Se ingresa por consola la consulta, respetando el formato indicado
                String consulta = scanner.nextLine();

                if (!consulta.equalsIgnoreCase("exit")) {
                    // Se busca la referencia al objeto remoto de ServidorCentral en el registro de dicha direccion 
                    SCServiciosAbstracto serverCentral = (SCServiciosAbstracto) Naming.lookup("//" + IP_SC + ":" + PUERTO_SC + "/SCServicios");
                    
                    // Realizamos la consulta, invocando el servicio que nos provee ServidorCentral 
                    String respuestaConsulta = serverCentral.consultarHoroscopoClima(consulta);
        
                    System.out.println("Cliente> Respuesta: " + respuestaConsulta + "\n");
                } 
                else {
                    scanner.close();
                    flag =! flag;
                }
            } while (flag);
            System.out.println("Cliente> Adios!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}