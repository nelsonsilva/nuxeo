/*
 * (C) Copyright 2018 Nuxeo SA (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-2.1.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 */

package org.nuxeo.ecm.core.io.marshallers.json.document;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.nuxeo.ecm.core.io.marshallers.json.document.DocumentModelListJsonWriter.ENTITY_DOCUMENT_LIST;
import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.SortInfo;
import org.nuxeo.ecm.core.io.marshallers.json.EntityJsonReader;
import org.nuxeo.ecm.core.io.marshallers.json.InputStreamWithJsonNode;
import org.nuxeo.ecm.core.io.registry.Reader;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;
import org.nuxeo.ecm.platform.query.api.PageProvider;
import org.nuxeo.ecm.platform.query.api.PageProviderDefinition;
import org.nuxeo.ecm.platform.query.api.PageProviderService;

import com.fasterxml.jackson.databind.JsonNode;
import org.nuxeo.ecm.platform.query.api.QuickFilter;

import javax.inject.Inject;

/**
 * @since 10.3
 */
@Setup(mode = SINGLETON, priority = REFERENCE)
public class PageProviderJsonReader extends EntityJsonReader<PageProvider> {

    @Inject
    PageProviderService pageProviderService;

    public PageProviderJsonReader() {
        super("pageProvider");
    }

    @Override
    protected PageProvider readEntity(JsonNode jn) throws IOException {

        String pageProviderName = getStringField(jn, "pageProviderName");
        PageProviderDefinition pageProviderDefinition = pageProviderService.getPageProviderDefinition(pageProviderName);

        String queryParams = getStringField(jn, "queryParams");
        String query = getStringField(jn, "query");
        String queryLanguage = getStringField(jn, "queryLanguage");

        Long pageSize = getLongField(jn, "pageSize");
        Long pageIndex = getLongField(jn, "currentPageIndex");
        Long maxResults = getLongField(jn, "maxResults");
        List<String> sortBy = getStringListField(jn, "sortBy");
        List<String> sortOrder = getStringListField(jn, "sortOrder");

        // read search document model
        DocumentModel searchDocument = null;
        if (jn.has("searchDocument")) {
            Reader<DocumentModel> docReader = registry.getReader(ctx, DocumentModel.class, DocumentModel.class, APPLICATION_JSON_TYPE);
            InputStreamWithJsonNode in = new InputStreamWithJsonNode(jn.get("searchDocument"));
            searchDocument = docReader.read(DocumentModel.class, DocumentModel.class, APPLICATION_JSON_TYPE, in);
        }

        // Quick filters management
        List<String> quickFilters = getStringListField(jn, "quickFilters");

        List<QuickFilter> quickFilterList = new ArrayList<>();
        if (quickFilters != null && !quickFilters.isEmpty()) {
            List<QuickFilter> ppQuickFilters = pageProviderDefinition.getQuickFilters();
            for (String filter : quickFilters) {
                for (QuickFilter quickFilter : ppQuickFilters) {
                    if (quickFilter.getName().equals(filter)) {
                        quickFilterList.add(quickFilter);
                        break;
                    }
                }
            }
        }

        JsonNode queryParamsNode = jn.has("params") ? jn.get("params") : null;

        Map<String, String> params = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        if (queryParamsNode != null) {
            Iterator<Map.Entry<String, JsonNode>> fields = queryParamsNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> fieldEntry = fields.next();
                params.put(
                        fieldEntry.getKey(),
                        fieldEntry.getValue().isTextual() ? fieldEntry.getValue().textValue()
                                : mapper.writeValueAsString(fieldEntry.getValue()));
            }
        }

        // Sort Info Management
        List<SortInfo> sortInfos = new ArrayList<>();
        if (!sortBy.isEmpty()) {
            for (int i = 0; i < sortBy.size(); i++) {
                String sort = sortBy.get(i);
                boolean sortAscending = (!sortOrder.isEmpty() && "asc".equals(sortOrder.get(i).toLowerCase()));
                sortInfos.add(new SortInfo(sort, sortAscending));
            }
        }

        Map<String, Serializable> props = new HashMap<>();

        if (query != null) {
            pageProviderDefinition.setPattern(query);
        }

        if (maxResults != null && maxResults != -1) {
            // set the maxResults to avoid slowing down queries
            // pageProviderDefinition.getProperties().put("maxResults", maxResults);
        }

        return pageProviderService.getPageProvider(pageProviderName, searchDocument,
                sortInfos, pageSize, pageIndex, props, quickFilterList, params);
    }
}
