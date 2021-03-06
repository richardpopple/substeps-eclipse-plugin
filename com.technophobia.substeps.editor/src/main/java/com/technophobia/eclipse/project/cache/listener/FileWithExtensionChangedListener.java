/*******************************************************************************
 * Copyright Technophobia Ltd 2012
 * 
 * This file is part of the Substeps Eclipse Plugin.
 * 
 * The Substeps Eclipse Plugin is free software: you can redistribute it and/or modify
 * it under the terms of the Eclipse Public License v1.0.
 * 
 * The Substeps Eclipse Plugin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Eclipse Public License for more details.
 * 
 * You should have received a copy of the Eclipse Public License
 * along with the Substeps Eclipse Plugin.  If not, see <http://www.eclipse.org/legal/epl-v10.html>.
 ******************************************************************************/
package com.technophobia.eclipse.project.cache.listener;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;

import com.technophobia.substeps.FeatureEditorPlugin;
import com.technophobia.substeps.supplier.Callback1;

public class FileWithExtensionChangedListener implements IResourceChangeListener {

    private final Callback1<IProject> projectChangedNotifier;
    private final Collection<String> fileExtensions;


    public FileWithExtensionChangedListener(final Callback1<IProject> projectChangedNotifier,
            final String... fileExtensions) {
        this.projectChangedNotifier = projectChangedNotifier;
        this.fileExtensions = Arrays.asList(fileExtensions);
    }


    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
        if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
            final IResourceDelta rootDelta = event.getDelta();

            final Collection<IProject> changedProjects = findChangedProjects(rootDelta);

            for (final IProject project : changedProjects) {
                projectChangedNotifier.doCallback(project);
            }
        }
    }


    private Collection<IProject> findChangedProjects(final IResourceDelta rootDelta) {
        try {
            final Collection<IProject> changedProjects = new HashSet<IProject>();
            final IResourceDeltaVisitor visitor = filesWithExtensionVisitor(changedProjects);
            rootDelta.accept(visitor);
            return changedProjects;
        } catch (final CoreException e) {
            FeatureEditorPlugin.instance().error(
                    "Could not determine changed resources for root delta " + rootDelta.getResource().getLocation());
            return Collections.emptyList();
        }
    }


    private IResourceDeltaVisitor filesWithExtensionVisitor(final Collection<IProject> changedProjects) {
        return new IResourceDeltaVisitor() {

            @Override
            public boolean visit(final IResourceDelta delta) throws CoreException {
                if (delta.getKind() == IResourceDelta.CHANGED) {
                    if ((delta.getFlags() & IResourceDelta.CONTENT) == IResourceDelta.CONTENT) {
                        final String fileExtension = delta.getResource().getFileExtension();
                        if (fileExtension != null && fileExtensions.contains(fileExtension.toLowerCase())) {
                            changedProjects.add(delta.getResource().getProject());
                        }
                    }
                }
                return true;
            }
        };
    }
}
