/*
 * Copyright (c) 2021, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package jdk.javadoc.internal.doclets.formats.html;

import javax.lang.model.element.Element;

import com.sun.source.doctree.DocTree;
import jdk.javadoc.internal.doclets.formats.html.Navigation.PageMode;
import jdk.javadoc.internal.doclets.formats.html.markup.Text;
import jdk.javadoc.internal.doclets.toolkit.Content;
import jdk.javadoc.internal.doclets.toolkit.util.CommentHelper;
import jdk.javadoc.internal.doclets.toolkit.util.DocFileIOException;
import jdk.javadoc.internal.doclets.toolkit.util.DocPath;
import jdk.javadoc.internal.doclets.toolkit.util.DocPaths;
import jdk.javadoc.internal.doclets.toolkit.util.NewAPIBuilder;

import java.util.List;

import static com.sun.source.doctree.DocTree.Kind.SINCE;

/**
 * Generate File to list all the new API elements with the
 * appropriate links.
 *
 *  <p><b>This is NOT part of any supported API.
 *  If you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 *
 */
public class NewAPIListWriter extends SummaryListWriter<NewAPIBuilder> {

    /**
     * Constructor.
     *
     * @param configuration the configuration for this doclet
     */
    public NewAPIListWriter(HtmlConfiguration configuration, DocPath filename) {
        super(configuration, filename, PageMode.NEW, "new elements",
                Text.of(configuration.docResources.getText("doclet.New_API")),
                "doclet.Window_New_List");
    }

    /**
     * Get list of all the new elements.
     * Then instantiate NewAPIListWriter and generate File.
     *
     * @param configuration the current configuration of the doclet.
     * @throws DocFileIOException if there is a problem writing the new API list
     */
    public static void generate(HtmlConfiguration configuration) throws DocFileIOException {
        if (configuration.conditionalPages.contains(HtmlConfiguration.ConditionalPage.NEW)) {
            NewAPIListWriter writer = new NewAPIListWriter(configuration, DocPaths.NEW_LIST);
            writer.generateSummaryListFile(configuration.newAPIPageBuilder);
        }
    }

    @Override
    protected void addTableTabs(Table table, String headingKey) {
        if (!configuration.newAPIPageBuilder.releases.isEmpty()) {
            table.setDefaultTab(getTableCaption(headingKey)).setAlwaysShowDefaultTab(true);
            configuration.newAPIPageBuilder.releases.forEach(release -> {
                table.addTab(
                        configuration.docResources.getText("doclet.New_In_Release", release),
                        element -> {
                            if (!utils.hasDocCommentTree(element)) {
                                return false;
                            }
                            List<? extends DocTree> since = utils.getBlockTags(element, SINCE);
                            if (since.isEmpty()) {
                                return false;
                            }
                            CommentHelper ch = utils.getCommentHelper(element);
                            return since.stream().anyMatch(tree -> release.equals(ch.getBody(tree).toString()));
                        });
            });
            getMainBodyScript().append(table.getScript());
        }
    }

    @Override
    protected void addComments(Element e, Content desc) {
        addSummaryComment(e, desc);
    }

    @Override
    protected String getTableCaption(String headingKey) {
        return resources.getText("doclet.New_Elements", super.getTableCaption(headingKey));
    }
}
