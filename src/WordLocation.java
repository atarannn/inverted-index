public class WordLocation {

    public int position;
    public String file;

    public WordLocation(int pos, String file) {
        this.position = pos;
        this.file = file;
    }

    @Override
    public String toString(){
        return "{" + position + ", " + file + "}";
    }
}
