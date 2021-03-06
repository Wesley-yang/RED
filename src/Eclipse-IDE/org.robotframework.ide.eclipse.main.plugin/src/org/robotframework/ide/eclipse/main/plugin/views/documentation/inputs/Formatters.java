/*
 * Copyright 2018 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.views.documentation.inputs;

import static java.util.stream.Collectors.joining;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.eclipse.swt.graphics.RGB;

import com.google.common.base.Strings;

class Formatters {

    public static String span(final String spanContent, final RGB color) {
        final String r = Strings.padStart(Integer.toHexString(color.red), 2, '0');
        final String g = Strings.padStart(Integer.toHexString(color.green), 2, '0');
        final String b = Strings.padStart(Integer.toHexString(color.blue), 2, '0');
        final String colorHex = "#" + r + g + b;
        return "<span style=\"color: " + colorHex + "\">" + spanContent + "</span>";
    }

    static String title(final String title, final int level) {
        return "<h" + level + ">" + title + "</h" + level + ">";
    }

    static String paragraph(final String content) {
        return "<p>" + content + "</p>";
    }

    static String bold(final String content) {
        return "<b>" + content + "</b>";
    }

    static String hyperlink(final URI href, final String label) {
        return hyperlink(href.toString(), label);
    }

    static String hyperlink(final String href, final String label) {
        return "<a href=\"" + href + "\">" + label + "</a>";
    }

    static String errorMessage(final Optional<URI> imgUri, final String error) {
        final StringBuilder builder = new StringBuilder();
        builder.append("<table style=\"border:none\">");
        builder.append("<tr style=\"border:none\">");
        builder.append("<td style=\"border:none\">");
        imgUri.ifPresent(
                uri -> builder.append("<img style=\"vertical-align: top;\" src=\"" + uri.toString() + "\"/>"));
        builder.append("</td>");
        builder.append("<td style=\"border: none; font-size: 1.1em;\">");
        builder.append(error);
        builder.append("</td>");
        builder.append("</tr>");
        builder.append("</table>");
        return builder.toString();
    }

    @SafeVarargs
    static String simpleHeader(final Optional<URI> imgUri, final String title,
            final List<String>... simpleTable) {
        return simpleHeader(imgUri, title, Arrays.asList(simpleTable));
    }

    static String simpleHeader(final Optional<URI> imgUri, final String title,
            final List<List<String>> simpleTable) {
        
        final StringBuilder builder = new StringBuilder();
        builder.append("<h3>");
        imgUri.ifPresent(
                uri -> builder.append("<img style=\"vertical-align: top;\" src=\"" + uri.toString() + "\"/> "));
        builder.append(title);
        builder.append("</h3>");

        if (!simpleTable.isEmpty()) {
            builder.append("<p>");
        }
        for (final List<String> row : simpleTable) {
            if (row.isEmpty()) {
                continue;
            }
            final String rowTitle = row.get(0);
            final String restOfRow = row.subList(1, row.size()).stream().collect(joining(" "));

            builder.append("<b>" + rowTitle + ": </b>");
            builder.append("<span style=\"font-family: monospace;\">" + restOfRow + "</span>");
            builder.append("<br/>");
        }
        if (!simpleTable.isEmpty()) {
            builder.append("</p>");
        }
        return builder.toString();

    }

}
