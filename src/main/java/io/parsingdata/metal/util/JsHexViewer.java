package io.parsingdata.metal.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.token.Def;

public class JsHexViewer {

    private static final int COLUMN_COUNT = 1 << 5;

    public static void generate(final ParseGraph graph) throws IOException {
        final Map<Long, LinkedList<Definition>> map = new TreeMap<>();
        step(graph, map);

        try {
            final File root = new File(JsHexViewer.class.getResource("/").toURI());

            final File file = new File(root, "jsHexViewer.htm");
            try (FileWriter out = new FileWriter(file);
                 InputStream in = JsHexViewer.class.getResourceAsStream("/util/jsHexViewer.htm");
                 BufferedReader br = new BufferedReader(new InputStreamReader(in))) {
                String line;
                while ((line = br.readLine()) != null) {
                    if (line.contains("<!-- generated -->")) {
                        if (line.trim().startsWith("var locations =")) {
                            out.write("    var locations = ");
                            out.write(map.keySet().toString());
                            out.write(";");
                        }
                        else if (line.trim().startsWith("var data =")) {
                            writeData(out, map);
                        }
                        else if (line.trim().startsWith("var columnCount =")) {
                            out.write("var columnCount = " + COLUMN_COUNT + ";");
                        }
                    }
                    else {
                        out.write(line);
                    }
                    out.write('\n');
                }
            }
        }
        catch (final URISyntaxException e) {
            throw new IOException(e);
        }
    }

    private static void writeData(final FileWriter out, final Map<Long, LinkedList<Definition>> map) throws IOException {
        out.write("    var data = [");
        for (final Long row : map.keySet()) {
            out.write(map.get(row).toString());
            out.write(",");
        }
        out.write("[]];");
    }

    private static void step(final ParseItem item, final Map<Long, LinkedList<Definition>> map) {
        if (!item.isGraph()) {
            if (item.getDefinition() instanceof Def) {
                final ParseValue value = item.asValue();
                getList(map, new Definition(value));
            }
            return;
        }
        if (item.asGraph().head == null) {
            return;
        }
        step(item.asGraph().head, map);
        step(item.asGraph().tail, map);
    }

    private static void getList(final Map<Long, LinkedList<Definition>> map, final Definition definition) {
        final long row = definition._offset / COLUMN_COUNT;
        LinkedList<Definition> list = map.get(row);
        if (list == null) {
            list = new LinkedList<>();
            map.put(row, list);
        }
        list.addFirst(definition);
    }

    private static class Definition {
        private final String _name;
        private final long _offset;
        private final long _size;

        public Definition(final ParseValue value) {
            _name = value.getFullName().substring(2); // W.
            _offset = value.getOffset();
            _size = value.getValue().length;
        }

        @Override
        public String toString() {
            return "[" + _offset + ", " + _size + ", '" + _name + "']";
        }
    }
}
