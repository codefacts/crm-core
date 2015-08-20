package diag;

/**
 * Created by someone on 16-Jun-2015.
 */
public class Watch {
    private long start;
    private long end;

    public Watch start() {
        start = System.nanoTime();
        return this;
    }

    public Watch end() {
        end = System.nanoTime();
        return this;
    }

    public long getStart() {
        return start;
    }

    public long getEnd() {
        return end;
    }

    public Elapsed elapsed() {
        return new Elapsed(end - start);
    }
}
