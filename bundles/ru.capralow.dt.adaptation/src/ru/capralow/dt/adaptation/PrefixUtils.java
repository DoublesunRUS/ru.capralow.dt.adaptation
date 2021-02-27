/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.adaptation;

public final class PrefixUtils
{

    public static boolean nameHasPrefix(String name, String[] prefixes)
    {
        for (String prefix : prefixes)
            if (name.startsWith(prefix))
                return true;

        return false;
    }

    private PrefixUtils()
    {
        throw new IllegalStateException(Messages.Internal_class);
    }
}
