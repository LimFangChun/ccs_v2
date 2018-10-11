/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.edu.tarc.communechat_v2.ADT;

/**
 *
 * @author Lim Fang Chun
 * @param <T>
 */
public class LinkedQueue<T> implements QueueInterface<T> {

    private Node firstNode;
    private Node lastNode;
    private int countEntry = 0;

    public LinkedQueue() {
        firstNode = null;
        lastNode = null;
    }

    @Override
    public void enqueue(T newEntry) {
        Node newNode = new Node(newEntry);

        if (isEmpty()) {
            firstNode = newNode;
        } else {
            lastNode.next = newNode;
        }

        lastNode = newNode;
        ++countEntry;
    }

    @Override
    public T dequeue() {
        T front = null;

        if (!isEmpty()) {
            front = firstNode.data;
            firstNode = firstNode.next;

            if (firstNode == null) {
                lastNode = null;
            }
        }
        --countEntry;
        return front;
    }

    @Override
    public T getFront() {
        if (isEmpty()) {
            return null;
        } else {
            return firstNode.data;
        }
    }

    @Override
    public boolean isEmpty() {
        return firstNode == null;
    }

    @Override
    public void clear() {
        firstNode = null;
        lastNode = null;
        countEntry = 0;
    }//
    
    @Override
    public int getNumberOfEntries(){
        return countEntry;
    }

    private class Node {

        T data;
        Node next;

        public Node(T data) {
            this.data = data;
        }

        public Node(T data, Node next) {
            this.data = data;
            this.next = next;
        }
    }
}
