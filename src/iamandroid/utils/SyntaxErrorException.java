/**
 * Copyright (c) 2005-2012 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Eclipse Public License (EPL).
 * Please see the license.txt included with this distribution for details.
 * Any modifications to this file must keep this entire header intact.
 */
package iamandroid.utils;

public class SyntaxErrorException extends Exception {

    private static final long serialVersionUID = -2833305218650293506L;

    public SyntaxErrorException() {
        super("Syntax error in buffer.");
    }

    /**
     * @param string
     */
    public SyntaxErrorException(String msg) {
        super(msg);
    }
}
