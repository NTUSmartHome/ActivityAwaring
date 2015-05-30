package elements;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SparseMatrix<T> {
    private final Map<Coordinates, T> map = new HashMap<Coordinates, T>();
    private final T defaultValue;

    public SparseMatrix(T defaultValue) {
       this.defaultValue = defaultValue;
    }

    private static class Coordinates {
        private final int[] coordinates;

        Coordinates(int... coordinates) {
            this.coordinates = coordinates;
        }

        @Override
        public boolean equals(Object o) {
            return Arrays.equals(coordinates, ((Coordinates)o).coordinates);
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(coordinates);
        }
    }

    public T get(int x, int y) {
        T value = map.get(new Coordinates(x, y));
        if ( value == null ) {
            return defaultValue;
        }
		return value;
    }

    public T set(int x, int y, T val) {
        return map.put(new Coordinates(x, y), val);
    }
} 