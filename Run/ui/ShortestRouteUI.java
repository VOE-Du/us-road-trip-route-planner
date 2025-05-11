package Run.ui;

import DataStructure.Graph;
import DataStructure.PathResult;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

//Visualization result
public class ShortestRouteUI extends JFrame {
    // Unify the color matching and font constants
    private static final Color PRIMARY = new Color(30, 144, 255);
    private static final Color BACKGROUND = new Color(245, 245, 245);
    private static final Font TITLE_FONT = new Font("SansSerif", Font.BOLD, 18);
    private static final Font HEADER_FONT = new Font("SansSerif", Font.BOLD, 14);
    private static final Font TEXT_FONT = new Font("SansSerif", Font.PLAIN, 14);
    private static final Font LABEL_FONT = new Font("SansSerif", Font.PLAIN, 12);
    private static final Font NODE_LABEL_FONT = new Font("SansSerif", Font.BOLD, 8);
    private static final int ARC = 16;

    private final List<String> path;
    private final int totalDistance;
    private final Graph graph;
    private final String algorithmName;
    private final String strategyName;
    private final String startCity;
    private final String endCity;
    private final List<String> attractions;

    // Set Nimbus L&F when the class is loaded
    static {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            // Ignore
        }
    }

    public ShortestRouteUI(PathResult result, Graph graph, String algorithmName, String strategyName, String startCity, String endCity, List<String> attractions) {
        this.path = result.path;
        this.totalDistance = result.totalDistance;
        this.graph = graph;
        this.algorithmName = algorithmName;
        this.strategyName = strategyName;
        this.startCity = startCity;
        this.endCity = endCity;
        this.attractions = attractions;

        setTitle("The Optimal Path Planning Result");
        setSize(800, 1000);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Root panel
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BACKGROUND);
        setContentPane(root);

        // 1. Top: Title + Exit
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("The Optimal Path Planning Result", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY);
        topPanel.add(titleLabel, BorderLayout.CENTER);
        JButton exitBtn = new JButton("Exit");
        exitBtn.setFont(LABEL_FONT);
        exitBtn.setBackground(PRIMARY);
        exitBtn.setForeground(Color.WHITE);
        exitBtn.setFocusPainted(false);
        exitBtn.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        exitBtn.addActionListener(e -> dispose());
        topPanel.add(exitBtn, BorderLayout.EAST);
        root.add(topPanel, BorderLayout.NORTH);

        // 2. Middle Section: Information Card
        JPanel infoContent = new JPanel();
        infoContent.setOpaque(false);
        infoContent.setLayout(new BoxLayout(infoContent, BoxLayout.Y_AXIS));
        infoContent.add(createHeaderLabel("Path Information"));
        infoContent.add(Box.createRigidArea(new Dimension(0, 8)));
        infoContent.add(createInfoLabel("<html><b>Start:</b> " + startCity + "</html>"));
        infoContent.add(createInfoLabel("<html><b>Destination:</b> " + endCity + "</html>"));
        infoContent.add(createInfoLabel("<html><b>Attractions:</b> "
                + (attractions.isEmpty() ? "no" : String.join(" → ", attractions)) + "</html>"));
        infoContent.add(createInfoLabel("<html><b>Shortest Path Algorithm:</b> " + algorithmName + "</html>"));
        infoContent.add(createInfoLabel("<html><b>Route Strategy:</b> " + strategyName + "</html>"));
        infoContent.add(createInfoLabel("<html><b>Total Distance:</b> " + totalDistance + " miles</html>"));
        infoContent.add(Box.createRigidArea(new Dimension(0, 10)));
        JTextArea routeArea = new JTextArea(result.toString());
        routeArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        routeArea.setLineWrap(true);
        routeArea.setWrapStyleWord(true);
        routeArea.setEditable(false);
        JScrollPane scroll = new JScrollPane(routeArea);
        scroll.setBorder(BorderFactory.createTitledBorder(null, "Path Details", TitledBorder.LEFT, TitledBorder.TOP, HEADER_FONT, PRIMARY));
        scroll.setPreferredSize(new Dimension(360, 120));
        scroll.setOpaque(false);
        infoContent.add(scroll);

        CardPanel infoCard = new CardPanel(new BorderLayout());
        infoCard.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        infoCard.add(infoContent, BorderLayout.CENTER);
        root.add(infoCard, BorderLayout.CENTER);

        // 3. Bottom: Visual cards
        MapPanel mapPanel = new MapPanel();
        CardPanel mapCard = new CardPanel(new BorderLayout());
        // Add titled border for Visual map
        mapCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(null, "Visual map", TitledBorder.CENTER, TitledBorder.TOP, HEADER_FONT, PRIMARY),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        mapCard.add(mapPanel, BorderLayout.CENTER);
        mapCard.setPreferredSize(new Dimension(780, 500));
        root.add(mapCard, BorderLayout.SOUTH);

        setVisible(true);
        toFront();
        requestFocus();
        setAlwaysOnTop(true);
        setAlwaysOnTop(false);
    }

    private JLabel createHeaderLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(HEADER_FONT);
        lbl.setForeground(PRIMARY);
        return lbl;
    }

    private JLabel createInfoLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(TEXT_FONT);
        lbl.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        return lbl;
    }

    // Custom rounded corner card panel
    private class CardPanel extends JPanel {
        public CardPanel(LayoutManager layout) {
            super(layout);
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            int w = getWidth(), h = getHeight();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            // Shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(4, 4, w - 8, h - 8, ARC, ARC);
            // White background card
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, w - 8, h - 8, ARC, ARC);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // Visual panel: scaled map + nodes + path
    private class MapPanel extends JPanel {
        private BufferedImage mapImage;
        private final int imgW, imgH;
        // Relative coords (0-1)
        private final Map<String, Point2D.Float> cityRelative = new HashMap<>();
        private final Map<String, Point> labelOffsets = new HashMap<>();
        private final Map<Integer, Point2D.Float> nodeCoords = new HashMap<>();
        private Integer hoverIndex = null;

        public MapPanel() {
            BufferedImage img = null;
            try (InputStream in = getClass().getResourceAsStream("map.png")) {
                if (in != null) {
                    img = ImageIO.read(in);
                } else {
                    System.err.println("map.png not found on classpath");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            mapImage = img;

            mapImage = img;
            imgW = (mapImage != null ? mapImage.getWidth() : 1);
            imgH = (mapImage != null ? mapImage.getHeight() : 1);

            // init coords
            cityRelative.put("Los Angeles CA", new Point2D.Float(0.11f, 0.66f));
            cityRelative.put("San Diego CA", new Point2D.Float(0.14f, 0.705f));
            cityRelative.put("San Jose CA", new Point2D.Float(0.05f, 0.535f));
            cityRelative.put("Phoenix AZ", new Point2D.Float(0.23f, 0.685f));
            cityRelative.put("San Antonio TX", new Point2D.Float(0.45f, 0.84f));
            cityRelative.put("Houston TX", new Point2D.Float(0.52f, 0.83f));
            cityRelative.put("Dallas TX", new Point2D.Float(0.49f, 0.72f));
            cityRelative.put("Austin TX", new Point2D.Float(0.465f, 0.799f));
            cityRelative.put("Fort Worth TX", new Point2D.Float(0.47f, 0.72f));
            cityRelative.put("Chicago IL", new Point2D.Float(0.645f, 0.365f));
            cityRelative.put("Columbus OH", new Point2D.Float(0.72f, 0.44f));
            cityRelative.put("Charlotte NC", new Point2D.Float(0.76f, 0.63f));
            cityRelative.put("Jacksonville FL", new Point2D.Float(0.745f, 0.799f));
            cityRelative.put("Philadelphia PA", new Point2D.Float(0.84f, 0.44f));
            cityRelative.put("New York NY", new Point2D.Float(0.877f, 0.4f));

            labelOffsets.put("Los Angeles CA", new Point(0, -25));
            labelOffsets.put("San Jose CA", new Point(0, -25));
            labelOffsets.put("Chicago IL", new Point(0, -25));
            labelOffsets.put("San Diego CA", new Point(0, 10));
            labelOffsets.put("Houston TX", new Point(0, 10));
            labelOffsets.put("Phoenix AZ", new Point(0, 15));
            labelOffsets.put("Columbus OH", new Point(0, 10));
            labelOffsets.put("Philadelphia PA", new Point(55, 5));
            labelOffsets.put("New York NY", new Point(58, 2));
            labelOffsets.put("Charlotte NC", new Point(46, 2));
            labelOffsets.put("Dallas TX", new Point(34, -10));
            labelOffsets.put("Fort Worth TX", new Point(-48, -10));
            labelOffsets.put("San Antonio TX", new Point(-30, 20));

            setOpaque(false);
            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseMoved(MouseEvent e) {
                    Integer newHover = null;
                    for (var entry : nodeCoords.entrySet()) {
                        Point2D.Float pt = entry.getValue();
                        // transform pt to panel coords for hit test
                        double px = pt.x * (getWidth()/ (double) imgW);
                        double py = pt.y * (getHeight()/ (double) imgH);
                        if (Point.distance(px, py, e.getX(), e.getY()) <= 12) {
                            newHover = entry.getKey();
                            break;
                        }
                    }
                    if (!Objects.equals(hoverIndex, newHover)) {
                        hoverIndex = newHover;
                        repaint();
                    }
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int pw = getWidth(), ph = getHeight();
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            // 1. calc scale & translate
            double sx = pw / (double) imgW;
            double sy = ph / (double) imgH;
            double scale = Math.min(sx, sy);
            double tx = (pw - imgW * scale) / 2;
            double ty = (ph - imgH * scale) / 2;
            AffineTransform at = new AffineTransform();
            at.translate(tx, ty);
            at.scale(scale, scale);

            // 2. draw map
            if (mapImage != null) {
                g2.drawImage(mapImage, at, null);
            }
            // 3. apply transform for overlay
            g2.transform(at);

            // 4. draw all cities
            g2.setStroke(new BasicStroke(1f / (float) scale));
            for (var e : cityRelative.entrySet()) {
                String city = e.getKey();
                Point2D.Float rel = e.getValue();
                float x = rel.x * imgW;
                float y = rel.y * imgH;
                // small circle
                g2.setColor(new Color(200,200,200,180));
                g2.fillOval((int)(x-5),(int)(y-5),10,10);
                g2.setColor(Color.GRAY);
                g2.drawOval((int)(x-5),(int)(y-5),10,10);
                // label
                Point off = labelOffsets.getOrDefault(city, new Point(0,-15));
                g2.setFont(LABEL_FONT.deriveFont((float)(LABEL_FONT.getSize()) / (float)scale));
                FontMetrics fm = g2.getFontMetrics();
                int tw = fm.stringWidth(city), th = fm.getHeight();
                float tx0 = x + off.x - tw/2f;
                float ty0 = y + off.y + th/2f;
                g2.setColor(Color.BLACK);
                g2.drawString(city, tx0, ty0);
            }

            // 5. no path -> done
            if (path==null||path.isEmpty()) { g2.dispose(); return; }

            // 6. compute nodeCoords in image space
            nodeCoords.clear();
            for (int i=0;i<path.size();i++){
                String city = path.get(i);
                var rel = cityRelative.get(city);
                if (rel!=null) nodeCoords.put(i,new Point2D.Float(rel.x*imgW,rel.y*imgH));
            }

            // 7. draw path segments
            for (int i=1;i<path.size();i++){
                Point2D.Float p1 = nodeCoords.get(i-1);
                Point2D.Float p2 = nodeCoords.get(i);
                if(p1==null||p2==null) continue;
                // main line
                g2.setStroke(new BasicStroke(3f/(float)scale));
                g2.setColor(Color.RED);
                g2.drawLine((int)p1.x,(int)p1.y,(int)p2.x,(int)p2.y);
                // arrow
                double theta = Math.atan2(p2.y-p1.y,p2.x-p1.x);
                double phi = Math.toRadians(40);
                int barb = 8;
                float mx=(p1.x+p2.x)/2f, my=(p1.y+p2.y)/2f;
                g2.setStroke(new BasicStroke(1f/(float)scale));
                int x1=(int)(mx-barb*Math.cos(theta+phi));
                int y1=(int)(my-barb*Math.sin(theta+phi));
                int x2=(int)(mx-barb*Math.cos(theta-phi));
                int y2=(int)(my-barb*Math.sin(theta-phi));
                g2.drawLine((int)mx,(int)my,x1,y1);
                g2.drawLine((int)mx,(int)my,x2,y2);
                // distance label
                String dStr = graph.getDistance(path.get(i-1),path.get(i))+" miles";
                g2.setFont(LABEL_FONT.deriveFont((float)LABEL_FONT.getSize()/ (float)scale));
                g2.setColor(Color.BLUE);
                FontMetrics fm2 = g2.getFontMetrics();
                int tw2=fm2.stringWidth(dStr);
                g2.drawString(dStr,mx-tw2/2f,my-5);
            }

            // 8. draw nodes
            for (int i=0;i<path.size();i++){
                Point2D.Float pt = nodeCoords.get(i);
                if(pt==null) continue;
                boolean isStart=(i==0), isEnd=(i==path.size()-1);
                boolean hover = Objects.equals(i,hoverIndex);
                int r = (isStart||isEnd)?12:8;
                // fill
                g2.setColor(isStart||isEnd? Color.ORANGE: Color.WHITE);
                g2.fillOval((int)(pt.x-r),(int)(pt.y-r),r*2,r*2);
                // border
                g2.setColor(PRIMARY);
                g2.setStroke(new BasicStroke(2f/(float)scale));
                g2.drawOval((int)(pt.x-r),(int)(pt.y-r),r*2,r*2);
                // label
                if(isStart||isEnd){
                    String lbl = isStart?"Start":"End";
                    g2.setFont(NODE_LABEL_FONT.deriveFont((float)NODE_LABEL_FONT.getSize()/(float)scale));
                    FontMetrics fm=g2.getFontMetrics();
                    int tw=fm.stringWidth(lbl), th=fm.getAscent();
                    g2.setColor(Color.BLACK);
                    g2.drawString(lbl,pt.x-tw/2f,pt.y+th/2f-1);
                }
            }
            drawLegend(g2);
            g2.dispose();
        }
    }

    /* Legend */
    private void drawLegend(Graphics2D g2) {
        int lx = 20, ly = 20, lw = 140, lh = 90;

        g2.setColor(new Color(255, 255, 255, 200));
        g2.fillRoundRect(lx, ly, lw, lh, 8, 8);
        g2.setColor(Color.GRAY);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(lx, ly, lw, lh, 8, 8);

        g2.setFont(LABEL_FONT);
        g2.setColor(Color.BLACK);
        int textX = lx + 10;
        int textY = ly + 20;
        int dy = 18;

        g2.setStroke(new BasicStroke(3));
        g2.setColor(Color.RED);
        g2.drawLine(textX, textY - 6, textX + 20, textY - 6);
        g2.setColor(Color.BLACK);
        g2.drawString("Route", textX + 30, textY);

        g2.setColor(Color.BLUE);
        g2.drawString("350 miles", textX, textY += dy);
        g2.setColor(Color.BLACK);
        g2.drawString("Distance", textX + 70, textY);

        int cx = textX + 8, cy = textY + dy - 4, r = 6;
        g2.setColor(Color.ORANGE);
        g2.fillOval(cx - r, cy - r, 2*r, 2*r);
        g2.setColor(PRIMARY);
        g2.setStroke(new BasicStroke(1));
        g2.drawOval(cx - r, cy - r, 2*r, 2*r);
        g2.setColor(Color.BLACK);
        g2.drawString("Start/End", textX + 30, textY + dy);

        cy += dy;
        g2.setColor(Color.WHITE);
        g2.fillOval(cx - r, cy - r, 2*r, 2*r);
        g2.setColor(PRIMARY);
        g2.drawOval(cx - r, cy - r, 2*r, 2*r);
        g2.setColor(Color.BLACK);
        g2.drawString("Waypoint", textX + 30, textY + 2*dy);
    }
}
