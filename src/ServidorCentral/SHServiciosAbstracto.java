package ServidorCentral;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface SHServiciosAbstracto extends Remote {

    public String consultarHoroscopo(String signo) throws RemoteException;

}