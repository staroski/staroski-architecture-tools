package br.com.staroski.tools.analysis.ui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.entity.EntityCollection;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

import br.com.staroski.tools.analysis.generators.ScatterPlotGenerator;
import br.com.staroski.utils.Arguments;

/**
 * A graphic user interface that allows to visualize {@link ProjectImpl} metrics generated by the {@link ScatterPlotGenerator}.
 *
 * @author Staroski, Ricardo Artur
 */
@SuppressWarnings("serial")
public final class DispersionChartViewer extends ApplicationFrame {

    private static class ComponentTableModel extends AbstractTableModel {

        private static final String[] COLUMNS = { "Row", "Name", "D", "I", "A", "Na", "Nc", "Ce", "Ca",
                "DAG" };

        private static final Class<?>[] COLUMN_TYPES = { Integer.class, String.class, Double.class,
                Double.class, Double.class, Integer.class, Integer.class, Integer.class, Integer.class, Boolean.class };

        private final List<PlotData> datalist = new ArrayList<>();

        private JTable table;

        private ComponentTableModel() {}

        public void bind(JTable table) {
            this.table = table;
        }

        @Override
        public Class<?> getColumnClass(int col) {
            return COLUMN_TYPES[col];
        }

        @Override
        public int getColumnCount() {
            return COLUMNS.length;
        }

        @Override
        public String getColumnName(int col) {
            return COLUMNS[col];
        }

        public PlotData getObjectAt(int row) {
            return datalist.get(row);
        }

        @Override
        public int getRowCount() {
            return datalist.size();
        }

        @Override
        public Object getValueAt(int row, int col) {
            if (!datalist.isEmpty() && row < datalist.size()) {
                PlotData data = getObjectAt(row);
                switch (col) {
                    case 0:
                        return table == null ? 0 : table.convertRowIndexToView(row) + 1;
                    case 1:
                        return data.name;
                    case 2:
                        return data.distance;
                    case 3:
                        return data.instability;
                    case 4:
                        return data.abstractness;
                    case 5:
                        return data.abstracts;
                    case 6:
                        return data.concretes;
                    case 7:
                        return data.outputs;
                    case 8:
                        return data.inputs;
                    case 9:
                        return data.acyclic;
                }
            }
            return null;
        }

        public void update(List<PlotData> newData) {
            datalist.clear();
            datalist.addAll(newData);
            fireTableDataChanged();
        }
    }

    private static final class ForcedListSelectionModel extends DefaultListSelectionModel {

        @Override
        public void setSelectionInterval(int index0, int index1) {
            super.setSelectionInterval(index0, index0);
        }
    }

    private static final class HeaderRenderer extends DefaultTableCellRenderer {

        private final int alignment;
        private final TableCellRenderer defaultHeaderRenderer;

        private HeaderRenderer(TableCellRenderer defaultHeaderRenderer, int alignment) {
            this.defaultHeaderRenderer = defaultHeaderRenderer;
            this.alignment = alignment;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            Component component = defaultHeaderRenderer.getTableCellRendererComponent(table, value, isSelected,
                    hasFocus, row, column);
            if (component instanceof JLabel) {
                JLabel label = (JLabel) component;
                label.setHorizontalAlignment(alignment);
            }
            return component;
        }
    }

    private static final class NumberCellRenderer extends DefaultTableCellRenderer {

        private NumberCellRenderer() {
            setHorizontalAlignment(SwingConstants.RIGHT);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                int row, int column) {
            if (value instanceof Double || value instanceof Float || value instanceof BigDecimal) {
                value = DECIMAL_FORMAT.format(value);
            }
            return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
        }
    }

    private static class PlotData {

        final String name;
        final Double distance;
        final Double instability;
        final Double abstractness;
        final Integer outputs;
        final Integer inputs;
        final Integer concretes;
        final Integer abstracts;
        final Boolean acyclic;

        private PlotData(String name, double d, double i, double a, int ce, int ca, int nc, int na, boolean dag) {
            this.name = name;
            this.distance = d;
            this.instability = i;
            this.abstractness = a;
            this.outputs = ce;
            this.inputs = ca;
            this.concretes = nc;
            this.abstracts = na;
            this.acyclic = dag;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            PlotData that = (PlotData) obj;
            return Double.compare(this.instability, that.instability) == 0
                    && Double.compare(this.abstractness, that.abstractness) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(instability, abstractness);
        }

        public boolean inCoordinate(double x, double y) {
            return Double.compare(this.instability, x) == 0 && Double.compare(this.abstractness, y) == 0;
        }
    }

    private static class PlotDataRenderer extends XYShapeRenderer {

        private List<PlotData> selectedPlotData;

        public PlotDataRenderer(List<PlotData> selectedPlotData) {
            this.selectedPlotData = selectedPlotData;
        }

        @Override
        public void drawItem(Graphics2D g2, XYItemRendererState state, Rectangle2D dataArea, PlotRenderingInfo info,
                XYPlot plot, ValueAxis domainAxis, ValueAxis rangeAxis, XYDataset dataset, int series, int item,
                CrosshairState crosshairState, int pass) {

            // Store points in a list to draw them later
            List<PlotDataToDraw> allItemsToDraw = new ArrayList<>();

            // Iterate over all items in the dataset and store the points
            int seriesCount = dataset.getSeriesCount();
            for (int s = 0; s < seriesCount; s++) {
                int itemCount = dataset.getItemCount(s);
                for (int i = 0; i < itemCount; i++) {
                    double x = dataset.getXValue(s, i);
                    double y = dataset.getYValue(s, i);

                    // choose image for plot data
                    BufferedImage imageToDraw = Images.BALL_BLUE_12; // deselected are blue

                    if (selectedPlotData != null && !selectedPlotData.isEmpty()) {
                        for (PlotData selected : selectedPlotData) {
                            if (selected.inCoordinate(x, y)) {
                                imageToDraw = Images.BALL_RED_16; // selected are red
                            }
                        }
                    }

                    double transX = domainAxis.valueToJava2D(x, dataArea, plot.getDomainAxisEdge());
                    double transY = rangeAxis.valueToJava2D(y, dataArea, plot.getRangeAxisEdge());
                    allItemsToDraw.add(new PlotDataToDraw(transX, transY, imageToDraw, s, i));
                }
            }

            // Draw all points except the selected one
            for (PlotDataToDraw itemToDraw : allItemsToDraw) {
                drawImage(g2, state, dataset, itemToDraw);
            }
        }

        private void drawImage(Graphics2D g2, XYItemRendererState state, XYDataset dataset, PlotDataToDraw item) {
            int imageWidth = item.image.getWidth();
            int imageHeight = item.image.getHeight();
            g2.drawImage(item.image, (int) (item.transX - imageWidth / 2), (int) (item.transY - imageHeight / 2), null);

            EntityCollection entities = state.getEntityCollection();
            if (entities != null) {
                Rectangle hotspot = new Rectangle((int) item.transX - imageWidth / 2,
                        (int) item.transY - imageHeight / 2, imageWidth, imageHeight);
                addEntity(entities, hotspot, dataset, item.seriesIndex, item.itemIndex, item.transX, item.transY);
            }
        }
    }

    private static final class PlotDataToDraw {
        double transX;
        double transY;
        BufferedImage image;
        int seriesIndex;
        int itemIndex;

        PlotDataToDraw(double transX, double transY, BufferedImage image, int seriesIndex, int itemIndex) {
            this.transX = transX;
            this.transY = transY;
            this.image = image;
            this.seriesIndex = seriesIndex;
            this.itemIndex = itemIndex;
        }
    }

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    public static void main(String... args) {
        try {
            // validate main args
            Arguments arguments = new Arguments(args, "-i");

            File csv = new File(arguments.getArgument("-i"));

            // read metrics data
            final List<PlotData> allData = readCsv(csv);

            // apply look and feel
            applyLookAndFeel();

            // show gui
            SwingUtilities.invokeLater(() -> new DispersionChartViewer(allData).setVisible(true));
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            showUsage();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private static void applyLookAndFeel() throws UnsupportedLookAndFeelException {
        UIManager.setLookAndFeel(new MetalLookAndFeel());
        JFrame.setDefaultLookAndFeelDecorated(true);
        JDialog.setDefaultLookAndFeelDecorated(true);
    }

    private static void copyColumnContentToClipboard(JTable table) {
        int column = table.getSelectedColumn();

        if (column != -1) {
            StringBuilder csvBuilder = new StringBuilder();
            TableModel model = table.getModel();

            csvBuilder.append(model.getColumnName(column)).append("\n");

            for (int i = 0; i < model.getRowCount(); i++) {
                csvBuilder.append(model.getValueAt(i, column)).append("\n");
            }

            String csvText = csvBuilder.toString();
            StringSelection selection = new StringSelection(csvText);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
        }
    }

    private static void copyTableContentToClipboard(JTable table) {
        StringBuilder csvBuilder = new StringBuilder();
        TableModel model = table.getModel();

        int columnCount = model.getColumnCount();
        int lastColumn = columnCount - 1;
        for (int i = 1; i < columnCount; i++) {
            csvBuilder.append(model.getColumnName(i));
            if (i < lastColumn) {
                csvBuilder.append(",");
            }
        }
        csvBuilder.append("\n");

        for (int i = 1; i < model.getRowCount(); i++) {
            for (int j = 0; j < columnCount; j++) {
                Object value = model.getValueAt(i, j);
                if (j == lastColumn) {
                    value = ((Boolean) value) ? 1 : 0;
                }
                csvBuilder.append(value);
                if (j < lastColumn) {
                    csvBuilder.append(",");
                }
            }
            csvBuilder.append("\n");
        }

        String csvText = csvBuilder.toString();
        StringSelection selection = new StringSelection(csvText);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
    }

    private static List<PlotData> readCsv(File csv) throws IOException {
        List<PlotData> allData = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line = br.readLine(); // Name,D,I,A,Ce,Ca,Nc,Na,DAG
            while ((line = br.readLine()) != null) { // data
                String[] values = line.split(",");
                int col = 0;
                String name = values[col++].trim();
                double d = Double.parseDouble(values[col++].trim());
                double i = Double.parseDouble(values[col++].trim());
                double a = Double.parseDouble(values[col++].trim());
                int ce = Integer.parseInt(values[col++].trim());
                int ca = Integer.parseInt(values[col++].trim());
                int nc = Integer.parseInt(values[col++].trim());
                int na = Integer.parseInt(values[col++].trim());
                boolean dag = Integer.parseInt(values[col++].trim()) > 0;
                PlotData data = new PlotData(name, d, i, a, ce, ca, nc, na, dag);
                allData.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return allData;
    }

    private static void showUsage() {
        System.out.println("Usage:");
        System.out.println("    java " + DispersionChartViewer.class.getName() + " -i <input>");
        System.out.println("Where:");
        System.out.println("    <input>: Path of CVS containing system metrics.");
    }

    private JFreeChart chart;

    private XYPlot plot;

    private List<PlotData> allData;

    private int[] selectedIndexes = {};

    private JTable tableComponents;

    private DispersionChartViewer(List<PlotData> allData) {
        super("Staroski Architecture Tools");

        this.allData = allData;

        chart = createChart();

        JSplitPane splitPane = createSplitPane();

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        mainPanel.add(splitPane, BorderLayout.CENTER);

        setContentPane(mainPanel);
        setIconImages(createIcons());
        setMinimumSize(new Dimension(640, 480));
        setSize(new Dimension(1366, 768));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        showAll();
    }

    private JFreeChart createChart() {

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Components");
        dataset.addSeries(series);

        JFreeChart chart = ChartFactory.createScatterPlot("Component Dispersion Chart", "Instability", "Abstractness",
                dataset, PlotOrientation.VERTICAL, false, true, false);

        plot = (XYPlot) chart.getPlot();
        plot.setDomainPannable(false);
        plot.setRangePannable(false);
        plot.setForegroundAlpha(0.75f);

        plot.setBackgroundPaint(Color.WHITE);

        // Setup grid lines
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.LIGHT_GRAY);
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Set the format and limits for X and Y axes
        double increment = 0.05;
        double minX = -0.04;
        double maxX = 1.04;
        double minY = -0.04;
        double maxY = 1.04;

        NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
        xAxis.setNumberFormatOverride(DECIMAL_FORMAT);
        xAxis.setRange(minX, maxX);
        xAxis.setTickUnit(new NumberTickUnit(increment));
        xAxis.setAxisLineVisible(true);
        xAxis.setAxisLinePaint(Color.BLACK);

        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();
        yAxis.setNumberFormatOverride(DECIMAL_FORMAT);
        yAxis.setRange(minY, maxY);
        yAxis.setTickUnit(new NumberTickUnit(increment));
        yAxis.setAxisLineVisible(true);
        yAxis.setAxisLinePaint(Color.BLACK);

        Stroke line = new BasicStroke(1.0f);
        Stroke dashes = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f,
                new float[] { 5.0f, 5.0f }, 0.0f);

        XYLineAnnotation xAxisTop = new XYLineAnnotation(0, 1, 1, 1, dashes, Color.BLACK);
        XYLineAnnotation xAxisBottom = new XYLineAnnotation(0, 0, 1, 0, line, Color.BLACK);

        XYLineAnnotation yAxisLeft = new XYLineAnnotation(0, 0, 0, 1, line, Color.BLACK);
        XYLineAnnotation yAxisRight = new XYLineAnnotation(1, 0, 1, 1, dashes, Color.BLACK);

        Font textFont = new Font("SansSerif", Font.PLAIN, 16);
        double textAngle = Math.toRadians(45.0);

        XYLineAnnotation mainSequence = new XYLineAnnotation(0, 1, 1, 0, line, Color.GREEN);
        XYTextAnnotation mainSequenceText = new XYTextAnnotation("The Main Sequence", 0.5, 0.5);
        mainSequenceText.setRotationAngle(textAngle);
        mainSequenceText.setFont(textFont);

        double x = -0.5;
        double y = -0.5;
        double w = 1.0;
        double h = 1.0;
        double start = 0.0;
        double extent = -90;
        XYShapeAnnotation zoneOfPain = new XYShapeAnnotation(new Arc2D.Double(x, y, w, h, start, extent, Arc2D.OPEN),
                line, Color.RED);
        XYTextAnnotation zoneOfPainText = new XYTextAnnotation("Zone of Pain", 0.2, 0.2);
        zoneOfPainText.setRotationAngle(textAngle);
        zoneOfPainText.setFont(textFont);

        x = 0.5;
        y = 0.5;
        w = 1.0;
        h = 1.0;
        start = 180.0;
        extent = -90;
        XYShapeAnnotation zoneOfUselessnes = new XYShapeAnnotation(
                new Arc2D.Double(x, y, w, h, start, extent, Arc2D.OPEN), line, Color.ORANGE);
        XYTextAnnotation zoneOfUselessnesText = new XYTextAnnotation("Zone of Uselessness", 0.8, 0.8);
        zoneOfUselessnesText.setRotationAngle(textAngle);
        zoneOfUselessnesText.setFont(textFont);

        plot.addAnnotation(xAxisTop);
        plot.addAnnotation(xAxisBottom);

        plot.addAnnotation(yAxisLeft);
        plot.addAnnotation(yAxisRight);

        plot.addAnnotation(mainSequence);
        plot.addAnnotation(mainSequenceText);

        plot.addAnnotation(zoneOfPain);
        plot.addAnnotation(zoneOfPainText);

        plot.addAnnotation(zoneOfUselessnes);
        plot.addAnnotation(zoneOfUselessnesText);

        // Set the renderer for the points
        plot.setRenderer(0, createDotRenderer());

        return chart;
    }

    private XYSeriesCollection createDataset(boolean all, boolean acyclic, boolean cyclic) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series = new XYSeries("Components");

        List<PlotData> visibleData = new ArrayList<>();

        for (PlotData data : allData) {
            if (all) {
                series.add(data.instability, data.abstractness);
                visibleData.add(data);
            } else if (acyclic && data.acyclic) {
                series.add(data.instability, data.abstractness);
                visibleData.add(data);
            } else if (cyclic && !data.acyclic) {
                series.add(data.instability, data.abstractness);
                visibleData.add(data);
            }
        }

        dataset.addSeries(series);

        if (tableComponents != null) {
            ComponentTableModel tableModel = (ComponentTableModel) tableComponents.getModel();
            tableModel.update(visibleData);
            tableComponents.clearSelection();
            plot.setRenderer(0, createDotRenderer(null)); // Update the renderer for the series of points
        }

        return dataset;
    }

    private XYShapeRenderer createDotRenderer() {
        return createDotRenderer(null);
    }

    private XYShapeRenderer createDotRenderer(final List<PlotData> selectedPlotData) {
        return new PlotDataRenderer(selectedPlotData);
    }

    private List<BufferedImage> createIcons() {
        List<BufferedImage> icons = Arrays.asList(//
                Images.TOOLS_16, //
                Images.TOOLS_32, //
                Images.TOOLS_64, //
                Images.TOOLS_128, //
                Images.TOOLS_256);
        return icons;
    }

    private JSplitPane createSplitPane() {
        JPanel leftPanel = createSplitPaneLeft();
        JPanel rightPanel = createSplitPaneRight();
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        SwingUtilities.invokeLater(() -> splitPane.setDividerLocation(0.45));
        return splitPane;
    }

    private JPanel createSplitPaneLeft() {
        JPanel topPanel = createSplitPaneLeftTop();

        tableComponents = createTable();
        JScrollPane scrollPane = new JScrollPane(tableComponents, //
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, //
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Components"));

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        leftPanel.add(topPanel, BorderLayout.NORTH);
        leftPanel.add(scrollPane, BorderLayout.CENTER);
        return leftPanel;
    }

    private JPanel createSplitPaneLeftTop() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.LEADING));
        panel.setBorder(BorderFactory.createTitledBorder("Components options"));

        JRadioButton radioButton1 = new JRadioButton("All", true);
        JRadioButton radioButton2 = new JRadioButton("Acyclic");
        JRadioButton radioButton3 = new JRadioButton("Cyclic");

        radioButton1.addActionListener(e -> showAll());
        radioButton2.addActionListener(e -> showAcyclic(true));
        radioButton3.addActionListener(e -> showAcyclic(false));

        ButtonGroup group = new ButtonGroup();
        group.add(radioButton1);
        group.add(radioButton2);
        group.add(radioButton3);

        panel.add(radioButton1);
        panel.add(radioButton2);
        panel.add(radioButton3);

        return panel;
    }

    private JPanel createSplitPaneRight() {
        JPanel topPanel = createSplitPaneRightTop();

        ChartPanel chartPanel = new ChartPanel(chart);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JScrollPane scrollPane = new JScrollPane(chartPanel, //
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, //
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Chart"));

        rightPanel.add(topPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        return rightPanel;
    }

    private JPanel createSplitPaneRightTop() {
        JCheckBox backgroundCheckbox = new JCheckBox("Colorful background");
        backgroundCheckbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JCheckBox checkBox = (JCheckBox) e.getSource();
                Image background = checkBox.isSelected() ? Images.SCATTERPLOT_BACKGROUND_1920 : null;
                plot.setBackgroundImage(background);
            }
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panel.setBorder(BorderFactory.createTitledBorder("Chart options"));

        panel.add(backgroundCheckbox);
        return panel;
    }

    private JTable createTable() {
        ComponentTableModel tableModel = new ComponentTableModel();
        final JTable table = new JTable(tableModel);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

        TableRowSorter<TableModel> sorter = new TableRowSorter<>(tableModel);
        sorter.setSortable(0, false);
        table.setRowSorter(sorter);

        tableModel.bind(table);

        TableCellRenderer defaultHeaderRenderer = table.getTableHeader().getDefaultRenderer();

        TableCellRenderer headersRenderer = new HeaderRenderer(defaultHeaderRenderer, SwingConstants.CENTER);
        TableCellRenderer column1Renderer = new HeaderRenderer(defaultHeaderRenderer, SwingConstants.RIGHT);
        TableCellRenderer numericRenderer = new NumberCellRenderer();

        table.getTableHeader().setDefaultRenderer(headersRenderer);

        TableColumnModel columnModel = table.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(33);
        columnModel.getColumn(1).setPreferredWidth(210);
        columnModel.getColumn(2).setPreferredWidth(40);
        columnModel.getColumn(3).setPreferredWidth(40);
        columnModel.getColumn(4).setPreferredWidth(40);
        columnModel.getColumn(5).setPreferredWidth(40);
        columnModel.getColumn(6).setPreferredWidth(40);
        columnModel.getColumn(7).setPreferredWidth(40);
        columnModel.getColumn(8).setPreferredWidth(40);
        columnModel.getColumn(9).setPreferredWidth(40);

        columnModel.getColumn(0).setCellRenderer(column1Renderer);
        columnModel.getColumn(2).setCellRenderer(numericRenderer);
        columnModel.getColumn(3).setCellRenderer(numericRenderer);
        columnModel.getColumn(4).setCellRenderer(numericRenderer);

        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        ListSelectionModel selectionModel = new ForcedListSelectionModel();
        table.setSelectionModel(selectionModel);

        createTableHeaderPopupMenu(table);
        createTableCellPopupMenu(table);

        // Add a listener to highlight the selected point
        selectionModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ComponentTableModel tableModel = (ComponentTableModel) table.getModel();

                List<PlotData> newPlotData = new ArrayList<>();

                int[] selectedRows = table.getSelectedRows();
                selectedIndexes = new int[selectedRows.length];

                for (int i = 0; i < selectedRows.length; i++) {
                    int selection = selectedRows[i];
                    int modelIndex = table.convertRowIndexToModel(selection == -1 ? e.getFirstIndex() : selection);
                    selectedIndexes[i] = modelIndex;

                    PlotData plotData = modelIndex < 0 ? null : tableModel.getObjectAt(modelIndex);
                    newPlotData.add(plotData);
                }
                plot.setRenderer(0, createDotRenderer(newPlotData)); // Update the renderer for the series of points
            }
        });

        // Add a KeyListener to detect the ESC key
        table.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    table.clearSelection(); // Clear the selection on ESC press
                    plot.setRenderer(0, createDotRenderer(null)); // Update the renderer for the series of points
                }
            }
        });
        return table;
    }

    private void createTableCellPopupMenu(JTable table) {
        JMenuItem copyCellItem = new JMenuItem("Copy table content as CSV to clipboard", new ImageIcon(Images.ACTION_COPY));
        copyCellItem.addActionListener(e -> copyTableContentToClipboard(table));

        JPopupMenu cellPopupMenu = new JPopupMenu();
        cellPopupMenu.add(copyCellItem);

        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showCellPopup(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showCellPopup(e);
                }
            }

            private void showCellPopup(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int column = table.columnAtPoint(e.getPoint());
                if (column > 0) {
                    if (!table.isRowSelected(row))
                        table.changeSelection(row, column, false, false);

                    cellPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

    }

    private void createTableHeaderPopupMenu(JTable table) {

        JMenuItem copyColumnItem = new JMenuItem("Copy column to clipboard", new ImageIcon(Images.ACTION_COPY));
        copyColumnItem.addActionListener(e -> copyColumnContentToClipboard(table));

        JPopupMenu headerPopupMenu = new JPopupMenu();
        headerPopupMenu.add(copyColumnItem);

        table.getTableHeader().addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showHeaderPopup(e);
                }
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showHeaderPopup(e);
                }
            }

            private void showHeaderPopup(MouseEvent e) {
                JTableHeader header = (JTableHeader) e.getSource();
                TableColumnModel columnModel = header.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int column = header.getTable().convertColumnIndexToModel(viewColumn);
                if (column > 0) {
                    header.getTable().setColumnSelectionInterval(column, column);
                    headerPopupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

    }

    private void showAcyclic(boolean acyclic) {
        plot.setDataset(createDataset(false, acyclic, !acyclic));
    }

    private void showAll() {
        plot.setDataset(createDataset(true, false, false));
    }
}
