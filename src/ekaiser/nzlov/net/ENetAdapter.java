
package ekaiser.nzlov.net;

import ekaiser.nzlov.methodmap.EMethodMapManage;

public abstract class ENetAdapter
	implements ENetListener
{

	public ENetAdapter()
	{
	}

	public void connected(ENetClient enetclient)
	{
	}

	public void discconnected(ENetClient enetclient)
	{
	}

	public void getMessage(ENetClient c)
	{
		ENetMessage message = c.getNextMessage();
		EMethodMapManage.sendMethodMessage(message.getName().substring("|EKaiser_".length() + 1), c, message);
	}
}
