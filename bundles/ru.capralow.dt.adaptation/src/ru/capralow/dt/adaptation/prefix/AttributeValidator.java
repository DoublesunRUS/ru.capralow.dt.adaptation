/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.adaptation.prefix;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.MODULE__CONTEXT_DEF;

import java.text.MessageFormat;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.util.CancelIndicator;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.bsl.validation.CustomValidationMessageAcceptor;
import com._1c.g5.v8.dt.bsl.validation.IExternalBslValidator;
import com._1c.g5.v8.dt.md.distribution.support.IDistributionSupportTypeProvider;
import com._1c.g5.v8.dt.md.distribution.support.UserSupportMode;
import com._1c.g5.v8.dt.metadata.mdclass.BasicDbObject;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.CatalogAttribute;
import com._1c.g5.v8.dt.metadata.mdclass.Document;
import com._1c.g5.v8.dt.metadata.mdclass.DocumentAttribute;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com.google.inject.Inject;

import ru.capralow.dt.adaptation.PrefixUtils;

public class AttributeValidator
    implements IExternalBslValidator
{

    public static final String ERROR_ATTRIBUTE_HAS_NO_PREFIX = "Adaptation_AttributeHasNoPrefix"; //$NON-NLS-1$
    public static final String ERROR_ATTRIBUTE_HAS_UNNECESSARY_PREFIX = "Adaptation_AttributeHasUnnecessaryPrefix"; //$NON-NLS-1$

    @Inject
    IDistributionSupportTypeProvider distributionSupportTypeProvider;

    private void checkAttribute(String attributeName, boolean objectHasSupport, MdObject attribute, EObject object,
        CustomValidationMessageAcceptor messageAcceptor)
    {
        UserSupportMode attributeSupportMode = distributionSupportTypeProvider.getUserSupportMode(attribute);
        boolean attributeHasSupport = objectHasSupport && attributeSupportMode != null;

        boolean attributeHasPrefix = !PrefixUtils.getPrefixFromName(attributeName).isEmpty();

        if (attributeHasSupport && attributeHasPrefix)
            messageAcceptor.warning(MessageFormat.format(Messages.Error_Attribute0HasUnnecessaryPrefix, attributeName),
                object, MODULE__CONTEXT_DEF, ERROR_ATTRIBUTE_HAS_UNNECESSARY_PREFIX);

        if (!attributeHasSupport && !attributeHasPrefix)
            messageAcceptor.warning(MessageFormat.format(Messages.Error_Attribute0HasNoPrefix, attributeName), object,
                MODULE__CONTEXT_DEF, ERROR_ATTRIBUTE_HAS_NO_PREFIX);
    }

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

        MdObject moduleMdObject = (MdObject)moduleObject;

        UserSupportMode objectSupportMode = distributionSupportTypeProvider.getUserSupportMode(moduleMdObject);
        boolean objectHasSupport = objectSupportMode != null;

        if (moduleMdObject instanceof Catalog)
        {
            if (!((Module)object).getModuleType().equals(ModuleType.OBJECT_MODULE))
                return;

            for (CatalogAttribute attribute : ((Catalog)moduleMdObject).getAttributes())
            {
                if (monitor.isCanceled())
                    return;

                checkAttribute(attribute.getName(), objectHasSupport, attribute, object, messageAcceptor);
            }

        }
        else if (moduleMdObject instanceof Document)
        {
            if (!((Module)object).getModuleType().equals(ModuleType.OBJECT_MODULE))
                return;

            for (DocumentAttribute attribute : ((Document)moduleMdObject).getAttributes())
            {
                if (monitor.isCanceled())
                    return;

                checkAttribute(attribute.getName(), objectHasSupport, attribute, object, messageAcceptor);
            }

        }
        else
        {
            // MdObject не имеет реквизитов для проверки

        }

    }
}
