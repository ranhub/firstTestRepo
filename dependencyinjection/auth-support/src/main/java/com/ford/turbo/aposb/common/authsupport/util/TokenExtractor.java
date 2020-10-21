package com.ford.turbo.aposb.common.authsupport.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Base64;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import com.ford.turbo.aposb.common.basemodels.controller.exception.AuthTokenMalformedException;
import com.ford.turbo.aposb.common.basemodels.controller.exception.AuthTokenNotFoundException;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @deprecated This is a temporary workaround that we're using until FIG is up and running. We can't be parsing
 * tokens at all by the time we go to production (because their internal structure is subject to change)
 */
@Deprecated
@Component
public class TokenExtractor {

    protected final DocumentBuilder documentBuilder;

    public TokenExtractor() throws ParserConfigurationException {
        documentBuilder = new XMLDocumentBuilderFactory().build();
    }

    public String extract(String authToken){

        if(authToken == null) {
            throw new AuthTokenNotFoundException();
        } else {
            try {
                byte[] e = Base64.getDecoder().decode(authToken);
                Element tokenData = this.checkXmlValidity(e);
                String jsonData = tokenData.getTextContent();

                ObjectMapper mapper = new ObjectMapper();
                TokenData token = mapper.readValue(jsonData, TokenData.class);

                return token.getUniqueAuthId();

            } catch (SAXException | IOException | IllegalArgumentException var4) {
                throw new AuthTokenMalformedException();
            }
        }
    }

    private Element checkXmlValidity(byte[] xml) throws SAXException, IOException {
        Document document = this.documentBuilder.parse(new ByteArrayInputStream(xml));
        NodeList elems = document.getElementsByTagName("TokenData");
        if(elems.getLength() != 1) {
            throw new IllegalArgumentException("Missing TokenData in parsed XML");
        } else {
            return (Element)elems.item(0);
        }
    }
}

