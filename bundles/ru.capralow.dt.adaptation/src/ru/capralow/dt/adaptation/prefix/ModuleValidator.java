/**
 * Copyright (c) 2020, Alexander Kapralov
 */
package ru.capralow.dt.adaptation.prefix;

import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;

import java.text.MessageFormat;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.EcoreUtil2;
import org.eclipse.xtext.resource.IResourceServiceProvider;
import org.eclipse.xtext.util.CancelIndicator;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.validation.CustomValidationMessageAcceptor;
import com._1c.g5.v8.dt.bsl.validation.IExternalBslValidator;
import com._1c.g5.v8.dt.md.distribution.support.IDistributionSupportTypeProvider;
import com._1c.g5.v8.dt.md.distribution.support.UserSupportMode;
import com._1c.g5.v8.dt.metadata.mdclass.BasicDbObject;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com.google.inject.Inject;

import ru.capralow.dt.adaptation.PrefixUtils;

public class ModuleValidator
    implements IExternalBslValidator
{

    private static BslMultiLineCommentDocumentationProvider commentProvider =
        IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("foo.bsl")).get( //$NON-NLS-1$
            BslMultiLineCommentDocumentationProvider.class);

    public static final String ERROR_METHOD_HAS_NO_PREFIX = "Adaptation_MethodHasNoPrefix"; //$NON-NLS-1$
    public static final String ERROR_METHOD_HAS_UNNECESSARY_PREFIX = "Adaptation_MethodHasUnnecessaryPrefix"; //$NON-NLS-1$

    @Inject
    IDistributionSupportTypeProvider distributionSupportTypeProvider;

    @Override
    public boolean needValidation(EObject object)
    {
        return object instanceof Method;
    }

    @Override
    public void validate(EObject object, CustomValidationMessageAcceptor messageAcceptor, CancelIndicator monitor)
    {
        if (monitor.isCanceled())
            return;

        Module module = EcoreUtil2.getContainerOfType(object, Module.class);

        IBmObject moduleOwner = (IBmObject)module.getOwner();

        IBmObject moduleObject = moduleOwner.bmGetTopObject();

        if (!(moduleObject instanceof BasicDbObject))
            return;

        MdObject moduleMdObject = (MdObject)moduleObject;

        UserSupportMode objectSupportMode = distributionSupportTypeProvider.getUserSupportMode(moduleMdObject);
        boolean objectHasSupport = objectSupportMode != null;
        boolean methodHasSupport = objectHasSupport;

        BslDocumentationComment comment = BslCommentUtils.parseTemplateComment((Method)object, true, commentProvider);
        Description description = comment.getDescription();
        for (IDescriptionPart part : description.getParts())
        {
            if (part instanceof TextPart)
            {
                String partText = ((TextPart)part).getText();
                if (partText.toLowerCase().indexOf("рарус") != -1) //$NON-NLS-1$
                {
                    methodHasSupport = false;
                    break;
                }
            }
        }

        String methodName = ((Method)object).getName();
        boolean methodHasPrefix = !PrefixUtils.getPrefixFromName(methodName).isEmpty();

        if (methodHasSupport && methodHasPrefix)
            messageAcceptor.warning(MessageFormat.format(Messages.Error_Method0HasUnnecessaryPrefix, methodName),
                object, NAMED_ELEMENT__NAME, ERROR_METHOD_HAS_UNNECESSARY_PREFIX);

        if (!methodHasSupport && !methodHasPrefix)
            messageAcceptor.warning(MessageFormat.format(Messages.Error_Method0HasNoPrefix, methodName), object,
                NAMED_ELEMENT__NAME, ERROR_METHOD_HAS_NO_PREFIX);
    }

}
