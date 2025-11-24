import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class TrackMySpendJava extends JFrame {

    private final String CURRENCY_SYMBOL = "₹"; 
    
    private Map<String, User> userDatabase = new HashMap<>();
    private User currentUser; 
    private final String FILE_NAME = "trackmyspend_users.dat";
    
    private JTabbedPane tabbedPane;
    private DefaultTableModel tableModel;
    private final Font DETAIL_FONT = new Font("Arial", Font.PLAIN, 16); 
    private final Font HEADER_FONT = new Font("Arial", Font.BOLD, 18);
    private final Font SUMMARY_FONT = new Font("Arial", Font.BOLD, 18);

    private JLabel totalSpentLabel, profileTotalLabel, profileGoalLabel, profileImageLabel;
    
    private JLabel bottomTotalBalanceLabel, bottomOnlineBalanceLabel, bottomOfflineBalanceLabel, bottomMonthlyGoalLabel; 

    private JTextField nameField, emailField, ageField, mobileField;
    private JTextField goalField;
    
    private JTextField dateField; 
    private JTextField descField, amountField;
    private JComboBox<String> categoryBox, filterCategoryBox, transactionTypeBox; // Added transactionTypeBox
    
    
    private Color PRIMARY_COLOR = new Color(240, 240, 240);
    private Color ACCENT_COLOR = new Color(70, 130, 180);

    private String[] categories = {"Food", "Travel", "Stationary", "Entertainment", "Rent", "Utilities", "Savings", "Other"};

    public TrackMySpendJava() {
        
        bottomOnlineBalanceLabel = new JLabel("Online: " + CURRENCY_SYMBOL + "0.00");
        bottomOfflineBalanceLabel = new JLabel("Offline: " + CURRENCY_SYMBOL + "0.00");
        bottomTotalBalanceLabel = new JLabel("Total Balance: " + CURRENCY_SYMBOL + "0.00");
        bottomMonthlyGoalLabel = new JLabel("| Monthly Goal: " + CURRENCY_SYMBOL + "0.00"); 

        setTitle("TrackMySpend - Java Edition"); 
        setSize(1000, 700); 
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));

        tabbedPane.addTab("Home & Profile", createProfilePanel());
        tabbedPane.addTab("Data Entry", createDataPanel());
        tabbedPane.addTab("Analysis (Graphs)", createAnalysisPanel()); 
        tabbedPane.addTab("Feedback", createFeedbackPanel()); 
        
        tabbedPane.addChangeListener(e -> updateDefaultButton());

        add(tabbedPane, BorderLayout.CENTER);
        
        add(createBottomStatusBar(), BorderLayout.SOUTH); 
    }
    
    private JPanel createBottomStatusBar() {
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 5));
        statusPanel.setBackground(new Color(220, 220, 220));
        
        bottomTotalBalanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        bottomOnlineBalanceLabel.setFont(DETAIL_FONT);
        bottomOfflineBalanceLabel.setFont(DETAIL_FONT);
        bottomMonthlyGoalLabel.setFont(DETAIL_FONT);
        
        statusPanel.add(bottomTotalBalanceLabel);
        statusPanel.add(new JSeparator(SwingConstants.VERTICAL));
        statusPanel.add(bottomOnlineBalanceLabel);
        statusPanel.add(bottomOfflineBalanceLabel);
        statusPanel.add(bottomMonthlyGoalLabel); 

        return statusPanel;
    }


    private void initializeUserUI() {
        setTitle("TrackMySpend (Logged in as: " + currentUser.getUsername() + ")");
        
        nameField.setText(currentUser.getName());
        emailField.setText(currentUser.getEmail());
        ageField.setText(String.valueOf(currentUser.getAge()));
        mobileField.setText(currentUser.getMobile());
        goalField.setText(String.valueOf(currentUser.getMonthlyGoal()));
        loadProfileImage(currentUser.getProfileImagePath());
        dateField.setText(LocalDate.now().toString()); 
        
        updateDashboard();
        populateTable(currentUser.getExpenses());
        
        updateDefaultButton();
    }
    
    private void updateDefaultButton() {
        JButton defaultBtn = null;
        int selectedIndex = tabbedPane.getSelectedIndex();

        if (selectedIndex == 1) { 
            try {
                JPanel rootPanel = (JPanel) tabbedPane.getComponentAt(1); 
                BorderLayout layout = (BorderLayout) rootPanel.getLayout();
                
                Component northComponent = layout.getLayoutComponent(rootPanel, BorderLayout.NORTH); 
                
                if (northComponent instanceof JPanel) {
                    JPanel inputPanel = (JPanel) northComponent;
                    if (inputPanel.getComponentCount() > 10) { 
                        defaultBtn = (JButton) inputPanel.getComponent(10); 
                    }
                }
            } catch (ClassCastException | NullPointerException ex) {
            }
        } else if (selectedIndex == 3) { 
            try {
                JPanel feedbackRootPanel = (JPanel) tabbedPane.getComponentAt(3);
                if (feedbackRootPanel.getComponentCount() > 0) {
                   
                     JPanel formCenter = (JPanel) feedbackRootPanel.getComponent(0); 
                     if (formCenter.getComponentCount() > 12) {
                         defaultBtn = (JButton) formCenter.getComponent(12);
                     }
                }
            } catch (ClassCastException | NullPointerException ex) {
            }
        }
        
        if (getRootPane() != null) {
            getRootPane().setDefaultButton(defaultBtn);
        }
    }


    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        JPanel profileDetailsSection = new JPanel(new GridBagLayout());
        profileDetailsSection.setOpaque(false);
        profileDetailsSection.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbcDetails = new GridBagConstraints();
        gbcDetails.insets = new Insets(8, 8, 8, 8); 
        gbcDetails.anchor = GridBagConstraints.WEST;
        
        profileImageLabel = new JLabel("Click to add/change image");
        profileImageLabel.setPreferredSize(new Dimension(150, 150));
        profileImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        profileImageLabel.setVerticalAlignment(SwingConstants.CENTER);
        profileImageLabel.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        profileImageLabel.setOpaque(true);
        profileImageLabel.setBackground(Color.LIGHT_GRAY);
        profileImageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) { selectProfileImage(); }
        });
        gbcDetails.gridx = 0; gbcDetails.gridy = 0; gbcDetails.gridheight = 4; gbcDetails.insets = new Insets(10, 10, 10, 20);
        profileDetailsSection.add(profileImageLabel, gbcDetails);
        
        gbcDetails.gridheight = 1;
        gbcDetails.weightx = 0; 
        
        GridBagConstraints fieldGBC = (GridBagConstraints) gbcDetails.clone();
        fieldGBC.gridx = 2;
        fieldGBC.fill = GridBagConstraints.HORIZONTAL; 
        fieldGBC.weightx = 1.0; 
        
        GridBagConstraints labelGBC = (GridBagConstraints) gbcDetails.clone();
        labelGBC.gridx = 1;
        
        labelGBC.gridy = 0; profileDetailsSection.add(new JLabel("Name:"), labelGBC);
        nameField = new JTextField(20); nameField.setFont(DETAIL_FONT); fieldGBC.gridy = 0; profileDetailsSection.add(nameField, fieldGBC);
        
        labelGBC.gridy = 1; profileDetailsSection.add(new JLabel("Email:"), labelGBC);
        emailField = new JTextField(20); emailField.setFont(DETAIL_FONT); fieldGBC.gridy = 1; profileDetailsSection.add(emailField, fieldGBC);
        
        labelGBC.gridy = 2; profileDetailsSection.add(new JLabel("Age:"), labelGBC);
        ageField = new JTextField(20); ageField.setFont(DETAIL_FONT); fieldGBC.gridy = 2; profileDetailsSection.add(ageField, fieldGBC);
        
        labelGBC.gridy = 3; profileDetailsSection.add(new JLabel("Mobile:"), labelGBC);
        mobileField = new JTextField(20); mobileField.setFont(DETAIL_FONT); fieldGBC.gridy = 3; profileDetailsSection.add(mobileField, fieldGBC);

        JPanel actionButtonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionButtonRow.setOpaque(false);
        JButton saveDetailsBtn = new JButton("Save Profile Details");
        saveDetailsBtn.setFont(DETAIL_FONT);
        saveDetailsBtn.setBackground(ACCENT_COLOR);
        saveDetailsBtn.setForeground(Color.WHITE);
        saveDetailsBtn.addActionListener(e -> saveProfileDetails());
        actionButtonRow.add(saveDetailsBtn);
        JButton changePassBtn = new JButton("Change Password");
        changePassBtn.setFont(DETAIL_FONT);
        changePassBtn.addActionListener(e -> showChangePasswordDialog());
        actionButtonRow.add(changePassBtn);
        
        gbcDetails.gridx = 0; gbcDetails.gridy = 4; gbcDetails.gridwidth = 3; gbcDetails.fill = GridBagConstraints.HORIZONTAL; gbcDetails.insets = new Insets(15, 10, 15, 10);
        profileDetailsSection.add(actionButtonRow, gbcDetails);
        
        JPanel goalPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        goalPanel.setOpaque(false);
        JLabel goalTitle = new JLabel("Set Monthly Goal (" + CURRENCY_SYMBOL + "):");
        goalTitle.setFont(DETAIL_FONT);
        goalPanel.add(goalTitle);
        goalField = new JTextField("0.00", 6);
        goalField.setFont(DETAIL_FONT);
        goalPanel.add(goalField);
        JButton updateGoalBtn = new JButton("Update Goal");
        updateGoalBtn.setFont(DETAIL_FONT);
        updateGoalBtn.addActionListener(e -> updateGoal());
        goalPanel.add(updateGoalBtn);
        JButton balanceBtn = new JButton("Update Balance");
        balanceBtn.setFont(DETAIL_FONT);
        balanceBtn.addActionListener(e -> showBalanceDialog());
        goalPanel.add(balanceBtn);
        
        gbcDetails.gridx = 0; gbcDetails.gridy = 5; gbcDetails.gridwidth = 3; gbcDetails.fill = GridBagConstraints.HORIZONTAL; gbcDetails.insets = new Insets(5, 10, 10, 10);
        profileDetailsSection.add(goalPanel, gbcDetails);
        
        JPanel balanceBreakdownPanel = createBalanceBreakdownPanel();
        gbcDetails.gridx = 0; gbcDetails.gridy = 6; gbcDetails.gridwidth = 3; gbcDetails.fill = GridBagConstraints.BOTH; gbcDetails.weighty = 1.0; gbcDetails.insets = new Insets(10, 10, 10, 10);
        profileDetailsSection.add(balanceBreakdownPanel, gbcDetails);
        
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFont(DETAIL_FONT);
        logoutBtn.addActionListener(e -> logout());
        gbcDetails.gridx = 0; gbcDetails.gridy = 7; 
        gbcDetails.gridwidth = 3; gbcDetails.weighty = 0.0; gbcDetails.fill = GridBagConstraints.NONE; gbcDetails.anchor = GridBagConstraints.SOUTHWEST; gbcDetails.insets = new Insets(10, 10, 10, 10);
        profileDetailsSection.add(logoutBtn, gbcDetails);
        
        
        JPanel summarySection = new JPanel(new GridBagLayout());
        summarySection.setOpaque(false);
        
        JPanel summaryBox = createSummaryBox();
        
        gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0; 
        summarySection.add(summaryBox, gbc);

        
        gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.weightx = 1.5; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        panel.add(profileDetailsSection, gbc);
        
        gbc.gridx = 1; gbc.weightx = 1.0; gbc.fill = GridBagConstraints.BOTH;
        panel.add(summarySection, gbc);

        return panel;
    }
    
    private GridBagConstraints getGBC(int x, int inty, int fill) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = x;
        gbc.gridy = inty;
        gbc.anchor = GridBagConstraints.WEST; 
        gbc.fill = fill; 
        
        if (fill == GridBagConstraints.HORIZONTAL || fill == GridBagConstraints.BOTH) {
            gbc.weightx = 1.0;
        }
        return gbc;
    }

    private JPanel createSummaryBox() {
        JPanel summaryBox = new JPanel(new GridLayout(4, 1));
        summaryBox.setBorder(BorderFactory.createTitledBorder("Financial Summary - This Month"));
        summaryBox.setBackground(new Color(230, 240, 255));
        summaryBox.setPreferredSize(new Dimension(250, 150));

        profileGoalLabel = new JLabel("Goal: " + CURRENCY_SYMBOL + "0.00");
        profileTotalLabel = new JLabel("Total Spent: " + CURRENCY_SYMBOL + "0.0");
        JLabel statusLabel = new JLabel("Status: Good");
        
        profileGoalLabel.setFont(SUMMARY_FONT);
        profileTotalLabel.setFont(SUMMARY_FONT);
        statusLabel.setFont(SUMMARY_FONT);

        summaryBox.add(profileGoalLabel);
        summaryBox.add(profileTotalLabel);
        summaryBox.add(statusLabel);
        
        return summaryBox;
    }
    
    private JPanel createBalanceBreakdownPanel() {
        JPanel breakdownPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        breakdownPanel.setBorder(BorderFactory.createTitledBorder("Current Balance Breakdown"));
        breakdownPanel.setBackground(new Color(240, 255, 240));
        
        JLabel totalTitle = new JLabel("Total Funds:");
        JLabel onlineTitle = new JLabel("Online Balance:");
        JLabel offlineTitle = new JLabel("Offline Cash:");
        
        totalTitle.setFont(DETAIL_FONT);
        onlineTitle.setFont(DETAIL_FONT);
        offlineTitle.setFont(DETAIL_FONT);
        
        bottomTotalBalanceLabel.setFont(DETAIL_FONT);
        bottomOnlineBalanceLabel.setFont(DETAIL_FONT);
        bottomOfflineBalanceLabel.setFont(DETAIL_FONT);

        breakdownPanel.add(totalTitle);
        breakdownPanel.add(bottomTotalBalanceLabel); 
        
        breakdownPanel.add(onlineTitle);
        breakdownPanel.add(bottomOnlineBalanceLabel);
        
        breakdownPanel.add(offlineTitle);
        breakdownPanel.add(bottomOfflineBalanceLabel);
        
        return breakdownPanel;
    }


    private JPanel createDataPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        inputPanel.setBackground(PRIMARY_COLOR);
        inputPanel.setBorder(BorderFactory.createTitledBorder("Add Transaction"));

        dateField = new JTextField(LocalDate.now().toString(), 10);
        dateField.setToolTipText("Date must be today or future (YYYY-MM-DD)");

        descField = new JTextField(10);
        amountField = new JTextField(6);
        categoryBox = new JComboBox<>(categories);
        
        String[] transactionTypes = {"Online", "Offline"};
        transactionTypeBox = new JComboBox<>(transactionTypes);
        
        JButton addBtn = new JButton("Add Data");
        addBtn.setBackground(ACCENT_COLOR);
        addBtn.setForeground(Color.WHITE);

        inputPanel.add(new JLabel("Date (YYYY-MM-DD):")); inputPanel.add(dateField); 
        inputPanel.add(new JLabel("Note:")); inputPanel.add(descField);
        inputPanel.add(new JLabel("Category:")); inputPanel.add(categoryBox);
        inputPanel.add(new JLabel("Amount ("+ CURRENCY_SYMBOL +"):")); inputPanel.add(amountField);
        inputPanel.add(new JLabel("Source:")); inputPanel.add(transactionTypeBox); // Added Source
        
        panel.add(inputPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new BorderLayout());
        
        JPanel topCenterPanel = new JPanel(new BorderLayout());
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterCategoryBox = new JComboBox<>(categories);
        filterCategoryBox.insertItemAt("ALL Categories", 0);
        filterCategoryBox.setSelectedIndex(0);
        JButton filterBtn = new JButton("Filter");
        filterBtn.addActionListener(e -> filterExpenses());
        
        filterPanel.add(new JLabel("Filter by Category:"));
        filterPanel.add(filterCategoryBox);
        filterPanel.add(filterBtn);
        topCenterPanel.add(filterPanel, BorderLayout.WEST);

        JPanel addBtnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addBtnPanel.add(addBtn);
        topCenterPanel.add(addBtnPanel, BorderLayout.EAST);
        
        centerPanel.add(topCenterPanel, BorderLayout.NORTH);


        String[] columns = {"Date", "Note", "Category", "Amount ("+ CURRENCY_SYMBOL +")", "Source"}; 
        tableModel = new DefaultTableModel(columns, 0);
        JTable table = new JTable(tableModel);
        table.setRowHeight(25);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JScrollPane scrollPane = new JScrollPane(table);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalSpentLabel = new JLabel("Total Spent (This Month): " + CURRENCY_SYMBOL + "0.00"); 
        totalSpentLabel.setFont(new Font("Arial", Font.BOLD, 16));
        bottomPanel.add(totalSpentLabel);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> addExpense());

        return panel;
    }

    private JPanel createAnalysisPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        JLabel title = new JLabel("Spending Distribution", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        
        JTabbedPane graphTabs = new JTabbedPane();
        
        graphTabs.addTab("Pie Chart (Distribution)", createPieChartPanel());
        graphTabs.addTab("Bar Chart (Totals)", createBarChartPanel()); 
        graphTabs.addTab("Line Chart (Trend)", createLineChartPanel()); 
        
        panel.add(graphTabs, BorderLayout.CENTER);
        
        JButton refreshBtn = new JButton("Refresh Graphs");
        refreshBtn.addActionListener(e -> graphTabs.repaint());
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }
    
    private JPanel createPieChartPanel() {
         JPanel graphPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentUser != null) { 
                    drawPieChart(g, getWidth(), getHeight(), currentUser.getExpenses()); 
                } else {
                    g.setColor(Color.BLACK);
                    g.drawString("Loading user data...", getWidth() / 2 - 50, getHeight() / 2);
                }
            }
        };
        graphPanel.setBackground(Color.WHITE);
        return graphPanel;
    }

    private JPanel createBarChartPanel() {
        JPanel barChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentUser != null) {
                    drawBarChart(g, getWidth(), getHeight(), currentUser.getExpenses());
                } else {
                    g.setColor(Color.BLACK);
                    g.drawString("Loading user data...", getWidth() / 2 - 50, getHeight() / 2);
                }
            }
        };
        barChartPanel.setBackground(Color.WHITE);
        return barChartPanel;
    }
    
    private JPanel createLineChartPanel() {
        JPanel lineChartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (currentUser != null) {
                    drawLineChart(g, getWidth(), getHeight(), currentUser.getExpenses());
                } else {
                    g.setColor(Color.BLACK);
                    g.drawString("Loading user data...", getWidth() / 2 - 50, getHeight() / 2);
                }
            }
        };
        lineChartPanel.setBackground(Color.WHITE);
        return lineChartPanel;
    }

    
    private JPanel createFeedbackPanel() {
        JPanel panel = new JPanel(new BorderLayout(50, 0)); 
        panel.setBackground(new Color(240, 240, 240));

        JPanel formCenter = new JPanel(new GridBagLayout());
        formCenter.setBackground(Color.WHITE);
        formCenter.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        formCenter.setPreferredSize(new Dimension(500, 450)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        JLabel title = new JLabel("Feedback Form", SwingConstants.CENTER);
        title.setFont(new Font("Serif", Font.BOLD, 22));
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2; gbc.weighty = 0;
        formCenter.add(title, gbc);
        
        gbc.gridwidth = 1;
        
        gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formCenter.add(new JLabel("Name:"), gbc);
        JTextField nameFieldF = new JTextField(20);
        nameFieldF.setEditable(true); 
        if (currentUser != null) nameFieldF.setText(currentUser.getName());
        gbc.gridx = 1; gbc.weightx = 1.0;
        formCenter.add(nameFieldF, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formCenter.add(new JLabel("Age:"), gbc);
        JTextField ageFieldF = new JTextField(String.valueOf(currentUser != null ? currentUser.getAge() : 0), 20);
        ageFieldF.setEditable(true);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formCenter.add(ageFieldF, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.WEST; gbc.weightx = 0;
        formCenter.add(new JLabel("Email:"), gbc);
        JTextField emailFieldF = new JTextField(currentUser != null ? currentUser.getEmail() : "", 20);
        emailFieldF.setEditable(true);
        gbc.gridx = 1; gbc.weightx = 1.0;
        formCenter.add(emailFieldF, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.weightx = 0;
        formCenter.add(new JLabel("Feedback:"), gbc);
        
        JTextArea commentsArea = new JTextArea(10, 20);
        commentsArea.setLineWrap(true);
        commentsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(commentsArea);
        
        commentsArea.setEditable(true);

        gbc.gridx = 1; gbc.weightx = 1.0; gbc.weighty = 1.0; gbc.fill = GridBagConstraints.BOTH;
        formCenter.add(scrollPane, gbc);

        JButton submitBtn = new JButton("Submit");
        submitBtn.addActionListener(e -> {
            if (commentsArea.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your feedback before submitting.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, 
                "Feedback submitted by " + nameFieldF.getText() + ". Thank you!", 
                "Feedback Submitted", 
                JOptionPane.INFORMATION_MESSAGE);
            commentsArea.setText("");
        });
        
        tabbedPane.addChangeListener(e -> {
            if (tabbedPane.getSelectedComponent() == panel) {
                SwingUtilities.getRootPane(this).setDefaultButton(submitBtn);
            } else {
                SwingUtilities.getRootPane(this).setDefaultButton(null); 
            }
        });

        gbc.gridx = 1; gbc.gridy = 5; gbc.gridwidth = 1; gbc.weighty = 0; gbc.fill = GridBagConstraints.NONE; gbc.anchor = GridBagConstraints.CENTER;
        formCenter.add(submitBtn, gbc);
        
        JPanel devInfoPanel = new JPanel();
        devInfoPanel.setLayout(new BoxLayout(devInfoPanel, BoxLayout.Y_AXIS));
        devInfoPanel.setBackground(new Color(240, 240, 240));
        devInfoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        devInfoPanel.setPreferredSize(new Dimension(250, 100)); 
        
        JLabel devTitle = new JLabel("Developer");
        devTitle.setFont(new Font("Serif", Font.BOLD, 18));
        devTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel devName = new JLabel("--Bhoomi Amlani--"); 
        devName.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel contactTitle = new JLabel("Contact Info");
        contactTitle.setFont(new Font("Serif", Font.BOLD, 18));
        contactTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel contactEmail = new JLabel("bhoomiamlani363@gmail.com"); 
        contactEmail.setAlignmentX(Component.CENTER_ALIGNMENT);

        devInfoPanel.add(Box.createVerticalStrut(20));
        devInfoPanel.add(devTitle);
        devInfoPanel.add(devName);
        devInfoPanel.add(Box.createVerticalStrut(20));
        devInfoPanel.add(contactTitle);
        devInfoPanel.add(contactEmail);

        
        JPanel container = new JPanel(new BorderLayout());
        container.add(formCenter, BorderLayout.CENTER);
        container.add(devInfoPanel, BorderLayout.EAST);
        
        panel.add(container);
        
        return panel;
    }

    
    private void showBalanceDialog() {
        JTextField onlineField = new JTextField(String.format("%.2f", currentUser.getOnlineBalance()));
        JTextField offlineField = new JTextField(String.format("%.2f", currentUser.getOfflineBalance()));
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Online Money (" + CURRENCY_SYMBOL + "):"));
        panel.add(onlineField);
        panel.add(new JLabel("Offline Money (" + CURRENCY_SYMBOL + "):"));
        panel.add(offlineField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Update User Balance", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                double newOnline = Double.parseDouble(onlineField.getText());
                double newOffline = Double.parseDouble(offlineField.getText());
                
                currentUser.setOnlineBalance(newOnline);
                currentUser.setOfflineBalance(newOffline);
                
                saveUserData(userDatabase);
                updateDashboard();
                JOptionPane.showMessageDialog(this, "Balance updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid number entered for balance.", "Input Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void addExpense() {
        try {
            String desc = descField.getText();
            double amount = Double.parseDouble(amountField.getText());
            String cat = (String) categoryBox.getSelectedItem();
            String source = (String) transactionTypeBox.getSelectedItem(); 
            boolean isOnline = "Online".equals(source);
            
            LocalDate date = LocalDate.parse(dateField.getText().trim());
            
            if (date.isBefore(LocalDate.now())) {
                JOptionPane.showMessageDialog(this, "Error: Cannot enter expenses for previous dates (" + date + ").", "Date Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            double currentBalance = isOnline ? currentUser.getOnlineBalance() : currentUser.getOfflineBalance();

            if (currentBalance < amount) {
                JOptionPane.showMessageDialog(this, "Warning: Expense exceeds your selected " + source + " balance (" + CURRENCY_SYMBOL + String.format("%.2f", currentBalance) + ").", "Balance Warning", JOptionPane.WARNING_MESSAGE);
            }
            
            if (isOnline) {
                currentUser.setOnlineBalance(currentUser.getOnlineBalance() - amount); 
            } else {
                currentUser.setOfflineBalance(currentUser.getOfflineBalance() - amount);
            }
            
            Expense exp = new Expense(desc, amount, cat, date, isOnline);
            currentUser.getExpenses().add(exp); 
            populateTable(currentUser.getExpenses()); 
            
            saveUserData(userDatabase); 
            updateDashboard();
            
            descField.setText("");
            amountField.setText("");
            dateField.setText(LocalDate.now().toString()); 
            
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Date format. Please use YYYY-MM-DD.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Amount. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "An unexpected error occurred: " + ex.getMessage(), "Input Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    
    private void filterExpenses() {
        String selectedCategory = (String) filterCategoryBox.getSelectedItem();
        ArrayList<Expense> filteredList = new ArrayList<>();
        
        if ("ALL Categories".equals(selectedCategory)) {
            ArrayList<Expense> sortedList = new ArrayList<>(currentUser.getExpenses());
            sortedList.sort(Comparator.comparing(Expense::getDate).reversed());
            filteredList.addAll(sortedList);
        } else {
            for (Expense e : currentUser.getExpenses()) {
                if (e.getCategory().equals(selectedCategory)) {
                    filteredList.add(e);
                }
            }
            filteredList.sort(Comparator.comparing(Expense::getDate).reversed());
        }
        populateTable(filteredList);
    }

    private void saveProfileDetails() {
        try {
            currentUser.setName(nameField.getText());
            currentUser.setEmail(emailField.getText());
            currentUser.setAge(Integer.parseInt(ageField.getText()));
            currentUser.setMobile(mobileField.getText());
            
            saveUserData(userDatabase);
            JOptionPane.showMessageDialog(this, "Profile Details Saved!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Age format. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showChangePasswordDialog() {
        JPasswordField newPass = new JPasswordField(); 
        JPasswordField confirmPass = new JPasswordField();
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("New Password:"));
        panel.add(newPass);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPass);

        int result = JOptionPane.showConfirmDialog(this, panel, "Change Password", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String newPassword = new String(newPass.getPassword());
            String confirmedPassword = new String(confirmPass.getPassword());
            
            if (newPassword.isEmpty()) {
                 JOptionPane.showMessageDialog(this, "Password cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
            } else if (!newPassword.equals(confirmedPassword)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                currentUser.setPassword(newPassword); 
                saveUserData(userDatabase);
                JOptionPane.showMessageDialog(this, "Password successfully changed!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to log out?", 
            "Confirm Logout", 
            JOptionPane.YES_NO_OPTION);
            
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            relaunchAuth();
        }
    }
    
    private void updateGoal() {
        try {
            double newGoal = Double.parseDouble(goalField.getText());
            currentUser.setMonthlyGoal(newGoal);
            saveUserData(userDatabase);
            updateDashboard(); 
            JOptionPane.showMessageDialog(this, "Monthly Goal Updated!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Invalid Goal Amount. Please enter a number.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void selectProfileImage() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String path = selectedFile.getAbsolutePath();
            currentUser.setProfileImagePath(path);
            loadProfileImage(path);
            saveUserData(userDatabase);
            JOptionPane.showMessageDialog(this, "Profile image updated.", "Image", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadProfileImage(String path) {
        if (path != null && !path.isEmpty()) {
            try {
                ImageIcon originalIcon = new ImageIcon(path);
                Image img = originalIcon.getImage().getScaledInstance(profileImageLabel.getWidth(), profileImageLabel.getHeight(), Image.SCALE_SMOOTH);
                profileImageLabel.setIcon(new ImageIcon(img));
                profileImageLabel.setText("");
                profileImageLabel.setBackground(Color.WHITE); 
            } catch (Exception e) {
                profileImageLabel.setIcon(null);
                profileImageLabel.setText("Image not found");
                profileImageLabel.setBackground(PRIMARY_COLOR);
            }
        } else {
            profileImageLabel.setIcon(null);
            profileImageLabel.setText("Click to add/change image");
            profileImageLabel.setBackground(PRIMARY_COLOR);
        }
    }

    private void populateTable(ArrayList<Expense> list) {
        tableModel.setRowCount(0); 
        list.sort(Comparator.comparing(Expense::getDate).reversed());
        
        for (Expense e : list) {
            String source = e.isOnline() ? "Online" : "Offline";
            tableModel.addRow(new Object[]{e.getDate(), e.getDescription(), e.getCategory(), String.format("%.2f", e.getAmount()), source});
        }
        
        if(tabbedPane != null && tabbedPane.getComponentCount() > 1) {
            try {
                JPanel rootDataPanel = (JPanel) tabbedPane.getComponentAt(1);
                JPanel centerPanel = (JPanel) rootDataPanel.getComponent(1); 
                JScrollPane scrollPane = (JScrollPane) centerPanel.getComponent(1); 
                ((JTable)scrollPane.getViewport().getView()).revalidate();
            } catch (ClassCastException | ArrayIndexOutOfBoundsException ex) {
                 System.err.println("Error revalidating table after population: " + ex.getMessage());
            }
        }
    }

    private void updateDashboard() {
        if (currentUser == null) {
            profileTotalLabel.setText("Total Spent: " + CURRENCY_SYMBOL + "0.0");
            profileGoalLabel.setText("Goal: " + CURRENCY_SYMBOL + "0.00");
            return; 
        }
        
        double total = 0;
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        for (Expense e : currentUser.getExpenses()) {
            if (e.getDate().getMonthValue() == currentMonth && e.getDate().getYear() == currentYear) {
                total += e.getAmount();
            }
        }
        
        String totalFormatted = String.format("%.2f", total);
        
        totalSpentLabel.setText("Total Spent (This Month): " + CURRENCY_SYMBOL + totalFormatted);
        profileTotalLabel.setText("Total Spent: " + CURRENCY_SYMBOL + totalFormatted);
        profileGoalLabel.setText("Goal: " + CURRENCY_SYMBOL + String.format("%.2f", currentUser.getMonthlyGoal()));
        
        if(total > currentUser.getMonthlyGoal()) {
            profileTotalLabel.setForeground(Color.RED);
        } else {
            profileTotalLabel.setForeground(new Color(0, 150, 0)); 
        }
        
        double totalBalance = currentUser.getTotalBalance();
        bottomOnlineBalanceLabel.setText("Online: " + CURRENCY_SYMBOL + String.format("%.2f", currentUser.getOnlineBalance()));
        bottomOfflineBalanceLabel.setText("Offline: " + CURRENCY_SYMBOL + String.format("%.2f", currentUser.getOfflineBalance()));
        bottomTotalBalanceLabel.setText("Total Balance: " + CURRENCY_SYMBOL + String.format("%.2f", totalBalance));
        bottomMonthlyGoalLabel.setText("| Monthly Goal: " + CURRENCY_SYMBOL + String.format("%.2f", currentUser.getMonthlyGoal())); 
        
        if (totalBalance < 0) {
            bottomTotalBalanceLabel.setForeground(Color.RED);
        } else {
            bottomTotalBalanceLabel.setForeground(Color.BLUE);
        }
        
        if (tabbedPane != null && tabbedPane.getComponentCount() > 2) {
             tabbedPane.getComponentAt(2).repaint();
        }
    }

    private void drawPieChart(Graphics g, int w, int h, ArrayList<Expense> expensesToAnalyze) {
        
        Map<String, Double> catTotals = getMonthlyTotals(expensesToAnalyze);
        double grandTotal = catTotals.values().stream().mapToDouble(Double::doubleValue).sum();
        
        if (grandTotal == 0) {
            g.drawString("No Spending this Month", w/2 - 50, h/2);
            return;
        }
        
        int x = w / 2 - 200; 
        int y = h / 2 - 150;
        int diameter = 300;
        int startAngle = 0;
        
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK, Color.DARK_GRAY};
        int colorIndex = 0;
        int legendX = w / 2 + 50;
        int legendY = 50;
        
        g.setFont(new Font("Arial", Font.PLAIN, 12));

        for (Map.Entry<String, Double> entry : catTotals.entrySet()) {
            double amount = entry.getValue();
            int arcAngle = (int) Math.round((amount / grandTotal) * 360);
            if (arcAngle == 0 && amount > 0) arcAngle = 1; 

            g.setColor(colors[colorIndex % colors.length]);
            g.fillArc(x, y, diameter, diameter, startAngle, arcAngle);

            g.fillRect(legendX, legendY, 20, 20);
            g.setColor(Color.BLACK);
            g.drawString(entry.getKey() + ": " + CURRENCY_SYMBOL + String.format("%.2f", amount), legendX + 30, legendY + 15);
            
            startAngle += arcAngle;
            colorIndex++;
            legendY += 30;
        }
    }
    
    private void drawBarChart(Graphics g, int w, int h, ArrayList<Expense> expensesToAnalyze) {
        Map<String, Double> catTotals = getMonthlyTotals(expensesToAnalyze);
        double maxTotal = catTotals.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);

        if (maxTotal == 0) {
            g.drawString("No Spending this Month", w/2 - 50, h/2);
            return;
        }

        int padding = 50;
        int barGap = 40;
        int barWidth = 30;
        int chartHeight = h - 2 * padding;
        
        Color[] colors = {Color.RED, Color.BLUE, Color.GREEN, Color.ORANGE, Color.MAGENTA, Color.CYAN, Color.PINK, Color.DARK_GRAY};
        
        g.setColor(Color.BLACK);
        g.drawLine(padding, padding, padding, h - padding); 
        g.drawLine(padding, h - padding, w - padding, h - padding); 
        
        g.drawString(CURRENCY_SYMBOL + String.format("%.0f", maxTotal), 5, padding + 10);
        
        int x = padding + 10;
        int colorIndex = 0;
        g.setFont(new Font("Arial", Font.PLAIN, 10));

        for (Map.Entry<String, Double> entry : catTotals.entrySet()) {
            double amount = entry.getValue();
            int barHeight = (int) ((amount / maxTotal) * chartHeight);
            int y = h - padding - barHeight;

            g.setColor(colors[colorIndex % colors.length]);
            g.fillRect(x, y, barWidth, barHeight);
            
            g.setColor(Color.BLACK);
            g.drawString(CURRENCY_SYMBOL + String.format("%.0f", amount), x, y - 5);
            
            String cat = entry.getKey().length() > 6 ? entry.getKey().substring(0, 6) + "..." : entry.getKey();
            g.drawString(cat, x, h - padding + 15);

            x += barWidth + barGap;
            colorIndex++;
            
            if (x > w - padding) break;
        }
    }
    
    private void drawLineChart(Graphics g, int w, int h, ArrayList<Expense> expensesToAnalyze) {
        
        LocalDate today = LocalDate.now();
        
        Map<DayOfWeek, Double> weeklyTotals = expensesToAnalyze.stream()
            .filter(e -> e.getDate().getMonthValue() == today.getMonthValue() && e.getDate().getYear() == today.getYear())
            .collect(Collectors.groupingBy(
                e -> e.getDate().getDayOfWeek(),
                Collectors.summingDouble(Expense::getAmount)
            ));

        double maxTotal = weeklyTotals.values().stream().mapToDouble(Double::doubleValue).max().orElse(0.0);

        if (maxTotal == 0) {
            g.drawString("No Spending this Month", w/2 - 50, h/2);
            return;
        }

        int padding = 50;
        int chartHeight = h - 2 * padding;
        int chartWidth = w - 2 * padding;
        
        g.setColor(Color.BLACK);
        g.drawLine(padding, padding, padding, h - padding); 
        g.drawLine(padding, h - padding, w - padding, h - padding); 
        
        g.drawString(CURRENCY_SYMBOL + String.format("%.0f", maxTotal), 5, padding + 10);
        
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        DayOfWeek[] dayOrder = {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY};
        
        int xStep = chartWidth / (days.length - 1);
        int[] xCoords = new int[days.length];
        int[] yCoords = new int[days.length];
        
        g.setFont(new Font("Arial", Font.PLAIN, 10));
        Graphics2D g2 = (Graphics2D) g;

        for (int i = 0; i < days.length; i++) {
            double amount = weeklyTotals.getOrDefault(dayOrder[i], 0.0);
            
            xCoords[i] = padding + i * xStep;
            int y = h - padding - (int) ((amount / maxTotal) * chartHeight);
            yCoords[i] = y;

            g.drawString(days[i], xCoords[i] - 10, h - padding + 15);
            
            g.setColor(Color.GRAY);
            g.drawString(String.format("%.0f", amount), xCoords[i] - 10, y - 5);
        }
        
        g2.setColor(ACCENT_COLOR);
        g2.setStroke(new BasicStroke(2));

        for (int i = 0; i < days.length; i++) {
            g2.fillOval(xCoords[i] - 4, yCoords[i] - 4, 8, 8);
            
            if (i < days.length - 1) {
                g2.drawLine(xCoords[i], yCoords[i], xCoords[i+1], yCoords[i+1]);
            }
        }
        g2.setStroke(new BasicStroke(1)); 
    }

    
    private Map<String, Double> getMonthlyTotals(ArrayList<Expense> expenses) {
        Map<String, Double> catTotals = new HashMap<>();
        
        LocalDate today = LocalDate.now();
        int currentMonth = today.getMonthValue();
        int currentYear = today.getYear();

        for (Expense e : expenses) {
            if (e.getDate().getMonthValue() == currentMonth && e.getDate().getYear() == currentYear) {
                catTotals.put(e.getCategory(), catTotals.getOrDefault(e.getCategory(), 0.0) + e.getAmount());
            }
        }
        return catTotals;
    }


    public static void saveUserData(Map<String, User> userDB) { 
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("trackmyspend_users.dat"))) {
            oos.writeObject(userDB);
        } catch (IOException e) { 
            JOptionPane.showMessageDialog(null, "Error saving user data: " + e.getMessage(), "File Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace(); 
        }
    }
    
    private static void relaunchAuth() {
        new Thread(() -> {
            try {
                main(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public static void main(String[] args) {
        final Map<String, User> userDatabaseToLoad;
        
        File f = new File("trackmyspend_users.dat");
        
        if (f.exists()) {
            Map<String, User> loadedDB = new HashMap<>();
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
                @SuppressWarnings("unchecked")
                Map<String, User> castedDB = (Map<String, User>) ois.readObject();
                loadedDB = castedDB;
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
            userDatabaseToLoad = loadedDB;
        } else {
            userDatabaseToLoad = new HashMap<>();
        }

        AuthFrame authFrame = new AuthFrame(userDatabaseToLoad);
        authFrame.setVisible(true); 

        while(authFrame.isVisible()) {
            try {
                Thread.sleep(100); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        
        if (authFrame.isAuthenticated()) {
            final User loggedInUser = authFrame.getLoggedInUser();
            
            EventQueue.invokeLater(() -> {
                try {
                    TrackMySpendJava app = new TrackMySpendJava();
                    app.userDatabase = userDatabaseToLoad; 
                    app.currentUser = loggedInUser;
                    
                    app.initializeUserUI(); 

                    app.setVisible(true);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, 
                        "Application failed to start after login due to an internal error: " + e.getMessage(), 
                        "Fatal Runtime Error", 
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                }
            });
        } else {
            System.out.println("Authentication cancelled or failed. Exiting application.");
            System.exit(0);
        }
    }
}