package Cliente;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SCServiciosAbstracto extends Remote {

    public String consultarHoroscopoClima(String consulta) throws RemoteException;
    
}
