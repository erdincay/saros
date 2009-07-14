/*
 * DPP - Serious Distributed Pair Programming
 * (c) Freie Universitaet Berlin - Fachbereich Mathematik und Informatik - 2006
 * (c) Riad Djemili - 2006
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 1, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */
package de.fu_berlin.inf.dpp.ui;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.picocontainer.annotations.Inject;

import de.fu_berlin.inf.dpp.Saros;
import de.fu_berlin.inf.dpp.annotations.Component;
import de.fu_berlin.inf.dpp.preferences.PreferenceConstants;

/**
 * Contains the basic preferences for Saros.
 * 
 * TODO Rename to GeneralPreferencePage or SarosPreferencePage
 * 
 * @author rdjemili
 */
@Component(module = "prefs")
public class PreferencePage extends FieldEditorPreferencePage implements
    IWorkbenchPreferencePage {

    @Inject
    Saros saros;

    public PreferencePage() {
        super(FieldEditorPreferencePage.GRID);
        Saros.reinject(this);

        setPreferenceStore(saros.getPreferenceStore());
        setDescription("Your settings for Jabber.");
    }

    @Override
    public void createFieldEditors() {
        addField(new StringFieldEditor(PreferenceConstants.SERVER, "Server:",
            getFieldEditorParent()));

        addField(new StringFieldEditor(PreferenceConstants.USERNAME,
            "Username:", getFieldEditorParent()));

        StringFieldEditor passwordField = new StringFieldEditor(
            PreferenceConstants.PASSWORD, "Password:", getFieldEditorParent());
        passwordField.getTextControl(getFieldEditorParent()).setEchoChar('*');
        addField(passwordField);

        addField(new BooleanFieldEditor(PreferenceConstants.AUTO_CONNECT,
            "Automatically connect on startup.", getFieldEditorParent()));

        addField(new BooleanFieldEditor(PreferenceConstants.AUTO_FOLLOW_MODE,
            "Start in Follow Mode.", getFieldEditorParent()));

        addField(new BooleanFieldEditor(
            PreferenceConstants.FOLLOW_EXCLUSIVE_DRIVER,
            "On role changes follow exclusive driver automatically.",
            getFieldEditorParent()));

        addField(new BooleanFieldEditor(
            PreferenceConstants.CONCURRENT_UNDO,
            "Enable concurrent undo (only local changes are undone, session restart necessary).",
            getFieldEditorParent()));
    }

    /*
     * @see org.eclipse.ui.IWorkbenchPreferencePage
     */
    public void init(IWorkbench workbench) {
        // nothing to initialize
    }
}