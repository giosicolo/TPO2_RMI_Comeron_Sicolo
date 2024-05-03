package ServidorCentral;

import java.rmi.RemoteException;
import java.rmi.Remote; 

public interface SPServiciosAbstracto extends Remote {
    
public String consultarPronostico(String fecha) throws RemoteException;

}
