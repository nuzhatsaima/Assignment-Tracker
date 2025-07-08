package org.app.gui;

import org.app.model.*;
import org.app.service.AssignmentService;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Submit Assignment Dialog for Students
 */
public class SubmitAssignmentDialog extends JDialog {
    private AssignmentService assignmentService;
    private Student student;
    private List<Course> courses;
    private boolean success = false;

    private JComboBox<Assignment> assignmentComboBox;
    private JTextArea contentArea;
    private JLabel assignmentDetailsLabel;
    private JList<String> attachmentsList;
    private DefaultListModel<String> attachmentsModel;
    private JButton addFileButton;
    private JButton removeFileButton;
    private List<String> selectedFiles;

    public SubmitAssignmentDialog(JFrame parent, Student student, List<Course> courses,
                                  AssignmentService assignmentService) {
        super(parent, "Submit Assignment", true);
        this.student = student;
        this.courses = courses;
        this.assignmentService = assignmentService;
        this.selectedFiles = new ArrayList<>();

        initializeComponents();
        setupLayout();

        setSize(600, 600); // Increased size to accommodate file attachments
        setLocationRelativeTo(parent);
    }

    private void initializeComponents() {
        // Get all available assignments from student's courses
        List<Assignment> availableAssignments = courses.stream()
                .flatMap(course -> assignmentService.getAssignmentsByCourse(course).stream())
                .filter(assignment -> assignment.getStatus() == AssignmentStatus.ACTIVE)
                .filter(assignment -> {
                    // Check if student hasn't already submitted
                    List<Submission> studentSubmissions = assignmentService.getSubmissionsByStudent(student);
                    return studentSubmissions.stream()
                            .noneMatch(sub -> sub.getAssignment().equals(assignment));
                })
                .toList();

        assignmentComboBox = new JComboBox<>(availableAssignments.toArray(new Assignment[0]));
        assignmentComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                                                          int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Assignment) {
                    Assignment assignment = (Assignment) value;
                    setText(assignment.getTitle() + " (" + assignment.getCourse().getCourseCode() + ")");
                }
                return this;
            }
        });

        contentArea = new JTextArea(8, 30);
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        contentArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLoweredBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        assignmentDetailsLabel = new JLabel("<html><i>Select an assignment to view details</i></html>");
        assignmentDetailsLabel.setVerticalAlignment(SwingConstants.TOP);

        // Add listener to update assignment details
        assignmentComboBox.addActionListener(e -> updateAssignmentDetails());

        // Initialize with first assignment if available
        if (assignmentComboBox.getItemCount() > 0) {
            updateAssignmentDetails();
        }

        // Attachments list
        attachmentsModel = new DefaultListModel<>();
        attachmentsList = new JList<>(attachmentsModel);
        attachmentsList.setBorder(BorderFactory.createTitledBorder("Attached Files"));
        attachmentsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Buttons for file attachment
        addFileButton = new JButton("Add File");
        removeFileButton = new JButton("Remove File");
        removeFileButton.setEnabled(false);

        addFileButton.addActionListener(e -> addFile());
        removeFileButton.addActionListener(e -> removeFile());

        // Listener to enable/disable remove button
        attachmentsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                removeFileButton.setEnabled(!attachmentsList.isSelectionEmpty());
            }
        });
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(76, 175, 80));
        JLabel titleLabel = new JLabel("Submit Assignment");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        headerPanel.add(titleLabel);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.WEST;

        // Assignment selection
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Select Assignment:"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.weightx = 1;
        formPanel.add(assignmentComboBox, gbc);

        // Assignment details
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        JScrollPane detailsScrollPane = new JScrollPane(assignmentDetailsLabel);
        detailsScrollPane.setPreferredSize(new Dimension(0, 80));
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Assignment Details"));
        formPanel.add(detailsScrollPane, gbc);

        // Content area
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; gbc.weighty = 0.7;
        JScrollPane contentScrollPane = new JScrollPane(contentArea);
        contentScrollPane.setBorder(BorderFactory.createTitledBorder("Your Submission Content"));
        formPanel.add(contentScrollPane, gbc);

        // Attachments
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2; gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.4;
        JScrollPane attachmentsScrollPane = new JScrollPane(attachmentsList);
        attachmentsScrollPane.setBorder(BorderFactory.createTitledBorder("Attachments"));
        formPanel.add(attachmentsScrollPane, gbc);

        // Buttons panel with both submission and file buttons
        JPanel bottomPanel = new JPanel(new BorderLayout());

        // File attachment buttons
        JPanel fileButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileButtonPanel.add(addFileButton);
        fileButtonPanel.add(removeFileButton);

        // Main action buttons
        JPanel actionButtonPanel = new JPanel(new FlowLayout());
        JButton submitButton = new JButton("Submit Assignment");
        submitButton.setBackground(new Color(76, 175, 80));
        submitButton.setForeground(Color.WHITE);
        submitButton.setPreferredSize(new Dimension(150, 35));

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(244, 67, 54));
        cancelButton.setForeground(Color.WHITE);
        cancelButton.setPreferredSize(new Dimension(80, 35));

        submitButton.addActionListener(e -> handleSubmission());
        cancelButton.addActionListener(e -> dispose());

        actionButtonPanel.add(submitButton);
        actionButtonPanel.add(cancelButton);

        bottomPanel.add(fileButtonPanel, BorderLayout.WEST);
        bottomPanel.add(actionButtonPanel, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void updateAssignmentDetails() {
        Assignment selected = (Assignment) assignmentComboBox.getSelectedItem();
        if (selected != null) {
            String details = String.format(
                    "<html><b>Course:</b> %s<br>" +
                            "<b>Type:</b> %s<br>" +
                            "<b>Due Date:</b> %s<br>" +
                            "<b>Max Marks:</b> %d<br>" +
                            "<b>Description:</b><br>%s</html>",
                    selected.getCourse().getCourseName(),
                    selected.getType().getDisplayName(),
                    selected.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")),
                    selected.getMaxMarks(),
                    selected.getDescription().length() > 100 ?
                            selected.getDescription().substring(0, 100) + "..." :
                            selected.getDescription()
            );
            assignmentDetailsLabel.setText(details);
        }
    }

    private void addFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select File to Attach");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            java.io.File file = fileChooser.getSelectedFile();
            String filePath = file.getAbsolutePath();

            // Add to list model and selected files
            attachmentsModel.addElement(filePath);
            selectedFiles.add(filePath);
        }
    }

    private void removeFile() {
        int selectedIndex = attachmentsList.getSelectedIndex();
        if (selectedIndex != -1) {
            // Remove from list model and selected files
            attachmentsModel.remove(selectedIndex);
            selectedFiles.remove(selectedIndex);
        }
    }

    private void handleSubmission() {
        Assignment selectedAssignment = (Assignment) assignmentComboBox.getSelectedItem();
        String content = contentArea.getText().trim();

        if (selectedAssignment == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an assignment.",
                    "Submission Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (content.isEmpty() && selectedFiles.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter submission content or attach at least one file.",
                    "Submission Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // Submit assignment with attachments
            Submission submission = assignmentService.submitAssignment(selectedAssignment, student, content);

            // Add file attachments to the submission
            for (String filePath : selectedFiles) {
                submission.addAttachment(filePath);
            }

            success = true;
            String message = "Assignment submitted successfully!";
            if (!selectedFiles.isEmpty()) {
                message += "\nWith " + selectedFiles.size() + " file(s) attached.";
            }

            JOptionPane.showMessageDialog(this,
                    message,
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Submission failed: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
