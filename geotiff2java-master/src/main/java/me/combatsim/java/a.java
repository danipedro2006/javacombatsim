package me.combatsim.java;

 

import me.combatsim.java.UnitTeam;
import me.combatsim.java.UnitType;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class a {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new ScenarioEditorFrame("C:/Users/danie/Downloads/geotiff2java-master/geotiff2java-master/src/main/resources/operations.bmp");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}

class ScenarioEditorFrame extends JFrame {

    private final ScenarioMapPanel mapPanel;
    private final UnitFormPanel formPanel;
    private final List<ScenarioUnit> units = new ArrayList<>();
    private File currentFile;

    public ScenarioEditorFrame(String bmpFile) throws Exception {
        setTitle("Scenario Editor");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ---- Map panel ----
        BufferedImage map = ImageIO.read(new File(bmpFile));
        mapPanel = new ScenarioMapPanel(map, units);
        mapPanel.setPreferredSize(new Dimension(map.getWidth(), map.getHeight()));

        // ---- Unit form panel ----
        formPanel = new UnitFormPanel(units, mapPanel);

        add(mapPanel, BorderLayout.CENTER);
        add(formPanel, BorderLayout.EAST);

        // ---- Menu ----
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem loadItem = new JMenuItem("Load CSV");
        loadItem.addActionListener(e -> loadCSV());
        JMenuItem saveItem = new JMenuItem("Save CSV");
        saveItem.addActionListener(e -> saveCSV());

        fileMenu.add(loadItem);
        fileMenu.add(saveItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadCSV() {
        JFileChooser chooser = new JFileChooser();
        int res = chooser.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            currentFile = chooser.getSelectedFile();
            units.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(currentFile))) {
                String header = br.readLine(); // skip header
                String line;
                while ((line = br.readLine()) != null) {
                    String[] cols = line.split(",");
                    ScenarioUnit u = new ScenarioUnit();
                    u.name = cols[0];
                    u.type = UnitType.valueOf(cols[1]);
                    u.team = UnitTeam.valueOf(cols[2]);
                    u.x = Integer.parseInt(cols[3]);
                    u.y = Integer.parseInt(cols[4]);
                    u.visible = Boolean.parseBoolean(cols[5]);
                    u.sensorRange = Double.parseDouble(cols[6]);
                    u.combatPower = Double.parseDouble(cols[7]);
                    u.speed = Double.parseDouble(cols[8]);
                    u.weapon = cols[9];
                    u.radius = Double.parseDouble(cols[10]);
                    u.symbol = cols[11];
                    units.add(u);
                }
                mapPanel.repaint();
                formPanel.refreshUnitList();
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to load CSV: " + ex.getMessage());
            }
        }
    }

    private void saveCSV() {
        if (currentFile == null) {
            JFileChooser chooser = new JFileChooser();
            int res = chooser.showSaveDialog(this);
            if (res != JFileChooser.APPROVE_OPTION) return;
            currentFile = chooser.getSelectedFile();
        }

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(currentFile))) {
            bw.write("NAME,TYPE,TEAM,X,Y,VISIBLE,SENSRANGE,COMBATPOWER,SPEED,WEAPON,RADIUS,SYMBOL");
            bw.newLine();
            for (ScenarioUnit u : units) {
                bw.write(String.join(",",
                        u.name,
                        u.type.name(),
                        u.team.name(),
                        String.valueOf(u.x),
                        String.valueOf(u.y),
                        String.valueOf(u.visible),
                        String.valueOf(u.sensorRange),
                        String.valueOf(u.combatPower),
                        String.valueOf(u.speed),
                        u.weapon,
                        String.valueOf(u.radius),
                        u.symbol
                ));
                bw.newLine();
            }
            JOptionPane.showMessageDialog(this, "CSV saved: " + currentFile.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to save CSV: " + ex.getMessage());
        }
    }
}

// ---------------------------
// Map Panel
// ---------------------------
class ScenarioMapPanel extends JPanel {

    private final BufferedImage map;
    private final List<ScenarioUnit> units;

    public ScenarioMapPanel(BufferedImage map, List<ScenarioUnit> units) {
        this.map = map;
        this.units = units;

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // On click, select unit
                for (ScenarioUnit u : units) {
                    if (Math.abs(u.x - e.getX()) < 10 && Math.abs(u.y - e.getY()) < 10) {
                        u.selected = true;
                    } else {
                        u.selected = false;
                    }
                }
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(map, 0, 0, null);

        for (ScenarioUnit u : units) {
            if (u.selected) g.setColor(Color.GREEN);
            else if (u.team == UnitTeam.FRIENDLY) g.setColor(Color.BLUE);
            else g.setColor(Color.RED);

            g.fillOval(u.x - 5, u.y - 5, 10, 10);
            g.setColor(Color.BLACK);
            g.drawString(u.name, u.x + 5, u.y - 5);
        }
    }
}

// ---------------------------
// Unit Form Panel
// ---------------------------
class UnitFormPanel extends JPanel {

    private final List<ScenarioUnit> units;
    private final ScenarioMapPanel mapPanel;
    private final DefaultListModel<ScenarioUnit> listModel = new DefaultListModel<>();
    private final JList<ScenarioUnit> unitJList = new JList<>(listModel);

    private JTextField nameField = new JTextField(10);
    private JComboBox<UnitType> typeBox = new JComboBox<>(UnitType.values());
    private JComboBox<UnitTeam> teamBox = new JComboBox<>(UnitTeam.values());
    private JSpinner xSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));
    private JSpinner ySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 10000, 1));

    public UnitFormPanel(List<ScenarioUnit> units, ScenarioMapPanel mapPanel) {
        this.units = units;
        this.mapPanel = mapPanel;

        setLayout(new BorderLayout());
        setBorder(new TitledBorder("Units"));

        // ---- List ----
        unitJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        unitJList.addListSelectionListener(e -> loadSelectedUnit());
        add(new JScrollPane(unitJList), BorderLayout.CENTER);

        // ---- Form ----
        JPanel form = new JPanel(new GridLayout(0, 2));
        form.add(new JLabel("Name:")); form.add(nameField);
        form.add(new JLabel("Type:")); form.add(typeBox);
        form.add(new JLabel("Team:")); form.add(teamBox);
        form.add(new JLabel("X:")); form.add(xSpinner);
        form.add(new JLabel("Y:")); form.add(ySpinner);

        JButton addBtn = new JButton("Add");
        JButton updateBtn = new JButton("Update");
        JButton deleteBtn = new JButton("Delete");

        addBtn.addActionListener(e -> addUnit());
        updateBtn.addActionListener(e -> updateUnit());
        deleteBtn.addActionListener(e -> deleteUnit());

        JPanel btnPanel = new JPanel();
        btnPanel.add(addBtn); btnPanel.add(updateBtn); btnPanel.add(deleteBtn);

        JPanel south = new JPanel(new BorderLayout());
        south.add(form, BorderLayout.CENTER);
        south.add(btnPanel, BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);

        refreshUnitList();
    }

    private void loadSelectedUnit() {
        ScenarioUnit u = unitJList.getSelectedValue();
        if (u == null) return;
        nameField.setText(u.name);
        typeBox.setSelectedItem(u.type);
        teamBox.setSelectedItem(u.team);
        xSpinner.setValue(u.x);
        ySpinner.setValue(u.y);
    }

    private void addUnit() {
        ScenarioUnit u = new ScenarioUnit();
        u.name = nameField.getText();
        u.type = (UnitType) typeBox.getSelectedItem();
        u.team = (UnitTeam) teamBox.getSelectedItem();
        u.x = (int) xSpinner.getValue();
        u.y = (int) ySpinner.getValue();
        units.add(u);
        refreshUnitList();
        mapPanel.repaint();
    }

    private void updateUnit() {
        ScenarioUnit u = unitJList.getSelectedValue();
        if (u == null) return;
        u.name = nameField.getText();
        u.type = (UnitType) typeBox.getSelectedItem();
        u.team = (UnitTeam) teamBox.getSelectedItem();
        u.x = (int) xSpinner.getValue();
        u.y = (int) ySpinner.getValue();
        mapPanel.repaint();
        refreshUnitList();
    }

    private void deleteUnit() {
        ScenarioUnit u = unitJList.getSelectedValue();
        if (u == null) return;
        units.remove(u);
        refreshUnitList();
        mapPanel.repaint();
    }

    public void refreshUnitList() {
        listModel.clear();
        for (ScenarioUnit u : units) listModel.addElement(u);
    }
}

// ---------------------------
// Scenario Unit Model
// ---------------------------
class ScenarioUnit {
    public String name;
    public UnitType type;
    public UnitTeam team;
    public int x;
    public int y;
    public boolean visible = true;
    public double sensorRange = 5000;
    public double combatPower = 10;
    public double speed = 10;
    public String weapon = "NONE";
    public double radius = 10;
    public String symbol = "";
    public boolean selected = false;

    @Override
    public String toString() {
        return name + " (" + type + ")";
    }
}
