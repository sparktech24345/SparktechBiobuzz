package ro.sparktech24345.logicore.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Array that maintains elements in descending order by weight.
 * Higher weight elements are updated first during module execution.
 */
public class WeightedArray<T> {
    public static class Weighted<Ty> {
        public Weighted(Ty value, double weight) {
            this.value = value;
            this.weight = weight;
        }
        public Ty value;
        public double weight;
    }
    private final ArrayList<Weighted<T>> arr = new ArrayList<>();

    public void plusAssign(Weighted<T> entry) {
        add(entry);
    }

    public void add(Weighted<T> entry) {
        this.add(entry.value, entry.weight);
    }

    /**
     * Add an element with the specified weight.
     * Elements are automatically inserted in descending weight order.
     */
    public void add(T value, double weight) {
        Weighted<T> newEntry = new Weighted<>(value, weight);

        int low = 0;
        int high = arr.size();

        while (low < high) {
            int mid = (low + high) >>> 1;

            if (arr.get(mid).weight >= weight) {
                low = mid + 1;
            } else {
                high = mid;
            }
        }

        arr.add(low, newEntry);
    }

    public Weighted<T> get(int index) { return arr.get(index); }

    public int size() { return arr.size(); }

    public void remove(int index) { arr.remove(index); }

    public List<Weighted<T>> list() { return arr; }
    public void clear() { arr.clear(); }
}
