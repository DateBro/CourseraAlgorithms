import edu.princeton.cs.algs4.StdRandom;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @author DateBro
 */

public class RandomizedQueue<Item> implements Iterable<Item> {

    private Item[] queue;
    private int index = 0;

    // construct an empty randomized queue
    public RandomizedQueue() {
        queue = (Item[]) new Object[1];
    }

    // is the randomized queue empty?
    public boolean isEmpty() {
        return index == 0;
    }

    // return the number of items on the randomized queue
    public int size() {
        return index;
    }

    // add the item
    public void enqueue(Item item) {
        if (item == null) {
            throw new IllegalArgumentException();
        } else {
            if (index == queue.length) resize(2 * queue.length);
            queue[index++] = item;
        }
    }

    // remove and return a random item
    public Item dequeue() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            int randomIndex = StdRandom.uniform(index);
            Item temp = queue[randomIndex];
            queue[randomIndex] = queue[--index];
            queue[index] = null;
            if (index > 0 && index == queue.length / 4) resize(queue.length / 2);

            return temp;
        }
    }

    // return a random item (but do not remove it)
    public Item sample() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else {
            int randomIndex = StdRandom.uniform(index);
            return queue[randomIndex];
        }
    }


    // return an independent iterator over items in random order
    public Iterator<Item> iterator() {
        return new RandomizedIterator();
    }

    private void resize(int capacity) {
        Item[] copy = (Item[]) new Object[capacity];
        for (int i = 0; i < index; i++)
            copy[i] = queue[i];
        queue = copy;
    }

    private class RandomizedIterator implements Iterator<Item> {

        RandomizedIterator() {
            StdRandom.shuffle(queue, 0, index);
        }

        private int i = 0;

        @Override
        public boolean hasNext() {
            return i < index;
        }

        @Override
        public Item next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            } else {
                return queue[i++];
            }
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}
