package com.technophobia.eclipse.project.cache;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;

import com.technophobia.eclipse.project.ProjectChangedListener;
import com.technophobia.eclipse.project.ProjectEventType;
import com.technophobia.eclipse.project.ProjectFileChangedListener;
import com.technophobia.eclipse.project.ProjectManager;
import com.technophobia.substeps.FeatureEditorPlugin;
import com.technophobia.substeps.observer.CacheMonitor;
import com.technophobia.substeps.supplier.Callback1;

public class CacheAwareProjectManager implements ProjectManager {

    private final CacheMonitor<IProject>[] cacheMonitors;

    private final Collection<ProjectFileChangedListener> substepsChangeListeners;
    private final Collection<ProjectFileChangedListener> featureChangeListeners;
    private final Map<ProjectEventType, Set<ProjectChangedListener>> projectChangeListeners;

    private final IElementChangedListener classpathDependencyListener;
    private final IElementChangedListener sourceFileChangedListener;

    private final IResourceChangeListener featureSubstepFileChangeListener;


    public CacheAwareProjectManager(final CacheMonitor<IProject>... cacheMonitors) {
        this.cacheMonitors = cacheMonitors;
        this.substepsChangeListeners = new HashSet<ProjectFileChangedListener>();
        this.featureChangeListeners = new HashSet<ProjectFileChangedListener>();
        this.projectChangeListeners = new HashMap<ProjectEventType, Set<ProjectChangedListener>>();

        this.sourceFileChangedListener = createClassFilesChangedListener();
        this.classpathDependencyListener = createClasspathChangedListener();
        this.featureSubstepFileChangeListener = createSubstepsFileChangedListener();
    }


    @Override
    public void registerFrameworkListeners() {
        JavaCore.addElementChangedListener(sourceFileChangedListener);
        JavaCore.addElementChangedListener(classpathDependencyListener);

        ResourcesPlugin.getWorkspace().addResourceChangeListener(featureSubstepFileChangeListener,
                IResourceChangeEvent.POST_CHANGE);
    }


    @Override
    public void unregisterFrameworkListeners() {
        JavaCore.removeElementChangedListener(sourceFileChangedListener);
        JavaCore.removeElementChangedListener(classpathDependencyListener);
    }


    @Override
    public void projectFileChange(final IProject project, final IFile file) {
        if (isSubstepsFile(file)) {
            FeatureEditorPlugin.instance().info(
                    "Substeps file " + file.getLocation() + " has changed in project " + project);
            updateCaches(project);
            updateFileChangeListeners(file, substepsChangeListeners);
        } else if (isFeatureFile(file)) {
            FeatureEditorPlugin.instance().info(
                    "Feature file " + file.getLocation() + " has changed in project " + project);
            updateCaches(project);
            updateFileChangeListeners(file, featureChangeListeners);
        }
    }


    @Override
    public void preferencesChanged(final IProject project) {
        FeatureEditorPlugin.instance().info("Preferences changed for project " + project);
        updateCaches(project);
    }


    @Override
    public void addFeatureFileListener(final ProjectFileChangedListener listener) {
        this.featureChangeListeners.add(listener);
    }


    @Override
    public void removeFeatureFileListener(final ProjectFileChangedListener listener) {
        this.featureChangeListeners.remove(listener);
    }


    @Override
    public void addSubstepsFileListener(final ProjectFileChangedListener listener) {
        this.substepsChangeListeners.add(listener);
    }


    @Override
    public void removeSubstepsFileListener(final ProjectFileChangedListener listener) {
        this.substepsChangeListeners.remove(listener);
    }


    @Override
    public void addProjectListener(final ProjectEventType projectEventType, final ProjectChangedListener listener) {
        if (!projectChangeListeners.containsKey(projectEventType)) {
            projectChangeListeners.put(projectEventType, new HashSet<ProjectChangedListener>());
        }
        this.projectChangeListeners.get(projectEventType).add(listener);
    }


    @Override
    public void removeProjectListener(final ProjectEventType projectEventType, final ProjectChangedListener listener) {
        if (projectChangeListeners.containsKey(projectEventType)) {
            projectChangeListeners.get(projectEventType).remove(listener);
        }
    }


    protected void updateCaches(final IProject project) {
        for (final CacheMonitor<IProject> cacheMonitor : cacheMonitors) {
            cacheMonitor.refreshCacheFor(project);
        }
    }


    protected IElementChangedListener createClassFilesChangedListener() {
        return new ClassFileChangedListener(updateProjectCachesCallback(ProjectEventType.SourceFileAnnotationsChanged));
    }


    protected IElementChangedListener createClasspathChangedListener() {
        return new ClasspathChangedListener(updateProjectCachesCallback(ProjectEventType.ProjectDependenciesChanged));
    }


    private IResourceChangeListener createSubstepsFileChangedListener() {
        return new FileWithExtensionChangedListener(updateProjectCachesCallback(ProjectEventType.FeatureFileChanged),
                "feature", "substeps");
    }


    protected void notifyProjectChangeListeners(final IProject project, final ProjectEventType projectEventType) {
        if (projectChangeListeners.containsKey(projectEventType)) {
            for (final ProjectChangedListener listener : projectChangeListeners.get(projectEventType)) {
                listener.projectChanged(project);
            }
        }
    }


    private boolean isSubstepsFile(final IFile file) {
        return file.getFileExtension().toLowerCase().equals("substeps");
    }


    private boolean isFeatureFile(final IFile file) {
        return file.getFileExtension().toLowerCase().equals("feature");
    }


    private void updateFileChangeListeners(final IFile file, final Collection<ProjectFileChangedListener> listeners) {
        for (final ProjectFileChangedListener listener : listeners) {
            listener.projectFileChange(file.getProject(), file);
        }
    }


    private Callback1<IProject> updateProjectCachesCallback(final ProjectEventType projectEventType) {
        return new Callback1<IProject>() {
            @Override
            public void doCallback(final IProject project) {
                final Job job = new Job("Update caches") {

                    @Override
                    protected IStatus run(final IProgressMonitor monitor) {
                        updateCaches(project);
                        notifyProjectChangeListeners(project, projectEventType);
                        return Status.OK_STATUS;
                    }
                };
                job.setRule(ResourcesPlugin.getWorkspace().getRoot());
                job.setPriority(Job.DECORATE);
                job.schedule(200);
            }
        };
    }
}
