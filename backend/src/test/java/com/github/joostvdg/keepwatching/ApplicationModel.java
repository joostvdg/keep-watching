package com.github.joostvdg.keepwatching;

import com.structurizr.Workspace;
import com.structurizr.io.WorkspaceWriterException;
import com.structurizr.io.plantuml.PlantUMLWriter;
import com.structurizr.model.*;
import com.structurizr.view.ComponentView;
import com.structurizr.view.ContainerView;
import com.structurizr.view.ViewSet;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Created by joost on 5-6-17.
 */
public class ApplicationModel {

    private Workspace workspace;
    private Model model;
    private SoftwareSystem keepWatchingSystem;
    private Container backend;

    @Before
    public void setup(){
        workspace = new Workspace("Keep-Watching", "Keep-Watching");
        model = workspace.getModel();
        Person user = model.addPerson("Watcher", "Watcher");
        keepWatchingSystem = model.addSoftwareSystem("Keep-Watching", "Keep-Watching");
        backend = keepWatchingSystem.addContainer("Backend", "Backend for Keep-Watching", "Spring Boot");
        Container postgresql = keepWatchingSystem.addContainer("postgresql", "Postgresql Database", "RDBMS");
        backend.uses(postgresql, "Data storage");

        Component movie = backend.addComponent("Movie", "A movie to watch", "Java");
        Component watchList = backend.addComponent("WatchList", "A watchlist to manage what to watch", "Java");

        watchList.delivers(user, "Movie watchlist management");
        watchList.uses(movie,"to watch");
        user.uses(watchList, "to manage list of movies to watch");
    }

    @Test
    public void generateContainerView() throws WorkspaceWriterException, IOException {
        ViewSet viewSet = workspace.getViews();
        ContainerView view = workspace.getViews().createContainerView(keepWatchingSystem, "Backend", "Container View");
        view.addAllContainers();
        view.addAllElements();
        StringWriter stringWriter = new StringWriter();
        PlantUMLWriter plantUMLWriter = new PlantUMLWriter();
        plantUMLWriter.write(workspace, stringWriter);
        // System.out.println(stringWriter.toString());
        String basePath = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        Path filePath = FileSystems.getDefault().getPath(basePath, "src/main/resources/container-model.txt");
        Files.write(filePath, stringWriter.toString().getBytes());
    }


    @Test
    public void generateComponentView() throws IOException, WorkspaceWriterException {
        ComponentView componentView = workspace.getViews().createComponentView(backend, "backend", "Backend Components");
        componentView.addAllComponents();
        StringWriter stringWriter = new StringWriter();
        PlantUMLWriter plantUMLWriter = new PlantUMLWriter();
        plantUMLWriter.write(workspace, stringWriter);
        // System.out.println(stringWriter.toString());
        String basePath = FileSystems.getDefault().getPath(".").toAbsolutePath().toString();
        Path filePath = FileSystems.getDefault().getPath(basePath, "src/main/resources/component-model.txt");
        Files.write(filePath, stringWriter.toString().getBytes());
    }
}
