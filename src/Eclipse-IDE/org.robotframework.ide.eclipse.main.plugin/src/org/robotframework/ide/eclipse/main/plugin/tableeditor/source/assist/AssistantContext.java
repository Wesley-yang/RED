/*
 * Copyright 2015 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.robotframework.ide.eclipse.main.plugin.tableeditor.source.assist;

import java.util.function.Supplier;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.bindings.keys.KeySequence;
import org.rf.ide.core.environment.IRuntimeEnvironment;
import org.robotframework.ide.eclipse.main.plugin.RedPlugin;
import org.robotframework.ide.eclipse.main.plugin.RedPreferences;
import org.robotframework.ide.eclipse.main.plugin.model.RobotSuiteFile;
import org.robotframework.ide.eclipse.main.plugin.tableeditor.source.InformationControlSupport;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author Michal Anglart
 */
public class AssistantContext {

    private final InformationControlSupport infoControlSupport;

    private final Supplier<RobotSuiteFile> modelSupplier;

    private final KeySequence activationTrigger;

    private final AssistPreferences assistPreferences;

    public AssistantContext(final InformationControlSupport infoControlSupport,
            final Supplier<RobotSuiteFile> modelSupplier, final KeySequence activationTrigger) {
        this(infoControlSupport, modelSupplier, activationTrigger,
                new AssistPreferences(RedPlugin.getDefault().getPreferences()));
    }

    @VisibleForTesting
    AssistantContext(final InformationControlSupport infoControlSupport,
            final Supplier<RobotSuiteFile> modelSupplier, final KeySequence activationTrigger,
            final AssistPreferences assistPreferences) {
        this.infoControlSupport = infoControlSupport;
        this.modelSupplier = modelSupplier;
        this.activationTrigger = activationTrigger;
        this.assistPreferences = assistPreferences;
    }

    public InformationControlSupport getInfoControlSupport() {
        return infoControlSupport;
    }

    public void refreshPreferences() {
        assistPreferences.refresh();
    }

    public RobotSuiteFile getModel() {
        return modelSupplier.get();
    }

    public IRuntimeEnvironment getEnvironment() {
        return getModel().getRobotProject().getRuntimeEnvironment();
    }

    public IFile getFile() {
        return getModel().getFile();
    }

    public boolean isTsvFile() {
        return getModel().isTsvFile();
    }

    public KeySequence getActivationTrigger() {
        return activationTrigger;
    }

    public String getSeparatorToFollow() {
        return assistPreferences.getSeparatorToFollow(isTsvFile());
    }

    public char[] getAssistantAutoActivationChars() {
        return assistPreferences.getAssistantAutoActivationChars();
    }

    static class AssistPreferences {

        // caches interesting preferences

        private final RedPreferences redPreferences;

        private String separatorToUseInTsv;

        private String separatorToUseInRobot;

        private char[] autoActivationChars;

        AssistPreferences(final RedPreferences redPreferences) {
            this.redPreferences = redPreferences;
            refresh();
        }

        void refresh() {
            separatorToUseInRobot = redPreferences.getSeparatorToUse(false);
            separatorToUseInTsv = redPreferences.getSeparatorToUse(true);
            autoActivationChars = redPreferences.getAssistantAutoActivationChars();
        }

        String getSeparatorToFollow(final boolean isTsvFile) {
            return isTsvFile ? separatorToUseInTsv : separatorToUseInRobot;
        }

        char[] getAssistantAutoActivationChars() {
            return autoActivationChars;
        }
    }
}
