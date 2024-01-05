import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Shape {
    private int x1, y1, x2, y2;
    private Color color;
    private String type;

    public Shape(int x1, int y1, int x2, int y2, Color color, String type) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
        this.color = color;
        this.type = type;
    }

    public boolean contains(int x, int y) {
        switch (type) {
            case "Line":
                return true; // Lines are always considered for erasing
            case "Rectangle":
                return x >= Math.min(x1, x2) && x <= Math.max(x1, x2) && y >= Math.min(y1, y2) && y <= Math.max(y1, y2);
            case "Oval":
                return Math.pow((x - (x1 + x2) / 2.0) / ((x2 - x1) / 2.0), 2) +
                        Math.pow((y - (y1 + y2) / 2.0) / ((y2 - y1) / 2.0), 2) <= 1;
        }
        return false;
    }

    public void draw(Graphics g) {
        g.setColor(color);
        switch (type) {
            case "Line":
                g.drawLine(x1, y1, x2, y2);
                break;
            case "Rectangle":
                g.drawRect(Math.min(x1, x2), Math.min(y1, y2),
                        Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
            case "Oval":
                g.drawOval(Math.min(x1, x2), Math.min(y1, y2),
                        Math.abs(x2 - x1), Math.abs(y2 - y1));
                break;
        }
    }
}

public class SimplePaintApp extends JFrame {

    private Color currentColor = Color.BLACK;
    private int brushSize = 5;
    private boolean eraserMode = false;

    private JPanel drawingPanel;
    private JButton colorChooserBtn;
    private JButton clearBtn;
    private JComboBox<String> shapeChooser;
    private JToggleButton eraserBtn;

    private int startX, startY, endX, endY;
    private List<Shape> shapes = new ArrayList<>();

    public SimplePaintApp() {
        setTitle("Simple Paint App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeUI();
    }

    private void initializeUI() {
        drawingPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                for (Shape shape : shapes) {
                    shape.draw(g);
                }

                if (!eraserMode) {
                    g.setColor(currentColor);

                    String selectedShape = (String) shapeChooser.getSelectedItem();
                    switch (selectedShape) {
                        case "Line":
                            g.drawLine(startX, startY, endX, endY);
                            break;
                        case "Rectangle":
                            g.drawRect(Math.min(startX, endX), Math.min(startY, endY),
                                    Math.abs(endX - startX), Math.abs(endY - startY));
                            break;
                        case "Oval":
                            g.drawOval(Math.min(startX, endX), Math.min(startY, endY),
                                    Math.abs(endX - startX), Math.abs(endY - startY));
                            break;
                    }
                }
            }
        };

        drawingPanel.setBackground(Color.WHITE);
        drawingPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                startX = e.getX();
                startY = e.getY();
                endX = startX;
                endY = startY;

                if (eraserMode) {
                    // Erase shapes and lines that are clicked
                    Iterator<Shape> iterator = shapes.iterator();
                    while (iterator.hasNext()) {
                        Shape shape = iterator.next();
                        if (shape.contains(startX, startY)) {
                            iterator.remove();
                        }
                    }
                    drawingPanel.repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!eraserMode) {
                    shapes.add(new Shape(startX, startY, endX, endY, currentColor, (String) shapeChooser.getSelectedItem()));
                    drawingPanel.repaint();
                }
            }
        });

        drawingPanel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                endX = e.getX();
                endY = e.getY();
                drawingPanel.repaint();
            }
        });

        colorChooserBtn = new JButton("Choose Color");
        colorChooserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                currentColor = JColorChooser.showDialog(null, "Choose Color", currentColor);
            }
        });

        clearBtn = new JButton("Clear");
        clearBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shapes.clear();
                drawingPanel.repaint();
            }
        });

        shapeChooser = new JComboBox<>(new String[]{"Line", "Rectangle", "Oval"});

        eraserBtn = new JToggleButton("Eraser");
        eraserBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eraserMode = eraserBtn.isSelected();
            }
        });

        setLayout(new BorderLayout());
        add(drawingPanel, BorderLayout.CENTER);

        JPanel controlsPanel = new JPanel();
        controlsPanel.add(colorChooserBtn);
        controlsPanel.add(new JLabel("Brush Size:"));
        controlsPanel.add(new JSpinner(new SpinnerNumberModel(5, 1, 50, 1)));
        controlsPanel.add(clearBtn);
        controlsPanel.add(new JLabel("Shape:"));
        controlsPanel.add(shapeChooser);
        controlsPanel.add(eraserBtn);

        add(controlsPanel, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SimplePaintApp().setVisible(true);
            }
        });
    }
}
