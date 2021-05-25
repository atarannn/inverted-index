import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Pattern;

public class IndexBuilder extends Thread {
    private final ConcurrentHashMap<String, ConcurrentLinkedQueue<WordLocation>> index;
    private final Pattern regex = Pattern.compile("[^a-zA-Z0-9-]");
    private final File[] files;

    public IndexBuilder(ConcurrentHashMap<String, ConcurrentLinkedQueue<WordLocation>> index, File[] files) {
        this.index = index;
        this.files = files;
    }

    private void indexFile(File file) {
        try {
            var fileScanner = new Scanner(file);
            var pos = 0;
            while (fileScanner.hasNextLine()) {
                var line = fileScanner
                        .nextLine()
                        .toLowerCase();
                var words = regex.split(line);

                for (var word : words) {
                    index.putIfAbsent(word, new ConcurrentLinkedQueue<>());
                    index.get(word).add(new WordLocation(pos++, file.getAbsolutePath()));
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(){
        for (var file : files){
            indexFile(file);
        }
    }
}