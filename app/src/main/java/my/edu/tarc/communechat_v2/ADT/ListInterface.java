/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.edu.tarc.communechat_v2.ADT;

/**
 *
 * @author Leo
 */
public interface ListInterface<T> {
    public void add(T newEntry);
    public boolean add(T newEntry, int newPosition);
    public T remove(int givenPosition);
    public void clear();
    public boolean replace(int givenPosition, T newEntry);
    public T getEntry(int givenPosition);
    public boolean contains(T entry);
    public int getNumberOfEntries();
    public boolean isEmpty();
}
