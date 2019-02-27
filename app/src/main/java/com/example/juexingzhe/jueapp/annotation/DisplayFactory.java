package com.example.juexingzhe.jueapp.annotation;


import com.example.juexingzhe.moduleb.Display;

import java.util.Iterator;
import java.util.ServiceLoader;

public class DisplayFactory {
    private static DisplayFactory mDisplayFactory;

    private Iterator<Display> mIterator;

    private DisplayFactory() {
        ServiceLoader<Display> loader = ServiceLoader.load(Display.class);
        mIterator = loader.iterator();
    }

    public static DisplayFactory getSingleton() {
        if (null == mDisplayFactory) {
            synchronized (DisplayFactory.class) {
                if (null == mDisplayFactory) {
                    mDisplayFactory = new DisplayFactory();
                }
            }
        }
        return mDisplayFactory;
    }

    public Display getDisplay() {
        return mIterator.next();
    }

    public boolean hasNextDisplay() {
        return mIterator.hasNext();
    }
}
