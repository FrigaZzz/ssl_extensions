package com.example.mavenpom.ui.toolwindow;

import com.example.mavenpom.actions.FetchMavenPomAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.ui.JBUI;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ResourceBundle;

public class MavenPomToolWindow extends JPanel {
    private final JBTextArea contentArea;
    private final Project project;
    private final ResourceBundle bundle;
    private final FetchMavenPomAction fetchAction;

    public MavenPomToolWindow(@NotNull Project project) {
        this.project = project;
        this.bundle = ResourceBundle.getBundle("messages.MavenPomBundle");
        this.fetchAction = new FetchMavenPomAction();
        
        setLayout(new BorderLayout());
        setBorder(JBUI.Borders.empty(5));

        contentArea = new JBTextArea();
        contentArea.setEditable(false);
        contentArea.setText(bundle.getString("toolwindow.empty.text"));

        JButton fetchButton = new JButton(bundle.getString("toolwindow.fetch.button"));
        fetchButton.addActionListener(e -> fetchPom());

        add(new JBScrollPane(contentArea), BorderLayout.CENTER);
        add(fetchButton, BorderLayout.NORTH);
    }

    private void fetchPom() {
        try {
            String content = fetchAction.fetchPom(project);
            contentArea.setText(content);
        } catch (Exception ex) {
            Messages.showErrorDialog(
                project,
                String.format(bundle.getString("toolwindow.error.fetch"), ex.getMessage()),
                bundle.getString("toolwindow.error.title")
            );
        }
    }
} 