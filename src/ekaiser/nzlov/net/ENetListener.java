
package ekaiser.nzlov.net;


public interface ENetListener
{

	public static final String FLAG = "|EKaiser_";
	public static final String PING = "|EKaiser_ping";
	public static final String REPING = "|EKaiser_|EKaiser_pingre";
	public static final String ERROR = "|EKaiser_error";

	public abstract void connected(ENetClient enetclient);

	public abstract void discconnected(ENetClient enetclient);

	public abstract void getMessage(ENetClient enetclient);
}
