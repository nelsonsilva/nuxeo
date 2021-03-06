/*
 * (C) Copyright 2020 Nuxeo (http://nuxeo.com/) and others.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Contributors:
 *     Anahide Tchertchian
 */
package org.nuxeo.functionaltests.explorer.pages.artifacts;

import org.nuxeo.functionaltests.explorer.pages.DistributionHeaderFragment;
import org.openqa.selenium.WebDriver;

/**
 * @since 11.1
 */
public class OperationArtifactPage extends ArtifactPage {

    public OperationArtifactPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void checkReference() {
        checkCommon("Operation Document.AddFacet", "Operation Document.AddFacet (Add Facet)",
                "In component org.nuxeo.ecm.core.automation.coreContrib");
    }

    @Override
    public void checkAlternative() {
        checkCommon("Operation Document.Create", "Operation Document.Create (Create)",
                "In component org.nuxeo.ecm.core.automation.coreContrib");
    }

    @Override
    public void checkSelectedTab() {
        DistributionHeaderFragment header = asPage(DistributionHeaderFragment.class);
        header.checkSelectedTab(header.operations);
    }

}
