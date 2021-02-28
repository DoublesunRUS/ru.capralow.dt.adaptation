/**
 * Copyright (c) 2021, Alexander Kapralov
 */
package ru.capralow.dt.adaptation.prefix.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com._1c.g5.v8.dt.core.platform.IDtProject;
import com._1c.g5.v8.dt.metadata.mdclass.CommonModule;
import com._1c.g5.v8.dt.testing.check.CheckTestBase;
import com._1c.g5.v8.dt.validation.marker.Marker;

import ru.capralow.dt.adaptation.prefix.PrefixCheck;

public class ObjectHasNoPrefixCheckTest
    extends CheckTestBase
{

    @Test
    public void testModule() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish("TestConfiguration"); //$NON-NLS-1$

        String moduleId = "platform:/resource/TestConfiguration/src/CommonModules/ОбщийМодуль/Module.bsl"; //$NON-NLS-1$

        Marker targetMarker = getFirstMarker(PrefixCheck.CHECK_ID, moduleId, dtProject);
        assertNotNull(targetMarker);
        assertEquals(PrefixCheck.CHECK_ID, targetMarker.getMessage());

    }

    @Test
    public void testObject() throws Exception
    {
        IDtProject dtProject = openProjectAndWaitForValidationFinish("TestConfiguration"); //$NON-NLS-1$

        CommonModule testObject = (CommonModule)getTopObjectByFqn("CommonModule.ОбщийМодуль", dtProject); //$NON-NLS-1$
        assertNotNull(testObject);

        Marker targetMarker = getFirstMarker(PrefixCheck.CHECK_ID, testObject, dtProject);
        assertNotNull(targetMarker);

        assertEquals(PrefixCheck.CHECK_ID, targetMarker.getMessage());

    }

}
