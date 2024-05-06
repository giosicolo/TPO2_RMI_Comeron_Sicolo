package ServidorPronosticoClima;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import ServidorCentral.SPServiciosAbstracto;

public class SPServicios extends UnicastRemoteObject implements SPServiciosAbstracto {

    // Constructor del objeto que implementa el servicio del Pronostico Clima   
    public SPServicios() throws RemoteException {

    }

    /* Metodo que, dada una fecha, genera una prediccion aleatoria de como sera el clima y su temperatura.
       Desde un objeto remoto interfaz de Pronostico Clima (del lado cliente) se invoca dicho metodo    */
    @Override
    public String consultarPronostico(String fecha) throws RemoteException {
        String [] predicciones =  {"Soleado", "Lluvioso", "Nublado", "Ventoso", "Tormentoso", "Neblina", "Templado", "Nevado"};
        String resultado, predSelecc;
        ArrayList<String> listaPredicionesPronosticoClima = new ArrayList<String>();
        
        System.out.println("ServidorPronosticoClima> Atendiendo consulta: " + fecha);

        Collections.addAll(listaPredicionesPronosticoClima, predicciones);
        Collections.shuffle(listaPredicionesPronosticoClima);

        predSelecc= listaPredicionesPronosticoClima.get(0);

        resultado = predSelecc + " - Con una temperatura de " + obtenerTemperaturaConsulta(fecha) + "° Grados";
        
        System.out.println("ServidorPronosticoClima> Respuesta enviada al ServidorCentral. \n");

        return resultado;
    }

    // Metodo que, dependiendo de la estacion del año (mes), devuelve en forma aleatoria una temperatura para dicha fecha
    private int obtenerTemperaturaConsulta(String fecha) {
        String[] dmy = fecha.split("/");
        int mes = Integer.parseInt(dmy[1]);
        Random rd = new Random();
        int temp = 0;

        if (mes >= 1 && mes < 4){
            temp = (15 + rd.nextInt(30));
        } else {
            if ((mes >= 4 && mes < 7) || (mes >= 9 && mes < 13))  {
                temp = (5 + rd.nextInt(25));   
            } else {
                temp = (1 + rd.nextInt(19));
            }
        }
        return temp;
    }
}