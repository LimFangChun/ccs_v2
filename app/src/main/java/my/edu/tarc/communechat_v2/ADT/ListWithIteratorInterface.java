/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package my.edu.tarc.communechat_v2.ADT;

import java.util.Iterator;

/**
 *
 * @author Lim Fang Chun
 */
public interface ListWithIteratorInterface<T> extends ListInterface<T> {
    Iterator<T> getIterator();
}
