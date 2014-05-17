/**
 * Copyright (c) 2005-2013 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package iamandroid.callbacks;

import iamandroid.utils.Log;
import iamandroid.utils.OrderedSet;


public class CallbackWithListeners<X> implements ICallbackWithListeners<X> {

    private final OrderedSet<ICallbackListener<X>> listeners;

    public CallbackWithListeners() {
        this.listeners = new OrderedSet<ICallbackListener<X>>();
    }

    public CallbackWithListeners(int initialCapacity) {
        this.listeners = new OrderedSet<ICallbackListener<X>>(initialCapacity);
    }

    public Object call(X obj) {
        Object result = null;
        for (ICallbackListener<X> listener : this.listeners) {
            try {
                Object callResult = listener.call(obj);
                if (callResult != null) {
                    result = callResult;
                }
            } catch (Throwable e) {
                //Should never fail!
                Log.log(e);
            }
        }
        return result;
    }

    public void registerListener(ICallbackListener<X> listener) {
        this.listeners.add(listener);
    }

    public void unregisterListener(ICallbackListener<X> listener) {
        this.listeners.remove(listener);
    }

}
