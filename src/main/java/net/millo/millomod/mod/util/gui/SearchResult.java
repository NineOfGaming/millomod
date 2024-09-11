package net.millo.millomod.mod.util.gui;

import net.millo.millomod.mod.features.impl.coding.cache.LineElement;

public class SearchResult {

    private final LineElement line;
    private final int segment;

    public SearchResult(LineElement lineElement, int segment) {
        this.line = lineElement;
        this.segment = segment;
    }

    public LineElement getLine() {
        return line;
    }

    public int getSegment() {
        return segment;
    }
}
