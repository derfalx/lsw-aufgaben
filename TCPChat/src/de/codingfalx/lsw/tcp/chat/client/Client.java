package de.codingfalx.lsw.tcp.chat.client;

import de.codingfalx.lsw.tcp.chat.AbstractClient;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Kristoffer Schneider
 * @created 01.08.2015
 */
public class Client extends AbstractClient
{
  //<editor-fold desc="Constructor">

  public Client ( Socket socket, String name ) throws IOException
  {
    super( socket );
    this.name = name;
    this.outputStream.println( name );
  }

  //</editor-fold>
}
