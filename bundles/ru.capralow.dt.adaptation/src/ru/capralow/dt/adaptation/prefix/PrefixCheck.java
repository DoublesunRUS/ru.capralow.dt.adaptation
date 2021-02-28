/**
 * Copyright (c) 2021, Alexander Kapralov
 */
package ru.capralow.dt.adaptation.prefix;

import static com._1c.g5.v8.dt.bsl.model.BslPackage.Literals.METHOD;
import static com._1c.g5.v8.dt.mcore.McorePackage.Literals.NAMED_ELEMENT__NAME;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT;
import static com._1c.g5.v8.dt.metadata.mdclass.MdClassPackage.Literals.MD_OBJECT__NAME;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.resource.IResourceServiceProvider;

import com._1c.g5.v8.bm.core.IBmObject;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslCommentUtils;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslDocumentationComment.Description;
import com._1c.g5.v8.dt.bsl.documentation.comment.BslMultiLineCommentDocumentationProvider;
import com._1c.g5.v8.dt.bsl.documentation.comment.IDescriptionPart;
import com._1c.g5.v8.dt.bsl.documentation.comment.TextPart;
import com._1c.g5.v8.dt.bsl.model.Method;
import com._1c.g5.v8.dt.bsl.model.Module;
import com._1c.g5.v8.dt.bsl.model.ModuleType;
import com._1c.g5.v8.dt.check.ICheckParameters;
import com._1c.g5.v8.dt.check.components.BasicCheck;
import com._1c.g5.v8.dt.check.settings.IssueType;
import com._1c.g5.v8.dt.common.StringUtils;
import com._1c.g5.v8.dt.md.distribution.support.IDistributionSupportTypeProvider;
import com._1c.g5.v8.dt.md.distribution.support.UserSupportMode;
import com._1c.g5.v8.dt.metadata.mdclass.Catalog;
import com._1c.g5.v8.dt.metadata.mdclass.CatalogAttribute;
import com._1c.g5.v8.dt.metadata.mdclass.Document;
import com._1c.g5.v8.dt.metadata.mdclass.DocumentAttribute;
import com._1c.g5.v8.dt.metadata.mdclass.MdObject;
import com._1c.g5.v8.dt.metadata.mdclass.Subsystem;
import com.google.inject.Inject;

import ru.capralow.dt.adaptation.MdUtils;
import ru.capralow.dt.adaptation.PrefixUtils;

public class PrefixCheck
    extends BasicCheck
{

    public static final String CHECK_ID = "PrefixCheck"; //$NON-NLS-1$

    private static final String PREFIX_NAME_PATTERN_PARAMETER_NAME = "prefixNamePattern"; //$NON-NLS-1$

    private static BslMultiLineCommentDocumentationProvider commentProvider =
        IResourceServiceProvider.Registry.INSTANCE.getResourceServiceProvider(URI.createURI("foo.bsl")).get( //$NON-NLS-1$
            BslMultiLineCommentDocumentationProvider.class);

    @Inject
    IDistributionSupportTypeProvider distributionSupportTypeProvider;

    @Override
    public String getCheckId()
    {
        return CHECK_ID;
    }

    private void checkAttribute(String attributeName, boolean objectHasSupport, MdObject attribute, EObject object,
        ResultAcceptor resultAcceptor, ICheckParameters parameters)
    {
        String[] prefixNamePattern = parameters.getString(PREFIX_NAME_PATTERN_PARAMETER_NAME).split("[ ]"); //$NON-NLS-1$

        UserSupportMode attributeSupportMode = distributionSupportTypeProvider.getUserSupportMode(attribute);
        boolean attributeHasSupport = attributeSupportMode != null;

        boolean attributeHasPrefix = !PrefixUtils.nameHasPrefix(attributeName, prefixNamePattern);

        boolean attributeNeedPrefix = true;
        if (!objectHasSupport || attributeHasSupport)
            attributeNeedPrefix = false;

        if (!attributeNeedPrefix && attributeHasPrefix)
            resultAcceptor.addIssue(MessageFormat.format(Messages.Error_Attribute0HasUnnecessaryPrefix, attributeName),
                MD_OBJECT__NAME);

        if (attributeNeedPrefix && !attributeHasPrefix)
            resultAcceptor.addIssue(MessageFormat.format(Messages.Error_Attribute0HasNoPrefix, attributeName),
                MD_OBJECT__NAME);
    }

    private void checkContainmentMdObject(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        if (progressMonitor.isCanceled())
            return;

        IBmObject moduleObject = ((IBmObject)object).bmGetTopObject();
        MdObject moduleMdObject = (MdObject)moduleObject;

        UserSupportMode objectSupportMode = distributionSupportTypeProvider.getUserSupportMode(moduleMdObject);
        boolean objectHasSupport = objectSupportMode != null;

        if (moduleMdObject instanceof Catalog)
        {
            if (!((Module)object).getModuleType().equals(ModuleType.OBJECT_MODULE))
                return;

            for (CatalogAttribute attribute : ((Catalog)moduleMdObject).getAttributes())
            {
                checkAttribute(attribute.getName(), objectHasSupport, attribute, (MdObject)object, resultAcceptor,
                    parameters);
            }

        }
        else if (moduleMdObject instanceof Document)
        {
            if (!((Module)object).getModuleType().equals(ModuleType.OBJECT_MODULE))
                return;

            for (DocumentAttribute attribute : ((Document)moduleMdObject).getAttributes())
            {
                checkAttribute(attribute.getName(), objectHasSupport, attribute, (MdObject)object, resultAcceptor,
                    parameters);
            }

        }
        else
        {
            // MdObject не имеет реквизитов для проверки

        }

    }

    private void checkModule(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        if (progressMonitor.isCanceled())
            return;

        IBmObject moduleOwner = (IBmObject)((Module)object).getOwner();

        IBmObject moduleObject = moduleOwner.bmGetTopObject();

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

        String[] prefixNamePattern = new String[1];

        String methodName = ((Method)object).getName();
        boolean methodHasPrefix = !PrefixUtils.nameHasPrefix(methodName, prefixNamePattern);

        if (methodHasSupport && methodHasPrefix)
            resultAcceptor.addIssue(MessageFormat.format(Messages.Error_Method0HasUnnecessaryPrefix, methodName),
                NAMED_ELEMENT__NAME);

        if (!methodHasSupport && !methodHasPrefix)
            resultAcceptor.addIssue(MessageFormat.format(Messages.Error_Method0HasNoPrefix, methodName),
                NAMED_ELEMENT__NAME);
    }

    private void checkTopMdObject(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        if (progressMonitor.isCanceled())
            return;

        String[] prefixNamePattern = parameters.getString(PREFIX_NAME_PATTERN_PARAMETER_NAME).split("[ ]"); //$NON-NLS-1$

        boolean hasStandartSubsystem = false;
        EList<Subsystem> objectSubsystems = MdUtils.getSubsystemsForObject((MdObject)object);
        for (Subsystem subsystem : objectSubsystems)
            if (!subsystem.isIncludeInCommandInterface()
                && !PrefixUtils.nameHasPrefix(subsystem.getName(), prefixNamePattern))
            {
                hasStandartSubsystem = true;
                break;
            }

        String objectName = ((MdObject)object).getName();
        boolean objectHasPrefix = PrefixUtils.nameHasPrefix(objectName, prefixNamePattern);

        if (!hasStandartSubsystem && !objectHasPrefix)
            resultAcceptor.addIssue(Messages.Error_ObjectHasNoPrefix, MD_OBJECT__NAME);

        if (hasStandartSubsystem && objectHasPrefix)
            resultAcceptor.addIssue(Messages.Error_ObjectHasUnnecessaryPrefix, MD_OBJECT__NAME);
    }

    @Override
    protected void check(Object object, ResultAcceptor resultAcceptor, ICheckParameters parameters,
        IProgressMonitor progressMonitor)
    {
        if (object instanceof MdObject)
        {
            if (!((IBmObject)object).bmIsTop())
                checkTopMdObject(object, resultAcceptor, parameters, progressMonitor);
            else
                checkContainmentMdObject(object, resultAcceptor, parameters, progressMonitor);
        }
        else
        {
            checkModule(object, resultAcceptor, parameters, progressMonitor);
        }
    }

    @Override
    protected void configureCheck(CheckConfigurer configurer)
    {
        configurer.title(Messages.Error_MethodPrefix_Title).description(Messages.Error_MethodPrefix_Description);
        configurer.issueType(IssueType.WARNING);

        configurer.parameter(PREFIX_NAME_PATTERN_PARAMETER_NAME, String.class, StringUtils.EMPTY,
            Messages.Parameter_PrefixName);

        configurer.topObject(MD_OBJECT).features(MD_OBJECT__NAME);
        configurer.topObject(MD_OBJECT).containment(MD_OBJECT).features(MD_OBJECT__NAME);
        configurer.module().checkedObjectType(METHOD);
    }

}
