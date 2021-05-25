import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;


class Server {
    ServerSocket server = null;
    Socket client = null;
    ConcurrentHashMap<String, ConcurrentLinkedQueue<WordLocation>> index;

    public Server(ConcurrentHashMap<String, ConcurrentLinkedQueue<WordLocation>> index) {
        this.index = index;
        doConnections();
    }

    public void doConnections() {

        try {
            server = new ServerSocket(11000);
            System.out.println("Server started on " + server.getLocalSocketAddress());
            while (true) {
                client = server.accept();
                System.out.println("New client " + client.getRemoteSocketAddress());
                var ct = new ClientThread(client, index);
                ct.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class ClientThread extends Thread {
    public Socket client = null;
    public DataOutputStream out = null;
    public DataInputStream in = null;
    ConcurrentHashMap<String, ConcurrentLinkedQueue<WordLocation>> index;


    public ClientThread(Socket c, ConcurrentHashMap<String, ConcurrentLinkedQueue<WordLocation>> index) {
        this.index = index;
        try {
            client = c;
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String inputLine;
            while (true) {
                inputLine = in.readUTF();
                var response = constructRespose(inputLine);
                sendResponse(response);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String constructRespose(String query) {
        var response = "";
        try {
            if (index.containsKey(query)) {
                var list = new ArrayList<>(index.get(query));
                response = "[\n\t";
                response += list.stream().map(Object::toString)
                        .collect(Collectors.joining(",\n\t"));
                response += "\n]";
            } else {
                response = "Word " + query + " is not in index";
            }
        } catch (Exception ignored) {
            response = "Wrong query";
        }
        return response;
    }

    private void sendResponse(String response) throws IOException {
        var myByteArray = new byte[response.length()];

        var bis = new BufferedInputStream(new ByteArrayInputStream(response.getBytes(StandardCharsets.UTF_8)));
        var dis = new DataInputStream(bis);

        dis.readFully(myByteArray, 0, myByteArray.length);

        out.writeLong(myByteArray.length);
        out.write(myByteArray, 0, myByteArray.length);
    }
}