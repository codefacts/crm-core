import java.io.IOException;

import static java.util.stream.Collectors.toMap;

/**
 * Created by sohan on 8/1/2015.
 */
public class TestDb {
    public static void main(String... args) throws IOException {

    }
    static class Entry {
        final long id;
        final String name;

        Entry(long id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Entry)) return false;

            Entry entry = (Entry) o;

            if (id != entry.id) return false;
            if (!(name != null ? !name.equals(entry.name) : entry.name != null)) {
                return true;
            } else {
                System.err.println("Name does not match: " + this + " and " + o);
            }
            return false;
        }

        @Override
        public int hashCode() {
            int result = (int) (id ^ (id >>> 32));
            result = 31 * result + (name != null ? name.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "\nEntry{" +
                    "id=" + id +
                    ", name='" + name + '\'' +
                    '}' + "\n";
        }
    }
}
