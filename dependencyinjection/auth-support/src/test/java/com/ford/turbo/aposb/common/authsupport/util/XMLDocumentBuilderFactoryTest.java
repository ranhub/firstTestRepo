package com.ford.turbo.aposb.common.authsupport.util;

import org.junit.Test;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.assertj.core.api.Assertions.assertThat;

public class XMLDocumentBuilderFactoryTest {

    @Test
    public void should_have_xmlExternalEntityProcessing_disabled() throws ParserConfigurationException {
        XMLDocumentBuilderFactory xmlDocumentBuilderFactory = new XMLDocumentBuilderFactory();
        DocumentBuilderFactory factory = xmlDocumentBuilderFactory.factory;

        xmlDocumentBuilderFactory.build();

        assertThat(factory.getFeature("http://apache.org/xml/features/disallow-doctype-decl")).isTrue();
        assertThat(factory.getFeature("http://xml.org/sax/features/external-general-entities")).isFalse();
        assertThat(factory.getFeature("http://xml.org/sax/features/external-parameter-entities")).isFalse();
        assertThat(factory.isXIncludeAware()).isFalse();
        assertThat(factory.isExpandEntityReferences()).isFalse();
    }
}