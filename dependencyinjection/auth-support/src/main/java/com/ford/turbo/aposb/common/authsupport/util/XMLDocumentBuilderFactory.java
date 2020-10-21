package com.ford.turbo.aposb.common.authsupport.util;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class XMLDocumentBuilderFactory {

    protected final DocumentBuilderFactory factory;

    public XMLDocumentBuilderFactory() {
        factory = DocumentBuilderFactory.newInstance();
    }

    public DocumentBuilder build() throws ParserConfigurationException {

        // Based on https://www.owasp.org/index.php/XML_External_Entity_(XXE)_Processing
        String FEATURE = null;
        try {
            FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
            factory.setFeature(FEATURE, true);

            FEATURE = "http://xml.org/sax/features/external-general-entities";
            factory.setFeature(FEATURE, false);

            FEATURE = "http://xml.org/sax/features/external-parameter-entities";
            factory.setFeature(FEATURE, false);

            factory.setXIncludeAware(false);
            factory.setExpandEntityReferences(false);
        } catch (ParserConfigurationException e) {
            // This should catch a failed setFeature feature
            log.info("ParserConfigurationException was thrown. The feature '" +
                    FEATURE +
                    "' is probably not supported by your XML processor.");
        }

        factory.setValidating(false);
        factory.setNamespaceAware(true);
        return factory.newDocumentBuilder();
    }
}
