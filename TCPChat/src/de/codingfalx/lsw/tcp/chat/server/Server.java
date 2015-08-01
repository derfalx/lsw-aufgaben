package de.codingfalx.lsw.tcp.chat.server;

import de.codingfalx.lsw.tcp.chat.AbstractClient;

import java.io.Closeable;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kristoffer Schneider
 * @created 30.07.2015
 */
public class Server
        implements Closeable
{
  //<editor-fold desc="Static">

  private static Logger logger;
  private HashMap<String, ServerClient> clientMap;

  static
  {
    Server.logger = Logger.getLogger( "ServerLogger" );
    Server.logger.setLevel( Level.CONFIG );
  }

  //</editor-fold>

  //<editor-fold desc="Fields">
  private Thread connectionThread;
  private ConnectionWorker connectionWorker;
  private ServerSocket serverSocket;


  //</editor-fold>

  //<editor-fold desc="Constructor">

  public Server ( int port, boolean start ) throws IOException
  {
    this.clientMap = new HashMap<>();
    this.serverSocket = new ServerSocket( port );
    Server.logger.info( String.format( "Created ServerSocket on port %d.", port ) );
    this.connectionWorker = new ConnectionWorker( this::registerClient, this.serverSocket );
    this.connectionThread = new Thread( this.connectionWorker );

    if ( start )
    {
      this.connectionThread.start();
      Server.logger.info( "Started server" );
    }
  }

  //</editor-fold>

  //<editor-fold desc="Methods">

  @Override
  public void close () throws IOException
  {
    if ( !this.connectionThread.isAlive() )
    {
      Server.logger.info( "Shutting down the Server.." );
      this.connectionWorker.stop();
      for ( ServerClient c : this.clientMap.values() )
        c.close();

      this.serverSocket.close();
      Server.logger.info( "..finished shutting down." );
    }
  }

  public void deregisterClient ( AbstractClient client )
  {
    this.clientMap.remove( client.getName() );
    try
    {
      client.close();
    }
    catch ( IOException e )
    {
      Server.logger.log( Level.WARNING,
                         String.format( "An exception occured while deregistering \"%s\".", client.getName() ),
                         e );
    }
    this.sendBroadcastMessage( client, ".. left the room." );
    Server.logger.info( String.format( "Deregistered client \"%s\".", client.getName() ) );
  }

  public void registerClient ( AbstractClient newClient )
  {
    this.clientMap.put( newClient.getName(), (ServerClient)newClient );
    newClient.setMessageReceived( this::sendBroadcastMessage );
    newClient.setClientDisconnected( this::deregisterClient );
    newClient.startReceiving();
    this.sendBroadcastMessage( newClient, ".. joined the room." );
    Server.logger.info( String.format( "Registered new client \"%s\".", newClient.getName() ) );
  }

  public void start ()
  {
    if ( connectionThread.isAlive() )
    {
      this.connectionThread.start();
      Server.logger.info( "Started server" );
    }
  }

  private void sendBroadcastMessage ( AbstractClient client, String msg )
  {
    msg = String.format( "%s: %s", client.getName(), msg );

    for ( ServerClient c : this.clientMap.values() )
    {
      if ( !c.getName().equals( client.getName() ) )
      {
        c.sendMessage( msg );
      }
    }
    Server.logger.info( String.format( "Message from %s broadcasted.", client.getName() ) );
  }

  //</editor-fold>
}
