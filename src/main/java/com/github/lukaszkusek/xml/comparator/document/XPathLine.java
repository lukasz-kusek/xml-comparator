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

import java.util.List;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

class XPathLine {

    private int index;
    private String xPath;
    private String value;
    private String attributeName;
    private String attributeValue;

    XPathLine(int index, String line, boolean ignoreNamespace) {
        this.index = index;
        List<String> lineElements = Splitter.on("\t").splitToList(line);

        setXPath(lineElements.get(0), ignoreNamespace);
        setValue(lineElements.get(1));
        setAttributeNameAndValue(lineElements.get(2));
    }

    private void setAttributeNameAndValue(String attributesNameAndValue) {
        String[] attribute = attributesNameAndValue.split("=", 2);

        if (attribute.length == 2) {
            attributeName = attribute[0];
            attributeValue = attribute[1];
        }
    }

    private void setValue(String value) {
        if (!Strings.isNullOrEmpty(value)) {
            this.value = value.trim();
        }
    }

    private void setXPath(String xPath, boolean ignoreNamespace) {
        this.xPath = xPath;

        if (ignoreNamespace) {
            this.xPath = NamespaceRemover.clean(this.xPath);
        }
    }

    int getIndex() {
        return index;
    }

    String getXPath() {
        return xPath;
    }

    String getValue() {
        return value;
    }

    String getAttributeName() {
        return attributeName;
    }

    String getAttributeValue() {
        return attributeValue;
    }

    private static class NamespaceRemover {

        private static final Cache<String, String> X_PATH_TO_CLEANED_X_PATH_CACHE;

        static {
            X_PATH_TO_CLEANED_X_PATH_CACHE = CacheBuilder
                    .newBuilder()
                    .maximumSize(10000)
                    .softValues()
                    .build();
        }


        private static String clean(String fullXPath) {
            String xPath = X_PATH_TO_CLEANED_X_PATH_CACHE.getIfPresent(fullXPath);

            if (xPath == null) {
                xPath = fullXPath
                        .replaceAll("@xmlns:.*", "@xmlns")
                        .replaceAll("[^/]*:", "")
                        .replaceAll("/[^/]*:", "/");

                X_PATH_TO_CLEANED_X_PATH_CACHE.put(fullXPath, xPath);
            }

            return xPath;
        }

    }
}
