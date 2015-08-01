package de.codingfalx.lsw.tcp.chat.server;

import de.codingfalx.lsw.tcp.chat.AbstractClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kristoffer Schneider
 * @created 30.07.2015
 */
public class ConnectionWorker implements Runnable
{
  //<editor-fold desc="Static">
  private static Logger logger;

  static
  {
    ConnectionWorker.logger = Logger.getLogger( "ConnectionWorkerLogger" );
    ConnectionWorker.logger.setLevel( Level.CONFIG );
  }
  //</editor-fold>


  //<editor-fold desc="Fields">

  private final Consumer<AbstractClient> clientRegistration;
  private final ServerSocket socketToListenAt;

  private volatile boolean running;

  //</editor-fold>

  //<editor-fold desc="Constructor">

  public ConnectionWorker ( Consumer<AbstractClient> clientRegistration, ServerSocket socketToListenAt )
  {
    this.clientRegistration = clientRegistration;
    this.socketToListenAt = socketToListenAt;
  }

  //</editor-fold>

  //<editor-fold desc="Methods">

  @Override
  public void run ()
  {
    running = true;

    while ( running )
    {
      try
      {
        Socket incoming = this.socketToListenAt.accept();
        ServerClient client = new ServerClient( incoming );
        this.clientRegistration.accept( client );
      }
      catch ( IOException e )
      {
        ConnectionWorker.logger.log( Level.WARNING, "Exception thrown while waiting for new clients", e );
      }
    }

    running = false;
  }

  public void stop ()
  {
    this.running = false;
  }

  //</editor-fold>
}
