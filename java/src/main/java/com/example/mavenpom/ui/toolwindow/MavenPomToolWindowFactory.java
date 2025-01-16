package com.example.mavenpom.ui.toolwindow;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import java.util.ResourceBundle;

public class MavenPomToolWindowFactory implements ToolWindowFactory {
    private final ResourceBundle bundle = 
        ResourceBundle.getBundle("messages.MavenPomBundle");

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        MavenPomToolWindow mavenPomToolWindow = new MavenPomToolWindow(project);
        ContentFactory contentFactory = ContentFactory.getInstance();
        Content content = contentFactory.createContent(
            mavenPomToolWindow, 
            bundle.getString("toolwindow.content.title"), 
            false
        );
        toolWindow.getContentManager().addContent(content);
    }
} 