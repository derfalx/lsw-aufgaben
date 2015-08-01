package de.codingfalx.lsw.tcp.chat.client;

import de.codingfalx.lsw.tcp.chat.AbstractClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

/**
 * @author Kristoffer Schneider
 * @created 01.08.2015
 */
public class ClientMain
{
  //<editor-fold desc="Methods">

  private static volatile boolean running;

  public static void main ( String[] args ) throws IOException, InterruptedException
  {
    final int PORT = 22000;

    String input = "";
    BufferedReader br = new BufferedReader( new InputStreamReader( System.in ) );
    System.out.println( "Please enter your name: " );
    String name = br.readLine();
    Socket socket = new Socket( InetAddress.getLocalHost(), PORT );
    Client client = new Client( socket, name );
    client.setClientDisconnected( (AbstractClient c) -> running = false );
    client.setMessageReceived( (AbstractClient c, String msg) -> System.out.println ( msg ) );
    client.startReceiving();

    running = true;
    while ( running )
    {
      input = br.readLine();
      if ( input.equals( "exit" ) )
      {
        running = false;
        break;
      }
      client.sendMessage( input );
    }

    client.close();
  }


  //</editor-fold>
}
