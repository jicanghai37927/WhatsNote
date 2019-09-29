package club.andnext.helper;

import java.util.ArrayList;

public abstract class ObservableHelper<T> {
    
    /**
     * The list of observers.  An observer can be in the list at most
     * once and will never be null.
     */
    protected final ArrayList<T> mObservers = new ArrayList<T>();

    /**
     * Adds an observer to the list. The observer cannot be null and it must not already
     * be registered.
     * @param observer the observer to register
     * @throws IllegalArgumentException the observer is null
     * @throws IllegalStateException the observer is already registered
     */
    public void registerObserver(T observer) {
        if (observer == null) {
            return;
        }

        synchronized(mObservers) {
            if (mObservers.contains(observer)) {
                return;
            }
            mObservers.add(observer);
        }
    }

    /**
     * Removes a previously registered observer. The observer must not be null and it
     * must already have been registered.
     * @param observer the observer to unregister
     * @throws IllegalArgumentException the observer is null
     * @throws IllegalStateException the observer is not yet registered
     */
    public void unregisterObserver(T observer) {
        if (observer == null) {
            return;
        }
        synchronized(mObservers) {
            int index = mObservers.indexOf(observer);
            if (index == -1) {
                return;
            }
            mObservers.remove(index);
        }
    }

    /**
     * Remove all registered observers.
     */
    public void unregisterAll() {
        synchronized(mObservers) {
            mObservers.clear();
        }
    }
}
