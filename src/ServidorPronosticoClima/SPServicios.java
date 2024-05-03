package ServidorPronosticoClima;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import ServidorCentral.SPServiciosAbstracto;

public class SPServicios extends UnicastRemoteObject implements SPServiciosAbstracto {

    public SPServicios() throws RemoteException {}

    @Override
    public String consultarPronostico(String fecha) throws RemoteException {
        String [] predicciones =  {"Soleado", "Lluvioso", "Nublado", "Ventoso", "Tormentoso", "Neblina", "Templado", "Nevado"};
        String resultado, predSelecc;
        ArrayList<String> listaPredicionesPronosticoClima = new ArrayList<String>();
        
        Collections.addAll(listaPredicionesPronosticoClima, predicciones);
        Collections.shuffle(listaPredicionesPronosticoClima);

        predSelecc= listaPredicionesPronosticoClima.get(0);

        resultado = predSelecc + " - Con una temperatura de " + obtenerTemperaturaConsulta(fecha) + "° Grados";
        
        return resultado;
    }


    private int obtenerTemperaturaConsulta(String fecha){
        String[] dmy = fecha.split("/");
        int mes= Integer.parseInt(dmy[1]);
        Random rd = new Random();
        int temp=0 ;

        if (mes>=1 && mes<4){
            temp= (15 + rd.nextInt(30));
        }else {
            if ((mes>=4 && mes<7) || (mes>=9 && mes<13))  {
                temp= (5 + rd.nextInt(25));   
            }else{
                temp= (1 +rd.nextInt(19));
            }
        }
    return temp;
    }

}