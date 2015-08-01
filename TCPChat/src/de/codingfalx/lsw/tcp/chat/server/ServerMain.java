package de.codingfalx.lsw.tcp.chat.server;

import java.io.IOException;

/**
 * @author Kristoffer Schneider
 * @created 01.08.2015
 */
public class ServerMain
{
  //<editor-fold desc="Methods">

  public static void main ( String[] args ) throws IOException, InterruptedException
  {
    final int PORT = 22000;
    Server server = new Server( PORT, true );
  }

    //</editor-fold>
}
