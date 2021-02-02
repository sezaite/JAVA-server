package serveris;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveris {

    public static final int PORT = 9999;

    public static final String WEB_DIR = "web";

    public static void main(String[] args) throws IOException {
        boolean running = true;
        try (ServerSocket ss = new ServerSocket(PORT);) {
            while (running) {
                try (
                        Socket s = ss.accept();
                        Reader sr = new InputStreamReader(s.getInputStream(), "UTF-8");
                        BufferedReader br = new BufferedReader(sr);
                        Writer sw = new OutputStreamWriter(s.getOutputStream(), "UTF-8");
                        BufferedWriter bw = new BufferedWriter(sw);) {
//                    String line;
//                    do {
//                        line = br.readLine();
//                        if (line != null) {
//                        }
//                    } while (line != null && !"".equals(line));
//                            System.out.println(line);
                    String firstLine = br.readLine();
                    if (firstLine != null) {
                        String[] parts = firstLine.split(" ");
                        if (parts.length != 3 || !"HTTP/1.1".equals(parts[2])) {
                            bw.write("HTTP/1.1 400 Bad Request\r\n");
                            bw.write("\r\n");
                        } else {
                            if ("/end".equals(parts[1])) {
                                bw.write("HTTP/1.1 200 OK\r\n");
                                bw.write("\r\n");
                                bw.write("<html><body><h1>Bye</h1></body></html>\r\n");
                                running = false;
                            } else {
                                String fileName = WEB_DIR + parts[1]; //failo pavadinimas kurio is manes praso
                                File f = new File(fileName); //nurodau skiaustuose pavadinima kurio noriu
                                if (f.exists() && !f.isDirectory()) { //tikrina, ar operacineje sistemoje yra failas ir grazina true arba false;
                                    try (FileInputStream fis = new FileInputStream(f);
                                            Reader fr = new InputStreamReader(fis, "UTF-8");
                                            BufferedReader fbr = new BufferedReader(fr);) {
                                        String content = "";
                                        String fileLine;
                                        while ((fileLine = fbr.readLine()) != null) {
                                            {
                                                content += fileLine + "\r\n";
                                            }
                                            bw.write("HTTP/1.1 200 OK\r\n");
                                            bw.write("\r\n");
                                            bw.write(content);
                                        }
                                        catch (Exception ex){
                                                bw.write("HTTP/1.1 500 internal server error \r\n");
                                    bw.write("\r\n");
                                                }
                                    }else{
                                    bw.write("HTTP/1.1 400 not found \r\n");
                                    bw.write("\r\n");
                                }
                                }
                                bw.write("HTTP/1.1 200 OK\r\n");
                                bw.write("\r\n");
                                bw.write("<html><body><h1>Hello</h1></body></html>\r\n");
                            }
                        }
                    } else {
                        bw.write("HTTP/1.1 400 Bad Request\r\n");
                        bw.write("\r\n");
                    }
                } catch (IOException ex) {
                    System.out.println(ex);
                }
            }
        } catch (IOException ex) {
            System.out.println("Port " + PORT + " already in use");
        }
    }
}
