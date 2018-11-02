/*
 * (C) Copyright 2015 Nuxeo SA (http://nuxeo.com/) and others.
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
 *     Nicolas Chapurlat <nchapurlat@nuxeo.com>
 */

package org.nuxeo.ecm.core.io.marshallers.json.document;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.nuxeo.ecm.core.io.marshallers.json.document.DocumentModelListJsonWriter.ENTITY_DOCUMENT_LIST;
import static org.nuxeo.ecm.core.io.registry.reflect.Instantiations.SINGLETON;
import static org.nuxeo.ecm.core.io.registry.reflect.Priorities.REFERENCE;

import com.fasterxml.jackson.databind.JsonNode;
import org.nuxeo.ecm.automation.jaxrs.io.documents.PaginableDocumentModelListImpl;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.io.marshallers.json.DefaultListJsonReader;
import org.nuxeo.ecm.core.io.marshallers.json.EntityJsonReader;
import org.nuxeo.ecm.core.io.marshallers.json.InputStreamWithJsonNode;
import org.nuxeo.ecm.core.io.registry.Reader;
import org.nuxeo.ecm.core.io.registry.reflect.Setup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import java.io.IOException;
import java.lang.reflect.Type;

import org.apache.commons.lang3.reflect.TypeUtils;
import org.nuxeo.ecm.core.io.registry.MarshallerRegistry;
import org.nuxeo.ecm.core.io.registry.Reader;
import org.nuxeo.ecm.platform.query.api.PageProvider;

/**
 * see {@link DefaultListJsonReader}
 *
 * @since 7.2
 */
@Setup(mode = SINGLETON, priority = REFERENCE)
public class DocumentModelListJsonReader extends EntityJsonReader<List<DocumentModel>> {

    public DocumentModelListJsonReader() {
        super(ENTITY_DOCUMENT_LIST);
    }

    @Override
    protected List<DocumentModel> readEntity(JsonNode jn) throws IOException {

        Reader<DocumentModel> entryReader = registry.getReader(ctx, DocumentModel.class, DocumentModel.class, APPLICATION_JSON_TYPE);
        List<DocumentModel> result = new ArrayList<>();

        JsonNode providerNode = jn.get("provider");
        if (providerNode != null && !providerNode.isNull()) {
            Reader<PageProvider> providerReader = registry.getReader(ctx, PageProvider.class, PageProvider.class, APPLICATION_JSON_TYPE);
            InputStreamWithJsonNode in = new InputStreamWithJsonNode(providerNode);
            PageProvider pageProvider = providerReader.read(PageProvider.class, PageProvider.class, APPLICATION_JSON_TYPE, in);
            return new PaginableDocumentModelListImpl(pageProvider, "restdocid");
        }

        JsonNode entriesNode = jn.get("entries");
        if (entriesNode != null && !entriesNode.isNull() && entriesNode.isArray()) {
            JsonNode entryNode = null;
            Iterator<JsonNode> it = entriesNode.elements();
            while (it.hasNext()) {
                entryNode = it.next();
                InputStreamWithJsonNode in = new InputStreamWithJsonNode(entryNode);
                DocumentModel doc = entryReader.read(DocumentModel.class, DocumentModel.class, APPLICATION_JSON_TYPE, in);
                result.add(doc);
            }
        }
        return result;
    }
}
