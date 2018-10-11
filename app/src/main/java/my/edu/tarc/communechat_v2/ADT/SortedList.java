/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.edu.tarc.communechat_v2.ADT;

import java.io.Serializable;
import java.util.Iterator;

/**
 *
 * @author Lim Fang Chun
 * @param <T>
 */
public class SortedList<T extends Comparable<? super T>> implements SortedListInterface<T>,
        SortedListWithIteratorInterface<T>, Serializable {

    private Node firstNode;
    private Node lastNode;
    private int countEntry = 0;

    public SortedList() {
        firstNode = null;
        lastNode = null;
    }

    @Override
    public boolean add(T newEntry) {
        if (isEmpty()) {
            Node newNode = new Node(newEntry);
            firstNode = newNode;
            lastNode = newNode;
        } else {
            Node currentNode = firstNode;
            while (currentNode != null) {
                if (newEntry.compareTo(currentNode.data) <= 0) {
                    break;
                }
                currentNode = currentNode.next;
            }

            if (currentNode == firstNode) {
                Node newNode = new Node(newEntry, firstNode);
                firstNode.previous = newNode;
                firstNode = newNode;
            } else if (currentNode == null) {
                Node newNode = new Node(newEntry, null, lastNode);
                lastNode.next = newNode;
                lastNode = newNode;
            } else {
                Node newNode = new Node(newEntry, currentNode, currentNode.previous);
                currentNode.previous.next = newNode;
                currentNode.previous = newNode;
            }
        }

        countEntry++;
        return true;
    }

    @Override
    public boolean remove(T entry) {
        Node currentNode = firstNode;
        while (currentNode != null) {
            if (entry.compareTo(currentNode.data) == 0) {
                break;
            }
            currentNode = currentNode.next;
        }

        if (currentNode == null) {
            //exception case 1:
            //if currentNode is null
            //the entry does not exist
            return false;
        } else if (currentNode == firstNode) {
            //exception case 2:
            //if currentNode is at first
            firstNode = firstNode.next;
            firstNode.previous = null;
        } else if (currentNode == lastNode) {
            //exception case 3:
            //if currentNode is at last
            lastNode = lastNode.previous;
            lastNode.next = null;
        } else {
            //final case
            //currentNode is at mid of list
            currentNode.previous.next = currentNode.next;
            currentNode.next.previous = currentNode.previous;
        }
        countEntry--;
        return true;
    }

    @Override
    public int getPosition(T entry) {
        Node currentNode = firstNode;
        int countPosition = 1;
        while (currentNode != null) {
            if (entry.compareTo(currentNode.data) == 0) {
                break;
            }
            ++countPosition;
            currentNode = currentNode.next;
        }

        if (currentNode == null) {
            return -1;
        }

        return countPosition;
    }

    @Override
    public T getEntry(int givenPosition) {
        T result = null;
        if (givenPosition >= 1 && givenPosition <= countEntry) {
            if (givenPosition < countEntry / 2) {
                //case 1:
                //if givenPosition is less than half of countEntry
                //start traverse from beginning
                Node currentNode = firstNode;
                for (int i = 1; i < givenPosition; ++i) {
                    currentNode = currentNode.next;
                }
                result = currentNode.data;
            } else {
                //case 2:
                //if givenPosition is more than half of countEntry
                //start traverse from end
                Node currentNode = lastNode;
                for (int i = 1; i < countEntry - givenPosition + 1; ++i) {
                    currentNode = currentNode.previous;
                }
                result = currentNode.data;
            }
        }
        return result;
    }

    @Override
    public boolean contains(T entry) {
        Node currentNode = firstNode;
        while (currentNode != null) {
            if (entry.compareTo(currentNode.data) == 0) {
                return true;
            }
            currentNode = currentNode.next;
        }
        return false;
    }

    @Override
    public T remove(int givenPosition) {
        Node currentNode;
        T result = null;
        if (getLength() == 1) {
            //case 1:
            //the list has only 1 entry
            result = firstNode.data;
            firstNode = firstNode.next;
        } else if (givenPosition == 1) {
            //case 2:
            //givenPosition is at first
            result = firstNode.data;
            firstNode = firstNode.next;
            firstNode.previous = null;
        } else if (givenPosition == countEntry) {
            //case 3:
            //givenPosition is at last
            result = lastNode.data;
            lastNode = lastNode.previous;
            lastNode.next = null;
        } else {
            //case 4:
            //givenPosition is at mid of list
            if (givenPosition < countEntry / 2) {
                //case 4.1:
                //givenPosition less than half of total entries
                //start traverse from beginning
                currentNode = firstNode;
                for (int i = 1; i < givenPosition; ++i) {
                    currentNode = currentNode.next;
                }
            } else {
                //case 4.2:
                //givenPosition more than half of total entries
                //start traverse from end
                currentNode = lastNode;
                for (int i = 1; i < countEntry - givenPosition + 1; ++i) {
                    currentNode = currentNode.previous;
                }
            }
            result = currentNode.data;
            currentNode.previous.next = currentNode.next;
            currentNode.next.previous = currentNode.previous;
        }
        if (result != null) {
            --countEntry;
        }

        return result;
    }

    @Override
    public void clear() {
        firstNode = null;
        lastNode = null;
        countEntry = 0;
    }

    @Override
    public int getLength() {
        return countEntry;
    }

    @Override
    public boolean isEmpty() {
        return countEntry == 0;
    }

    @Override
    public boolean isFull() {
        return false;
    }

    @Override
    public String toString() {
        String msg = "";
        Node currentNode = firstNode;
        while (currentNode != null) {
            if (currentNode.data != null) {
                msg += currentNode.data;
            }
            currentNode = currentNode.next;
        }
        return msg;
    }

    @Override
    public Iterator getIterator() {
        return new SortedLinkedListIterator();
    }

    @Override
    public T getLastEntry() {
        return lastNode.data;
    }

    private class SortedLinkedListIterator implements Iterator<T> {

        private Node currentNode;

        public SortedLinkedListIterator() {
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
        }

        public Node(T data, Node next) {
            this.data = data;
            this.next = next;
        }

        public Node(T data, Node next, Node previous) {
            this.data = data;
            this.next = next;
            this.previous = previous;
        }

    }
}
