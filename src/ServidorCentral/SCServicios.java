package ServidorCentral;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import Cliente.SCServiciosAbstracto;

public class SCServicios extends UnicastRemoteObject implements SCServiciosAbstracto {
    
    private static int puerto_SH;
    private static String ip_SH;
    private static int puerto_SP;
    private static String ip_SP;

    private static ConcurrentHashMap<String, String> historialHoroscopo;
    private static ConcurrentHashMap<String, String> historialPronosticoClima;
 
   
    public SCServicios(String ipPronostico, int puertoPronostico, String ipHoroscopo, int puertoHoroscopo, ConcurrentHashMap<String, String> cachePronostico, ConcurrentHashMap<String, String> cacheHoroscopo ) throws RemoteException {
        
        this.ip_SP = ipPronostico;
        this.puerto_SP = puertoPronostico;
        this.ip_SH= ipHoroscopo;
        this.puerto_SH = puertoHoroscopo;
        this.historialHoroscopo = cacheHoroscopo;
        this.historialPronosticoClima = cachePronostico;
    }

    @Override
    public String consultarHoroscopoClima(String consulta) throws RemoteException {
        String res="";
        String respuestaHoroscopo="";
        String respuestaPronostico="";

        System.out.println("Cliente> Consulta: " + consulta);

        /*extraer fecha y horoscopo de query*/
        try {
            if (consulta.isBlank() || consulta.isEmpty()){ 
                    res = "ServidorCentral> Error: Formato de consulta no valido.";}
                else { 
                    if (consulta.matches("[a-zA-Z]+ \\d{2}/\\d{2}/\\d{4}")) {
                        String consultaHoroscopo = consulta.split(" ")[0].toLowerCase();
                        String consultaPronosticoClima = consulta.split(" ")[1];
                        
                        Socket socketClienteSH = null;
         
                        
                        if (validarConsultaHoroscopo(consultaHoroscopo)) {
                            respuestaHoroscopo = historialHoroscopo.get(consultaHoroscopo);
                            if (respuestaHoroscopo == null) {
                                respuestaHoroscopo= consultarHoroscopo(consultaHoroscopo);
                            } 
                        }
                        else 
                           { respuestaHoroscopo = "Signo no valido";}
                    
                        
                        if (validarConsultaPronosticoClima(consultaPronosticoClima)) {
                            respuestaPronostico= historialPronosticoClima.get(consultaPronosticoClima);
                            if (respuestaPronostico==null) {
                                respuestaPronostico="";
                            }
                        }else 
                        { respuestaPronostico = "Fecha no valida";}
                        
                        
                        }
                    }    
        } catch (Exception e) {
            res = "Formato de consulta invalido";
        }
        
        res=  respuestaHoroscopo + "---" + respuestaPronostico; 
        return res;
    }

     private String consultarHoroscopo(String signo) throws RemoteException {
        String res;
        SHServiciosAbstracto server;
                try {
                    server = (SHServiciosAbstracto) Naming.lookup("//" + ip_SH + ":" + puerto_SH + "/SHServicios");
                    res = server.consultarHoroscopo(signo);
                    historialHoroscopo.put(signo, res);
                } catch (Exception ex) {
                    res = "El servidor de horoscopo no esta disponible, consulte mas tarde.";
                    System.out.println("Servidor> Servidor de horoscopo no responde.");
                }
        return res;
    }

   private boolean validarConsultaHoroscopo(String signoHoroscopo) {
        String[] signos = {"aries", "tauro", "geminis", "cancer", "leo", "virgo", "libra", "escorpio", 
                           "sagitario", "capricornio", "acuario", "piscis"};
        boolean esSignoValido = false;
        
        int i = 0;
        while ((i < signos.length) && (!esSignoValido)) {
            if (signos[i].equals(signoHoroscopo)) 
                esSignoValido = true;
            i++;
        }
        return esSignoValido;
    }
    
    private boolean validarConsultaPronosticoClima(String fechaPronosticoClima) {
        boolean esFechaValida = true;
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        formatoFecha.setLenient(false);
        
        try {
            formatoFecha.parse(fechaPronosticoClima);
        } catch (ParseException e) {
            esFechaValida = false;
        }
        return esFechaValida;
    }
}
