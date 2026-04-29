import org.jfree.chart.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;

import java.awt.Color;
import java.util.List;

public class Graph {

    public static void show(List<Integer> p, List<Integer> h, List<Integer> c) {

        // 📊 Dataset
        DefaultCategoryDataset d = new DefaultCategoryDataset();

        for (int i = 0; i < p.size(); i++) {
            d.addValue(p.get(i), "Plants", "" + i);
            d.addValue(h.get(i), "Herbivores", "" + i);
            d.addValue(c.get(i), "Carnivores", "" + i);
        }

        // 📈 Chart
        JFreeChart chart = ChartFactory.createLineChart(
                "Ecosystem Simulation",
                "Time",
                "Population",
                d
        );

        // 🎨 COLOR CUSTOMIZATION
        CategoryPlot plot = chart.getCategoryPlot();
        LineAndShapeRenderer renderer = new LineAndShapeRenderer();

        // 🌱 Plants → Green
        renderer.setSeriesPaint(0, Color.GREEN);

        // 🐑 Herbivores → Blue
        renderer.setSeriesPaint(1, Color.BLUE);

        // 🐺 Carnivores → Red
        renderer.setSeriesPaint(2, Color.RED);

        plot.setRenderer(renderer);

        // 🖼️ Frame
        ChartFrame f = new ChartFrame("Graph", chart);
        f.setSize(900, 600);
        f.setVisible(true);
    }
}