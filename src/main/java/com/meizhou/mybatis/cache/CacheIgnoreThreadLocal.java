package com.meizhou.mybatis.cache;

import java.util.Stack;

/**
 * Created by meizhou on 2018/8/18.
 */
public class CacheIgnoreThreadLocal {

    private static ThreadLocal<Stack<Boolean>> holder = new ThreadLocal<Stack<Boolean>>();

    public static Boolean get() {
        return holder.get() != null && holder.get().size() > 0;
    }
    
    public static void add() {
        if (holder.get() == null) {
            holder.set(new Stack<Boolean>());
        }
        holder.get().add(true);
    }

    public static void pop() {
        holder.get().pop();
    }

}
