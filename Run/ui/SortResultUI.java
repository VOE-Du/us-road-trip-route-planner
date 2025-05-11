package Run.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.*;
import java.util.List;

//Visual sorting result UI interface
public class SortResultUI extends JFrame {
    private final Map<String, long[]> data;
    private final List<String> names;

    public SortResultUI(Map<String, long[]> resultMap) {
        this.data = resultMap;
        this.names = new ArrayList<>(resultMap.keySet());

        setTitle("Sorting Algorithm Performance");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // —— 1. main title ——
        JLabel headerLabel = new JLabel("The Final Sorting Result Is Displayed", SwingConstants.CENTER);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        // —— 2. Prepare the form ——
        JTable table = createTable();
        JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(1200, 200));

        // —— 3. table caption ——
        JLabel tableTitle = new JLabel("Data result presentation (Table)", SwingConstants.CENTER);
        tableTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        tableTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        // —— 4. Put the table title and the table into tablePanel ——
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        tablePanel.add(tableScroll, BorderLayout.CENTER);

        // —— 5. Place the main title and tablePanel in the topPanel ——
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerLabel, BorderLayout.NORTH);
        topPanel.add(tablePanel, BorderLayout.CENTER);

        // —— 6. Place the topPanel in the north area of JFrame——
        add(topPanel, BorderLayout.NORTH);

        // 7. The second half: Add titles to each chart and display them side by side
        JPanel chartPanel = new JPanel(new GridLayout(1, 3, 10, 0));

        // Bar Chart packaging
        JPanel barWrapper = new JPanel(new BorderLayout());
        JLabel barTitle = new JLabel("Bar Chart of Sorting Time", SwingConstants.CENTER);
        barTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        barTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        barWrapper.add(barTitle, BorderLayout.NORTH);
        barWrapper.add(new BarChartPanel(), BorderLayout.CENTER);
        chartPanel.add(barWrapper);

        // Line Chart packaging
        JPanel lineWrapper = new JPanel(new BorderLayout());
        JLabel lineTitle = new JLabel("Line Chart of Sorting Time", SwingConstants.CENTER);
        lineTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        lineTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        lineWrapper.add(lineTitle, BorderLayout.NORTH);
        lineWrapper.add(new LineChartPanel(), BorderLayout.CENTER);
        chartPanel.add(lineWrapper);

        // Radar Chart packaging
        JPanel radarWrapper = new JPanel(new BorderLayout());
        JLabel radarTitle = new JLabel("Radar Chart of Sorting Time", SwingConstants.CENTER);
        radarTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        radarTitle.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        radarWrapper.add(radarTitle, BorderLayout.NORTH);
        radarWrapper.add(new RadarChartPanel(), BorderLayout.CENTER);
        chartPanel.add(radarWrapper);

        // Finally, place the entire chartPanel in the middle
        add(chartPanel, BorderLayout.CENTER);
    }

    private JTable createTable() {
        String[] cols = {"Dataset", "Insertion (ns)", "Quick (ns)", "Merge (ns)"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (String name : names) {
            long[] arr = data.get(name);
            model.addRow(new Object[]{name, arr[0], arr[1], arr[2]});
        }
        return new JTable(model);
    }

    /*
     Abbreviate the file name and turn it into the label of the chart
     */
    private String shortenName(String name) {
        String base = name;
        if (base.endsWith(".csv")) {
            base = base.substring(0, base.length() - 4);
        }
        String[] parts = base.split("places_");
        if (parts.length == 2) {
            return parts[0] + " (" + parts[1] + ")";
        }
        return base;
    }

    /*
     Grouped bar chart
     */
    class BarChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth(), height = getHeight();
            int margin = 50;
            int chartW = width - 2 * margin;
            int chartH = height - 2 * margin;

            // 1. Calculate the maximum value
            long max = 0;
            for (long[] arr : data.values()) {
                for (long v : arr) {
                    max = Math.max(max, v);
                }
            }
            double logMax = Math.log10(max);

            // 2. Draw a bar (Y is mapped to the log scale)
            int n = names.size();
            int groupW = chartW / n;
            int barW = (groupW - 20) / 3;
            for (int i = 0; i < n; i++) {
                long[] arr = data.get(names.get(i));
                int x0 = margin + i * groupW;
                for (int j = 0; j < 3; j++) {
                    long v = arr[j];
                    double ratio = Math.log10(v) / logMax;
                    int barH = (int) (ratio * chartH);
                    int x = x0 + 5 + j * (barW + 5);
                    int y = margin + chartH - barH;
                    Color c = j == 0 ? Color.RED : j == 1 ? Color.GREEN : Color.BLUE;
                    g2.setColor(c);
                    g2.fillRect(x, y, barW, barH);
                }
            }

            // 3. Draw the coordinate axes
            g2.setColor(Color.BLACK);
            g2.drawLine(margin, margin, margin, margin + chartH);
            g2.drawLine(margin, margin + chartH, margin + chartW, margin + chartH);

            // 4. Draw logarithmic scales and grid lines
            FontMetrics fm = g2.getFontMetrics();
            for (int exp = 0; exp <= (int) Math.floor(logMax); exp++) {
                double val = Math.pow(10, exp);
                double ratio = Math.log10(val) / logMax;
                int y = margin + chartH - (int) (ratio * chartH);
                g2.setColor(Color.BLACK);
                g2.drawLine(margin - 5, y, margin, y);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(margin, y, margin + chartW, y);
                String label;
                if (exp < 3) label = String.format("%.0f", val);
                else if (exp < 6) label = String.format("%.0fk", val / 1e3);
                else label = String.format("%.0fM", val / 1e6);
                int lw = fm.stringWidth(label);
                g2.setColor(Color.BLACK);
                g2.drawString(label, margin - lw - 8, y + fm.getAscent() / 2);
            }

            // 5. Draw the X-axis label (using a short name)
            for (int i = 0; i < n; i++) {
                String ds = shortenName(names.get(i));
                int x = margin + i * groupW + groupW / 2 - fm.stringWidth(ds) / 2;
                int y = margin + chartH + fm.getHeight();
                g2.drawString(ds, x, y);
            }

            // 6. pantograph
            int lx = width - 80, ly = margin + 20;
            g2.setColor(Color.RED);
            g2.fillRect(lx, ly, 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawString("Insertion", lx + 15, ly + 10);

            g2.setColor(Color.GREEN);
            g2.fillRect(lx, ly + 20, 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawString("Quick", lx + 15, ly + 30);

            g2.setColor(Color.BLUE);
            g2.fillRect(lx, ly + 40, 10, 10);
            g2.setColor(Color.BLACK);
            g2.drawString("Merge", lx + 15, ly + 50);
        }
    }

    /*
    line chart
     */
    class LineChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth(), h = getHeight();
            int m = 50;
            int cw = w - 2 * m, ch = h - 2 * m;

            // 1. Calculate the data range
            long max = 0;
            for (long[] arr : data.values()) {
                for (long v : arr) max = Math.max(max, v);
            }
            double minVal = 100_000;
            double logMin = Math.log10(minVal);
            double logMax = Math.log10(max);
            double logRange = logMax - logMin;

            FontMetrics fm = g2.getFontMetrics();

            // 2. Draw the coordinate axes
            g2.setColor(Color.BLACK);
            g2.drawLine(m, m, m, m + ch);
            g2.drawLine(m, m + ch, m + cw, m + ch);

            // 3. Draw the logarithmic scale and grid on the Y-axis
            for (int exp = (int)Math.ceil(logMin); exp <= (int)Math.floor(logMax); exp++) {
                double val = Math.pow(10, exp);
                double ratio = (exp - logMin) / logRange;
                int y = m + ch - (int)(ratio * ch);
                g2.setColor(Color.BLACK);
                g2.drawLine(m - 5, y, m, y);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawLine(m, y, m + cw, y);
                String label;
                if (exp < 6) label = String.format("%.0fk", val / 1e3);
                else label = String.format("%.0fM", val / 1e6);
                int lw = fm.stringWidth(label);
                g2.setColor(Color.BLACK);
                g2.drawString(label, m - lw - 8, y + fm.getAscent() / 2);
            }

            // 4. line plot
            Color[] cols = {Color.RED, Color.GREEN, Color.BLUE};
            String[] labels = {"Insertion", "Quick", "Merge"};
            int n = names.size();
            for (int j = 0; j < 3; j++) {
                g2.setColor(cols[j]);
                Stroke oldStroke = g2.getStroke();
                g2.setStroke(new BasicStroke(2));
                int prevX = -1, prevY = -1;
                for (int i = 0; i < n; i++) {
                    long val = data.get(names.get(i))[j];
                    double logVal = Math.log10(Math.max(val, (long)minVal));
                    double ratio = (logVal - logMin) / logRange;
                    int x = m + (int)((i / (double)(n - 1)) * cw);
                    int y = m + ch - (int)(ratio * ch);
                    g2.fillOval(x - 3, y - 3, 6, 6);
                    if (prevX != -1) g2.drawLine(prevX, prevY, x, y);
                    prevX = x;
                    prevY = y;
                }
                g2.setStroke(oldStroke);
            }

            // 5. Draw the X-axis scale and label (using short names)
            for (int i = 0; i < n; i++) {
                int x = m + (int)((i / (double)(n - 1)) * cw);
                int y1 = m + ch, y2 = y1 + 5;
                g2.setColor(Color.BLACK);
                g2.drawLine(x, y1, x, y2);
                String ds = shortenName(names.get(i));
                int labelY = y2 + fm.getHeight();
                int labelX = x - fm.stringWidth(ds) / 2;
                g2.drawString(ds, labelX, labelY);
            }

            // 6. pantograph
            int lx = w - 120, ly = m - 10;
            for (int j = 0; j < 3; j++) {
                g2.setColor(cols[j]);
                g2.fillRect(lx, ly + j * 20, 10, 10);
                g2.setColor(Color.BLACK);
                g2.drawString(labels[j], lx + 15, ly + 10 + j * 20);
            }
        }
    }

    /*
    radar map
     */
    class RadarChartPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int w = getWidth(), h = getHeight();
            int cx = w / 2, cy = h / 2;
            int radius = Math.min(w, h) / 3;

            // 1. axis
            int n = names.size();

            // 2. Calculate the logarithmic range
            double logMin = Double.POSITIVE_INFINITY, logMax = Double.NEGATIVE_INFINITY;
            for (long[] arr : data.values()) {
                for (long v : arr) {
                    double lv = Math.log10(v);
                    logMin = Math.min(logMin, lv);
                    logMax = Math.max(logMax, lv);
                }
            }
            int minExp = (int) Math.floor(logMin);
            int maxExp = (int) Math.ceil(logMax);
            double logRange = maxExp - minExp;

            FontMetrics fm = g2.getFontMetrics();

            // 3. gridding
            g2.setColor(Color.LIGHT_GRAY);
            for (int exp = minExp; exp <= maxExp; exp++) {
                double ratio = (exp - minExp) / logRange;
                int rR = (int)(ratio * radius);
                Polygon grid = new Polygon();
                for (int i = 0; i < n; i++) {
                    double angle = Math.toRadians(-90 + 360.0 * i / n);
                    grid.addPoint(
                            cx + (int)(rR * Math.cos(angle)),
                            cy + (int)(rR * Math.sin(angle))
                    );
                }
                g2.draw(grid);
                String label;
                double val = Math.pow(10, exp);
                if (exp < 6) label = String.format("%.0fk", val / 1e3);
                else label = String.format("%.0fM", val / 1e6);
                int lw = fm.stringWidth(label);
                g2.setColor(Color.BLACK);
                g2.drawString(label, cx - lw/2, cy - rR - 5);
                g2.setColor(Color.LIGHT_GRAY);
            }

            // 4. Draw the axes and labels (using short names)
            g2.setColor(Color.BLACK);
            for (int i = 0; i < n; i++) {
                double angle = Math.toRadians(-90 + 360.0 * i / n);
                int x = cx + (int)(radius * Math.cos(angle));
                int y = cy + (int)(radius * Math.sin(angle));
                g2.drawLine(cx, cy, x, y);
                String ds = shortenName(names.get(i));
                int tx = cx + (int)((radius + 20) * Math.cos(angle)) - fm.stringWidth(ds)/2;
                int ty = cy + (int)((radius + 20) * Math.sin(angle)) + fm.getAscent()/2;
                g2.drawString(ds, tx, ty);
            }

            // 5. Filled polygon
            String[] algos = {"Insertion", "Quick", "Merge"};
            Color[] fills = {new Color(255,0,0,64), new Color(0,255,0,64), new Color(0,0,255,64)};
            for (int alg = 0; alg < 3; alg++) {
                Polygon poly = new Polygon();
                for (int i = 0; i < n; i++) {
                    long v = data.get(names.get(i))[alg];
                    double lv = Math.log10(v);
                    double ratio = (lv - minExp) / logRange;
                    int rR = (int)(ratio * radius);
                    double angle = Math.toRadians(-90 + 360.0 * i / n);
                    poly.addPoint(
                            cx + (int)(rR * Math.cos(angle)),
                            cy + (int)(rR * Math.sin(angle))
                    );
                }
                g2.setColor(fills[alg]);
                g2.fill(poly);
                g2.setColor(fills[alg].darker());
                g2.draw(poly);
            }

            // 6. legend
            int lx = w - 120, ly = 20;
            for (int alg = 0; alg < 3; alg++) {
                g2.setColor(fills[alg]);
                g2.fillRect(lx, ly + alg*20, 10, 10);
                g2.setColor(Color.BLACK);
                g2.drawString(algos[alg], lx + 15, ly + 10 + alg*20);
            }
        }
    }

    /*
        show UI
     */
    public static void showUI(Map<String, long[]> resultMap) {
        SwingUtilities.invokeLater(() -> {
            SortResultUI ui = new SortResultUI(resultMap);
            ui.setVisible(true);
        });
    }
}
