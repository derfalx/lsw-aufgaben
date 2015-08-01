package de.codingfalx.lsw.tcp.chat.server;

import de.codingfalx.lsw.tcp.chat.AbstractClient;

import java.io.IOException;
import java.net.Socket;

/**
 * @author Kristoffer Schneider
 * @created 01.08.2015
 */
public class ServerClient extends AbstractClient
{
  //<editor-fold desc="Constructor">

  public ServerClient ( Socket socket ) throws IOException
  {
    super( socket );
    this.name = this.inputStream.readLine();
  }

  //</editor-fold>
}
