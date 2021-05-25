import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
    Socket socket;
    DataOutputStream out;
    DataInputStream in;
    BufferedReader stdIn;

    public static void main(String[] args) {
        var c = new Client();
        c.connectToServer();
    }

    private void connectToServer() {
        while (true) {
            var hostName = "127.0.0.1";
            int portNumber = 11000;

            System.out.println("Connecting to server");
            try {
                socket = new Socket(hostName, portNumber);
                out = new DataOutputStream(socket.getOutputStream());
                in = new DataInputStream(socket.getInputStream());
                stdIn = new BufferedReader(new InputStreamReader(System.in));

                String userInput;
                System.out.println("Print query:");
                while ((userInput = stdIn.readLine()) != null) {
                    out.writeUTF(userInput);

                    var searchResult = handleResponse();
                    System.out.println(searchResult);
                    System.out.println("Print query:");
                }
            } catch (UnknownHostException e) {
                System.err.println("Don't know about host " + hostName);

            } catch (IOException e) {
                System.err.println("Couldn't get I/O for the connection to " +
                        hostName);
            }
        }
    }

    private String handleResponse() throws IOException {
        int bytesRead;
        var size = in.readLong();
        var output = new ByteArrayOutputStream((int) size);
        var buffer = new byte[1024];

        bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, size));

        while (size > 0 && bytesRead != -1) {
            output.write(buffer, 0, bytesRead);
            size -= bytesRead;
            bytesRead = in.read(buffer, 0, (int) Math.min(buffer.length, size));
        }

        return output.toString();
    }
}
