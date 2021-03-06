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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;
import java.util.stream.IntStream;

import org.nuxeo.functionaltests.Required;
import org.nuxeo.functionaltests.explorer.pages.AbstractExplorerPage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

/**
 * Page representing a selected artifact.
 *
 * @since 11.1
 */
public abstract class ArtifactPage extends AbstractExplorerPage {

    @Required
    @FindBy(xpath = "//section/article[@role='contentinfo']/h1")
    public WebElement header;

    @FindBy(xpath = "//section/article[@role='contentinfo']/div[contains(@class, 'include-in')]")
    public WebElement description;

    public ArtifactPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public void check() {
        checkReference();
    }

    public abstract void checkReference();

    public abstract void checkAlternative();

    public void checkCommon(String title, String headerText, String description) {
        checkTitle(title);
        checkHeaderText(headerText);
        checkDescription(description);
    }

    protected abstract void checkSelectedTab();

    public void checkHeaderText(String expected) {
        assertEquals(expected, header.getText());
    }

    public void checkDescription(String expected) {
        try {
            assertEquals(expected, description.getText());
        } catch (NoSuchElementException e) {
            // description is not mandatory on all pages
            assertNull(expected);
        }
    }

    public void checkRequirements(List<String> ids) {
        WebElement requirements = null;
        try {
            requirements = driver.findElement(By.id("requirements"));
        } catch (NoSuchElementException e) {
            assertNull(ids);
            return;
        }
        assertNotNull(ids);
        List<WebElement> bundles = requirements.findElements(By.xpath(".//li"));
        assertEquals(ids.size(), bundles.size());
        IntStream.range(0, bundles.size()).forEach(i -> assertEquals(ids.get(i), bundles.get(i).getText()));
    }

}
