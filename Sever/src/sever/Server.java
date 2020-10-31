/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sever;

/**
 *
 * @author Giang
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Server {
	public static int buffsize = 512;
	public static int port = 1234;
	public static void main(String[] args) throws MalformedURLException, IOException, ParseException {
		DatagramSocket socket;
		DatagramPacket dpreceive, dpsend;
                JSONObject test=getDataCountryApi();
		try {
			socket = new DatagramSocket(1234);
			dpreceive = new DatagramPacket(new byte[buffsize], buffsize);
			while(true) {
				socket.receive(dpreceive);
				String tmp = new String(dpreceive.getData(), 0 , dpreceive.getLength());
				System.out.println("Server received: " + tmp + " from " + 
						dpreceive.getAddress().getHostAddress() + " at port " + 
						socket.getLocalPort());
				if(tmp.equals("bye")) {
					System.out.println("Server socket closed");
					socket.close();
					break;
				}
				// Uppercase, sent back to client
				tmp = tmp.toUpperCase();
				dpsend = new DatagramPacket(tmp.getBytes(), tmp.getBytes().length, 
						dpreceive.getAddress(), dpreceive.getPort());
				System.out.println("Server sent back " + tmp + " to client");
				socket.send(dpsend);
			}
		} catch (IOException e) { System.err.println(e);}
	}
        public static JSONObject getDataCountryApi() throws MalformedURLException, IOException, ParseException
        {  
            URL url = new URL("https://api.airvisual.com/v2/countries?key=b57d001e-31c7-499f-9433-f6d2762863ea"); 
            String inline="";
            HttpURLConnection conn = (HttpURLConnection)url.openConnection(); 
            conn.setRequestMethod("GET"); 
            conn.connect();
            int responsecode = conn.getResponseCode(); 
            System.out.print(responsecode);
            if(responsecode!=200)
            {
                throw new RuntimeException("HttpResponseCode:" );
            }
            else
            {
                Scanner sc = new Scanner(url.openStream());
                while(sc.hasNext())
                {
                    inline+=sc.nextLine();
                }
                System.out.println("\n Json data in string format");
                System.out.println(inline);
                sc.close();
            }
            JSONParser parse = new JSONParser(); 
            JSONObject jobj = (JSONObject)parse.parse(inline); 
            return jobj;
        }

         
}
