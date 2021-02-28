/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.adaptation.prefix;

import org.eclipse.osgi.util.NLS;

public class Messages
    extends NLS
{

    private static final String BUNDLE_NAME = "ru.capralow.dt.adaptation.prefix.messages"; //$NON-NLS-1$

    public static String Error_ObjectHasNoPrefix;
    public static String Error_ObjectHasUnnecessaryPrefix;

    public static String Error_Attribute0HasNoPrefix;
    public static String Error_Attribute0HasUnnecessaryPrefix;

    public static String Error_Method0HasNoPrefix;
    public static String Error_Method0HasUnnecessaryPrefix;

    public static String Error_MethodPrefix_Title;
    public static String Error_MethodPrefix_Description;

    public static String Parameter_PrefixName;

    static
    {
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

}
