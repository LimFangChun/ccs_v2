package my.edu.tarc.communechat_v2.ADT;

import java.util.Iterator;

/**
 *
 * @author Lim Fang Chun
 * @param <T>
 */
public class LinkedList<T> implements ListInterface<T>, ListWithIteratorInterface<T> {

    private Node firstNode;
    private Node lastNode;
    private int countEntry = 0;

    public LinkedList() {
        firstNode = null;
        lastNode = null;
    }

    @Override
    public void add(T newEntry) {
        Node newNode = new Node(newEntry);

        if (isEmpty()) {
            firstNode = newNode;

            lastNode = newNode;
        } else {
            newNode.previous = lastNode;
            lastNode.next = newNode;
            lastNode = lastNode.next;
        }
        ++countEntry;
    }

    @Override
    public boolean add(T newEntry, int newPosition) {
        if (newPosition >= 1 && newPosition <= countEntry + 1) {
            Node newNode = new Node(newEntry);

            if (newPosition == 1) {
                //add new entry to first place
                add(newEntry);
            } else if (newPosition == countEntry + 1) {
                //add new entry to last place
                //total entry + 1 = last entry
                newNode.previous = lastNode;
                lastNode.next = newNode;
                lastNode = newNode;
            } else {
                Node nodeBefore;
                //if newPosition > total entry / 2
                //start the traverse from beginning
                //otherwise traverse from behind of list

                if (newPosition <= countEntry / 2) {
                    nodeBefore = firstNode;
                    for (int i = 1; i < newPosition - 1; ++i) {
                        nodeBefore = nodeBefore.next;
                    }
                } else {
                    nodeBefore = lastNode;
                    for (int i = 1; i <= countEntry - newPosition + 1; ++i) {
                        nodeBefore = nodeBefore.previous;
                    }
                }

                //update link
                Node nodeAfter = nodeBefore.next;

                newNode.previous = nodeBefore;
                newNode.next = nodeAfter;
                nodeBefore.next = newNode;
                nodeAfter.previous = newNode;
            }
            ++countEntry;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T remove(int givenPosition) {
        T result = null;

        if (givenPosition >= 1 && givenPosition <= countEntry) {
            if (givenPosition == 1) {
                result = firstNode.data;
                firstNode = firstNode.next;
                firstNode.previous = null;
            } else if (givenPosition == countEntry) {
                result = lastNode.data;
                lastNode = lastNode.previous;
                lastNode.next = null;
            } else {
                Node nodeBefore = firstNode;
                for (int i = 1; i < givenPosition - 1; ++i) {
                    nodeBefore = nodeBefore.next;
                }

                Node nodeAfter = nodeBefore.next;

                //get data into result
                result = nodeAfter.data;

                //update nodeAfter, traverse one more time;
                nodeAfter = nodeAfter.next;

                //update linking of nodes
                nodeAfter.previous = nodeBefore;
                nodeBefore.next = nodeAfter;
            }

            --countEntry;
        }
        return result;

    }

    @Override
    public final void clear() {
        firstNode = null;
        lastNode = null;
        countEntry = 0;
    }

    @Override
    public boolean replace(int givenPosition, T newEntry) {
        if (givenPosition >= 1 && givenPosition <= countEntry) {
            if (givenPosition == 1) {
                firstNode.data = newEntry;
            } else if (givenPosition == countEntry) {
                lastNode.data = newEntry;
            } else {
                Node currentNode;
                //if given position is less than half of total entries
                //start the loop from beginnning
                //else start the loop from the end
                //eg. total entries = 100
                //    given position = 90
                //if the loop starts from the end
                //the loop will iterate for 10 times only
                if (givenPosition <= countEntry / 2) {
                    currentNode = firstNode;
                    for (int i = 1; i < givenPosition; ++i) {
                        currentNode = currentNode.next;
                    }
                } else {
                    currentNode = lastNode;
                    for (int i = 1; i <= countEntry - givenPosition; ++i) {
                        currentNode = currentNode.previous;
                    }
                }
                currentNode.data = newEntry;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public T getEntry(int givenPosition) {
        if (givenPosition >= 1 && givenPosition <= countEntry) {
            if (givenPosition == 1) {
                return firstNode.data;
            } else if (givenPosition == countEntry) {
                return lastNode.data;
            } else {
                Node currentNode;
                //if given position is less than half of total entries
                //start the loop from beginnning
                //else start the loop from the end
                //eg. total entries = 100
                //    given position = 90
                //if the loop starts from the end
                //the loop will iterate for 10 times only
                if (givenPosition <= countEntry / 2) {
                    currentNode = firstNode;
                    for (int i = 1; i < givenPosition; ++i) {
                        currentNode = currentNode.next;
                    }
                } else {
                    currentNode = lastNode;
                    for (int i = 1; i < countEntry - givenPosition + 1; ++i) {
                        currentNode = currentNode.previous;
                    }
                }

                return currentNode.data;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean contains(T entry) {
        Node currentNode = firstNode;

        while (currentNode != null) {
            if (currentNode.next.equals(entry)) {
                return true;
            }
            currentNode = currentNode.next;
        }

        return false;
    }

    @Override
    public int getNumberOfEntries() {
        return countEntry;
    }

    @Override
    public boolean isEmpty() {
        return countEntry == 0 || firstNode == null;
    }

    @Override
    public String toString() {
        String msg = "";
        Node currentNode = firstNode;
        while (currentNode != null) {
            if (currentNode.data != null) {
                msg += (currentNode.data).toString();
            }
            //msg += "\n";
            currentNode = currentNode.next;
        }
        return msg;
    }

    @Override
    public Iterator<T> getIterator() {
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {

        private Node currentNode;

        public LinkedListIterator() {
            currentNode = firstNode;
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public T next() {
            if (hasNext()) {
                T result = currentNode.data;
                currentNode = currentNode.next;
                return result;
            } else {
                return null;
            }
        }

    }

    private class Node {

        T data;
        Node next;
        Node previous;

        public Node(T data) {
            this.data = data;
            this.next = null;
            this.previous = null;
        }

        public Node(T data, Node next) {
            this.data = data;
            this.next = next;
            this.previous = null;
        }

        public Node(T data, Node next, Node previous) {
            this.data = data;
            this.next = next;
            this.previous = previous;
        }
    }
}
