
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;

public class OnlineExamSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginScreen());
    }
}

class User {
    String username;
    String password;
    String profile;

    User(String username, String password, String profile) {
        this.username = username;
        this.password = password;
        this.profile = profile;
    }
}

class LoginScreen extends JFrame {
    static HashMap<String, User> users = new HashMap<>();
    JTextField userField;
    JPasswordField passField;

    LoginScreen() {
        users.put("user", new User("user", "pass", "Default Profile"));
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JLabel userLabel = new JLabel("Username:");
        userField = new JTextField(15);
        JLabel passLabel = new JLabel("Password:");
        passField = new JPasswordField(15);
        JButton loginButton = new JButton("Login");

        loginButton.addActionListener(e -> login());

        JPanel panel = new JPanel();
        panel.add(userLabel);
        panel.add(userField);
        panel.add(passLabel);
        panel.add(passField);
        panel.add(loginButton);

        add(panel);
        setVisible(true);
    }

    void login() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        User user = users.get(username);
        if (user != null && user.password.equals(password)) {
            dispose();
            new Dashboard(user);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials.");
        }
    }
}

class Dashboard extends JFrame {
    User user;

    Dashboard(User user) {
        this.user = user;
        setTitle("Dashboard");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JButton profileBtn = new JButton("Update Profile");
        JButton examBtn = new JButton("Start Exam");
        JButton logoutBtn = new JButton("Logout");

        profileBtn.addActionListener(e -> new ProfileScreen(user));
        examBtn.addActionListener(e -> new ExamScreen(user));
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginScreen();
        });

        JPanel panel = new JPanel();
        panel.add(profileBtn);
        panel.add(examBtn);
        panel.add(logoutBtn);

        add(panel);
        setVisible(true);
    }
}

class ProfileScreen extends JFrame {
    User user;
    JTextField profileField, passField;

    ProfileScreen(User user) {
        this.user = user;
        setTitle("Update Profile");
        setSize(300, 200);
        setLocationRelativeTo(null);

        profileField = new JTextField(user.profile, 15);
        passField = new JTextField(user.password, 15);
        JButton updateBtn = new JButton("Update");

        updateBtn.addActionListener(e -> {
            user.profile = profileField.getText();
            user.password = passField.getText();
            JOptionPane.showMessageDialog(this, "Profile Updated!");
            dispose();
        });

        JPanel panel = new JPanel();
        panel.add(new JLabel("Profile:"));
        panel.add(profileField);
        panel.add(new JLabel("Password:"));
        panel.add(passField);
        panel.add(updateBtn);

        add(panel);
        setVisible(true);
    }
}

class ExamScreen extends JFrame {
    User user;
    int score = 0, current = 0;
    String[] questions = {
        "What is Java?\n1) OS\n2) Programming Language\n3) Browser\n4) Software",
        "Which keyword is used to inherit a class?\n1) this\n2) super\n3) extends\n4) implements",
        "Which company developed Java?\n1) Apple\n2) Oracle\n3) Sun Microsystems\n4) Google",
        "Which method is used to start a thread in Java?\n1) start()\n2) run()\n3) init()\n4) main()",
        "Which of these is not a Java keyword?\n1) class\n2) interface\n3) goto\n4) delete"
    };
    int[] answers = {2, 3, 3, 1, 4};
    ButtonGroup group;
    JRadioButton[] options = new JRadioButton[4];
    JButton nextBtn;
    JLabel questionLabel;
    JLabel timerLabel;
    int timeLeft = 60;
    javax.swing.Timer timer;

    ExamScreen(User user) {
        this.user = user;
        setTitle("Online Exam");
        setSize(500, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        questionLabel = new JLabel();
        group = new ButtonGroup();
        JPanel optionPanel = new JPanel(new GridLayout(4, 1));

        for (int i = 0; i < 4; i++) {
            options[i] = new JRadioButton();
            group.add(options[i]);
            optionPanel.add(options[i]);
        }

        nextBtn = new JButton("Next");
        nextBtn.addActionListener(e -> nextQuestion());

        timerLabel = new JLabel("Time left: 60s");

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(timerLabel);
        bottomPanel.add(nextBtn);

        add(questionLabel, BorderLayout.NORTH);
        add(optionPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        loadQuestion();
        startTimer();
        setVisible(true);
    }

    void loadQuestion() {
        if (current >= questions.length) {
            submitExam();
            return;
        }

        String q = questions[current];
        questionLabel.setText("<html>" + q.replaceAll("\n", "<br>") + "</html>");
        String[] lines = q.split("\n");
        for (int i = 1; i <= 4; i++) {
            options[i - 1].setText(lines[i]);
        }
        group.clearSelection();
    }

    void nextQuestion() {
        if (options[answers[current] - 1].isSelected()) {
            score++;
        }
        current++;
        loadQuestion();
    }

    void startTimer() {
        timer = new javax.swing.Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText("Time left: " + timeLeft + "s");
            if (timeLeft <= 0) submitExam();
        });
        timer.start();
    }

    void submitExam() {
        if (timer != null) timer.stop();
        JOptionPane.showMessageDialog(this, "Exam Over! Your score is: " + score);
        dispose();
        new LoginScreen();
    }
}
