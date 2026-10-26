package org.apache.oozie.util.graph.type;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.apache.oozie.util.graph.GraphvizRenderer;

import java.awt.image.BufferedImage;
import java.util.concurrent.Callable;

public class PngRenderer implements Callable<BufferedImage> {
    private final GraphvizRenderer graphvizRenderer;

    public PngRenderer(GraphvizRenderer graphvizRenderer) {
        this.graphvizRenderer = graphvizRenderer;
    }

    @Override
    public BufferedImage call() throws Exception {
        final Graphviz graphviz = graphvizRenderer.newGraphviz();

        return graphviz.render(Format.PNG).toImage();
    }
}
