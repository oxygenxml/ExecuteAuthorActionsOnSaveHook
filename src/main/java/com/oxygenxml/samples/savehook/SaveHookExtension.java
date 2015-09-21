package com.oxygenxml.samples.savehook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.schlichtherle.io.FileInputStream;
import ro.sync.exml.editor.EditorPageConstants;
import ro.sync.exml.plugin.PluginDescriptor;
import ro.sync.exml.plugin.workspace.WorkspaceAccessPluginExtension;
import ro.sync.exml.workspace.api.PluginWorkspace;
import ro.sync.exml.workspace.api.editor.WSEditor;
import ro.sync.exml.workspace.api.editor.page.author.WSAuthorEditorPage;
import ro.sync.exml.workspace.api.editor.page.author.actions.AuthorActionsProvider;
import ro.sync.exml.workspace.api.listeners.WSEditorChangeListener;
import ro.sync.exml.workspace.api.listeners.WSEditorListener;
import ro.sync.exml.workspace.api.standalone.StandalonePluginWorkspace;

/**
 * Plugin extension - workspace access extension.
 */
public class SaveHookExtension implements WorkspaceAccessPluginExtension {
	/**
	 * Action to execute on the save hook.
	 */
	private List<String> actions;
	
	public void applicationStarted(final StandalonePluginWorkspace workspace) {
		workspace.addEditorChangeListener(new WSEditorChangeListener() {
			@Override
			public void editorOpened(URL url) {
				final WSEditor editorAccess = workspace.getEditorAccess(url, PluginWorkspace.MAIN_EDITING_AREA);
				editorAccess.addEditorListener(new WSEditorListener() {
					@Override
					public boolean editorAboutToBeSavedVeto(int operation) {
						if (editorAccess.getCurrentPageID().equals(EditorPageConstants.PAGE_AUTHOR)) {
							init();
							WSAuthorEditorPage page = (WSAuthorEditorPage) editorAccess.getCurrentPage();
							AuthorActionsProvider actionsProvider = page.getActionsProvider();
							Map<String, Object> authorExtensionActions = actionsProvider.getAuthorExtensionActions();
							page.getDocumentController().beginCompoundEdit();
							try {
								for (int i = 0; i < actions.size(); i++) {
									Object action = authorExtensionActions.get(actions.get(i));
									if (action != null) {
										actionsProvider.invokeAction(action);
									} else {
										System.err.println("Action not found " + actions.get(i));
									}
								}
							} finally {
								page.getDocumentController().endCompoundEdit();
							}
						}
						return true;
					}
				});
			}
		}, PluginWorkspace.MAIN_EDITING_AREA);
	}

	private void init() {
		if (actions == null) {
			actions = new ArrayList<String>();
			PluginDescriptor descriptor = SaveHookPlugin.getInstance().getDescriptor();
			File config = new File(descriptor.getBaseDir(), "etc/actions.config");
			if (config.exists()) {
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(new FileInputStream(config), "UTF-8"));
					// Right now we only have save hooks.
					String line = null;
					while ((line = reader.readLine()) != null) {
						actions.add(line.trim());
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (reader != null) {
						try {
							reader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	public boolean applicationClosing() {
		return true;
	}
}