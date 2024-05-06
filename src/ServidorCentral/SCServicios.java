package ServidorCentral;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Cliente.SCServiciosAbstracto;

public class SCServicios extends UnicastRemoteObject implements SCServiciosAbstracto {
    
    private static String IP_SH;
    private static String IP_SP;
    private static int PUERTO_SH;
    private static int PUERTO_SP;
    private ConcurrentHashMap<String, String> historialHoroscopo;
    private ConcurrentHashMap<String, String> historialPronosticoClima;
 
    public SCServicios(String ipPronostico, int puertoPronostico, String ipHoroscopo, int puertoHoroscopo, ConcurrentHashMap<String, String> cachePronostico, ConcurrentHashMap<String, String> cacheHoroscopo) throws RemoteException {
        this.IP_SP = ipPronostico;
        this.PUERTO_SP = puertoPronostico;
        this.IP_SH = ipHoroscopo;
        this.PUERTO_SH = puertoHoroscopo;
        this.historialHoroscopo = cacheHoroscopo;
        this.historialPronosticoClima = cachePronostico;
    }

    @Override
    public String consultarHoroscopoClima(String consulta) throws RemoteException {
        String respuestaConsulta = "";
        String respuestaHoroscopo = "";
        String respuestaPronostico = "";

        System.out.println("ServidorCentral> Atendiendo consulta: " + consulta);

        try {
            // Se verifica que la consulta ingresada mantenga el formato establecido: signo fecha (dd/mm/aaaa) 
            if (consulta.matches("[a-zA-Z]+ \\d{2}/\\d{2}/\\d{4}")) {
                // Si cumple con el patron, se extraen los datos de la consulta
                String consultaHoroscopo = consulta.split(" ")[0].toLowerCase();
                String consultaPronosticoClima = consulta.split(" ")[1];
                
                // Se valida que el signo ingresado sea correcto y se busca en la cache de horoscopo
                if (validarConsultaHoroscopo(consultaHoroscopo)) {
                    respuestaHoroscopo = historialHoroscopo.get(consultaHoroscopo);

                    // Si no esta en la cache, se traslada la consulta al objeto remoto del servidor de horoscopo
                    if (respuestaHoroscopo == null) {
                        respuestaHoroscopo = consultarHoroscopo(consultaHoroscopo);
                    } 
                     else {
                        System.out.println("ServidorCentral> Acierto de cache: " + consultaHoroscopo);
                    }
                }
                else { 
                    respuestaHoroscopo = "Signo no valido";
                }
                    
                // Se valida que la fecha ingresada sea correcta y se busca en la cache de pronostico clima
                if (validarConsultaPronosticoClima(consultaPronosticoClima)) {
                    respuestaPronostico = historialPronosticoClima.get(consultaPronosticoClima);

                    // Si no esta en la cache, se traslada la consulta al objeto remoto del servidor de pronostico clima
                    if (respuestaPronostico == null) {
                        respuestaPronostico = consultarPronosticoClima(consultaPronosticoClima);
                    }
                    else {
                        System.out.println("ServidorCentral> Acierto de cache: " + consultaPronosticoClima);
                    }
                }
                else { 
                    respuestaPronostico = "Fecha no valida";
                }

                respuestaConsulta = "\nHoroscopo: " + respuestaHoroscopo + 
                                    "\nPronostico Clima: "+ respuestaPronostico;    
            }
            else {
                respuestaConsulta = "\nError: Formato de consulta no valido.";
            }  
        } catch (Exception e) {
            System.out.println("ServidorCentral> Error: " + e.getMessage());
        }
        
        System.out.println("ServidorCentral> Respuesta enviada al Cliente. \n");

        return respuestaConsulta;
    }

    // Metodo que comprueba si el signo ingresado por el usuario es valido
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

    // Metodo que comprueba si la fecha ingresada por el usuario es valida (rango de valores de fechas, meses)
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
            } 
            else {
                if ((mes == 4 || mes == 6 || mes == 9 || mes == 11) && dia >= 1 && dia <= 30) {
                    return true;
                } 
                else {
                    if (dia >= 1 && dia <= 31) {
                        return true;
                    }
                }
            }
        }

        return false;  
    }

    // Metodo utilizado en validarConsultaPronosticoClima(String fecha)
    private static boolean esBisiesto(int anio) {
        return (anio % 4 == 0 && anio % 100 != 0) || (anio % 400 == 0);
    } 
    
    // Metodo que traslada la consulta al Servidor Horoscopo, a traves de la referencia a su objeto remoto que se encuentra en el registro del SH  
    private String consultarHoroscopo(String signo) throws RemoteException {
        String respuestaHoroscopo;
        SHServiciosAbstracto serverHoroscopo;

        try {
            // Se busca la referencia al objeto remoto de ServidorHoroscopo en el registro de dicha direccion
            serverHoroscopo = (SHServiciosAbstracto) Naming.lookup("//" + IP_SH + ":" + PUERTO_SH + "/SHServicios");

            // Invocamos el servicio que nos provee ServidorHoroscopo por medio de su objeto remoto. Se consulta por un horoscopo para el signo dado
            respuestaHoroscopo = serverHoroscopo.consultarHoroscopo(signo);

            // Se agrega la nueva entrada a la cache del Horoscopo
            historialHoroscopo.put(signo, respuestaHoroscopo);
        } catch (Exception ex) {
            respuestaHoroscopo = "El servidor de horoscopo no esta disponible, consulte mas tarde.";
            System.out.println("ServidorCentral> ServidorHoroscopo no responde.");
        }
        return respuestaHoroscopo;
    }

    // Metodo que traslada la consulta al Servidor Pronostico Clima, a traves de la referencia a su objeto remoto que se encuentra en el registro del SPC
    private String consultarPronosticoClima(String fecha) throws RemoteException {
        String respuestaPronostico;
        SPServiciosAbstracto serverClima;

        try {
            // Se busca la referencia al objeto remoto de ServidorPronosticoCLima en el registro de dicha direccion
            serverClima = (SPServiciosAbstracto) Naming.lookup("//" + IP_SP + ":" + PUERTO_SP + "/SPServicios");

            // Invocamos el servicio que nos provee ServidorPronosticoClima por medio de su objeto remoto. Se consulta por un clima para la fecha dada
            respuestaPronostico = serverClima.consultarPronostico(fecha);  

            // Se agrega la nueva entrada a la cache del Clima
            historialPronosticoClima.put(fecha, respuestaPronostico);
        } catch (Exception ex) {
            respuestaPronostico = "El servidor del pronostico no esta disponible, consulte mas tarde.";
            System.out.println("ServidorCentral> ServidorPronostico Clima no responde.");
        }
        return respuestaPronostico;
    }
}