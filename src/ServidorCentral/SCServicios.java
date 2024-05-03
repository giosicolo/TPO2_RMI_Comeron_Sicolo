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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                            if (respuestaPronostico == null) {
                                respuestaPronostico= consultarPronosticoClima(consultaPronosticoClima);
                            }
                        }else 
                        { respuestaPronostico = "Fecha no valida";}    
                        }else{
                            respuestaHoroscopo = "Signo no valido";
                            respuestaPronostico = "Fecha no valida";
                        }
                    }    
        } catch (Exception e) {
            res = "Formato de consulta invalido";
        }
        
        res = "\n" + "Respuesta: \n" + "Horoscopo: "+respuestaHoroscopo+"\n"
            + "Pronostico Clima: "+respuestaPronostico +"\n" ;
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
    
    private String consultarPronosticoClima(String fecha) throws RemoteException {
        String res;
        SPServiciosAbstracto server;
                try {
                    server = (SPServiciosAbstracto) Naming.lookup("//" + ip_SP + ":" + puerto_SP + "/SPServicios");
                    res = server.consultarPronostico(fecha);
                    historialPronosticoClima.put(fecha, res);
                } catch (Exception ex) {
                    res = "El servidor del pronostico no esta disponible, consulte mas tarde.";
                    System.out.println("Servidor> Servidor de horoscopo no responde.");
                }
        return res;
    }

    private boolean validarConsultaPronosticoClima(String fecha) {
            // Expresion regular para verificar el formato dd/mm/yyyy
            String regex = "^(0?[1-9]|[12][0-9]|3[01])/(0?[1-9]|1[0-2])/((19|20)\\d\\d)$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(fecha);
    
            if (matcher.matches()) {
                int dia = Integer.parseInt(matcher.group(1));
                int mes = Integer.parseInt(matcher.group(2));
                int anio = Integer.parseInt(matcher.group(3));
    
                // Verifica la validez de la fecha
                if (mes == 2) {
                    if (dia >= 1 && dia <= (esBisiesto(anio) ? 29 : 28)) {
                        return true;
                    }
                } else if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia >= 1 && dia <= 30) {
                    return true;
                } else if (dia >= 1 && dia <= 31) {
                    return true;
                }
            }
            return false;  
    }

    private static boolean esBisiesto(int anio) {
        return (anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0);
    } 
}
