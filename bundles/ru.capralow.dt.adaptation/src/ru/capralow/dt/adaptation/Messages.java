/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.adaptation;

import org.eclipse.osgi.util.NLS;

public class Messages
    extends NLS
{

    private static final String BUNDLE_NAME = "ru.capralow.dt.adaptation.messages"; //$NON-NLS-1$

    public static String Internal_class;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
