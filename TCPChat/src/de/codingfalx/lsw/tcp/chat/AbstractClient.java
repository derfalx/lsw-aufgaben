package de.codingfalx.lsw.tcp.chat;

import java.io.*;
import java.net.Socket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Kristoffer Schneider
 * @created 01.08.2015
 */
public abstract class AbstractClient
        implements Closeable
{
  // <editor-fold Desc="Static">
  
  protected static Logger logger;

  static
  {
    AbstractClient.logger = Logger.getLogger( "ClientLogger" );
    AbstractClient.logger.setLevel( Level.CONFIG );
  }
  
  // </editor-fold>
  
  // <editor-fold Desc="Fields">
  
  protected final BufferedReader inputStream;
  protected String name;
  protected final PrintWriter outputStream;
  protected final Socket socket;
  protected Thread receivingThread;
  protected BiConsumer<AbstractClient, String> messageReceived;
  protected Consumer<AbstractClient> clientDisconnected;
  private volatile boolean running;
  
  // </editor-fold>

  // <editor-fold Desc="Constructor">
  
  public AbstractClient ( Socket socket ) throws IOException
  {
    this.socket = socket;
    this.receivingThread = new Thread( this::receivingWorking );
    this.outputStream = new PrintWriter( socket.getOutputStream(), true );
    this.inputStream = new BufferedReader( new InputStreamReader( socket.getInputStream() ) );
  }
  
  // </editor-fold>

  // <editor-fold Desc="Methods">
  
  @Override
  public void close () throws IOException
  {
    this.running = false;

    this.socket.close();
  }

  public String getName ()
  {
    return this.name;
  }

  public synchronized void sendMessage ( String msg )
  {
    this.outputStream.println( msg );
    if ( this.outputStream.checkError() )
    {
      this.outputStream.print( msg );
      if ( this.outputStream.checkError() )
        AbstractClient.logger.log( Level.WARNING, "An error occured while sending a message to " + this.name );
    }
  }

  public void setClientDisconnected ( Consumer<AbstractClient> clientDisconnected )
  {
    this.clientDisconnected = clientDisconnected;
  }

  public void setMessageReceived ( BiConsumer<AbstractClient, String> sendMessage )
  {
    this.messageReceived = sendMessage;
  }

  public synchronized void startReceiving()
  {
    if ( !this.running )
      this.receivingThread.start();
  }

  private void receivingWorking ()
  {
    this.running = true;

    while ( this.running )
    {
      try
      {
        String msg = this.inputStream.readLine();
        if ( msg == null )
          throw new IOException ( "Received null message from client" );

        this.messageReceived.accept( this, msg );
      }
      catch ( IOException e )
      {
        AbstractClient.logger.log( Level.WARNING, "Exception occured while reading from client " + this.name, e );
        AbstractClient.logger.log( Level.INFO, String.format( "Connection to client %s is closed.", this.name ) );
        this.clientDisconnected.accept( this );
        this.running = false;
      }
    }

    this.running = false;
  }

  //</editor-fold>
}
