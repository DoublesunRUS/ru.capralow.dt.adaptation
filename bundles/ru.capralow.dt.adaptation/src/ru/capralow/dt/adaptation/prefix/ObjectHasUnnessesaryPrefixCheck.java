/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.adaptation.prefix;

import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.check.ICheckParameters;
import com._1c.g5.v8.dt.check.components.BasicCheck;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.Subsystem;

import ru.capralow.dt.adaptation.MdUtils;
import ru.capralow.dt.adaptation.PrefixUtils;

public class ObjectHasUnnessesaryPrefixCheck
    extends BasicCheck
{

    public static final String CHECK_TITLE = "Префиксы: У объекта ненужный префикс"; //$NON-NLS-1$

    private static final String PREFIX_NAME_PATTERN_PARAMETER_NAME = "prefixNamePattern"; //$NON-NLS-1$

    @Override
    public String getCheckId()
    {
        return CHECK_TITLE;
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        if (!((IBmObject)object).bmIsTop())
            return;

        String[] prefixNamePattern = parameters.getString(PREFIX_NAME_PATTERN_PARAMETER_NAME).split("[ ]"); //$NON-NLS-1$

        boolean hasStandartSubsystem = false;
        EList<Subsystem> objectSubsystems = MdUtils.getSubsystemsForObject((MdObject)object);
        for (Subsystem subsystem : objectSubsystems)
        {
            if (subsystem.isIncludeInCommandInterface())
                continue;

            if (!PrefixUtils.nameHasPrefix(subsystem.getName(), prefixNamePattern))
            {
                hasStandartSubsystem = true;
                break;
            }
        }

        String objectName = ((MdObject)object).getName();
        boolean objectHasPrefix = PrefixUtils.nameHasPrefix(objectName, prefixNamePattern);

        if (hasStandartSubsystem && objectHasPrefix)
            resultAcceptor.addIssue(Messages.Error_ObjectHasUnnecessaryPrefix, MD_OBJECT__NAME);
    }

    @Override
    protected void configureCheck(CheckConfigurer configurer)
    {
        configurer.topObject(MD_OBJECT).features(MD_OBJECT__NAME).parameter(PREFIX_NAME_PATTERN_PARAMETER_NAME,
            String.class, "", Messages.Parameter_PrefixName); //$NON-NLS-1$
    }

}
