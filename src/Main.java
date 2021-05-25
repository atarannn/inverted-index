import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Main {
    public static void main(String[] args) {

        var dataSet = new File("/Users/anastasia_tarannn/Downloads/datasets/1000/");
        var maxTreadsCount = 8;
        ConcurrentHashMap<String, ConcurrentLinkedQueue<WordLocation>> index = null;
        HashMap<Integer, Long> times = new HashMap<>();
        for (var threadsCount = 1; threadsCount <= maxTreadsCount; threadsCount++) {
            long start = System.nanoTime();
            index = buildIndex(dataSet, threadsCount);
            times.put(threadsCount, (System.nanoTime() - start) / 1000000);
        }

        System.out.println(times);
        System.out.println("Index is built\nStarting server...");
        new Server(index);
    }

    public static ConcurrentHashMap<String, ConcurrentLinkedQueue<WordLocation>>
    buildIndex(File dataSet, int threadsCount) {
        var files = dataSet.listFiles();

        var index = new ConcurrentHashMap<String, ConcurrentLinkedQueue<WordLocation>>();
        if (files != null) {
            var threads = new Thread[threadsCount];
            var partLen = files.length / threadsCount;
            for (var i = 0; i < threadsCount; i++) {
                File[] threadFiles;
                if (i != threadsCount - 1) {
                    threadFiles = Arrays.copyOfRange(files, i * partLen, (i + 1) * partLen);
                } else if (i != 0) {
                    threadFiles = Arrays.copyOfRange(files, (i - 1) * partLen, files.length - 1);
                } else {
                    threadFiles = files;
                }
                var thread = new IndexBuilder(index, threadFiles);
                thread.start();
                threads[i] = thread;
            }
            try {
                for (var thread : threads) {
                    thread.join();
                }
            } catch (InterruptedException ignored) {

            }

        }

        return index;
    }
}
