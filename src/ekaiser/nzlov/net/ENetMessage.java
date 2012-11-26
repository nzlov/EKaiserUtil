
package ekaiser.nzlov.net;

import ekaiser.nzlov.util.CompressionUtil;
import java.io.*;


public class ENetMessage
	implements Externalizable
{

	private static final long serialVersionUID = 0x2ed91c63b1L;
	public String name;
	public String message;

	public ENetMessage()
	{
	}

	public ENetMessage(String tn, String tm)
	{
		name = tn;
		message = tm;
	}

	public String getMessage()
	{
		return message;
	}

	public void setMessage(String message)
	{
		this.message = message;
	}

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public void setData(byte d[])
		throws IOException
	{
		d = CompressionUtil.unZLib(d);
		String ss[] = (new String(d)).split(ENetClient.getDelimiter());
		name = ss[0];
		message = ss[1];
	}

	public byte[] getData()
		throws IOException
	{
		StringBuffer sb = new StringBuffer();
		sb.append(name);
		sb.append(ENetClient.getDelimiter());
		sb.append(message);
		return CompressionUtil.zLib(sb.toString().getBytes());
	}

	public void writeExternal(ObjectOutput out)
		throws IOException
	{
		out.write(getData());
	}

	public void readExternal(ObjectInput in)
		throws IOException, ClassNotFoundException
	{
		setData((byte[])in.readObject());
	}
}
