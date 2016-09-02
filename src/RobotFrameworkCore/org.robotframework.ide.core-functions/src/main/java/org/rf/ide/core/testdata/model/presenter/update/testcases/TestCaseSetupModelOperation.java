/*
 * Copyright 2016 Nokia Solutions and Networks
 * Licensed under the Apache License, Version 2.0,
 * see license.txt file for details.
 */
package org.rf.ide.core.testdata.model.presenter.update.testcases;

import java.util.List;

import org.rf.ide.core.testdata.model.AModelElement;
import org.rf.ide.core.testdata.model.ModelType;
import org.rf.ide.core.testdata.model.presenter.update.ITestCaseTableElementOperation;
import org.rf.ide.core.testdata.model.table.testcases.TestCase;
import org.rf.ide.core.testdata.model.table.testcases.TestCaseSetup;
import org.rf.ide.core.testdata.text.read.IRobotTokenType;
import org.rf.ide.core.testdata.text.read.recognizer.RobotTokenType;

public class TestCaseSetupModelOperation implements ITestCaseTableElementOperation {

    @Override
    public boolean isApplicable(final ModelType elementType) {
        return elementType == ModelType.TEST_CASE_SETUP;
    }
    
    @Override
    public boolean isApplicable(final IRobotTokenType elementType) {
        return elementType == RobotTokenType.TEST_CASE_SETTING_SETUP;
    }

    @Override
    public AModelElement<?> create(final TestCase testCase, final String settingName, final List<String> args,
            final String comment) {
        final TestCaseSetup setup = testCase.newSetup();
        if (!args.isEmpty()) {
            setup.setKeywordName(args.get(0));
            for (int i = 1; i < args.size(); i++) {
                setup.addArgument(args.get(i));
            }
        }
        if (comment != null && !comment.isEmpty()) {
            setup.setComment(comment);
        }
        return setup;
    }

    @Override
    public void insert(final TestCase testCase, final int index, final AModelElement<?> modelElement) {
        testCase.addSetup(0, (TestCaseSetup) modelElement);
    }

    @Override
    public void update(final AModelElement<?> modelElement, final int index, final String value) {
        final TestCaseSetup setup = (TestCaseSetup) modelElement;
        if (index == 0) {
            setup.setKeywordName(value != null ? value : "");
        } else if (index > 0) {
            if (value != null) {
                setup.setArgument(index - 1, value);
            } else {
                setup.removeElementToken(index - 1);
            }
        }
    }

    @Override
    public void update(final AModelElement<?> modelElement, final List<String> newValues) {
        final TestCaseSetup setup = (TestCaseSetup) modelElement;

        setup.setKeywordName(newValues.isEmpty() ? "" : newValues.get(0));
        for (int i = 0; i < setup.getArguments().size(); i++) {
            setup.removeElementToken(0);
        }
        for (int i = 1; i < newValues.size(); i++) {
            setup.setArgument(i - 1, newValues.get(i));
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void remove(final TestCase testCase, final AModelElement<?> modelElement) {
        testCase.removeUnitSettings((AModelElement<TestCase>) modelElement);
    }
}
