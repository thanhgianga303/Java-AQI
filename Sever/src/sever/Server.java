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
import org.json.JSONArray;
import org.json.JSONObject;

public class Server {

    public static int buffsize = 512;
    public static int port = 1234;
    public static String _urlApi = "https://api.airvisual.com/v2/";
    public static String _key = "b57d001e-31c7-499f-9433-f6d2762863ea";
    public static String country = "";
    public static String state = "";
    public static String city = "";

    public static void main(String[] args) throws MalformedURLException, IOException {
        DatagramSocket socket;
        DatagramPacket dpreceive, dpsend;
        try {
            socket = new DatagramSocket(1234);
            dpreceive = new DatagramPacket(new byte[buffsize], buffsize);
            while (true) {
                socket.receive(dpreceive);
                String tmp = new String(dpreceive.getData(), 0, dpreceive.getLength());
				System.out.println("Server received: " + tmp + " from " + 
						dpreceive.getAddress().getHostAddress() + " at port " + 
						socket.getLocalPort());
                                  if(!tmp.contains(";"))
                                  {
                                      if(tmp.equals("hello"))
                                      {
                                          JSONObject Countries=getListData(_urlApi + "countries?key=" + _key);
                                          tmp= "\n*****Countries*****"+getDataToString(Countries,"country");
                                      }
                                      else
                                      {
                                          tmp=tmp.replace(" ", "+");
                                          System.out.println(tmp);
                                          JSONObject StatesInACountry = getListData(_urlApi + "states?country="+tmp+"&key=" + _key);
                                          if(StatesInACountry==null)
                                          {
                                              tmp="\nnot found state";
                                          }
                                          else
                                          {
                                              tmp="\n*****States*****"+ getDataToString(StatesInACountry,"state");
                                          }
                                      }              
                                  }
                                  else
                                  {
                                      
                                     String[] st = tmp.split(";");
                                     st=editStringArray(st);
                                     for(int i=0;i<st.length;i++)
                                    {
                                     System.out.println(st[i]);
                                    }
                                     switch(st.length) {
                                    case 2:
                                      country=st[0];
                                      state=st[1];
                                      JSONObject CitiesInAState = getListData(_urlApi + "cities?state="+state+"&country="+country+"&key=" + _key);
                                      if(CitiesInAState==null)
                                          {
                                              tmp="\nnot found city";
                                          }
                                          else
                                          {
                                              tmp= "\n*****Cities***** "+getDataToString(CitiesInAState,"city");
                                          }
                                      break;
                                    case 3:
                                      country=st[0];
                                      state=st[1];
                                      city=st[2];
                                      JSONObject SpecifiedCity = getListData(_urlApi + "city?city="+city+"&state="+state+"&country="+country+"&key=" + _key);
                                      if(SpecifiedCity==null)
                                          {
                                              tmp="\nnot found aqi";
                                          }
                                      
                                          else
                                          {
                                              tmp="\nAQI:"+getDataToString(SpecifiedCity,"pollution");;
                                          }
                                      break;
                                    default:
                                      tmp="error";
                                  }

                                  }
                if (tmp.equals("bye")) {
                    System.out.println("Server socket closed");
                    socket.close();
                    break;
                }
                // Uppercase, sent back to client
                dpsend = new DatagramPacket(tmp.getBytes(), tmp.getBytes().length,
                        dpreceive.getAddress(), dpreceive.getPort());
                System.out.println("Server sent back " + tmp + " to client");
                socket.send(dpsend);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }
    public static String[] editStringArray(String[] str)
    {
        for(int i=0;i<str.length;i++)
        {
            str[i]=str[i].replace(" ", "+");
        }
        return str;
    }
    public static JSONObject getListData(String _url) throws MalformedURLException, IOException {
        URL url = new URL(_url);
        String inline = "";
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        int responsecode = conn.getResponseCode();
        System.out.print(responsecode);
        if (responsecode != 200) {
            return null;
        } else {
            Scanner sc = new Scanner(url.openStream());
            while (sc.hasNext()) {
                inline += sc.nextLine();
            }
            System.out.println("\n Json data in string format");
            System.out.println(inline);
            sc.close();
        }
        JSONObject jobj = new JSONObject(inline);
        return jobj;
    }
    public static String getDataToString(JSONObject jobj, String nameDataList)
    {
        String data="\n";
        if(nameDataList.equals("pollution"))
        {
            JSONObject element = jobj.getJSONObject("data").getJSONObject("current").getJSONObject("pollution");
            data=String.valueOf(element.getInt("aqius"));
        }
        else
        {
            JSONArray jArray= jobj.getJSONArray("data");
            for (int i = 0; i < jArray.length(); i++) {
                
                data += jArray.getJSONObject(i).getString(nameDataList) +"\n";
            }
            
        }
        return data;
    }

}
