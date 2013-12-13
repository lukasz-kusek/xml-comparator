/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Lukasz Kusek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.github.lukaszkusek.xml.comparator.document;

import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import com.github.lukaszkusek.xml.comparator.util.ResourceReader;

class XSLTransformer {

    private Templates templates;

    XSLTransformer(String xslFileName) throws TransformerConfigurationException, IOException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setErrorListener(new XSLTransformerErrorListener());

        templates = transformerFactory.newTemplates(
                new StreamSource(ResourceReader.getInputStream(xslFileName)));
    }

    String translate(String inputXml) throws TransformerException {
        Transformer transformer = templates.newTransformer();

        transformer.setErrorListener(new XSLTransformerErrorListener());

        StringWriter resultBuffer = new StringWriter();
        transformer.transform(
                new StreamSource(
                        new StringReader(inputXml)),
                new StreamResult(resultBuffer));

        return resultBuffer.toString();
    }
}
