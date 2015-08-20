package diag;

/**
 * Created by someone on 16-Jun-2015.
 */
public class Elapsed {
    public final long nanos;
    public final long milis;
    public final long seconds;
    public final long minutes;

    public Elapsed(long nanosElapsed) {
        nanos = nanosElapsed;
        milis = (long) (nanos / 1E6);
        seconds = (long) (nanos / 1E9);
        minutes = seconds / 60;
    }

    @Override
    public String toString() {
        return String.format("ELAPSED: %d m : %d s : %d ms : %d ns", minutes, seconds, milis, nanos);
    }

    public static void main(String... args) {
        System.out.println(String.format("%d", 10));
    }
}
