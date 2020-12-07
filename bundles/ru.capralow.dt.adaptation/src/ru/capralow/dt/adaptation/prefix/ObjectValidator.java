/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.adaptation.prefix;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE__CONTEXT_DEF;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.CancelIndicator;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.validation.CustomValidationMessageAcceptor;
import com._1c.g5.v8.dt.bsl.validation.IExternalBslValidator;
import com._1c.g5.v8.dt.md.resource.MdTypeUtil;
import com._1c.g5.v8.dt.metadata.mdclass.BasicDbObject;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.Subsystem;

import ru.capralow.dt.adaptation.MdUtils;
import ru.capralow.dt.adaptation.PrefixUtils;

public class ObjectValidator
    implements IExternalBslValidator
{

    public static final String ERROR_OBJECT_HAS_NO_PREFIX = "Adaptation_ObjectHasNoPrefix"; //$NON-NLS-1$
    public static final String ERROR_OBJECT_HAS_UNNECESSARY_PREFIX = "Adaptation_ObjectHasUnnecessaryPrefix"; //$NON-NLS-1$

    @Override
    public boolean needValidation(EObject object)
    {
        return object instanceof Module;
    }

    @Override
    public void validate(EObject object, CustomValidationMessageAcceptor messageAcceptor, CancelIndicator monitor)
    {
        if (monitor.isCanceled())
            return;

        IBmObject moduleOwner = (IBmObject)((Module)object).getOwner();

        IBmObject moduleObject = moduleOwner.bmGetTopObject();

        if (!(moduleObject instanceof BasicDbObject))
            return;

        boolean hasStandartSubsystem = false;
        EList<Subsystem> objectSubsystems = MdUtils.getSubsystemsForObject((MdObject)moduleObject);
        for (Subsystem subsystem : objectSubsystems)
        {
            if (subsystem.isIncludeInCommandInterface())
                continue;

            if (PrefixUtils.getPrefixFromName(subsystem.getName()).isEmpty())
            {
                hasStandartSubsystem = true;
                break;
            }
        }

        String objectName = MdTypeUtil.getRefType((BasicDbObject)moduleObject).getName();
        boolean objectHasPrefix = !PrefixUtils.getPrefixFromName(objectName).isEmpty();

        if (hasStandartSubsystem && objectHasPrefix)
            messageAcceptor.warning(Messages.Error_ObjectHasUnnecessaryPrefix, object, MODULE__CONTEXT_DEF,
                ERROR_OBJECT_HAS_UNNECESSARY_PREFIX);

        if (!hasStandartSubsystem && !objectHasPrefix)
            messageAcceptor.warning(Messages.Error_ObjectHasNoPrefix, object, MODULE__CONTEXT_DEF,
                ERROR_OBJECT_HAS_NO_PREFIX);
    }

}
