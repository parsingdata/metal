package io.parsingdata.metal.writer;

import java.io.OutputStream;
import java.lang.reflect.Field;

import io.parsingdata.metal.encoding.Encoding;

public class Writer {

    public static void write(final Object obj, final String name, final OutputStream out, final Encoding encoding) throws Exception {
        // TODO argnotnull
        for (final Field field : obj.getClass().getFields()) {
            final Name annotation = field.getAnnotation(Name.class);
            if (annotation != null && name.equals(annotation.value())) {
                write(obj, field, out, encoding);
            }
        }
    }

    private static void write(final Object obj, final Field field, final OutputStream out, final Encoding encoding) throws Exception {
        if (field.getType() == String.class) {
            final String value = (String) field.get(obj);
            if (value != null) {
                out.write(value.getBytes(encoding != null && encoding.charset != null ? encoding.charset : Encoding.DEFAULT_CHARSET));
            }
        }
        else if (field.getType().getTypeName().equals("byte")) {
            out.write((byte) field.get(obj));
        }
    }
}
