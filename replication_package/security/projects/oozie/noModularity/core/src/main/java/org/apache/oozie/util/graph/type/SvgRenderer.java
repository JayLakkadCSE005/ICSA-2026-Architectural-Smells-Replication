package org.apache.oozie.util.graph.type;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import org.apache.oozie.util.graph.GraphvizRenderer;

import java.util.concurrent.Callable;

public class SvgRenderer implements Callable<String> {

    private final GraphvizRenderer graphvizRenderer;

    public SvgRenderer(GraphvizRenderer graphvizRenderer) {
        this.graphvizRenderer = graphvizRenderer;
    }

    @Override
    public String call() throws Exception {
        final Graphviz graphviz = graphvizRenderer.newGraphviz();

        return graphviz.render(Format.SVG).toString();
    }
}
