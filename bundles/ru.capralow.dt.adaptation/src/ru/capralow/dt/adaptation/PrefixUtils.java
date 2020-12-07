/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.adaptation;

public final class PrefixUtils
{

    public static String getPrefixFromName(String name)
    {
        String[] nameArray = name.split("[.]"); //$NON-NLS-1$
        String[] subNameArray = nameArray[nameArray.length - 1].split("[_]"); //$NON-NLS-1$

        if (subNameArray.length == 1)
            return ""; //$NON-NLS-1$

        return subNameArray[0].concat("_"); //$NON-NLS-1$
    }

    private PrefixUtils()
    {
        throw new IllegalStateException(Messages.Internal_class);
    }
}
