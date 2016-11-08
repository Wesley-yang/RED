/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.preferences;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.SharedScrolledComposite;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.robotframework.ide.eclipse.main.plugin.RedPlugin;
import org.robotframework.ide.eclipse.main.plugin.project.build.causes.ProblemCategory;
import org.robotframework.ide.eclipse.main.plugin.project.build.causes.ProblemCategory.ProblemCategoryType;
import org.robotframework.ide.eclipse.main.plugin.project.build.causes.ProblemCategory.Severity;

public class ValidationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

    private static final String ID = "org.robotframework.ide.eclipse.main.plugin.preferences.validation";

    private static final String PROBLEM_PREFERENCES = "problemPreferences";

    private final Map<ProblemCategory, Severity> currentPreferences;

    private IDialogSettings preferencesSettings;

    public ValidationPreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        setPreferenceStore(new ScopedPreferenceStore(InstanceScope.INSTANCE, RedPlugin.PLUGIN_ID));
        setDescription("RED validation settings");
        this.currentPreferences = new EnumMap<>(ProblemCategory.class);
        for (final ProblemCategory category : EnumSet.allOf(ProblemCategory.class)) {
            currentPreferences.put(category, category.getSeverity());
        }

        IDialogSettings dialogSettings = RedPlugin.getDefault().getDialogSettings();
        preferencesSettings = dialogSettings.getSection(PROBLEM_PREFERENCES);
        if (preferencesSettings == null) {
            preferencesSettings = dialogSettings.addNewSection(PROBLEM_PREFERENCES);
        }
    }

    @Override
    public void init(final IWorkbench workbench) {
        // nothing to do
    }

    @Override
    protected void createFieldEditors() {
        final Composite parent = getFieldEditorParent();
        final ScrolledContent scrolled = new ScrolledContent(parent);
        GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200).applyTo(scrolled);
        scrolled.addControlListener(new ControlAdapter() {

            @Override
            public void controlResized(ControlEvent e) {
                scrolled.reflow(true);
            }
        });

        Map<ProblemCategoryType, Collection<ProblemCategory>> categories = ProblemCategory.getCategories();
        for (Entry<ProblemCategoryType, Collection<ProblemCategory>> categpryEntry : categories.entrySet()) {
            if (!categpryEntry.getValue().isEmpty()) {
                createProblemCategorySection(scrolled.getBody(), categpryEntry.getKey(), categpryEntry.getValue());
            }
        }
    }

    @Override
    protected void performDefaults() {
        for (final ProblemCategory category : EnumSet.allOf(ProblemCategory.class)) {
            currentPreferences.put(category, category.getDefaultSeverity());
        }

        super.performDefaults();
    }

    private void createProblemCategorySection(final Composite parent, final ProblemCategoryType type,
            Collection<ProblemCategory> categpries) {
        final ExpandableComposite redExpandableComposite = createExpandableSection(parent, type);
        final Composite client = new Composite(redExpandableComposite, SWT.NONE);
        redExpandableComposite.setClient(client);

        for (final ProblemCategory category : categpries) {
            addField(new ComboBoxFieldEditor(category.getId(), category.getName(), category.getDescription(), 20,
                    entries(category), client));
        }
    }

    private ExpandableComposite createExpandableSection(final Composite parent, final ProblemCategoryType type) {
        final ExpandableComposite section = new ExpandableComposite(parent,
                ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
        section.setText(type.getName());
        section.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
        GridDataFactory.fillDefaults().grab(true, false).span(3, SWT.DEFAULT).hint(900, SWT.DEFAULT).applyTo(section);
        section.addExpansionListener(new ExpansionAdapter() {

            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                preferencesSettings.put(type.name(), (boolean) e.data);
                ScrolledContent scrolled = getParentScrolledComposite((ExpandableComposite) e.getSource());
                if (scrolled != null) {
                    scrolled.reflow(true);
                }
            }
        });
        section.setExpanded(preferencesSettings.getBoolean(type.name()));
        return section;
    }

    private ScrolledContent getParentScrolledComposite(Control control) {
        Control parent = control.getParent();
        while (!(parent instanceof ScrolledContent) && parent != null) {
            parent = parent.getParent();
        }
        if (parent instanceof ScrolledContent) {
            return (ScrolledContent) parent;
        }
        return null;
    }

    private static String[][] entries(final ProblemCategory category) {
        Severity[] severities = category.getPossibleSeverities();
        String[][] entries = new String[severities.length][];
        for (int i = 0; i < severities.length; i++) {
            entries[i] = entry(severities[i]);
        }
        return entries;
    }

    private static String[] entry(final Severity severity) {
        return new String[] { severity.getName(), severity.name() };
    }

    private class ScrolledContent extends SharedScrolledComposite {

        public ScrolledContent(Composite parent) {
            this(parent, SWT.H_SCROLL | SWT.V_SCROLL);
        }

        public ScrolledContent(Composite parent, int style) {
            super(parent, style);

            setExpandHorizontal(true);
            setExpandVertical(true);

            Composite body = new Composite(this, SWT.NONE);
            body.setFont(parent.getFont());
            setContent(body);
            GridLayoutFactory.fillDefaults().applyTo(body);
        }

        public Composite getBody() {
            return (Composite) getContent();
        }

    }

}
