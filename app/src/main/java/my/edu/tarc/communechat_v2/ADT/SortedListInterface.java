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
public interface SortedListInterface<T extends Comparable<? super T>> {
    boolean add(T newEntry);
    boolean remove(T entry);
    int getPosition(T entry);
    T getEntry(int givenPosition);
    boolean contains(T entry);
    T remove(int givenPosition);
    void clear();
    int getLength();
    boolean isEmpty();
    boolean isFull();
    T getLastEntry();
}
